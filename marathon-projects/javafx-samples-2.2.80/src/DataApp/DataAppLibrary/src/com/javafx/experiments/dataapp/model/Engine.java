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
@Table(name = "ENGINE", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Engine.findAll", query = "SELECT e FROM Engine e"),
    @NamedQuery(name = "Engine.findByEngineId", query = "SELECT e FROM Engine e WHERE e.engineId = :engineId"),
    @NamedQuery(name = "Engine.findByLitre", query = "SELECT e FROM Engine e WHERE e.litre = :litre"),
    @NamedQuery(name = "Engine.findByCylinders", query = "SELECT e FROM Engine e WHERE e.cylinders = :cylinders"),
    @NamedQuery(name = "Engine.findBySupercharge", query = "SELECT e FROM Engine e WHERE e.supercharge = :supercharge"),
    @NamedQuery(name = "Engine.findByTurbocharge", query = "SELECT e FROM Engine e WHERE e.turbocharge = :turbocharge"),
    @NamedQuery(name = "Engine.findByHybrid", query = "SELECT e FROM Engine e WHERE e.hybrid = :hybrid"),
    @NamedQuery(name = "Engine.findByInline", query = "SELECT e FROM Engine e WHERE e.inline = :inline"),
    @NamedQuery(name = "Engine.findByVee", query = "SELECT e FROM Engine e WHERE e.vee = :vee")})
public class Engine implements Serializable {
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "LITRE")
    private Double litre;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ENGINE_ID")
    private Integer engineId;
    @Column(name = "CYLINDERS")
    private Short cylinders;
    @Column(name = "SUPERCHARGE")
    private Short supercharge;
    @Column(name = "TURBOCHARGE")
    private Short turbocharge;
    @Column(name = "HYBRID")
    private Short hybrid;
    @Column(name = "INLINE")
    private Short inline;
    @Column(name = "VEE")
    private Short vee;
    @OneToMany(mappedBy = "engineId")
    private Collection<Product> productCollection;

    public Engine() {
    }

    public Engine(Integer engineId) {
        this.engineId = engineId;
    }

    public Integer getEngineId() {
        return engineId;
    }

    public void setEngineId(Integer engineId) {
        this.engineId = engineId;
    }

    public Short getCylinders() {
        return cylinders;
    }

    public void setCylinders(Short cylinders) {
        this.cylinders = cylinders;
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

    public Short getHybrid() {
        return hybrid;
    }

    public void setHybrid(Short hybrid) {
        this.hybrid = hybrid;
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

    @XmlTransient
    public Collection<Product> getProductCollection() {
        return productCollection;
    }

    public void setProductCollection(Collection<Product> productCollection) {
        this.productCollection = productCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (engineId != null ? engineId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Engine)) {
            return false;
        }
        Engine other = (Engine) object;
        if ((this.engineId == null && other.engineId != null) || (this.engineId != null && !this.engineId.equals(other.engineId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.javafx.experiments.dataapp.model.Engine[ engineId=" + engineId + " ]";
    }

    public Double getLitre() {
        return litre;
    }

    public void setLitre(Double litre) {
        this.litre = litre;
    }
    
}
