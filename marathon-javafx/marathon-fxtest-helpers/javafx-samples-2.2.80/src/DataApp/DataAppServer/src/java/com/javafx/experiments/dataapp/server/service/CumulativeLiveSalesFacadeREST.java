/*
 * Copyright (c) 2008, 2011 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.javafx.experiments.dataapp.server.service;


import com.javafx.experiments.dataapp.model.ProductType;
import com.javafx.experiments.dataapp.model.Product;
import com.javafx.experiments.dataapp.model.Region;
import com.javafx.experiments.dataapp.model.transit.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

@Stateless
@Path("com.javafx.experiments.dataapp.model.cumulativelivesales")
public class CumulativeLiveSalesFacadeREST {
    @PersistenceContext(unitName = "DataAppLibraryPU")
    private EntityManager em;

//    SQL    
//    private static final String BASE_QUERY = "select sum(p.cost), sum(p.price) " +
//            "from app.sales_order so "
//            + "left outer join app.sales_order_line sol on so.order_id = sol.order_id "
//            + "left outer join app.product p on p.product_id = sol.product_id "
//            + "group by YEAR(so.date), MONTH(so.date), DAY(so.date), HOUR(so.date)";
    
    // JPQL
//    private static final String BASE_RANGE_QUERY = 
//            "select sum(hs.quantity * p.cost), "
//                + " sum(hs.quantity * p.price), " 
//                +  "FUNC('YEAR', hs.date), "
//                + "FUNC('MONTH', hs.date), "
//                + "FUNC('DAY', hs.date), "
//            + "hs.date "
//            + "from DailySales hs "
//            + "inner join hs.productId p "
//            + "group by FUNC('YEAR', hs.date), FUNC('MONTH', hs.date), FUNC('DAY', hs.date), hs.date "
//            + "order by hs.date desc";

        private static final String BASE_RANGE_QUERY = 
            "select "
                + "min(hs.dailySalesId), "
                + "max(hs.dailySalesId), "
                + "sum(hs.quantity * p.cost), "
                + "sum(hs.quantity * p.price), " 
                + "hs.date "
            + "from DailySales hs "
            + "inner join hs.productId p "
            + "group by hs.date "
            + "order by hs.date desc";
    
    private static final String TYPE_SUM_QUERY = 
              "select sum(hs.quantity * p.cost), "
                + "sum(hs.quantity * p.price), " 
                + "sum(hs.quantity), "
                + "pt "
            + "from DailySales hs "
            + "left join hs.productId p "            
            + "left join p.productTypeId pt "    
            + "where hs.dailySalesId >= :startId and hs.dailySalesId <= :endId "
            + "group by pt.productTypeId ";

    private static final String TYPE_RANGE_QUERY = 
            "select sum(hs.quantity), "
                + "pt "
            + "from DailySales hs "
            + "left join hs.productId p "
            + "left join p.productTypeId pt "
            + "where hs.dailySalesId >= :startId and hs.dailySalesId <= :endId "
            + "group by pt.productTypeId, hs.date ";

    private static final String REGION_SUM_QUERY = 
            "select sum(hs.quantity * p.cost), "
                + "sum(hs.quantity * p.price), " 
                + "sum(hs.quantity), "
                + "r "
            + "from DailySales hs "
            + "left join hs.productId p "
            + "left join hs.regionId r "
            + "where hs.dailySalesId >= :startId and hs.dailySalesId <= :endId "
            + "group by r.regionId";
    
    
    //begin region queries
    private static final String REGION_RANGE_QUERY = 
            "select "
                + "min(hs.dailySalesId), "
                + "max(hs.dailySalesId), "
                + "sum(hs.quantity * p.cost), "
                + "sum(hs.quantity * p.price), " 
                + "hs.date "
            + "from DailySales hs "
            + "inner join hs.productId p "
            + "where hs.regionId.regionId = :regionId "
            + "group by hs.date "
            + "order by hs.date desc";
    
    private static final String REGION_TYPE_SUM_QUERY = 
              "select sum(hs.quantity * p.cost), "
                + "sum(hs.quantity * p.price), " 
                + "sum(hs.quantity), "
                + "pt "
            + "from DailySales hs "
            + "left join hs.productId p "            
            + "left join p.productTypeId pt "    
            + "where hs.dailySalesId >= :startId and hs.dailySalesId <= :endId "
            + "and hs.regionId.regionId = :regionId "
            + "group by pt.productTypeId ";

    private static final String REGION_TYPE_RANGE_QUERY = 
            "select sum(hs.quantity), "
                + "pt "
            + "from DailySales hs "
            + "left join hs.productId p "
            + "left join p.productTypeId pt "
            + "where hs.dailySalesId >= :startId and hs.dailySalesId <= :endId "
            + "and hs.regionId.regionId = :regionId "
            + "group by pt.productTypeId, hs.date ";

    private static final String STATE_SUM_QUERY = 
            "select sum(hs.quantity * p.cost), "
                + "sum(hs.quantity * p.price), " 
                + "sum(hs.quantity), "
                + "hs.stateProvCd "
            + "from DailySales hs "
            + "left join hs.productId p "
            + "where hs.dailySalesId >= :startId and hs.dailySalesId <= :endId "
            + "and hs.regionId.regionId = :regionId "
            + "group by hs.stateProvCd";
    
//end region
    
    public CumulativeLiveSalesFacadeREST() { 
    }
   
    protected EntityManager getEntityManager() {
        return em;
    }
  
    @GET
    @Produces({"application/xml", "application/json"})
    public List<TransitCumulativeSales> findAll() {       
        Query baseRangeQuery = em.createQuery(BASE_RANGE_QUERY); 
        List<TransitCumulativeSales> result = new ArrayList<TransitCumulativeSales>();
        List<Object[]> resultList = baseRangeQuery.getResultList();
        for (int i=0; i < resultList.size(); i++){
            Object o[] = resultList.get(i);
            TransitCumulativeSales t = new TransitCumulativeSales();
            t.setStartDailySalesId((Integer)o[0]);
            t.setEndDailySalesId((Integer)o[1]);
            t.setCost((Double)o[2]);
            t.setSales((Double) o[3]);
            t.setDate((Date)o[4]);
            result.add(t);
        }
        return result;
    }
    
    @GET
    @Path("/recent/")
    @Produces({"application/xml", "application/json"})
    public List<TransitCumulativeSales> findRecent() {     
        Query baseRangeQuery = em.createQuery(BASE_RANGE_QUERY); 
        //Query baseRangeQuery = getEntityManager().createQuery(BASE_RANGE_QUERY);
        baseRangeQuery.setMaxResults(200);
        List<TransitCumulativeSales> result = new ArrayList<TransitCumulativeSales>();
        List<Object[]> resultList = baseRangeQuery.getResultList();
        System.out.print("hello world");
        for (int i=0; i < resultList.size(); i++){
            Object o[] = resultList.get(i);
            TransitCumulativeSales t = new TransitCumulativeSales();
            t.setStartDailySalesId((Integer)o[0]);
            t.setEndDailySalesId((Integer)o[1]);
            t.setCost((Double)o[2]);
            t.setSales((Double) o[3]);
            t.setDate((Date)o[4]);
            result.add(t);
        }
        return result;
    }
    
    
    @GET
    @Path("/type/{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<ProductTypeTransitCumulativeSeriesSales> findTypeRange(@PathParam("from") String from, @PathParam("to") String to) throws ParseException, JAXBException {
        System.out.println("START findTypeRange (from="+from+" , to="+to+")");
        long DIFF, TIME = System.currentTimeMillis(), START_TIME = System.currentTimeMillis();
        Query q = getEntityManager().createQuery(TYPE_SUM_QUERY);
      
        DIFF = (System.currentTimeMillis() - TIME);
        System.out.println("    Q1 COMP TIME = "+DIFF+"ms");
        TIME = System.currentTimeMillis();
        
        Parameter<Integer> p1 = q.getParameter("startId", Integer.class);
        q.setParameter(p1, Integer.parseInt(from));
        Parameter<Integer> p2 = q.getParameter("endId", Integer.class);
        q.setParameter(p2, Integer.parseInt(to));
        
        List<ProductTypeTransitCumulativeSeriesSales> result = new ArrayList<ProductTypeTransitCumulativeSeriesSales>();
        List<Object[]> resultList = q.getResultList();
        
        DIFF = (System.currentTimeMillis() - TIME);
        System.out.println("    Q1 TIME = "+DIFF+"ms");
        TIME = System.currentTimeMillis();
               
        for (int i=0; i < resultList.size(); i++){
            Object o[] = resultList.get(i);
            ProductTypeTransitCumulativeSeriesSales t = new ProductTypeTransitCumulativeSeriesSales();
            t.setCost((Double)o[0]);
            t.setSales((Double) o[1]);
            t.setUnits((Long) o[2]);
            t.setProductType((ProductType) o[3]);
            result.add(t);
        }
        
        //building sales range
        HashMap<ProductType, List<Double>> seriesGenerator = new HashMap<ProductType, List<Double>>();
        
        Query q2 = getEntityManager().createQuery(TYPE_RANGE_QUERY);
        q2.setParameter(p1, Integer.parseInt(from));
        q2.setParameter(p2, Integer.parseInt(to));
        resultList = q2.getResultList();
        
        DIFF = (System.currentTimeMillis() - TIME);
        System.out.println("    Q2 TIME = "+DIFF+"ms");
        TIME = System.currentTimeMillis();
        
        for (int i=0; i < resultList.size(); i++){
            Object o[] = resultList.get(i);
            if (!seriesGenerator.containsKey((ProductType)o[1])){
                seriesGenerator.put((ProductType) o[1], new ArrayList<Double>());
            }            
            seriesGenerator.get((ProductType) o[1]).add(((Long)o[0]).doubleValue());
        }
        for (ProductTypeTransitCumulativeSeriesSales tcs : result){
            tcs.setSeries(seriesGenerator.get((ProductType) tcs.getProductType())) ;
        }
        
        DIFF = (System.currentTimeMillis() - START_TIME);
        System.out.println("    TOTAL TIME = "+DIFF+"ms");
        
        
        return result;
    }
    
//    @GET
//    @Path("/product/{from}/{to}")
//    @Produces({"application/xml", "application/json"})
//    public List<ProductTransitCumulativeSales> findProductRange(@PathParam("from") String from, @PathParam("to") String to) throws ParseException {
//        Date dFrom = new Date(from);
//        Date dTo = new Date(to);
//        Query q = getEntityManager().createQuery(PRODUCT_SUM_QUERY);
//        Parameter<Date> p1 = q.getParameter("date1", Date.class);
//        q.setParameter(p1, dFrom);
//        Parameter<Date> p2 = q.getParameter("date2", Date.class);
//        q.setParameter(p2, dTo);
//        
//        List<ProductTransitCumulativeSales> result = new ArrayList<ProductTransitCumulativeSales>();
//        List<Object[]> resultList = q.getResultList();
//        
//        
//        for (int i=0; i < resultList.size(); i++){
//            Object o[] = resultList.get(i);
//            ProductTransitCumulativeSales t = new ProductTransitCumulativeSales();
//            t.setCost((Double)o[0]);
//            t.setSales((Double) o[1]);
//            t.setUnits((Long) o[2]);
//            t.setProduct((Product) o[3]);
//            result.add(t);
//        }
//        return result;
//    }
    
    @GET
    @Path("/region/{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<RegionTransitCumulativeSales> findRegionRange(@PathParam("from") String from, @PathParam("to") String to) throws ParseException {
        System.out.println("START findRegionRange (from="+from+" , to="+to+")");
        long DIFF, TIME = System.currentTimeMillis(), START_TIME = System.currentTimeMillis();
        Query q = getEntityManager().createQuery(REGION_SUM_QUERY);
        Parameter<Integer> p1 = q.getParameter("startId", Integer.class);
        q.setParameter(p1, Integer.parseInt(from));
        Parameter<Integer> p2 = q.getParameter("endId", Integer.class);
        q.setParameter(p2, Integer.parseInt(to));
        
        List<RegionTransitCumulativeSales> result = new ArrayList<RegionTransitCumulativeSales>();
        List<Object[]> resultList = q.getResultList();
        
        
        for (int i=0; i < resultList.size(); i++){
            Object o[] = resultList.get(i);
            RegionTransitCumulativeSales t = new RegionTransitCumulativeSales();
            t.setCost((Double)o[0]);
            t.setSales((Double) o[1]);
            t.setUnits((Long) o[2]);
            t.setRegion((Region) o[3]);
            result.add(t);
        }
        
        DIFF = (System.currentTimeMillis() - START_TIME);
        System.out.println("    TOTAL TIME = "+DIFF+"ms");
        
        return result;
    }
    
    
    //region calls --same as above but with region, this can probably be refactored to make more sense
    @GET
    @Produces({"application/xml", "application/json"})
    @Path("/region/{regionId}")
    public List<TransitCumulativeSales> findAllRegion(@PathParam("regionId") Integer regionId) {       
        Query baseRangeQuery = em.createQuery(REGION_RANGE_QUERY); 
        Parameter<Integer> p1 = baseRangeQuery.getParameter("regionId", Integer.class);
        baseRangeQuery.setParameter(p1, regionId);
        
        List<TransitCumulativeSales> result = new ArrayList<TransitCumulativeSales>();
        List<Object[]> resultList = baseRangeQuery.getResultList();
        for (int i=0; i < resultList.size(); i++){
            Object o[] = resultList.get(i);
            TransitCumulativeSales t = new TransitCumulativeSales();
            t.setStartDailySalesId((Integer)o[0]);
            t.setEndDailySalesId((Integer)o[1]);
            t.setCost((Double)o[2]);
            t.setSales((Double) o[3]);
            t.setDate((Date)o[4]);
            result.add(t);
        }
        return result;
    }
    
    @GET
    @Path("/state/{from}/{to}/{regionId}")
    @Produces({"application/xml", "application/json"})
    public List<StateTransitCumulativeSales> findStateRange(@PathParam("from") String from, @PathParam("to") String to, @PathParam("regionId") Integer regionId) {
        System.out.println("START findRegionRange (from="+from+" , to="+to+")");
        long DIFF, TIME = System.currentTimeMillis(), START_TIME = System.currentTimeMillis();
        Query q = getEntityManager().createQuery(STATE_SUM_QUERY);
        Parameter<Integer> p1 = q.getParameter("startId", Integer.class);
        q.setParameter(p1, Integer.parseInt(from));
        Parameter<Integer> p2 = q.getParameter("endId", Integer.class);
        q.setParameter(p2, Integer.parseInt(to));
        Parameter<Integer> p3 = q.getParameter("regionId", Integer.class);
        q.setParameter(p3, regionId);
        
        List<StateTransitCumulativeSales> result = new ArrayList<StateTransitCumulativeSales>();
        List<Object[]> resultList = q.getResultList();
        
        
        for (int i=0; i < resultList.size(); i++){
            Object o[] = resultList.get(i);
            StateTransitCumulativeSales t = new StateTransitCumulativeSales();
            t.setCost((Double)o[0]);
            t.setSales((Double) o[1]);
            t.setUnits((Long) o[2]);
            t.setState((String) o[3]);
            result.add(t);
        }
        
        DIFF = (System.currentTimeMillis() - START_TIME);
        System.out.println("    TOTAL TIME = "+DIFF+"ms");
        
        return result;
    }
    
    @GET
    @Path("/type/{from}/{to}/{regionId}")
    @Produces({"application/xml", "application/json"})
    public List<ProductTypeTransitCumulativeSeriesSales> findTypeRegionRange(@PathParam("from") String from, @PathParam("to") String to, @PathParam("regionId") Integer regionId) {
        System.out.println("START findTypeRange (from="+from+" , to="+to+")");
        long DIFF, TIME = System.currentTimeMillis(), START_TIME = System.currentTimeMillis();
        Query q = getEntityManager().createQuery(REGION_TYPE_SUM_QUERY);
      
        DIFF = (System.currentTimeMillis() - TIME);
        System.out.println("    Q1 COMP TIME = "+DIFF+"ms");
        TIME = System.currentTimeMillis();
        
        Parameter<Integer> p1 = q.getParameter("startId", Integer.class);
        q.setParameter(p1, Integer.parseInt(from));
        Parameter<Integer> p2 = q.getParameter("endId", Integer.class);
        q.setParameter(p2, Integer.parseInt(to));
        Parameter<Integer> p3 = q.getParameter("regionId", Integer.class);
        q.setParameter(p3, regionId);
        
        List<ProductTypeTransitCumulativeSeriesSales> result = new ArrayList<ProductTypeTransitCumulativeSeriesSales>();
        List<Object[]> resultList = q.getResultList();
        
        DIFF = (System.currentTimeMillis() - TIME);
        System.out.println("    Q1 TIME = "+DIFF+"ms");
        TIME = System.currentTimeMillis();
               
        for (int i=0; i < resultList.size(); i++){
            Object o[] = resultList.get(i);
            ProductTypeTransitCumulativeSeriesSales t = new ProductTypeTransitCumulativeSeriesSales();
            t.setCost((Double)o[0]);
            t.setSales((Double) o[1]);
            t.setUnits((Long) o[2]);
            t.setProductType((ProductType) o[3]);
            result.add(t);
        }
        
        //building sales range
        HashMap<ProductType, List<Double>> seriesGenerator = new HashMap<ProductType, List<Double>>();
        
        Query q2 = getEntityManager().createQuery(REGION_TYPE_RANGE_QUERY);
        q2.setParameter(p1, Integer.parseInt(from));
        q2.setParameter(p2, Integer.parseInt(to));
        q2.setParameter(p3, regionId);
        resultList = q2.getResultList();
        
        DIFF = (System.currentTimeMillis() - TIME);
        System.out.println("    Q2 TIME = "+DIFF+"ms");
        TIME = System.currentTimeMillis();
        
        for (int i=0; i < resultList.size(); i++){
            Object o[] = resultList.get(i);
            if (!seriesGenerator.containsKey((ProductType)o[1])){
                seriesGenerator.put((ProductType) o[1], new ArrayList<Double>());
            }            
            seriesGenerator.get((ProductType) o[1]).add(((Long)o[0]).doubleValue());
        }
        for (ProductTypeTransitCumulativeSeriesSales tcs : result){
            tcs.setSeries(seriesGenerator.get((ProductType) tcs.getProductType())) ;
        }
        
        DIFF = (System.currentTimeMillis() - START_TIME);
        System.out.println("    TOTAL TIME = "+DIFF+"ms");
        
        
        return result;
    }
}
