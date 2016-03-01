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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "DISCOUNT_RATE", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DiscountRate.findAll", query = "SELECT d FROM DiscountRate d"),
    @NamedQuery(name = "DiscountRate.findByDiscountRate", query = "SELECT d FROM DiscountRate d WHERE d.discountRate = :discountRate"),
    @NamedQuery(name = "DiscountRate.findByRate", query = "SELECT d FROM DiscountRate d WHERE d.rate = :rate")})
public class DiscountRate implements Serializable {
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "RATE")
    private Double rate;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "DISCOUNT_RATE")
    private Character discountRate;
    @OneToMany(mappedBy = "discountRate")
    private Collection<SalesOrderLine> salesOrderLineCollection;

    public DiscountRate() {
    }

    public DiscountRate(Character discountRate) {
        this.discountRate = discountRate;
    }

    public Character getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Character discountRate) {
        this.discountRate = discountRate;
    }

    @XmlTransient
    public Collection<SalesOrderLine> getSalesOrderLineCollection() {
        return salesOrderLineCollection;
    }

    public void setSalesOrderLineCollection(Collection<SalesOrderLine> salesOrderLineCollection) {
        this.salesOrderLineCollection = salesOrderLineCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (discountRate != null ? discountRate.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DiscountRate)) {
            return false;
        }
        DiscountRate other = (DiscountRate) object;
        if ((this.discountRate == null && other.discountRate != null) || (this.discountRate != null && !this.discountRate.equals(other.discountRate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.javafx.experiments.dataapp.model.DiscountRate[ discountRate=" + discountRate + " ]";
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
    
}
