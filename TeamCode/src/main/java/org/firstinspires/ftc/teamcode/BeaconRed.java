package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.robotlibrary.AutonomousUtils;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.BeaconUtils;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.ColorUtils;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.DriveTrain;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.EncoderDrive;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.EncoderTurn;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.FlyWheel;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.GyroUtils;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.Intake;
import org.firstinspires.ftc.teamcode.robotlibrary.BigAl.RangeUtils;

import static org.firstinspires.ftc.teamcode.robotlibrary.AutonomousUtils.COMPLETED;

/**
 * Created by Leo on 10/16/2016.
 */

@Autonomous(name = "BeaconRed", group = "Encoder Autonomous")
public class BeaconRed extends OpMode {
    ColorUtils.Color actedColor;

    int stage = 0;//testing
    ElapsedTime time = new ElapsedTime();
    DriveTrain driveTrain;
    //GyroUtils gyroUtils;
    ColorUtils colorUtils;
    BeaconUtils beaconUtils;
    //GyroSensor gyro;
    Intake intake;
    FlyWheel flyWheel;
    EncoderDrive drive;
    EncoderTurn turn;
    RangeUtils rangeUtils;


    /* Selector variables */
    private String alliance = "Red";
    private String beaconAmount = "2";
    private int shoot = 2;

    @Override
    public void init() {

        driveTrain = new DriveTrain(hardwareMap);
        //gyroUtils = new GyroUtils(hardwareMap, driveTrain, telemetry);
        colorUtils = new ColorUtils(hardwareMap);
        flyWheel = new FlyWheel(hardwareMap);
        intake = new Intake(hardwareMap);
        beaconUtils = new BeaconUtils(hardwareMap, colorUtils, alliance);
        rangeUtils = new RangeUtils(hardwareMap);

        //gyro = gyroUtils.gyro;
        //gyro.calibrate();

    }

    @Override
    public void start() {

        //gyro.calibrate();
        colorUtils.lineColorSensor.enableLed(true);
    }

