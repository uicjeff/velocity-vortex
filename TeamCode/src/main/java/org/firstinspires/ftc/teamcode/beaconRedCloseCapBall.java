package org.firstinspires.ftc.teamcode;

/**
 * Created by Dynamic Signals on 3/24/2017.
 */
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "beaconRedCloseCapBall", group = "AWorking")
public class beaconRedCloseCapBall extends beaconRedClose {

    @Override
    public void init() {
        super.init();
        capBallGet = true;
    }

}
