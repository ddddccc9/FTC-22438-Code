package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.*;

public class Tool {

    private Map<String, Object[]> triggers = new HashMap<>();

    public Tool(){

    }
    private void func(Runnable function) {
        function.run();
    }

    public void blind(String pad_name,String button_name,String type,Runnable function){

    }

    public void addTriggerToChange(String tiggerName,Boolean initialVal){
        Object[] temp = new Object[2];

        temp[0] = initialVal;

        triggers.put(tiggerName,temp);
    }



    public void update(String name,boolean val,Runnable function1){
        Object[] temp = triggers.get(name);
        if((boolean)temp[0] && val){
            temp[0]=false;
            function1.run();
        }
        if(!(boolean)temp[0] && !val){
            temp[0]=true;
        }
    }

    public void update(String name,boolean val,Runnable function1,Runnable function2){
        Object[] temp = triggers.get(name);
        if((boolean)temp[0] && val){
            temp[0]=false;
            function1.run();
        }
        if(!(boolean)temp[0] && !val){
            temp[0]=true;
            function2.run();
        }
    }


}
