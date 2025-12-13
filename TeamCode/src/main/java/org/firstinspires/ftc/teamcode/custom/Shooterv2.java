package org.firstinspires.ftc.teamcode.custom;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Shooterv2 {
    DcMotorEx flywheel = null;
    CheeksKicker cheeks;
    ElapsedTime time = null;
    double p = 300;
    double i = 1;
    double d = 4;
    double f = 10;

    double flyWheelTarget = 1200;

    int shootState = 1;
    boolean doneWaiting = false;

    public Shooterv2(HardwareMap hwmap) {
        time = new ElapsedTime();
        cheeks = new CheeksKicker(hwmap, time);

        flywheel = hwmap.get(DcMotorEx.class, "flywheel");
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(p, i, d, f)
        );
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public boolean flywheelReachedTargetVelocity (){
        return (flywheel.getVelocity() < flyWheelTarget * 1.05) && (flywheel.getVelocity() > flyWheelTarget * 0.95);
    }

    public void spinUp() {
        flywheel.setVelocity(flyWheelTarget);
    }

    public void spinUp(double speed) {
        flywheel.setVelocity(speed);
    }



    public void spinDown() {
        flywheel.setVelocity(0);
    }

    public int getStep() {
        return shootState;
    }

    public boolean shootBall() { // Runnin it back
        switch (shootState){
            case 1:
                flywheel.setVelocity(flyWheelTarget);
                if(flywheelReachedTargetVelocity()) {
                    shootState = 2;
                }
                break;
            case 2:
                cheeks.kickerExtend(); // Extend kicker
                shootState = 3; // Bump state
                break;
            case 3:
                doneWaiting = Wait.wait(2, time.seconds());
                if (doneWaiting){ // If we waited 2 seconds
                    doneWaiting = false;
//                    if(cheeks.kicker.getPosition() != cheeks.kickerMax) {
//                        //todo implement where if position is not maxed after a while it will self correct
//                    }
                    cheeks.kickerRetract();
                    shootState = 4;
                }
                break;
            case 4:
                doneWaiting = Wait.wait(2, time.seconds());
                if(doneWaiting) {
                    shootState = 1;
                    return true;
                }
        }
        return false;
    }
}
