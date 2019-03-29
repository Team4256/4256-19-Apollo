package org.usfirst.frc.team4256.robot.auto;

import org.usfirst.frc.team4256.robot.auto.actions.Action;

import edu.wpi.first.wpilibj.DriverStation;

public abstract class AutoMode {
    protected double updateRate = 1.0/50.0;//50hz
    protected boolean isActive = false;

    protected abstract void routine() throws AutoModeEndedException;

    public void run() {
        isActive = true;

        try {
            routine();
        } catch (AutoModeEndedException e) {
            DriverStation.reportError("Auto Ended Early!", false);
            return;
        }

        done();
    }

    public void done() {
        System.out.println("Auto is complete");
    }

    public void stop() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isActiveWithThrow() throws AutoModeEndedException {
        if (!isActive) {
            throw new AutoModeEndedException();
        }

        return isActive;
    }

    public void runAction(Action action) throws AutoModeEndedException {
        isActiveWithThrow();
        action.start();

        while (isActiveWithThrow() && !action.isFinished()) {
            action.update();
            long sleepTime = (long) (updateRate * 1000.0);//seconds to ms

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        action.done();
    }
}
