package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;



@Autonomous(name = "AUTO_RED",group="Autonomous")

public class AUTO_RED extends LinearOpMode {

    private DcMotor lf;
    private DcMotor lb;
    private DcMotor rf;
    private DcMotor rb;
    private Servo lift;
    private Servo turn;

    private Base base;


    @Override
    public void runOpMode() {

        lf = hardwareMap.get(DcMotor.class, "motor_lf");
        lb = hardwareMap.get(DcMotor.class, "motor_lb");
        rf = hardwareMap.get(DcMotor.class, "motor_rf");
        rb = hardwareMap.get(DcMotor.class, "motor_rb");

        turn = hardwareMap.get(Servo.class, "servo_turn");
        lift = hardwareMap.get(Servo.class, "servo_lift");

        base=new Base(lf,lb,rf,rb,lift,turn);

//
//        int LF;
//        int LB;
//        int RB;
//        int RF;
//
//        lf.setTargetPosition(0);
//        lb.setTargetPosition(0);
//        rf.setTargetPosition(0);
//        rb.setTargetPosition(0);
//        lf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        rf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        lb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        rb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//        lb.setDirection(DcMotor.Direction.REVERSE);
//        lf.setDirection(DcMotor.Direction.REVERSE);
//
//        LF = lf.getCurrentPosition();
//        LB = lb.getCurrentPosition();
//        RB = rb.getCurrentPosition();
//        RF = rf.getCurrentPosition();


        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            base.MoveTo(0.6,0,2100,0,true);

            base.MoveTo(0.2,0,0,300,true);




            telemetry.update();
        }
    }
}

