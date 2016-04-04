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
    public CentralServer connector = new CentralServer(this);

    public Thread receiveMessages;
    private boolean newSensorListFlag = false;

    public Server() {
        System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");  //get ssl working
        System.setProperty("javax.net.ssl.trustStorePassword", "sensor"); //get ssl working
    }

    public void ProcessMessage(Message msg){

    }

    public void run() {

        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        try {
            ServerSocket receiveSocket = factory.createServerSocket(9999); //what port number to create a receive server on android?
            receiveMessages = new GetMessage(receiveSocket, this);
        }catch(Exception e){
            e.printStackTrace();
        }
        receiveMessages.start(); //spawn a thread that constantly recerive messages

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

    public ArrayList<SensorInfo> getSensors() {
        Message msg;
        try {
            newSensorListFlag = false;
            msg = new SensorsMessage("Get sensors", null);
            new SendMessage(msg, server_ip, server_port).start();
            while(newSensorListFlag != true);
            newSensorListFlag = false;
        }catch(Exception e){
            e.printStackTrace();
        }
        return this.sensorList;
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
    }

    class MessageHandler extends Thread{
        public Socket socket;
        private Server serverRef;
        public MessageHandler(Socket socket, Server serverRef){
            this.socket = socket;
            this.serverRef = serverRef;
        }

        public void run(){
            try{
                ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());
                Message msg = (Message) in.readObject();

                switch(msg.type){
                    case READING:
                        ReadingMessage rMsg = (ReadingMessage) msg;
                        float reading = rMsg.getCurrentThreshold();
                        
                        //update the reading. I don't know how do you want readings strutured ? in SensorInfo?

                    case GET_SENSORS:
                        serverRef.newSensorListFlag = true;
                        SensorsMessage sMsg = (SensorsMessage) msg;
                        serverRef.sensorList = sMsg.ar;
                        break;
                    default:
                        System.out.println("Message type: " + msg.type + " not supported");
                }
                in.close();
                socket.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
