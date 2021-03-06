/**
 * This file is part of Mobile Robot Framework.
 * Mobile Robot Framework is free software under the terms of GNU AFFERO GENERAL PUBLIC LICENSE.
 */
package de.developgroup.mrf.rover.collision;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import de.developgroup.mrf.rover.pcf8591.IRSensor;
import de.developgroup.mrf.rover.pcf8591.PCF8591ADConverter;

public interface IRSensorFactory {
    /**
     * Create a new IR sensor using the specified channel for reading IR values
     * and the specified pin for switching the accompanying LED.
     * @param channel the channel to read sensor values from
     * @param ledPin the pin where the IR LED can be found
     * @return a new IRSensor with the given values
     */
    IRSensor create(PCF8591ADConverter.InputChannel channel,
                    GpioPinDigitalOutput ledPin);
}
