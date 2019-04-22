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
	
	private double moduleD_maxSpeed = 70.0;//always put max slightly higher than max observed
	private double moduleD_previousAngle = 0.0;
	private double previousSpin = 0.0;
	
	private double direction = 0.0, speed = 0.0, spin = 0.0;
	
	private SwerveMode currentSwerveMode = SwerveMode.FIELD_CENTRIC;

	private boolean isInitialized = false;

	private D_Swerve() {
		moduleA = new SwerveModule(Parameters.ROTATOR_A_ID, Parameters.IS_ROTATOR_A_SENSOR_FLIPPED, Parameters.TRACTION_A_ID, Parameters.IS_TRACTION_A_MOTOR_FLIPPED, Parameters.ROTATOR_A_OFFSET_ANGLE);
		moduleB = new SwerveModule(Parameters.ROTATOR_B_ID, Parameters.IS_ROTATOR_B_SENSOR_FLIPPED, Parameters.TRACTION_B_ID, Parameters.IS_TRACTION_B_MOTOR_FLIPPED, Parameters.ROTATOR_B_OFFSET_ANGLE);
		moduleC = new SwerveModule(Parameters.ROTATOR_C_ID, Parameters.IS_ROTATOR_C_SENSOR_FLIPPED, Parameters.TRACTION_C_ID, Parameters.IS_TRACTION_C_MOTOR_FLIPPED, Parameters.ROTATOR_C_OFFSET_ANGLE);
		moduleD = new SwerveModule(Parameters.ROTATOR_D_ID, Parameters.IS_ROTATOR_D_SENSOR_FLIPPED, Parameters.TRACTION_D_ID, Parameters.IS_TRACTION_D_MOTOR_FLIPPED, Parameters.ROTATOR_D_OFFSET_ANGLE);
		modules = new SwerveModule[] {moduleA, moduleB, moduleC, moduleD};
	}

	public static D_Swerve getInstance() {
		if (instance == null) {
			instance = new D_Swerve();
		}

		return instance;
	}
	
	/**
	 * This function prepares each swerve module individually.
	**/
	@Override
	public void init() {
		moduleA.init();	
		moduleB.init();
		moduleC.init();	
		moduleD.init();
		isInitialized = true;
	}

	public boolean isInitialized() {
		return isInitialized;
	}
	
	/**
	 * @deprecated
	 * This function is now deprecated.
	 *{@link #holonomic_encoderIgnorant(double, double, double)} is the new version of this.
	 * Use this instead.
	 * @param direction
	 * @param speed
	 * @param spin
	 */
	@Deprecated
	public void holonomic(final double direction, double speed, final double spin) {
		//{PREPARE VARIABLES}
		speed = Math.abs(speed);
		final double chassis_fieldAngle = Robot.gyroHeading;
		final double forward = speed*Math.cos(Math.toRadians(SwerveModule.convertToRobot(direction, chassis_fieldAngle))),
					 strafe  = speed*Math.sin(Math.toRadians(SwerveModule.convertToRobot(direction, chassis_fieldAngle)));
		final double[] comps_desired = computeComponents(strafe, forward, spin);
		final boolean bad = speed == 0.0 && spin == 0.0;
		
		//{GET ENCODER SPEED}
		final double[] speeds_actual = speedsFromModuleD();
		double speed_actual = Math.hypot(speeds_actual[0], speeds_actual[1]);
		speed_actual = Math.floor(speed_actual*10.0)/10.0;
		
		//{COMPUTE ANGLES}
		final double[] angles_final;
		if ((speed < speed_actual) && (speed_actual > .1)) {
			final double[] angles_desired = computeAngles(comps_desired);
			final double stdd_desired = Compass.stdd(angles_desired);
			
			final double[] angles_actual = computeAngles(computeComponents(speeds_actual[0], speeds_actual[1], spin));
			final double stdd_actual = Compass.stdd(angles_actual);
			
			angles_final = stdd_desired > stdd_actual ? angles_actual : angles_desired;
		}else {
			angles_final = computeAngles(comps_desired);
		}
		
		//{CONTROL MOTORS, using above angles and computing traction outputs as needed}
		if (!bad) {
			for (int i = 0; i < 4; i++) modules[i].swivelTo(angles_final[i]);//control rotation if good
			moduleD_previousAngle = angles_final[3];
		}
		
		if (!bad && isThere(6.0)) {
			final double[] speeds_final = computeSpeeds(comps_desired);
			for (int i = 0; i < 4; i++) modules[i].set(speeds_final[i]);//control traction if good and there
		}else stop();//otherwise, stop traction
		
		if (spin < 0.07) moduleD.checkTractionEncoder();
		
		//{UPDATE RECORDS}
		previousSpin = spin;
	}
	
	
	private void holonomic_encoderIgnorant(final double direction, double speed, final double spin) {
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
		
		if (spin < 0.07) moduleD.checkTractionEncoder();
	}


	
	private double[] speedsFromModuleD() {
		double rawSpeed = moduleD.tractionSpeed()*moduleD.getDecapitated();
		if (Math.abs(rawSpeed) > moduleD_maxSpeed) moduleD_maxSpeed = Math.abs(rawSpeed);
		rawSpeed /= moduleD_maxSpeed;
		
		final double angle = Math.toRadians(moduleD_previousAngle);
		
		final double drivetrainX = /*linear*/rawSpeed*Math.sin(angle) + /*rotational*/previousSpin*PIVOT_TO_AFT_Y/PIVOT_TO_AFT*Math.signum(rawSpeed);
		final double drivetrainY = /*linear*/rawSpeed*Math.cos(angle) + /*rotational*/previousSpin*PIVOT_TO_AFT_X/PIVOT_TO_AFT*Math.signum(rawSpeed);
		
		return new double[] {drivetrainX, drivetrainY};
	}
	
	public void formX() {moduleA.swivelTo(-45.0); moduleB.swivelTo(45.0); moduleC.swivelTo(45.0); moduleD.swivelTo(-45.0);}

	public boolean isThere(final double threshold) {
		return moduleA.isThere(threshold) && moduleB.isThere(threshold) && moduleC.isThere(threshold) && moduleD.isThere(threshold);
	}

	private void stop() {for (SwerveModule module : modules) module.set(0.0);}

	@Override
	public void completeLoopUpdate() {
		holonomic_encoderIgnorant(direction, speed, spin);
		for (SwerveModule module : modules) module.completeLoopUpdate();
	}
	
	
	
	//-------------------------------------------------COMPUTATION CODE------------------------------------------
	private static double[] computeComponents(final double speedX, final double speedY, final double speedSpin) {
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
	
	
	private static double[] computeAngles(final double[] moduleComponents) {
		double[] angles = new double[4];
		for (int i = 0; i < 4; i++) angles[i] = Math.toDegrees(Math.atan2(moduleComponents[i*2], moduleComponents[i*2 + 1]));
		return angles;
	}
	
	
	private static double[] computeSpeeds(final double[] moduleComponents) {
		//don't use for loop because of max divide
		final double speedA = Math.hypot(moduleComponents[0], moduleComponents[1]),
					 speedB = Math.hypot(moduleComponents[2], moduleComponents[3]),
					 speedC = Math.hypot(moduleComponents[4], moduleComponents[5]),
					 speedD = Math.hypot(moduleComponents[6], moduleComponents[7]);
		double max = Math.max(speedA, Math.max(speedB, Math.max(speedC, speedD)));
		if (max < 1.0) {max = 1.0;}
		return new double[] {speedA/max, speedB/max, speedC/max, speedD/max};
	}

	public void setFieldCentric() {
		currentSwerveMode = SwerveMode.FIELD_CENTRIC;
	}
	public void setRobotCentric() {
		currentSwerveMode = SwerveMode.ROBOT_CENTRIC;
	}
	public SwerveMode getSwerveMode() {
		return currentSwerveMode;
	}

	public SwerveModule[] getSwerveModules() {
		return modules;
	}

	/**
     * Outputs relevant information to the SmartDashboard.
     */
	public void outputToSmartDashboard() {
		SmartDashboard.putNumber("moduleA Traction Temp (C)", moduleA.getTractionMotor().getMotorTemperature());
		SmartDashboard.putNumber("moduleB Traction Temp (C)", moduleB.getTractionMotor().getMotorTemperature());
		SmartDashboard.putNumber("moduleC Traction Temp (C)", moduleC.getTractionMotor().getMotorTemperature());
		SmartDashboard.putNumber("moduleD Traction Temp (C)", moduleD.getTractionMotor().getMotorTemperature());
	}

	public void setAllModulesToZero() {
		moduleA.swivelTo(0.0);
		moduleB.swivelTo(0.0);
		moduleC.swivelTo(0.0);
		moduleD.swivelTo(0.0);
	}

	public void resetValues() {
		direction = 0.0;
		speed = 0.0;
		spin = 0.0;
	}

	
	//------------------------------------------------CONFORMING CODE----------------------------------------
	@Override
	public void setSpeed(final double speed) {this.speed = speed <= 1.0 ? speed : 1.0;}
	@Override
	public void setSpin(final double speed) {this.spin = Math.abs(speed) <= 1.0 ? speed : Math.signum(speed);}
	@Override
	public void travelTowards(final double heading) {this.direction = heading;}

	@Override
	public void correctFor(final double errorDirection, final double errorMagnitude) {
		travelTowards(errorDirection);
		
		double speed = PID.get("leash", errorMagnitude);//DO NOT use I gain with this because errorMagnitude is always positive
		if (speed > 0.6) speed = 0.6;
		
		setSpeed(speed);
	}
	
	@Override
	public double face(final double orientation, double maximumOutput) {
		final double error = Compass.path(Robot.gyroHeading, orientation);
		final double spin = PID.get("spin", error);
		setSpin(Math.max(-maximumOutput, Math.min(spin, maximumOutput)));
		return error;
	}
}