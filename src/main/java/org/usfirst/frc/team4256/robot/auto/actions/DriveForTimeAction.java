package org.usfirst.frc.team4256.robot.auto.actions;

import org.usfirst.frc.team4256.robot.D_Swerve;

import edu.wpi.first.wpilibj.Timer;

public class DriveForTimeAction implements Action {

    private final D_Swerve swerve = D_Swerve.getInstance();
    private final double direction;
    private final double speed;
    private final double duration;
    private double startTime;

    public DriveForTimeAction(final double direction, final double speed, final double duration) {
        this.direction = direction;
        this.speed = speed;
        this.duration = duration;
    }

    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() - startTime > duration;
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
        swerve.travelTowards(direction);
        swerve.setSpeed(0.0);
        swerve.setSpin(0.0);
        swerve.completeLoopUpdate();
        System.out.println("Drive For Time Action Finished");
        System.out.println("Module A Delta Encoder Counts: " + swerve.getSwerveModules()[0].getTractionMotor().getCounts());
        System.out.println("Module B Delta Encoder Counts: " + swerve.getSwerveModules()[1].getTractionMotor().getCounts());
        System.out.println("Module C Delta Encoder Counts: " + swerve.getSwerveModules()[2].getTractionMotor().getCounts());
        System.out.println("Module D Delta Encoder Counts: " + swerve.getSwerveModules()[3].getTractionMotor().getCounts());
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
        swerve.getSwerveModules()[0].getTractionMotor().setEncPosition(0.0);//FOR TESTING ONLY
        swerve.getSwerveModules()[1].getTractionMotor().setEncPosition(0.0);//FOR TESTING ONLY
        swerve.getSwerveModules()[2].getTractionMotor().setEncPosition(0.0);//FOR TESTING ONLY
        swerve.getSwerveModules()[3].getTractionMotor().setEncPosition(0.0);//FOR TESTING ONLY
        System.out.println("Drive For Time Action Started");
    }

}
