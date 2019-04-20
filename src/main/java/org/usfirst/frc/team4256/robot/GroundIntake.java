package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Encoder;
import com.cyborgcats.reusable.phoenix.Talon;
import com.cyborgcats.reusable.phoenix.Victor;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class GroundIntake {
    private static final double MINIMUM_ANGLE = 0.0;
    private static final double TRANSFER_ANGLE = 10.0;
    private static final double MAXIMUM_ANGLE = 105.0;
    private static final double MINIMUM_ANGLE_THRESHOLD = 0.5;
    private static final double MAXIMUM_ANGLE_THRESHOLD = 15.0;
    private static final double SLURP_SPEED = 1.0;
    private static final double SPIT_SPEED = -0.5;
    private static final double STOP_SPEED = 0.0;
    private static final double GEAR_RATIO = 2.0;
    
    private static GroundIntake instance;
    private final Talon liftMotor;
    private final Victor intakeMotor;
    private final boolean isLiftMotorFlipped;
    private final boolean isIntakeMotorFlipped;
    private final DigitalInput limitSwitch;
    private double desiredDegrees;
    private boolean wasLimitSwitchPressed = false;
    private boolean isOverride = false;
    private boolean isInitialized = false;

    private GroundIntake() {
        liftMotor = new Talon(Parameters.GROUND_LIFT_ID, GEAR_RATIO, ControlMode.Position, Encoder.CTRE_MAG_ABSOLUTE, Parameters.IS_GROUND_LIFT_SENSOR_FLIPPED);
        intakeMotor = new Victor(Parameters.GROUND_INTAKE_ID, ControlMode.PercentOutput);
        limitSwitch = new DigitalInput(Parameters.LIMIT_SWITCH_GROUND_INTAKE);
        isLiftMotorFlipped = Parameters.IS_GROUND_LIFT_MOTOR_FLIPPED;
        isIntakeMotorFlipped = Parameters.IS_GROUND_INTAKE_MOTOR_FLIPPED;
    }

    /**
     * @return
     * A static <code>GroundIntake</code> instance
     */
    public synchronized static GroundIntake getInstance() {
        if (instance == null) {
            instance = new GroundIntake();
        }

        return instance;
    }

    /**
     * Performs neccessary initialization meant to be done in <code>RobotInit</code>
     */
    public void init() {
        liftMotor.init();
        liftMotor.setInverted(isLiftMotorFlipped);
        liftMotor.config_kP(0, 1.5);
        liftMotor.config_kI(0, 0.0);
        liftMotor.config_kD(0, 0.0);
        liftMotor.configClosedLoopPeakOutput(0, 0.3);
        liftMotor.configContinuousCurrentLimit(40, Talon.TIMEOUT_MS);
	    liftMotor.configPeakCurrentLimit(45, Talon.TIMEOUT_MS);
        liftMotor.configPeakCurrentDuration(250, Talon.TIMEOUT_MS);
        setDisabled();
        resetPosition();
        intakeMotor.init();
        intakeMotor.setInverted(isIntakeMotorFlipped);
        isInitialized = true;
    }

    /**
     * @return
     * <b>True</b> if initialization has previosuly occured
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * <p><h3>Checks the value of the limit switch from the last time the function was called,
     * if it was previously false and is now true, the encoder and position will be reset.</h3></p>
     */
    public void checkLimitSwitchUpdate() {
        boolean isLimitSwitchPressed = isLimitSwitch();
        if (isLimitSwitchPressed) {
            resetPosition();
            if (!wasLimitSwitchPressed) {
                setAngle(MINIMUM_ANGLE);//Technically sets to minimum angle not the position it was reset to
            }   
        }
        wasLimitSwitchPressed = isLimitSwitchPressed;
    }

    /**
     * Disables the <code>liftMotor</code> temporarily to keep PID at bay.
     */
    public void setDisabled() {
        isOverride = false;
        liftMotor.set(ControlMode.Disabled, 0.0);
    }

    /**
     * Sets the <code>liftMotor</code>'s encoder position to zero.
     */
    public void resetPosition() {
        liftMotor.setSelectedSensorPosition(0, 0, Talon.TIMEOUT_MS);
    }

    /**
     * Ensures the <code>requestedAngle</code> is within predefined bounds set by the constants {@link #MINIMUM_ANGLE} and {@link #MAXIMUM_ANGLE}.
     * @return
     * <p><code>True</code> if the <code>requestedAngle</code> is within the predefined bounds.</p>
     * <p>and</p>
     * <p><code>False</code> if the <code>requestedAngle</code> is not within the predefined bounds.</p>
     */
    private boolean validateRequestedAngle(double requestedAngle) {//In degrees
        return ((requestedAngle >= MINIMUM_ANGLE) && (requestedAngle <= MAXIMUM_ANGLE));
    }
    
    /**
     * Checks the the {@link #MINIMUM_ANGLE}, {@link #MAXIMUM_ANGLE_THRESHOLD}, {@link #MINIMUM_ANGLE}, {@link #MINIMUM_ANGLE_THRESHOLD}, {@link #desiredDegrees}, and {@link #getCurrentAngle()} with one another to monitor if the <code>liftMotor</code> should be disabled.
     */
    public boolean checkAngle() {
        if (isOverride) {
            return true;
        }
        if (((getCurrentAngle() < (MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD)) && 
             (desiredDegrees < (MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD))) ||
             ((getCurrentAngle() > (MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD)) &&
             (desiredDegrees > (MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD)))) { 
            setDisabled(); 
            return false;
        }else {
            return true;
        }
    }

    /**
     * Sets the angle of the <code>liftMotor</code> after running {@link #validateRequestedAngle(double)} and {@link #checkAngle()}.
     * @param degrees
     * inteded angle in degrees for the <code>liftMotor</code> to be set to.
     */
    public void setAngle(double degrees) {
        isOverride = false;
        if (validateRequestedAngle(degrees)) {
            desiredDegrees = degrees;
            if (checkAngle()) {
                liftMotor.setDegreesLifter(desiredDegrees);
            }
        }
    }

    /**
     * A method used to zero/home the <code>groundIntake</code>.
     */
    public void setOverrideUp() {
        if (!isLimitSwitch()) {
            isOverride = true;
            liftMotor.set(ControlMode.PercentOutput, -0.3);
        } else {
            isOverride = false;
            setDisabled();
        }        
    }

    /**
     * @return
     * Current angle in degrees of the <code>liftMotor</code>.
     */
    public double getCurrentAngle() {
        return liftMotor.getCurrentAngle(false);
    }

    /**
     * @return
     * <p><code>True</code> if the <code>limitSwitch</code> is activated</p>
     * <p>and</p>
     * <p><code>False</code> if the <code>limitSwitch</code> is not activated</p> 
     */
    public boolean isLimitSwitch() {
        return !limitSwitch.get();
    }

    /**
     * @return
     * <p><code>True</code> if the <code>liftMotor</code> is disabled</p>
     * <p>and</p>
     * <p><code>False</code> if the <code>liftMotor</code> is not disabled</p>
     */
    public boolean isDisabled() {
        return (liftMotor.getControlMode() == ControlMode.Disabled);
    }
    
    /**
     * "Autonomously" transfers a hatch from the <code>groundIntake</code> to the <code>hatchIntake</code>.
     * @param hatchIntake an instance of the hatchIntake class
     * @param intakeLifter an instance of the intakeLifter class
     */
    public void transferHatch(HatchIntake hatchIntake, IntakeLifter intakeLifter) {
        if (!isOverride && !isLimitSwitch()) {//Ensures the ground intake is not stowed and or not in the proccess of stowing
            setAngle(TRANSFER_ANGLE);//Puts the ground intake into transfer position
        }
        if ((Math.abs(getCurrentAngle() - TRANSFER_ANGLE) < 2.5) &&//Ensures the ground intake is within a threshold of the transfer position 
            (intakeLifter.getDesiredDegrees() <= 14.0)) {//Checks to make sure the intake lifter is in the down position to recieve the hatch.
            hatchIntake.latch();//Latches onto the hatch
            spit();//Spits the hatch out of the ground intake
            intakeLifter.setAngle(15.0);//Moves the intake lifter (and hopefully the hatch too) up and out of the way of the ground intake so it can stow itself
        } else if ((Math.abs(getCurrentAngle() - TRANSFER_ANGLE) < 2.5) &&//Ensures the ground intake is at transfer angle
                   (Math.abs(intakeLifter.getCurrentAngle() - 15.0) < 2.5) &&//Ensures the intake lifter is within a threshold of being out of the way
                   (intakeLifter.getDesiredDegrees() > 14.0)) {//Ensures the intake lifter is being set to move out of the way
            stop();//Stops spitting the hatch (which should hopefully no longer be in the ground intake)
            setOverrideUp();//Stows the ground intake
        } else if (isLimitSwitch()) {//If the ground intake detects that it is stowed
            intakeLifter.setAngle(0.0);//The ground take is not set to stay there (more of a safety feature and may very well not be neccessary)
        }
    }

    /**
     * @return Desired angle in degrees of the <code>liftMotor</code>.
     */
    public double getDesiredDegrees() {
        return desiredDegrees;
    }

    public Talon getLiftMotor() {
        return liftMotor;
    }

    /**
     * "Slurps" a hatch up off of the ground.
     * <p>
     * (Similar to how one would slurp noodles.)
     */
    public void slurp() {
        intakeMotor.quickSet(SLURP_SPEED);
    }

    /**
     * "Spits" a hatch out.
     */
    public void spit() {
        intakeMotor.quickSet(SPIT_SPEED);
    }

    /**
     * Stops the <code>intakeMotor</code>.
     */
    public void stop() {
        intakeMotor.quickSet(STOP_SPEED);
    }

    /**
     * @return
     * The instance of the <code>intakeMotor</code>
     */
    public Victor getIntakeMotor() {
        return intakeMotor;
    }

    /**
     * Outputs relevant information to the SmartDashboard.
     */
    public void outputToSmartDashboard() {
        SmartDashboard.putBoolean("GroundIntake On Limit Switch", isLimitSwitch());
        SmartDashboard.putBoolean("GroundIntake Is Disabled", isDisabled());
        SmartDashboard.putBoolean("GroundIntake Is Override", isOverride);
        SmartDashboard.putNumber("Ground Intake Current Degrees", getCurrentAngle());
        SmartDashboard.putNumber("GroundIntake Desired Degrees", desiredDegrees);
    }

}
