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

import com.javafx.experiments.dataapp.model.Address;
import com.javafx.experiments.dataapp.model.Customer;
import com.javafx.experiments.dataapp.model.DiscountRate;
import com.javafx.experiments.dataapp.model.Product;
import com.javafx.experiments.dataapp.model.Region;
import com.javafx.experiments.dataapp.model.SalesOrder;
import com.javafx.experiments.dataapp.model.SalesOrderLine;
import com.javafx.experiments.dataapp.model.ZipCityInfo;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

public class SalesSimulator {
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();
    //TODO: shove these constants into the database?
    //Percentage of sales, based on Ford's year end data
    //http://www.sec.gov/Archives/edgar/data/37996/000115752311001210/a6622311.htm
    public static final double US_SMALL_CAR_PCT = .1364;
    public static final double US_MEDIUM_CAR_PCT = .1112;
    public static final double US_LARGE_CAR_PCT = .0758;
    public static final double US_PREMIUM_CAR_PCT = .0286;
    public static final double US_COMPACT_TRUCK_PCT = .0322;
    public static final double US_BUS_VAN_PCT = .0702;
    public static final double US_FULL_SIZE_TRUCK_PCT = .2776;
    public static final double US_UTILIY_PCT = .2442;
    public static final double US_PREMIUM_TRUCK_PCT = .0196;
    public static final double US_MED_HEAVY_TRUCK_PCT = .0042;
    
    private double usSmallCarPct = .1364;
    private double usMediumCarPct = .1112;
    private double usLargeCarPct = .0758;
    private double usPremiumCarPct = .0286;
    private double usCompactTruckPct = .0322;
    private double usBusVanPct = .0702;
    private double usFullsizePct = .2776;
    private double usUtilityPct = .2442;
    private double usPremiumTruckPct = .0196;
    private double usMedHeavyTruckPct = .0042;
    
    //car ids
    public static final List<Integer> US_SMALL_CARS = Arrays.asList(7,8,9,10,11,12,13);
    public static final List<Integer> US_MEDIUM_CARS = Arrays.asList(14,15,16,17);
    public static final List<Integer> US_LARGE_CARS = Arrays.asList(19,20);
    public static final List<Integer> US_PREMIUM_CARS = Arrays.asList(1,2,3,4);
    public static final List<Integer> US_COMPACT_TRUCKS = Arrays.asList(45,46,47,48);
    public static final List<Integer> US_BUS_VANS = Arrays.asList(29,30,31,32);
    public static final List<Integer> US_FULL_SIZE_TRUCKS = Arrays.asList(49,50,51,52,53,54,55,56);
    public static final List<Integer> US_UTILIYS = Arrays.asList(29,30,31,32,33,34,35,36,37,38,39,40,41);
    public static final List<Integer> US_PREMIUM_TRUCKS = Arrays.asList(56,64,42);
    public static final List<Integer> US_MED_HEAVY_TRUCKS = Arrays.asList(57,58,59,60,61,62,63,64);
    
    public static final List<Integer> US_REGIONS = Arrays.asList(1,2,3,4,5,6,7);
    
    public static double US_SALE_PCT = .55;
    
    public static int FLEET_SALES_SIZE = 30;
    public static double FLEET_SALES_PCT = (1.0/70.0);

    private static List<ZipCityInfo> listOfZips;
    
    //sell a car every 6.5 seconds
    public static int TIME_BETWEEN_SALES = 6500;
    
    private EntityManager em;
    
 //   private Query regionByZipCode;
    
    private List<Region> usRegions;
    private List<Region> intlRegions;
    
    Random thisRandom;
    
    public SalesSimulator(EntityManager em){
        this.em = em;
//        regionByZipCode = em.createNamedQuery("Region.findByZipCodePrefix", Region.class);
        
        thisRandom = new Random();
        
        Query q = em.createNamedQuery("ZipCityInfo.findAll", ZipCityInfo.class);
        listOfZips = q.getResultList();
        
        usRegions= new ArrayList<Region>();
        intlRegions= new ArrayList<Region>();
        
        generateRegionLists();
        
        
        
    }
    
    private void generateRegionLists() {
        Query q = em.createNamedQuery("Region.findAll", Region.class);
        for (Object o : q.getResultList()){
            Region r = (Region) o;
            if (r.getInternational()==1){
                intlRegions.add(r);                                
            } else{
                usRegions.add(r);
            }
        }
        
    }
    
    /**
     * Generate a single sales record for the current date and time
     */
    public void run ()  {
        generate(new Date());
        em.flush();
    }
    
