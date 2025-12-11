package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "servo_test",group="TELEOP")
@Disabled
public class servo_test extends LinearOpMode {

    private Servo lift;


    @Override

    public void runOpMode() {


        lift = hardwareMap.get(Servo.class, "servo_turn");
        //lf.setTargetPosition(0);

        //LF = lf.getCurrentPosition();

        waitForStart();
        if (opModeIsActive()) {

            while (opModeIsActive()) {


                if(gamepad1.x){
                    lift.setPosition(0);
                }
                if(gamepad1.y){
                    lift.setPosition(0.5);
                }
                else{
                    lift.setPosition(1);
                }






                telemetry.update();
            }
        }
    }
}





