package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.custom.Shooter;
import org.firstinspires.ftc.teamcode.custom.TagType;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.custom.AprilTag;

import org.firstinspires.ftc.teamcode.custom.AprilTag;
import org.firstinspires.ftc.teamcode.custom.Shooter;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "HeadingTester")

public class HeadingTester extends OpMode{
    private Follower follower;
    ElapsedTime time = null;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;
    private String telemetryStatus = "";
    private int pathStep;
    private final Pose poseOne = new Pose(0, 0, Math.toRadians(90));
    private final Pose poseTwo = new Pose(0, 0, Math.toRadians(0));

    private PathChain turnToOne, turnToTwo;

    public void buildPaths() {
        turnToOne = follower.pathBuilder()
                .addPath(new BezierLine(poseTwo, poseOne))
                .setLinearHeadingInterpolation(poseTwo.getHeading(), poseOne.getHeading())
                .build();

        turnToTwo = follower.pathBuilder()
                .addPath(new BezierLine(poseOne, poseTwo))
                .setLinearHeadingInterpolation(poseOne.getHeading(), poseTwo.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch(pathStep) {
            case 0:
                if(!follower.isBusy()) {
                    telemetryStatus = "Turning to pose 2";
//                    follower.followPath(turnToTwo);
                    follower.turnTo(poseTwo.getHeading());
                    telemetryStatus = "Waiting 2 seconds";
                    waitSeconds(2);
                    setPathState(1);
                }
            case 1:
                if(!follower.isBusy()) {
                    telemetryStatus = "Turning to pose 1";
//                    follower.followPath(turnToOne);
                    follower.turnTo(poseOne.getHeading());
                    telemetryStatus = "Waiting 2 seconds";
                    waitSeconds(2);
                    setPathState(0);
                }
        }
    }

    private void waitSeconds(double seconds) { // Woah look at this a fancy wait thing
        double start = time.seconds();
        while (time.seconds() - start < seconds) {
            try { Thread.sleep(10); } catch (Exception ignored) {}
        }
    }

    public void setPathState(int pState) { // PEDRO PEDRO change what path we're on
        pathStep = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        time = new ElapsedTime();

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(poseOne);
    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void loop() {
        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub for debugging
        telemetry.addData("Path Step", pathStep);
        telemetry.addData("Bot X", follower.getPose().getX());
        telemetry.addData("Bot Y", follower.getPose().getY());
        telemetry.addData("Bot Heading", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addLine();
        telemetry.addData("Auto Status", telemetryStatus);
        telemetry.update();
    }
}
