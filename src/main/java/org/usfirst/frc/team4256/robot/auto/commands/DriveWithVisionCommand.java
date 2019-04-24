/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot.auto.commands;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.Limelight;

import edu.wpi.first.wpilibj.command.Command;

public class DriveWithVisionCommand extends Command {
  private static final D_Swerve swerve = D_Swerve.getInstance();
  private static final Limelight limelight = Limelight.getInstance();
  private int count = 0;

  public DriveWithVisionCommand() {
    requires(swerve);
    requires(limelight);
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    setTimeout(4);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    limelight.updateVisionTracking();
    swerve.setRobotCentric();
    swerve.travelTowards(limelight.getCommandedDirection());
    swerve.setSpeed(limelight.getCommandedSpeed());
    swerve.setSpin(limelight.getCommandedSpin());
    swerve.completeLoopUpdate();
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if (!limelight.hasTarget()) {
      count++;
    }
    return count > 5;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    swerve.setSpeed(0.0);
    swerve.setSpin(0.0);
    swerve.completeLoopUpdate();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
