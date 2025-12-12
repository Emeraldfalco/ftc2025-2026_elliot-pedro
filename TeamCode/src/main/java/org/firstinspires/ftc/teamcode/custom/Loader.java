package org.firstinspires.ftc.teamcode.custom;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.custom.CheeksKicker;

public class Loader {
    private com.qualcomm.robotcore.hardware.DistanceSensor tofFront;
    CheeksKicker cheeks;
    ElapsedTime time;
    double middleDistance;
    public boolean ballPreviouslyDetected = false;

    public char left = ' ';
    public char middle = ' ';
    public char right = ' ';

    public Loader(HardwareMap hwmap) {
        time = new ElapsedTime();
        tofFront = hwmap.get(com.qualcomm.robotcore.hardware.DistanceSensor.class,"middleDistance");
        cheeks = new CheeksKicker(hwmap, time);
    }

    public double getDistance() {
        return tofFront.getDistance(DistanceUnit.MM);
    }

    public boolean intake() {
        middleDistance = getDistance();
        boolean ballDetected = middleDistance < 120;

        // Only run loading logic when a ball FIRST appears
        if (ballDetected && !ballPreviouslyDetected) {

            if(left == ' ') {
                cheeks.leftUp();
                left = 'F';
            } else if(right == ' ') {
                cheeks.rightUp();
                right = 'F';
            } else if (middle == ' ') {
                middle = 'F';
            }

            // mark that we handled this ball
            ballPreviouslyDetected = true;
        }

        if (!ballDetected) {
            ballPreviouslyDetected = false;
        }

        return left == 'F' && right == 'F' && middle == 'F';
    }
}