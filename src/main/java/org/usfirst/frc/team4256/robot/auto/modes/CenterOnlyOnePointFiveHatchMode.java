/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team4256.robot.auto.actions.*;

public class CenterOnlyOnePointFiveHatchMode extends AutoMode {

    @Override
    public void done() {
        super.done();
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(new LatchHatchAction());
        runAction(new SeriesAction(
            Arrays.asList(
                new DriveForTimeAction(0.0, 0.6, 1.2),
                new WaitAction(0.6),
                new OrientRobotAction(0.0),
                new WaitAction(0.2),
                new DriveWithVisionAction(),
                new WaitAction(0.2),
                new ReleaseHatchAction(),
                new WaitAction(0.2),
                new DriveForTimeAction(180.0, 0.4, 0.7),
                new WaitAction(0.2),
                new OrientRobotAction(180.0),
                new WaitAction(0.2),
                new DriveForTimeAction(90.0, 0.7, 1.2)
            )
        ));
    }

}
