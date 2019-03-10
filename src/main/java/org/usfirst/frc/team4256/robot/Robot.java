/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.Gyro;
import com.cyborgcats.reusable.PID;

import org.usfirst.frc.team4256.robot.SwerveModule;

import com.cyborgcats.reusable.Xbox;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;

public class Robot extends TimedRobot {

    private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, false, 240.0);// PRACTICE BOT
    private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, false, 38.0);// PRACTICE BOT
    private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, false, 251.0);// PRACTICE BOT
    private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, false, 220.0);// PRACTICE BOT
    // private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, true, -63.0);
    // private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, true, -15.0);
    // private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, true, -45.0);
    // private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, false, -16.0);
    private static final D_Swerve swerve = new D_Swerve(moduleA, moduleB, moduleC, moduleD);
    private static final IntakeLifter intakeLifter = new IntakeLifter(Parameters.LIFTER_MASTER_ID, Parameters.LIFTER_FOLLOWER_3_ID, true/* Master Flipped Sensor */, true/*Master Flipped Motor*/,  true/* Follower Three Flipped Sensor */, true/* Follower Three Flipped Motor */, Parameters.LIMIT_SWTICH_LIFTER);
    private static final BallIntake ballIntake = new BallIntake(Parameters.BALL_INTAKE_MOTOR_ID, Parameters.BALL_INTAKE_SENSOR);
    private static final HatchIntake hatchIntake = new HatchIntake(Parameters.HATCH_SOLENOID_FORWARD_CHANNEL, Parameters.HATCH_SOLENOID_REVERSE_CHANNEL);
    private static final Climber climber = new Climber(Parameters.CLIMBER_SOLENOID_LEFT_FORWARD_CHANNEL, Parameters.CLIMBER_SOLENOID_LEFT_REVERSE_CHANNEL, Parameters.CLIMBER_SOLENOID_RIGHT_FORWARD_CHANNEL, Parameters.CLIMBER_SOLENOID_RIGHT_REVERSE_CHANNEL);
    private static final GroundIntake groundIntake = new GroundIntake(28, 2.0, false, false, 27, false, Parameters.LIMIT_SWITCH_GROUND_INTAKE);
    private static final Xbox driver = new Xbox(0);
    private static final Xbox gunner = new Xbox(1);
    private static final Gyro gyro = new Gyro(Parameters.GYRO_UPDATE_HZ);
    private static final DigitalInput tx2PowerSensor = new DigitalInput(Parameters.TX2_POWER_SENSOR);
    private static final DigitalOutput tx2PowerControl = new DigitalOutput(Parameters.TX2_POWER_CONTROL);
    private static final Limelight limelight = new Limelight();
    public static double gyroHeading = 0.0;
    private static NetworkTableInstance nt;
    private static NetworkTable apollo;
    private boolean limelightHasValidTarget = false;
    private boolean isAlignedWithTarget = false;
    private boolean hadBall = false;
    private double spinError = 0.0;
    private double previousIntakeLifterAngle = 0.0;
    private int ballIntakeBlinkCount = 0;
    private boolean isIntakeBlinking = false;

    public static void updateGyroHeading() {
        gyroHeading = gyro.getCurrentAngle();
    }

    @Override
    public void robotInit() {
        gyro.reset();
        gyro.setAngleAdjustment(180.0);

        nt = NetworkTableInstance.getDefault();
        apollo = nt.getTable("Apollo");

        PID.set("spin", 0.005, 0.0, 0.011);

        swerve.init();
        intakeLifter.init();
        groundIntake.init();
        moduleA.getRotationMotor().setInverted(true);
        moduleB.getRotationMotor().setInverted(true);
        moduleC.getRotationMotor().setInverted(true);
        moduleD.getRotationMotor().setInverted(true);
        climber.init();

        if (!tx2PowerSensor.get()) {
            tx2PowerControl.set(true);
            try {
                Thread.sleep(50);// millisecond
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            tx2PowerControl.set(false);
        }
    }

    @Override
    public void disabledInit() {
    }

    @Override
    public void autonomousInit() {
    }

    @Override
    public void teleopInit() {
    }

    @Override
    public void testInit() {
    }

    @Override
    public void robotPeriodic() {
        updateGyroHeading();
        apollo.getEntry("Gyro").setNumber(gyroHeading);
        apollo.getEntry("TX2 Powered On").setBoolean(tx2PowerSensor.get());
        apollo.getEntry("Hatch Intake Open").setBoolean(hatchIntake.isOpen());
        apollo.getEntry("Has Ball").setBoolean(ballIntake.hasBall());
        apollo.getEntry("Current Lifter Angle Degrees").setNumber(intakeLifter.getCurrentAngle());
        apollo.getEntry("Desired Lifter Angle Degrees").setNumber(intakeLifter.getDesiredDegrees());
        apollo.getEntry("Current Lifter Encoder Angle Difference In Degrees").setNumber(intakeLifter.getEncoderDifferenceDegrees());
        apollo.getEntry("Current Lifter Encoder Angle Difference In Counts").setNumber(intakeLifter.getEncoderDifferenceCounts());
        apollo.getEntry("Is Lifter Disabled").setBoolean(intakeLifter.getMaster().getControlMode() == ControlMode.Disabled);
        apollo.getEntry("Is Lifter Limit Switch Pressed").setBoolean(intakeLifter.isLimitSwitch());
        apollo.getEntry("Is First Climber Extended").setBoolean(climber.isLeftExtended());
        apollo.getEntry("Is Second Climber Extended").setBoolean(climber.isRightExtended());
        apollo.getEntry("ModuleA Angle").setNumber(moduleA.getRotationMotor().getCurrentAngle(true));
        apollo.getEntry("ModuleB Angle").setNumber(moduleB.getRotationMotor().getCurrentAngle(true));
        apollo.getEntry("ModuleC Angle").setNumber(moduleC.getRotationMotor().getCurrentAngle(true));
        apollo.getEntry("ModuleD Angle").setNumber(moduleD.getRotationMotor().getCurrentAngle(true));
        apollo.getEntry("ModuleA Traction Temperature (C)").setNumber(moduleA.getTractionMotor().getMotorTemperature());
        apollo.getEntry("ModuleB Traction Temperature (C)").setNumber(moduleB.getTractionMotor().getMotorTemperature());
        apollo.getEntry("ModuleC Traction Temperature (C)").setNumber(moduleC.getTractionMotor().getMotorTemperature());
        apollo.getEntry("ModuleD Traction Temperature (C)").setNumber(moduleD.getTractionMotor().getMotorTemperature());
        apollo.getEntry("ModuleA Traction RPM").setNumber(moduleA.getTractionMotor().getRPM());
        apollo.getEntry("ModuleB Traction RPM").setNumber(moduleB.getTractionMotor().getRPM());
        apollo.getEntry("ModuleC Traction RPM").setNumber(moduleC.getTractionMotor().getRPM());
        apollo.getEntry("ModuleD Traction RPM").setNumber(moduleD.getTractionMotor().getRPM());
        apollo.getEntry("CURRENT POV").setNumber(driver.getPOV());
        apollo.getEntry("Spin Error").setNumber(spinError);
        apollo.getEntry("Valid Target Found").setBoolean(limelightHasValidTarget);
        apollo.getEntry("Is Aligned With Target").setBoolean(isAlignedWithTarget);
        apollo.getEntry("Number Of Encoder Spikes").setNumber(intakeLifter.getNumberOfEncoderSpikes());
        apollo.getEntry("Module A Current").setNumber(moduleA.getTractionMotor().getOutputCurrent());
        apollo.getEntry("Ground Intake Current Angle").setNumber(groundIntake.getCurrentAngle());
        apollo.getEntry("Ground Intake Desired Angle").setNumber(groundIntake.getDesiredDegrees());
        apollo.getEntry("Ground Intake Is Disabled").setBoolean(groundIntake.getLiftMotor().getControlMode() == ControlMode.Disabled);
        apollo.getEntry("Ground Intake Limit Switch Pressed").setBoolean(groundIntake.isLimitSwitchOn());
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void autonomousPeriodic() {
        sharedPeriodic();
    }

    @Override
    public void teleopPeriodic() {
        sharedPeriodic();
        /*
        groundIntake.checkLimitSwitchUpdate();
        
        if (gunner.getAxisPress(Xbox.AXIS_RT, 0.3)) {
            groundIntake.setAngle(105.0);//TODO constant
            if (Math.abs(groundIntake.getCurrentAngle() - 105.0) <= 7.0) {//TODO function this
                groundIntake.slurp();
            } else {
                groundIntake.stop();
            }
        } else if(gunner.getAxisPress(Xbox.AXIS_LT, 0.3)) {
            groundIntake.setAngle(10.0);
            if ((Math.abs(groundIntake.getCurrentAngle() - 10.0) <= 2.5)) {//TODO function this
                hatchIntake.close();
                groundIntake.spit();
            }
        }else if (gunner.getRawButton(Xbox.BUTTON_BACK)) {
            groundIntake.setAngle(0.0);
        } else {
            groundIntake.setDisabled();
            groundIntake.stop();
        }

        groundIntake.checkAngle();

        // BALL INTAKE
        if (driver.getAxisPress(Xbox.AXIS_LT, 0.1)) {
            ballIntake.spit();
        } else if (driver.getAxisPress(Xbox.AXIS_RT, 0.1) && !ballIntake.hasBall()) {
            ballIntake.slurp();
        } else {
            ballIntake.stop();
        }

        // INTAKE LIFTER
        intakeLifter.checkForEncoderSpike();

        intakeLifter.checkLimitSwitchUpdate();
        

        // SET
        if (driver.getRawButtonPressed(Xbox.BUTTON_A)) {// DOWN
            intakeLifter.setAngle(IntakeLifter.POSITION_DOWN);// DOWN POSITION
        } else if (driver.getRawButtonPressed(Xbox.BUTTON_Y)) {// UP
            intakeLifter.setAngle(IntakeLifter.POSITION_UP);// UP POSITION
        } else if (driver.getRawButtonPressed(Xbox.BUTTON_X)) { // UP
            intakeLifter.setAngle(IntakeLifter.POSITION_ROCKETSHIP);// ROCKETSHIP
        } else if (driver.getRawButtonPressed(Xbox.BUTTON_B)) {
            intakeLifter.setAngle(IntakeLifter.POSITION_CARGOSHIP);// CARGOSHIP
        }

        intakeLifter.checkAngle();

        // HATCH INTAKE
        if (driver.getRawButtonPressed(Xbox.BUTTON_LB)) {
            hatchIntake.open();
        } else if (driver.getRawButtonPressed(Xbox.BUTTON_RB)) {
            hatchIntake.close();
        }

        // Climber
        if (gunner.getRawButtonPressed(Xbox.BUTTON_A)) {
            climber.extendLeft();
        } else if (gunner.getRawButtonPressed(Xbox.BUTTON_B)) {
            climber.retractLeft();
        }

        if (gunner.getRawButtonPressed(Xbox.BUTTON_Y)) {
            climber.extendRight();
        } else if (gunner.getRawButtonPressed(Xbox.BUTTON_X)) {
            climber.retractRight();
        }

        limelight.updateVisionTracking();
        // {speed multipliers}
        final boolean turbo = driver.getRawButton(Xbox.BUTTON_STICK_LEFT);
        final boolean snail = driver.getRawButton(Xbox.BUTTON_STICK_RIGHT);

        // {calculating speed}
        double speed = driver.getCurrentRadius(Xbox.STICK_LEFT, true);// turbo mode
        if (turbo) {
            speed *= speed;// ---------------------------------------turbo mode (squared because of Luke's
                           // preference)
        } else if (snail) {
            speed *= 0.2 * speed;// ---------------------------------------snail mode
        } else {
            speed *= 0.6 * speed;// ---------------------------------------normal mode
        }

        // {calculating spin}
        double spin = 0.5 * driver.getDeadbandedAxis(Xbox.AXIS_RIGHT_X);// normal mode
        if (snail) {
            spin *= 0.4;// ----------------------------------------snail mode
        }
        spin *= spin * Math.signum(spin);

        swerve.setFieldCentric();

        if (driver.getRawButton(Xbox.BUTTON_BACK)) {
            swerve.formX();// X lock
        } else {// SWERVE DRIVE
            boolean auto = gunner.getRawButton(Xbox.BUTTON_START);
            int currentPOVGunner = gunner.getPOV();
            int currentPOV = driver.getPOV();
            if (auto) {
                limelight.turnLEDOn();
                swerve.setRobotCentric();
                swerve.travelTowards(limelight.getCommandedDirection());
                swerve.setSpeed(limelight.getCommandedSpeed());
                swerve.setSpin(0.0);
            } else if (currentPOV != -1) {
                swerve.travelTowards(0.0);
                swerve.setSpeed(0.0);
                double desiredDirection = (((double) currentPOV) + 180.0) % 360.0;// 180 degree offset due to gyro offset
                spinError = swerve.face(desiredDirection, 0.3);
            } else if (currentPOVGunner == -1) {
                swerve.travelTowards(driver.getCurrentAngle(Xbox.STICK_LEFT, true));
                swerve.setSpeed(speed);
                swerve.setSpin(spin);
            } else {
                swerve.setRobotCentric();
                speed = ((currentPOVGunner % 90) == 0) ? (0.07) : (0.0);// TODO CONSTANTIZE IT
                speed = (turbo && (currentPOVGunner % 90) == 0) ? (0.15) : (speed);
                double desiredDirection = (((double) currentPOVGunner) + 180.0) % 360.0;// 180 degree offset due to gyro offset
                swerve.travelTowards(desiredDirection);
                swerve.setSpeed(speed);
                swerve.setSpin(0.0);
            }

            if (!auto) {
                // limelight.turnLEDOff();
            }

            if (currentPOV == -1) {
                PID.clear("spin");
            }

        }

        // RESETS GYRO
        if (driver.getRawButtonPressed(Xbox.BUTTON_START)) {
            gyro.reset();
        }

        swerve.completeLoopUpdate();
        */
    }

    @Override
    public void testPeriodic() {

        ballIntakePeriodic();

        hatchIntakePeriodic();

        intakeLifterPeriodic();

        groundIntake.checkLimitSwitchUpdate();
        
        if (gunner.getRawButton(Xbox.BUTTON_BACK)) {
            groundIntake.setAngle(0.0);
        } else if (gunner.getAxisPress(Xbox.AXIS_RT, 0.3)) {
            groundIntake.setAngle(105.0);//TODO constant
            if (Math.abs(groundIntake.getCurrentAngle() - 105.0) <= 7.0) {//TODO function this
                groundIntake.slurp();
            } else {
                groundIntake.stop();
            }
        } else if(gunner.getAxisPress(Xbox.AXIS_LT, 0.3)) {
            groundIntake.transferHatch(hatchIntake, intakeLifter);
        } else {
            groundIntake.setDisabled();
            groundIntake.stop();
        }

        groundIntake.checkAngle();
        
        swervePeriodic();

        
        // swerve.setAllModulesToZero();
    }

    public void hatchIntakePeriodic() {
        if (driver.getRawButtonPressed(Xbox.BUTTON_LB)) {//open
            hatchIntake.open();//open
        } else if (driver.getRawButtonPressed(Xbox.BUTTON_RB)) {//close
            hatchIntake.close();//close
        }
    }

    public void ballIntakePeriodic() {
        boolean hasBall = ballIntake.hasBall();

        if (hasBall && !hadBall) {//TODO TEST
            isIntakeBlinking = true;
            limelight.makeLEDBlink();
            ballIntakeBlinkCount++;
        } else if (isIntakeBlinking && ballIntakeBlinkCount <= 20) {
            ballIntakeBlinkCount++;
        } else if ((isIntakeBlinking && ballIntakeBlinkCount > 20) || (isIntakeBlinking && hadBall && !hasBall)) {
            isIntakeBlinking = false;
            ballIntakeBlinkCount = 0;
            limelight.turnLEDOn();
        }
        
        if (driver.getAxisPress(Xbox.AXIS_LT, 0.1)) {                               
            ballIntake.spit(); //spit
        } else if (driver.getAxisPress(Xbox.AXIS_RT, 0.1) && !ballIntake.hasBall()) {
            ballIntake.slurp();//slurp
        } else {                                                                     
            ballIntake.stop(); //stop
        }

        hadBall = hasBall;
    }

    public void intakeLifterPeriodic() {
        intakeLifter.checkForEncoderSpike();//TODO combine the two checks
        intakeLifter.checkLimitSwitchUpdate();//TODO combine the two checks
        
        //Set Predefined
        if (driver.getRawButtonPressed(Xbox.BUTTON_A)) {
            intakeLifter.setAngle(IntakeLifter.POSITION_DOWN);      //down position
        } else if (driver.getRawButtonPressed(Xbox.BUTTON_Y)) {
            intakeLifter.setAngle(IntakeLifter.POSITION_UP);        //up position
        } else if (driver.getRawButtonPressed(Xbox.BUTTON_X)) {
            intakeLifter.setAngle(IntakeLifter.POSITION_ROCKETSHIP);//rocketship position
        } else if (driver.getRawButtonPressed(Xbox.BUTTON_B)) {
            intakeLifter.setAngle(IntakeLifter.POSITION_CARGOSHIP); //cargoship position
        }

        intakeLifter.checkAngle();//End of loop check
    }

    public void groundIntakePeriodic() {
        groundIntake.checkLimitSwitchUpdate();
        
        if (gunner.getAxisPress(Xbox.AXIS_RT, 0.3)) {
            groundIntake.setAngle(105.0);//TODO constant
            if (Math.abs(groundIntake.getCurrentAngle() - 105.0) <= 7.0) {//TODO function this
                groundIntake.slurp();
            } else {
                groundIntake.stop();
            }
        } else if(gunner.getAxisPress(Xbox.AXIS_LT, 0.3)) {
            groundIntake.setAngle(10.0);
            if ((Math.abs(groundIntake.getCurrentAngle() - 10.0) <= 2.5)) {//TODO function this
                hatchIntake.close();
                groundIntake.spit();
            }
        }else if (gunner.getRawButton(Xbox.BUTTON_BACK)) {
            groundIntake.setAngle(0.0);
        } else {
            groundIntake.setDisabled();
            groundIntake.stop();
        }

        groundIntake.checkAngle();
    }

    public void climberPeriodic() {
        if (gunner.getRawButtonPressed(Xbox.BUTTON_A)) {
            climber.extendLeft();
        } else if (gunner.getRawButtonPressed(Xbox.BUTTON_B)) {
            climber.retractLeft();
        }

        if (gunner.getRawButtonPressed(Xbox.BUTTON_Y)) {
            climber.extendRight();
        } else if (gunner.getRawButtonPressed(Xbox.BUTTON_X)) {
            climber.retractRight();
        }
    }

    public void swervePeriodic() {
        limelight.updateVisionTracking();

        //TODO NEEDS TESTING
        if (intakeLifter.getCurrentAngle() <= 90.0 && previousIntakeLifterAngle > 90) {//Cargoship
            limelight.changePipeline(0);//TODO setup
        } else if (intakeLifter.getCurrentAngle() > 90.0 && previousIntakeLifterAngle <= 90) {//Rocketship
            limelight.changePipeline(1);//TODO setup
        }
        previousIntakeLifterAngle = intakeLifter.getCurrentAngle();
 
        //speed multipliers
        final boolean turbo = driver.getRawButton(Xbox.BUTTON_STICK_LEFT);
        final boolean snail = driver.getRawButton(Xbox.BUTTON_STICK_RIGHT);

        double speed = driver.getCurrentRadius(Xbox.STICK_LEFT, true);
        speed *= speed;
        if (snail) {
            speed *= 0.2;
        } else if (!turbo) {
            speed *= 0.6;
        }

        double spin = 0.5 * driver.getDeadbandedAxis(Xbox.AXIS_RIGHT_X);// normal mode
        if (snail) {
            spin *= 0.4;// ----------------------------------------snail mode
        }
        spin *= spin * Math.signum(spin);

        swerve.setFieldCentric();

        if (driver.getRawButton(Xbox.BUTTON_BACK)) {
            swerve.formX();//X lock
        } else {//Swerve Drive
            boolean auto = gunner.getRawButton(Xbox.BUTTON_START);
            int currentPOVGunner = gunner.getPOV();
            int currentPOV = driver.getPOV();
            if (auto) {
                limelight.turnLEDOn();
                swerve.setRobotCentric();
                swerve.travelTowards(limelight.getCommandedDirection());
                swerve.setSpeed(limelight.getCommandedSpeed());
                swerve.setSpin(0.0);
            } else if (currentPOV != -1) {
                swerve.travelTowards(0.0);
                swerve.setSpeed(0.0);
                double desiredDirection = (((double) currentPOV) + 180.0) % 360.0;// 180 degree offset due to gyro offset
                spinError = swerve.face(desiredDirection, 0.3);
            } else if (currentPOVGunner == -1) {
                swerve.travelTowards(driver.getCurrentAngle(Xbox.STICK_LEFT, true));
                swerve.setSpeed(speed);
                swerve.setSpin(spin);
            } else {
                swerve.setRobotCentric();
                speed = ((currentPOVGunner % 90) == 0) ? (0.07) : (0.0);// TODO CONSTANTIZE IT
                speed = (turbo && (currentPOVGunner % 90) == 0) ? (0.15) : (speed);
                double desiredDirection = (((double) currentPOVGunner) + 180.0) % 360.0;// 180 degree offset due to gyro offset
                swerve.travelTowards(desiredDirection);
                swerve.setSpeed(speed);
                swerve.setSpin(0.0);
            }

            if (currentPOV == -1) {
                PID.clear("spin");
            }
        }

        if (driver.getRawButtonPressed(Xbox.BUTTON_START)) {
            gyro.reset();
        }

        swerve.completeLoopUpdate();
    }

    public void sharedPeriodic() {
        hatchIntakePeriodic();

        ballIntakePeriodic();

        intakeLifterPeriodic();

        groundIntakePeriodic();
        
        climberPeriodic();
        
        swervePeriodic();
    }
}
