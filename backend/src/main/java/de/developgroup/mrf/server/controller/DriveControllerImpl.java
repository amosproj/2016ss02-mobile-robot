/**
 * This file is part of Mobile Robot Framework.
 * Mobile Robot Framework is free software under the terms of GNU AFFERO GENERAL PUBLIC LICENSE.
 */
package de.developgroup.mrf.server.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import de.developgroup.mrf.rover.collision.CollisionRunnable;
import de.developgroup.mrf.rover.motor.MotorController;
import de.developgroup.mrf.rover.motor.MotorControllerConfiguration;
import de.developgroup.mrf.rover.motor.MotorControllerImpl;
import de.developgroup.mrf.rover.pwmgenerator.PCA9685PWMGenerator;
import org.cfg4j.provider.ConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Class to handle sending drive commands to the actual hardware.
 * Not testable - hardware dependant!
 */
@Singleton
public class DriveControllerImpl extends AbstractDriveController {

    private static Logger LOGGER = LoggerFactory.getLogger(DriveControllerImpl.class);

    private double speedMultiplier = 1.0;

    public MotorController leftMotor;
    public MotorController rightMotor;

    @Inject
    public DriveControllerImpl(ContinuousDrivingAlgorithm drivingAlgorithm, CollisionRunnable collisionRunnable) throws IOException {
        super(drivingAlgorithm, collisionRunnable);
    }

    @Override
    public void initialize(ConfigurationProvider configurationProvider) throws IOException {
        // this is a "robot leg problem"
        // see https://github.com/google/guice/wiki/FrequentlyAskedQuestions#How_do_I_build_two_similar_but_slightly_different_trees_of_objec
        // we have a leftMotor and a rightMotor - same class, different configuration
        // I did not use the private module solution as it is a bit scary to look at. But maybe:
        // TODO: clean this mess up - MotorControllers should be injected
        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        I2CDevice device = bus.getDevice(0x40);
        PCA9685PWMGenerator driver = new PCA9685PWMGenerator(device);
        driver.open();
        driver.setFrequency(50);

        leftMotor = new MotorControllerImpl(driver.getOutput(14),
                configurationProvider.bind("motorLeft", MotorControllerConfiguration.class));
        rightMotor = new MotorControllerImpl(driver.getOutput(15),
                configurationProvider.bind("motorRight", MotorControllerConfiguration.class));

        LOGGER.debug("Completed setting up DriveController");
    }

    @Override
    public void applyMotorSettings(MotorSettings settings) throws IOException {
        LOGGER.trace("left: {} right: {}", settings.leftMotorPercentage, settings.rightMotorPercentage);
        setCurrentMotorSettings(settings);
        leftMotor.setSpeedPercentage(speedMultiplier * settings.leftMotorPercentage);
        rightMotor.setSpeedPercentage(speedMultiplier * settings.rightMotorPercentage);
    }

    /**
     * Return a value in a defined interval [min, max]??or min or max if the value exceeds the interval.
     * @param val value to confine to the interval
     * @param min lower border of the interval
     * @param max upper border of the interval
     * @return
     */
    public int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public void setSpeedMultiplier(double value) throws IOException {
        if (value >= 0.0 && value <= 1.0) {
            speedMultiplier = value;
            applyMotorSettings(getCurrentMotorSettings());
        } else {
            LOGGER.error("Speed multipilier value is invalid: " + value);
        }
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
}
