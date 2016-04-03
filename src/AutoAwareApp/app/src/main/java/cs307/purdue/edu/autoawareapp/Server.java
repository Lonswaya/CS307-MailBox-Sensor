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
public class Server implements Runnable{
    public ArrayList<SensorInfo> sensorList;
    public String server_ip;
    public int server_port;

    public Server(){
        System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");  //get ssl working
        System.setProperty("javax.net.ssl.trustStorePassword", "sensor"); //get ssl working
    }

    public void run(){

    }

    public ArrayList<SensorInfo> getSensors(){
        ArrayList<SensorInfo> list = null;
        Socket sendSocket;
        ServerSocket receiveSocket;
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLServerSocketFactory serverfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        try {

            ObjectOutputStream outputStream;
            sendSocket = factory.createSocket(server_ip, server_port);
            outputStream = new ObjectOutputStream(sendSocket.getOutputStream());
            SensorsMessage msg = new SensorsMessage("Get sensors", null);
            outputStream.writeObject(msg);
            outputStream.flush();
            outputStream.close();
            sendSocket.close();
            receiveSocket = serverfactory.createServerSocket(server_port);
            sendSocket = receiveSocket.accept();
            ObjectInputStream inputStream = new ObjectInputStream(sendSocket.getInputStream());
            msg = (SensorsMessage) inputStream.readObject();
            list = msg.ar;
        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }
}
