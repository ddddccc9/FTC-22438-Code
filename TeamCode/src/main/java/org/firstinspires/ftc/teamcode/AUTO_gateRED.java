package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;



@Autonomous(name = "AUTO_gateRED",group="Autonomous")

public class AUTO_gateRED extends LinearOpMode {

    private final int READ_PERIOD = 1;

    private HuskyLens huskyLens1;
    private DcMotor lf;
    private DcMotor lb;
    private DcMotor rf;
    private DcMotor rb;
    private DcMotor motor_upper;
    private DcMotor motor_lower;
    private DcMotor motor_intake;
    private Servo lift;
    private Servo turn;

    private Base base;
    private CameraTeam cam;


    @Override
    public void runOpMode() {
        Init();
        waitForStart();

        if (opModeIsActive()) {
            Update();

//
//            //面向球 吸球
//            base.MoveToLinear(0.2,0,0,360);
//            motor_intake.setPower(1);
//
//            base.MoveToLinear(0.3,0,800,0);
//            for(int i=0;i<3;i++){
//                base.TURN(i, true);
//            }
//
//
//
//            motor_intake.setPower(0);
//
//            sleep(800);



            telemetry.update();
        }
    }
    private void Init(){
        huskyLens1 = hardwareMap.get(HuskyLens.class, "huskylens1");


        Deadline rateLimit = new Deadline(READ_PERIOD, TimeUnit.NANOSECONDS);

        // fS = hardwareMap.get(Servo.class, "fS");

        lf = hardwareMap.get(DcMotor.class, "motor_lf");
        lb = hardwareMap.get(DcMotor.class, "motor_lb");
        rf = hardwareMap.get(DcMotor.class, "motor_rf");
        rb = hardwareMap.get(DcMotor.class, "motor_rb");

        turn = hardwareMap.get(Servo.class, "servo_turn");
        lift = hardwareMap.get(Servo.class, "servo_lift");

        motor_upper = hardwareMap.get(DcMotor.class, "motor_upper");
        motor_lower = hardwareMap.get(DcMotor.class, "motor_lower");
        motor_upper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_lower.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_lower.setDirection(DcMotor.Direction.REVERSE);

        motor_intake = hardwareMap.get(DcMotor.class, "motor_intake");

        base=new Base(lf,lb,rf,rb,lift,turn,1);
        rateLimit.expire();

        if (!huskyLens1.knock()) {
            telemetry.addData(">>", "Problem communicating with " + huskyLens1.getDeviceName());
        } else {
            telemetry.addData(">>", "Press start to continue");
        }

        int CamId  =hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        huskyLens1.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);
        cam = new CameraTeam(CamId,webcamName,huskyLens1);

        telemetry.update();

    }
    private void Update(){
        base.TURN(3,false);

        //查看二维码
        base.MoveToLinear(0.5,-936,0,0);

        while (true){
            telemetry.addData("Tag",cam.AprilTag());
            telemetry.update();

        }


//        //发射
//        base.MoveToLinear(0.2,-594,0,0);
//        base.MoveToLinear(0.2,0,0,660);
//
//
//        motor_upper.setPower(1);
//        motor_lower.setPower(1);
//        sleep(500);
//        for(int i=0;i<3;i++){
//            base.TURN(3+i, true);
//            base.LIFT();
//
//        }
//        motor_upper.setPower(0);
//        motor_lower.setPower(0);
    }
}

