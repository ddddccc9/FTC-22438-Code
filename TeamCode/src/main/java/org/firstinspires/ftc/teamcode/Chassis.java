/**
 该文件制作于学术月，是利用PID算法的底盘，结合外置编码器实现底盘移动
 */

package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.DcMotor;


public class Chassis {
    private DcMotor lf;
    private DcMotor lb;
    private DcMotor rf;
    private DcMotor rb;

    private long Y1,Y2,X;
    private int y1,y2,x;
    private PID pid1,pid2,pid3,pid1_0,pid2_0,pid3_0;

    public Chassis(DcMotor lf1,DcMotor lb1,DcMotor rf1,DcMotor rb1){
        //初始化4个电机
        lf=lf1;
        lb=lb1;
        rf=rf1;
        rb=rb1;

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
        pid1.limit(0.5);
        pid2.limit(0.9);
        pid3.limit(0.5);
    }
    public double move(double x,double y,double angle,double power) {


        angle = -angle;

        double sum = Math.abs(x) + Math.abs(y) + Math.abs(angle);
        if (sum > 1) {
            double k = 1 / sum;
            x *= k;
            y *= k;
            angle *= k;
        }

        if (sum * power < 0.1) {
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

        return sum * power;
    }

}