    @Override
    public void loop() {
        if (stage == 0) {//calibrates to 0
            stage++;
        }

        if (stage == 1) {
            if (drive == null) {
                drive = new EncoderDrive(driveTrain, 1300, 0.5);
                drive.run();
                if (shoot > 0) {
                    flyWheel.currentPower = flyWheel.defaultStartingPower;
                    flyWheel.currentlyRunning = true;
                }
            }
            if (drive.isCompleted()) {
                driveTrain.stopRobot();
                time.reset();
                stage++;
            }
        }

        flyWheel.powerMotor(); // Update flywheel values

        if (stage == 2) {
            if (shoot == 1) {
                intake.setIntake(Intake.IntakeSpec.A, Intake.IntakeDirection.IN);
            }
            if (shoot == 2) {
                intake.setIntake(Intake.IntakeSpec.BOTH, Intake.IntakeDirection.IN);
            }
            if (time.time() > 2.5 || shoot <= 0) {
                stage++;
                time.reset();
                //intake.stopIntake(Intake.IntakeSpec.BOTH);
                flyWheel.currentlyRunning = false;
            }
        }

        if (stage == 3) { // Wait
            if (time.time() > AutonomousUtils.WAITTIME) {
                stage++;
                drive = null;
                turn = null;
                time.reset();
            }
        }

        if (stage == 4) { // Drive backwards a wee bit
            if (drive == null) {
                drive = new EncoderDrive(driveTrain, -900, 0.2);
                drive.run();
            }
            if (drive.isCompleted()) {
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }

        if (stage == 5) { // Wait
            if (time.time() > AutonomousUtils.WAITTIME) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
            }
        }
        if (stage == 6) {
            if (turn == null) { // Turn to the right direction to get to the white line
                turn = new EncoderTurn(driveTrain, 28, GyroUtils.Direction.COUNTERCLOCKWISE);
                turn.run();
            }
            if (turn.isCompleted()) {
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }
        if (stage == 7) { // Wait
            if (time.time() > AutonomousUtils.WAITTIME) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
            }
        }
        if (stage == 8) { // Drive until white line
            if (drive == null) {
                drive = new EncoderDrive(driveTrain, 3600, 0.45);
            }
            drive.runWithDecrementPower(0.000325); //slows down gradually to hit white line
            if (colorUtils.aboveWhiteLine()) {
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
            if (drive.isCompleted()) { //fail safe if we miss white line
                stage = AutonomousUtils.DEADBEEF;
                driveTrain.stopRobot();
                AutonomousUtils.failSafeError(hardwareMap);
            }
        }
        if (stage == 9) { // Wait
            if (time.time() > AutonomousUtils.WAITTIME) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
            }
        }
        if (stage == 10) { // Turn to face the beacon
            if (turn == null) {
                turn = new EncoderTurn(driveTrain, 29, GyroUtils.Direction.COUNTERCLOCKWISE);
                turn.run();
            }
            if (turn.isCompleted()) {
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }
        if (stage == 11) { // Wait
            if (time.time() > .5) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
                driveTrain.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }
        if (stage == 12) { // Drive until we see a color
            if (colorUtils.beaconColor().equals(ColorUtils.Color.NONE) && rangeUtils.rangeSensor.getDistance(DistanceUnit.CM) > 17) {
                driveTrain.powerLeft(0.25);
                driveTrain.powerRight(0.25);
            } else {
                RobotLog.d("Attempted to stop robot at " + rangeUtils.rangeSensor.getDistance(DistanceUnit.CM));
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }

        if (stage == 13) { // Wait
            if (time.time() > 1) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
            }
        }

        if (stage == 14) { // Act on beacon with color sensor
            if (!colorUtils.beaconColor().equals(ColorUtils.Color.NONE)) {
                actedColor = beaconUtils.actOnBeaconWithColorSensor();
                stage++;
                time.reset();
            } else {
                stage = AutonomousUtils.DEADBEEF;
                AutonomousUtils.failSafeError(hardwareMap);
                time.reset();
            }

        }

        if (stage == 15) { // Drive forward till we're at the wall
            if (drive == null) {
                int counts = (int) (rangeUtils.rangeSensor.getDistance(DistanceUnit.CM) - 4) * 19; // Get the distance to the wall in enc counts, -4 ajusts for chaisi
                drive = new EncoderDrive(driveTrain, counts, 0.225); // Just a little umph to hit the button
                drive.run();
            }
            if (drive.isCompleted() || time.time() > 2) { // Time failsafe just in case we need to bail
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }


        if (stage == 16) { // Wiggle for beacon 1
            if (actedColor.equals(ColorUtils.Color.RED)) {
                if (turn == null) {
                    turn = new EncoderTurn(driveTrain, 5, GyroUtils.Direction.CLOCKWISE);
                    turn.run();
                }
                if (turn.isCompleted() || time.time() > .5) {
                    driveTrain.stopRobot();
                    stage++;
                    time.reset();
                    turn = null;
                }


            } else if (actedColor.equals(ColorUtils.Color.BLUE)) {
                if (turn == null) {
                    turn = new EncoderTurn(driveTrain, 5, GyroUtils.Direction.COUNTERCLOCKWISE);
                    turn.run();
                }
                if (turn.isCompleted() || time.time() > .5) {
                    driveTrain.stopRobot();
                    stage++;
                    time.reset();
                    turn = null;
                }
            } else if (actedColor.equals(ColorUtils.Color.NONE)) {
                if (turn == null) {
                    turn = new EncoderTurn(driveTrain, 5, GyroUtils.Direction.COUNTERCLOCKWISE);
                    turn.run();
                }
                if (turn.isCompleted() || time.time() > .5) {
                    driveTrain.stopRobot();
                    stage++;
                    time.reset();
                    turn = null;
                }
            }
        }

        if (stage == 17) { //Back up 15cm with prox
            if (rangeUtils.getDistance(DistanceUnit.CM, -1) <= 12) {
                driveTrain.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                driveTrain.powerLeft(-0.55);
                driveTrain.powerRight(-0.55);
            } else {
                driveTrain.stopRobot();
                beaconUtils.rotateServo(BeaconUtils.ServoPosition.CENTER);
                if (beaconAmount.equals("1")) stage = COMPLETED;
                if (beaconAmount.equals("2")) stage++;
            }
        }

        if (stage == 18) { // Turn towards the white line of the second beacon
            if (turn == null) {
                turn = new EncoderTurn(driveTrain, 67, GyroUtils.Direction.COUNTERCLOCKWISE);
                turn.run();
            }
            if (turn.isCompleted()) {
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }

        if (stage == 19) { // Wait plus a little extra
            if (time.time() > AutonomousUtils.WAITTIME) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
                driveTrain.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }

        if (stage == 20) { // Drive to the white line of the second beacon
            if (drive == null) {
                drive = new EncoderDrive(driveTrain, -3100, 0.45);
            }
            drive.runWithDecrementPower(0.000325); // slows down gradually to hit white line
            if (colorUtils.aboveWhiteLine() && Math.abs(driveTrain.RightFrontMotor.getCurrentPosition()) > 1000) {
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
            if (drive.isCompleted()) { //fail safe if we miss white line
                stage = AutonomousUtils.DEADBEEF;
                driveTrain.stopRobot();
                AutonomousUtils.failSafeError(hardwareMap);
            }
        }

        if (stage == 21) { // Wait plus a little extra
            if (time.time() > AutonomousUtils.WAITTIME) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
            }
        }

        if (stage == 22) { // Back up
            if (drive == null) {
                drive = new EncoderDrive(driveTrain, 125, 0.3);
                drive.run();
            }
            if (drive.isCompleted()) {
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }

        if (stage == 23) { // Wait plus a little extra
            if (time.time() > AutonomousUtils.WAITTIME) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
            }
        }

        if (stage == 24) { // Turn stage to face the beacon
            if (turn == null) {
                turn = new EncoderTurn(driveTrain, 71, GyroUtils.Direction.CLOCKWISE);
                turn.run();
            }
            if (turn.isCompleted()) {
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }

        if (stage == 25) { // Wait
            if (time.time() > AutonomousUtils.WAITTIME) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
            }
        }

        if (stage == 26) { // We just need a little distance to help identify color
            if (drive == null) {
                drive = new EncoderDrive(driveTrain, 100, 0.25);
                drive.run();
            }
            if (drive.isCompleted()) {
                driveTrain.stopRobot();
                stage++;
                time.reset();
                driveTrain.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }

        if (stage == 27) { // Wait
            if (time.time() > AutonomousUtils.WAITTIME) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
            }
        }

        if (stage == 28) { // Drive until we see a color
            if (colorUtils.beaconColor().equals(ColorUtils.Color.NONE)) {
                driveTrain.powerLeft(0.25);
                driveTrain.powerRight(0.25);
            } else {
                RobotLog.d("Attempted to stop robot at " + rangeUtils.rangeSensor.getDistance(DistanceUnit.CM));
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }

        if (stage == 29) { // Wait
            if (time.time() > 1) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
            }
        }

        if (stage == 30) { // Act on beacon with color sensor
            if (!colorUtils.beaconColor().equals(ColorUtils.Color.NONE)) {
                beaconUtils.actOnBeaconWithColorSensor();
                actedColor = beaconUtils.actOnBeaconWithColorSensor();
                stage++;
                time.reset();
            } else {
                stage = 626;
                AutonomousUtils.failSafeError(hardwareMap);
                time.reset();
            }

        }

        if (stage == 31) { // Drive forward till we're at the wall
            if (drive == null) {
                int counts = (int) (rangeUtils.rangeSensor.getDistance(DistanceUnit.CM) - 4) * 19; // Get the distance to the wall in enc counts, -4 ajusts for chaisi
                drive = new EncoderDrive(driveTrain, counts + 25, 0.225); // Just a little umph to hit the button
                drive.run();
            }
            if (drive.isCompleted() || time.time() > 2) { // Time failsafe just in case we need to bail
                driveTrain.stopRobot();
                stage++;
                time.reset();
            }
        }


        if (stage == 32) { // Wiggle for beacon 2
            if (actedColor.equals(ColorUtils.Color.RED)) {
                if (turn == null) {
                    turn = new EncoderTurn(driveTrain, 5, GyroUtils.Direction.CLOCKWISE);
                    turn.run();
                }
                if (turn.isCompleted() || time.time() > .5) {
                    driveTrain.stopRobot();
                    stage++;
                    time.reset();
                    turn = null;
                }
            }
            else if (actedColor.equals(ColorUtils.Color.BLUE)) {
                if (turn == null) {
                    turn = new EncoderTurn(driveTrain, 5, GyroUtils.Direction.COUNTERCLOCKWISE);
                    turn.run();
                }
                if (turn.isCompleted() || time.time() > .5) {
                    driveTrain.stopRobot();
                    stage++;
                    time.reset();
                    turn = null;
                }


            }
            else if (actedColor.equals(ColorUtils.Color.NONE)) {
                if (turn == null) {
                    turn = new EncoderTurn(driveTrain, 5, GyroUtils.Direction.COUNTERCLOCKWISE);
                    turn.run();
                }
                if (turn.isCompleted() || time.time() > .5) {
                    driveTrain.stopRobot();
                    stage++;
                    time.reset();
                    turn = null;
                }

            }
        }
        if (stage == 33) { // Wait plus a little extra
            if (time.time() > AutonomousUtils.WAITTIME) {
                stage++;
                time.reset();
                drive = null;
                turn = null;
                driveTrain.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }

        if (stage == 34) { //Back up 15cm with prox
            if (rangeUtils.getDistance(DistanceUnit.CM, -1) <= 10) {
                driveTrain.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                driveTrain.powerLeft(-0.55);
                driveTrain.powerRight(-0.55);
            } else {
                driveTrain.stopRobot();
                beaconUtils.rotateServo(BeaconUtils.ServoPosition.CENTER);
                stage++;
                time.reset();
            }
        }

        if (stage == 35) {
            if (turn == null) {
                turn = new EncoderTurn(driveTrain, 90, GyroUtils.Direction.COUNTERCLOCKWISE);
                turn.run();
            }
            if (turn.isCompleted()) {
                    turn.completed();
                    stage++;
                    drive = null;
                    time.reset();
            }
        }

        if (stage == 36) {
            if (drive == null) {
                drive = new EncoderDrive(driveTrain, 2000, .75);
                drive.run();
            }
            if (drive.isCompleted()) {
                drive.completed();
                stage++;
                turn = null;
                time.reset();
            }
        }


            telemetry.addData("F", driveTrain.LeftFrontMotor.getCurrentPosition() + ":" + driveTrain.RightFrontMotor.getCurrentPosition());
            telemetry.addData("B", driveTrain.LeftBackMotor.getCurrentPosition() + ":" + driveTrain.RightBackMotor.getCurrentPosition());
            telemetry.addData("Range", rangeUtils.rangeSensor.getDistance(DistanceUnit.CM));
            telemetry.addData("Beacon", colorUtils.beaconColor().toString());
            telemetry.addData("Stage", String.valueOf(stage));
            telemetry.addData("Time", String.valueOf(time.time()));

    }
}