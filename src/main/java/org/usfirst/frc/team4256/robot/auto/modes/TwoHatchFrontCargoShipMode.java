package org.usfirst.frc.team4256.robot.auto.modes;

import java.util.Arrays;

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
        switch (startingPosition) {//TODO NOT READY
            case LEFT:
                System.out.println("Running Two Hatch Cargo Ship From Left");
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveUntilTargetFoundAction(0.0),
                        new DriveTowardVisionTargetAction(),
                        new WaitAction(0.4),
                        new ParallelAction(
                            Arrays.asList(
                            new DriveForTimeAction(0.0, 0.25, 0.1),
                            new ReleaseHatchAction(),
                            new WaitAction(0.2)            
                            )
                        )
                    )
                ));
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(180.0, 0.35, 0.5),
                        new DriveForTimeAction(270.0, 0.35, 3.0),
                        new OrientRobotAction(180.0),
                        new DriveUntilTargetFoundAction(0.0),//Robot Centric
                        new DriveTowardVisionTargetAction(),
                        new WaitAction(0.4),
                        new ParallelAction(
                            Arrays.asList(
                                new LatchHatchAction(),
                                new WaitAction(0.2)
                            )
                        )
                    )
                ));
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.35, 3.0),
                        new DriveForTimeAction(90.0, 0.35, 3.0),
                        new OrientRobotAction(0.0),
                        new DriveUntilTargetFoundAction(60.0),//Robot Centric
                        new DriveTowardVisionTargetAction(),
                        new WaitAction(0.4),
                        new ParallelAction(
                            Arrays.asList(
                                new DriveForTimeAction(0.0, 0.25, 0.1),
                                new ReleaseHatchAction(),
                                new WaitAction(0.2)
                            )
                        )
                    )
                ));
                break;
            case CENTER:
                System.out.println("Running Two Hatch Cargo Ship From Center");
                runAction(new SeriesAction(//TODO READY FOR TESTING
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.5, 0.3),
                        new DriveForTimeAction(0.90, 0.5, 0.2),
                        new OrientRobotAction(0.0),
                        new DriveUntilTargetFoundAction(0.0),
                        new DriveTowardVisionTargetAction(),
                        new WaitAction(0.4),
                        new ParallelAction(
                            Arrays.asList(
                            new DriveForTimeAction(0.0, 0.25, 0.1),
                            new ReleaseHatchAction(),
                            new WaitAction(0.2)            
                            )
                        )
                    )
                ));
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(180.0, 0.5, 0.5),
                        new DriveForTimeAction(270.0, 0.5, 3.0),
                        new OrientRobotAction(180.0),
                        new DriveUntilTargetFoundAction(0.0),//Robot Centric
                        new DriveTowardVisionTargetAction(),
                        new WaitAction(0.4),
                        new ParallelAction(
                            Arrays.asList(
                                new LatchHatchAction(),
                                new WaitAction(0.2)
                            )
                        )
                    )
                ));
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.5, 3.0),
                        new DriveForTimeAction(90.0, 0.5, 2.0),
                        new OrientRobotAction(0.0),
                        new DriveUntilTargetFoundAction(0.0),//Robot Centric
                        new DriveTowardVisionTargetAction(),
                        new WaitAction(0.4),
                        new ParallelAction(
                            Arrays.asList(
                                new DriveForTimeAction(0.0, 0.25, 0.1),
                                new ReleaseHatchAction(),
                                new WaitAction(0.2)
                            )
                        )
                    )
                ));
                break;
            case RIGHT:
                System.out.println("Running Two Hatch Cargo Ship From Right");
                runAction(new SeriesAction(//TODO NOT READY
                    Arrays.asList(
                        new DriveUntilTargetFoundAction(0.0),
                        new DriveTowardVisionTargetAction(),
                        new WaitAction(0.4),
                        new ParallelAction(
                            Arrays.asList(
                            new DriveForTimeAction(0.0, 0.25, 0.1),
                            new ReleaseHatchAction(),
                            new WaitAction(0.2)            
                            )
                        )
                    )
                ));
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(180.0, 0.35, 0.5),
                        new DriveForTimeAction(90.0, 0.35, 3.0),
                        new OrientRobotAction(180.0),
                        new DriveUntilTargetFoundAction(0.0),//Robot Centric
                        new DriveTowardVisionTargetAction(),
                        new WaitAction(0.4),
                        new ParallelAction(
                            Arrays.asList(
                                new LatchHatchAction(),
                                new WaitAction(0.2)
                            )
                        )
                    )
                ));
                runAction(new SeriesAction(
                    Arrays.asList(
                        new DriveForTimeAction(0.0, 0.35, 3.0),
                        new DriveForTimeAction(270.0, 0.35, 3.0),
                        new OrientRobotAction(0.0),
                        new DriveUntilTargetFoundAction(300.0),//Robot Centric
                        new DriveTowardVisionTargetAction(),
                        new WaitAction(0.4),
                        new ParallelAction(
                            Arrays.asList(
                                new DriveForTimeAction(0.0, 0.25, 0.1),
                                new ReleaseHatchAction(),
                                new WaitAction(0.2)
                            )
                        )
                    )
                ));
                break;
            default:
                System.out.println("There Was An Error...");
                System.out.println("But Ian Has Your Back And Is Running Front Cargo Ship Mode From The Left Two");
                runAction(new WaitAction(0.2));
                runAction(new DriveUntilTargetFoundAction(0.0));
                runAction(new WaitAction(0.2));
                runAction(new OrientRobotAction(0.0));
                runAction(new WaitAction(0.2));
                runAction(new DriveTowardVisionTargetAction()); 
                runAction(new WaitAction(0.4));
                runAction(new ParallelAction(
                    Arrays.asList(
                        new ReleaseHatchAction(),
                        new WaitAction(0.2)            
                    )
                ));
                break;
        }
    }
}
