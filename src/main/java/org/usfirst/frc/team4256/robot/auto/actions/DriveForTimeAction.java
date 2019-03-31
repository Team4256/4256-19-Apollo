package org.usfirst.frc.team4256.robot.auto.actions;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.Robot;

import edu.wpi.first.wpilibj.Timer;

public class DriveForTimeAction implements Action {

    private static final D_Swerve swerve = D_Swerve.getInstance();
    private final double direction;
    private final double speed;
    private final double duration;
    private double startTime;

    public DriveForTimeAction(double direction, double speed, double duration) {
        this.direction = direction;
        this.speed = speed;
        this.duration = duration;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void update() {
        swerve.setFieldCentric();
        swerve.travelTowards(direction+Robot.GYRO_OFFSET);
        swerve.setSpeed(speed);
        swerve.setSpin(0.0);
        swerve.completeLoopUpdate();
    }

    @Override
    public void done() {
        swerve.resetValues();
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
    }
}
