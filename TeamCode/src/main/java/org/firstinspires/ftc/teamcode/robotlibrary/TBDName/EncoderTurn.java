package org.firstinspires.ftc.teamcode.robotlibrary.TBDName;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by leoforney on 12/11/16.
 */

public class EncoderTurn implements EncoderRoutine {

    private double turnPerDegree = ((((GearRatio * SprocketRatio) * 28) / 360) * 180 / 121.5);

    private DriveTrain driveTrain;
    private final double power = 0.45;
    private int encoderCounts;
    public GyroUtils.Direction turnDirection;

    /**
     * Constructor for the EncoderTurn object
     *
     * @param driveTrain - The drive train object that should be initialized
     */
    public EncoderTurn(DriveTrain driveTrain, int degreesToTurn, GyroUtils.Direction turnDirection) {
        this.driveTrain = driveTrain;
        this.turnDirection = turnDirection;

        driveTrain.LeftFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveTrain.RightFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //int currentPos = gyroSensor.getHeading();
        encoderCounts = degreesToTurn * (int)turnPerDegree;

        switch (turnDirection) {
            case CLOCKWISE:
                driveTrain.LeftFrontMotor.setTargetPosition(encoderCounts);
                driveTrain.RightFrontMotor.setTargetPosition(-encoderCounts);

                driveTrain.LeftFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                driveTrain.RightFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case COUNTERCLOCKWISE:
                driveTrain.LeftFrontMotor.setTargetPosition(-encoderCounts);
                driveTrain.RightFrontMotor.setTargetPosition(encoderCounts);

                driveTrain.LeftFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                driveTrain.RightFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
        }

        // Set the run mode for only the front motors (Right only)
        driveTrain.LeftFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        driveTrain.RightFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    }

    @Override
    public void run() {
        switch (turnDirection) {
            case CLOCKWISE:
                driveTrain.powerLeft(power);
                driveTrain.powerRight(-power);
                break;
            case COUNTERCLOCKWISE:
                driveTrain.powerLeft(-power);
                driveTrain.powerRight(power);
                break;
        }
    }

    @Override
    public boolean isCompleted() {
        switch (turnDirection) {
            case CLOCKWISE:
                if (driveTrain.RightFrontMotor.getCurrentPosition() < -encoderCounts) {
                    return true;
                }
                break;
            case COUNTERCLOCKWISE:
                if (driveTrain.RightFrontMotor.getCurrentPosition() > encoderCounts) {
                    return true;
                }
                break;
        }

        return false;
    }

    @Override
    public void completed() {
        driveTrain.stopRobot();
        driveTrain.LeftFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        driveTrain.RightFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}