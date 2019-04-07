package org.usfirst.frc.team4256.robot.auto.modes;

import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeEndedException;

public class DriverControlMode extends AutoMode {

    @Override
    protected void routine() throws AutoModeEndedException {
        System.out.println("Running Driver Control");
        stop();
    }

}
