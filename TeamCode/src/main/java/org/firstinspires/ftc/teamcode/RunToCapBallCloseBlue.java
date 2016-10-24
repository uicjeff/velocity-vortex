package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robotlibrary.TBDName.ColorUtils;
import org.firstinspires.ftc.teamcode.robotlibrary.TBDName.DriveTrain;
import org.firstinspires.ftc.teamcode.robotlibrary.TBDName.GyroUtils;

/**
 * Created by Leo on 10/16/2016.
 */

@Autonomous(name = "CapBallCloseBlue", group = "Testing")
public class RunToCapBallCloseBlue extends OpMode {

    int stage = 0;
    ElapsedTime time = new ElapsedTime();
    DriveTrain driveTrain;
    GyroUtils gyroUtils;
    ColorUtils colorUtils;

    GyroSensor gyro;

    @Override
    public void init() {

        driveTrain = new DriveTrain(hardwareMap);
        gyroUtils = new GyroUtils(hardwareMap, driveTrain, telemetry);
        colorUtils = new ColorUtils(hardwareMap);
        gyro = gyroUtils.gyro;
        gyro.calibrate();

    }

    @Override
    public void start() {
        gyro.calibrate();
    }

    @Override
    public void loop() {

        if (stage == 0) { //Calibrates gyro to 0
            if (!gyro.isCalibrating()) {
                stage++;
                time.reset();
            }
            telemetry.addData("Calibrating", String.valueOf(gyro.isCalibrating()));
        }
        if (stage == 1) { //drives forward 3 seconds
            if (time.time() <= 3) {
                gyroUtils.driveOnHeading(0);
            } else {
                driveTrain.powerLeft(0);
                driveTrain.powerRight(0);
                stage++;
                time.reset();
            }
        }
        if (stage == 2) {// wait
            if (time.time() > 0.15) {
                stage++;
                time.reset();
            }
        }
        if (stage == 3) {
            if (time.time() < 0.5) {
                gyroUtils.driveOnHeading(0);
            } else {
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }
        if (stage == 4) {
            if (time.time() > 0.15) {
                stage++;
                time.reset();
            }
        }
        if (stage == 5) {
            if (time.time() < 1) {
                gyroUtils.driveOnHeading(0);
            } else {
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }
        if (stage == 6) {
            if (time.time() > 0.15) {
                stage++;
                time.reset();
            }
        }
        if (stage == 7) {
            if (!gyroUtils.isGyroInTolerance(270)) {
                gyroUtils.rotateUsingSpoofed(45, 225, 162, GyroUtils.Direction.COUNTERCLOCKWISE);
            } else {
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }
        if (stage == 8) {
            if (time.time() > 0.15) {
                stage++;
                time.reset();
            }
        }
        if (stage == 9) {
            if (!colorUtils.aboveBlueLine()) {
                gyroUtils.driveOnHeading(225);
            } else {
                stage++;
                time.reset();
            }
        }

        telemetry.addData("Stage", String.valueOf(stage));
        telemetry.addData("Gyro", String.valueOf(gyro.getHeading()));
        telemetry.addData("Time", String.valueOf(time.time()));
        telemetry.addData("Color", String.valueOf("White: " + colorUtils.aboveWhiteLine() + ", Blue: " + colorUtils.aboveBlueLine()));

    }
}
