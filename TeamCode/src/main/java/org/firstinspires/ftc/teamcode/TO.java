//gamepad1:                                     gamepad2:
//A 前夹开、小臂出
//B 前夹关（小臂向下又向上，夹取动作）
//X 前夹开
//Y 滑轨收，小臂收
//left 前夹左转
//down 滑轨收
//right 前夹右转
//up 滑轨出，小臂出，前夹开
//LB 速度0.3
//LT 前夹回中
//RB 速度1
//RT 后小臂向后（手动阶段碰杆）


package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TO",group="TELEOP")
public class TO extends LinearOpMode {

    private DcMotor motor_lf; private DcMotor motor_lb; private DcMotor motor_rf; private DcMotor motor_rb;

    //parameter setting
    float move;   float fun;  float turn;

    double SPEED;


    @Override

    public void runOpMode() {



        motor_lf = hardwareMap.get(DcMotor.class, "motor_lf");
        motor_lb = hardwareMap.get(DcMotor.class, "motor_lb");
        motor_rf = hardwareMap.get(DcMotor.class, "motor_rf");
        motor_rb = hardwareMap.get(DcMotor.class, "motor_rb");

        motor_lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_lb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_rb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        motor_rf.setDirection(DcMotor.Direction.REVERSE);
        motor_rb.setDirection(DcMotor.Direction.REVERSE);


        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {
                telemetry.update();

                if (gamepad1.right_bumper) {
                    SPEED = 1.0;

                } else if (gamepad1.left_bumper) {
                    SPEED = 0.3;
                } else {
                    SPEED = 0.5;
                }


                move = gamepad1.left_stick_y;
                fun = gamepad1.left_stick_x;
                turn = gamepad1.right_stick_x;
                motor_lf.setPower(SPEED * (move + (fun + turn)));
                motor_lb.setPower(SPEED * (move - (fun - turn)));
                motor_rf.setPower(SPEED * (move - (fun + turn)));
                motor_rb.setPower(SPEED * (move + (fun - turn)));
            }
        }
    }

}