/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot.auto.commands;

import org.usfirst.frc.team4256.robot.HatchIntake;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Add your docs here.
 */
public class ReleaseHatchCommand extends InstantCommand {
  private static final HatchIntake hatchIntake = HatchIntake.getInstance();

  public ReleaseHatchCommand() {
    super();
    requires(hatchIntake);
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }

  // Called once when the command executes
  @Override
  protected void initialize() {
    hatchIntake.release();
  }

}
