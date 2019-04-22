/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot.auto.commands;

import com.cyborgcats.reusable.PID;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class OrientRobotCommand extends Command {
  private static final D_Swerve swerve = D_Swerve.getInstance();
  private static final double ANGLE_THRESHOLD = 4.0;
  private final double orientation;
  private double spinError = 180.0;
  private int count = 0;
  
  public OrientRobotCommand(double orientation) {
    this.orientation = orientation;
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    setTimeout(1.5);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    swerve.setFieldCentric();
    swerve.travelTowards(0.0);
    swerve.setSpeed(0.0);
    spinError = swerve.face(((orientation+Robot.GYRO_OFFSET) % 360.0), 0.3);
    swerve.completeLoopUpdate();
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if (Math.abs(spinError) < ANGLE_THRESHOLD) {
      count++;
    }
    return count > 6;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    PID.clear("spin");
    swerve.resetValues();
    swerve.completeLoopUpdate();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
