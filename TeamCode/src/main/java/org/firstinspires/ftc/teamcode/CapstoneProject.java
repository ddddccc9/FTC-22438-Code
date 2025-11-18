/**
 * 该文件制作于学术月
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

//-21000=90° 顺时针转90°


///我改动了什么
/// 1.角度的计算从y2-y1变成y1-y2，看这样是否正确
/// 2.我把PID的输入值从int变成double
/// 3.内环变成速度环，看是否计算正确

@TeleOp(name = "Capstone_Circle")

public class CapstoneProject extends LinearOpMode{
    private DcMotor lf;
    private DcMotor lb;
    private DcMotor rf;
    private DcMotor rb;

    private long Y1,Y2,X;
    private int y1,y2,x;
    private PID pid1,pid2,pid3,pid1_0,pid2_0,pid3_0;
    private int target_x,target_y,target_angle;

    


    public void runOpMode() {
        //初始化4个电机
        lf = hardwareMap.get(DcMotor.class, "1");
        lb = hardwareMap.get(DcMotor.class, "0");
        rf = hardwareMap.get(DcMotor.class, "3");
        rb = hardwareMap.get(DcMotor.class, "2");

        rb.setDirection(DcMotor.Direction.REVERSE);
        rf.setDirection(DcMotor.Direction.REVERSE);
        lb.setDirection(DcMotor.Direction.FORWARD);
        lf.setDirection(DcMotor.Direction.FORWARD);

        //通过电机获得外置编码器的值
        Y1=rf.getCurrentPosition();
        Y2=lf.getCurrentPosition();
        X=rb.getCurrentPosition();




        //创建pid，串环，由于最后时间没有时间了，所以外环参数是复制的内环，并且外环和内环的意义也不明确。
        pid1_0 = new PID(0.01,0,0.015,0); //y外环
        pid1 = new PID(0.005,0,0.015,0); //y内环

        pid2_0 = new PID(0.15,0,0.05,0);  //angle外环
        pid2 = new PID(0.005,0,0.00277,0);  //angle内环

        pid3_0 = new PID(0.01,0,0.015,0);  //x外环
        pid3 = new PID(0.005,0,0.015,0);  //x内环

        //pid外环限制输出，限制的最大速度
        pid1_0.limit(0.5);
        pid2_0.limit(0.9);
        pid3_0.limit(0.5);

        //pid内环限制输出
//        pid1.limit(0.5);
//        pid2.limit(0.5);
//        pid3.limit(0.5);

        waitForStart();

        /// 这里的上面是初始化，下面是正式代码
        if (opModeIsActive()) {
            if (opModeIsActive()) {  //这行代码可以携程while，但由于我只想计算一次于是就写成了if

                //计算相对变化
                y1=(int)(rf.getCurrentPosition()-Y1);
                y2=(int)(lf.getCurrentPosition()-Y2);
                x=-(int)(rb.getCurrentPosition()-X);


                //围绕场地走一圈
                moveTo(0,80000,0,false);
                moveTo(40000*2,0,0,false);
                moveTo(0,-80000,0,false);
                moveTo(-40000,40000,0,true);

//                moveTo(0,40000*4,0,false);
//                moveTo(40000*4,0,0,false);
//                moveTo(0,-40000*5-20000,0,false);
//                moveTo(40000*4,0,0,false);

                //moveTo(10000,40000*2,1000,false);

                telemetry.update();


            }
        }

    }

    /// 封装的控制函数，方便进行底盘的控制
    public double move(double x,double y,double angle,double power){


        angle = -angle;

        double sum = Math.abs(x)+Math.abs(y)+Math.abs(angle);
        if(sum>1){
            double k = 1/sum;
            x *= k;
            y *= k;
            angle *= k;
        }

        if(sum*power < 0.1){
            lf.setPower(0);
            lb.setPower(0);
            rf.setPower(0);
            rb.setPower(0);
            return -1;
        }

        lf.setPower(power * (y + (x + angle)));
        lb.setPower(power * (y - (x - angle)));
        rf.setPower(power * (y - (x + angle)));
        rb.setPower(power * (y + (x - angle)));

        return sum*power;
    }


    public double Cal_angle(double num){
        return num;//3000;
    }


    /// 这里是我封装的移动函数，调用PID持续运算，直到发现当前位置满足要求。
    /// 参数分别是 目标X，目标Y，目标角度，是否开启持续移动
    ///
    /// 开启持续移动，意味着移动到指定地点后，仍然保持循环，防止外力干扰，进行位置纠正
    ///
    public void moveTo(int target_x,int target_y,int target_angle,boolean Flag){

        //获取起始位置
        Y1=rf.getCurrentPosition();
        Y2=lf.getCurrentPosition();
        X=rb.getCurrentPosition();

        y1=(int)(rf.getCurrentPosition()-Y1);
        y2=(int)(lf.getCurrentPosition()-Y2);
        x=-(int)(rb.getCurrentPosition()-X);

        int t_x0,t_y0,t_x1,t_y1;
        t_x0 = x;
        t_y0 = y2;

        while(true){
            //计算全局相对位移
            y1=(int)(rf.getCurrentPosition()-Y1);
            y2=(int)(lf.getCurrentPosition()-Y2);
            x=-(int)(rb.getCurrentPosition()-X);
            t_x1 = x;
            t_y1 = y2;

            //PID外环计算
            double upper1 = pid1_0.run(y1,target_y);                 //外环，位置环
            double upper2 = pid2_0.run((y2-y1)/1.1,target_angle);  //内环，速度环
            double upper3 = pid3_0.run(x,target_x);

            double speed_y = (t_y1-t_y0)/10.0;
            double speed_x = (t_x1-t_x0)/10.0;

            //PID内环计算
            double move_y = pid1.run(0,upper1);
            double move_angle = pid2.run(0,upper2);
            double move_x = pid3.run(0,upper3);

            double sum = Math.abs(move_x)+Math.abs(move_y)+Math.abs(move_angle);

            double output = move(move_x,move_y,move_angle,0.5);

            t_x0 = t_x1;
            t_y0 = t_y1;
            telemetry.addData("y1",y1);
            telemetry.addData("y2",y2);
            telemetry.addData("x",x);
            telemetry.addData("y",y1);
            telemetry.addData("angle",Cal_angle(y2-y1));
            telemetry.addData("move",output);
            telemetry.addData("upper1",upper1);
            telemetry.addData("move_y",move_y);



            telemetry.update();//刷新调试信息

            double E = 0.1;//输出下限 电机精度

            if(!Flag){
                if(Math.abs(move_y)<E && Math.abs(move_x)<E && Math.abs(move_angle)<E){
                    break;
                }
                if(output < 0.1){
                    break;
                }
            }



        }
    }



}
