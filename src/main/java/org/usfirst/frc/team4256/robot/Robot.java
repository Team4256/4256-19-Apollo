/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.cameraserver.CameraServer;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import java.lang.System;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private MjpegServer server;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    UsbCamera usbCamera = new UsbCamera("USB Camera 0", 0);
    usbCamera.setResolution(640, 360);
    usbCamera.setFPS(60);
    server = new MjpegServer("USB Camera 0 out", 1181);
    server.setSource(usbCamera);
  }

  public void robotInit_frame_by_frame() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(0);
    camera.setResolution(1920, 1080);
    CvSink cvSink = CameraServer.getInstance().getVideo();
    CvSource outputStream = CameraServer.getInstance().putVideo("Black and White", 480, 270);
    Mat source = new Mat();
    Mat rescaled = new Mat();
    Mat output = new Mat();

    while(!Thread.interrupted()) {
      long beforeGrabTime = System.currentTimeMillis();
      cvSink.grabFrame(source);
      long grabDuration = System.currentTimeMillis() - beforeGrabTime;

      long beforeResizeTime = System.currentTimeMillis();
      Imgproc.resize(source, rescaled, new Size(480,270));
      long resizeDuration = System.currentTimeMillis() - beforeResizeTime;

      long beforeConvertTime = System.currentTimeMillis();
      Imgproc.cvtColor(rescaled, output, Imgproc.COLOR_BGR2GRAY);
      long convertDuration = System.currentTimeMillis() - beforeConvertTime;

      long beforePutFrameTime = System.currentTimeMillis();
      outputStream.putFrame(output);
      long putFrameDuration = System.currentTimeMillis() - beforePutFrameTime;

      SmartDashboard.putNumber("grab duration", grabDuration);
      SmartDashboard.putNumber("resize duration", resizeDuration);
      SmartDashboard.putNumber("convert duration", convertDuration);
      SmartDashboard.putNumber("putFrame duration", putFrameDuration);
    }
    
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
