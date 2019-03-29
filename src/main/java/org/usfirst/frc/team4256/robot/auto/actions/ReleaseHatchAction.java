package org.usfirst.frc.team4256.robot.auto.actions;

import org.usfirst.frc.team4256.robot.HatchIntake;

public class ReleaseHatchAction implements Action {

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void update() {

    }

    @Override
    public void done() {

    }

    @Override
    public void start() {
        HatchIntake.getInstance().release();
    }
}
