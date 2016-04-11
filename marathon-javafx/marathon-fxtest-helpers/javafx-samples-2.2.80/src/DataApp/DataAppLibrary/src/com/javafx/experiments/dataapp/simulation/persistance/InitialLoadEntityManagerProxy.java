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
package com.javafx.experiments.dataapp.simulation.persistance;

import com.javafx.experiments.dataapp.model.DailySales;
import com.javafx.experiments.dataapp.model.Dealer;
import com.javafx.experiments.dataapp.model.Employee;
import com.javafx.experiments.dataapp.model.Product;
import com.javafx.experiments.dataapp.model.Region;
import com.javafx.experiments.dataapp.model.SalesOrder;
import com.javafx.experiments.dataapp.model.SalesOrderLine;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;

/**
 * This class acts as a proxy for an entity manager and should ONLY be used on an
 * load scenario with the goal of generating DailySales. The general idea is that persist only 'persists' sales
 * order lines in an accumulation that corresponds to an entry in the DailySales.
 * 
 * Flush should be called at the end of every day, to insure that the ids generated
 * in the daily sales table are correct. 
 * 
 * This proxy is a bit of a hack but allows us to use the current Persistence heavy
 * framework of the SalesSimulator without changing the code.
 */
public class InitialLoadEntityManagerProxy implements EntityManager {

    private EntityManager em;
    
    //Accumulates daily sales to be persisted out to the database.
    private TreeMap<Date, HashMap<Product, HashMap<String, HashMap<Region,Integer>>>> dailySalesCounter;
    private SimpleDateFormat dateFormatter;
    
    public InitialLoadEntityManagerProxy (EntityManager em ){
        this.em = em;
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        
        this.dailySalesCounter = new TreeMap<Date, 
                                                    HashMap<Product, 
                                                        HashMap<String,
                                                            HashMap<Region, Integer>>>>();
    }
    
    @Override
    public void persist(Object o) {
        if (o instanceof SalesOrderLine){
            doBlackMagic((SalesOrderLine) o);
        }
    }
    
