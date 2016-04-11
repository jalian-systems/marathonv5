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
package com.javafx.experiments.dataapp.model.transit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TransitCumulativeSales implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Double cost;
    
    private Double sales;
    
    private Long units;
    
    private Integer startDailySalesId;
    
    private Integer endDailySalesId;
    
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getEndDailySalesId() {
        return endDailySalesId;
    }

    public void setEndDailySalesId(Integer endDailySalesId) {
        this.endDailySalesId = endDailySalesId;
    }

    public Integer getStartDailySalesId() {
        return startDailySalesId;
    }

    public void setStartDailySalesId(Integer endDailySalesId) {
        this.startDailySalesId = endDailySalesId;
    }
    
    public Double getCost() {
        return cost;
    }

    public Double getSales() {
        return sales;
    }

    public Long getUnits() {
        return units;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setSales(Double sales) {
        this.sales = sales;
    }

    public void setUnits(Long units) {
        this.units = units;
    }
    
    public Calendar makeCalendar() {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        return c;
    }
}
