package org.usfirst.frc.team4256.robot.controllers;

import com.cyborgcats.reusable.Xbox;

/**
 * The point of this class is to provide a static way of accessing the gunner controller for use in autonomous (and anywhere else) easily
 */
public class Gunner extends Xbox {

    private static Gunner instance = null;

    private Gunner() {
        super(1);
    }

    /**
     * @return
     * A static <code>Gunner</code> instance
     */
    public static synchronized Gunner getInstance() {
        if (instance == null) {
            instance = new Gunner();
        }

        return instance;
    }

}
