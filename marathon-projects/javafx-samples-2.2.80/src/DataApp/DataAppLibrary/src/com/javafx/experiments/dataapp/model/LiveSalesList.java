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
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "LIVE_SALES_LIST", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LiveSalesList.findAll", query = "SELECT l FROM LiveSalesList l"),
    @NamedQuery(name = "LiveSalesList.findByOrderLineId", query = "SELECT l FROM LiveSalesList l WHERE l.orderLineId = :orderLineId"),
    @NamedQuery(name = "LiveSalesList.findByProduct", query = "SELECT l FROM LiveSalesList l WHERE l.product = :product"),
    @NamedQuery(name = "LiveSalesList.findByType", query = "SELECT l FROM LiveSalesList l WHERE l.type = :type"),
    @NamedQuery(name = "LiveSalesList.findBySubType", query = "SELECT l FROM LiveSalesList l WHERE l.subType = :subType"),
    @NamedQuery(name = "LiveSalesList.findByPrice", query = "SELECT l FROM LiveSalesList l WHERE l.price = :price"),
    @NamedQuery(name = "LiveSalesList.findByRegion", query = "SELECT l FROM LiveSalesList l WHERE l.region = :region"),
    @NamedQuery(name = "LiveSalesList.findByChannel", query = "SELECT l FROM LiveSalesList l WHERE l.channel = :channel"),
    @NamedQuery(name = "LiveSalesList.findByQuantity", query = "SELECT l FROM LiveSalesList l WHERE l.quantity = :quantity"),
    @NamedQuery(name = "LiveSalesList.findByDealer", query = "SELECT l FROM LiveSalesList l WHERE l.dealer = :dealer"),
    @NamedQuery(name = "LiveSalesList.findBySalesman", query = "SELECT l FROM LiveSalesList l WHERE l.salesman = :salesman"),
    @NamedQuery(name = "LiveSalesList.findByDate", query = "SELECT l FROM LiveSalesList l WHERE l.date = :date"),
    @NamedQuery(name = "LiveSalesList.findFromOrderLineId", query = "SELECT l FROM LiveSalesList l WHERE l.orderLineId > :orderLineId")})
public class LiveSalesList implements Serializable {
    @Column(name = "DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Column(name = "PRODUCT_TYPE_ID")
    private Integer productTypeId;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRICE")
    private Double price;
    @Column(name = "LATITUDE")
    private Double latitude;
    @Column(name = "LONGITUDE")
    private Double longitude;
    @Column(name = "CITY")
    private String city;
    @Column(name = "STATE")
    private String state;
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Id
    @Column(name = "ORDER_LINE_ID")
    private int orderLineId;
    @Column(name = "PRODUCT")
    private String product;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "SUBTYPE")
    private String subType;
    @Column(name = "REGION")
    private String region;
    @Column(name = "CHANNEL")
    private String channel;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "DEALER")
    private String dealer;
    @Column(name = "SALESMAN")
    private String salesman;

    public LiveSalesList() {
    }

    public int getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(int orderLineId) {
        this.orderLineId = orderLineId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(Integer productTypeId) {
        this.productTypeId = productTypeId;
    }
    
}
