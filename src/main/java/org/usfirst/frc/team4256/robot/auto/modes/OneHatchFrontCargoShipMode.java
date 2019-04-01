package org.usfirst.frc.team4256.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.Robot;
import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team4256.robot.auto.StartingPosition;
import org.usfirst.frc.team4256.robot.auto.actions.*;

public class OneHatchFrontCargoShipMode extends AutoMode {

    private final StartingPosition startingPosition;

    public OneHatchFrontCargoShipMode(final StartingPosition startingPosition) {
        this.startingPosition = startingPosition;
    }

    @Override
    public void done() {
        super.done();
        D_Swerve.getInstance().resetValues();
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        switch (startingPosition) {
            case LEFT:
                System.out.println("Running One Hatch Cargo Ship From Left");
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.5, 0.0, 0.3),//TODO fimetune
                        new DriveForTimeAction(90.0, 0.5, 0.0, 0.3),//TODO finetune
                        new OrientRobotAction(0.0),
                        new DriveUntilTargetFoundAction(0.0),
                        new DriveTowardVisionTargetAction(),
                        new DriveForTimeAction(0.0, 0.3, 0.0, 0.1)//Ensures it's on there good
                    )
                ));
                break;
            case CENTER:
                System.out.println("Running One Hatch Cargo Ship From Center");
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.5, 0.0, 0.3),//TODO fimetune
                        new DriveForTimeAction(270.0, 0.5, 0.0, 0.2),//TODO finetune
                        new OrientRobotAction(0.0),
                        new DriveUntilTargetFoundAction(0.0),
                        new DriveTowardVisionTargetAction(),
                        new DriveForTimeAction(0.0, 0.3, 0.0, 0.1)//Ensures it's on there good
                    )
                ));
                break;
            case RIGHT:
                System.out.println("Running One Hatch Cargo Ship From Right");
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.5, 0.0, 0.3),//TODO fimetune
                        new DriveForTimeAction(270.0, 0.5, 0.0, 0.3),//TODO finetune
                        new OrientRobotAction(0.0),
                        new DriveUntilTargetFoundAction(0.0),
                        new DriveTowardVisionTargetAction(),
                        new DriveForTimeAction(0.0, 0.3, 0.0, 0.1)//Ensures it's on there good
                    )
                ));
                break;
            default:
                System.out.println("There Was An Error...");
                System.out.println("But Ian Has Your Back And Is Running Front Cargo Ship Mode From The Left");
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.5, 0.0, 0.3),//TODO fimetune
                        new DriveForTimeAction(90.0, 0.5, 0.0, 0.3),//TODO finetune
                        new OrientRobotAction(0.0),
                        new DriveUntilTargetFoundAction(0.0),
                        new DriveTowardVisionTargetAction(),
                        new DriveForTimeAction(0.0, 0.3, 0.0, 0.1)//Ensures it's on there good
                    )
                ));
                break;
        }
    }
}
