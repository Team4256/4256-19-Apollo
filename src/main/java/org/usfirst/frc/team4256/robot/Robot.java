/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot;

import java.util.Optional;

import com.cyborgcats.reusable.Gyro;
import com.cyborgcats.reusable.PID;

import org.usfirst.frc.team4256.robot.BallIntake.BallIntakeState;
import org.usfirst.frc.team4256.robot.Controllers.Driver;
import org.usfirst.frc.team4256.robot.Controllers.Gunner;
import org.usfirst.frc.team4256.robot.LED.LEDState;
import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeChooser;
import org.usfirst.frc.team4256.robot.auto.AutoModeExecutor;

import com.cyborgcats.reusable.Xbox;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Robot extends TimedRobot {
    public static final double GYRO_OFFSET = 180.0;
    public static final double TIPPING_THRESHOLD = 11.0;
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
    private static final LED ledStrip = LED.getInstance();
    public static double gyroHeading = 0.0;
    private static NetworkTableInstance nt;
    private static NetworkTable apollo;
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
        apollo.getEntry("Selected Starting Position").setString(autoModeChooser.getRawSelections()[0]);
        apollo.getEntry("Desired Auto Mode").setString(autoModeChooser.getRawSelections()[1]);
        apollo.getEntry("Has Ball Test").setBoolean(ballIntake.hasBall());
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

        if (autoModeExecutor.getAutoMode() == null) {
            autoModeExecutor.setAutoMode(autoModeChooser.getSelectedAutoMode().get());//TODO if still broken choose a default auto mode
        }

        autoModeExecutor.start();
    }

    @Override
    public void autonomousPeriodic() {
        limelight.turnLEDOn();
        //exits auto early if driver presses the right stick and an auto mode is currently active
        if (driver.getRawButtonPressed(Xbox.BUTTON_STICK_RIGHT) && autoModeExecutor.getAutoMode().isActive()) {//TODO test
            System.out.println("Driver Took Over");
            autoModeExecutor.stop();
            autoModeExecutor = null;
        }
        //if an auto mode is not active run sharedPeriodic
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
                limelight.turnLEDOn();
                swerve.setRobotCentric();
                direction = limelight.getCommandedDirection();
                speed = limelight.getCommandedSpeed();
                spin = limelight.getCommandedSpin();
            } else if (currentPOV != -1) {//orienent robot (driver dpad)
                direction = 0.0;
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

            if (!auto) {//Not in auto alignment and placement mode
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

    public void LEDPeriodic() {
        if (climber.isLeftExtended()) {
            ledStrip.setLEDState(LEDState.CLIMBER_LEFT);
        } else if (climber.isRightExtended()) {
            ledStrip.setLEDState(LEDState.CLIMBER_RIGHT);
        } else if (limelight.hasTarget()) {
            ledStrip.setLEDState(LEDState.VALID_TARGET);
        } else if (driver.getRawButton(Xbox.BUTTON_STICK_RIGHT) && !limelight.hasTarget()) {
            ledStrip.setLEDState(LEDState.NO_VALID_TARGET);
        } else if (ballIntake.hasBall()) {
            ledStrip.setLEDState(LEDState.HAS_BALL);
        } else if (ballIntake.getCurrentBallIntakeState() == BallIntakeState.SLURP) {
            ledStrip.setLEDState(LEDState.WANTS_BALL);
        } else {
            ledStrip.setLEDState(LEDState.DRIVER_CONTROL);
        }
        ledStrip.update();
    }

    public void sharedPeriodic() {
        hatchIntakePeriodic();

        ballIntakePeriodic();

        intakeLifterPeriodic();

        groundIntakePeriodic();
        
        climberPeriodic();
        
        swervePeriodic();

        LEDPeriodic();
    }
}
