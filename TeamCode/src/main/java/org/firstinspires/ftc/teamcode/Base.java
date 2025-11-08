package org.firstinspires.ftc.teamcode;


import static java.lang.Thread.sleep;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import java.util.concurrent.TimeUnit;
public class Base extends LinearOpMode{
    private DcMotor motor_lf;
    private DcMotor motor_lb;
    private DcMotor motor_rf;
    private DcMotor motor_rb;
    private Servo lift;
    private Servo turn;
    private int current_id;

    private int LF;
    private int LB;
    private int RF;
    private int RB;
    private Tool global_tool;

    private final double[] Data_turn = {0.19    , 0.563 ,0.94   ,0  ,0.37   ,0.75};
    private final int[] Time_wait    = {1000    ,1000   ,1000   ,1000,1000  ,1000};

    public void runOpMode() {}


    public Base(DcMotor lf1,DcMotor lb1,DcMotor rf1,DcMotor rb1,Servo lift1,Servo turn1,int id){
        motor_lf=lf1;
        motor_lb=lb1;
        motor_rf=rf1;
        motor_rb=rb1;

        global_tool = new Tool();



        if(id==0){
            motor_lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_lb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_rb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        else{
            LF= motor_lf.getCurrentPosition();
            LB=motor_lb.getCurrentPosition();
            RF=motor_rf.getCurrentPosition();
            RB=motor_rb.getCurrentPosition();
            motor_lf.setTargetPosition(LF);
            motor_lb.setTargetPosition(LB);
            motor_rf.setTargetPosition(RF);
            motor_rb.setTargetPosition(RB);
            motor_lf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor_lb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor_rf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor_rb.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor_lf.setDirection(DcMotorSimple.Direction.REVERSE);
            motor_lb.setDirection(DcMotorSimple.Direction.REVERSE);
            motor_rf.setDirection(DcMotorSimple.Direction.FORWARD);
            motor_rb.setDirection(DcMotorSimple.Direction.FORWARD);
        }


        lift=lift1;
        lift.setPosition(0);
        turn=turn1;

        TURN(0,false);
        current_id=0;
    }


    public void TURN(int id,boolean T) {
        turn.setPosition(Data_turn[id]);
        if(T) {
            sleep(Time_wait[id]);
        }
    }


    public void TURN(double angle,boolean T) {
        turn.setPosition(angle);
        if(T) {
            sleep(1000);
        }
    }

    public void LIFT() {
        lift.setPosition(0.3);
        sleep(300);
        lift.setPosition(0);
        sleep(300);
    }

    public void Move(double power,double x,double y,double angle){
        angle = -angle;
        x = -x;
        motor_lf.setPower(power * (y + (x + angle)));
        motor_lb.setPower(power * (y - (x - angle)));
        motor_rf.setPower(power * (y - (x + angle)));
        motor_rb.setPower(power * (y + (x - angle)));
    }

    public void MoveTo(double power,int x,int y,int angle){
        LF= motor_lf.getCurrentPosition();
        LB=motor_lb.getCurrentPosition();
        RF=motor_rf.getCurrentPosition();
        RB=motor_rb.getCurrentPosition();

        motor_lf.setPower(power);
        motor_lb.setPower(power);
        motor_rf.setPower(power);
        motor_rb.setPower(power);

        motor_lf.setTargetPosition(LF+(y + (x + angle)));
        motor_lb.setTargetPosition(LB+(y - (x - angle)));
        motor_rf.setTargetPosition(RF+(y - (x + angle)));
        motor_rb.setTargetPosition(RB+(y + (x - angle)));

        while (motor_lf.isBusy()|| motor_lb.isBusy()){
            sleep(10);
        }


    }

    public int GetPos(int id){
        if(id==0){
            return motor_lf.getCurrentPosition();
        }
        if(id==1){
            return motor_lb.getCurrentPosition();
        }
        if(id==2){
            return motor_rf.getCurrentPosition();
        }
        if(id==3){
            return motor_rb.getCurrentPosition();
        }
        return -1;
    }

    public void MoveToLinear(double TargetPower,int x,int y,int angle){
        LF= motor_lf.getCurrentPosition();
        LB=motor_lb.getCurrentPosition();
        RF=motor_rf.getCurrentPosition();
        RB=motor_rb.getCurrentPosition();

        motor_lf.setPower(0.1);
        motor_lb.setPower(0.1);
        motor_rf.setPower(0.1);
        motor_rb.setPower(0.1);


        motor_lf.setTargetPosition(LF+(y + (x + angle)));
        motor_lb.setTargetPosition(LB+(y - (x - angle)));
        motor_rf.setTargetPosition(RF+(y - (x + angle)));
        motor_rb.setTargetPosition(RB+(y + (x - angle)));


        double power = 0.1;

        while (motor_lf.isBusy()|| motor_lb.isBusy()){
            double percent= (double) (motor_lf.getCurrentPosition() - LF) /(y + (x + angle));

            double[] line1 = global_tool.Calculate_Line(0,0.1,0.4,TargetPower);
            double[] line2 = global_tool.Calculate_Line(0.6,TargetPower,1,0.2);

            power=TargetPower;
            if(percent<0.2) power = percent*line1[0]+line1[1];
            else if(percent>0.8) power = percent*line2[0]+line2[1];

            motor_lf.setPower(power);
            motor_lb.setPower(power);
            motor_rf.setPower(power);
            motor_rb.setPower(power);

        }

    }



}
