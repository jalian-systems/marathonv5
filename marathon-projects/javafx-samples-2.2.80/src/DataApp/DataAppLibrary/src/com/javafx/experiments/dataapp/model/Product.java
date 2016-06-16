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
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "PRODUCT", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p"),
    @NamedQuery(name = "Product.findByProductId", query = "SELECT p FROM Product p WHERE p.productId = :productId"),
    @NamedQuery(name = "Product.findByName", query = "SELECT p FROM Product p WHERE p.name = :name"),
    @NamedQuery(name = "Product.findByBodyStyle", query = "SELECT p FROM Product p WHERE p.bodyStyle = :bodyStyle"),
    @NamedQuery(name = "Product.findByLength", query = "SELECT p FROM Product p WHERE p.length = :length"),
    @NamedQuery(name = "Product.findByWidth", query = "SELECT p FROM Product p WHERE p.width = :width"),
    @NamedQuery(name = "Product.findByHeight", query = "SELECT p FROM Product p WHERE p.height = :height"),
    @NamedQuery(name = "Product.findByCost", query = "SELECT p FROM Product p WHERE p.cost = :cost"),
    @NamedQuery(name = "Product.findByPrice", query = "SELECT p FROM Product p WHERE p.price = :price"),
    @NamedQuery(name = "Product.findByModelYear", query = "SELECT p FROM Product p WHERE p.modelYear = :modelYear")})
public class Product implements Serializable {
    @OneToMany(mappedBy = "productId")
    private Collection<DailySales> dailySalesCollection;
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
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "PRODUCT_ID")
    private Integer productId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "BODY_STYLE")
    private Integer bodyStyle;
    @Column(name = "MODEL_YEAR")
    private Integer modelYear;
    @OneToMany(mappedBy = "productId")
    private Collection<SalesOrderLine> salesOrderLineCollection;
    @JoinColumn(name = "TRANSMISSION_ID", referencedColumnName = "TRANSMISSION_ID")
    @ManyToOne
    private Transmission transmissionId;
    @JoinColumn(name = "PRODUCT_TYPE_ID", referencedColumnName = "PRODUCT_TYPE_ID")
    @ManyToOne
    private ProductType productTypeId;
    @JoinColumn(name = "ENGINE_ID", referencedColumnName = "ENGINE_ID")
    @ManyToOne
    private Engine engineId;

    public Product() {
    }

    public Product(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBodyStyle() {
        return bodyStyle;
    }

    public void setBodyStyle(Integer bodyStyle) {
        this.bodyStyle = bodyStyle;
    }

    public Integer getModelYear() {
        return modelYear;
    }

    public void setModelYear(Integer modelYear) {
        this.modelYear = modelYear;
    }

    @XmlTransient
    public Collection<SalesOrderLine> getSalesOrderLineCollection() {
        return salesOrderLineCollection;
    }

    public void setSalesOrderLineCollection(Collection<SalesOrderLine> salesOrderLineCollection) {
        this.salesOrderLineCollection = salesOrderLineCollection;
    }

    public Transmission getTransmissionId() {
        return transmissionId;
    }

    public void setTransmissionId(Transmission transmissionId) {
        this.transmissionId = transmissionId;
    }

    public ProductType getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(ProductType productTypeId) {
        this.productTypeId = productTypeId;
    }

    public Engine getEngineId() {
        return engineId;
    }

    public void setEngineId(Engine engineId) {
        this.engineId = engineId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productId != null ? productId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        if ((this.productId == null && other.productId != null) || (this.productId != null && !this.productId.equals(other.productId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.javafx.experiments.dataapp.model.Product[ productId=" + productId + " ]";
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

    @XmlTransient
    public Collection<DailySales> getDailySalesCollection() {
        return dailySalesCollection;
    }

    public void setDailySalesCollection(Collection<DailySales> dailySalesCollection) {
        this.dailySalesCollection = dailySalesCollection;
    }
    
}
