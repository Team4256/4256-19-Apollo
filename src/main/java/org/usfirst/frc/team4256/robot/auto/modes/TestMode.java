package org.usfirst.frc.team4256.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team4256.robot.auto.actions.*;

public class TestMode extends AutoMode {

    @Override
    public void done() {
        super.done();
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(new SeriesAction(
            Arrays.asList(
                new WaitAction(0.4),
                new DriveTowardVisionTargetAction(),
                new DriveForTimeAction(0.0, 0.3, 0.5),
                new LatchHatchAction(),
                new WaitAction(0.2)

                /*
                new DriveForTimeAction(0.0, 0.3, 2.0),
                new WaitAction(0.4),
                new DriveForTimeAction(270.0, 0.4, 0.7),
                new WaitAction(0.4),
                new OrientRobotAction(0.0),
                new WaitAction(0.2),
                new DriveTowardVisionTargetAction(),
                new WaitAction(0.2),
                new ReleaseHatchAction(),
                new WaitAction(0.2),
                new DriveForTimeAction(180.0, 0.4, 0.7),
                new WaitAction(0.2),
                new DriveForTimeAction(90.0, 0.5, 1.8),
                new WaitAction(0.2),
                new DriveForTimeAction(180.0, 0.5, 1.5),
                new OrientRobotAction(180.0),
                new WaitAction(0.3),
                new DriveTowardVisionTargetAction(),
                new WaitAction(0.2),
                new LatchHatchAction(),
                new DriveForTimeAction(0.0, 0.5, 0.5)
                */
            )
        ));
    }

}
