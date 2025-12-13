package org.firstinspires.ftc.teamcode.custom;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Arrays;

// Heya! All this code does is basically make my life easier by putting all of the launching stuff in one file and make one big ahh function that does all the work for me. This WILL be unreadable :)
public class Shooter {
    ElapsedTime time = null;
    DcMotorEx flywheel = null;
    CheeksKicker cheeks;
    String telemetryStatus = "";
    Telemetry telemetry;
    Follower follower;

    double p = 300;
    double i = 1;
    double d = 4;
    double f = 10;

    double flyWheelTarget = 1200;

    int shootState = 1;
    boolean shootDone = false;

    public Shooter(HardwareMap hwmap, Telemetry telemetry, Follower follower) {
        time = new ElapsedTime();
        cheeks = new CheeksKicker(hwmap, time);
        this.telemetry = telemetry;
        this.follower = follower;

        flywheel = hwmap.get(DcMotorEx.class, "flywheel");
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(p, i, d, f)
        );
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setFlywheelVelocity(double velocity){
        flyWheelTarget = velocity;
        flywheel.setVelocity(velocity);
        telemetryStatus = "Set Flywheel Velocity to " + velocity;
        updateTelemetry();
    }

    public double getFlywheelVelocity() {
        return flywheel.getVelocity();
    }

    public boolean flywheelReachedTargetVelocity (){
        return (flywheel.getVelocity() < flyWheelTarget * 1.05) && (flywheel.getVelocity() > flyWheelTarget * 0.95);
    }

    public void spinUp() {
        flywheel.setVelocity(flyWheelTarget);
        telemetryStatus = "Flywheel spin up";
        updateTelemetry();
    }


    public void spinDown() {
        flywheel.setVelocity(0);
        telemetryStatus = "Flywheel spin down";
        updateTelemetry();
    }


    // Oh no its the big complicated function :((((
    public boolean shoot(String motif, char armLeft, char active, char armRight) {
        char[] pattern = motif.toCharArray();
        telemetryStatus = "Shooting pattern with motif " + Arrays.toString(pattern);
        updateTelemetry();

        char leftBall = Character.toUpperCase(armLeft);
        char midBall  = Character.toUpperCase(active);
        char rightBall = Character.toUpperCase(armRight);
        telemetryStatus = "Loaded balls L/M/R: " + leftBall + " / " + midBall + " / " + rightBall;
        updateTelemetry();


        spinUp();

        boolean firstMotif = (pattern[0] == midBall);
        telemetryStatus = "Mid ball shot - matched first motif: " + firstMotif;
        updateTelemetry();

        telemetryStatus = "Shooting ball...";

        if (!shootBall()) {
        }
        updateTelemetry();
        midBall = ' ';

        char next = firstMotif ? pattern[1] : pattern[0];
        telemetryStatus = "Next ball: " + next;

        if (leftBall == next) {
            telemetryStatus = "Letting down left ball";
            updateTelemetry();
            cheeks.leftDown();
            leftBall = ' ';
        } else if (rightBall == next) {
            telemetryStatus = "Letting down right ball";
            updateTelemetry();
            cheeks.rightDown();
            rightBall = ' ';
        } else {
            telemetryStatus = "Letting down left ball [Reserve]";
            updateTelemetry();
            cheeks.leftDown();
            leftBall = ' ';
        }

        telemetryStatus = "Shooting ball...";
        updateTelemetry();
        if (!shootBall()) {
        }

        if (leftBall != ' ') {
            telemetryStatus = "Letting down left ball";
            updateTelemetry();
            cheeks.leftDown();
            leftBall = ' ';
        } else if (rightBall != ' ') {
            telemetryStatus = "Letting down right ball";
            updateTelemetry();
            cheeks.rightDown();
            rightBall = ' ';
        } else {
            telemetryStatus = "How did we get here?";
            updateTelemetry();
            // should never happen unless input was invalid
        }

        telemetryStatus = "Shooting ball...";
        updateTelemetry();
        if (!shootBall()) {
        }
        spinDown();
        telemetryStatus = "Finished shooting load";
        updateTelemetry();
        return true;
    }

    public boolean shootBall() {
        switch (shootState){
            case 1:
                cheeks.kickerExtend();
                shootDone = Wait.wait(2, time.seconds());
                if (shootDone){
                    shootDone = false;
                    shootState = 2;
                }
                break;
            case 2:
                cheeks.kickerRetract();
                shootDone = false;
                shootDone = Wait.wait(2, time.seconds());
                if (shootDone){
                    shootDone = false;
                    shootState = 1;
//                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

//    private void waitSeconds(double seconds) { // Woah look at this a fancy wait thing
//        double start = time.seconds();
//        while (time.seconds() - start < seconds) {
//            try { Thread.sleep(10); } catch (Exception ignored) {}
//        }
//    }

    private void updateTelemetry() {
        telemetry.addData("Shooter Status: ", telemetryStatus);
        telemetry.update();
    }
}
