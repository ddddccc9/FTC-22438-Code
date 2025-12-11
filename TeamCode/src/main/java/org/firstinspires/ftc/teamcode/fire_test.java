/**
 * 发射功能测试，测试能否正常发射小球
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "fire_test",group="TELEOP")
@Disabled
public class fire_test extends LinearOpMode {

    private DcMotor motor_upper;
    private DcMotor motor_lower;
    private DcMotor motor_intake;
    private Servo lift;
    private Servo turn;
    private double Angle;


    @Override

    public void runOpMode() {


        motor_upper = hardwareMap.get(DcMotor.class, "motor_upper");
        motor_lower = hardwareMap.get(DcMotor.class, "motor_lower");
        motor_intake = hardwareMap.get(DcMotor.class, "motor_intake");
        lift = hardwareMap.get(Servo.class, "servo_lift");
        turn = hardwareMap.get(Servo.class, "servo_turn");
        //lf.setTargetPosition(0);
        motor_upper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_lower.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor_lower.setDirection(DcMotor.Direction.REVERSE);


        Angle=0;
        //LF = lf.getCurrentPosition();

        waitForStart();
        if (opModeIsActive()) {

            while (opModeIsActive()) {

                if(gamepad1.b){
                    motor_upper.setPower(1);
                    motor_lower.setPower(1);
                }
                else {
                    motor_upper.setPower(0);
                    motor_lower.setPower(0);
                }


                if(gamepad1.y) {
                    turn.setPosition(0.19);
                }
                else if(gamepad1.x){
                    turn.setPosition(0.563);
                }
                else{
                    turn.setPosition(0.94);
                }






                if(gamepad1.a){
                    lift.setPosition(0.3);
                }
                else {
                    lift.setPosition(0);
                }

                if(gamepad1.dpad_down){
                    motor_intake.setPower(1);
                }
                else{
                    motor_intake.setPower(0);
                }




                telemetry.addData("Angle:",Angle);


                telemetry.update();
            }
        }
    }
}





