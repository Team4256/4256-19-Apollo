package org.usfirst.frc.team4256.robot;

public final class Parameters {
    
    public static final byte GYRO_UPDATE_HZ = 50;

    //DIO
    public static final int LIMIT_SWTICH_LIFTER = 0;
    public static final int BALL_INTAKE_SENSOR = 1;
    public static final int TX2_POWER_SENSOR = 2;
    public static final int TX2_POWER_CONTROL = 3;
    public static final int LIMIT_SWITCH_GROUND_INTAKE = 4;

    //SWERVE MOTORS
    public static final int ROTATOR_A_ID = 11;//FRONT LEFT
    public static final int ROTATOR_B_ID = 12;//FRONT RIGHT
    public static final int ROTATOR_C_ID = 13;//AFT LEFT
    public static final int ROTATOR_D_ID = 14;//AFT RIGHT

    public static final int TRACTION_A_ID = 21;//FRONT LEFT
    public static final int TRACTION_B_ID = 22;//FRONT RIGHT
    public static final int TRACTION_C_ID = 23;//AFT LEFT
    public static final int TRACTION_D_ID = 24;//AFT RIGHT

    //OTHER SWERVE STUFF
    public static final boolean IS_PRACTICE_ROTATOR_A_SENSOR_FLIPPED = true;
    public static final boolean IS_PRACTICE_ROTATOR_B_SENSOR_FLIPPED = true;
    public static final boolean IS_PRACTICE_ROTATOR_C_SENSOR_FLIPPED = true;
    public static final boolean IS_PRACTICE_ROTATOR_D_SENSOR_FLIPPED = true;

    public static final boolean IS_PRACTICE_TRACTION_A_MOTOR_FLIPPED = true;
    public static final boolean IS_PRACTICE_TRACTION_B_MOTOR_FLIPPED = true;
    public static final boolean IS_PRACTICE_TRACTION_C_MOTOR_FLIPPED = false;
    public static final boolean IS_PRACTICE_TRACTION_D_MOTOR_FLIPPED = true;

    public static final double PRACTICE_ROTATOR_A_OFFSET_ANGLE = 236.60;
    public static final double PRACTICE_ROTATOR_B_OFFSET_ANGLE = 337.15;
    public static final double PRACTICE_ROTATOR_C_OFFSET_ANGLE = 106.17;
    public static final double PRACTICE_ROTATOR_D_OFFSET_ANGLE = 2.11;

    public static final boolean IS_COMPETITION_ROTATOR_A_SENSOR_FLIPPED = true;
    public static final boolean IS_COMPETITION_ROTATOR_B_SENSOR_FLIPPED = true;
    public static final boolean IS_COMPETITION_ROTATOR_C_SENSOR_FLIPPED = true;
    public static final boolean IS_COMPETITION_ROTATOR_D_SENSOR_FLIPPED = true;

    public static final boolean IS_COMPETITION_TRACTION_A_MOTOR_FLIPPED = true;
    public static final boolean IS_COMPETITION_TRACTION_B_MOTOR_FLIPPED = true;
    public static final boolean IS_COMPETITION_TRACTION_C_MOTOR_FLIPPED = true;
    public static final boolean IS_COMPETITION_TRACTION_D_MOTOR_FLIPPED = true;

    public static final double COMPETITION_ROTATOR_A_OFFSET_ANGLE = 320.625;
    public static final double COMPETITION_ROTATOR_B_OFFSET_ANGLE = 48.867;
    public static final double COMPETITION_ROTATOR_C_OFFSET_ANGLE = 56.602;
    public static final double COMPETITION_ROTATOR_D_OFFSET_ANGLE = 303.047;

    //INTAKE MOTORS
    public static final int BALL_INTAKE_MOTOR_ID = 15;
    
    public static final int LIFTER_MASTER_ID = 26;//LEFT
    public static final int GROUND_INTAKE_ID = 27;//LEFT
    public static final int GROUND_LIFT_ID = 28;//RIGHT
    public static final int LIFTER_FOLLOWER_3_ID = 29;//RIGHT

    //HATCHINTAKE
    public static final int HATCH_SOLENOID_FORWARD_CHANNEL = 0;
    public static final int HATCH_SOLENOID_REVERSE_CHANNEL = 1;

    //INTAKE LIFTER BOOLEANS
    public static final boolean IS_LIFTER_MASTER_SENSOR_FLIPPED = true;
    public static final boolean IS_LIFTER_MASTER_MOTOR_FLIPPED = true;
    public static final boolean IS_LIFTER_FOLLOWER_3_SENSOR_FLIPPED = true;
    public static final boolean IS_LIFTER_FOLLOWER_3_MOTOR_FLIPPED = false;

    //GROUND INTAKE STUFF
    public static final boolean IS_GROUND_LIFT_SENSOR_FLIPPED = false;
    public static final boolean IS_GROUND_LIFT_MOTOR_FLIPPED = false;
    public static final boolean IS_GROUND_INTAKE_MOTOR_FLIPPED = false;

    //CLIMBER
    public static final int CLIMBER_SOLENOID_LEFT_FORWARD_CHANNEL = 2;
    public static final int CLIMBER_SOLENOID_LEFT_REVERSE_CHANNEL = 3;
    public static final int CLIMBER_SOLENOID_RIGHT_FORWARD_CHANNEL = 5;
    public static final int CLIMBER_SOLENOID_RIGHT_REVERSE_CHANNEL = 4;
    
}