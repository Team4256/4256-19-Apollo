package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Victor;
import edu.wpi.first.wpilibj.DigitalInput;

public final class BallIntake {

    private final Victor ballMotor;
    private final DigitalInput sensor;
    private boolean slurp = false;
    private boolean spit = false;

    public BallIntake(int deviceID, int sensorID) {
        ballMotor = new Victor(deviceID, ControlMode.PercentOutput);
        sensor = new DigitalInput(sensorID);
    }

    public boolean isSlurp() {
        return slurp;
    }

    public void setSlurp(boolean slurp) {
        this.slurp = slurp;
    }

    public boolean isSpit() {
        return spit;
    }

    public void setSpit(boolean spit) {
        this.spit = spit;
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
        return sensor.get();
    }
    
    public void completeLoopUpdate() {
        if (spit) {
            spit();
        }else if (slurp) {
            slurp();
        }else {
            stop();
        }
    }

}