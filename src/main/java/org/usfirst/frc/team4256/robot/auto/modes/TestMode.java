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
        D_Swerve.getInstance().resetValues();
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(new SeriesAction(
            Arrays.asList(
                new ParallelAction(
                    Arrays.asList(
                        new ReleaseHatchAction(),
                        new WaitAction(0.2)
                    )
                ),
                new DriveForTimeAction(0.0, 0.3, 0.0, 1.0),
                new DriveForTimeAction(90.0, 0.3, 0.0, 1.0),
                new DriveForTimeAction(180.0, 0.3, 0.0, 1.0),
                new DriveForTimeAction(270.0, 0.3, 0.0, 1.0)
            )
        ));
    }

}
