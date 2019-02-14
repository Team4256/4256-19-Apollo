package org.usfirst.frc.team4256.robot;

public final class Parameters {
    
    public static final byte GYRO_UPDATE_HZ = 50;

    //SWERVE
    public static final int ROTATOR_A_ID = 11;
    public static final int ROTATOR_B_ID = 12;
    public static final int ROTATOR_C_ID = 13;
    public static final int ROTATOR_D_ID = 14;

    public static final int TRACTION_A_ID = 21;
    public static final int TRACTION_B_ID = 22;
    public static final int TRACTION_C_ID = 23;
    public static final int TRACTION_D_ID = 24;


    //INTAKE
    public static final int BALL_INTAKE_MOTOR_ID = 15;

    public static final int BALL_INTAKE_SENSOR_ID = 1;//TODO find
    
    public static final int LIFTER_MASTER_ID = 26;//LEFT
    public static final int LIFTER_FOLLOWER_1_ID = 27;//LEFT
    public static final int LIFTER_FOLLOWER_2_ID = 28;//RIGHT
    public static final int LIFTER_FOLLOWER_3_ID = 29;//RIGHT

    public static final int LIMIT_SWTICH_ID = 0;

    //HATCHINTAKE
    public static final int HATCH_SOLENOID_FORWARD_CHANNEL = 0;
    public static final int HATCH_SOLENOID_REVERSE_CHANNEL = 1;

    //CLIMBER
    public static final int CLIMBER_SOLENOID_LEFT_FORWARD_CHANNEL = 2;
    public static final int CLIMBER_SOLENOID_LEFT_REVERSE_CHANNEL = 3;
    public static final int CLIMBER_SOLENOID_RIGHT_FORWARD_CHANNEL = 5;
    public static final int CLIMBER_SOLENOID_RIGHT_REVERSE_CHANNEL = 4;
    
}