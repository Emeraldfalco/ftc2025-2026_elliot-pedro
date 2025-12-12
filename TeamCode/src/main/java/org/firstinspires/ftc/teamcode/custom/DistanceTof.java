package org.firstinspires.ftc.teamcode.custom;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DistanceTof {
    private com.qualcomm.robotcore.hardware.DistanceSensor tofFront;
    double middleDistance;

    public DistanceTof(HardwareMap hwmap) {
        tofFront = hwmap.get(com.qualcomm.robotcore.hardware.DistanceSensor.class,"middleDistance");
    }

    public double getDistance(String location) {
        switch (location) {
            case "middle":
                return tofFront.getDistance(DistanceUnit.MM);
            default:
                return Double.NaN;
        }
    }
}