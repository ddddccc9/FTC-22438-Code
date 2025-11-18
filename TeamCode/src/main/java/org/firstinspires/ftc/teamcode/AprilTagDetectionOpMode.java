/**
 *二维码识别
 *
 * 二维码识别程序，摄像头识别二维码的ID并输出
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

@TeleOp(name = "AprilTag Detection", group = "Concept")
public class AprilTagDetectionOpMode extends LinearOpMode {
    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    double tagsize = 0.166;

    @Override
    public void runOpMode() {
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
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera Error", errorCode);
                telemetry.update();
            }
        });

        telemetry.addLine("等待开始...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

            if (currentDetections.size() != 0) {
                boolean tagFound = false;

                for (AprilTagDetection tag : currentDetections) {
                    if (tag.hamming == 0) {
                        telemetry.addLine(String.format("检测到标签 ID: %d", tag.id));
                        tagFound = true;

                        switch (tag.id) {
                            case 21:
                                executeAction1();
                                break;
                            case 22:
                                executeAction2();
                                break;
                            case 23:
                                executeAction3();
                                break;
                            default:
                                telemetry.addLine("未知标签ID: " + tag.id);
                                break;
                        }
                    }
                }

                if (!tagFound) {
                    telemetry.addLine("未检测到有效标签");
                }

            } else {
                telemetry.addLine("未检测到任何标签");
            }

            telemetry.update();
            sleep(20);
        }

        aprilTagDetectionPipeline.close();
    }

    private void executeAction1() {

        telemetry.addLine("执行动作1 - 标签21");

    }

    private void executeAction2() {

        telemetry.addLine("执行动作2 - 标签22");
    }

    private void executeAction3() {

        telemetry.addLine("执行动作3 - 标签23");
    }
}