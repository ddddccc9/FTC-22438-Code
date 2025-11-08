package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


@Autonomous(name = "AUTO_farBLUE",group="Autonomous")

public class AUTO_farBLUE extends LinearOpMode {

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

    private int[] List_goal = {0, 0, 1};      //0是紫球 1是绿球  三个数分别是左中右
    private int[] List_current = {-1, -1, -1};  //0是紫球 1是绿球 -1是没球

    private int[] List_order = {0, 1, 2};    //数字是index，用于存储顺序
    private Tool global_tool = new Tool();


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
        base.MoveToLinear(0.4,0,2150,0);
        int id = -1;
        while (id==-1){
            id = cam.AprilTag();
        }
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
            base.MoveToLinear(0.4,0,0,-330);
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
            base.MoveToLinear(0.3,0,0,-332);
        }).start();
        sleep(2000);
        motor_upper.setPower(0);
        motor_lower.setPower(0);

        //吸入
        motor_intake.setPower(1);
        base.TURN(0,false);
        new Thread(()->{
            base.MoveToLinear(0.2,0,700,0);
        }).start();

        sleep(3000);

        base.TURN(1,false);
        sleep(2000);
        new Thread(()->{
            base.MoveTo(0.25,0,120,0);
        }).start();
        sleep(2000);
        base.TURN(2,false);
        sleep(1050);
        new Thread(()->{
            base.MoveTo(0.25,0,350,0);
        }).start();
        sleep(1700);


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
            base.MoveToLinear(0.4,0,0,325);
        }).start();
        sleep(2050);

        while (!flag2.get()){
            telemetry.update();
        }

        motor_upper.setPower(0.98);
        motor_lower.setPower(0.98);
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

