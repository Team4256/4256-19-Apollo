package org.usfirst.frc.team4256.robot.auto.actions;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.Limelight;

import edu.wpi.first.wpilibj.Timer;

public class DriveWithVisionAction implements Action {

    private static final D_Swerve swerve = D_Swerve.getInstance();
    private static final Limelight limelight = Limelight.getInstance();
    private static final double TIMEOUT_SECONDS = 3.0;
    private double startTime;
    private int count = 0;

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
        limelight.turnLEDOn();
        System.out.println("Drive With Vision Action Starting");
    }

    @Override
    public boolean isFinished() {
        if (Timer.getFPGATimestamp() - startTime > TIMEOUT_SECONDS) {
            System.out.println("Drive With Vision Has Timed Out");
            return true;
        }
        if (!limelight.hasTarget()) {
            count++;
        }else {
            count = 0;
        }
        return count > 5;
    }

    @Override
    public void update() {
        limelight.updateVisionTracking();
        swerve.setRobotCentric();
        swerve.travelTowards(limelight.getCommandedDirection());
        swerve.setSpeed(limelight.getCommandedSpeed());
        swerve.setSpin(limelight.getCommandedSpin());
        swerve.completeLoopUpdate();
    }

    @Override
    public void done() {
        swerve.resetValues();
        swerve.completeLoopUpdate();
        System.out.println("Drive With Vision Action Finished");
    }
}
