package org.usfirst.frc.team4256.robot.controller;

import com.cyborgcats.reusable.Xbox;

public class Driver extends Xbox {

    private static Driver instance = null;

    private Driver() {
        super(0);
    }

    public static synchronized Driver getInstance() {
        if (instance == null) {
            instance = new Driver();
        }

        return instance;
    }
}
