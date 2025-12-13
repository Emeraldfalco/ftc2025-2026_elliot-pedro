package org.firstinspires.ftc.teamcode.custom;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Intake {
    double rejectStart = 0;
    double rejectEnd = 0;
    double intakeStart = 0;
    double intakeEnd = 0;
    ElapsedTime time = null;

    DcMotorEx intakeMot;
    public Intake(HardwareMap hwmap, ElapsedTime elapsedTime) {
        intakeMot = hwmap.get(DcMotorEx.class, "intake");
        time = elapsedTime;
    }

    public void intake(){
        intakeMot.setPower(1);
    }
    public boolean reject (double howLong, double currentTime){
        if (rejectEnd == 0){
            rejectStart = currentTime;
            rejectEnd = currentTime + howLong;
        }
        if (currentTime > rejectStart + howLong){
            rejectStart = 0;
            rejectEnd = 0;
            hold();
            return true;
        } else{
            reject();
            return false;
        }
    }


    public void reject(){
        intakeMot.setPower(-1);
    }


    public boolean intake (double howLong, double currentTime){
        if (intakeEnd == 0){
            intakeStart = currentTime;
            intakeEnd = currentTime + howLong;
        }
        if (currentTime > intakeStart + howLong){
            intakeStart = 0;
            intakeEnd = 0;
            hold();
            return true;
        } else{
            intake();
            return false;
        }


    }

    public void hold() {
        intakeMot.setPower(0);
    }   
}