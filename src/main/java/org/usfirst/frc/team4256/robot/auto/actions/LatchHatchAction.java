package org.usfirst.frc.team4256.robot.auto.actions;

import org.usfirst.frc.team4256.robot.HatchIntake;

public class LatchHatchAction implements Action {

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
        System.out.println("Latching Onto Hatch");
        HatchIntake.getInstance().latch();
    }
}
