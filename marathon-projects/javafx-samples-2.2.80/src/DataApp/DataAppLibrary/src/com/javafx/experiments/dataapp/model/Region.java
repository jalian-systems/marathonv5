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
package com.javafx.experiments.dataapp.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "REGION", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Region.findAll", query = "SELECT r FROM Region r"),
    @NamedQuery(name = "Region.findByRegionId", query = "SELECT r FROM Region r WHERE r.regionId = :regionId"),
    @NamedQuery(name = "Region.findByName", query = "SELECT r FROM Region r WHERE r.name = :name"),
    @NamedQuery(name = "Region.findByInternational", query = "SELECT r FROM Region r WHERE r.international = :international"),
    @NamedQuery(name = "Region.findByZipCodePrefix", query = "SELECT r FROM Region r WHERE r.startZone <= :zone and r.endZone >= :zone and r.international=0"),
    @NamedQuery(name = "Region.findByStartZone", query = "SELECT r FROM Region r WHERE r.startZone = :startZone"),
    @NamedQuery(name = "Region.findByEndZone", query = "SELECT r FROM Region r WHERE r.endZone = :endZone")})

public class Region implements Serializable {
    @OneToMany(mappedBy = "regionId")
    private Collection<DailySales> dailySalesCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "REGION_ID")
    private Integer regionId;
    @Column(name = "NAME")
    private String name;
    @Basic(optional = false)
    @Column(name = "INTERNATIONAL")
    private short international;
    @Basic(optional = false)
    @Column(name = "START_ZONE")
    private int startZone;
    @Basic(optional = false)
    @Column(name = "END_ZONE")
    private int endZone;
    @OneToMany(mappedBy = "regionId")
    private Collection<SalesOrder> salesOrderCollection;
    @OneToMany(mappedBy = "regionId")
    private Collection<ProjectedSales> projectedSalesCollection;
    @OneToMany(mappedBy = "regionId")
    private Collection<Dealer> dealerCollection;

    public Region() {
    }

    public Region(Integer regionId) {
        this.regionId = regionId;
    }

    public Region(Integer regionId, short international, int startZone, int endZone) {
        this.regionId = regionId;
        this.international = international;
        this.startZone = startZone;
        this.endZone = endZone;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getInternational() {
        return international;
    }

    public void setInternational(short international) {
        this.international = international;
    }

    public int getStartZone() {
        return startZone;
    }

    public void setStartZone(int startZone) {
        this.startZone = startZone;
    }

    public int getEndZone() {
        return endZone;
    }

    public void setEndZone(int endZone) {
        this.endZone = endZone;
    }

    @XmlTransient
    public Collection<SalesOrder> getSalesOrderCollection() {
        return salesOrderCollection;
    }

    public void setSalesOrderCollection(Collection<SalesOrder> salesOrderCollection) {
        this.salesOrderCollection = salesOrderCollection;
    }

    @XmlTransient
    public Collection<ProjectedSales> getProjectedSalesCollection() {
        return projectedSalesCollection;
    }

    public void setProjectedSalesCollection(Collection<ProjectedSales> projectedSalesCollection) {
        this.projectedSalesCollection = projectedSalesCollection;
    }

    @XmlTransient
    public Collection<Dealer> getDealerCollection() {
        return dealerCollection;
    }

    public void setDealerCollection(Collection<Dealer> dealerCollection) {
        this.dealerCollection = dealerCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (regionId != null ? regionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Region)) {
            return false;
        }
        Region other = (Region) object;
        if ((this.regionId == null && other.regionId != null) || (this.regionId != null && !this.regionId.equals(other.regionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    @XmlTransient
    public Collection<DailySales> getDailySalesCollection() {
        return dailySalesCollection;
    }

    public void setDailySalesCollection(Collection<DailySales> dailySalesCollection) {
        this.dailySalesCollection = dailySalesCollection;
    }
    
    private static final String[] Northeast = new String[]{ "DE", "MA", "RI", "NH", "ME", "VT", "CT", "NY", "NJ", "PA" };
    private static final String[] MidAtlantic = new String[]{ "DC", "VA", "MD", "WV", "NC", "SC" };
    private static final String[] South = new String[]{ "GA", "TN", "FL", "AL", "MS" };
    private static final String[] MidWest = new String[]{ "KY", "OH", "IN", "MI", "IA", "IL", "WI", "MN", "SD", "ND", "MT", "MO", "KS", "NE" };
    private static final String[] ArkLaTex = new String[]{ "LA", "AR", "OK", "TX" };
    private static final String[] Southwest = new String[]{ "CO", "WY", "ID", "UT", "AZ", "NM", "NV" };
    private static final String[] West = new String[]{ "CA", "HI", "AS", "OR", "WA" };
    private static final String[] Territories = new String[]{ "PR", "VI" };
    public static final String[] ALL_STATES = new String[]{ "DE", "MA", "RI", "NH", "ME", "VT", "CT", "NY", "NJ", "PA" , "PR", "VI", "DC", "VA", "MD", "WV", "NC", "SC", "GA", "TN", "FL", "AL", "MS", "KY", "OH", "IN", "MI", "IA", "IL", "WI", "MN", "SD", "ND", "MT", "MO", "KS", "NE", "LA", "AR", "OK", "TX","CO", "WY", "ID", "UT", "AZ", "NM", "NV","CA", "HI", "AS", "OR", "WA" };
    private static final Map<String,String> stateToRegionMap = new HashMap<String,String>();
    static {
        // sort them so we can use binary search later
        Arrays.sort(Northeast);
        Arrays.sort(MidAtlantic);
        Arrays.sort(South);
        Arrays.sort(MidWest);
        Arrays.sort(ArkLaTex);
        Arrays.sort(Southwest);
        Arrays.sort(West);
        Arrays.sort(Territories);
        Arrays.sort(ALL_STATES);
        // populate map
        for (String state: Northeast) stateToRegionMap.put(state, "Northeast");
        for (String state: MidAtlantic) stateToRegionMap.put(state, "Mid-Atlantic");
        for (String state: South) stateToRegionMap.put(state, "South");
        for (String state: MidWest) stateToRegionMap.put(state, "Mid-West");
        for (String state: ArkLaTex) stateToRegionMap.put(state, "Ark-La-Tex");
        for (String state: Southwest) stateToRegionMap.put(state, "Southwest");
        for (String state: West) stateToRegionMap.put(state, "West");
        for (String state: Territories) stateToRegionMap.put(state, "Territories");
    }
    
    public static String getRegionName(String state) {
        return stateToRegionMap.get(state);
    }
    
    /**
     * Get the list of states in this region if its a US region or null if international
     * 
     * @return array of state two char names
     */
    public String[] computeStates() {
        if ("Northeast".equals(getName())) {
            return Northeast;
        } else if ("Mid-Atlantic".equals(getName())) {
            return  MidAtlantic;
        } else if ("South".equals(getName())) {
            return South;
        } else if ("Mid-West".equals(getName())) {
            return MidWest;
        } else if ("Ark-La-Tex".equals(getName())) {
            return ArkLaTex;
        } else if ("Southwest".equals(getName())) {
            return Southwest;
        } else if ("West".equals(getName())) {
            return West;
        } else if ("Territories".equals(getName())) {
            return Territories;
        }
        return null;
    }
}