    private void doBlackMagic(SalesOrderLine sol){
        SalesOrder so = sol.getOrderId();
        Date date = null;

        //all we care about is the day the sale happened
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(so.getDate().getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        
        date = cal.getTime();
        
        if (!dailySalesCounter.containsKey(date)){
            dailySalesCounter.put(date, new HashMap<Product, HashMap<String, HashMap<Region, Integer>>>());
        }
        if (!dailySalesCounter.get(date).containsKey(sol.getProductId())){
            dailySalesCounter.get(date).put(sol.getProductId(), new HashMap<String, HashMap<Region, Integer>>());
        }
        if (!dailySalesCounter.get(date).get(sol.getProductId()).containsKey(so.getCustomerId().getAddressId().getStateProvCd())){
            dailySalesCounter.get(date).get(sol.getProductId()).put(so.getCustomerId().getAddressId().getStateProvCd(), new HashMap<Region,Integer>());
        }
        if (!dailySalesCounter.get(date).get(sol.getProductId()).get(so.getCustomerId().getAddressId().getStateProvCd()).containsKey(so.getRegionId())){
            dailySalesCounter.get(date).get(sol.getProductId()).get(so.getCustomerId().getAddressId().getStateProvCd()).put(so.getRegionId(), sol.getQuantity());
        }
        else{
            Integer quantity = dailySalesCounter.get(date).get(sol.getProductId()).get(so.getCustomerId().getAddressId().getStateProvCd()).put(so.getRegionId(), sol.getQuantity());
            quantity = quantity + sol.getQuantity();
            dailySalesCounter.get(date).get(sol.getProductId()).get(so.getCustomerId().getAddressId().getStateProvCd()).put(so.getRegionId(), quantity);
        }
       
        

    }
    
    private void persistBlackMagic() {
        for (Date date : dailySalesCounter.keySet()){
                    for (Product product : dailySalesCounter.get(date).keySet()){
                        for (String state : dailySalesCounter.get(date).get(product).keySet()){
                            for (Region region : dailySalesCounter.get(date).get(product).get(state).keySet()){
                                DailySales dailySales = new DailySales();
                                dailySales.setRegionId(region);
                                dailySales.setProductId(product);
                                dailySales.setStateProvCd(state);
                                dailySales.setQuantity(dailySalesCounter.get(date).get(product).get(state).get(region));
                                dailySales.setDate(date);
                                em.persist(dailySales);
                            }
                        }
            }
        System.out.println(date);
        }

    }

    /* There is a huge assumption that the person calling this method knows what it is doing
     * Call every day and at the end of the day.
     */
    @Override
    public void flush() {
        persistBlackMagic();
        em.flush();
        dailySalesCounter = new TreeMap<Date, HashMap<Product, HashMap<String, HashMap<Region, Integer>>>>();
    }

    
    //----the rest of the class is delegated to the internal EntityManager
    @Override
    public <T> T merge(T t) {
        return em.merge(t);
    }

    @Override
    public void remove(Object o) {
        em.remove(o);
    }

    @Override
    public <T> T find(Class<T> type, Object o) {
        return em.find(type, o);
    }

    @Override
    public <T> T find(Class<T> type, Object o, Map<String, Object> map) {
        return em.find(type, o, map);
    }

    @Override
    public <T> T find(Class<T> type, Object o, LockModeType lmt) {
        return em.find(type, o, lmt);
    }

    @Override
    public <T> T find(Class<T> type, Object o, LockModeType lmt, Map<String, Object> map) {
        return em.find(type, o, lmt, map);
    }

    @Override
    public <T> T getReference(Class<T> type, Object o) {
        return em.getReference(type, o);
    }

    @Override
    public void setFlushMode(FlushModeType fmt) {
        em.setFlushMode(fmt);
    }

    @Override
    public FlushModeType getFlushMode() {
        return em.getFlushMode();
    }

    @Override
    public void lock(Object o, LockModeType lmt) {
        em.lock(o, lmt);
    }

    @Override
    public void lock(Object o, LockModeType lmt, Map<String, Object> map) {
        em.lock(o, lmt, map);
    }

    @Override
    public void refresh(Object o) {
        em.refresh(o);
    }

    @Override
    public void refresh(Object o, Map<String, Object> map) {
        em.refresh(o, map);
    }

    @Override
    public void refresh(Object o, LockModeType lmt) {
        em.refresh(o, lmt);
    }

    @Override
    public void refresh(Object o, LockModeType lmt, Map<String, Object> map) {
        em.refresh(o, lmt, map);
    }

    @Override
    public void clear() {
        em.clear();
    }

    @Override
    public void detach(Object o) {
        em.detach(o);
    }

    @Override
    public boolean contains(Object o) {
        return em.contains(o);
    }

    @Override
    public LockModeType getLockMode(Object o) {
        return em.getLockMode(o);
    }

    @Override
    public void setProperty(String string, Object o) {
        em.setProperty(string, o);
    }

    @Override
    public Map<String, Object> getProperties() {
        return em.getProperties();
    }

    @Override
    public Query createQuery(String string) {
        return em.createQuery(string);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> cq) {
        return em.createQuery(cq);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String string, Class<T> type) {
        return em.createQuery(string, type);
    }

    @Override
    public Query createNamedQuery(String string) {
        return em.createNamedQuery(string);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String string, Class<T> type) {
        return em.createNamedQuery(string, type);
    }

    @Override
    public Query createNativeQuery(String string) {
        return em.createNamedQuery(string);
    }

    @Override
    public Query createNativeQuery(String string, Class type) {
        return em.createNativeQuery(string, type);
    }

    @Override
    public Query createNativeQuery(String string, String string1) {
        return em.createNativeQuery(string, string1);
    }

    @Override
    public void joinTransaction() {
        em.joinTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return em.unwrap(type);
    }

    @Override
    public Object getDelegate() {
        return em.getDelegate();
    }

    @Override
    public void close() {
        em.close();
    }

    @Override
    public boolean isOpen() {
        return em.isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return em.getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return em.getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return em.getMetamodel();
    }
    
}
