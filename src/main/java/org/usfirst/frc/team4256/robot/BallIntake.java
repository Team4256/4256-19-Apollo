package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Victor;
import edu.wpi.first.wpilibj.DigitalInput;

public final class BallIntake {

    public enum BallIntakeState {
        SLURP, SPIT, STOP
    }
    //CONSTANTS
    private static final double SLURP_SPEED = -0.75;
    private static final double SPIT_SPEED = 0.85;
    private static final double STOP_SPEED = 0.0;

    //INSTANCE
    private final Victor ballMotor;
    private final DigitalInput sensor;
    private BallIntakeState currentBallIntakeState; 

    /**
     * BallIntake consists of a Victor Motor Controller and a Photoeletric Sensor.
     * @param deviceID the CAN ID of the BallIntake's Victor Motor Controller
     * @param sensorID the Digital Input Channel of the BallIntake's Photoeletric Sensor.
     */
    public BallIntake(int deviceID, int sensorID) {
        ballMotor = new Victor(deviceID, ControlMode.PercentOutput);
        sensor = new DigitalInput(sensorID);
        currentBallIntakeState = BallIntakeState.STOP;
    }

    /**
     * <p>"Slurps" a ball up.</p>
     * <p>(Similar to how one would slurp noodles.)</p>
     */
    public void slurp() {
        ballMotor.quickSet(SLURP_SPEED);
    }

    /**
     * <p>"Spits" a ball out.</p>
     */
    public void spit() {
        ballMotor.quickSet(SPIT_SPEED);
    }

    /**
     * <p>Stops the BallIntake's motor</p>
     */
    public void stop() {
        ballMotor.quickSet(STOP_SPEED);
    }

    /**
     * @return
     * <p><code>True</code> if the BallIntake's Photoeletric Sensor detects a ball is present</p>
     * <p><code>False</code> if the BallIntake's Photoeletric Sensor detects a ball is not present</p>
     */
    public boolean hasBall() {
        return sensor.get();
    }

    /**
     * <p>Runs once per loop, checking the <code>currentBallIntakeState</code>
     * and running either <code>SPIT</code>, <code>SLURP</code>, or <code>STOP</code></p>
     * <p>In order to switch the <code>currentBallIntakeState</code>,
     * either <code>setSpit</code> or <code>setSlurp</code> needs to
     * be run prior to running this function.</p>
     * @see #setSpit(boolean)
     * @see #setSlurp(boolean)
     */
    public void completeLoopUpdate() {
        ballMotor.completeLoopUpdate();
    }
    
}