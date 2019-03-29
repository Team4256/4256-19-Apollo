package org.usfirst.frc.team4256.robot.auto.modes;

import org.usfirst.frc.team4256.robot.Robot;
import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team4256.robot.auto.actions.*;

public class FrontCargoShipSingleMode extends AutoMode {

    @Override
    protected void routine() throws AutoModeEndedException {
        System.out.println("Running Front Cargo Ship Single Mode");
        runAction(new WaitAction(1.0));
        runAction(new DriveForwardUntilVisionTargetIsFoundAction());
        runAction(new WaitAction(1.0));
        runAction(new OrientRobotAction(Robot.GYRO_OFFSET));
        runAction(new WaitAction(1.0));
        runAction(new DriveTowardVisionTargetAction());
    }
}
