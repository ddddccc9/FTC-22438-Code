package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;



@Autonomous(name = "AUTO_R",group="Autonomous")

public class AUTO_R extends LinearOpMode {

    private final int READ_PERIOD = 1;

    private HuskyLens huskyLens1;
    private DcMotor lf;
    private DcMotor lb;
    private DcMotor rf;
    private DcMotor rb;


    @Override
    public void runOpMode() {
        huskyLens1 = hardwareMap.get(HuskyLens.class, "huskylens1");


        Deadline rateLimit = new Deadline(READ_PERIOD, TimeUnit.NANOSECONDS);

        // fS = hardwareMap.get(Servo.class, "fS");

        lf = hardwareMap.get(DcMotor.class, "lf");
        lb = hardwareMap.get(DcMotor.class, "lb");
        rf = hardwareMap.get(DcMotor.class, "rf");
        rb = hardwareMap.get(DcMotor.class, "rb");
        int LF;
        int LB;
        int RB;
        int RF;
        int R1;
        int R2;


        lf = hardwareMap.get(DcMotor.class, "lf");
        rf = hardwareMap.get(DcMotor.class, "rf");
        lb = hardwareMap.get(DcMotor.class, "lb");
        rb = hardwareMap.get(DcMotor.class, "rb");
        lf.setTargetPosition(0);
        lb.setTargetPosition(0);
        rf.setTargetPosition(0);
        rb.setTargetPosition(0);
        lf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lb.setDirection(DcMotor.Direction.REVERSE);
        lf.setDirection(DcMotor.Direction.REVERSE);
        LF = lf.getCurrentPosition();
        LB = lb.getCurrentPosition();
        RB = rb.getCurrentPosition();
        RF = rf.getCurrentPosition();

        rateLimit.expire();


        if (!huskyLens1.knock()) {
            telemetry.addData(">>", "Problem communicating with " + huskyLens1.getDeviceName());
        } else {
            telemetry.addData(">>", "Press start to continue");
        }


        huskyLens1.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);

        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {


            LF = lf.getCurrentPosition();
            LB = lb.getCurrentPosition();
            RB = rb.getCurrentPosition();
            RF = rf.getCurrentPosition();
            lf.setTargetPosition(LF - 450);
            lf.setPower(1);
            rb.setTargetPosition(RB + 450);
            rb.setPower(1);
            rf.setTargetPosition(RF + 450);
            rf.setPower(1);
            lb.setTargetPosition(LB - 450);
            lb.setPower(1);
            sleep(800);


            telemetry.update();
        }
    }
}

