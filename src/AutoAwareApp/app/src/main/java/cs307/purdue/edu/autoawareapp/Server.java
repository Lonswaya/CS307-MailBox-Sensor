package cs307.purdue.edu.autoawareapp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
/**
 * Used to grab sensor infos from central server
 */
public class Server implements Runnable {
    public ArrayList<SensorInfo> sensorList;
    public String server_ip;
    public int server_port;

    public Server() {
        System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");  //get ssl working
        System.setProperty("javax.net.ssl.trustStorePassword", "sensor"); //get ssl working
    }

    public void run() {
        int interval = 5000;

        long lastMilli = System.currentTimeMillis();
        while(true){
            if( (int) (System.currentTimeMillis() - lastMilli) >= interval) break;
        }

        this.sensorList = getSensors();
    }

    public ArrayList<SensorInfo> getSensors() {
        ArrayList<SensorInfo> list = null;
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        Message msg;
        try {
            ServerSocket server = factory.createServerSocket(server_port);
            msg = new SensorsMessage("Get sensors", null);
            GetMessage getThread = new GetMessage(msg, server);
            getThread.start();
            new SendMessage(msg, server_ip, server_port).start();
            msg = null;
            while(msg == null) msg = getThread.getMessage();
            if(msg.type == MessageType.GET_SENSORS){
                SensorsMessage sMsg = (SensorsMessage) msg;
                list = sMsg.ar;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
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
        public Message msg;
        public ServerSocket serverSocket;

        public GetMessage(Message msg, ServerSocket serverSocket){
            this.msg = msg;
            this.serverSocket = serverSocket;
        }
        public Message getMessage(){
            return this.msg;
        }
        public void run(){
            try{
                Socket socket = serverSocket.accept();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                this.msg = (SensorsMessage) in.readObject();
                //serverSocket.close();
                in.close();
                socket.close();
                serverSocket.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
