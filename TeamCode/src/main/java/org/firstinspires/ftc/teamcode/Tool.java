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

    public double[] Calculate_Line(double x1,double y1,double x2,double y2){
        double[] temp = new double[2];

        double a = (y2-y1)/(x2-x1);
        double b = y1 - a * x1;

        temp[0] = a;
        temp[1] = b;
        return temp;
    }

    public int[] Goal_update(int id) {
        int[] List_goal = new int[]{0, 0, 0};
        List_goal[id - 21] = 1;
        return  List_goal;
    }

    public int[] Current_update(int firstGreenX) {
        int[] List_current = new int[]{0, 0, 0};
        if (firstGreenX <= 150) { //左
            List_current[0] = 1;
        } else if (firstGreenX <= 200) { //中
            List_current[1] = 1;
        } else if (firstGreenX > 200) { //右
            List_current[2] = 1;
        }
        else {
            List_current = new int[]{-1,-1,-1};
        }
        return List_current;
    }

    public int[] Calculate_order(int[] List_goal,int[] List_current) {
        try {
            int[] List_order = new int[]{0,1,2};
            //初始化池
            ArrayList<Integer> pool = new ArrayList<>();
            for (int i = 0; i < List_current.length; i++) {
                pool.add(i);
            }

            //计算顺序
            for (int i = 0; i < List_goal.length; i++) {
                int index = 0;
                while (List_goal[i] != List_current[pool.get(index)] && index<pool.size()-1) index++;
                List_order[i] = pool.get(index);
                pool.remove(index);
            }

            ///因为顺序与转盘顺序不一致
            /// 左-2 中-0 右-1
            /// 前面是列表顺序，后面是对应的转盘旋转ID

            //转换顺序到转盘顺序
            for (int i = 0; i < List_order.length; i++) {
                List_order[i] = (List_order[i] + 2) % 3;
            }
            return List_order;


        } catch (ArithmeticException e) {  //小球数量有问题 比如吸进了俩绿色
            return new int[]{0,1,2};
        }
    }

    //输出数组
    public String show_list(int[] a){
        String ans="",temp="";
        for (int e:a){
            temp = ans + e +" ";
            ans = temp;
        }
        return ans;
    }


}
