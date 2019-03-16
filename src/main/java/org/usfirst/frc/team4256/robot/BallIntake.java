package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Victor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class BallIntake {

    //CONSTANTS
    private static final double SLURP_SPEED = -0.50;
    private static final double SPIT_SPEED = 0.75;
    private static final double STOP_SPEED = 0.0;

    //INSTANCE
    private final Victor ballMotor;
    private final DigitalInput sensor;

    /**
     * An open loop, motor driven ball intake
     * <p>
     * Consists of a Victor Motor Controller and a Photoeletric Sensor.
     * @param deviceID the CAN ID of the BallIntake's Victor Motor Controller
     * @param sensorID the Digital Input Channel of the BallIntake's Photoeletric Sensor.
     */
    public BallIntake(int deviceID, int sensorID) {
        ballMotor = new Victor(deviceID, ControlMode.PercentOutput);
        sensor = new DigitalInput(sensorID);
    }

    /**
     * "Slurps" a ball up.
     * <p>
     * (Similar to how one would slurp noodles.)
     */
    public void slurp() {
        ballMotor.quickSet(SLURP_SPEED);
    }

    /**
     * "Spits" a ball out.
     */
    public void spit() {
        ballMotor.quickSet(SPIT_SPEED);
    }

    /**
     * Stops the <code>BallIntake</code>'s motor.
     */
    public void stop() {
        ballMotor.quickSet(STOP_SPEED);
    }

    /**
     * @return
     * <code>True</code> if the <code>BallIntake</code>'s Photoeletric Sensor detects a ball is present.
     * <p>
     * <code>False</code> if the <code>BallIntake</code>'s Photoeletric Sensor detects a ball is not present.
     */
    public boolean hasBall() {
        return sensor.get();
    }

    public void outputToSmartDashboard() {
        SmartDashboard.putBoolean("BallIntake Has Ball", hasBall());
    }

    public void completeLoopUpdate() {
        ballMotor.completeLoopUpdate();
    }
    
}