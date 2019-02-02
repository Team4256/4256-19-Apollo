package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Victor;
import edu.wpi.first.wpilibj.DigitalInput;

public final class BallIntake {

    private boolean hasBall;

    private final Victor ballMotor;
    private final DigitalInput sensor;

    public BallIntake(int deviceID, int sensorID) {
        ballMotor = new Victor(deviceID, ControlMode.PercentOutput);
        sensor = new DigitalInput(sensorID);
        hasBall = false;
    }

    public void slurp() {
        ballMotor.quickSet(0.5);
    }

    public void spit() {
        ballMotor.quickSet(-0.5);
    }

    public void stop() {
        ballMotor.quickSet(0.0);
    }

    public boolean hasBall() {
        hasBall = sensor.get();
        return hasBall;
    }
    
}