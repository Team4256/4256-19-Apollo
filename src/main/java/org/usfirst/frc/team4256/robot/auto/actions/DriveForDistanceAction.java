package org.usfirst.frc.team4256.robot.auto.actions;

import org.usfirst.frc.team4256.robot.D_Swerve;

import edu.wpi.first.wpilibj.Timer;

public class DriveForDistanceAction implements Action {

    private static final D_Swerve swerve = D_Swerve.getInstance();
    private static final double TIMEOUT = 5.0;//seconds
    private static final double DISTANCE_THRESHOLD = 0.5;
    private final double angle;//degrees
    private final double distance;//inches
    private double startTime;

    public DriveForDistanceAction(final double angle, final double distance) {
        this.angle = angle;
        this.distance = distance;
    }

    @Override
    public boolean isFinished() {
        if (Timer.getFPGATimestamp() - startTime > TIMEOUT) {
            System.out.println("Drive For Distance Action Timed Out");
            return true;
        }
        System.out.println(distance - swerve.getAverageInches());
        return Math.abs(distance - swerve.getAverageInches()) < DISTANCE_THRESHOLD;
    }

    @Override
    public void update() {
        if (swerve.setAngles(angle)) {
            swerve.setInches(distance);
        }
    }

    @Override
    public void done() {
        swerve.stop();
        swerve.resetEncoders();
        swerve.disableBrakeMode();
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
        swerve.resetEncoders();
        swerve.enableBrakeMode();
    }

}
