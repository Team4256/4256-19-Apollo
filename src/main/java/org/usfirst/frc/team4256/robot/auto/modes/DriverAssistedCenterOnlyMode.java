package org.usfirst.frc.team4256.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team4256.robot.auto.actions.*;
import org.usfirst.frc.team4256.robot.controllers.Driver;

public class DriverAssistedCenterOnlyMode extends AutoMode {

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
                new WaitForDriverButtonPressAction(Driver.BUTTON_RB),
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
