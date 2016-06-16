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
package com.javafx.experiments.dataapp.server.service;

import com.javafx.experiments.dataapp.simulation.DailySalesGenerator;
import com.javafx.experiments.dataapp.simulation.SalesSimulator;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@LocalBean
@Startup
public class SingletonSimulationBean {
    @PersistenceContext(unitName = "DataAppLibraryPU")
    private EntityManager em;
    
    SalesSimulator sim;
    DailySalesGenerator hourlySalesGenerator;
    
    Timer simulationTimer;
    Timer hourlySalesTimer;
    
    @Resource
    TimerService timerService;
    
    @PostConstruct
    public void applicationStartup(){   
        sim = new SalesSimulator(em);
        hourlySalesGenerator = new DailySalesGenerator(em);
        
        simulationTimer = timerService.createTimer(SalesSimulator.TIME_BETWEEN_SALES, SalesSimulator.TIME_BETWEEN_SALES, "Creating Auto Sales simulation");
        //run on startup, move over old sales
        hourlySalesTimer = timerService.createTimer(1000, 60*1000*60*24, "Creating data migration service");
    }
        
    @Timeout
    public void timeout(Timer timer) {
        if (timer.equals(simulationTimer)){
            sim.run();
        }
        else if (timer.equals(hourlySalesTimer)){
            hourlySalesGenerator.run();
        }
    }
    
}
