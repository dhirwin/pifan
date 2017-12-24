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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 *
 * @author Dave Irwin (dhirwinjr@gmail.com)
 */
public class GpioControl {
    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(GpioControl.class);

    /**
     * 
     */
    private GpioController gpio;

    /**
     * 
     */
    private final int gpioPinNum;
    private final String desc;

    private GpioPinDigitalOutput outletPin;

    /**
     * 
     * @param gpioPinNum
     * @param desc
     */
    public GpioControl(int gpioPinNum, String desc) {
        super();

        this.gpioPinNum = gpioPinNum;
        this.desc = desc;

        this.init();
    }

    /**
     * 
     */
    private void init() {
        // create gpio controller
        this.gpio = GpioFactory.getInstance();

        Pin gpioPin = RaspiPin.getPinByAddress(this.gpioPinNum);

        // provision gpio pin #08 as an output pin and turn off
        this.outletPin = this.gpio.provisionDigitalOutputPin(gpioPin, this.desc, PinState.LOW);

        // set the shutdown state
        this.outletPin.setShutdownOptions(true, PinState.LOW);
    }

    /**
     * 
     */
    public void turnOn() {
        logger.debug("Turning GPIO on [pinNum: {}, desc: {}]", this.gpioPinNum, this.desc);

        this.outletPin.setState(PinState.HIGH);
    }

    /**
     * 
     */
    public void turnOff() {
        logger.debug("Turning GPIO off [pinNum: {}, desc: {}]", this.gpioPinNum, this.desc);

        this.outletPin.setState(PinState.LOW);
    }
}