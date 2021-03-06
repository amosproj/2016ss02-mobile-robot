/*
 * This file is part of Mobile Robot Framework.
 * Mobile Robot Framework is free software under the terms of GNU AFFERO GENERAL PUBLIC LICENSE.
 */

package de.developgroup.mrf.server.controller;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class ContinuousDrivingAlgorithmImplTest {

    ContinuousDrivingAlgorithmImpl algorithm;

    @Before
    public void setUp() {
        algorithm = new ContinuousDrivingAlgorithmImpl();
    }

    @Test
    public void testFullPowerForward() {
        MotorSettings setting = algorithm.calculateMotorSetting(90, 100);

        Assert.assertEquals(1d, setting.leftMotorPercentage);
        Assert.assertEquals(1d, setting.rightMotorPercentage);
    }

    @Test
    public void testFullPowerBackward() {
        MotorSettings setting = algorithm.calculateMotorSetting(270, 100);

        Assert.assertEquals(-1d, setting.leftMotorPercentage);
        Assert.assertEquals(-1d, setting.rightMotorPercentage);
    }

    @Test
    public void testRoverDoesTheStopAtSpeedZero() {
        for (int angle = 0; angle < 360; angle += 90) {
            MotorSettings setting = algorithm.calculateMotorSetting(angle, 0);

            Assert.assertEquals(0d, setting.leftMotorPercentage, 0);
            Assert.assertEquals(0d, setting.rightMotorPercentage, 0);
        }
    }

    @Test
    public void testSlightTurnRightForward() {
        MotorSettings setting = algorithm.calculateMotorSetting(50, 100);

        Assert.assertEquals(1d, setting.leftMotorPercentage);
        Assert.assertTrue(setting.rightMotorPercentage > 0d);
        Assert.assertTrue("Left motor should turn faster than the right",
                setting.leftMotorPercentage > setting.rightMotorPercentage);
    }

    @Test
    public void testSlightTurnLeftForward() {
        MotorSettings setting = algorithm.calculateMotorSetting(130, 100);

        Assert.assertEquals(1d, setting.rightMotorPercentage);
        Assert.assertTrue(setting.leftMotorPercentage > 0d);
        Assert.assertTrue("Right motor should turn faster than the left",
                setting.rightMotorPercentage > setting.leftMotorPercentage);
    }

    @Test
    public void testSlightTurnLeftBackward() {
        MotorSettings setting = algorithm.calculateMotorSetting(230, 100);

        Assert.assertEquals(-1d, setting.rightMotorPercentage);
        Assert.assertTrue(setting.leftMotorPercentage < 0d);
        Assert.assertTrue("Right motor should turn faster than the left",
                Math.abs(setting.rightMotorPercentage) > Math.abs(setting.leftMotorPercentage));
    }

    @Test
    public void testSlightTurnRightBackward() {
        MotorSettings setting = algorithm.calculateMotorSetting(310, 100);

        Assert.assertEquals(-1d, setting.leftMotorPercentage);
        Assert.assertTrue(setting.rightMotorPercentage < 0d);
        Assert.assertTrue("Left motor should turn faster than the right",
                Math.abs(setting.leftMotorPercentage) > Math.abs(setting.rightMotorPercentage));
    }

    @Test
    public void testRotateLeft() {
        MotorSettings setting = algorithm.calculateMotorSetting(180, 100);

        Assert.assertEquals(1d, setting.rightMotorPercentage);
        Assert.assertEquals(-1d, setting.leftMotorPercentage);
    }

    @Test
    public void testRotateRight() {
        MotorSettings setting = algorithm.calculateMotorSetting(0, 100);

        Assert.assertEquals(-1d, setting.rightMotorPercentage);
        Assert.assertEquals(1d, setting.leftMotorPercentage);
    }

    @Test
    public void testRotationZoneRight20Degrees() {
        MotorSettings rightUp = algorithm.calculateMotorSetting(20, 100);
        MotorSettings rightDown = algorithm.calculateMotorSetting(340, 100);

        Assert.assertEquals(rightUp.leftMotorPercentage, rightDown.leftMotorPercentage);
        Assert.assertEquals(rightUp.rightMotorPercentage, rightDown.rightMotorPercentage);
    }

    @Test
    public void testRotationZoneLeft20Degrees() {
        MotorSettings leftUp = algorithm.calculateMotorSetting(160, 100);
        MotorSettings leftDown = algorithm.calculateMotorSetting(200, 100);

        Assert.assertEquals(leftUp.leftMotorPercentage, leftDown.leftMotorPercentage);
        Assert.assertEquals(leftUp.rightMotorPercentage, leftDown.rightMotorPercentage);
    }

    @Test
    public void testSpeedScalingForward() {
        MotorSettings upFast = algorithm.calculateMotorSetting(90, 100);
        MotorSettings upSlow = algorithm.calculateMotorSetting(90, 50);

        Assert.assertTrue(Math.abs(upFast.leftMotorPercentage) + Math.abs(upFast.rightMotorPercentage)
                > Math.abs(upSlow.leftMotorPercentage) + Math.abs(upSlow.rightMotorPercentage));
    }
}
