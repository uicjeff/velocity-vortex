package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.DriveTrain;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.EncoderTurn;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.GyroUtils;
//import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.EncoderDrive;

/**
 * Created by Dynamic Signals on 12/6/2016.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "EncoderTurnTest", group = "Testing")
public class EncoderTurnTest extends OpMode {

    DriveTrain driveTrain;

    EncoderTurn turn;

    int stage = 0;

    @Override
    public void init() {

        driveTrain = new DriveTrain(hardwareMap);

        driveTrain.RightFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /* Encoder drive sequence
     * 1. Reset
     * 2. Set target
     * 3. Set mode to RUN_TO_POSITION
     * 4. Set motor power
     * 5. Wait until motor isn't busy
     */

    @Override
    public void start() {

    }

    @Override
    public void loop() {

        telemetry.addData("F", driveTrain.LeftFrontMotor.getCurrentPosition() + ":" + driveTrain.RightFrontMotor.getCurrentPosition());
        telemetry.addData("B", driveTrain.LeftBackMotor.getCurrentPosition() + ":" + driveTrain.RightBackMotor.getCurrentPosition());
        telemetry.addData("Stage", stage);

        if (stage == 0) {
            if(turn == null)
            {
                turn = new EncoderTurn(driveTrain, 90, GyroUtils.Direction.CLOCKWISE);
                turn.run();
            }
            if (turn.isCompleted()){
                turn.completed();
                stage++;
            }
        }
        if (stage == 1) {
            turn = null; // We can reuse the same variable!!!
        }
    }
}
