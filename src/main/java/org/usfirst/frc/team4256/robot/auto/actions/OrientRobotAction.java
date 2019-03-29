package org.usfirst.frc.team4256.robot.auto.actions;

import com.cyborgcats.reusable.PID;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.Robot;

import edu.wpi.first.wpilibj.Timer;

public class OrientRobotAction implements Action {

    private static final double TIMEOUT_SECONDS = 5.0;
    private static final double ANGLE_THRESHOLD = 4.0;
    private static final D_Swerve swerve = D_Swerve.getInstance();
    private final double orientation;
    private double startTime;

    public OrientRobotAction(final double orientation) {
        this.orientation = orientation;
    }
    
    @Override
    public boolean isFinished() {
        if (Timer.getFPGATimestamp() - startTime > TIMEOUT_SECONDS) {
            System.out.println("Orient Robot Action Has Timed Out.");
            return true;
        }

        return Math.abs(Robot.gyroHeading - orientation) < ANGLE_THRESHOLD;
    }

    @Override
    public void update() {
        swerve.setFieldCentric();
        swerve.travelTowards(0.0);
        swerve.setSpeed(0.0);
        swerve.face(orientation, 0.3);
        swerve.completeLoopUpdate();
    }

    @Override
    public void done() {
        PID.clear("spin");
        swerve.travelTowards(0.0);
        swerve.setSpeed(0.0);
        swerve.setSpin(0.0);
        swerve.completeLoopUpdate();
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
        PID.clear("spin");
    }

    
}
