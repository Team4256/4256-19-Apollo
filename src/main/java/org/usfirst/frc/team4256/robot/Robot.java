/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.Compass;
import com.cyborgcats.reusable.Gyro;
import com.cyborgcats.reusable.PID;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team4256.robot.SwerveModule;

import com.cyborgcats.reusable.Xbox;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;

public class Robot extends TimedRobot {

  private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, false, 240.0);//PRACTICE BOT
  private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, false, 40.0);//PRACTICE BOT
  private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, false, 251.0);//PRACTICE BOT
  private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, false, 224.0);//PRACTICE BOT
//  private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, true, -63.0);
//  private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, true, -15.0);
//  private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, true, -45.0);
//  private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, false, -16.0);
  private static final D_Swerve swerve = new D_Swerve(moduleA, moduleB, moduleC, moduleD);
  private static final IntakeLifter intakeLifter = new IntakeLifter(Parameters.LIFTER_MASTER_ID, Parameters.LIFTER_FOLLOWER_1_ID, Parameters.LIFTER_FOLLOWER_2_ID, Parameters.LIFTER_FOLLOWER_3_ID, false/*Master Flipped Sensor*/, false/*Follower One Flipped Motor*/, true/*Follower Two Flipped Sensor*/, true/*Follower Two Flipped Motor*/, true/*Follower Three Flipped Motor*/, Parameters.LIMIT_SWTICH_LIFTER);
  private static final BallIntake ballIntake = new BallIntake(Parameters.BALL_INTAKE_MOTOR_ID, Parameters.BALL_INTAKE_SENSOR);
  private static final HatchIntake hatchIntake = new HatchIntake(Parameters.HATCH_SOLENOID_FORWARD_CHANNEL, Parameters.HATCH_SOLENOID_REVERSE_CHANNEL);
  private static final Climber climber = new Climber(Parameters.CLIMBER_SOLENOID_LEFT_FORWARD_CHANNEL, Parameters.CLIMBER_SOLENOID_LEFT_REVERSE_CHANNEL, Parameters.CLIMBER_SOLENOID_RIGHT_FORWARD_CHANNEL, Parameters.CLIMBER_SOLENOID_RIGHT_REVERSE_CHANNEL);
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
  private double limelightSwerveDirection = 0.0;
  private double limelightSwerveSpeed = 0.0;
  private double spinError = 0.0;

  public static void updateGyroHeading() {
    gyroHeading = gyro.getCurrentAngle();
  }

  @Override
  public void robotInit() {
    gyro.reset();
    gyro.setAngleAdjustment(180.0);//TODO TEST if I should do this every time gyro resets

    nt = NetworkTableInstance.getDefault();
    apollo = nt.getTable("Apollo");

    PID.set("spin", 0.005, 0.0, 0.011);//TODO test

    swerve.init();
    intakeLifter.init();
    moduleA.getRotationMotor().setInverted(true);//TODO find better place to put this
    moduleB.getRotationMotor().setInverted(true);//TODO find better place to put this
    moduleC.getRotationMotor().setInverted(true);//TODO find better place to put this
    moduleD.getRotationMotor().setInverted(true);//TODO find better place to put this
    climber.retractLeft();//TODO make init function for climber
    climber.retractRight();//TODO make init function for climber
    
    if (!tx2PowerSensor.get()) {
        tx2PowerControl.set(true);
        try {
            Thread.sleep(50);//millisecond
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        tx2PowerControl.set(false);
    }
    new Thread(() -> {
        UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
        camera.setResolution(480, 360);

        CvSink cvSink = CameraServer.getInstance().getVideo();
        CvSource outputStream = CameraServer.getInstance().putVideo("Black & White", 480, 360);

        Mat source = new Mat();
        Mat output = new Mat();

        while(!Thread.interrupted()) {
            cvSink.grabFrame(source);
            Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
            outputStream.putFrame(output);
        }
    }).start();
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
    }


  @Override
  public void disabledPeriodic() {
  }


  @Override
  public void autonomousPeriodic() {
  }


  @Override
  public void teleopPeriodic() {

    //BALL INTAKE
    if (driver.getAxisPress(Xbox.AXIS_LT, 0.1)) 
    {
        ballIntake.spit();
    }
    else if (driver.getAxisPress(Xbox.AXIS_RT, 0.1) && !ballIntake.hasBall()) 
    {
        ballIntake.slurp();
    }
    else 
    {
        ballIntake.stop();
    }

    //INTAKE LIFTER
    intakeLifter.checkForEncoderSpike();
    
    intakeLifter.checkLimitSwitchUpdate();

    //INCREMENT
    if (gunner.getRawButtonPressed(Xbox.BUTTON_RB)) 
    {//DOWN
        intakeLifter.increment(IntakeLifter.INCREMENT);//MOVE DOWN
    }
    else if (gunner.getRawButtonPressed(Xbox.BUTTON_LB))
    {//UP
        intakeLifter.decrement(IntakeLifter.DECREMENT);//MOVE UP
    }

    //SET
    if (driver.getRawButtonPressed(Xbox.BUTTON_A)) 
    {//DOWN
        intakeLifter.setAngle(IntakeLifter.POSITION_DOWN);//DOWN POSITION
    }
    else if (driver.getRawButtonPressed(Xbox.BUTTON_Y)) 
    {//UP
        intakeLifter.setAngle(IntakeLifter.POSITION_UP);//UP POSITION
    }
    else if (driver.getRawButtonPressed(Xbox.BUTTON_X)) 
    {   //UP
        intakeLifter.setAngle(IntakeLifter.POSITION_ROCKETSHIP);//ROCKETSHIP
    }
    else if (driver.getRawButtonPressed(Xbox.BUTTON_B)) 
    {
        intakeLifter.setAngle(IntakeLifter.POSITION_CARGOSHIP);//CARGOSHIP
    }

    intakeLifter.checkAngle();


    //HATCH INTAKE
    if (driver.getRawButtonPressed(Xbox.BUTTON_LB)) 
    {
        hatchIntake.open();
    }
    else if (driver.getRawButtonPressed(Xbox.BUTTON_RB)) 
    {
        hatchIntake.close();
    }


    //Climber
    if (gunner.getRawButtonPressed(Xbox.BUTTON_A)) 
    {
        climber.extendLeft();
    }
    else if (gunner.getRawButtonPressed(Xbox.BUTTON_B))
    {
        climber.retractLeft();
    }
    
    if (gunner.getRawButtonPressed(Xbox.BUTTON_Y))
    {
        climber.extendRight();
    }
    else if (gunner.getRawButtonPressed(Xbox.BUTTON_X))
    {
        climber.retractRight();
    }


    limelight.updateVisionTracking(); 
    //{speed multipliers}    
    final boolean turbo = driver.getRawButton(Xbox.BUTTON_STICK_LEFT);
	final boolean snail = driver.getRawButton(Xbox.BUTTON_STICK_RIGHT);
		
	//{calculating speed}
	double speed = driver.getCurrentRadius(Xbox.STICK_LEFT, true);//turbo mode
    if (turbo)
    {
        speed *= speed;//---------------------------------------turbo mode (squared because of Luke's preference)
    } 
    else if(snail) 
    {
        speed *= 0.2 * speed;//---------------------------------------snail mode
    }
    else 
    {
        speed *= 0.6 * speed;//---------------------------------------normal mode
    }
		
    //{calculating spin}
	double spin = 0.5*driver.getDeadbandedAxis(Xbox.AXIS_RIGHT_X);//normal mode
    if (snail) 
    {
        spin  *= 0.4;//----------------------------------------snail mode
    }
        spin *= spin*Math.signum(spin);
        
        swerve.setFieldCentric();

    if (driver.getRawButton(Xbox.BUTTON_BACK)) 
    {
        swerve.formX();//X lock
    }
    else 
    {//SWERVE DRIVE
        boolean auto = gunner.getAxisPress(Xbox.AXIS_RT, 0.5);
        int currentPOVGunner = gunner.getPOV();
        int currentPOV = driver.getPOV();
        if (auto)
        {
            limelight.turnLEDOn();
            swerve.setRobotCentric();
            swerve.travelTowards(limelight.getCommandedDirection());
            swerve.setSpeed(limelight.getCommandedSpeed());
            swerve.setSpin(0.0);
        }
        else if (currentPOV != -1) 
        {
            swerve.travelTowards(0.0);
            swerve.setSpeed(0.0);
            double desiredDirection = (((double)currentPOV)+180.0) % 360.0;//180 degree offset due to gyro offset
            spinError = swerve.face(desiredDirection, 0.3);
        }
        else if (currentPOVGunner == -1) 
        {
            swerve.travelTowards(driver.getCurrentAngle(Xbox.STICK_LEFT, true));
		    swerve.setSpeed(speed);
		    swerve.setSpin(spin);
        }
        else 
        {
            swerve.setRobotCentric();
            speed = ((currentPOVGunner % 90) == 0) ? (0.07) : (0.0);//TODO CONSTANTIZE IT
            speed = (turbo && (currentPOVGunner % 90) == 0) ? (0.15) : (speed);
            double desiredDirection = (((double)currentPOVGunner)+180.0)%360.0;//180 degree offset due to gyro offset
            swerve.travelTowards(desiredDirection);
            swerve.setSpeed(speed);
            swerve.setSpin(0.0);
        }

        if (!auto) {
//            limelight.turnLEDOff();
        }

        if (currentPOV == -1) {
            PID.clear("spin");
        }
		
    }

    //RESETS GYRO
    if (driver.getRawButtonPressed(Xbox.BUTTON_START)) 
    {
        gyro.reset();
    }

    swerve.completeLoopUpdate();
  }

  //TODO find somewhere else to put this
  public void updateLimelightTracking()
  {

    final double LIMELIGHT_SPEED_CONSTANT = 0.02;
//    final double LIMELIGHT_SPEED_CONSTANT = 0.04;//TODO larger speed constant to test
    final double LIMELIGHT_MAX_SPEED = 0.15;
//    final double LIMELIGHT_MAX_SPEED = 0.25;//TODO Larger max speed to test
    double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
    double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);
    double ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0.0);

    if (tv < 1.0) 
    {
        limelightHasValidTarget = false;
        limelightSwerveDirection = 0.0;
        limelightSwerveSpeed = 0.0;
        return;
    }

    limelightHasValidTarget = true;

    isAlignedWithTarget = Math.abs(tx) < 2.0;//TODO test
