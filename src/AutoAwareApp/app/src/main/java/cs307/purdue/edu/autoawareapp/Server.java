package cs307.purdue.edu.autoawareapp;

import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
/**
 * Used to grab sensor infos from central server
 */
public class Server implements Runnable, MessageProcessor {
    public ArrayList<SensorInfo> sensorList;

    public String server_ip;
    public int server_port;

    public CentralServer connector = null;
    public Thread connectorThread = null;


    public Server() {
        System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");  //get ssl working
        System.setProperty("javax.net.ssl.trustStorePassword", "sensor"); //get ssl working
        //connectorThread.start();
    }
    //use this after constructor to be safe
    public void setUpConnector(){
        connector = new CentralServer(this);
        connectorThread = new Thread(connector);
    }
    public void ProcessMessage(Message msg){
        try{
            switch(msg.type){
                case READING:
                    ReadingMessage rMsg = (ReadingMessage) msg;
                    float reading = rMsg.getCurrentThreshold();

                    //update the reading. I don't know how do you want readings strutured ? in SensorInfo?

                case GET_SENSORS:
                    SensorsMessage sMsg = (SensorsMessage) msg;
                    sensorList = sMsg.ar;
                    break;
                default:
                    System.out.println("Message type: " + msg.type + " not supported");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateSensorInfoList(SensorInfo sensorInfo) {
        for (int i = 0; i < sensorList.size(); i++) {
            if (sensorInfo.ip == sensorList.get(i).ip) {
                sensorList.set(i, sensorInfo);
                break;
            }
        }
    }

    public void run() {

        if(connector == null || connectorThread == null){ setUpConnector();}
        connectorThread.start();

        try {
        }catch(Exception e){
            e.printStackTrace();
        }

        while(true) {
            //shitty timer
            int interval = 5000;

            long lastMilli = System.currentTimeMillis();
            while (true) {
                if ((int) (System.currentTimeMillis() - lastMilli) >= interval) break;
            }

            this.sensorList = getSensors();
        }
    }

    public void sendMessage(Message msg){
        this.connector.sendMessageThreaded(msg);
    }
    public void addSensor(SensorInfo info){
        connector.AddSensor(new ConfigMessage("Adding a new sensor", info));
    }
    public ArrayList<SensorInfo> getSensors() {
        //this.sensorList = connectorObject.GetSensors();
        return connector.GetSensors();
    }

    /*class MessageHandler extends Thread{
        Message msg;
        Server ref;
        public MessageHandler(Message msg, Server ref){
            this.msg = msg;
            this.ref = ref;
        }

        public void run(){
            try{

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    class SendMessage extends Thread {
        public Message msg;
        public String ip;
        public int port;

        public SendMessage(Message msg , String ip, int port) {
            this.msg = msg;
            this.ip = ip;
            this.port = port;
        }

        public void run() {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try {
                Socket socket = factory.createSocket(ip, port);
                ObjectOutputStream out;
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(msg);
                out.flush();
                out.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class GetMessage extends Thread{
        public ServerSocket serverSocket;
        private Server serverRef;
        public GetMessage(ServerSocket serverSocket, Server serverRef){
            this.serverSocket = serverSocket;
            this.serverRef = serverRef;
        }
        public void run(){
            while(true) {
                try {
                    Socket socket = serverSocket.accept();
                    new MessageHandler(socket, serverRef);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
}
