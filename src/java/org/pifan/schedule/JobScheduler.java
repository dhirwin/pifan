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

package org.pifan.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.Properties;

import org.pifan.util.PropertiesUtils;
import org.pifan.util.TimeUtil;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A job scheduler.
 *
 * @author Dave Irwin (dhirwinjr@gmail.com)
 */
public class JobScheduler {
    /**
     * 
     */
    private static Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    /**
     * We default to using 20 threads but this can be configured as needed.
     */
    private static final int defaultNumThreads = 5;
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
    public JobScheduler(Properties properties) {
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
            logger.info("  number of threads:         {}", this.numThreads);
            logger.info("  interruptOnUnregistration: {}", this.interruptOnUnregistration);
        } catch (SchedulerException e) {
            logger.error(
                    "Scheduler exception while initializing Quartz poller [pollerName: " + this.pollerName + "]: " + e,
                    e);

            // we really can't continue
            throw new RuntimeException(e);
        }
    }

    /**
     * Register a work to run based on the given CRON expression.
     * 
     * @param worker
     * @param cronExp The CRON expression
     * @param workerName
     */
    public final void registerWorker(Runnable worker, String cronExp, String workerName) {
        logger.debug("Registering worker with CRON expression:");
        logger.debug("  Worker name:     {}", workerName);
        logger.debug("  CRON expression: {}", cronExp);

        this.unregisterWorker(workerName, "registering worker using CRON expression");

        try {
            // create the job detail
            JobDetail jobDetail = newJob(PifanJob.class).withIdentity(workerName, this.pollerName).build();

            CronTrigger trigger = newTrigger().withDescription(this.pollerName + "--" + workerName)
                    .withIdentity(workerName, this.pollerName).withSchedule(cronSchedule(cronExp)).build();

            // put a reference to the actual executable worker code
            JobDataMap dataMap = jobDetail.getJobDataMap();
            dataMap.put(POLLER_NAME, this.pollerName);
            dataMap.put(WORKER, worker);
            dataMap.put(WORKER_NAME, workerName);

            // schedule the job
            this.sched.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            logger.error("Error registering worker [pollerName: " + this.pollerName + ", workerName: " + workerName
                    + "]: " + e, e);
        }
    }

    /**
     * 
     * @param worker
     * @param intervalInMs
     * @param delayInMs
     * @param workerName
     */
    public void registerWorker(Runnable worker, long intervalInMs, long delayInMs, String workerName) {
        this.registerWorkerAtFixedRate(worker, intervalInMs, delayInMs, workerName);
    }

    /**
     * 
     * @param worker
     * @param intervalInMs
     * @param delayInMs
     * @param workerName
     */
    public void registerWorkerAtFixedRate(Runnable worker, long intervalInMs, long delayInMs, String workerName) {
        logger.debug("Registering worker at fixed rate:");
        logger.debug("  Worker name:       {}", workerName);
        logger.debug("  Worker init delay: {}", TimeUtil.elapsedTime(delayInMs, "HmsS"));
        logger.debug("  Worker interval:   {}", TimeUtil.elapsedTime(intervalInMs, "DHmsS"));

        this.unregisterWorker(workerName, "registering worker at fixed rate");

        try {
            // create the job detail
            JobDetail jobDetail = newJob(PifanJob.class).withIdentity(workerName, this.pollerName).build();

            Date startTime = new Date(System.currentTimeMillis() + delayInMs);

            // create the trigger
            SimpleTrigger trigger = newTrigger().withDescription(this.pollerName + "--" + workerName)
                    .withIdentity(workerName, this.pollerName).startAt(startTime)
                    .withSchedule(simpleSchedule().withIntervalInMilliseconds(intervalInMs).repeatForever()).build();

            // put a reference to the actual executable worker code
            JobDataMap dataMap = jobDetail.getJobDataMap();
            dataMap.put(POLLER_NAME, this.pollerName);
            dataMap.put(WORKER, worker);
            dataMap.put(WORKER_NAME, workerName);

            // schedule the job
            this.sched.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            logger.error("Error registering worker [pollerName: " + this.pollerName + ", workerName: " + workerName
                    + "]: " + e, e);
        }
    }

    /**
     * 
     * @param worker
     * @param delay
     * @param workerName
     */
    public void registerWorkerOnce(Runnable worker, long delay, String workerName) {
        logger.debug("Registering worker once:");
        logger.debug("  Worker name:  {}", workerName);
        logger.debug("  Worker delay: {}", TimeUtil.elapsedTime(delay, "DHmsS"));

        this.unregisterWorker(workerName, "registering worker once");

        try {
            // create the job detail
            JobDetail jobDetail = newJob(PifanJob.class).withIdentity(workerName, this.pollerName).build();

            Date startTime = new Date(System.currentTimeMillis() + delay);

            SimpleTrigger trigger = (SimpleTrigger) newTrigger().withDescription(this.pollerName + "--" + workerName)
                    .withIdentity(workerName, this.pollerName).startAt(startTime).build();

            // put a reference to the actual executable worker code
            JobDataMap dataMap = jobDetail.getJobDataMap();
            dataMap.put(POLLER_NAME, this.pollerName);
            dataMap.put(WORKER, worker);
            dataMap.put(WORKER_NAME, workerName);

            // schedule the job
            this.sched.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            logger.error(
                    "Error registering worker [pollerName: " + this.pollerName + ", name: " + workerName + "]: " + e,
                    e);
        }
    }

    /**
     * 
     * @param workerName
     */
    public final void unregisterWorker(String workerName) {
        this.unregisterWorker(workerName, "Manually unregistered");
    }

    /**
     * 
     * @param workerName
     * @param reason
     */
    private void unregisterWorker(String workerName, String reason) {
        logger.debug("Unregistering worker:");
        logger.debug("  Worker name:           {}", workerName);
        logger.debug("  Unregistration reason: {}", reason);

        try {
            JobKey jobKey = jobKey(workerName, this.pollerName);

            // try and interrupt an already running worker
            if (this.interruptOnUnregistration) {
                if (this.sched.interrupt(jobKey)) {
                    logger.debug("Successfully interrupted worker");
                } else {
                    logger.warn("Unable to successfully interrupt worker");
                }
            }

            // now actually delete the worker
            if (this.sched.deleteJob(jobKey)) {
                logger.debug("Successfully unregistered worker [name: {}]", workerName);
            }
        } catch (SchedulerException e) {
            logger.error(
                    "Error unregistering worker [pollerName: " + this.pollerName + ", name: " + workerName + "]: " + e,
                    e);
        }
    }
}