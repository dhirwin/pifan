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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.pifan.util.TimeUtil;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Quartz poller job implementation.
 *
 * @author Dave Irwin (david.irwin@parsons.com)
 */
public class PifanJob implements InterruptableJob {
    /**
     * 
     */
    private static final Logger logger = LoggerFactory.getLogger(PifanJob.class);

    /**
     * 
     */
    private String pollerName;
    private String workerName;

    /**
     * Used for tracking the current running thread so that we can
     * interrupt if needed.
     */
    private final AtomicReference<Thread> runningThread = new AtomicReference<>();
    private final AtomicBoolean stopFlag = new AtomicBoolean(false);

    /**
     * 
     */
    public PifanJob() {
        super();
    }

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        final long startTime = System.currentTimeMillis();

        this.workerName = (String) context.getJobDetail().getJobDataMap().get(JobScheduler.WORKER_NAME);
        this.pollerName = (String) context.getJobDetail().getJobDataMap().get(JobScheduler.POLLER_NAME);

        StringBuilder sb = new StringBuilder();
        sb.append("Starting task execution [name: ").append(this.workerName);
        sb.append(", pollerName: ").append(this.pollerName);
        sb.append("]");
        logger.trace(sb.toString());

        try {
            // keep a reference to this running thread
            this.runningThread.set(Thread.currentThread());

            // extract & execute the worker
            Runnable worker = (Runnable) context.getJobDetail().getJobDataMap().get(JobScheduler.WORKER);

            while (!this.stopFlag.get()) {
                // run the actual worker
                worker.run();

                // we only run the worker once
                this.stopFlag.set(true);
            }
        } finally {
            long completedTime = System.currentTimeMillis();

            sb = new StringBuilder();
            sb.append("Quartz poller job complete [name: ").append(this.workerName);
            sb.append(", pollerName: ").append(this.pollerName);
            sb.append(", executionTime: ").append(TimeUtil.elapsedTime(startTime, completedTime, "msS"));
            sb.append("]");
            logger.trace(sb.toString());

            // null out the running thread
            this.runningThread.set(null);
        }
    }

    /**
     * Attempt to interrupt an already running Quartz job.
     * <p>
     * This method will be called on the same thread that originally called the
     * QuartzPoller's <code>unregisterWorker</code> method.
     * 
     * @see org.quartz.InterruptableJob#interrupt()
     */
    public void interrupt() throws UnableToInterruptJobException {
        logger.debug("Received an 'interrupt' callback [pollerName: " + this.pollerName + ", workerName: "
                + this.workerName + "]");

        this.stopFlag.set(true);

        /*
         * Attempt to interrupt the running thread. 
         * 
         * Keep in mind that it's up to the underlying code being run to honor the
         * thread's interrupted status and stop as needed.
         */
        Thread thread = this.runningThread.getAndSet(null);
        if (thread != null) {
            thread.interrupt();
        }
    }
}