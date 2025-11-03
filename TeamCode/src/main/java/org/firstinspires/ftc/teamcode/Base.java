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

    private final double[] Data_turn = {0.19, 0.563, 0.94,0,0.37,0.75};

    public void runOpMode() {}


    public Base(DcMotor lf1,DcMotor lb1,DcMotor rf1,DcMotor rb1,Servo lift1,Servo turn1){
        motor_lf=lf1;
        motor_lb=lb1;
        motor_rf=rf1;
        motor_rb=rb1;

        lift=lift1;
        turn=turn1;
    }


    public void TURN(int id,boolean T) {
        turn.setPosition(Data_turn[id]);
        if(T) {
            sleep(1000);
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
        sleep(1000);
        lift.setPosition(0);
        sleep(1000);
    }

}
