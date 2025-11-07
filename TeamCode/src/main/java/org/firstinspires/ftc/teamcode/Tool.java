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

    public void addTriggerToChange(String tiggerName,int initialVal){
        Object[] temp = new Object[3];
        temp[0] = true;
        temp[1] = initialVal;
        temp[2] = "switchTrigger";
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
        assert temp != null;
        if((boolean)temp[0] && val){
            temp[0]=false;
            temp[1]=((int)temp[1]+1)%2;
            if((int)temp[1]==1){
                function1.run();
            }
            else if((int)temp[1]==0){
                function2.run();
            }

        }
        if(!(boolean)temp[0] && !val){
            temp[0]=true;
        }
    }


}
