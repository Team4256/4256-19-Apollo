package org.usfirst.frc.team4256.robot.auto.actions;

import org.usfirst.frc.team4256.robot.controllers.Driver;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class WaitForDriverButtonPressAction implements Action {

    private static Driver driver = Driver.getInstance();
    private static final double DEFAULT_TIMEOUT = 3.0;
    private final int button;
    private final double timeout;
    private double startTime;

    public WaitForDriverButtonPressAction(int button, double timeout) {
        this.button = button;
        this.timeout = timeout;
    }

    public WaitForDriverButtonPressAction(int button) {
        this(button, DEFAULT_TIMEOUT);
    }

    @Override
    public boolean isFinished() {
        if ((Timer.getFPGATimestamp() - startTime) > timeout) {
            System.out.println("Wait For Driver Button Press Action Timed Out");
            return true;
        }
        return driver.getRawButtonPressed(button);
    }

    @Override
    public void update() {

    }

    @Override
    public void done() {
        driver.setRumble(RumbleType.kRightRumble, 0.0);
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
        driver.setRumble(RumbleType.kRightRumble, 0.6);
    }

}
