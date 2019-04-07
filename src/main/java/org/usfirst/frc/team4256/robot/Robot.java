/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot;

import java.util.Optional;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.Gyro;
import com.cyborgcats.reusable.PID;

import org.usfirst.frc.team4256.robot.SwerveModule;
import org.usfirst.frc.team4256.robot.Controllers.Driver;
import org.usfirst.frc.team4256.robot.Controllers.Gunner;
import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeChooser;
import org.usfirst.frc.team4256.robot.auto.AutoModeExecutor;
import org.usfirst.frc.team4256.robot.auto.modes.*;

import com.cyborgcats.reusable.Xbox;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Robot extends TimedRobot {
    public static final double GYRO_OFFSET = 180.0;
    public static final double TIPPING_THRESHOLD = 11.0;
    //private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, false, 8.8);// PRACTICE BOT
    //private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, false, 195.1);// PRACTICE BOT
    //private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, false, 251.2);// PRACTICE BOT
    //private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, false, 57.1);// PRACTICE BOT
    //private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, true, 320.625);
    //private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, true, 48.867);
    //private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, true, 56.602);
    //private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, true, 303.047);
    private static final D_Swerve swerve = D_Swerve.getInstance();
    private static final IntakeLifter intakeLifter = IntakeLifter.getInstance();
    private static final BallIntake ballIntake = BallIntake.getInstance();
    private static final HatchIntake hatchIntake = HatchIntake.getInstance();
    private static final Climber climber = Climber.getInstance();
    private static final GroundIntake groundIntake = GroundIntake.getInstance();
    private static final Driver driver = Driver.getInstance();
    private static final Gunner gunner = Gunner.getInstance();
    private static final Gyro gyro = new Gyro(Parameters.GYRO_UPDATE_HZ);
    private static final Limelight limelight = Limelight.getInstance();
    public static double gyroHeading = 0.0;
    private static NetworkTableInstance nt;
    private static NetworkTable apollo;
//    private static boolean isClimbing = false;
    private static AutoModeChooser autoModeChooser = new AutoModeChooser();
    private static AutoModeExecutor autoModeExecutor = null;

    public synchronized static void updateGyroHeading() {
        gyroHeading = gyro.getCurrentAngle();
    }

    @Override
    public void robotInit() {
        gyro.reset();
        gyro.setAngleAdjustment(GYRO_OFFSET);

        nt = NetworkTableInstance.getDefault();
        apollo = nt.getTable("Apollo");

//        PID.set("spin", 0.005, 0.0, 0.011);
        PID.set("spin", 0.006, 0.0, 0.018);
        
        limelight.setPipeline(0);//default pipeline

        swerve.init();
        intakeLifter.init();
        groundIntake.init();
        swerve.getSwerveModules()[0].getRotationMotor().setInverted(true);
        swerve.getSwerveModules()[1].getRotationMotor().setInverted(true);
        swerve.getSwerveModules()[2].getRotationMotor().setInverted(true);
        swerve.getSwerveModules()[3].getRotationMotor().setInverted(true);
        climber.init();
    }

    @Override
    public void robotPeriodic() {
        updateGyroHeading();
        apollo.getEntry("Gyro").setNumber(gyroHeading);
        apollo.getEntry("ModuleA Angle").setNumber(swerve.getSwerveModules()[0].getRotationMotor().getCurrentAngle(true));
        apollo.getEntry("ModuleB Angle").setNumber(swerve.getSwerveModules()[1].getRotationMotor().getCurrentAngle(true));
        apollo.getEntry("ModuleC Angle").setNumber(swerve.getSwerveModules()[2].getRotationMotor().getCurrentAngle(true));
        apollo.getEntry("ModuleD Angle").setNumber(swerve.getSwerveModules()[3].getRotationMotor().getCurrentAngle(true));
        apollo.getEntry("Driver").setNumber(driver.getRawAxis(Xbox.AXIS_LEFT_X));
        apollo.getEntry("Gyro Pitch").setNumber(gyro.getPitch());
        apollo.getEntry("Gyro Roll").setNumber(gyro.getRoll());
        apollo.getEntry("Selected Starting Position").setString(autoModeChooser.getRawSelections()[0]);
        apollo.getEntry("Desired Auto Mode").setString(autoModeChooser.getRawSelections()[1]);
    }

    @Override
    public void disabledInit() {
        if (autoModeExecutor != null) {
            autoModeExecutor.stop();
        }
        autoModeExecutor = null;

        autoModeChooser.reset();
        autoModeChooser.update();
        autoModeExecutor = new AutoModeExecutor();
    }

    @Override
    public void disabledPeriodic() {
        limelight.turnLEDOff();
        autoModeChooser.update();
        Optional<AutoMode> autoMode = autoModeChooser.getSelectedAutoMode();
        if (autoMode.isPresent() && autoMode.get() != autoModeExecutor.getAutoMode()) {
            autoModeExecutor.setAutoMode(autoMode.get());
        }
        //System.gc();
    }

    @Override
    public void autonomousInit() {
        limelight.turnLEDOn();
        gyro.reset();
        autoModeExecutor.start();
    }

    @Override
    public void autonomousPeriodic() {
        if (driver.isActive() || gunner.isActive()) {//TODO test
            autoModeExecutor.stop();
        }
        if (!autoModeExecutor.getAutoMode().isActive()) {//TODO test
            sharedPeriodic();
        }
    }

    @Override
    public void teleopInit() {
        if (autoModeExecutor != null) {
            autoModeExecutor.stop();
        }
        autoModeExecutor = null;
        limelight.turnLEDOff();
    }

    @Override
    public void teleopPeriodic() {
        sharedPeriodic();
    }

    @Override
    public void testInit() {
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
        intakeLifter.checkForEncoderSpike();
        intakeLifter.checkLimitSwitchUpdate();

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
            groundIntake.setOverrideUp();
        } else if (gunner.getAxisPress(Xbox.AXIS_RT, 0.3)) {
            groundIntake.setAngle(105.0);//TODO Could be a constant...
            if (Math.abs(groundIntake.getCurrentAngle() - 105.0) <= 7.0) {
                hatchIntake.release();
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
//            isClimbing = true;
            climber.extendLeft();
        } else if (gunner.getRawButtonPressed(Xbox.BUTTON_B)) {
            climber.retractLeft();
        }

        if (gunner.getRawButtonPressed(Xbox.BUTTON_Y)) {
 //           isClimbing = true;
            climber.extendRight();
        } else if (gunner.getRawButtonPressed(Xbox.BUTTON_X)) {
            climber.retractRight();
        }

        climber.outputToSmartDashboard();
    }

    public void swervePeriodic() {
        /*
        if (!isClimbing) {
            limelight.turnLEDOn();
        }else {
            limelight.turnLEDOff();
        }
        */
        limelight.updateVisionTracking();
        if (limelight.isSplitView()) {
            limelight.setOtherCameraView();//Driver oriented view
        }

        //speed multipliers
        final boolean turbo = driver.getRawButton(Xbox.BUTTON_STICK_LEFT);

        double direction = driver.getCurrentAngle(Xbox.STICK_LEFT, true);

        double speed = driver.getCurrentRadius(Xbox.STICK_LEFT, true);
        speed *= speed;
        if (!turbo) {
            speed *= 0.6;
        }

        double spin = 0.5 * driver.getDeadbandedAxis(Xbox.AXIS_RIGHT_X);// normal mode
        spin *= spin * Math.signum(spin);

        swerve.setFieldCentric();

        if (driver.getRawButton(Xbox.BUTTON_BACK)) {
            swerve.formX();//X lock
        } else {//Swerve Drive
            boolean auto = driver.getRawButton(Xbox.BUTTON_STICK_RIGHT);
            int currentPOVGunner = gunner.getPOV();
            int currentPOV = driver.getPOV();
            if (auto) {//vision auto
                //isClimbing = false;
                limelight.turnLEDOn();
                swerve.setRobotCentric();
                direction = limelight.getCommandedDirection();
                speed = limelight.getCommandedSpeed();
                spin = limelight.getCommandedSpin();
            } else if (currentPOV != -1) {//orienent robot (driver dpad)
                direction = 0.0;//TODO test removing this
                speed = 0.0;
                double desiredDirection = (currentPOV + GYRO_OFFSET) % 360;// 180 degree offset due to gyro offset
                swerve.face(desiredDirection, 0.3);//sets spin
            } else if (currentPOVGunner != -1) {//gunner dpad
                swerve.setRobotCentric();
                direction = (((double)currentPOVGunner) + GYRO_OFFSET) % 360.0;// 180 degree offset due to gyro offset
                speed = ((currentPOVGunner % 90) == 0) ? (0.07) : (0.0);
                speed = (turbo && (currentPOVGunner % 90) == 0) ? (0.15) : (speed);
                spin = 0.0;
            }

            if (!auto) {
                limelight.turnLEDOff();
            }

            if (currentPOV == -1) {//reset spin pid
                PID.clear("spin");
                swerve.setSpin(spin);
            }

            swerve.travelTowards(direction);
            swerve.setSpeed(speed);
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
