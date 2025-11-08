package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.ArrayList;

public class CameraTeam {
    OpenCvCamera camera;
    int cameraMonitorViewId;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;
    WebcamName webcamName;
    private Deadline rateLimit;
    private boolean hasLockedTagInInit = false;
    double fx = 578.272, fy = 578.272, cx = 402.145, cy = 221.506;
    double tagsize = 0.166;
    private int firstDetectedTagId = -1;
    private HuskyLens huskyLens;

    //所有初始化操作
    public CameraTeam(int cameraViewId, WebcamName webName, HuskyLens husk){

        int cameraMonitorViewId = cameraViewId;//hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        huskyLens = husk;
        WebcamName webcamName = webName;//hardwareMap.get(WebcamName.class, "Webcam 1");
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
            }
        });

        huskyLens.initialize();
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);



    }
    public int AprilTag(){

        ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();
        if (!currentDetections.isEmpty()) {
            for (AprilTagDetection tag : currentDetections) {
                if (tag.hamming == 0) {
                    firstDetectedTagId = tag.id;
                    return firstDetectedTagId;
                }
            }
        }
        return -1;

    }

    public HuskyLens.Block[] getBlocks(){
        return huskyLens.blocks();
    }

}
