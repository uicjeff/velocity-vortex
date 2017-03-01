package org.firstinspires.ftc.teamcode.robotlibrary.testing.navxtesting;

import com.kauailabs.navx.ftc.AHRS;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.BasicGyroTurn;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.DriveTrain;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.EncoderDrive;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Dynamic Signals on 2/26/2017.
 */

@Autonomous(name = "ConceptNavXLASABasicTurn")
public class ConceptNavXLASABasicTurn extends OpMode {

    DriveTrain driveTrain;
    AHRS navx;
    int stage = 0;

    ElapsedTime time = new ElapsedTime();

    BasicGyroTurn gyroTurn;

    @Override
    public void init() {

        driveTrain = new DriveTrain(hardwareMap);
        navx = AHRS.getInstance(hardwareMap);

    }

    @Override
    public void loop() {

        if (stage == 0) {
            if (!navx.isCalibrating()) {
                navx.zeroYaw();
                stage++;
                time.reset();
            }
        }

        if (stage == 1) {
            if (time.time() > 1) {
                stage++;
            }
        }

        if (stage == 2) {
            if (gyroTurn == null) {
                gyroTurn = new BasicGyroTurn(navx, driveTrain, 90);
            }
            if (gyroTurn.isCompleted()) {
                stage++;
            }
        }

        telemetry.addData("Yaw", navx.getYaw());

        if (gyroTurn != null) {
            telemetry.addData("Degrees left", gyroTurn.detail.degreesOff);
            telemetry.addData("Direction", gyroTurn.detail.turnDirection.toString().toLowerCase());
            telemetry.addData("Percent complete", new DecimalFormat("#").format(gyroTurn.percentComplete) + "%");
            telemetry.addData("Power", "L: " + driveTrain.LeftBackMotor.getPower() + " , R: " + driveTrain.RightBackMotor.getPower());

        }

        telemetry.addData("Stage", stage);

    }
}
