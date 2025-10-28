package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@TeleOp(name = "TURNTEST1", group = "Concept")
public class TURNTEST1 extends LinearOpMode {

    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;
    double fx = 578.272, fy = 578.272, cx = 402.145, cy = 221.506;
    double tagsize = 0.166;
    private int firstDetectedTagId = -1;
    private boolean hasLockedTagInInit = false;

    private final int READ_PERIOD = 1;
    private HuskyLens huskyLens;
    private Servo turn;
    private Servo lift;
    private Deadline rateLimit;
    private int firstGreenX = -1;
    private boolean hasLockedGreen = false;
    private boolean hasStartedHuskyLens = false;
    private boolean hasExecutedProgramA = false;
    private boolean hasExecutedCombinedAction = false;

    //数据表********************************************************************
    private final double[] Data_turn = {0.19, 0.563, 0.94};
    private int[] List_goal = {0, 0, 1};      //0是紫球 1是绿球  三个数分别是左中右
    private int[] List_current = {-1, -1, -1};  //0是紫球 1是绿球 -1是没球

    private int[] List_order = {0, 1, 2};    //数字是index，用于存储顺序


    @Override
    public void runOpMode() {
        turn = hardwareMap.get(Servo.class, "servo_turn");
        huskyLens = hardwareMap.get(HuskyLens.class, "huskylens1");
        lift = hardwareMap.get(Servo.class, "servo_lift");

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(fx, fy, cx, cy, tagsize);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
                telemetry.addData("AprilTag 状态", "相机已启动，初始化识别中...");
                telemetry.update();
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera Error", "代码: " + errorCode);
                telemetry.update();
            }
        });

        while (!isStarted() && !hasLockedTagInInit) {
            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();
            if (!currentDetections.isEmpty()) {
                for (AprilTagDetection tag : currentDetections) {
                    if (tag.hamming == 0) {
                        firstDetectedTagId = tag.id;
                        hasLockedTagInInit = true;
                        telemetry.addData("AprilTag ", firstDetectedTagId);
                        Goal_update(firstDetectedTagId);
                        telemetry.addData("Goal", show_list(List_goal));
                        break;
                    }
                }
            } else {
                telemetry.addData("AprilTag", "ERROR");
            }
            telemetry.update();
            sleep(20);
        }
        turn.setPosition(0.19);
        lift.setPosition(0);
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (!hasExecutedProgramA) {
                executeProgramA();
                hasExecutedProgramA = true;
                telemetry.update();
                sleep(500);
            }

            if (hasExecutedProgramA && !hasStartedHuskyLens) {


                rateLimit = new Deadline(READ_PERIOD, TimeUnit.MILLISECONDS);
                rateLimit.expire();

                if (!huskyLens.knock()) {
                    telemetry.addData("HuskyLens ", "error");
                } else {
                    huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);
                }
                hasStartedHuskyLens = true;
                telemetry.update();
                sleep(500);
            }

            if (hasStartedHuskyLens && !hasLockedGreen) {
                if (rateLimit.hasExpired()) {
                    rateLimit.reset();
                    HuskyLens.Block[] blocks = huskyLens.blocks();

                    for (HuskyLens.Block block : blocks) {
                        if (block.id == 1) {
                            firstGreenX = block.x;
                            hasLockedGreen = true;
                            telemetry.addData("首次绿色X坐标: ", firstGreenX);
                            telemetry.update();
                            break;
                        }
                    }

                }
            }

            if (hasLockedTagInInit && hasLockedGreen && !hasExecutedCombinedAction) {
                //开始计算以及移动
                //默认已经读取到了目标顺序
                Current_update(firstGreenX); //计算当前小球位置
                telemetry.addData("Current", show_list(List_current));
                telemetry.update();
                Calculate_order();//计算应该旋转的顺序
                controlTurn();
                hasExecutedCombinedAction = true;
            }


        }

        aprilTagDetectionPipeline.close();
    }


    //快捷方法
    private void executeProgramA() {
        telemetry.addData("state", "A");

        sleep(1000);
    }

    public void TURN(int id) {
        turn.setPosition(Data_turn[id]);
        sleep(1000);
    }

    public void LIFT() {
        lift.setPosition(0.3);
        sleep(1000);
        lift.setPosition(0);
        sleep(1000);
    }

    public void Goal_update(int id) {
        List_goal = new int[]{0, 0, 0};
        List_goal[id - 21] = 1;
    }

    public void Current_update(int firstGreenX) {
        List_current = new int[]{-1, -1, -1};
        if (firstGreenX > 25 && firstGreenX < 120) { //左
            List_current[0] = 1;
        } else if (firstGreenX > 150 && firstGreenX < 190) { //中
            List_current[1] = 1;
        } else if (firstGreenX > 200 && firstGreenX < 300) {
            List_current[2] = 1;
        }
    }

    public void Calculate_order() {
        try {
            //初始化池
            ArrayList<Integer> pool = new ArrayList<>();
            for (int i = 0; i < List_current.length; i++) {
                pool.add(i);
            }

            //计算顺序
            for (int i = 0; i < List_goal.length; i++) {
                int index = 0;
                while (List_goal[i] != List_current[pool.get(index)] && index<pool.size()-1) index++;
                List_order[i] = pool.get(index);
                pool.remove(index);
            }

            ///因为顺序与转盘顺序不一致
            /// 左-2 中-0 右-1
            /// 前面是列表顺序，后面是对应的转盘旋转ID

            //转换顺序到转盘顺序
            for (int i = 0; i < List_order.length; i++) {
                List_order[i] = (List_order[i] + 2) % 3;
            }


        } catch (ArithmeticException e) {  //小球数量有问题 比如吸进了俩绿色

        }
    }

    //输出数组
    public String show_list(int[] a){
        String ans="",temp="";
        for (int e:a){
            temp = ans + e +" ";
            ans = temp;
        }
        return ans;
    }


    private void controlTurn() {

        for (int j : List_order) {
            TURN(j);
            LIFT();
        }

    }
}