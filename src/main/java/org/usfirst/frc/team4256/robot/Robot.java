/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.Gyro;
import org.usfirst.frc.team4256.robot.SwerveModule;
import com.cyborgcats.reusable.Xbox;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

public class Robot extends TimedRobot {

  private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, true, -26.0);
  private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, true, 49.0);
  private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, true, -51.0);
  private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, true, 2.0);
  private static final D_Swerve swerve = new D_Swerve(moduleA, moduleB, moduleC, moduleD);
  private static final IntakeLifter intakeLifter = new IntakeLifter(Parameters.LIFTER_MASTER_ID, Parameters.LIFTER_FOLLOWER_1_ID, Parameters.LIFTER_FOLLOWER_2_ID, Parameters.LIFTER_FOLLOWER_3_ID, false, false, true, true, Parameters.LIMIT_SWTICH_ID);
  private static final BallIntake ballIntake = new BallIntake(Parameters.BALL_INTAKE_MOTOR_ID, Parameters.BALL_INTAKE_SENSOR_ID);
  private static final HatchIntake hatchIntake = new HatchIntake(Parameters.HATCHSOLENOID_FORWARD_CHANNEL, Parameters.HATCHSOLENOID_REVERSE_CHANNEL);
  private static final Xbox driver = new Xbox(0);
  private static final Xbox gunner = new Xbox(1);

  private static final Gyro gyro = new Gyro(Parameters.GYRO_UPDATE_HZ);
  public static double gyroHeading = 0.0;

  public static void updateGyroHeading() {
    gyroHeading = gyro.getCurrentAngle();
  }

  @Override
  public void robotInit() {
    gyro.reset();
    swerve.init();
    intakeLifter.init();
    moduleA.rotationMotor().setInverted(true);//TODO find better place to put this
    moduleB.rotationMotor().setInverted(true);//TODO find better place to put this
    moduleC.rotationMotor().setInverted(true);//TODO find better place to put this
    moduleD.rotationMotor().setInverted(true);//TODO find better place to put this
//    moduleA.init(false);
//    moduleB.init(false);
//    moduleC.init(false);
//    moduleD.init(false);
	  
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
    if (driver.getAxisPress(Xbox.AXIS_LT, 0.1)) {
      ballIntake.spit();
    }else if (driver.getAxisPress(Xbox.AXIS_RT, 0.1) && !ballIntake.hasBall()) {
      ballIntake.slurp();
    }else {
      ballIntake.stop();
    }

    //INTAKE LIFTER
    if (!(intakeLifter.isClimbMode())) 
    {//NOT CLIMB MODE
      intakeLifter.checkForEncoderSpike();
      if (gunner.getRawButtonPressed(Xbox.BUTTON_A)) 
      {//DOWN
        intakeLifter.increment(5.0);
      }
      else if (gunner.getRawButtonPressed(Xbox.BUTTON_Y))
      {//UP
        intakeLifter.decrement(5.0);
      }
      else if (driver.getRawButtonPressed(Xbox.BUTTON_A)) 
      {//DOWN
        intakeLifter.setAngle(170.0);
      }
      else if (driver.getRawButtonPressed(Xbox.BUTTON_Y)) 
      {//UP
        intakeLifter.setAngle(0.0);
      }

      intakeLifter.checkAngle();
    }
    else 
    {//CLIMB MODE
      intakeLifter.climb(0.0);
    }
    
    
    
  
    //HATCH INTAKE
    if (driver.getRawButton(Xbox.BUTTON_LB)) 
    {
      hatchIntake.open();
    }
    else if (driver.getRawButton(Xbox.BUTTON_RB)) 
    {
      hatchIntake.close();
    }

    //{speed multipliers}
		final boolean turbo = driver.getRawButton(Xbox.BUTTON_STICK_LEFT);
		final boolean snail = driver.getRawButton(Xbox.BUTTON_STICK_RIGHT);
		
		//{calculating speed}
		double speed = driver.getCurrentRadius(Xbox.STICK_LEFT, true);//turbo mode
    if (turbo)
    {
      speed *= 1.0;//---------------------------------------turbo mode
    } 
    else if(snail) 
    {
      speed *= 0.5;//---------------------------------------snail mode
    }
    else 
    {
      speed *= 0.7;//---------------------------------------normal mode
    }
		
		//{calculating spin}
		double spin = 0.7*driver.getDeadbandedAxis(Xbox.AXIS_RIGHT_X);//normal mode
    if (snail) 
    {
      spin  *= 0.7;//----------------------------------------snail mode
    }
		spin *= spin*Math.signum(spin);
		
		if (driver.getRawButton(Xbox.BUTTON_X)) {
      swerve.formX();//X lock
    }
    else 
    {//SWERVE DRIVE
			swerve.travelTowards(driver.getCurrentAngle(Xbox.STICK_LEFT, true));
			swerve.setSpeed(speed);
			swerve.setSpin(spin);
    }

    if (gunner.getRawButtonPressed(Xbox.BUTTON_START)) {
      gyro.reset();
    }

    SmartDashboard.putNumber("Current Current", intakeLifter.getMaster().getOutputCurrent());
    SmartDashboard.putNumber("Current Bus Voltage", intakeLifter.getMaster().getBusVoltage());
    SmartDashboard.putNumber("Desired Angle", intakeLifter.getDesiredDegrees());
    SmartDashboard.putNumber("Error", intakeLifter.getMaster().getClosedLoopError());
    SmartDashboard.putNumber("Lifter Angle", intakeLifter.getCurrentAngle());
    SmartDashboard.putBoolean("Is Lifter Disabled", intakeLifter.getMaster().getControlMode() == ControlMode.Disabled);
    SmartDashboard.putNumber("Lifter Encoder Count", intakeLifter.getMaster().getSelectedSensorPosition(0));
    /*
    SmartDashboard.putNumber("DesiredAngle", driver.getCurrentAngle(Xbox.STICK_LEFT, true));
    SmartDashboard.putNumber("moduleA Angle", moduleA.rotationMotor().getCurrentAngle(true));
    SmartDashboard.putNumber("moduleB Angle", moduleB.rotationMotor().getCurrentAngle(true));
    SmartDashboard.putNumber("moduleC Angle", moduleC.rotationMotor().getCurrentAngle(true));
    SmartDashboard.putNumber("moduleD Angle", moduleD.rotationMotor().getCurrentAngle(true));
    SmartDashboard.putNumber("moduleA Error", moduleA.rotationMotor().getCurrentError(true));
    SmartDashboard.putNumber("moduleB Error", moduleB.rotationMotor().getCurrentError(true));
    SmartDashboard.putNumber("moduleC Error", moduleC.rotationMotor().getCurrentError(true));
    SmartDashboard.putNumber("moduleD1 Error", moduleD.rotationMotor().getCurrentError(true));
    */
    swerve.completeLoopUpdate();
  }

  static boolean isCalibrating = false;
  @Override
  public void testPeriodic() {
      
    
    intakeLifter.setDisabled();

    SmartDashboard.putNumber("ENCODER COUNTS", intakeLifter.getMaster().getSelectedSensorPosition());
    SmartDashboard.putBoolean("LIMIT SWITCH", intakeLifter.getLimitSwitch());
    SmartDashboard.putNumber("LIFTER ANGLE", intakeLifter.getCurrentAngle());
    SmartDashboard.putNumber("LIFTER ERROR COUNTS", intakeLifter.getMaster().getClosedLoopError());
    SmartDashboard.putBoolean("IS LIFTER DISABLED", intakeLifter.getMaster().getControlMode() == ControlMode.Disabled);
    SmartDashboard.putNumber("DESIRED DEGREES", intakeLifter.getDesiredDegrees());
  }
}
