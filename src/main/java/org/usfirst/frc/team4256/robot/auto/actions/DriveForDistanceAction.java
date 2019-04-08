package org.usfirst.frc.team4256.robot.auto.actions;

import org.usfirst.frc.team4256.robot.D_Swerve;

import edu.wpi.first.wpilibj.Timer;

public class DriveForDistanceAction implements Action {

    private static final D_Swerve swerve = D_Swerve.getInstance();
    private static final double TIMEOUT = 5.0;//seconds
    private final double distance;//inches
    private double startTime;

    public DriveForDistanceAction(final double distance) {
        this.distance = distance;
    }

    @Override
    public boolean isFinished() {
        if (Timer.getFPGATimestamp() - startTime > TIMEOUT) {
            System.out.println("Drive For Distance Action Timed Out");
            return true;
        }
        return false;
    }

    @Override
    public void update() {
        
    }

    @Override
    public void done() {

    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
    }

}
