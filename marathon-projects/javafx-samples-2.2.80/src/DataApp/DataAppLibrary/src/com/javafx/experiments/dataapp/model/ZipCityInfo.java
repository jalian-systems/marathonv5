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
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "ZIP_CITY_INFO", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ZipCityInfo.findAll", query = "SELECT z FROM ZipCityInfo z"),
    @NamedQuery(name = "ZipCityInfo.findByZip", query = "SELECT z FROM ZipCityInfo z WHERE z.zip = :zip"),
    @NamedQuery(name = "ZipCityInfo.findByCity", query = "SELECT z FROM ZipCityInfo z WHERE z.city = :city"),
    @NamedQuery(name = "ZipCityInfo.findByState", query = "SELECT z FROM ZipCityInfo z WHERE z.state = :state"),
    @NamedQuery(name = "ZipCityInfo.findByLatitude", query = "SELECT z FROM ZipCityInfo z WHERE z.latitude = :latitude"),
    @NamedQuery(name = "ZipCityInfo.findByLongitude", query = "SELECT z FROM ZipCityInfo z WHERE z.longitude = :longitude"),
    @NamedQuery(name = "ZipCityInfo.findByTimezone", query = "SELECT z FROM ZipCityInfo z WHERE z.timezone = :timezone"),
    @NamedQuery(name = "ZipCityInfo.findByRegion", query = "SELECT z FROM ZipCityInfo z WHERE z.zip > :startZone and z.zip < :endZone")
})
public class ZipCityInfo implements Serializable {
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "LONGITUDE")
    private Double longitude;
    @Column(name = "LATITUDE")
    private Double latitude;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ZIP")
    private Integer zip;
    @Column(name = "CITY")
    private String city;
    @Column(name = "STATE")
    private String state;
    @Column(name = "TIMEZONE")
    private Short timezone;

    public ZipCityInfo() {
    }

    public ZipCityInfo(Integer zip) {
        this.zip = zip;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Short getTimezone() {
        return timezone;
    }

    public void setTimezone(Short timezone) {
        this.timezone = timezone;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (zip != null ? zip.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZipCityInfo)) {
            return false;
        }
        ZipCityInfo other = (ZipCityInfo) object;
        if ((this.zip == null && other.zip != null) || (this.zip != null && !this.zip.equals(other.zip))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.javafx.experiments.dataapp.model.ZipCityInfo[ zip=" + zip + " ]";
    }
    
    public String zipToString() {
        String s = zip.toString();
        //we represent zip as integer and lose leading zeros
        while (s.length() < 5){
            s = "0".concat(s);
        }
        return s;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
}
