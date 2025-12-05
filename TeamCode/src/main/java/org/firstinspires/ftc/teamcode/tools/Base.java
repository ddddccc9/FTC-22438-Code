/**
 * 小车底盘算法 封包
 */
package org.firstinspires.ftc.teamcode.tools;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.tools.Tool;

import java.util.concurrent.atomic.AtomicBoolean;


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
    private final Tool global_tool;

    private final double[] Data_turn = {0.19    , 0.563 ,0.94   ,0  ,0.37   ,0.75};
    private final int[] Time_wait    = {1000    ,1000   ,1000   ,1000,1000  ,1000};

    public void runOpMode() {}


    /**
     * 基本功能的类，方便手动/自动程序编写
     *
     * @param id 0代表手动 1代表自动程序
     */
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

        TURN(3,false);
        current_id=3;
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
        boolean End=false;
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


        double power = 0.01;

        AtomicBoolean Flag = new AtomicBoolean(true);


        new Thread(()->{
            sleep(4000);
            Flag.set(false);
        }).start();



        while ((motor_lf.isBusy()|| motor_lb.isBusy()) && Flag.get()){
            double percent=0.01;
            if(motor_lf.getTargetPosition() != 0){
                percent= (double) (motor_lf.getCurrentPosition() - LF) /(y + (x + angle));
            }
            else{
                percent= (double) (motor_rf.getCurrentPosition() - RF) /(y - (x + angle));
            }

            double initVal = 0.075;
            if(x==0 && y==0) initVal = 0.1;
            double[] line1 = global_tool.Calculate_Line(0,initVal,0.3,TargetPower);
            double[] line2 = global_tool.Calculate_Line(0.8,TargetPower,1,0.2);

            power=TargetPower;
            if(percent<0.2) power = percent*line1[0]+line1[1];
            else if(percent>0.8) power = percent*line2[0]+line2[1];

            motor_lf.setPower(power);
            motor_lb.setPower(power);
            motor_rf.setPower(power);
            motor_rb.setPower(power);

        }


    }

    private double cal_power(double[] line1,double[] line2,double percent,double TargetPower){
        double power=TargetPower;
        if(percent<0.2) power = percent*line1[0]+line1[1];
        else if(percent>0.8) power = percent*line2[0]+line2[1];
        return power;
    }


    public void MoveToSlowStart(double TargetPower,int x,int y,int angle){
        LF= motor_lf.getCurrentPosition();
        LB=motor_lb.getCurrentPosition();
        RF=motor_rf.getCurrentPosition();
        RB=motor_rb.getCurrentPosition();

        motor_lf.setPower(0);
        motor_lb.setPower(0);
        motor_rf.setPower(0);
        motor_rb.setPower(0);

        motor_lf.setTargetPosition(LF+(y + (x + angle)));
        motor_lb.setTargetPosition(LB+(y - (x - angle)));
        motor_rf.setTargetPosition(RF+(y - (x + angle)));
        motor_rb.setTargetPosition(RB+(y + (x - angle)));


        for (double i=0;i<TargetPower;i+=0.02) {
            motor_lf.setPower(i);
            motor_lb.setPower(i);
            motor_rf.setPower(i);
            motor_rb.setPower(i);
            sleep(50);
        }
        int s1 = (int)((TargetPower/0.01)*100*TargetPower/2);
        int s2 = (int)(((TargetPower-0.16)/0.02)*100*TargetPower/2);

        motor_lf.setPower(TargetPower);
        motor_lb.setPower(TargetPower);
        motor_rf.setPower(TargetPower);
        motor_rb.setPower(TargetPower);

        sleep((int)(Math.abs(s1-s2)/TargetPower/10));

        for (double i=TargetPower;i>0.16;i-=0.03) {
            motor_lf.setPower(i);
            motor_lb.setPower(i);
            motor_rf.setPower(i);
            motor_rb.setPower(i);
            sleep(50);
        }


        while ((motor_lf.isBusy()|| motor_lb.isBusy())){
            s1++;
        }

    }



}
