/**************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *************************************************************************/

package org.pifan;

import java.util.Properties;

import org.pifan.util.PropertiesUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dave Irwin (dhirwinjr@gmail.com)
 */
public class PifanScheduler {
    /**
     * 
     */
    private static Logger logger = LoggerFactory.getLogger(PifanScheduler.class);

    /**
     * We default to using 20 threads but this can be configured as needed.
     */
    private static final int defaultNumThreads = 20;
    private int numThreads = defaultNumThreads;

    /**
     * 
     */
    public static final String POLLER_NAME = "pollerName";
    public static final String WORKER_NAME = "workerName";
    public static final String WORKER = "worker";

    /**
     * Whether running jobs should be interrupted when the job is unregistered.
     */
    private boolean interruptOnUnregistration = false;

    /**
     * 
     */
    private SchedulerFactory sf = new StdSchedulerFactory();
    private Scheduler sched;

    /**
     * 
     */
    private String pollerName = "FanSchedule";

    /**
     * 
     * @param properties
     */
    public PifanScheduler(Properties properties) {
        super();

        try {
            /*
             * Initialize the properties.
             * 
             * See:
             *     http://quartz-scheduler.org/documentation/quartz-1.x/configuration/ConfigThreadPool
             *     http://quartz-scheduler.org/documentation/quartz-1.x/configuration/ConfigMain
             */
            Properties schedulerProps = new Properties();

            this.numThreads = PropertiesUtils.getIntegerValue(properties, "poller." + this.pollerName + ".numThreads",
                    defaultNumThreads);

            schedulerProps.setProperty("org.quartz.scheduler.instanceName", this.pollerName);
            schedulerProps.setProperty("org.quartz.scheduler.instanceId", "AUTO");

            schedulerProps.setProperty("org.quartz.threadPool.threadCount", String.valueOf(this.numThreads));
            schedulerProps.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
            schedulerProps.setProperty("org.quartz.threadPool.threadPriority", String.valueOf("5"));

            this.sf = new StdSchedulerFactory(schedulerProps);
            this.sched = this.sf.getScheduler();
            this.sched.start();

            logger.info("Quartz poller [pollerName: " + this.pollerName + "] initialized");
            logger.info("  number of threads:         " + this.numThreads);
            logger.info("  interruptOnUnregistration: " + this.interruptOnUnregistration);
        } catch (SchedulerException e) {
            logger.error(
                    "Scheduler exception while initializing Quartz poller [pollerName: " + this.pollerName + "]: " + e,
                    e);

            // we really can't continue
            throw new RuntimeException(e);
        }
    }
}