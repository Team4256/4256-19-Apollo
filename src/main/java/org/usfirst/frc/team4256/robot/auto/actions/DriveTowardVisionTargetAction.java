package org.usfirst.frc.team4256.robot.auto.actions;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.Limelight;

import edu.wpi.first.wpilibj.Timer;

public class DriveTowardVisionTargetAction implements Action {

    private static final D_Swerve swerve = D_Swerve.getInstance();
    private static final Limelight limelight = Limelight.getInstance();
    private static final double TIMEOUT_SECONDS = 5.0;
    private double startTime;

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
        limelight.turnLEDOn();
    }

    @Override
    public boolean isFinished() {
        if (Timer.getFPGATimestamp() - startTime > TIMEOUT_SECONDS) {
            System.out.println("Drive Toward Vision Target Timed Out");
            return true;
        }
        return !limelight.hasTarget();
    }

    @Override
    public void update() {
        limelight.updateVisionTracking3();
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
    }
}
