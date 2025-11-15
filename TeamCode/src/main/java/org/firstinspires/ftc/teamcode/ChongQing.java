package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "ChongQing",group="TELEOP")
public class ChongQing extends LinearOpMode {

    public Tool global_tool = new Tool();
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


    private double intake_val;
    private boolean state_fire =true;

    private Base base;

    //private Fire fire1 = new Fire();

    private int current_id =-1;


    @Override

    public void runOpMode() {

        Initiation();
        //LF = lf.getCurrentPosition();



        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                Update();
            }
        }
    }

    //所有初始化
    private void Initiation(){
        motor_upper = hardwareMap.get(DcMotor.class, "motor_upper");
        motor_lower = hardwareMap.get(DcMotor.class, "motor_lower");
        motor_intake = hardwareMap.get(DcMotor.class, "motor_intake");
        motor_lf = hardwareMap.get(DcMotor.class, "motor_lf");
        motor_lb = hardwareMap.get(DcMotor.class, "motor_lb");
        motor_rf = hardwareMap.get(DcMotor.class, "motor_rf");
        motor_rb = hardwareMap.get(DcMotor.class, "motor_rb");

//        motor_lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        motor_lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        motor_rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        motor_rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lift = hardwareMap.get(Servo.class, "servo_lift");
        turn = hardwareMap.get(Servo.class, "servo_turn");
        //lf.setTargetPosition(0);
        motor_upper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_lower.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor_rf.setDirection(DcMotorSimple.Direction.REVERSE);
        motor_rb.setDirection(DcMotorSimple.Direction.REVERSE);

        motor_lower.setDirection(DcMotor.Direction.REVERSE);
        base = new Base(motor_lf,motor_lb,motor_rf,motor_rb,lift,turn,0);

        motor_rf.setDirection(DcMotorSimple.Direction.REVERSE);
        motor_rb.setDirection(DcMotorSimple.Direction.REVERSE);
        motor_lf.setDirection(DcMotorSimple.Direction.FORWARD);
        motor_lb.setDirection(DcMotorSimple.Direction.FORWARD);



        power=1;

        global_tool.addTriggerToChange("Y",0);

        global_tool.addTriggerToChange("B",0);
    }

    private void Update(){
        //玩家1
        double y = gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double angle = gamepad1.right_stick_x*0.9;

        global_tool.update("Y",gamepad1.y,()->{
            intake_val = 1;
            motor_intake.setPower(1);
        },()->{
            intake_val = 0;

        });

        global_tool.update("B",gamepad1.b,()->{
            intake_val = intake_val*-1;
        });

        motor_intake.setPower(intake_val);
//                if(state_2 && gamepad1.y){
//                    state_2=false;
//                    state_2_1 = (state_2_1+1)%2;
//                    motor_intake.setPower(state_2_1);
//                }
//                if(!state_2 && !gamepad1.y){
//                    state_2=true;
//                }

        if(gamepad1.left_trigger>0.5){
            power = 0.3;
        }
        else {
            power=0.8;
        }

        base.Move(power,x,y,angle);

        if(gamepad1.a){
            motor_intake.setDirection(DcMotor.Direction.REVERSE);
        }
        else {
            motor_intake.setDirection(DcMotor.Direction.FORWARD);
        }

        if(gamepad1.dpad_down || gamepad1.dpad_right || gamepad1.dpad_left){ //确认方向键按下
            if(gamepad1.dpad_left){
                current_id=0;
            }
            else if(gamepad1.dpad_right){
                current_id=1;
            } else if (gamepad1.dpad_down) {
                current_id=2;
            }
            base.TURN(current_id,false);
        }



        //玩家2

        //A键发射
        if(gamepad2.a && state_fire){
            state_fire=false;
            new Thread(() -> {
                //防舵机抬升打到转盘
                if(current_id < 3){
                    current_id = 3;
                    base.TURN(current_id, true);
                }

                base.LIFT();

                state_fire = true;

            }).start();
        }

        if(gamepad2.right_bumper){
            motor_upper.setPower(1);
            motor_lower.setPower(1);
        }
        else {
            motor_upper.setPower(0);
            motor_lower.setPower(0);
        }


        //转盘初始化
        if(current_id==-1){
            current_id=1;
            base.TURN(current_id,false);
        }


        if(gamepad2.dpad_down || gamepad2.dpad_right || gamepad2.dpad_left){ //确认方向键按下
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





