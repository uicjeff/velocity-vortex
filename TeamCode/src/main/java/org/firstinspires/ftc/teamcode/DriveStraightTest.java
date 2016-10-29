package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robotlibrary.TBDName.DriveTrain;
import org.firstinspires.ftc.teamcode.robotlibrary.TBDName.DynamicAutonomousSelector;
import org.firstinspires.ftc.teamcode.robotlibrary.TBDName.GyroUtils;

/**
 * Created by Dynamic Signals on 10/18/2016.
 */

@Autonomous(name = "DriveStraightOrNot", group = "Testing")
public class DriveStraightTest extends OpMode {

    DriveTrain driveTrain;
    GyroUtils gyroUtils;

    int stage = 0;

    ElapsedTime driveTime = new ElapsedTime();

    DynamicAutonomousSelector das;

    @Override
    public void init() {

        driveTrain = new DriveTrain(hardwareMap);
        gyroUtils = new GyroUtils(hardwareMap, driveTrain, telemetry);

        gyroUtils.gyro.calibrate();


    }

    @Override
    public void start() {

        gyroUtils.gyro.calibrate();


    }

    @Override
    public void loop() {

        if (stage == 0) {
            if (!gyroUtils.gyro.isCalibrating()) {
                stage++;
                driveTime.reset();
            }
        }

        if (stage == 1) {
            // We drive for 1 seconds at full power drive on heading and stop after the 2 seconds
            final double time = 2;
            if (driveTime.time() <= time) {
                gyroUtils.driveOnHeading(0);
            } else {
                driveTrain.powerRight(0);
                driveTrain.powerLeft(0);
            }
        }

        telemetry.addData("Gyro", String.valueOf(gyroUtils.gyro.getHeading()));

    }
}
