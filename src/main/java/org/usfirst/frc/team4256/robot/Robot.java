/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.Gyro;
import org.usfirst.frc.team4256.robot.SwerveModule;

import edu.wpi.first.wpilibj.TimedRobot;


public class Robot extends TimedRobot {

  private static final SwerveModule moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, false, Parameters.TRACTION_A_ID, false, 0);
  private static final SwerveModule moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, false, Parameters.TRACTION_B_ID, false, 0);
  private static final SwerveModule moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, false, Parameters.TRACTION_C_ID, false, 0);
  private static final SwerveModule moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, false, Parameters.TRACTION_D_ID, false, 0);
  private static final D_Swerve swerve = new D_Swerve(moduleA, moduleB, moduleC, moduleD);

  private static final Gyro gyro = new Gyro(Parameters.GYRO_UPDATE_HZ);//TODO add parameters as a byte
  public static double gyroHeading = 0.0;

  public static void updateGyroHeading() {
    gyroHeading = gyro.getCurrentAngle();
  }

  @Override
  public void robotInit() {
    gyro.reset();
    swerve.init();
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
  }


  @Override
  public void testPeriodic() {
    moduleA.swivelTo(0);
    moduleB.swivelTo(0);
    moduleC.swivelTo(0);
    moduleD.swivelTo(0);
  }
}