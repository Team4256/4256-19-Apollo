package org.usfirst.frc.team4256.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team4256.robot.auto.AutoModeChooser.DesiredDirection;
import org.usfirst.frc.team4256.robot.auto.actions.*;

public class CenterOnlyTwoHatchCargoShipMode extends AutoMode {

    private final DesiredDirection direction;

    public CenterOnlyTwoHatchCargoShipMode(final DesiredDirection direction) {
        this.direction = direction;
    }

    @Override
    public void done() {
        super.done();
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(new LatchHatchAction());
        switch (direction) {
            case LEFT:
                System.out.println("Running Center Only Two Hatch Cargoship Mode (LEFT)");
                //Drive up and place the first hatch
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.6, 1.2),
                        new WaitAction(0.6),
                        new OrientRobotAction(0.0),
                        new WaitAction(0.2),
                        new DriveWithVisionAction(),
                        new WaitAction(0.2),
                        new ReleaseHatchAction()
                    )
                ));
                //Drive over and pick-up second hatch
                runAction(new SeriesAction(
                    Arrays.asList(
                        new WaitAction(0.2),
                        new DriveForDistanceAction(180.0, 10.0),//calculated
                        new WaitAction(0.2),
                        new DriveForDistanceAction(200.0, 175.12),//calculated
                        new WaitAction(0.2),
                        new OrientRobotAction(180.0),
                        new WaitAction(0.2),
                        new DriveWithVisionAction(),
                        new WaitAction(0.2),
                        new LatchHatchAction(),
                        new DriveForDistanceAction(0.0, 95.28),//calculated
                        new WaitAction(0.2)
                    )
                ));
                //Drive over to the left side of the cargoship and place the hatch
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForDistanceAction(67.86, 280.48),//calculated
                        new WaitAction(0.2),
                        new OrientRobotAction(90.0),
                        new WaitAction(0.2),
                        new DriveWithVisionAction()
                    )
                ));
                break;
            case RIGHT:
                System.out.println("Running Center Only Two Hatch Cargoship Mode (LEFT)");
                //Drive up and place the first hatch
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.6, 1.2),
                        new WaitAction(0.6),
                        new OrientRobotAction(0.0),
                        new WaitAction(0.2),
                        new DriveWithVisionAction(),
                        new WaitAction(0.2),
                        new ReleaseHatchAction()
                    )
                ));
                //Drive over and pick-up second hatch
                runAction(new SeriesAction(
                    Arrays.asList(
                        new WaitAction(0.2),
                        new DriveForDistanceAction(180.0, 10.0),//calculated
                        new WaitAction(0.2),
                        new DriveForDistanceAction(160.0, 175.12),//calculated
                        new WaitAction(0.2),
                        new OrientRobotAction(180.0),
                        new WaitAction(0.2),
                        new DriveWithVisionAction(),
                        new WaitAction(0.2),
                        new LatchHatchAction(),
                        new DriveForDistanceAction(0.0, 95.28),//calculated
                        new WaitAction(0.2)
                    )
                ));
                //Drive over to the right side of the cargoship and place the hatch
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForDistanceAction(202.14, 280.48),//calculated
                        new WaitAction(0.2),
                        new OrientRobotAction(90.0),
                        new WaitAction(0.2),
                        new DriveWithVisionAction()
                    )
                ));
                break;
            default:
                System.out.println("There Was An Error...");
                break;
        }
    }
}
