package org.usfirst.frc.team4256.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;

public class WaitAction implements Action {

    private final double waitTime;
    private double startTime;

    public WaitAction(double waitTime) {
        this.waitTime = waitTime;
    }

    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() - startTime > waitTime;
    }

    @Override
    public void update() {

    }

    @Override
    public void done() {
        System.out.println("Wait Action Finished");
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
        System.out.println("Wait Action Started");
    }

}
