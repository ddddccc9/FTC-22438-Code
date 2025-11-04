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
    private Servo lift;
    private Servo turn;

    private Base base;


    @Override
    public void runOpMode() {
        huskyLens1 = hardwareMap.get(HuskyLens.class, "huskylens1");


        Deadline rateLimit = new Deadline(READ_PERIOD, TimeUnit.NANOSECONDS);

        // fS = hardwareMap.get(Servo.class, "fS");

        lf = hardwareMap.get(DcMotor.class, "motor_lf");
        lb = hardwareMap.get(DcMotor.class, "motor_lb");
        rf = hardwareMap.get(DcMotor.class, "motor_rf");
        rb = hardwareMap.get(DcMotor.class, "motor_rb");

        turn = hardwareMap.get(Servo.class, "servo_turn");
        lift = hardwareMap.get(Servo.class, "servo_lift");

        base=new Base(lf,lb,rf,rb,lift,turn);
        int LF;
        int LB;
        int RB;
        int RF;
        int R1;
        int R2;





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

            base.MoveTo(0.5,0,2100,0);
            sleep(2500);
            base.MoveTo(0.2,0,0,300);
            sleep(1000);
            base.MoveTo(0.5,0,0,360);
            sleep(800);
            loop1:while(0==0) {
                if (!rateLimit.hasExpired()) {
                    continue;
                }
                rateLimit.reset();


                HuskyLens.Block[] blocks = huskyLens1.blocks();

                telemetry.addData("count", blocks.length);

                for (int i = 0; i < blocks.length; i++) {
                    telemetry.addData("Block", blocks[i].x);
                    if (blocks[i].x <= 140 && blocks[i].x >= 130) {
                        break loop1;
                    } else if (blocks[i].x < 130) {
                        base.MoveTo(0.2,-20,0,0);

                    } else if (blocks[i].x > 140) {
                        base.MoveTo(0.2,20,0,0);

                    }
                }
            }
            base.MoveTo(0.5,850,0,0);
            sleep(800);



            telemetry.update();
        }
    }
}

