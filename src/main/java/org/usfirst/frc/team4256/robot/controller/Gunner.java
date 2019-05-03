package org.usfirst.frc.team4256.robot.controller;

import com.cyborgcats.reusable.Xbox;

public class Gunner extends Xbox {

    private static Gunner instance = null;

    private Gunner() {
        super(1);
    }

    public static synchronized Gunner getInstance() {
        if (instance == null) {
            instance = new Gunner();
        }

        return instance;
    }

}
