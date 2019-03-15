/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.Gyro;
import com.cyborgcats.reusable.PID;

import org.usfirst.frc.team4256.robot.SwerveModule;

import com.cyborgcats.reusable.Xbox;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Robot extends TimedRobot {

    //private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, false, 8.8);// PRACTICE BOT
    //private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, false, 195.1);// PRACTICE BOT
    //private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, false, 251.2);// PRACTICE BOT
    //private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, false, 57.1);// PRACTICE BOT
    private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, true, 17.2);
    private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, true, 351.5);
    private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, true, 54.5);
    private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, true, 284.0);
    private static final D_Swerve swerve = new D_Swerve(moduleA, moduleB, moduleC, moduleD);
    private static final IntakeLifter intakeLifter = new IntakeLifter(Parameters.LIFTER_MASTER_ID, Parameters.LIFTER_FOLLOWER_3_ID, true/* Master Flipped Sensor */, true/*Master Flipped Motor*/,  true/* Follower Three Flipped Sensor */, false/* Follower Three Flipped Motor */, Parameters.LIMIT_SWTICH_LIFTER);
    private static final BallIntake ballIntake = new BallIntake(Parameters.BALL_INTAKE_MOTOR_ID, Parameters.BALL_INTAKE_SENSOR);
    private static final HatchIntake hatchIntake = new HatchIntake(Parameters.HATCH_SOLENOID_FORWARD_CHANNEL, Parameters.HATCH_SOLENOID_REVERSE_CHANNEL);
    private static final Climber climber = new Climber(Parameters.CLIMBER_SOLENOID_LEFT_FORWARD_CHANNEL, Parameters.CLIMBER_SOLENOID_LEFT_REVERSE_CHANNEL, Parameters.CLIMBER_SOLENOID_RIGHT_FORWARD_CHANNEL, Parameters.CLIMBER_SOLENOID_RIGHT_REVERSE_CHANNEL);
    private static final GroundIntake groundIntake = new GroundIntake(Parameters.GROUND_LIFT_ID, 2.0, false, false, Parameters.GROUND_INTAKE_ID, false, Parameters.LIMIT_SWITCH_GROUND_INTAKE);
    private static final Xbox driver = new Xbox(0);
    private static final Xbox gunner = new Xbox(1);
    private static final Gyro gyro = new Gyro(Parameters.GYRO_UPDATE_HZ);
    private static final Limelight limelight = new Limelight();
    public static double gyroHeading = 0.0;
    private static NetworkTableInstance nt;
    private static NetworkTable apollo;

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
        
        limelight.changePipeline(0);//default pipeline

        swerve.init();
        intakeLifter.init();
        groundIntake.init();
        moduleA.getRotationMotor().setInverted(true);
        moduleB.getRotationMotor().setInverted(true);
        moduleC.getRotationMotor().setInverted(true);
        moduleD.getRotationMotor().setInverted(true);
        climber.init();
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
        apollo.getEntry("ModuleA Angle").setNumber(moduleA.getRotationMotor().getCurrentAngle(true));
        apollo.getEntry("ModuleB Angle").setNumber(moduleB.getRotationMotor().getCurrentAngle(true));
        apollo.getEntry("ModuleC Angle").setNumber(moduleC.getRotationMotor().getCurrentAngle(true));
        apollo.getEntry("ModuleD Angle").setNumber(moduleD.getRotationMotor().getCurrentAngle(true));
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
    }

    @Override
    public void testPeriodic() {
    }

    public void hatchIntakePeriodic() {
        if (driver.getRawButtonPressed(Xbox.BUTTON_LB)) {//open
            hatchIntake.release();//open
        } else if (driver.getRawButtonPressed(Xbox.BUTTON_RB)) {//close
            hatchIntake.latch();//close
        }
        
        hatchIntake.outputToSmartDashboard();
    }

    public void ballIntakePeriodic() {
        
        if (driver.getAxisPress(Xbox.AXIS_LT, 0.1)) {                               
            ballIntake.spit(); //spit
        } else if (driver.getAxisPress(Xbox.AXIS_RT, 0.1) && !ballIntake.hasBall()) {
            ballIntake.slurp();//slurp
        } else {                                                                     
            ballIntake.stop(); //stop
        }

        ballIntake.outputToSmartDashboard();
    }

    public void intakeLifterPeriodic() {
        intakeLifter.checkForEncoderSpike();//TODO combine the two checks
        intakeLifter.checkLimitSwitchUpdate();//TODO combine the two checks

        //Increment
        if (gunner.getRawButtonPressed(Xbox.BUTTON_RB)) {
            intakeLifter.increment(IntakeLifter.INCREMENT);//down
        } else if (gunner.getRawButtonPressed(Xbox.BUTTON_LB)) {
            intakeLifter.decrement(IntakeLifter.DECREMENT);//up
        }
        
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

        intakeLifter.outputToSmartDashboard();

        intakeLifter.checkAngle();//End of loop check
    }

    public void groundIntakePeriodic() {
        groundIntake.checkLimitSwitchUpdate();
        
        if (gunner.getRawButton(Xbox.BUTTON_BACK)) {
            groundIntake.setOverrideUp();//TODO TEST THIS
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

        groundIntake.outputToSmartDashboard();

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

        climber.outputToSmartDashboard();
    }

    public void swervePeriodic() {
        limelight.updateVisionTracking();

        //speed multipliers
        final boolean turbo = driver.getRawButton(Xbox.BUTTON_STICK_LEFT);
        final boolean snail = false;
//        final boolean snail = driver.getRawButton(Xbox.BUTTON_STICK_RIGHT);

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
            boolean auto = driver.getRawButton(Xbox.BUTTON_STICK_RIGHT);
            int currentPOVGunner = gunner.getPOV();
            int currentPOV = driver.getPOV();
            if (auto) {//vision auto
                limelight.turnLEDOn();
                swerve.setRobotCentric();
                swerve.travelTowards(limelight.getCommandedDirection());
                swerve.setSpeed(limelight.getCommandedSpeed());
                swerve.setSpin(0.0);
            } else if (currentPOV != -1) {//orienent robot (driver dpad)
                swerve.travelTowards(0.0);
                swerve.setSpeed(0.0);
                int desiredDirection = ((currentPOV) + 180) % 360;// 180 degree offset due to gyro offset
                swerve.face((double)desiredDirection, 0.3);
            } else if (currentPOVGunner == -1) {//normal swerve
                swerve.travelTowards(driver.getCurrentAngle(Xbox.STICK_LEFT, true));
                swerve.setSpeed(speed);
                swerve.setSpin(spin);
            } else {//gunner dpad
                swerve.setRobotCentric();
                speed = ((currentPOVGunner % 90) == 0) ? (0.07) : (0.0);// TODO CONSTANTIZE IT
                speed = (turbo && (currentPOVGunner % 90) == 0) ? (0.15) : (speed);
                double desiredDirection = (((double) currentPOVGunner) + 180.0) % 360.0;// 180 degree offset due to gyro offset
                swerve.travelTowards(desiredDirection);
                swerve.setSpeed(speed);
                swerve.setSpin(0.0);
            }

            if (currentPOV == -1) {//reset spin pid
                PID.clear("spin");
            }

            if (!auto) {//Turns off leds when not in use
 //               limelight.turnLEDOff();
            }
        }

        if (driver.getRawButtonPressed(Xbox.BUTTON_START)) {//reset gyro
            gyro.reset();
        }

        swerve.outputToSmartDashboard();
        limelight.outputToSmartDashboard();

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