    /** 
     * Generate data for the time period between the two given dates
     * 
     * This generates a large amount of sales and in the interest of time should
     * only be used with something such as the InitialLoadEntityManagerProxy that
     * culls the data in an intelligent way
     * 
     * @param start The date to start generating from
     * @param end The date to stop generating
     */
    public void run(Date start, Date end) {
        System.out.println("SalesSimulator:: generating sales from ["+DATE_FORMAT.format(start) +"] to ["+DATE_FORMAT.format(end)+"]");
        // generate a sale every 6 seconds in the time range
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        System.out.println("        Generated data for the day: ["+DATE_FORMAT.format(cal.getTime())+"]");
        int day = cal.get(Calendar.DAY_OF_YEAR);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int year = cal.get(Calendar.YEAR);
        
        EntityTransaction et = em.getTransaction();
        
        et.begin();
        double time = start.getTime();
        Double d = Double.valueOf(time);
        double dailySalesRate = (TIME_BETWEEN_SALES * (1+salesRateAdjustment(d.longValue(), thisRandom)));
        
        while(time <= end.getTime()) {
            time = time + dailySalesRate;
            
            d = Double.valueOf(time);
            cal.setTimeInMillis(d.longValue());
            final int currentDay = cal.get(Calendar.DAY_OF_YEAR);
            final int currentHour = cal.get(Calendar.HOUR_OF_DAY);
            final int currentYear = cal.get(Calendar.YEAR);
            
            if (currentHour != hour) {
                hour = currentHour;
       //         System.out.println("                hour: ["+currentHour+":00]");
            }
            if (currentDay != day) {
                day = currentDay;
                System.out.println("        Generated data for the day: ["+DATE_FORMAT.format(cal.getTime())+"]");
                
                
                dailySalesRate = (TIME_BETWEEN_SALES * (1+salesRateAdjustment(d.longValue(), thisRandom)));
                salesPctAdjustment();
                //this flush works specifically with the InitialLoadEntityManagerProxy to ensure
                // that the data is loaded into DailySales in the correct order.
                em.flush();
                et.commit();
                em.clear();
                et.begin();
            }
            if (currentYear != year){
                year = currentYear;
            }
            
            generate(new Date(d.longValue()));
            
            
        }
        em.flush();
        et.commit();
        
    }
    
    private void salesPctAdjustment(){
        //nextGaussian produces a value generated from a normal distribution with mean (mu) 1.0 centered around 0.0
        //http://en.wikipedia.org/wiki/Normal_distribution
        //this means that there will be noise added but in the long term the percentages would average out to be the same
        double sum = 0.0;
        usSmallCarPct = US_SMALL_CAR_PCT * ((10 + thisRandom.nextGaussian())/10);
        sum+=usSmallCarPct;
        usMediumCarPct = US_MEDIUM_CAR_PCT * ((10 + thisRandom.nextGaussian())/10);
        sum+=usMediumCarPct;
        usLargeCarPct = US_LARGE_CAR_PCT * ((10 + thisRandom.nextGaussian())/10);
        sum+=usLargeCarPct;
        usPremiumCarPct = US_PREMIUM_CAR_PCT * ((10 + thisRandom.nextGaussian())/10);
        sum+=usPremiumCarPct;
        usCompactTruckPct = US_COMPACT_TRUCK_PCT * ((10 + thisRandom.nextGaussian())/10);
        sum+=usCompactTruckPct;
        usBusVanPct = US_BUS_VAN_PCT * ((1.0 + thisRandom.nextGaussian())/10);
        sum+=usBusVanPct;
        usFullsizePct = US_FULL_SIZE_TRUCK_PCT * ((10 + thisRandom.nextGaussian())/10);
        sum+=usFullsizePct;
        usUtilityPct = US_UTILIY_PCT * (1.0 + thisRandom.nextGaussian());
        sum+=usUtilityPct;
        usPremiumTruckPct = US_PREMIUM_CAR_PCT * ((10 + thisRandom.nextGaussian())/10);
        sum+=usPremiumTruckPct;
        usMedHeavyTruckPct = US_MED_HEAVY_TRUCK_PCT * ((10 + thisRandom.nextGaussian())/10);
        sum+=usMedHeavyTruckPct;
        
        usSmallCarPct = usSmallCarPct/sum;
        usMediumCarPct = usMediumCarPct/sum;
        usLargeCarPct = usLargeCarPct/sum;
        usPremiumCarPct = usPremiumCarPct/sum;
        usCompactTruckPct = usCompactTruckPct/sum;
        usBusVanPct = usBusVanPct/sum;
        usFullsizePct = usFullsizePct/sum;
        usUtilityPct = usUtilityPct/sum;
        usPremiumTruckPct = usPremiumTruckPct/sum;
        usMedHeavyTruckPct = usMedHeavyTruckPct/sum;
        
        
    }
    
