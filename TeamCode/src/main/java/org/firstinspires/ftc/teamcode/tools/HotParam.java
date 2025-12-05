package org.firstinspires.ftc.teamcode.tools;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class HotParam {
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static boolean closed = false;
    private static ArrayList<String> data;
    private static int id = -1;

    private HotParam() {
        socket = null;
        in = null;
        out = null;
        data = new ArrayList<String>();
        id = -1;
    }

    public static boolean connect(int port, boolean keepTry){
        try {
            new HotParam(); // 初始化

            String hostname = "localhost";
            boolean connected = false;
            socket = null;

            // 持续尝试连接直到成功
            while (!connected) {
                try {
                    socket = new Socket(hostname, port);
                    connected = true;
                    System.out.println("成功连接到服务器!");
                } catch (IOException _) {}

                if (!keepTry) break;
            }

            if (socket != null) {
                // 使用DataInputStream和DataOutputStream包装流
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return false连接失败 true连接成功
     */
    public static boolean isConnected() {
        return !(socket == null);
    }

    public static void clear() {
        try{
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean needClose() {
        return !closed;
    }

    public static void waitForParam(){
        try {
            id = -1;
            out.writeUTF("done");
            out.flush();

            String inputLine = in.readUTF();
            if (inputLine != null) {
                data.clear();
                if (inputLine.equals("quit")) {
                    closed = true;
                }
                // 简单按分隔符拆分，实际解析应更严谨
                String[] receivedData = inputLine.split("\n");
                for (String item : receivedData) {
                    data.add(item);
                }
            } else {
                System.out.println("Server closed the connection.");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void show(){
        System.out.print("[");
        for(int i=0;i<data.size();i++){
            System.out.print(data.get(i));
            if(i<data.size()-1) System.out.print(",");
        }
        System.out.print("]");
        System.out.println("");
    }

    /// 信息头部 0-int  1-float  2-double
    public static int add(int a) {
        id++;
        try {
            if(data.size()>id){
                a= Integer.parseInt(data.get(id));
            }
            out.writeUTF(""+a);
            out.flush();
            data.add(""+a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return a;
    }

    public static float add(float a) {
        id++;
        try {
            if(data.size()>id){
                a= Float.parseFloat(data.get(id));
            }
            out.writeUTF(""+a);
            out.flush();
            data.add("" + a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return a;
    }

    public static double add(double a) {
        id++;
        try {
            if(data.size()>id){
                a= Double.parseDouble((data.get(id)));
            }
            out.writeUTF("" + a);
            out.flush();
            data.add("" + a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return a;
    }
}