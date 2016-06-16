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
@Table(name = "TRANSMISSION", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Transmission.findAll", query = "SELECT t FROM Transmission t"),
    @NamedQuery(name = "Transmission.findByTransmissionId", query = "SELECT t FROM Transmission t WHERE t.transmissionId = :transmissionId"),
    @NamedQuery(name = "Transmission.findByType", query = "SELECT t FROM Transmission t WHERE t.type = :type"),
    @NamedQuery(name = "Transmission.findByGears", query = "SELECT t FROM Transmission t WHERE t.gears = :gears")})
public class Transmission implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "TRANSMISSION_ID")
    private Integer transmissionId;
    @Basic(optional = false)
    @Column(name = "TYPE")
    private String type;
    @Column(name = "GEARS")
    private Short gears;
    @OneToMany(mappedBy = "transmissionId")
    private Collection<Product> productCollection;

    public Transmission() {
    }

    public Transmission(Integer transmissionId) {
        this.transmissionId = transmissionId;
    }

    public Transmission(Integer transmissionId, String type) {
        this.transmissionId = transmissionId;
        this.type = type;
    }

    public Integer getTransmissionId() {
        return transmissionId;
    }

    public void setTransmissionId(Integer transmissionId) {
        this.transmissionId = transmissionId;
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
        hash += (transmissionId != null ? transmissionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Transmission)) {
            return false;
        }
        Transmission other = (Transmission) object;
        if ((this.transmissionId == null && other.transmissionId != null) || (this.transmissionId != null && !this.transmissionId.equals(other.transmissionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.javafx.experiments.dataapp.model.Transmission[ transmissionId=" + transmissionId + " ]";
    }
    
}
