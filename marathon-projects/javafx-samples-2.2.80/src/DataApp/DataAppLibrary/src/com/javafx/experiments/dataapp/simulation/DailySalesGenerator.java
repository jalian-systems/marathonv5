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
package com.javafx.experiments.dataapp.simulation;

import com.javafx.experiments.dataapp.model.Product;
import com.javafx.experiments.dataapp.model.ProductType;
import com.javafx.experiments.dataapp.model.Region;
import com.javafx.experiments.dataapp.model.SalesOrder;
import com.javafx.experiments.dataapp.model.SalesOrderLine;
import com.javafx.experiments.dataapp.model.DailySales;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class DailySalesGenerator {
    private EntityManager em;
    
    
        //columns are commented out because the DB doesnt handle GROUP by null
        private static final String BASE_QUERY = 
            "select "
                + "so.regionId, "
                + "p, "
                + "a.stateProvCd, "
                + "sum (sol.quantity) as quantity, "
                +  "FUNC('YEAR', so.date), "
                + "FUNC('MONTH', so.date), "
                + "FUNC('DAY', so.date) "
            + "from SalesOrder so "
            + "inner join so.salesOrderLineCollection sol "
            + "inner join sol.productId p "
            + "inner join so.customerId c "
            + "inner join c.addressId a "
            + "where so.date < :date1 "    
            + "group by "
                + "FUNC('YEAR', so.date), "
                + "FUNC('MONTH', so.date), "
                + "FUNC('DAY', so.date), "
                + "so.regionId, "
                + "a.stateProvCd, "
                + "p.productId";
    
    private static final String REMOVE_QUERY = "select so from SalesOrder so where so.date < :date1";
        
    Query baseQuery;
    
    TypedQuery<SalesOrder> removeQuery;
        
    public DailySalesGenerator(EntityManager em){
        this.em = em;        
    }
    
     /**
     * Generate a single sales record for the current date and time
     */
    public void run ()  {
        baseQuery = em.createQuery(BASE_QUERY);
        removeQuery = em.createQuery(REMOVE_QUERY, SalesOrder.class);
        System.out.println("moving over old sales orders into weekly sales");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        generate(cal.getTime());
        em.flush();
        em.clear();
    }
    
    private void generate(Date date){
        Parameter<Date> p1 = baseQuery.getParameter("date1", Date.class);
        baseQuery.setParameter(p1, date);
        List<Object[]> results = baseQuery.getResultList();
        for (Object[] result : results){
            DailySales hourlySales = new DailySales();
            hourlySales.setRegionId((Region)result[0]);
            //hourlySales.setEmployeeId(null);
            //hourlySales.setDealerId(null);
            hourlySales.setProductId((Product)result[1]);
            hourlySales.setStateProvCd((String)result[2]);
            hourlySales.setQuantity(((Long)result[3]).intValue());
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(Calendar.YEAR, (Integer) result[4]);
            cal.set(Calendar.MONTH, (Integer) result[5]-1);
            cal.set(Calendar.DAY_OF_MONTH, (Integer) result[6]);
            hourlySales.setDate(cal.getTime());
            em.persist(hourlySales);
        }
        removeStaleEntries(date);
        
        
        
    }
    private void removeStaleEntries(Date date){
            Parameter<Date> p1 = removeQuery.getParameter("date1", Date.class);
            removeQuery.setParameter(p1, date);
            for (SalesOrder so : removeQuery.getResultList()){
                for (SalesOrderLine sol : so.getSalesOrderLineCollection())
                    em.remove(sol);
                em.remove(so);
            } 
        }
}