//    isAlignedWithTarget = Math.abs(tx) < 1.0;//TODO test for lower threshold
//    isAlignedWithTarget = Math.abs(tx) < 0.5;//TODO test for even lower threshold

    if (!isAlignedWithTarget) 
    {
        limelightSwerveDirection = ((Math.signum(tx) > 0.0) ? 270.0 : 90.0);
//        limelightSwerveDirection = ((Math.signum(tx) > 0.0) ? 280.0 : 80.0);//TODO test odd direction idea
        limelightSwerveSpeed = Math.abs(tx) * LIMELIGHT_SPEED_CONSTANT;
        limelightSwerveSpeed = (limelightSwerveSpeed > LIMELIGHT_MAX_SPEED) ? LIMELIGHT_MAX_SPEED : limelightSwerveSpeed;
    }
    else //TODO probably put somewhere else
    {
        limelightSwerveDirection = 0.0;//TODO
        limelightSwerveSpeed = 0.0;//TODO
    }
    
  }

  @Override
  public void testPeriodic() {
    limelight.updateVisionTracking(); 
    //{speed multipliers}    
    final boolean turbo = driver.getRawButton(Xbox.BUTTON_STICK_LEFT);
	final boolean snail = driver.getRawButton(Xbox.BUTTON_STICK_RIGHT);
		
	//{calculating speed}
	double speed = driver.getCurrentRadius(Xbox.STICK_LEFT, true);//turbo mode
    if (turbo)
    {
        speed *= speed;//---------------------------------------turbo mode (squared because of Luke's preference)
    } 
    else if(snail) 
    {
        speed *= 0.2 * speed;//---------------------------------------snail mode
    }
    else 
    {
        speed *= 0.6 * speed;//---------------------------------------normal mode
    }
		
    //{calculating spin}
	double spin = 0.5*driver.getDeadbandedAxis(Xbox.AXIS_RIGHT_X);//normal mode
    if (snail) 
    {
        spin  *= 0.4;//----------------------------------------snail mode
    }
        spin *= spin*Math.signum(spin);
        
        swerve.setFieldCentric();

    if (driver.getRawButton(Xbox.BUTTON_BACK)) 
    {
        swerve.formX();//X lock
    }
    else 
    {//SWERVE DRIVE
        boolean auto = gunner.getAxisPress(Xbox.AXIS_RT, 0.5);
        int currentPOVGunner = gunner.getPOV();
        int currentPOV = driver.getPOV();
        if (auto)
        {
            swerve.setRobotCentric();
            swerve.travelTowards(limelight.getCommandedDirection());
            swerve.setSpeed(limelight.getCommandedSpeed());
            swerve.setSpin(limelight.getCommandedSpin());//TODO test
        }
        else if (currentPOVGunner != -1) 
        {
            swerve.travelTowards(0.0);
            swerve.setSpeed(0.0);
            spinError = swerve.face((((double)currentPOVGunner)+180.0) % 360.0, 0.5);
        }
        else if (currentPOV == -1) 
        {
            swerve.travelTowards(driver.getCurrentAngle(Xbox.STICK_LEFT, true));
		    swerve.setSpeed(speed);
		    swerve.setSpin(spin);
        }
        else 
        {
            swerve.setRobotCentric();
            speed = ((currentPOV % 90) == 0) ? (0.07) : (0.0);//TODO CONSTANTIZE IT
            speed = (turbo && (currentPOV % 90 == 0)) ? (0.15) : (speed);
            swerve.travelTowards((((double)currentPOV)+180.0)%360.0);
            swerve.setSpeed(speed);
            swerve.setSpin(0.0);
        }

        if (currentPOVGunner == -1) {
            PID.clear("spin");
        }
		
    }

    //RESETS GYRO
    if (driver.getRawButtonPressed(Xbox.BUTTON_START)) 
    {
        gyro.reset();
    }

    swerve.completeLoopUpdate();

    intakeLifter.setDisabled();
//    swerve.setAllModulesToZero();
  }
}

