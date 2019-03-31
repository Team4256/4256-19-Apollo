package org.usfirst.frc.team4256.robot.auto.actions;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.Limelight;
import org.usfirst.frc.team4256.robot.Robot;

import edu.wpi.first.wpilibj.Timer;

public class DriveUntilTargetFoundAction implements Action {

    private static final D_Swerve swerve = D_Swerve.getInstance();
    private static final Limelight limelight = Limelight.getInstance();
    private static final double TIMEOUT_SECONDS = 5.0;
    private final double direction;
    private double startTime;

    public DriveUntilTargetFoundAction(double direction) {
        this.direction = direction;
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
        limelight.turnLEDOn();
    }

    @Override
    public boolean isFinished() {
        if (Timer.getFPGATimestamp() - startTime > TIMEOUT_SECONDS) {
            System.out.println("Drive Until Vision Target Timed Out.");
            return true;
        }
        return limelight.hasTarget();
    }

    @Override
    public void update() {
        limelight.updateVisionTracking();
        swerve.setRobotCentric();
        swerve.travelTowards(direction+Robot.GYRO_OFFSET);
        swerve.setSpeed(0.3);
        swerve.setSpin(0.0);
        swerve.completeLoopUpdate();
    }

    @Override
    public void done() {
        limelight.updateVisionTracking();
        swerve.resetValues();
    }
}
