package org.usfirst.frc.team4256.robot.auto.modes;

import org.usfirst.frc.team4256.robot.Robot;
import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team4256.robot.auto.StartingPosition;
import org.usfirst.frc.team4256.robot.auto.actions.*;

public class TwoHatchFrontCargoShipMode extends AutoMode {

    private final StartingPosition startingPosition;

    public TwoHatchFrontCargoShipMode(final StartingPosition startingPosition) {
        this.startingPosition = startingPosition;
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        switch (startingPosition) {
            case LEFT:
                System.out.println("Running Two Hatch Cargo Ship From Left");
                runAction(new WaitAction(0.2));
                runAction(new DriveForwardUntilVisionTargetIsFoundAction());
                runAction(new WaitAction(0.5));
                runAction(new OrientRobotAction(Robot.GYRO_OFFSET));
                runAction(new WaitAction(0.3));
                runAction(new DriveTowardVisionTargetAction()); 
                runAction(new WaitAction(0.5));
                runAction(new ReleaseHatchAction());
                break;
            case CENTER:
                System.out.println("Running Two Hatch Cargo Ship From Center");
                runAction(new WaitAction(0.2));
                runAction(new DriveForwardUntilVisionTargetIsFoundAction());
                runAction(new WaitAction(0.5));
                runAction(new OrientRobotAction(Robot.GYRO_OFFSET));
                runAction(new WaitAction(0.3));
                runAction(new DriveTowardVisionTargetAction());
                runAction(new WaitAction(0.5));
                runAction(new ReleaseHatchAction());
                break;
            case RIGHT:
                System.out.println("Running Two Hatch Cargo Ship From Right");
                runAction(new WaitAction(0.2));
                runAction(new DriveForwardUntilVisionTargetIsFoundAction());
                runAction(new WaitAction(0.5));
                runAction(new OrientRobotAction(Robot.GYRO_OFFSET));
                runAction(new WaitAction(0.3));
                runAction(new DriveTowardVisionTargetAction());
                runAction(new WaitAction(0.5));
                runAction(new ReleaseHatchAction());
                break;
            default:
                System.out.println("There Was An Error...");
                System.out.println("But Ian Has Your Back And Is Running Front Cargo Ship Mode From The Left");
                runAction(new WaitAction(0.2));
                runAction(new DriveForwardUntilVisionTargetIsFoundAction());
                runAction(new WaitAction(0.5));
                runAction(new OrientRobotAction(Robot.GYRO_OFFSET));
                runAction(new WaitAction(0.3));
                runAction(new DriveTowardVisionTargetAction());
                runAction(new WaitAction(0.5));
                runAction(new ReleaseHatchAction());
                break;
        }
    }
}
