package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TeleOp(name = "Ball Tracking OpMode", group = "Vision")
@Disabled
public class BallTrackingOpMode extends LinearOpMode {

    private OpenCvCamera camera;
    private BallDetectionPipeline pipeline;

    @Override
    public void runOpMode() {
        int cameraMonitorViewId = hardwareMap.appContext
                .getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1"); // C270i 默认名
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);

        pipeline = new BallDetectionPipeline();
        camera.setPipeline(pipeline);

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

        telemetry.addLine("初始化完成，按下开始键启动识别...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // 获取线程安全的检测结果
            List<BallDetectionPipeline.DetectedBall> balls = pipeline.getDetectionsSnapshot();

            telemetry.addData("检测到的球数", balls.size());
            for (BallDetectionPipeline.DetectedBall ball : balls) {
                if (ball.area > 5000) {
                    telemetry.addData("颜色", ball.color);

                    telemetry.addData("area", ball.area);
                    telemetry.addData("位置", "(%.0f, %.0f)", ball.center.x, ball.center.y);
                }
            }
            telemetry.update();

            sleep(50);
        }

        camera.stopStreaming();
    }

    // 内部静态类：检测逻辑
    public static class BallDetectionPipeline extends OpenCvPipeline {
        private final Object lock = new Object();
        private final List<DetectedBall> detectedBalls = new ArrayList<>();

        public static class DetectedBall {
            public String color;
            public Point center;
            public double area;
            public DetectedBall(String color, Point center, double area) {
                this.color = color;
                this.center = center;
                this.area = area;
            }
        }

        // HSV 阈值（可根据环境调整）
        private final Scalar lowerPurple = new Scalar(125, 60, 80);
        private final Scalar upperPurple = new Scalar(155, 255, 255);
        private final Scalar lowerGreen = new Scalar(40, 70, 70);
        private final Scalar upperGreen = new Scalar(80, 255, 255);

        private final Mat hsv = new Mat();
        private final Mat mask = new Mat();
        private final Mat hierarchy = new Mat();

        @Override
        public Mat processFrame(Mat input) {
            Imgproc.cvtColor(input, hsv, Imgproc.COLOR_RGB2HSV);

            List<DetectedBall> newDetections = new ArrayList<>();

            // 检测紫色
            Core.inRange(hsv, lowerPurple, upperPurple, mask);
            findBalls(mask, input, "Purple", newDetections);

            // 检测绿色
            Core.inRange(hsv, lowerGreen, upperGreen, mask);
            findBalls(mask, input, "Green", newDetections);

            // 更新线程安全列表
            synchronized (lock) {
                detectedBalls.clear();
                detectedBalls.addAll(newDetections);
            }

            return input;
        }

        private void findBalls(Mat mask, Mat frame, String color, List<DetectedBall> detections) {
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            for (MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                if (area > 400) { // 过滤小噪点
                    Rect rect = Imgproc.boundingRect(contour);
                    Point center = new Point(rect.x + rect.width / 2.0, rect.y + rect.height / 2.0);

                    // 绘制可视化
                    Scalar drawColor = color.equals("Purple") ? new Scalar(200, 50, 200) : new Scalar(0, 255, 0);
                    Imgproc.rectangle(frame, rect, drawColor, 2);
                    Imgproc.circle(frame, center, 5, drawColor, -1);
                    Imgproc.putText(frame, color, new Point(rect.x, rect.y - 10),
                            Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, drawColor, 2);

                    detections.add(new DetectedBall(color, center, area));
                }
            }
        }

        public List<DetectedBall> getDetectionsSnapshot() {
            synchronized (lock) {
                return new ArrayList<>(detectedBalls);
            }
        }
    }
}
