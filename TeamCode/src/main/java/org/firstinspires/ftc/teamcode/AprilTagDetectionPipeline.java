/**
 * 二维码识别功能 封装
 *
 * 打包二维码识别功能的代码
 */
package org.firstinspires.ftc.teamcode;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.apriltag.AprilTagDetectorJNI;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;

public class AprilTagDetectionPipeline extends OpenCvPipeline {
    private long nativeApriltagPtr;
    private Mat grey = new Mat();
    private ArrayList<AprilTagDetection> detections = new ArrayList<>();

    private ArrayList<AprilTagDetection> detectionsUpdate = new ArrayList<>();
    private final Object detectionsUpdateSync = new Object();

    private double fx;
    private double fy;
    private double cx;
    private double cy;

    private static final double FEET_PER_METER = 3.28084;

    private double tagsize = 0.166; // 标准AprilTag尺寸

    public AprilTagDetectionPipeline(double fx, double fy, double cx, double cy, double tagsize) {
        this.fx = fx;
        this.fy = fy;
        this.cx = cx;
        this.cy = cy;
        this.tagsize = tagsize;

        nativeApriltagPtr = AprilTagDetectorJNI.createApriltagDetector("tag36h11", 3, 3);
    }

    @Override
    public void init(Mat firstFrame) {
        Imgproc.cvtColor(firstFrame, grey, Imgproc.COLOR_RGBA2GRAY);
    }

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, grey, Imgproc.COLOR_RGBA2GRAY);

        detections = AprilTagDetectorJNI.runAprilTagDetectorSimple(
                nativeApriltagPtr, grey, tagsize, fx, fy, cx, cy);

        synchronized (detectionsUpdateSync) {
            detectionsUpdate = detections;
        }

        for (AprilTagDetection detection : detections) {
            if (detection.hamming == 0) {
                for (int i = 0; i < 4; i++) {
                    Point pt1 = new Point(detection.corners[i].x, detection.corners[i].y);
                    Point pt2 = new Point(detection.corners[(i + 1) % 4].x, detection.corners[(i + 1) % 4].y);
                    Imgproc.line(input, pt1, pt2, new Scalar(0, 255, 0), 3);
                }


                Point center = new Point(detection.center.x, detection.center.y);
                Imgproc.circle(input, center, 5, new Scalar(0, 0, 255), -1);

                String text = "ID: " + detection.id;
                Imgproc.putText(input, text, new Point(center.x + 10, center.y),
                        Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, new Scalar(0, 255, 0), 2);
            }
        }

        return input;
    }

    public ArrayList<AprilTagDetection> getLatestDetections() {
        synchronized (detectionsUpdateSync) {
            return detectionsUpdate;
        }
    }

    public void close() {
        if (nativeApriltagPtr != 0) {
            AprilTagDetectorJNI.releaseApriltagDetector(nativeApriltagPtr);
            nativeApriltagPtr = 0;
        }
    }
}