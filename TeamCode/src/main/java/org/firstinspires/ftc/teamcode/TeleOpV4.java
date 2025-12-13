package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.custom.CheeksKicker;
import org.firstinspires.ftc.teamcode.custom.Intake;
import org.firstinspires.ftc.teamcode.custom.Shooter;
import org.firstinspires.ftc.teamcode.custom.Shooterv2;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;


@TeleOp
public class TeleOpV4 extends OpMode{

    Intake intake = null;
    CheeksKicker cheeksKicker = null;
    Follower follower = null;
    Shooterv2 shooter = null;
    ElapsedTime elapsedTime = null;

    double ly = 0;
    double lx = 0;
    double rx = 0;
    boolean dpadLeft = false;
    boolean dpadRight = false;
    boolean dpadUp = false;
    boolean dpadDown = false;
    boolean leftBump = false;
    boolean rightBump = false;
    double leftTrig = 0.0;
    double rightTrig = 0.0;
    boolean a = false;
    boolean b = false;

    boolean x = false;
    boolean y = false;
    boolean rightTrigPress = false;

    double targetFlywheelVel = 1360;

    boolean lastRightBumpState = false;
    boolean lastRightTrigState = false;
    boolean lastLeftBumpState = false;
    boolean spinFlywheel = false;



    @Override
    public void init() {
        elapsedTime = new ElapsedTime();
        intake = new Intake(hardwareMap,elapsedTime);
        cheeksKicker = new CheeksKicker(hardwareMap, elapsedTime);
        follower = Constants.createFollower(hardwareMap);
        shooter = new Shooterv2(hardwareMap);


    }

    @Override
    public void start() {
        follower.startTeleOpDrive();
    }

    @Override
    public void loop() {
        //Drivetrain


        ly = gamepad1.left_stick_y;
        lx = gamepad1.left_stick_x;
        rx = gamepad1.right_stick_x;

        follower.update();
        follower.setTeleOpDrive(ly,lx,rx,true);

        //intake

        a = gamepad1.a;
        x = gamepad1.x;

        if (a){
            intake.intake();
        } else if (x) {
            intake.reject();
        } else {
            intake.hold();
        }

        //flywheel

        rightBump = gamepad1.right_bumper;
        leftBump = gamepad1.left_bumper;
        rightTrig = gamepad1.right_trigger;


        rightTrigPress = gamepad1.right_trigger >= 0.7;

        if (rightBump && !lastRightBumpState) {
            targetFlywheelVel += 20;
        }
        lastRightBumpState = rightBump;

        if (leftBump && !lastLeftBumpState) {
            targetFlywheelVel -= 20;
        }
        lastLeftBumpState = leftBump;



        if (rightTrigPress && !lastRightTrigState) {
            spinFlywheel = !spinFlywheel;
        }
        lastRightTrigState = rightTrigPress;

        if (spinFlywheel){
            shooter.spinUp(targetFlywheelVel);
        } else {
            shooter.spinDown();
        }


        //kicker

        if (b){
            cheeksKicker.kickerExtend();
        } else {
            cheeksKicker.kickerRetract();
        }

        //cheeks

        cheeksKicker.update(gamepad1.dpad_left,gamepad1.dpad_right);

    }
}
