/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot.auto.commands;

import org.usfirst.frc.team4256.robot.D_Swerve;

import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Add your docs here.
 */
public class DriveForTimeCommand extends TimedCommand {
  private static final D_Swerve swerve = D_Swerve.getInstance();
  private final double direction;
  private final double speed;

  public DriveForTimeCommand(double direction, double speed, double timeout) {
    super(timeout);
    this.direction = direction;
    this.speed = speed;
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    swerve.setFieldCentric();
    swerve.setSpeed(speed);
    swerve.setSpin(0.0);
    swerve.travelTowards(direction);
    swerve.completeLoopUpdate();
  }

  // Called once after timeout
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
