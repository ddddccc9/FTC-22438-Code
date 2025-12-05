package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import org.firstinspires.ftc.teamcode.tools.Base;
import org.firstinspires.ftc.teamcode.tools.CameraTeam;
import org.firstinspires.ftc.teamcode.tools.Tool;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


@Autonomous(name = "AUTO_gateRED2",group="Autonomous")

public class AUTO_gateRED2 extends LinearOpMode {

    private final int READ_PERIOD = 1;

    private HuskyLens huskyLens1;
    private DcMotor lf;
    private DcMotor lb;
    private DcMotor rf;
    private DcMotor rb;
    private DcMotorEx motor_upper;
    private DcMotorEx motor_lower;
    private DcMotor motor_intake;
    private Servo lift;
    private Servo turn;

    private Base base;
    private CameraTeam cam;
    private PIDFCoefficients pid_fire;

    private int[] List_goal = {0, 0, 1};      //0是紫球 1是绿球  三个数分别是左中右
    private int[] List_current = {-1, -1, -1};  //0是紫球 1是绿球 -1是没球

    private int[] List_order = {0, 1, 2};    //数字是index，用于存储顺序
    private Tool global_tool = new Tool();
    private int id = -1;


    @Override
    public void runOpMode() {
        Init();
        waitForStart();

        if (opModeIsActive()) {
            Update();
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

        motor_upper = hardwareMap.get(DcMotorEx.class, "motor_upper");
        motor_lower = hardwareMap.get(DcMotorEx.class, "motor_lower");
        motor_upper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor_lower.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor_lower.setDirection(DcMotor.Direction.REVERSE);

//        //发射器控制PID代码
//        pid_fire = new PIDFCoefficients(10,3,0,0); //PID得调参
//        motor_upper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        motor_lower.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        motor_upper.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pid_fire);
//        motor_lower.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pid_fire);
//
//        motor_upper.setVelocity();
//        motor_lower.setVelocity();

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
        base.MoveToLinear(0.55,-1150,-1150,0);
        id = -1;
        new Thread(()->{
            while (id==-1){
                id = cam.AprilTag();
            }

        }).start();

        sleep(2000);
        if(id==-1) id=21;
        List_goal = global_tool.Goal_update(id);
        for (HuskyLens.Block block : cam.getBlocks()) {
            if (block.id == 1) {
                List_current = global_tool.Current_update(block.x);
                break;
            }
        }
        List_order = global_tool.Calculate_order(List_goal,List_current);

        //发射
        motor_upper.setPower(0.96);
        motor_lower.setPower(0.96);
        AtomicBoolean flag = new AtomicBoolean(false);

        new Thread(()->{
            base.TURN(List_order[0]+3,false);
            flag.set(true);
        }).start();

        new Thread(()->{
            base.MoveToLinear(0.4,0,0,330);
        }).start();
        sleep(2000);

        while(!flag.get()){
            telemetry.update();
        }
        base.LIFT();

        for(int i=1;i<3;i++){
            base.TURN(List_order[i]+3,true);
            base.LIFT();
        }

        new Thread(()->{
            base.MoveToLinear(0.25,0,0,330);
        }).start();
        sleep(3500);
        motor_upper.setPower(0);
        motor_lower.setPower(0);

        //吸入
        motor_intake.setPower(1);
        base.TURN(0,false);
        base.MoveToSlowStar(0.1,0,700,0,2500);


        base.TURN(1,false);
        sleep(1500);
        base.MoveToSlowStar(0.1,0,120,0,2000);

        base.TURN(2,false);
        sleep(1050);

        base.MoveToSlowStar(0.1,0,350,0,1700);


        //发射2
        motor_intake.setPower(0);

        //顺序计算2
        AtomicBoolean flag2 = new AtomicBoolean(false);
        new Thread(()->{
            for (HuskyLens.Block block : cam.getBlocks()) {
                if (block.id == 1) {
                    List_current = global_tool.Current_update(block.x);
                    break;
                }
            }
            List_order = global_tool.Calculate_order(List_goal,List_current);
            base.TURN(List_order[0]+3,false);
            flag2.set(true);
        }).start();


        new Thread(()->{
            base.MoveToLinear(0.45,0,-1120,0);
        }).start();
        sleep(2000);

        new Thread(()->{
            base.MoveToLinear(0.4,0,0,-325);
        }).start();
        sleep(2050);

        while (!flag2.get()){
            telemetry.update();
        }

        motor_upper.setPower(0.96);
        motor_lower.setPower(0.96);
        sleep(1000);
        base.LIFT();

        for(int i=1;i<3;i++){
            base.TURN(List_order[i]+3,true);
            base.LIFT();
        }
        motor_upper.setPower(0);
        motor_lower.setPower(0);








    }
}

