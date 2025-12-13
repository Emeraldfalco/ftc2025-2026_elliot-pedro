package org.firstinspires.ftc.teamcode.custom;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.custom.CheeksKicker;
import org.firstinspires.ftc.teamcode.custom.Intake;

public class Loader {
    private com.qualcomm.robotcore.hardware.DistanceSensor tofFront;
    CheeksKicker cheeks;
    ElapsedTime time;
    ElapsedTime cycleTime;
    Intake intake;
    double middleDistance;
    public int loaderStep = 1;
    public boolean ballPreviouslyDetected = false;

    public char left = ' ';
    public char middle = ' ';
    public char right = ' ';

    public Loader(HardwareMap hwmap) {
        time = new ElapsedTime();
        cycleTime = new ElapsedTime();
        tofFront = hwmap.get(com.qualcomm.robotcore.hardware.DistanceSensor.class,"middleDistance");
        intake = new Intake(hwmap, time);
        cheeks = new CheeksKicker(hwmap, time);
    }

    public double getDistance() {
        return tofFront.getDistance(DistanceUnit.MM);
    }

    public boolean intake() {
//        intake.intake();
//        middleDistance = getDistance();
//        boolean ballDetected = middleDistance < 100;
//
//        if (ballDetected && !ballPreviouslyDetected) {
//
//            if (left == ' ' && cheeks.leftCheek.getPosition() == 0.0) {
//                cheeks.leftUp();
//                left = 'F';
//            } else if (right == ' ' && cheeks.rightCheek.getPosition() == 0.0) {
//                cheeks.rightUp();
//                right = 'F';
//            } else if (middle == ' ' && left != ' ' && right != ' ') {
//                middle = 'F';
//            }
//
//            ballPreviouslyDetected = true;
//            cycleTime.reset();
//        }
//
//        if(left == 'F') cheeks.leftUp();
//        if(right == 'F') cheeks.rightUp();
//
//        if (!ballDetected && cycleTime.seconds() > 1) {
//            ballPreviouslyDetected = false;
//        }
//
//        return left == 'F' && right == 'F' && middle == 'F';
        switch(loaderStep) {
            case 1:
                intake.intake();
                if(getDistance() < 100) {
                    loaderStep = 5;
                }
                break;
            case 5:
                if (left == ' ' && cheeks.leftCheek.getPosition() == 0.0) {
                    cheeks.leftUp();
                    left = 'F';
                    loaderStep = 10;
                } else if (right == ' ' && cheeks.rightCheek.getPosition() == 0.0) {
                    cheeks.rightUp();
                    right = 'F';
                    loaderStep = 10;
                } else if (middle == ' ' && left != ' ' && right != ' ') {
                    middle = 'F';
                    loaderStep = 10;
                }
                break;
            case 10:
                if (left == ' ' && cheeks.leftCheek.getPosition() == 0.0) {
                    cheeks.leftUp();
                    left = 'F';
                    loaderStep = 15;
                } else if (right == ' ' && cheeks.rightCheek.getPosition() == 0.0) {
                    cheeks.rightUp();
                    right = 'F';
                    loaderStep = 15;
                } else if (middle == ' ' && left != ' ' && right != ' ') {
                    middle = 'F';
                    loaderStep = 15;
                }
                break;
            case 15:
                if (left == ' ' && cheeks.leftCheek.getPosition() == 0.0) {
                    cheeks.leftUp();
                    left = 'F';
                    loaderStep = 20;
                } else if (right == ' ' && cheeks.rightCheek.getPosition() == 0.0) {
                    cheeks.rightUp();
                    right = 'F';
                    loaderStep = 20;
                } else if (middle == ' ' && left != ' ' && right != ' ') {
                    middle = 'F';
                    loaderStep = 20;
                }
                break;
            case 20:
                loaderStep = 1;
                return left == 'F' && right == 'F' && middle == 'F';
        }
        return false;
    }
}