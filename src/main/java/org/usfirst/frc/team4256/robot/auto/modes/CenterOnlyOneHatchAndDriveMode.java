/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot.auto.modes;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

import org.usfirst.frc.team4256.robot.auto.commands.*;

public class CenterOnlyOneHatchAndDriveMode extends CommandGroup {
  /**
   * Add your docs here.
   */
  public CenterOnlyOneHatchAndDriveMode() {

    addSequential(new LatchHatchCommand());
    addSequential(new DriveForTimeCommand(0.0, 0.6, 1.2));
    addSequential(new WaitCommand(0.6));
    addSequential(new OrientRobotCommand(0.0));
    addSequential(new WaitCommand(0.2));
    addSequential(new DriveWithVisionCommand());
    addSequential(new WaitCommand(0.2));
    addSequential(new ReleaseHatchCommand());
    addSequential(new WaitCommand(0.2));
    addSequential(new DriveForTimeCommand(180.0, 0.4, 0.7));
    addSequential(new WaitCommand(0.2));
    addSequential(new OrientRobotCommand(180.0));
    addSequential(new WaitCommand(0.2));
    addSequential(new DriveForTimeCommand(90.0, 0.7, 1.2));
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // addSequential(new Command2());
    // these will run in order.

    // To run multiple commands at the same time,
    // use addParallel()
    // e.g. addParallel(new Command1());
    // addSequential(new Command2());
    // Command1 and Command2 will run in parallel.

    // A command group will require all of the subsystems that each member
    // would require.
    // e.g. if Command1 requires chassis, and Command2 requires arm,
    // a CommandGroup containing them would require both the chassis and the
    // arm.
  }
}
