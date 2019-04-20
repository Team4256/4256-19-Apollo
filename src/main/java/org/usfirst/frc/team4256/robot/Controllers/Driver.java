package org.usfirst.frc.team4256.robot.controllers;

import com.cyborgcats.reusable.Xbox;

/**
 * The point of this class is to provide a static way of accessing the driver controller for use in autonomous (and anywhere else) easily
 */
public class Driver extends Xbox {

    private static Driver instance = null;

    private Driver() {
        super(0);
    }

    /**
     * @return
     * A static <code>Driver</code> instance
     */
    public static synchronized Driver getInstance() {
        if (instance == null) {
            instance = new Driver();
        }

        return instance;
    }
}
