package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "ChongQing",group="TELEOP")
public class ChongQing extends LinearOpMode {

    private DcMotor motor_upper;
    private DcMotor motor_lower;
    private DcMotor motor_intake;
    private DcMotor motor_lf;
    private DcMotor motor_lb;
    private DcMotor motor_rf;
    private DcMotor motor_rb;
    private Servo lift;
    private Servo turn;
    private double power;
    private double Angle;
    private boolean state_1 = true;
    private boolean state_2 = true;
    private int state_2_1 = 0;
    private boolean state_fire =true;

    private Base base;

    private Fire fire1 = new Fire();

    private int current_id =-1;


    @Override

    public void runOpMode() {



        motor_upper = hardwareMap.get(DcMotor.class, "motor_upper");
        motor_lower = hardwareMap.get(DcMotor.class, "motor_lower");
        motor_intake = hardwareMap.get(DcMotor.class, "motor_intake");
        motor_lf = hardwareMap.get(DcMotor.class, "motor_lf");
        motor_lb = hardwareMap.get(DcMotor.class, "motor_lb");
        motor_rf = hardwareMap.get(DcMotor.class, "motor_rf");
        motor_rb = hardwareMap.get(DcMotor.class, "motor_rb");
        lift = hardwareMap.get(Servo.class, "servo_lift");
        turn = hardwareMap.get(Servo.class, "servo_turn");
        //lf.setTargetPosition(0);
        motor_upper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_lower.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor_rf.setDirection(DcMotorSimple.Direction.REVERSE);
        motor_rb.setDirection(DcMotorSimple.Direction.REVERSE);

        motor_lower.setDirection(DcMotor.Direction.REVERSE);

        base = new Base(motor_lf,motor_lb,motor_rf,motor_rb,lift,turn);



        Angle=0;
        power=1;
        //LF = lf.getCurrentPosition();



        waitForStart();
        if (opModeIsActive()) {

            while (opModeIsActive()) {

                //玩家1
                double y = gamepad1.left_stick_y;
                double x = gamepad1.left_stick_x;
                double angle = gamepad1.right_stick_x*0.9;

                if(state_2 && gamepad1.y){
                    state_2=false;
                    state_2_1 = (state_2_1+1)%2;
                    motor_intake.setPower(state_2_1);
                }
                if(!state_2 && !gamepad1.y){
                    state_2=true;
                }

                if(gamepad1.left_trigger>0.5){
                    power = 0.8;
                }
                else {
                    power=0.3;
                }

                base.Move(power,x,y,angle);

                if(gamepad1.a){
                    motor_intake.setDirection(DcMotor.Direction.REVERSE);
                }
                else {
                    motor_intake.setDirection(DcMotor.Direction.FORWARD);
                }



                //玩家2

                if(gamepad2.a && state_fire){
                    state_fire=false;
                    fire1.start();
                }


                if(current_id==-1){
                    current_id=1;
                    base.TURN(current_id,false);
                }
                if(gamepad1.dpad_down || gamepad1.dpad_right || gamepad1.dpad_left){
                    if(gamepad1.dpad_right){
                        current_id=0;
                    }
                    else if(gamepad1.dpad_down){
                        current_id=1;
                    } else if (gamepad1.dpad_left) {
                        current_id=2;
                    }
                    base.TURN(current_id,false);
                }


                if(gamepad2.dpad_down || gamepad2.dpad_right || gamepad2.dpad_left){
                    if(gamepad2.dpad_right){
                        current_id=3;
                    }
                    else if(gamepad2.dpad_down){
                        current_id=4;
                    } else if (gamepad2.dpad_left) {
                        current_id=5;
                    }
                    base.TURN(current_id,false);
                }






                telemetry.addData("id", current_id);

                telemetry.addData("LF", base.GetPos(0));
                telemetry.addData("LB", base.GetPos(1));


                telemetry.update();
            }
        }
    }

    public class Fire extends Thread{
        public void run(){
            try{

                if(current_id<3){//防止舵机打到转盘
                    current_id=3;
                    base.TURN(current_id,true);
                }

                motor_upper.setPower(1);
                motor_lower.setPower(1);
                sleep(500);

                base.LIFT();

                motor_upper.setPower(0);
                motor_lower.setPower(0);

                state_fire=true;


            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }



}





