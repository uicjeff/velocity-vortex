package org.firstinspires.ftc.teamcode.robotlibrary.BigAl;

import android.os.Environment;
import android.support.annotation.Nullable;

import com.google.common.io.Files;
import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.navXPIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dynamic Signals on 1/16/2017.
 */

public class GyroTurn implements Routine {

    private static GyroTurn instance;
    private final double TOLERANCE_DEGREES = 2; // The degrees positive and negative that you want to get to
    private final double TIMEOUT = 0; // In seconds, 0 if you don't want a timeout

    public navXPIDController yawPIDController;
    public GyroUtils.GyroDetail detail; // Used for getting useful data and stats from a turn
    public DriveTrain driveTrain; // DriveTrain instance, it's public so you can grab the object outside th class
    private AHRS navx; // navX instance

    private double targetDegree = 0;
    private int completedCounter = 0;

    private ElapsedTime creationTime = new ElapsedTime(); // Used for timeout failsafe

    private StateMachineOpMode opMode;
    private navXPIDController.PIDResult yawPIDResult;
    private double MinMotor = 0.095, MaxMotor = 0.7;

    private boolean recordData = false;
    private File chartLocation = new File(Environment.getExternalStorageDirectory() + File.separator + "FIRST" + File.separator + "GyroTurnData.html");
    private List<String> gyroData;

    /**
     * Static constructor for a GyroTurn if you want to specify the PID for
     *
     * @param pid The PID instance of the class that has the P, I, and D coefficients.
     * @return the GyroTurn instance
     */
    public static GyroTurn createTurn(StateMachineOpMode opMode, double targetDegree, PID pid) {
        if (instance == null) {
            instance = new GyroTurn(opMode, targetDegree, pid);
        }
        instance.isCompleted();
        return instance;
    }

    /**
     * Static constructor for an OpMode style class with just the degree and opmode
     *
     * @param opModeArg       the OpMode that implements StateMachineOpMode, usually just type "this"
     * @param targetDegreeArg the degree that you want to turn to (0-360)
     * @return the instance of the GyroTurn class. You can check the progress using the GyroDetail detail for percentage complete.
     */
    public static GyroTurn createTurn(StateMachineOpMode opModeArg, double targetDegreeArg) {
        return createTurn(opModeArg, targetDegreeArg, null);
    }

    private GyroTurn(StateMachineOpMode opMode, double targetDegree, @Nullable PID pid) {
        this.opMode = opMode;
        this.navx = AHRS.getInstance(opMode.hardwareMap);
        this.targetDegree = targetDegree;

        driveTrain = new DriveTrain(opMode.hardwareMap);

        if (pid != null) recordData = true;
        if (recordData) {
            gyroData = new ArrayList<>();
        }

        creationTime.reset();

        /* Create a PID Controller which uses the Yaw Angle as input. */
        yawPIDController = new navXPIDController(navx,
                navXPIDController.navXTimestampedDataSource.YAW);

        yawPIDController.reset();

        /* Configure the PID controller */
        yawPIDController.setSetpoint(targetDegree);
        yawPIDController.setContinuous(true);
        yawPIDController.setOutputRange(-1, 1);
        yawPIDController.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, TOLERANCE_DEGREES);

        if (pid == null) pid = new PID(); // Use defaults if not specified a PID preference
        yawPIDController.setPID(pid.p, pid.i, pid.d);

        /* Enable the controller and send data to the navX */
        yawPIDController.enable(true);

        /* We want to use the RUN_USING_ENCODER run mode to get the most accurate turning power */
        driveTrain.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        /* Start tracking data */
        yawPIDResult = new navXPIDController.PIDResult();
        detail = new GyroUtils.GyroDetail(navx, targetDegree);

    }

    @Override
    public void run() {

        detail.updateData();

        if (yawPIDController.isNewUpdateAvailable(yawPIDResult)) {
            double output = yawPIDResult.getOutput();
            double power = 0; // Temp value
            if (Math.signum(output) == 1) {
                power = Range.clip(output, MinMotor, MaxMotor);
            }
            if (Math.signum(output) == -1) {
                power = Range.clip(output, -MaxMotor, -MinMotor);
            }
            driveTrain.powerLeft(power);
            driveTrain.powerRight(-power);
        }

        if (recordData) {
            double timestamp = (double) Math.round(creationTime.time() * 100) / 100;
            double degree = targetDegree - (double) Math.round(detail.degreesOff * 100) / 100;
            gyroData.add("\n[" + timestamp + ", " + degree + "],");
        }

    }

    @Override
    public boolean isCompleted() {
        boolean onTarget = yawPIDResult.isOnTarget();
        if (onTarget) {
            completedCounter++;
        } else {
            completedCounter = 0;
        }
        if (completedCounter > 25 || (TIMEOUT > 0 && creationTime.time() > TIMEOUT)) {
            completed();
        } else {
            run();
        }
        return (completedCounter > 25);
    }

    @Override
    public void completed() {
        driveTrain.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        driveTrain.stopRobot(); // Stop the robot while floating into position
        yawPIDController.enable(false); // Tell navX to stop tracking
        // Write our data to a google chart
        opMode.next(); // Go to next stage
        if (recordData) writeGyroData();
        teardown();
    }

    public static void teardown() {
        instance = null;
    }

    public void writeGyroData() {
        StringBuilder sb = new StringBuilder();
        sb.append("<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script><script type=\"text/javascript\">google.charts.load('current', {packages: ['corechart', 'line']});google.charts.setOnLoadCallback(drawBackgroundColor);function drawBackgroundColor() {var data = new google.visualization.DataTable();data.addColumn('number', 'Time (seconds)');data.addColumn('number', 'Degrees');data.addRows([");
        for (String arrayString : gyroData) sb.append(arrayString);
        sb.append("\n]);var options = {hAxis: {title: 'Time'},vAxis: {title: 'Degrees to target'}};var chart = new google.visualization.LineChart(document.getElementById('chart_div'));chart.draw(data, options);}</script><div id=\"chart_div\"></div>");
        try {
            Files.write(sb.toString().getBytes(Charset.defaultCharset()), chartLocation); // Write to the html location
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}