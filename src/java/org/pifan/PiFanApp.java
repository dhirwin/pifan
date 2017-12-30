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

import org.pifan.io.GpioControl;
import org.pifan.schedule.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dave Irwin (dhirwinjr@gmail.com)
 */
public class PiFanApp {
    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(PiFanApp.class);

    /**
     * 
     */
    private PiFanApp() {
        super();
    }

    /**
     * 
     */
    private void start() {
        logger.debug("Starting PiFan application");

        final GpioControl gpio = new GpioControl(8, "Outlet");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                gpio.shutdown();
            }
        });

        try {
            Properties schProps = new Properties();

            JobScheduler scheduler = new JobScheduler(schProps);
            scheduler.registerWorker(() -> {
                gpio.turnOn();
            }, "0 10,40 * * * ?", "fanOn");

            scheduler.registerWorker(() -> {
                gpio.turnOff();
            }, "0 25,55 * * * ?", "fanOff");

            // default by turning the fan on
            gpio.turnOn();
        } catch (Exception ex) {
            logger.error("Error: " + ex, ex);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            PiFanApp app = new PiFanApp();
            app.start();
        } catch (Exception ex) {
            logger.error("Error: " + ex, ex);
        }
    }
}