package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Motor_test",group="TELEOP")
public class motor_test extends LinearOpMode {

    private DcMotor lf;
    private DcMotor rf;
    private DcMotor lb;
    private DcMotor rb;
    private Base base;
    private Servo lift;
    private Servo turn;

    @Override

    public void runOpMode() {

        lf = hardwareMap.get(DcMotor.class, "motor_lf");
        lb = hardwareMap.get(DcMotor.class, "motor_lb");
        rf = hardwareMap.get(DcMotor.class, "motor_rf");
        rb = hardwareMap.get(DcMotor.class, "motor_rb");

        turn = hardwareMap.get(Servo.class, "servo_turn");
        lift = hardwareMap.get(Servo.class, "servo_lift");

        //lf.setTargetPosition(0);
        double LF;
        lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rb.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        lb.setDirection(DcMotor.Direction.REVERSE);
        lf.setDirection(DcMotor.Direction.REVERSE);
        rb.setDirection(DcMotor.Direction.FORWARD);
        rf.setDirection(DcMotor.Direction.FORWARD);

        base=new Base(lf,lb,rf,rb,lift,turn,1);




        //LF = lf.getCurrentPosition();】


        waitForStart();
        if (opModeIsActive()) {
            base.MoveToLinear(0.2,0,700,0);

            while (opModeIsActive()) {

                if (gamepad1.left_stick_y>0.9) {
                    lf.setPower(1);
                    lb.setPower(1);
                    rf.setPower(1);
                    rb.setPower(1);
                }

                else if (gamepad1.a) {
                    //lf.setTargetPosition(LF+1000);
                    lf.setPower(1);


                    //telemetry.addData("POS",LF);
                }
                else if(gamepad1.b){
                    lf.setPower(-1);

                }
                else if (gamepad1.x) {
                    lb.setPower(1);

                }
                else if (gamepad1.y) {
                    rb.setPower(-1);

                }
                else{
                    lf.setPower(0);
                    lb.setPower(0);
                    rf.setPower(0);
                    rb.setPower(0);
                }

                telemetry.addData("lf",lf.getCurrentPosition());
                telemetry.addData("rf",rf.getCurrentPosition());
                telemetry.addData("lb",lb.getCurrentPosition());
                telemetry.addData("rb",rb.getCurrentPosition());

                telemetry.update();
            }
        }
    }
}