    private static double salesRateAdjustment(long time, Random random){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        double x = cal.get(Calendar.DAY_OF_YEAR);
        //if you graph out this rate equation you will see two peaks and two troughs with some valleys
        //but summed over 365 days it will add up to zero so the sales rate adjustement, if averaged over a year period (365 days)
        //will be zero. Basically this is to simulate the ups and downs in sales in a year
        double rate =  (-1.0/3.0) * Math.sin(x * ((4.0 * Math.PI)/365.0)) - (1.0/13.0) * Math.sin(x *(32.0 * Math.PI)/365.0);
        
        double noise = (10 + random.nextGaussian())/10;
        if (Math.abs(noise) < 2.0){
            rate = rate * noise;
        }
        
        return rate;
        
        
    }
    
    private void generate(Date date) {
        Random random = new Random();
        if (random.nextDouble() < US_SALE_PCT){
            
            if (random.nextDouble() < FLEET_SALES_PCT) {
                doFleetSale(random, date);
                //System.out.println("Generating a US Fleet sale");
            } else{
                doSingleSale(random, date);
                //System.out.println("Generating a US Consumer sale");
            }
        }
        
    }
    
    private void doFleetSale(Random random, Date date){
        //TODO: dealer, employee 
        SalesOrder order = new SalesOrder();
        order.setDate(date);
        Customer c = generateCustomer(random);
        order.setCustomerId(c);
        order.setChannel("FLEET");
        
        try{
            Region orderRegion = getRegion(c.getAddressId());
            order.setRegionId(orderRegion);
        }
        catch (Exception e){
            System.out.print(c.getAddressId());
        }
        
        em.persist(order);
        
        int salesToGo = FLEET_SALES_SIZE;
        int quantity;
        while (salesToGo > 0){
            //we need it to be >0
            quantity = random.nextInt(salesToGo);
            if (quantity == 0) quantity=1;
            int productId = pickUSProduct(random);
            char discountRate = pickDiscountRateForFleet(random);
            SalesOrderLine orderLine = new SalesOrderLine();
            orderLine.setOrderId(order);
            orderLine.setDiscountRate(em.find(DiscountRate.class, discountRate));
            orderLine.setProductId(em.find(Product.class, productId));
            orderLine.setQuantity(quantity);
            em.persist(orderLine);
            
            salesToGo = salesToGo - quantity;
        }
        
       
    }
    
   
    
    private Customer generateCustomer(Random random){
        Customer customer = new Customer();
        customer.setAddressId(generateAddress(random));
        
        em.persist(customer);
        
        return customer;
    }
    
    private Address generateAddress(Random random){
        //TODO: street addresses
        //we are dealing with zip code prefixes here
        int i = random.nextInt(listOfZips.size());
        ZipCityInfo randomZip = listOfZips.get(i);
        Address address = new Address();
        address.setCity(randomZip.getCity());
        address.setCountry("USA");
        address.setPostalCode(randomZip.zipToString());
        address.setStateProvCd(randomZip.getState());
        
        em.persist(address);
        
        return address;
    }
    
    private void doSingleSale(Random random, Date date){
        //TODO: dealer, employee customer
        //int region = pickRegion(random);
        SalesOrder order = new SalesOrder();
        order.setDate(date);
        Customer c = generateCustomer(random);
        order.setCustomerId(c);
        try{
            Region orderRegion = getRegion(c.getAddressId());
            order.setRegionId(orderRegion);
        }
        catch (Exception e){
            System.out.print(c.getAddressId());
        }
        //Region orderRegion = em.find(Region.class, region);
        
        order.setChannel("RETAIL");
        em.persist(order);
        
        int productId = pickUSProduct(random);
        SalesOrderLine orderLine = new SalesOrderLine();
        orderLine.setOrderId(order);
        orderLine.setDiscountRate(em.find(DiscountRate.class, 'N'));
        orderLine.setProductId(em.find(Product.class, productId));
        orderLine.setQuantity(1);
        em.persist(orderLine);
        
    }
    
