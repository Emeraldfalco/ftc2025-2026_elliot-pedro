package org.firstinspires.ftc.teamcode.custom;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.custom.TagType;

// Heya! All this code does is basically make my life easier by putting all of the launching stuff in one file and make one big ahh function that does all the work for me. This WILL be unreadable :)
public class Shooter {
    ElapsedTime time = null;
    DcMotorEx flywheel = null;
    CheeksKicker cheeks;

    double p = 300;
    double i = 1;
    double d = 4;
    double f = 10;

    double flyWheelTarget = 1350;

    public Shooter(HardwareMap hwmap) {
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

    public void setFlywheelVelocity(double velocity){
        flyWheelTarget = velocity;
        flywheel.setVelocity(velocity);
    }

    public double getFlywheelVelocity() {
        return flywheel.getVelocity();
    }

    public boolean flywheelReachedTargetVelocity (){
        return (flywheel.getVelocity() < flyWheelTarget * 1.05) && (flywheel.getVelocity() > flyWheelTarget * 0.95);
    }

    public void spinUp() {
        flywheel.setVelocity(flyWheelTarget);
    }

    public void spinDown() {
        flywheel.setVelocity(0);
    }


    // Oh no its the big complicated function :((((
    public void shoot(String motif, char armLeft, char active, char armRight) {
        char[] pattern = motif.toCharArray();

        char leftBall = Character.toUpperCase(armLeft);
        char midBall  = Character.toUpperCase(active);
        char rightBall = Character.toUpperCase(armRight);

        spinUp();

        boolean firstMotif = (pattern[0] == midBall);

        shootBall();
        midBall = ' ';

        char next = firstMotif ? pattern[1] : pattern[0];

        if (leftBall == next) {
            cheeks.leftDown();
            leftBall = ' ';
        } else if (rightBall == next) {
            cheeks.rightDown();
            rightBall = ' ';
        } else {
            cheeks.leftDown();
            leftBall = ' ';
        }

        shootBall();

        if (leftBall != ' ') {
            cheeks.leftDown();
            leftBall = ' ';
        } else if (rightBall != ' ') {
            cheeks.rightDown();
            rightBall = ' ';
        } else {
            // should never happen unless input was invalid
        }

        shootBall();
        spinDown();
    }

    private void shootBall() {
        while (!flywheelReachedTargetVelocity()) {
            try { Thread.sleep(10); } catch (Exception ignored) {}
        }
        waitSeconds(1);
        cheeks.kickerExtend();
        waitSeconds(2);
        cheeks.kickerRetract();
    }

    private void waitSeconds(double seconds) { // Woah look at this a fancy wait thing
        double start = time.seconds();
        while (time.seconds() - start < seconds) {
            try { Thread.sleep(10); } catch (Exception ignored) {}
        }
    }
}
