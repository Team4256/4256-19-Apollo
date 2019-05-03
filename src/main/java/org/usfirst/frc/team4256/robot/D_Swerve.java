package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.Compass;
import com.cyborgcats.reusable.Drivetrain;
import com.cyborgcats.reusable.PID;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class D_Swerve implements Drivetrain {

	public static enum SwerveMode {
		FIELD_CENTRIC, ROBOT_CENTRIC
	}
	
	private static final double PIVOT_TO_FRONT_X = 8.25,//inches, pivot point to front wheel tip, x
								PIVOT_TO_FRONT_Y = 5.25,//inches, pivot point to front wheel tip, y
								PIVOT_TO_AFT_X   = 8.25,//inches, pivot point to aft wheel tip, x
								PIVOT_TO_AFT_Y   = 5.25;//inches, pivot point to aft wheel tip, y
	private static final double PIVOT_TO_FRONT = Math.hypot(PIVOT_TO_FRONT_X, PIVOT_TO_FRONT_Y),
								PIVOT_TO_AFT = Math.hypot(PIVOT_TO_AFT_X, PIVOT_TO_AFT_Y);
	
	private static D_Swerve instance = null;
	private final SwerveModule moduleA, moduleB, moduleC, moduleD;
	private final SwerveModule[] modules;
	
	private double direction = 0.0, speed = 0.0, spin = 0.0;
	
	private SwerveMode currentSwerveMode = SwerveMode.FIELD_CENTRIC;

	private boolean isInitialized = false;

	private D_Swerve() {
		moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, Parameters.IS_PRACTICE_ROTATOR_A_SENSOR_FLIPPED, Parameters.TRACTION_A_ID, Parameters.IS_PRACTICE_TRACTION_A_MOTOR_FLIPPED, Parameters.PRACTICE_ROTATOR_A_OFFSET_ANGLE);
		moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, Parameters.IS_PRACTICE_ROTATOR_B_SENSOR_FLIPPED, Parameters.TRACTION_B_ID, Parameters.IS_PRACTICE_TRACTION_B_MOTOR_FLIPPED, Parameters.PRACTICE_ROTATOR_B_OFFSET_ANGLE);
		moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, Parameters.IS_PRACTICE_ROTATOR_C_SENSOR_FLIPPED, Parameters.TRACTION_C_ID, Parameters.IS_PRACTICE_TRACTION_C_MOTOR_FLIPPED, Parameters.PRACTICE_ROTATOR_C_OFFSET_ANGLE);
		moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, Parameters.IS_PRACTICE_ROTATOR_D_SENSOR_FLIPPED, Parameters.TRACTION_D_ID, Parameters.IS_PRACTICE_TRACTION_D_MOTOR_FLIPPED, Parameters.PRACTICE_ROTATOR_D_OFFSET_ANGLE);
		modules = new SwerveModule[] {moduleA, moduleB, moduleC, moduleD};
	}

	public synchronized static D_Swerve getInstance() {
		if (instance == null) {
			instance = new D_Swerve();
		}

		return instance;
	}
	
	/**
	 * This function prepares each swerve module individually.
	**/
	@Override
	public synchronized void init() {
		moduleA.init();	
		moduleB.init();
		moduleC.init();	
		moduleD.init();
		isInitialized = true;
	}

	public synchronized boolean isInitialized() {
		return isInitialized;
	}
	
	private synchronized void holonomic_encoderIgnorant(final double direction, double speed, final double spin) {
		//{PREPARE VARIABLES}
		speed = Math.abs(speed);
		final double chassis_fieldAngle = Robot.gyroHeading;
		double forward;
		double strafe;
		if(getSwerveMode() == SwerveMode.ROBOT_CENTRIC) {
			forward = speed*Math.cos(Math.toRadians(direction));
			strafe = speed*Math.sin(Math.toRadians(direction));
		}else {
			forward = speed*Math.cos(Math.toRadians(SwerveModule.convertToRobot(direction, chassis_fieldAngle)));
		    strafe  = speed*Math.sin(Math.toRadians(SwerveModule.convertToRobot(direction, chassis_fieldAngle)));
		}

		final double[] comps_desired = computeComponents(strafe, forward, spin);
		
		final boolean bad = speed == 0.0 && spin == 0.0;
		
		//{CONTROL MOTORS, computing outputs as needed}
		if (!bad) {
			final double[] angles_final = computeAngles(comps_desired);
			for (int i = 0; i < 4; i++) modules[i].swivelTo(angles_final[i]);//control rotation if driver input
		}

		if (!bad && isThere(10.0)) {
			final double[] speeds_final = computeSpeeds(comps_desired);
			for (int i = 0; i < 4; i++) modules[i].set(speeds_final[i]);//control traction if good and there
		}else stop();//otherwise, stop traction

	}
	
	public synchronized void formX() {moduleA.swivelTo(-45.0); moduleB.swivelTo(45.0); moduleC.swivelTo(45.0); moduleD.swivelTo(-45.0);}

	public synchronized boolean isThere(final double threshold) {
		return moduleA.isThere(threshold) && moduleB.isThere(threshold) && moduleC.isThere(threshold) && moduleD.isThere(threshold);
	}

	private synchronized void stop() {for (SwerveModule module : modules) module.set(0.0);}

	@Override
	public synchronized void completeLoopUpdate() {
		holonomic_encoderIgnorant(direction, speed, spin);
		for (SwerveModule module : modules) module.completeLoopUpdate();
	}
	
	//-------------------------------------------------COMPUTATION CODE------------------------------------------
	private synchronized static double[] computeComponents(final double speedX, final double speedY, final double speedSpin) {
		return new double[] {
			speedX + speedSpin*PIVOT_TO_FRONT_Y/PIVOT_TO_FRONT,//moduleAX
			speedY + speedSpin*PIVOT_TO_FRONT_X/PIVOT_TO_FRONT,//moduleAY
			speedX + speedSpin*PIVOT_TO_FRONT_Y/PIVOT_TO_FRONT,//moduleBX
			speedY - speedSpin*PIVOT_TO_FRONT_X/PIVOT_TO_FRONT,//moduleBY
			speedX - speedSpin*PIVOT_TO_AFT_Y/PIVOT_TO_AFT,//moduleCX
			speedY + speedSpin*PIVOT_TO_AFT_X/PIVOT_TO_AFT,//moduleCY
			speedX - speedSpin*PIVOT_TO_AFT_Y/PIVOT_TO_AFT,//moduleDX
			speedY - speedSpin*PIVOT_TO_AFT_X/PIVOT_TO_AFT//moduleDY
		};
	}
	
	
	private synchronized static double[] computeAngles(final double[] moduleComponents) {
		double[] angles = new double[4];
		for (int i = 0; i < 4; i++) angles[i] = Math.toDegrees(Math.atan2(moduleComponents[i*2], moduleComponents[i*2 + 1]));
		return angles;
	}
	
	
	private synchronized static double[] computeSpeeds(final double[] moduleComponents) {
		//don't use for loop because of max divide
		final double speedA = Math.hypot(moduleComponents[0], moduleComponents[1]),
					 speedB = Math.hypot(moduleComponents[2], moduleComponents[3]),
					 speedC = Math.hypot(moduleComponents[4], moduleComponents[5]),
					 speedD = Math.hypot(moduleComponents[6], moduleComponents[7]);
		double max = Math.max(speedA, Math.max(speedB, Math.max(speedC, speedD)));
		if (max < 1.0) {max = 1.0;}
		return new double[] {speedA/max, speedB/max, speedC/max, speedD/max};
	}

	public synchronized void setFieldCentric() {
		currentSwerveMode = SwerveMode.FIELD_CENTRIC;
	}
	public synchronized void setRobotCentric() {
		currentSwerveMode = SwerveMode.ROBOT_CENTRIC;
	}
	public synchronized SwerveMode getSwerveMode() {
		return currentSwerveMode;
	}

	public synchronized SwerveModule[] getSwerveModules() {
		return modules;
	}

	/**
     * Outputs relevant information to the SmartDashboard.
     */
	public synchronized void outputToSmartDashboard() {
		SmartDashboard.putNumber("moduleA Traction Temp (C)", moduleA.getTractionMotor().getMotorTemperature());
		SmartDashboard.putNumber("moduleB Traction Temp (C)", moduleB.getTractionMotor().getMotorTemperature());
		SmartDashboard.putNumber("moduleC Traction Temp (C)", moduleC.getTractionMotor().getMotorTemperature());
		SmartDashboard.putNumber("moduleD Traction Temp (C)", moduleD.getTractionMotor().getMotorTemperature());
	}

	public synchronized void setAllModulesToZero() {
		moduleA.swivelTo(0.0);
		moduleB.swivelTo(0.0);
		moduleC.swivelTo(0.0);
		moduleD.swivelTo(0.0);
	}

	public synchronized void resetValues() {
		direction = 0.0;
		speed = 0.0;
		spin = 0.0;
	}

	
	//------------------------------------------------CONFORMING CODE----------------------------------------
	@Override
	public synchronized void setSpeed(final double speed) {this.speed = speed <= 1.0 ? speed : 1.0;}
	@Override
	public synchronized void setSpin(final double speed) {this.spin = Math.abs(speed) <= 1.0 ? speed : Math.signum(speed);}
	@Override
	public synchronized void travelTowards(final double heading) {this.direction = heading;}

	@Override
	public synchronized void correctFor(final double errorDirection, final double errorMagnitude) {
		travelTowards(errorDirection);
		
		double speed = PID.get("leash", errorMagnitude);//DO NOT use I gain with this because errorMagnitude is always positive
		if (speed > 0.6) speed = 0.6;
		
		setSpeed(speed);
	}
	
	@Override
	public synchronized double face(final double orientation, double maximumOutput) {
		final double error = Compass.path(Robot.gyroHeading, orientation);
		final double spin = PID.get("spin", error);
		setSpin(Math.max(-maximumOutput, Math.min(spin, maximumOutput)));
		return error;
	}
}