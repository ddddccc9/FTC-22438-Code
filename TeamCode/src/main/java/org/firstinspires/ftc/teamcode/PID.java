package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


/// 这个是PID函数，封装成为一个类，方便调用
public class PID extends LinearOpMode{
    private double last_error,sum_error;
    private double k_P,k_I,k_D;
    private double edge,B;


    ///这个好像可以删，不确定，没试过
    public void runOpMode() {
    }

    /// 初始化
    public PID(double p,double i,double d,double b){
        last_error=0;
        sum_error=0;
        edge = -1;
        k_P = p;
        k_I = i;
        k_D = d;
        B=b;
    }

    /// 设置输出大小限制
    public void limit(double a){
        edge = a;
    }

    /// PID的计算
    public double run(double current,double target){
        double error = target-current;
        if(Math.abs(error)<B) {
            error =0;
        }
        sum_error += error;

        double output = k_P * error + k_I * sum_error + k_D * (error - last_error);
        last_error=error;

        if(edge != -1){
            if(output > edge)
                output = edge;
            if(output < -edge)
                output = -edge;
        }


        return output;
    }




}
