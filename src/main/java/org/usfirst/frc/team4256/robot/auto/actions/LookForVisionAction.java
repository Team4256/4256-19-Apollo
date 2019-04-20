package org.usfirst.frc.team4256.robot.auto.actions;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.Limelight;

import edu.wpi.first.wpilibj.Timer;

public class LookForVisionAction implements Action {

    private static final D_Swerve swerve = D_Swerve.getInstance();
    private static final Limelight limelight = Limelight.getInstance();
    private final double direction;
    private final double speed;
    private final double timeout;
    private double startTime;
    private double count = 0;

    public LookForVisionAction(final double direction, final double speed, final double timeout) {
        this.direction = direction;
        this.speed = speed;
        this.timeout = timeout;
    }

    @Override
    public boolean isFinished() {
        if (Timer.getFPGATimestamp() - startTime > timeout) {
            System.out.println("Look For Vision Action Has Timed Out");
            return true;
        }

        count = limelight.hasTarget() ? count++ : 0;
        
        return count > 2;
    }

    @Override
    public void update() {
        swerve.setFieldCentric();
        swerve.travelTowards(direction);
        swerve.setSpeed(speed);
        swerve.setSpin(0.0);
        swerve.completeLoopUpdate();
    }

    @Override
    public void done() {
        swerve.setFieldCentric();
        swerve.travelTowards(direction);
        swerve.setSpeed(0.0);
        swerve.setSpin(0.0);
        swerve.completeLoopUpdate();
//        System.out.println("Look For Vision Action Finished");
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
//        System.out.println("Look For Vision Action Started");
    }


}
