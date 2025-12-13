package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.custom.CheeksKicker;
import org.firstinspires.ftc.teamcode.custom.Shooterv2;
import org.firstinspires.ftc.teamcode.custom.TagType;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.custom.AprilTag;
import org.firstinspires.ftc.teamcode.custom.Intake;
import org.firstinspires.ftc.teamcode.custom.Loader;

@Autonomous
public class PedroRedTestV2 extends OpMode {
    private Character leftBall = 'P';
    private Character midBall = 'G';
    private Character rightBall = 'P';
    private char[] order;
    private String motif = "unknown";
    public String autoStatus = "";
    public String shooterStatus = "";
    public String queryStatus = "";
    private boolean doneShooting = false;
    private boolean doneLoading = false;
    private boolean doneDecidingArm = false;
    private AprilTag april;
    private Follower follower;
    private CheeksKicker cheeks;
    private Shooterv2 shooter;
    private Intake intake;
    private Loader loader;
    private Timer pathTimer, actionTimer, opmodeTimer;
    ElapsedTime time = null;

    private int pathStep; // Which path we're on - this will start at zero (score preloaded balls) then go to one once we go to the next set

    private final Pose startPose = new Pose(109, 135, Math.toRadians(270)); // Start Pose - Bot at the corner of the red goal with it facing out into the field (feeder against the wall)
    private final Pose scorePose = new Pose(96, 96, Math.toRadians(50)); // Scoring pose - red goal
    private final Pose motifPose = new Pose(96, 96, Math.toRadians(120)); // Pose to Read Motif Pattern - from scoring pose

    // These ball poses have the bot facing backwards (Intake facing balls)
    private final Pose ppgPose = new Pose(96, 84, Math.toRadians(180)); // Highest (First Set) of Artifacts - closest to red goal
    private final Pose ppgRam = new Pose(130, 84, Math.toRadians(180));
    private final Pose pgpPose = new Pose(96, 60, Math.toRadians(180)); // Middle (Second Set) of Artifacts - halfway
    private final Pose pgpRam = new Pose(130, 60, Math.toRadians(180));
    private final Pose gppPose = new Pose(96, 36, Math.toRadians(180)); // Lowest (Third Set) of Artifacts - furthest from red goal
    private final Pose gppRam = new Pose(130, 36, Math.toRadians(180));

    private Path turnToMotif;
    private PathChain grabPpg, scorePpg, grabPgp, scorePgp, grabGpp, scoreGpp, ramPpg, ramPgp, ramGpp;

