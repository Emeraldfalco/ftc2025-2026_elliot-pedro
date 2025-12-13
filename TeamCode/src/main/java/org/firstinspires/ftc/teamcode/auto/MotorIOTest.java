package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;


@TeleOp
public class MotorIOTest extends OpMode {

    boolean a = false;
    boolean b = false;
    boolean x = false;
    boolean y = false;
    DcMotorEx leftFront = null;
    DcMotorEx rightFront = null;
    DcMotorEx leftBack = null;
    DcMotorEx rightBack = null;

    String motor = "no motor";

    @Override
    public void init() {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftBack = hardwareMap.get(DcMotorEx.class, "leftBack");
        rightBack = hardwareMap.get(DcMotorEx.class, "rightBack");
    }

    @Override
    public void loop() {

        a = gamepad1.a;
        b = gamepad1.b;
        x = gamepad1.x;
        y = gamepad1.y;

        if (a){
            leftFront.setPower(1);
            motor = "leftFront";
        } else {
            leftFront.setPower(0);
        }
        if (b){
            rightFront.setPower(1);
            motor = "rightFront";
        } else{
            rightFront.setPower(0);
        }
        if (x){
            leftBack.setPower(1);
            motor = "leftBack";
        } else {
            leftBack.setPower(0);
        }
        if (y){
            rightBack.setPower(1);
            motor = "rightBack";
        } else {
            rightBack.setPower(0);
        }




        telemetry.addData("motor", motor);
    }
}
