/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.Gyro;
import org.usfirst.frc.team4256.robot.SwerveModule;
import com.cyborgcats.reusable.Xbox;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends TimedRobot {

  //TODO Check a b c d location
//  private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, true, 26.0);
//  private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, true, -49.0);
//  private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, true, 51.0);
//  private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, true, -2.0);
  private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, true, Parameters.TRACTION_A_ID, true, -26.0);
  private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, true, Parameters.TRACTION_B_ID, true, 49.0);
  private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, true, Parameters.TRACTION_C_ID, true, -51.0);
  private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, true, Parameters.TRACTION_D_ID, true, 2.0);
  private static final D_Swerve swerve = new D_Swerve(moduleA, moduleB, moduleC, moduleD);
  private final BallIntake ballIntake = new BallIntake(Parameters.BALL_INTAKE_MOTOR_ID, Parameters.BALL_INTAKE_SENSOR_ID);
  private final Xbox driver = new Xbox(0);
  private boolean slurp = false;
  private boolean spit = false;

  private static final Gyro gyro = new Gyro(Parameters.GYRO_UPDATE_HZ);
  public static double gyroHeading = 0.0;

  public static void updateGyroHeading() {
    gyroHeading = gyro.getCurrentAngle();
  }

  @Override
  public void robotInit() {
    gyro.reset();
    swerve.init();
    moduleA.rotationMotor().setInverted(true);//TODO find better place to put this
    moduleB.rotationMotor().setInverted(true);//TODO find better place to put this
    moduleC.rotationMotor().setInverted(true);//TODO find better place to put this
    moduleD.rotationMotor().setInverted(true);//TODO find better place to put this
//    moduleA.init(false);
//    moduleB.init(false);
//    moduleC.init(false);
//    moduleD.init(false);
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
    if (driver.getRawButton(Xbox.BUTTON_A)) {//TODO use actual buttons on the actual controller
      slurp = ballIntake.hasBall() ? false : !slurp;
      if (slurp) {
        spit = false;
      }
    }else if (driver.getRawButton(Xbox.BUTTON_B)) {//TODO use actual buttons on the actual controller
      spit = !spit;
      if (spit) {
        slurp = false;
      }
    }

    ballIntake.completeLoopUpdate(spit, slurp);

    //{speed multipliers}
		final boolean turbo = driver.getRawButton(Xbox.BUTTON_STICK_LEFT);
		final boolean snail = driver.getRawButton(Xbox.BUTTON_STICK_RIGHT);
		
		//{calculating speed}
		double speed = driver.getCurrentRadius(Xbox.STICK_LEFT, true);//turbo mode
		if (!turbo) speed *= 0.7;//---------------------------------------normal mode
  	if (snail)  speed *= 0.5;//---------------------------------------snail mode
		speed *= speed;
		
		//{calculating spin}
		double spin = 0.7*driver.getDeadbandedAxis(Xbox.AXIS_RIGHT_X);//normal mode
		if (snail) spin  *= 0.7;//----------------------------------------snail mode
		spin *= spin*Math.signum(spin);
		
		if (driver.getRawButton(Xbox.BUTTON_X)) swerve.formX();//X lock
		else {//SWERVE DRIVE
			swerve.travelTowards(driver.getCurrentAngle(Xbox.STICK_LEFT, true));
			swerve.setSpeed(speed);
			swerve.setSpin(spin);
    }
    SmartDashboard.putNumber("DesiredAngle", driver.getCurrentAngle(Xbox.STICK_LEFT, true));
    SmartDashboard.putNumber("moduleA Angle", moduleA.rotationMotor().getCurrentAngle(true));
    SmartDashboard.putNumber("moduleB Angle", moduleB.rotationMotor().getCurrentAngle(true));
    SmartDashboard.putNumber("moduleC Angle", moduleC.rotationMotor().getCurrentAngle(true));
    SmartDashboard.putNumber("moduleD Angle", moduleD.rotationMotor().getCurrentAngle(true));
    SmartDashboard.putNumber("moduleA Error", moduleA.rotationMotor().getCurrentError(true));
    SmartDashboard.putNumber("moduleB Error", moduleB.rotationMotor().getCurrentError(true));
    SmartDashboard.putNumber("moduleC Error", moduleC.rotationMotor().getCurrentError(true));
    SmartDashboard.putNumber("moduleD1 Error", moduleD.rotationMotor().getCurrentError(true));
    swerve.completeLoopUpdate();
  }


  @Override
  public void testPeriodic() {
    if (driver.getRawButton(Xbox.BUTTON_A)) {
      moduleA.swivelTo(180);
      moduleB.swivelTo(180);
      moduleC.swivelTo(180);
      moduleD.swivelTo(180);
    }
    if (driver.getRawButton(Xbox.BUTTON_B)) {
      moduleA.swivelTo(90);
      moduleB.swivelTo(90);
      moduleC.swivelTo(90);
      moduleD.swivelTo(90);
    }
    if (driver.getRawButton(Xbox.BUTTON_X)) {
      moduleA.swivelTo(270);
      moduleB.swivelTo(270);
      moduleC.swivelTo(270);
      moduleD.swivelTo(270);
    }
    if (driver.getRawButton(Xbox.BUTTON_Y)) {
      moduleA.swivelTo(0);
      moduleB.swivelTo(0);
      moduleC.swivelTo(0);
      moduleD.swivelTo(0);
    }
    SmartDashboard.putNumber("moduleA Error", moduleA.rotationMotor().getCurrentError(true));
    SmartDashboard.putNumber("moduleB Error", moduleB.rotationMotor().getCurrentError(true));
    SmartDashboard.putNumber("moduleC Error", moduleC.rotationMotor().getCurrentError(true));
    SmartDashboard.putNumber("moduleD Error", moduleD.rotationMotor().getCurrentError(true));    
    
  }
}