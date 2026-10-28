package org.firstinspires.ftc.teamcode.monarch;

import android.renderscript.ScriptGroup;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Monarch v1.0.3", group="Monarch")
public class Monarch extends OpMode {
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor backLeft = null;
    private DcMotor frontRight = null;
    private DcMotor frontLeft = null;
    private DcMotor backRight = null;
    private DcMotor intake = null;
    private DcMotor midtake = null;
    private DcMotor launch = null;

    double speed_modifier = 1.0; // scale the speed
    ElapsedTime InputCoolDown = new ElapsedTime();
    ElapsedTime LaunchTimer = new ElapsedTime();
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        // Initialize all  motors
        backLeft = hardwareMap.get(DcMotor.class, "motorFrontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "motorFrontRight");
        frontLeft = hardwareMap.get(DcMotor.class, "motorBackLeft");
        backRight = hardwareMap.get(DcMotor.class, "motorBackRight");
        intake = hardwareMap.get(DcMotor.class, "intake");
        midtake = hardwareMap.get(DcMotor.class, "midtake");
        launch = hardwareMap.get(DcMotor.class, "launch");



        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits START
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits START but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("Version", "1.0.4");

        drive();
        jankLaunch();
        telemetry.update();
    }

    private double launchPower;
    private void jankLaunch(){ // please replace with pid system later cuz this is so jank it is incredible
        //I would do it now but i dont want to make something hard to debug cuz ill be gone and my brain is fried.
        if (gamepad1.a && InputCoolDown.seconds()>.35){
            launchPower+=.1;

            InputCoolDown.reset();
        } else if (gamepad1.b && InputCoolDown.seconds()>.35){
            launchPower-=.1;

            InputCoolDown.reset();
        }

        telemetry.addData("launchPower", launchPower);

        // This code is so trash it is insane

        if (gamepad1.y){
            LaunchTimer.reset(); // i should really use a different timer here

            launch.setPower(launchPower);

        } else if (launch.getPower()!=0.0){
            if (LaunchTimer.seconds()>.3){
                launch.setPower(0.0);
            }
        }

    }


// drive mecanum robot centric
    private void drive() {


        // input for speed and driving control
        double axial   = -gamepad1.left_stick_y;
        double lateral =  gamepad1.left_stick_x;
        double yaw     =  gamepad1.right_stick_x;

        double frontLeftPower  = axial + lateral + yaw;
        double frontRightPower = axial - lateral - yaw;
        double backLeftPower   = axial - lateral + yaw;
        double backRightPower  = axial + lateral - yaw;

        if (gamepad1.left_bumper&&InputCoolDown.seconds()>.35){
            speed_modifier-=.1;
            speed_modifier = Math.max(.1,speed_modifier);

            InputCoolDown.reset();
        } else if (gamepad1.right_bumper&&InputCoolDown.seconds()>.35){
            speed_modifier+=.1;
            speed_modifier = Math.min(1.0,speed_modifier);

            InputCoolDown.reset();
        }

        //Modify and keep power under 1.0
        double max;
        max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        //should result in speed being capped at 1, which is max motor can have
        // ex. 3 / 1.0  = 3 as denom equation- but div by denom, 3/3 = 1 -- so it should be good since 3 is the highest possible input
        double denom = 1.0;
        if (max>speed_modifier){
            denom = max / speed_modifier;
        }
        double calcFrontLeftPower = (frontLeftPower/denom);
        double calcFrontRightPower = (frontRightPower/denom);
        double calcBackLeftPower = (backLeftPower/denom);
        double calcBackRightPower = (backRightPower/denom);

        // Send calculated power to wheels
        frontLeft.setPower(calcFrontLeftPower);
        frontRight.setPower(calcFrontRightPower);
        backLeft.setPower(calcBackLeftPower);
        backRight.setPower(calcBackRightPower);

        intake.setPower(gamepad1.left_trigger);
        midtake.setPower(gamepad1.right_trigger);

        telemetry.addData("Speed-Cap", speed_modifier);
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", calcFrontLeftPower, calcFrontRightPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", calcBackLeftPower, calcBackRightPower);
        telemetry.addData("Intake Power", intake.getPower());
        telemetry.addData("Midtake Power", midtake.getPower());

        telemetry.update(); // possibly redundant but none of this code has been tested at the moment so i am not trying to find out
    }
    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}


