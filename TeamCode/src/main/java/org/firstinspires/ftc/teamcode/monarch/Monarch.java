package org.firstinspires.ftc.teamcode.monarch;

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

    double speed_modifier = 1.0; // scale the speed

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        // Initialize all 4 motors
        backLeft = hardwareMap.get(DcMotor.class, "motorFrontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "motorFrontRight");
        frontLeft = hardwareMap.get(DcMotor.class, "motorBackLeft");
        backRight = hardwareMap.get(DcMotor.class, "motorBackRight");
        intake = hardwareMap.get(DcMotor.class, "intake");

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
        telemetry.update();
    }

// drive mecanum robot centric - copy-pasted from basic thing at this current moment because we just need to spit stuff out
    private void drive() {


        // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
        double axial   = -gamepad1.left_stick_y;
        double lateral =  gamepad1.left_stick_x;
        double yaw     =  gamepad1.right_stick_x;

        // Combine the joystick requests for each axis-motion to determine each wheel's power.
        // Set up a variable for each drive wheel to save the power level for telemetry.
        double frontLeftPower  = axial + lateral + yaw;
        double frontRightPower = axial - lateral - yaw;
        double backLeftPower   = axial - lateral + yaw;
        double backRightPower  = axial + lateral - yaw;

        if (gamepad1.left_bumper){
            speed_modifier-=.1;
            speed_modifier = Math.max(.1,speed_modifier);
        } else if (gamepad1.right_bumper){
            speed_modifier+=.1;
            speed_modifier = Math.min(1.0,speed_modifier);
        }
     // do I need to maintain a ratio?? because of the mecanum

        //max of all of them
        double max;
        max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        double denom = 1.0;
        if (max>speed_modifier){
            denom = max / speed_modifier;
        }

   // increment max speed by like .1 and cooldown between presses -- or something idk  how it reads input - cap speed differently i dont really understand
        // how it does it currently but it has to get done
        // cap power abs

        // Send calculated power to wheels
        frontLeft.setPower(frontLeftPower/denom);
        frontRight.setPower(frontRightPower/denom);
        backLeft.setPower(backLeftPower/denom);
        backRight.setPower(backRightPower/denom);

        intake.setPower(gamepad1.left_trigger);

        telemetry.addData("Speed-Cap", speed_modifier);
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);

    }
    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}


