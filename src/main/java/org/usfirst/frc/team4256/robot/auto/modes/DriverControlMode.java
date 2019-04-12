package org.usfirst.frc.team4256.robot.auto.modes;

import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team4256.robot.auto.actions.LatchHatchAction;

public class DriverControlMode extends AutoMode {

    @Override
    protected void routine() throws AutoModeEndedException {
        System.out.println("Running Driver Control");
        runAction(new LatchHatchAction());
        stop();
    }

}
