package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Victor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class BallIntake {

    //CONSTANTS
    private static final double SLURP_SPEED = -0.60;
    private static final double SPIT_SPEED = 0.75;
    private static final double STOP_SPEED = 0.0;

    private static BallIntake instance = null;

    //INSTANCE
    private final Victor ballMotor;
    private final DigitalInput sensor;
    private boolean isInitialized = false;

    /**
     * An open loop, motor driven ball intake
     * <p>
     * Consists of a Victor Motor Controller and a Photoeletric Sensor.
     */
    private BallIntake() {
        ballMotor = new Victor(Parameters.BALL_INTAKE_MOTOR_ID, ControlMode.PercentOutput);
        sensor = new DigitalInput(Parameters.BALL_INTAKE_SENSOR);
    }

    public synchronized static BallIntake getInstance() {
        if (instance == null) {
            instance = new BallIntake();
        }
        
        return instance;
    }

    public synchronized void init() {
        ballMotor.init();
        isInitialized = true;
    }

    public synchronized boolean isInitialized() {
        return isInitialized;
    }

    /**
     * "Slurps" a ball up.
     * <p>
     * (Similar to how one would slurp noodles.)
     */
    public synchronized void slurp() {
        ballMotor.quickSet(SLURP_SPEED);
    }

    /**
     * "Spits" a ball out.
     */
    public synchronized void spit() {
        ballMotor.quickSet(SPIT_SPEED);
    }

    /**
     * Stops the <code>BallIntake</code>'s motor.
     */
    public synchronized void stop() {
        ballMotor.quickSet(STOP_SPEED);
    }

    /**
     * @return
     * <code>True</code> if the <code>BallIntake</code>'s Photoelectric Sensor detects a ball is present.
     */
    public synchronized boolean hasBall() {
        return sensor.get();
    }

    /**
     * Outputs relevant information to the SmartDashboard.
     */
    public synchronized void outputToSmartDashboard() {
        SmartDashboard.putBoolean("BallIntake Has Ball", hasBall());
    }
    
}