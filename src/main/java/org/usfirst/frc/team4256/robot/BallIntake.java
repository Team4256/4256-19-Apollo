package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Victor;
import edu.wpi.first.wpilibj.DigitalInput;

public final class BallIntake {

    public enum BallIntakeState {
        SLURP, SPIT, STOP
    }
    //CONSTANTS
    private static final double SLURP_SPEED = 1.0;
    private static final double SPIT_SPEED = -1.0;
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
     * @return
     * <p><code>True</code> if <code>currentBallIntakeState</code> is <code>SLURP</code>.</p>
     * <p><code>False</code> if <code>currentBallIntakeState</code> is not <code>SLURP</code>.</p>
     */
    public boolean isSlurp() {
        return currentBallIntakeState.equals(BallIntakeState.SLURP);
    }

    /**
     * @return
     * <p><code>True</code> if <code>currentBallIntakeState</code> is <code>SPIT</code>.</p>
     * <p><code>False</code> if <code>currentBallIntakeState</code> is not <code>SPIT</code>.</p>
     */
    public boolean isSpit() {
        return currentBallIntakeState.equals(BallIntakeState.SPIT);
    }

    /**
     * 
     * @param enableSlurp 
     * <p><code>True</code> corresponds with <code>currentBallIntakeState</code> 
     * being set to <code>SLURP</code>.</p>
     * <p><code>False</code> corresponds with <code>currentBallIntakeState</code>
     * being set to <code>STOP</code>.</p>
     */
    public void setSlurp(boolean enableSlurp) {
        currentBallIntakeState = enableSlurp ? BallIntakeState.SLURP : BallIntakeState.STOP;
    }

    /**
     * @param enableSpit 
     * <p><code>True</code> corresponds with <code>currentBallIntakeState</code> 
     * being set to <code>SPIT</code>.
     * <p><code>False</code> corresponds with <code>currentBallIntakeState</code>
     * being set to <code>STOP</code>.</p>
     */
    public void setSpit(boolean enableSpit) {
        currentBallIntakeState = enableSpit ? BallIntakeState.SPIT : BallIntakeState.STOP;
    }

    /**
     * <p>"Slurps" a ball up.</p>
     * <p>(Similar to how one would slurp noodles.)</p>
     */
    private void slurp() {
        ballMotor.quickSet(SLURP_SPEED);
    }

    /**
     * <p>"Spits" a ball out.</p>
     */
    private void spit() {
        ballMotor.quickSet(SPIT_SPEED);
    }

    /**
     * <p>Stops the BallIntake's motor</p>
     */
    private void stop() {
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
        if (currentBallIntakeState.equals(BallIntakeState.SPIT)) {
            spit();
        }else if (currentBallIntakeState.equals(BallIntakeState.SLURP)) {
            slurp();
        }else {
            stop();
        }
    }

}