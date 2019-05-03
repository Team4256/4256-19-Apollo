package org.usfirst.frc.team4256.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team4256.robot.auto.AutoModeChooser.StartingPosition;
import org.usfirst.frc.team4256.robot.auto.actions.*;

public class TwoHatchFrontCargoShipMode extends AutoMode {

    private final StartingPosition startingPosition;

    public TwoHatchFrontCargoShipMode(final StartingPosition startingPosition) {
        this.startingPosition = startingPosition;
    }

    @Override
    public void done() {
        super.done();
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(new LatchHatchAction());
        switch (startingPosition) {
            case LEFT:
                System.out.println("Running Two Hatch Cargo Ship From Left");
                runAction(new SeriesAction(//TODO READY FOR TESTING
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.6, 1.0),
                        new WaitAction(0.6),
                        new DriveForTimeAction(90.0, 0.6, 0.8),
                        new WaitAction(0.2),
                        new OrientRobotAction(0.0),
                        new WaitAction(0.2),
                        new DriveWithVisionAction()
//                        new WaitAction(0.2),
//                        new ReleaseHatchAction(),
//                        new WaitAction(0.2),
//                        new DriveForTimeAction(180.0, 0.4, 0.7)
                        /*
                        new DriveForTimeAction(0.0, 0.3, 2.0),
                        new WaitAction(0.4),
                        new DriveForTimeAction(90.0, 0.4, 0.7),
                        new WaitAction(0.4),
                        new OrientRobotAction(0.0),
                        new WaitAction(0.2),
                        new DriveTowardVisionTargetAction(),
                        new WaitAction(0.2),
                        new ReleaseHatchAction(),
                        new WaitAction(0.2),
                        new DriveForTimeAction(180.0, 0.4, 0.7),
                        new WaitAction(0.2),
                        new DriveForTimeAction(270.0, 0.5, 1.8),
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
                break;
            case CENTER:
                System.out.println("Running Two Hatch Cargo Ship From Center");
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
                        new DriveForDistanceAction(40.46, 164.24),//calculated
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
                System.out.println("Running Two Hatch Cargo Ship From Right");
                runAction(new SeriesAction(//TODO READY FOR TESTING
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.6, 1.0),
                        new WaitAction(0.6),
                        new DriveForTimeAction(270.0, 0.6, 0.8),
                        new WaitAction(0.2),
                        new OrientRobotAction(0.0),
                        new WaitAction(0.2),
                        new DriveWithVisionAction()
                        //Idk what this is
                        /*
                        new DriveForTimeAction(0.0, 0.3, 2.0),
                        new WaitAction(0.4),
                        new DriveForTimeAction(270.0, 0.6, 0.5),
                        new OrientRobotAction(0.0),
                        new DriveWithVisionAction(),
                        new WaitAction(0.2),
                        new ReleaseHatchAction(),
                        new WaitAction(0.2),
                        new DriveForTimeAction(180.0, 0.4, 0.7),
                        new OrientRobotAction(180.0),
                        new DriveForTimeAction(90.0, 0.5, 1.8),
                        new WaitAction(0.0),
                        new DriveForTimeAction(180.0, 0.5, 1.7),
                        new DriveWithVisionAction(),
                        new WaitAction(0.2),
                        new LatchHatchAction(),
                        new WaitAction(0.2),
                        new DriveForTimeAction(0.0, 0.5, 1.5)
                        //new DriveForTimeAction(270.0, 0.5, 2.4)
                        */
                        //1.5 working
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
                break;
            default:
                System.out.println("There Was An Error...");
                System.out.println("But Ian Has Your Back And Is Running Front Cargo Ship Mode From The Left Two");
                runAction(new SeriesAction(//TODO READY FOR TESTING
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.3, 2.0),
                        new WaitAction(0.4),
                        new DriveForTimeAction(90.0, 0.4, 0.7),
                        new WaitAction(0.4),
                        new OrientRobotAction(0.0),
                        new WaitAction(0.2),
                        new DriveWithVisionAction(),
                        new WaitAction(0.2),
                        new ReleaseHatchAction(),
                        new WaitAction(0.2),
                        new DriveForTimeAction(180.0, 0.4, 0.7),
                        new WaitAction(0.2),
                        new DriveForTimeAction(270.0, 0.5, 1.8),
                        new WaitAction(0.2),
                        new DriveForTimeAction(180.0, 0.5, 1.8),
                        new OrientRobotAction(180.0),
                        new WaitAction(0.3),
                        new DriveWithVisionAction(),
                        new WaitAction(0.2),
                        new LatchHatchAction(),
                        new DriveForTimeAction(0.0, 0.5, 0.5)
                    )
                ));
                break;
        }
    }
}
