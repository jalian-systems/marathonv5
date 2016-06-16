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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "PROJECTED_SALES", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProjectedSales.findAll", query = "SELECT p FROM ProjectedSales p"),
    @NamedQuery(name = "ProjectedSales.findByProjectedSalesId", query = "SELECT p FROM ProjectedSales p WHERE p.projectedSalesId = :projectedSalesId"),
    @NamedQuery(name = "ProjectedSales.findBySalesYear", query = "SELECT p FROM ProjectedSales p WHERE p.salesYear = :salesYear"),
    @NamedQuery(name = "ProjectedSales.findByQuarter", query = "SELECT p FROM ProjectedSales p WHERE p.quarter = :quarter"),
    @NamedQuery(name = "ProjectedSales.findByProjectedSales", query = "SELECT p FROM ProjectedSales p WHERE p.projectedSales = :projectedSales")})
public class ProjectedSales implements Serializable {
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PROJECTED_SALES")
    private Double projectedSales;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "PROJECTED_SALES_ID")
    private Integer projectedSalesId;
    @Column(name = "SALES_YEAR")
    private Short salesYear;
    @Column(name = "QUARTER")
    private Short quarter;
    @JoinColumn(name = "REGION_ID", referencedColumnName = "REGION_ID")
    @ManyToOne
    private Region regionId;
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID")
    @ManyToOne
    private Employee employeeId;
    @JoinColumn(name = "DEALER_ID", referencedColumnName = "DEALER_ID")
    @ManyToOne
    private Dealer dealerId;

    public ProjectedSales() {
    }

    public ProjectedSales(Integer projectedSalesId) {
        this.projectedSalesId = projectedSalesId;
    }

    public Integer getProjectedSalesId() {
        return projectedSalesId;
    }

    public void setProjectedSalesId(Integer projectedSalesId) {
        this.projectedSalesId = projectedSalesId;
    }

    public Short getSalesYear() {
        return salesYear;
    }

    public void setSalesYear(Short salesYear) {
        this.salesYear = salesYear;
    }

    public Short getQuarter() {
        return quarter;
    }

    public void setQuarter(Short quarter) {
        this.quarter = quarter;
    }

    public Region getRegionId() {
        return regionId;
    }

    public void setRegionId(Region regionId) {
        this.regionId = regionId;
    }

    public Employee getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Employee employeeId) {
        this.employeeId = employeeId;
    }

    public Dealer getDealerId() {
        return dealerId;
    }

    public void setDealerId(Dealer dealerId) {
        this.dealerId = dealerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectedSalesId != null ? projectedSalesId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectedSales)) {
            return false;
        }
        ProjectedSales other = (ProjectedSales) object;
        if ((this.projectedSalesId == null && other.projectedSalesId != null) || (this.projectedSalesId != null && !this.projectedSalesId.equals(other.projectedSalesId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.javafx.experiments.dataapp.model.ProjectedSales[ projectedSalesId=" + projectedSalesId + " ]";
    }

    public Double getProjectedSales() {
        return projectedSales;
    }

    public void setProjectedSales(Double projectedSales) {
        this.projectedSales = projectedSales;
    }
    
}