    public void buildPaths() {
        autoStatus = "Building paths...";
        turnToMotif = new Path(new BezierLine(startPose, motifPose)); // Make a path from starting pose to motif pose
        turnToMotif.setLinearHeadingInterpolation(startPose.getHeading(), motifPose.getHeading()); // Make the path also change our heading

        grabPpg = follower.pathBuilder() // Build this line:
                .addPath(new BezierLine(scorePose, ppgPose)) // From score pose to ppg pose
                .setLinearHeadingInterpolation(scorePose.getHeading(), ppgPose.getHeading()) // From score heading to ppg heading
                .build();

        scorePpg = follower.pathBuilder() // Go from grabbing ppg to the score pose
                .addPath(new BezierLine(ppgPose, scorePose))
                .setLinearHeadingInterpolation(ppgPose.getHeading(), scorePose.getHeading())
                .build();

        grabPgp = follower.pathBuilder() // Build this line:
                .addPath(new BezierLine(scorePose, pgpPose)) // From score pose to pgp pose
                .setLinearHeadingInterpolation(scorePose.getHeading(), pgpPose.getHeading()) // From score heading to pgp heading
                .build();

        scorePgp = follower.pathBuilder() // Go from grabbing pgp to the score pose
                .addPath(new BezierLine(pgpPose, scorePose))
                .setLinearHeadingInterpolation(pgpPose.getHeading(), scorePose.getHeading())
                .build();

        grabGpp = follower.pathBuilder() // Build this line:
                .addPath(new BezierLine(scorePose, gppPose)) // From score pose to gpp pose
                .setLinearHeadingInterpolation(scorePose.getHeading(), gppPose.getHeading()) // From score heading to gpp heading
                .build();

        scoreGpp = follower.pathBuilder() // Go from grabbing gpp to the score pose
                .addPath(new BezierLine(gppPose, scorePose))
                .setLinearHeadingInterpolation(gppPose.getHeading(), scorePose.getHeading())
                .build();



        ramPpg = follower.pathBuilder()
                .addPath(new BezierLine(ppgPose, ppgRam))
                .setLinearHeadingInterpolation(ppgPose.getHeading(), ppgRam.getHeading())
                .build();

        ramPgp = follower.pathBuilder()
                .addPath(new BezierLine(pgpPose, pgpRam))
                .setLinearHeadingInterpolation(pgpPose.getHeading(), pgpRam.getHeading())
                .build();

        ramGpp = follower.pathBuilder()
                .addPath(new BezierLine(gppPose, gppRam))
                .setLinearHeadingInterpolation(gppPose.getHeading(), gppRam.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch(pathStep) { // If we're on step _ were going to do this (Main nav + shooting loop basically)
            // This will increment by 5 so more can be added later
            //todo implement timer-based path fallback
            case -1:
                autoStatus = "Auto finished";
                shooter.spinDown();
                break;

            case 1: // Navigate to motif pose
                if(!follower.isBusy()) {
                    autoStatus = "[nav] Going to motif pose";
                    follower.followPath(turnToMotif);
                    setPathState(5);
                }
                break;

            case 5: // Process camera info
                if(!follower.isBusy()) {
                    autoStatus = "[cam] Reading april tag";
                    // wait 0.5s after entering state 5 before reading tag info
                    if (pathTimer.getElapsedTime() > 500) {
                        TagType motifTagType = null;
                        april.updateTagInfo();
                        motifTagType = april.getTagInfo(TagType.meaning, 0); // Get info from first detected april tag
                        if (motifTagType == null)
                            motifTagType = TagType.PGP; // If read failed, default to PGP
                        motif = motifTagType.name(); // Gets the name of the enum - so if TagType.PGP it returns PGP
                        setPathState(10);
                    }
                }
                // Update camera info every loop regardless cause... idk
                april.updateTagInfo();
                break;

            case 10: // Nav to scoring pose
                if(!follower.isBusy()) {
                    autoStatus = "[nav] Turning to scoring pose";
                    follower.turnTo(scorePose.getHeading()); // Turn to scorePose heading IN RADIANS
                    setPathState(20); // todo add color
                }
                break;

            case 15: // read color sensors and pharse info
                // todo bump from above
                break;

            case 20: // querry shooting order and pharse info
                autoStatus = "[qry] Fetching shooting order";
                order = query(motif, leftBall, midBall, rightBall);
                setPathState(25);
                break;

            case 25: // start shooting process (set flywheel velocity, set arms, etc)
                autoStatus = "[shoot] Starting shooting process";
                if (pathTimer.getElapsedTime() > 2000) {
                    shooter.spinUp();
                    cheeks.kickerRetract();
                    cheeks.leftUp();
                    cheeks.rightUp();
                    setPathState(30);
                }
                break;

            case 30: // Shoot ball 1
                autoStatus = "[shoot] Shooting first ball";
                doneShooting = shooter.shootBall(); // shooter manages its own internal states
                if (doneShooting) {
                    doneShooting = false;
                    midBall = ' ';    // mark middle ball fired
                    setPathState(35);
                }
                break;

            case 35: // Shoot ball 2
                autoStatus = "[sort] Picking second ball arm";
                if(!doneDecidingArm) {
                    if (order[1] == leftBall) {
                        cheeks.leftDown();
                        leftBall = ' ';
                        doneDecidingArm = true;
                        shooterStatus = "[sort] Picked left arm";
                    } else if (order[1] == rightBall) {
                        cheeks.rightDown();
                        rightBall = ' ';
                        doneDecidingArm = true;
                        shooterStatus = "[sort] Picked right arm";
                    } else { // reserve to left if ambiguous
                        cheeks.leftDown();
                        leftBall = ' ';
                        doneDecidingArm = true;
                        shooterStatus = "[sort] Picked left arm (reserve)";
                    }
                }

                // wait 1 second after deploying cheek before actually firing
                if (pathTimer.getElapsedTime() > 1000) {
                    doneDecidingArm = false;
                    setPathState(36);
                }
                break;

            case 36:
                // Fire second ball
                autoStatus = "[shoot] Shooting second ball";
                doneShooting = shooter.shootBall();
                if (doneShooting) {
                    doneShooting = false;
                    setPathState(40);
                }
                break;

            case 40: // Fire third ball
                autoStatus = "[sort] Picking third ball arm";
                if(!doneDecidingArm) {
                    if (order[2] == leftBall && leftBall != ' ') {
                        cheeks.leftDown();
                        leftBall = ' ';
                        doneDecidingArm = true;
                        shooterStatus = "[sort] Picked left arm";
                    } else if (order[2] == rightBall && rightBall != ' ') {
                        cheeks.rightDown();
                        rightBall = ' ';
                        doneDecidingArm = true;
                        shooterStatus = "[sort] Picked right arm";
                    } else if (rightBall != ' ') {
                        cheeks.rightDown();
                        rightBall = ' ';
                        doneDecidingArm = true;
                        shooterStatus = "[sort] Picked right arm (reserve)";
                    } else {
                        cheeks.leftDown();
                        leftBall = ' ';
                        doneDecidingArm = true;
                        shooterStatus = "[sort] Picked left arm (reserve)";
                    }
                }

                // wait 1 second after deploying cheek before firing
                if (pathTimer.getElapsedTime() > 1000) {
                    doneDecidingArm = false;
                    setPathState(41);
                }
                break;

            case 41:
                autoStatus = "[shoot] Shooting third ball";
                doneShooting = shooter.shootBall();
                if (doneShooting) {
                    doneShooting = false;
                    setPathState(45);
                }
                break;
            case 45:
                if (!follower.isBusy()) {
                    autoStatus = "[nav] Going to grabPPG pose";
                    follower.followPath(grabPpg);
                    setPathState(-1);
                }
                break;

            case 46:
                if(!follower.isBusy()) {
                    if(loader.intake()) {
                        setPathState(-1);
                    } else {
//                        follower.followPath(ramPpg);
                    }
                }
                break;
            case 50:
                if(!follower.isBusy()) {
                    autoStatus = "[nav] Going to score pose";
                    follower.followPath(scorePpg);
                    setPathState(55);
                }
                break;
            case 55:
                if(!follower.isBusy()) {
                    autoStatus = "[nav] Going to grabPGP pose";
                    follower.followPath(grabPgp);
                    setPathState(60);
                }
                break;
            case 60:
                if(!follower.isBusy()) {
                    autoStatus = "[nav] Going to score pose";
                    follower.followPath(scorePgp);
                    setPathState(65);
                }
                break;
            case 65:
                if(!follower.isBusy()) {
                    autoStatus = "[nav] Going to grabGPP pose";
                    follower.followPath(grabGpp);
                    setPathState(70);
                }
                break;
            case 70:
                if(!follower.isBusy()) {
                    autoStatus = "[nav] Going to score pose";
                    follower.followPath(scoreGpp);
                    setPathState(-1);
                }
                break;
        }
    }


    public void setPathState(int pState) { // Updates path state and resets path timer
        pathStep = pState;
        pathTimer.resetTimer();
    }

    public static char[] query(String motif, char armLeft, char active, char armRight) {
        /*
        This method takes in the motif pattern and the balls in the left, right, and middle chambers
        of the bot, and returns the optimal shooting pattern if we want the highest chance of
        satisfying the pattern.
        */
        char[] pattern = motif.toCharArray();

        char leftBall = Character.toUpperCase(armLeft);
        char midBall  = Character.toUpperCase(active);
        char rightBall = Character.toUpperCase(armRight);

        char[] order = new char[3]; // Order is a array of three characters
        order[0] = midBall; // First ball in the order is the ball in the middle

        boolean firstMotif = (pattern[0] == midBall); // is the middle ball the first on the motif?
        // Set next ball based on if the first motif was satisfied
        char next = firstMotif ? pattern[1] : pattern[0];

        if (leftBall == next) {
            order[1] = leftBall; // Next ball is left ball
            leftBall = ' ';
        } else if (rightBall == next) {
            order[1] = rightBall; // Next ball is right ball
            rightBall = ' ';
        } else {
            order[1] = leftBall; // Next ball is left ball [Reserve]
            leftBall = ' ';
        }

        if (leftBall != ' ') { // If we used the left ball, set it as next in order
            order[2] = leftBall;
            leftBall = ' ';
        } else if (rightBall != ' ') { // If we used the right ball, set it as next in order
            order[2] = rightBall;
            rightBall = ' ';
        } else {
            order[2] = 'P';
            // This should never happen but...?
        }

        return order; // Return final order array
    }

    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        time = new ElapsedTime();

        shooter = new Shooterv2(hardwareMap);
        cheeks = new CheeksKicker(hardwareMap, time);
        april = new AprilTag(hardwareMap);
        intake = new Intake(hardwareMap, time);
        loader = new Loader(hardwareMap);

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);
    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(1);
    }

    @Override
    public void loop() {
        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update(); // Updates pedro's understanding of the bot
        autonomousPathUpdate(); // Main state machine

        // Telemetry to Driver Hub for debugging
        telemetry.addData("Path Step", pathStep);
        telemetry.addData("Bot X", follower.getPose().getX());
        telemetry.addData("Bot Y", follower.getPose().getY());
        telemetry.addData("Bot Heading", follower.getPose().getHeading());
        telemetry.addData("Loaded Balls L/M/R", "%c, %c, %c", leftBall, midBall, rightBall);
        telemetry.addData("Motif", motif);
        telemetry.addLine();
        telemetry.addData("Auto Status", autoStatus);
        telemetry.addData("Shooter Status", shooterStatus + ", - Step: " + shooter.getStep());
        telemetry.addData("Query Status", queryStatus);
        telemetry.addData("Servo Debug - Kicker", cheeks.kicker.getPosition());
        telemetry.addData("Servo Debug - Left Arm", cheeks.leftCheek.getPosition());
        telemetry.addData("Servo Debug - Right Arm", cheeks.rightCheek.getPosition());
        telemetry.addData("Distance Debug - Middle", loader.getDistance());
        telemetry.addData("Balls Loader L/M/R", "%c, %c, %c", loader.left, loader.middle, loader.right);
        telemetry.addData("Loader ball detected", loader.ballPreviouslyDetected);
        telemetry.update();
    }
}
