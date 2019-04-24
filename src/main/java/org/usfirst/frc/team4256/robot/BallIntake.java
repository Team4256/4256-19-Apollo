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
    private BallIntakeState currentBallIntakeState = BallIntakeState.STOP;
    private boolean isInitialized = false;
    private boolean hadBall = false;

    public enum BallIntakeState {
        SLURP,
        SPIT,
        STOP;
    }

    /**
     * An open loop, motor driven ball intake
     * <p>
     * Consists of a Victor Motor Controller and a Photoeletric Sensor.
     */
    private BallIntake() {
        ballMotor = new Victor(Parameters.BALL_INTAKE_MOTOR_ID, ControlMode.PercentOutput);
        sensor = new DigitalInput(Parameters.BALL_INTAKE_SENSOR);
    }

    /**
     * @return
     * A static <code>BallIntake</code> instance
     */
    public static BallIntake getInstance() {
        if (instance == null) {
            instance = new BallIntake();
        }
        
        return instance;
    }

    /**
     * Performs neccessary initialization for the <code>BallIntake</code> which is meant to be run in <code>RobotInit</code>
     */
    public void init() {
        ballMotor.init();
        isInitialized = true;
    }

    /**
     * @return
     * <b>True</b> if initialization has previously occured
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * "Slurps" a ball up.
     * <p>
     * (Similar to how one would slurp noodles.)
     */
    public void slurp() {
        ballMotor.quickSet(SLURP_SPEED);
        currentBallIntakeState = BallIntakeState.SLURP;
    }

    /**
     * "Spits" a ball out.
     */
    public void spit() {
        ballMotor.quickSet(SPIT_SPEED);
        currentBallIntakeState = BallIntakeState.SPIT;
    }

    /**
     * Stops the <code>BallIntake</code>'s motor.
     */
    public void stop() {
        ballMotor.quickSet(STOP_SPEED);
        currentBallIntakeState = BallIntakeState.STOP;
    }

    /**
     * A way of keeping track of what the <code>BallIntake</code> is currently doing
     * @return
     * The current <code>BallIntakeState</code> of the <code>BallIntake</code>
     */
    public BallIntakeState getCurrentBallIntakeState() {
        return currentBallIntakeState;
    }

    /**
     * @return
     * <code>True</code> if the <code>BallIntake</code>'s Photoelectric Sensor detects a ball is present.
     */
    public boolean hasBall() {
        return sensor.get();
    }

    /**
     * A more accurate method of keeping track of if the intake contains a ball
     * @return
     * <b>True</b> if a ball is expected to be in the intake
     */
    public boolean hasBallFiltered() {
        boolean hasBall = hasBall();
        boolean shouldHaveBall = (hasBall && hadBall);
        hadBall = hasBall;
        return shouldHaveBall;
    }

    /**
     * Outputs relevant information to the SmartDashboard.
     */
    public void outputToSmartDashboard() {
        SmartDashboard.putBoolean("BallIntake Has Ball", hasBall());
    }
    
}