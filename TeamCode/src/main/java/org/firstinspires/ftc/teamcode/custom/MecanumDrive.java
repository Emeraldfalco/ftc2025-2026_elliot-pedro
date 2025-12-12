package org.firstinspires.ftc.teamcode.custom;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class MecanumDrive {
    DcMotorEx leftFront = null;
    DcMotorEx leftBack = null;
    DcMotorEx rightFront = null;
    DcMotorEx rightBack = null;
    
    public MecanumDrive(HardwareMap hwMap, Pose2D startPose){
        leftFront = hwMap.get(DcMotorEx.class, "leftFront");
        leftBack = hwMap.get(DcMotorEx.class, "leftBack");
        rightBack = hwMap.get(DcMotorEx.class, "rightBack");
        rightFront = hwMap.get(DcMotorEx.class, "rightFront");

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // TODO: reverse motor directions if needed
        //   leftFront.setDirection(DcMotorSimple.Direction.REVERSE);

        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);
        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);



    }

    public void setDrivePowers(double lx, double ly, double rx) {
        // Calculate raw motor powers
        double leftFrontPower  = lx + ly + rx;
        double leftBackPower   = lx - ly + rx;
        double rightFrontPower = lx - ly - rx;
        double rightBackPower  = lx + ly - rx;

        // Normalize powers so no value exceeds 1.0
        double max = Math.max(1.0, Math.max(
                Math.abs(leftFrontPower),
                Math.max(Math.abs(leftBackPower),
                        Math.max(Math.abs(rightFrontPower),
                                Math.abs(rightBackPower)))));

        leftFront.setPower(leftFrontPower / max);
        leftBack.setPower(leftBackPower / max);
        rightFront.setPower(rightFrontPower / max);
        rightBack.setPower(rightBackPower / max);
    }



}
