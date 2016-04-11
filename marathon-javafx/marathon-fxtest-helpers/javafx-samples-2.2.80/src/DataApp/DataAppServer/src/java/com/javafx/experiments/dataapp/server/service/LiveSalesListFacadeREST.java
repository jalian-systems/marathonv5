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

import com.javafx.experiments.dataapp.model.LiveSalesList;
import com.javafx.experiments.dataapp.model.LiveSalesList_;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Stateless
@Path("com.javafx.experiments.dataapp.model.livesaleslist")
public class LiveSalesListFacadeREST extends AbstractFacade<LiveSalesList> {
    @PersistenceContext(unitName = "DataAppLibraryPU")
    private EntityManager em;

    public LiveSalesListFacadeREST() {
        super(LiveSalesList.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(LiveSalesList entity) {
        super.create(entity);
    }

    @PUT
    @Override
    @Consumes({"application/xml", "application/json"})
    public void edit(LiveSalesList entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id")
    Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public LiveSalesList find(@PathParam("id")
    Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<LiveSalesList> findAll() {
        return super.findAll();
    }
    
    @GET
    @Produces({"application/xml", "application/json"})
    @Path("/recent/")
    public List<LiveSalesList> findRecent() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(LiveSalesList.class));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(500);
        return q.getResultList();
    }
    
    @GET
    @Produces({"application/xml", "application/json"})
    @Path("/recent/region/{regionName}")
    public List<LiveSalesList> findRecentRegion(@PathParam("regionName") String regionName) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        Root<LiveSalesList> liveSalesList = cq.from(LiveSalesList.class);
        cq.select(liveSalesList);
        cq.where(cb.equal(liveSalesList.get(LiveSalesList_.region), regionName));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(500);
        return q.getResultList();
    }
    
    @GET
    @Produces({"application/xml", "application/json"})
    @Path("/recent/region/{regionName}/{orderLineId}")
    public List<LiveSalesList> findRecentRegionFrom(@PathParam("regionName") String regionName, @PathParam("orderLineId") Integer orderLineId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        Root<LiveSalesList> liveSalesList = cq.from(LiveSalesList.class);
        cq.select(liveSalesList);
        cq.where(cb.and(
            cb.equal(liveSalesList.get(LiveSalesList_.region), regionName),
            cb.gt(liveSalesList.get(LiveSalesList_.orderLineId), orderLineId)
        ));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(500);
        return q.getResultList();
    }    
    
    @GET
    @Produces({"application/xml", "application/json"})
    @Path("/recent/producttype/{id}")
    public List<LiveSalesList> findRecentProductType(@PathParam("id") Integer productTypeId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        Root<LiveSalesList> liveSalesList = cq.from(LiveSalesList.class);
        cq.select(liveSalesList);
        cq.where(cb.equal(liveSalesList.get(LiveSalesList_.productTypeId), productTypeId));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(500);
        return q.getResultList();
    }
    
    @GET
    @Produces({"application/xml", "application/json"})
    @Path("/recent/producttype/{id}/{orderLineId}")
    public List<LiveSalesList> findRecentProductTypeFrom(@PathParam("id") Integer productTypeId, @PathParam("orderLineId") Integer orderLineId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        Root<LiveSalesList> liveSalesList = cq.from(LiveSalesList.class);
        cq.select(liveSalesList);
        cq.where(cb.and(
            cb.equal(liveSalesList.get(LiveSalesList_.productTypeId), productTypeId),
            cb.gt(liveSalesList.get(LiveSalesList_.orderLineId), orderLineId)
        ));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(500);
        return q.getResultList();
    }    
    
    @GET
    @Produces({"application/xml", "application/json"})
    @Path("/recent/region/producttype/{regionName}/{productTypeId}")
    public List<LiveSalesList> findRecentRegionProductType(@PathParam("regionName") String regionName, @PathParam("productTypeId") Integer productTypeId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        Root<LiveSalesList> liveSalesList = cq.from(LiveSalesList.class);
        cq.select(liveSalesList);
        cq.where(cb.and(
                cb.equal(liveSalesList.get(LiveSalesList_.productTypeId), productTypeId), 
                cb.equal(liveSalesList.get(LiveSalesList_.region), regionName)
        ));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(500);
        return q.getResultList();
    }
    
    @GET
    @Produces({"application/xml", "application/json"})
    @Path("/recent/region/producttype/{regionName}/{productTypeId}/{orderLineId}")
    public List<LiveSalesList> findRecentRegionProductTypeFrom(@PathParam("regionName") String regionName, @PathParam("productTypeId") Integer productTypeId, @PathParam("orderLineId") Integer orderLineId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        Root<LiveSalesList> liveSalesList = cq.from(LiveSalesList.class);
        cq.select(liveSalesList);
        cq.where(cb.and(
            cb.equal(liveSalesList.get(LiveSalesList_.productTypeId), productTypeId),
            cb.equal(liveSalesList.get(LiveSalesList_.region), regionName),
            cb.gt(liveSalesList.get(LiveSalesList_.orderLineId), orderLineId)
        ));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(500);
        return q.getResultList();
    }    

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<LiveSalesList> findRange(@PathParam("from")
    Integer from, @PathParam("to")
    Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("/date/{from}")
    @Produces({"application/xml", "application/json"})
    public List<LiveSalesList> findFrom(@PathParam("from") Integer from) {
        Query q = getEntityManager().createNamedQuery("LiveSalesList.findFromOrderLineId");
        Parameter<Integer> p = q.getParameter("orderLineId", Integer.class);
        q.setParameter(p, from);
        return q.getResultList();
    }
    
    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @java.lang.Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
