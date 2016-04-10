package cs307.purdue.edu.autoawareapp;

import android.net.wifi.WifiManager;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Formatter;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
/**
 * Used to grab sensor infos from central server
 */
public class Server implements Runnable, MessageProcessor, Serializable {
    public ArrayList<ClientConfig> sensorList;

    public String server_ip;
    public String my_ip;
    public int server_port = StaticPorts.serverPort;

    //public CentralServer connector = null;
    //public Thread connectorThread = null;
    public Socket godSocket;
    public ObjectOutputStream godOut;
    public ObjectInputStream godIn;

    public Server(String ip) {
        this.server_ip = ip;
        //System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");  //get ssl working
        //System.setProperty("javax.net.ssl.trustStorePassword", "sensor"); //get ssl working
        my_ip = "128.210.106.76";
        //setupSocketAndThings();
    }
    //use this after constructor to be safe
    public void setUpConnector(){
        //connector = new CentralServer(this);
        //connectorThread = new Thread(connector);
    }

    public void addClientConfigObject(ClientConfig sensorInfo) {
        if (sensorInfo != null) {
            sensorList.add(sensorInfo);
        }
    }

    public void ProcessMessage(Message msg){
        try{
            switch(msg.type){
                case READING:
                    ReadingMessage rMsg = (ReadingMessage) msg;
                    float reading = rMsg.getCurrentThreshold();

                    //update the reading. I don't know how do you want readings strutured ? in ClientConfig?

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

    public void setupSocketAndThings(){
        try {
            godSocket = new Socket(server_ip, server_port);
            godOut = new ObjectOutputStream(godSocket.getOutputStream());
            godIn = new ObjectInputStream(godSocket.getInputStream());
        }catch(Exception e){
            System.out.println("Can't fucking connect to server and streams and shits dumbshit");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void updateClientConfigList(ClientConfig sensorInfo) {
        for (int i = 0; i < sensorList.size(); i++) {
            if (sensorInfo.ip == sensorList.get(i).ip) {
                sensorList.set(i, sensorInfo);
                break;
            }
        }
    }

    public void run() {
        try {
            /*
                TESTING SHIT IF THIS BREAKS NOTHIGN WORKS
            SensorsMessage msg = new SensorsMessage("asdasdasd", null);
            System.out.println("IP: " + my_ip);
            System.out.println("open sock");
            Socket sock = new Socket("10.186.95.215", 8888);
            System.out.println("get output stream");
            ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
            System.out.println("write shit");
            msg.setFrom(ip);
            out.writeObject(msg);
            System.out.println("All is good familia");
            System.out.print("got message");
            ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
            System.out.print("stream good");
            Message msg2 = (Message) in.readObject();
            System.out.println(msg2.toString());
            */
        }catch(Exception e){
            e.printStackTrace();
        }

        while(true) {
            if(godSocket == null){
                try {
                    godSocket = new Socket(server_ip, server_port);
                    godOut = new ObjectOutputStream(godSocket.getOutputStream());
                    godIn = new ObjectInputStream(godSocket.getInputStream());
                }catch(Exception e){
                    System.out.println("Can't fucking connect to server and streams and shits dumbshit");
                    e.printStackTrace();
                    System.exit(0);
                }
            }
            if(godOut == null || godIn == null){
                try {

                    godOut = new ObjectOutputStream(godSocket.getOutputStream());
                    godIn = new ObjectInputStream(godSocket.getInputStream());
                }catch(Exception e){
                    System.out.println("Can't fucking connect to server and streams and shits dumbshit");
                    e.printStackTrace();
                    System.exit(0);
                }
            }

            //shitty timer
            int interval = 5000;
            long lastMilli = System.currentTimeMillis();
            while (true) {
                if ((int) (System.currentTimeMillis() - lastMilli) >= interval) break;
            }

            this.sensorList = getSensors();
        }
    }
    public void addSensor(ClientConfig config) {
        if (godOut == null) return;
        ConfigMessage msg = new ConfigMessage("Add Sensor", config);
        msg.setFrom(my_ip);
        msg.type = MessageType.ADD_SENSOR;
        try{
            godOut.writeObject(msg);
            godOut.flush();

        }catch(Exception e){

        }
    }
    public ArrayList<ClientConfig> getSensors(){
        SensorsMessage msg = new SensorsMessage("Get list of sensors", null);
        msg.setFrom(my_ip);

        if(godOut == null) return null;
        try {
            godOut.writeObject(msg);
            godOut.flush();
            msg = null;
            while(msg == null){
                System.out.println("waiting for messageasdadasdasdsadadasdasadsd");
                msg = (SensorsMessage) godIn.readObject();

                System.out.println("got listasdasdasdadasdasdsadasdasdds");
                godIn.close();
                godOut.close();
                return msg.ar;


            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<ClientConfig> getSensorLists(){
        return this.sensorList;
    }

    public String getMyIP(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String tmp = inetAddress.getHostAddress().toString();
                        System.out.println("IP:" + tmp);
                        return tmp;
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println("Something wrong getting ip, shit doesnt work if you get this ");
            System.exit(0);
        }
        return null;
    }
    /*public void sendMessage(Message msg){
        this.connector.sendMessageThreaded(msg);
    }
    public void addSensor(ClientConfig info){
        connector.AddSensor(new ConfigMessage("Adding a new sensor", info));
    }
    public ArrayList<ClientConfig> getSensors() {
        //this.sensorList = connectorObject.GetSensors();
        return connector.GetSensors();
    }

    class MessageHandler extends Thread{
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
