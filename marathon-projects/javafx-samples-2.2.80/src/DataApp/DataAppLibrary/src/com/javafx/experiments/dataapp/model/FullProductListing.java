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
@Table(name = "FULL_PRODUCT_LISTING", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FullProductListing.findAll", query = "SELECT f FROM FullProductListing f"),
    @NamedQuery(name = "FullProductListing.findByProductId", query = "SELECT f FROM FullProductListing f WHERE f.productId = :productId"),
    @NamedQuery(name = "FullProductListing.findByName", query = "SELECT f FROM FullProductListing f WHERE f.name = :name"),
    @NamedQuery(name = "FullProductListing.findByClass1", query = "SELECT f FROM FullProductListing f WHERE f.class1 = :class1"),
    @NamedQuery(name = "FullProductListing.findBySubclass", query = "SELECT f FROM FullProductListing f WHERE f.subclass = :subclass"),
    @NamedQuery(name = "FullProductListing.findByLength", query = "SELECT f FROM FullProductListing f WHERE f.length = :length"),
    @NamedQuery(name = "FullProductListing.findByWidth", query = "SELECT f FROM FullProductListing f WHERE f.width = :width"),
    @NamedQuery(name = "FullProductListing.findByHeight", query = "SELECT f FROM FullProductListing f WHERE f.height = :height"),
    @NamedQuery(name = "FullProductListing.findByCost", query = "SELECT f FROM FullProductListing f WHERE f.cost = :cost"),
    @NamedQuery(name = "FullProductListing.findByPrice", query = "SELECT f FROM FullProductListing f WHERE f.price = :price"),
    @NamedQuery(name = "FullProductListing.findByModelYear", query = "SELECT f FROM FullProductListing f WHERE f.modelYear = :modelYear"),
    @NamedQuery(name = "FullProductListing.findByCylinders", query = "SELECT f FROM FullProductListing f WHERE f.cylinders = :cylinders"),
    @NamedQuery(name = "FullProductListing.findByLitre", query = "SELECT f FROM FullProductListing f WHERE f.litre = :litre"),
    @NamedQuery(name = "FullProductListing.findByInline", query = "SELECT f FROM FullProductListing f WHERE f.inline = :inline"),
    @NamedQuery(name = "FullProductListing.findByVee", query = "SELECT f FROM FullProductListing f WHERE f.vee = :vee"),
    @NamedQuery(name = "FullProductListing.findByHybrid", query = "SELECT f FROM FullProductListing f WHERE f.hybrid = :hybrid"),
    @NamedQuery(name = "FullProductListing.findBySupercharge", query = "SELECT f FROM FullProductListing f WHERE f.supercharge = :supercharge"),
    @NamedQuery(name = "FullProductListing.findByTurbocharge", query = "SELECT f FROM FullProductListing f WHERE f.turbocharge = :turbocharge"),
    @NamedQuery(name = "FullProductListing.findByType", query = "SELECT f FROM FullProductListing f WHERE f.type = :type"),
    @NamedQuery(name = "FullProductListing.findByGears", query = "SELECT f FROM FullProductListing f WHERE f.gears = :gears")})
public class FullProductListing implements Serializable {
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "LENGTH")
    private Double length;
    @Column(name = "WIDTH")
    private Double width;
    @Column(name = "HEIGHT")
    private Double height;
    @Column(name = "COST")
    private Double cost;
    @Column(name = "PRICE")
    private Double price;
    @Column(name = "LITRE")
    private Double litre;
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "PRODUCT_ID")
    @Id
    private int productId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "CLASS")
    private String class1;
    @Column(name = "SUBCLASS")
    private String subclass;
    @Column(name = "MODEL_YEAR")
    private Integer modelYear;
    @Column(name = "CYLINDERS")
    private Short cylinders;
    @Column(name = "INLINE")
    private Short inline;
    @Column(name = "VEE")
    private Short vee;
    @Column(name = "HYBRID")
    private Short hybrid;
    @Column(name = "SUPERCHARGE")
    private Short supercharge;
    @Column(name = "TURBOCHARGE")
    private Short turbocharge;
    @Basic(optional = false)
    @Column(name = "TYPE")
    private String type;
    @Column(name = "GEARS")
    private Short gears;

    public FullProductListing() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClass1() {
        return class1;
    }

    public void setClass1(String class1) {
        this.class1 = class1;
    }

    public String getSubclass() {
        return subclass;
    }

    public void setSubclass(String subclass) {
        this.subclass = subclass;
    }

    public Integer getModelYear() {
        return modelYear;
    }

    public void setModelYear(Integer modelYear) {
        this.modelYear = modelYear;
    }

    public Short getCylinders() {
        return cylinders;
    }

    public void setCylinders(Short cylinders) {
        this.cylinders = cylinders;
    }

    public Short getInline() {
        return inline;
    }

    public void setInline(Short inline) {
        this.inline = inline;
    }

    public Short getVee() {
        return vee;
    }

    public void setVee(Short vee) {
        this.vee = vee;
    }

    public Short getHybrid() {
        return hybrid;
    }

    public void setHybrid(Short hybrid) {
        this.hybrid = hybrid;
    }

    public Short getSupercharge() {
        return supercharge;
    }

    public void setSupercharge(Short supercharge) {
        this.supercharge = supercharge;
    }

    public Short getTurbocharge() {
        return turbocharge;
    }

    public void setTurbocharge(Short turbocharge) {
        this.turbocharge = turbocharge;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Short getGears() {
        return gears;
    }

    public void setGears(Short gears) {
        this.gears = gears;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getLitre() {
        return litre;
    }

    public void setLitre(Double litre) {
        this.litre = litre;
    }
    
}
