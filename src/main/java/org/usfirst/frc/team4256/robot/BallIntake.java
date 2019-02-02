package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Victor;
import edu.wpi.first.wpilibj.DigitalInput;

public final class BallIntake {

    private boolean hasBall;

    private final Victor ballMotor;

    public BallIntake(int deviceID) {
        ballMotor = new Victor(deviceID, ControlMode.PercentOutput);
        hasBall = false;
    }

    public void slurp() {
        ballMotor.quickSet(.5);
    }

    public void spit() {
        ballMotor.quickSet(-.5);
    }

    public void stop() {
        ballMotor.quickSet(0);
    }
}