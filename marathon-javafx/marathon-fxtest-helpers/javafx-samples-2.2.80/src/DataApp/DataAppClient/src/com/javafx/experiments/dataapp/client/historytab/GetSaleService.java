/*
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
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
package com.javafx.experiments.dataapp.client.historytab;

import com.javafx.experiments.dataapp.client.rest.CumulativeLiveSalesClient;
import com.javafx.experiments.dataapp.model.Region;
import com.javafx.experiments.dataapp.model.transit.ProductTypeTransitCumulativeSeriesSales;
import com.javafx.experiments.dataapp.model.transit.RegionTransitCumulativeSales;
import com.javafx.experiments.dataapp.model.transit.StateTransitCumulativeSales;
import com.javafx.experiments.dataapp.model.transit.TransitCumulativeSales;
import java.util.concurrent.atomic.AtomicReference;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;

class GetSaleService extends Service<Pair<ProductTypeTransitCumulativeSeriesSales[], TransitCumulativeSales[]>> {
    private final CumulativeLiveSalesClient clsClient;
    private final AtomicReference<String> from = new AtomicReference<String>();
    private final AtomicReference<String> to = new AtomicReference<String>();
    private final AtomicReference<Object> regionSelection = new AtomicReference<Object>();

    public GetSaleService(CumulativeLiveSalesClient clsClient) {
        this.clsClient = clsClient;
    }

    public void setRange(Integer from, Integer to) {
        this.from.set(from.toString());
        this.to.set(to.toString());
    }

    public void setRegionSelection(Object o) {
        this.regionSelection.set(o);
    }

    @Override
    protected Task<Pair<ProductTypeTransitCumulativeSeriesSales[], TransitCumulativeSales[]>> createTask() {
        return new Task<Pair<ProductTypeTransitCumulativeSeriesSales[], TransitCumulativeSales[]>>() {

            @Override
            protected Pair<ProductTypeTransitCumulativeSeriesSales[], TransitCumulativeSales[]> call() throws Exception {
                ProductTypeTransitCumulativeSeriesSales[] productResults = null;
                TransitCumulativeSales[] regionOrStateResults = null;
                if (regionSelection.get().equals("All Regions")) {
                    productResults = clsClient.findTypeRange_JSON(ProductTypeTransitCumulativeSeriesSales[].class, from.get(), to.get());
                    if (isCancelled()) {
                        return null;
                    }
                    regionOrStateResults = clsClient.findRegionRange_JSON(RegionTransitCumulativeSales[].class, from.get(), to.get());
                    if (isCancelled()) {
                        return null;
                    }
                } else {
                    int productId = ((Region) regionSelection.get()).getRegionId();
                    productResults = clsClient.findTypeRegionRange_JSON(ProductTypeTransitCumulativeSeriesSales[].class, from.get(), to.get(), productId);
                    if (isCancelled()) {
                        return null;
                    }
                    regionOrStateResults = clsClient.findRegionStateRange_JSON(StateTransitCumulativeSales[].class, from.get(), to.get(), productId);
                    if (isCancelled()) {
                        return null;
                    }
                }
                return new Pair(productResults, regionOrStateResults);
            }
        };
    }
    
}
