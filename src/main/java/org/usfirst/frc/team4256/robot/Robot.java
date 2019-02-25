/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.Gyro;

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

  private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, true, 76.0);//PRACTICE BOT
  private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, false, 87.0);//PRACTICE BOT
  private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, false, -31.0);//PRACTICE BOT
  private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, true, 5.0);//PRACTICE BOT
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
  public static double gyroHeading = 0.0;
  private static NetworkTableInstance nt;
  private static NetworkTable apollo;

  public static void updateGyroHeading() {
    gyroHeading = gyro.getCurrentAngle();
  }

  @Override
  public void robotInit() {
    gyro.reset();

    nt = NetworkTableInstance.getDefault();
    apollo = nt.getTable("Apollo");

    swerve.init();
    intakeLifter.init();
    moduleA.rotationMotor().setInverted(true);//TODO find better place to put this
    moduleB.rotationMotor().setInverted(true);//TODO find better place to put this
    moduleC.rotationMotor().setInverted(true);//TODO find better place to put this
    moduleD.rotationMotor().setInverted(true);//TODO find better place to put this
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
    apollo.getEntry("ModuleA Angle").setNumber(moduleA.rotationMotor().getCurrentAngle(true));
    apollo.getEntry("ModuleB Angle").setNumber(moduleB.rotationMotor().getCurrentAngle(true));
    apollo.getEntry("ModuleC Angle").setNumber(moduleC.rotationMotor().getCurrentAngle(true));
    apollo.getEntry("ModuleD Angle").setNumber(moduleD.rotationMotor().getCurrentAngle(true));
    apollo.getEntry("ModuleA Traction Temperature (C)").setNumber(moduleA.tractionMotor().getMotorTemperature());
    apollo.getEntry("ModuleB Traction Temperature (C)").setNumber(moduleB.tractionMotor().getMotorTemperature());
    apollo.getEntry("ModuleC Traction Temperature (C)").setNumber(moduleC.tractionMotor().getMotorTemperature());
    apollo.getEntry("ModuleD Traction Temperature (C)").setNumber(moduleD.tractionMotor().getMotorTemperature());
    apollo.getEntry("ModuleA Traction RPM").setNumber(moduleA.tractionMotor().getRPM());
    apollo.getEntry("ModuleB Traction RPM").setNumber(moduleB.tractionMotor().getRPM());
    apollo.getEntry("ModuleC Traction RPM").setNumber(moduleC.tractionMotor().getRPM());
    apollo.getEntry("ModuleD Traction RPM").setNumber(moduleD.tractionMotor().getRPM());
    apollo.getEntry("CURRENT POV").setNumber(driver.getPOV());
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
        intakeLifter.increment(5.0);//MOVE DOWN
    }
    else if (gunner.getRawButtonPressed(Xbox.BUTTON_LB))
    {//UP
        intakeLifter.decrement(5.0);//MOVE UP
    }

    //SET
    if (driver.getRawButtonPressed(Xbox.BUTTON_A)) 
    {//DOWN
        intakeLifter.setAngle(170.0);//DOWN POSITION
    }
    else if (driver.getRawButtonPressed(Xbox.BUTTON_Y)) 
    {//UP
        intakeLifter.setAngle(0.0);//UP POSITION
    }
    else if (driver.getRawButtonPressed(Xbox.BUTTON_X)) 
    {   //UP
        intakeLifter.setAngle(103.0);//CARGO BAY
    }
    else if (driver.getRawButtonPressed(Xbox.BUTTON_B)) 
    {   //UP
        intakeLifter.setAngle(20.0);//ROCKETSHIP
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
        speed *= 0.5 * speed;//---------------------------------------normal mode
    }
		
    //{calculating spin}
	double spin = 0.5*driver.getDeadbandedAxis(Xbox.AXIS_RIGHT_X);//normal mode
    if (snail) 
    {
        spin  *= 0.3;//----------------------------------------snail mode
    }
        spin *= spin*Math.signum(spin);
        
        swerve.setFieldCentric();
    if (driver.getRawButton(Xbox.BUTTON_X)) 
    {
        swerve.formX();//X lock
    }
    else 
    {//SWERVE DRIVE
        int currentPOV = driver.getPOV();
        if (currentPOV == -1) {
            swerve.travelTowards(driver.getCurrentAngle(Xbox.STICK_LEFT, true));
		    swerve.setSpeed(speed);
		    swerve.setSpin(spin);
        }
        else 
        {
            swerve.setRobotCentric();
            speed = ((currentPOV % 90) == 0) ? 0.05 : 0.0;//TODO CONSTANTIZE IT
            swerve.travelTowards((double)currentPOV);
            swerve.setSpeed(speed);
            swerve.setSpin(0.0);
        }
		
    }

    //RESETS GYRO
    if (driver.getRawButtonPressed(Xbox.BUTTON_START)) 
    {
        gyro.reset();
    }

    swerve.completeLoopUpdate();
  }

  @Override
  public void testPeriodic() {
    //INTAKE LIFTER
    intakeLifter.checkForEncoderSpike();//BEGIN LOOP UPDATING
    intakeLifter.checkLimitSwitchUpdate();//BEGIN LOOP UPDATING

    //INCREMENT
    if (gunner.getRawButtonPressed(Xbox.BUTTON_RB)) 
    {//DOWN
        double incrementAmount = (intakeLifter.isOverrideMode()) ? IntakeLifter.OVERRIDE_INCREMENT : IntakeLifter.NORMAL_INCREMENT; 
        intakeLifter.increment(incrementAmount);//MOVE DOWN
    }
    else if (gunner.getRawButtonPressed(Xbox.BUTTON_LB))
    {//UP
        double decrementAmount = (intakeLifter.isOverrideMode()) ? IntakeLifter.OVERRIDE_DECREMENT : IntakeLifter.NORMAL_DECREMENT;
        intakeLifter.decrement(decrementAmount);//MOVE UP
    }

    //SET
    if (driver.getRawButtonPressed(Xbox.BUTTON_A)) 
    {
        intakeLifter.setAngle(IntakeLifter.POSITION_DOWN);//DOWN POSITION
    }
    else if (driver.getRawButtonPressed(Xbox.BUTTON_Y)) 
    {
        intakeLifter.setAngle(IntakeLifter.POSITION_UP);//UP POSITION
    }
    else if (driver.getRawButtonPressed(Xbox.BUTTON_X)) 
    {
        intakeLifter.setAngle(IntakeLifter.POSITION_ROCKETSHIP);//ROCKETSHIP
    }
    else if (driver.getRawButtonPressed(Xbox.BUTTON_B)) 
    {
        intakeLifter.setAngle(IntakeLifter.POSITION_CARGOSHIP);//CARGOSHIP
    }

    intakeLifter.checkAngle();//COMPLETE LOOP UPDATE

    if (gunner.getRawButton(Xbox.BUTTON_START) && gunner.getRawButton(Xbox.BUTTON_BACK))//Gunner start and back simultaneously
    {
        intakeLifter.enableOverrideMode();
    }
    else if (gunner.getRawButton(Xbox.BUTTON_STICK_LEFT) && gunner.getRawButton(Xbox.BUTTON_STICK_RIGHT))//Gunner left and right stick pressed simultaneously
    {
        intakeLifter.disableOverrideMode();    
    }
//    swerve.setAllModulesToZero();
  }
}