    private Region getRegion(Address address){
        //TODO: just US 
        String zipCode = address.getPostalCode();
        Integer zipCodePrefix = Integer.valueOf(zipCode.substring(0, 2));
        //regionByZipCode.setParameter("zone", Integer.valueOf(zipCodePrefix));
        //Object o = regionByZipCode.getSingleResult();       
        Region returnRegion = null;
        for (Region r : usRegions){
            if (r.getStartZone() <= zipCodePrefix && r.getEndZone() >= zipCodePrefix){
                returnRegion=r;
            }
        }
        
        return returnRegion;
        
        
    }
    
    private int pickRegion(Random random){
        return US_REGIONS.get(random.nextInt(US_REGIONS.size()));
    }
    
    private int pickUSProduct(Random random){
        //nextDouble produces a value from 0 to 1.
        //all the Pcts should sum to one, it selects the type of car
        //and then randomly selects one of the associated autos
        double pick = random.nextDouble();
        int i=0;
        if  (pick <= usSmallCarPct ){
            //small car
            i = US_SMALL_CARS.get(random.nextInt(US_SMALL_CARS.size())); 
        }
        else if ((pick > usSmallCarPct) && (pick <= usSmallCarPct + usMediumCarPct)){
            //medium car
            i = US_MEDIUM_CARS.get(random.nextInt(US_MEDIUM_CARS.size())); 
        }
        else if ((pick > usSmallCarPct + usMediumCarPct) && (pick <= usSmallCarPct + usMediumCarPct + usLargeCarPct)){
            //large car
            i = US_LARGE_CARS.get(random.nextInt(US_LARGE_CARS.size())); 
        }
        else if ((pick > usSmallCarPct + usMediumCarPct + usLargeCarPct) && 
                    (pick <= usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct)){
            //premium car
            i = US_PREMIUM_CARS.get(random.nextInt(US_PREMIUM_CARS.size())); 
        }
        else if ((pick > usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct) && 
                    (pick <= usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct + usCompactTruckPct)){
            //compact truck
            i = US_COMPACT_TRUCKS.get(random.nextInt(US_COMPACT_TRUCKS.size())); 
        }
        else if ((pick > usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct + usCompactTruckPct) && 
                    (pick <= usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct + usCompactTruckPct + usBusVanPct)){
            //bus van
            i = US_BUS_VANS.get(random.nextInt(US_BUS_VANS.size())); 
        }                
        else if ((pick > usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct + usCompactTruckPct + usBusVanPct) && 
                    (pick <= usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct + usCompactTruckPct + usBusVanPct + usFullsizePct)){
            //full size truck
            i = US_FULL_SIZE_TRUCKS.get(random.nextInt(US_FULL_SIZE_TRUCKS.size())); 
        }
        else if ((pick > usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct + usCompactTruckPct + usBusVanPct + usFullsizePct) && 
                    (pick <= usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct + usCompactTruckPct + usBusVanPct + usFullsizePct + usUtilityPct)){
            //utility
            i = US_UTILIYS.get(random.nextInt(US_UTILIYS.size())); 
        }
        else if ((pick > usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct + usCompactTruckPct + usBusVanPct + usFullsizePct + usUtilityPct) && 
                    (pick <= usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct + usCompactTruckPct + usBusVanPct + usFullsizePct + usUtilityPct + usPremiumTruckPct)){
            i = US_PREMIUM_TRUCKS.get(random.nextInt(US_PREMIUM_TRUCKS.size())); 
            //premium truck
        }
        else if ((pick > usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct + usCompactTruckPct + usBusVanPct + usFullsizePct + usUtilityPct + usPremiumTruckPct) && 
                    (pick <= usSmallCarPct + usMediumCarPct + usLargeCarPct + usPremiumCarPct + usCompactTruckPct + usBusVanPct + usFullsizePct + usUtilityPct + usPremiumTruckPct + usMedHeavyTruckPct)){
            //heavy truck
            i = US_MED_HEAVY_TRUCKS.get(random.nextInt(US_MED_HEAVY_TRUCKS.size())); 
        }
        if (i==0){
            System.out.println(pick);
            System.out.println("Sum of pick percentages did not go to 1!");
        }
        
        return i;
    }

    private char pickDiscountRateForFleet(Random random) {
        double chance = random.nextDouble();
        char result = 'N';
        
        if (chance > .6){
            result = 'L';
        }
        if (chance > .79){
            result = 'M';
        }
        if (chance > .91){
            result = 'H';
        }
        
        return result;
    }
    
}

