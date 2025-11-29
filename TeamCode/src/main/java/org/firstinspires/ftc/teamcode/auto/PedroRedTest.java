package org.firstinspires.ftc.teamcode.auto;

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

@Autonomous
public class PedroRedTest extends OpMode {
    // Hello guys welcome back to another Youtube video
    // today we will show you how to auto the ball
    private Character leftBall = 'P';
    private Character midBall = 'G';
    private Character rightBall = 'P';
    private AprilTag april;
    private Follower follower;
    private Shooter shooter;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private int pathStep; // Which path we're on - this will start at zero (score preloaded balls) then go to one once we go to the next set

    private final Pose startPose = new Pose(109, 135, Math.toRadians(270)); // Start Pose - Bot at the corner of the red goal with it facing out into the field (feeder against the wall)
    private final Pose scorePose = new Pose(96, 96, Math.toRadians(45)); // Scoring pose - red goal
    private final Pose motifPose = new Pose(96, 96, Math.toRadians(120)); // Scoring pose - red goal

    // These ball poses have the bot facing backwards (Intake facing balls)
    private final Pose ppgPose = new Pose(96, 84, Math.toRadians(180)); // Highest (First Set) of Artifacts - closest to red goal
    private final Pose pgpPose = new Pose(96, 60, Math.toRadians(180)); // Middle (Second Set) of Artifacts - halfway
    private final Pose gppPose = new Pose(96, 36, Math.toRadians(180)); // Lowest (Third Set) of Artifacts - furthest from red goal

    private Path scorePreload;
    private PathChain grabPpg, scorePpg;

    public void buildPaths() {
        scorePreload = new Path(new BezierLine(startPose, scorePose)); // Hey pedro make a line from our start pose to our score pose
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading()); // Hey pedro make our heading change too but make it complicated

        grabPpg = follower.pathBuilder() // Hey pedro umm like make a line pls
                .addPath(new BezierLine(scorePose, ppgPose)) // Hey pedro make the line start at the scorePose and end at the ppgPose
                .setLinearHeadingInterpolation(scorePose.getHeading(), ppgPose.getHeading()) // Hey pedro ake the headings do something pls
                .build(); // Hey pedro im finished build pls

        scorePpg = follower.pathBuilder() // Go from grabbing ppg to the score pose
                .addPath(new BezierLine(ppgPose, scorePose))
                .setLinearHeadingInterpolation(ppgPose.getHeading(), scorePose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch(pathStep) { // If we're on step _ were going to do this (Main nav + shooting loop basically)
            case 0:
                follower.followPath(scorePreload); // PEDRO PEDRO GO FOLLOW THIS PATH!!!1!1!1!
                follower.turnToDegrees(motifPose.getHeading());
                april.updateTagInfo();
                TagType motifTagType = april.getTagInfo(TagType.meaning, 0);
                String motif = motifTagType.name(); // My extreme intelligence in making the apriltag class
                follower.turnToDegrees(scorePose.getHeading());
                shooter.shoot(motif, leftBall, midBall, rightBall);
                setPathState(1); // Ya we're done you can go to the next path now
                break;
        }
    }

    public void setPathState(int pState) { // PEDRO PEDRO change what path we're on
        pathStep = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void loop() {

        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub for debugging
        telemetry.addData("path state", pathStep);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        shooter = new Shooter(hardwareMap);
        april = new AprilTag(hardwareMap);

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);

    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }
}
