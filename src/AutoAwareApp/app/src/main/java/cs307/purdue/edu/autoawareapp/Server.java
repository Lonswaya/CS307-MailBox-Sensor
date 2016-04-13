package cs307.purdue.edu.autoawareapp;

import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresPermission;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
/**
 * Used to bridge android to server
 */
public class Server implements Runnable, MessageProcessor, Serializable {

    public volatile ArrayList<ClientConfig> sensorList;

    public String server_ip;
    public String my_ip;
    public int server_port = StaticPorts.serverPort;
    public SocketWrapper centralServer = null;
    public boolean running = true;
    public boolean suspended = false;

    public Server(String ip) {
        this.server_ip = ip;
        //System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");  //get ssl working
        //System.setProperty("javax.net.ssl.trustStorePassword", "sensor"); //get ssl working
        my_ip = Server.getMyIP();
    }

    /*
    Initializes the connection to central server
    Return: true if connection is set up, false if connection is not setup
     */
    public boolean serverInit(){
        this.centralServer = UserBackend.SetServerConnection(server_ip, this);
        if(this.centralServer == null) return false;
        return true;
    }
    /*
    Get a list of sensor infos (ClientConfig) from central server
    Used By Server.java only
    */
    private void getSensorsFromServer(){
        UserBackend.GetSensors(centralServer);
    }

    /*
    Find the position of a sensor in the ArrayList
    Param: clientconfig
    Return: int if success, -1 if not found
     */
    private int getSensorPosition(ClientConfig config){
        for (int i = 0; i < sensorList.size(); i++) {
            if (config.ip == sensorList.get(i).ip){
                return i;
            }
        }
        System.out.println("    BACKEND SREVER DEBUG: Sensor Not Found");
        return -1;
    }

    /*
    Run of the thread
     */
    @Override
    public void run() {
        /*try {

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

        }catch(Exception e){
            e.printStackTrace();
        }*/

        while(running) {
            //shitty timer
            if(!suspended) {
                int interval = 5000;
                long lastMilli = System.currentTimeMillis();
                while (true) {
                    if ((int) (System.currentTimeMillis() - lastMilli) >= interval) break;
                }
                getSensorsFromServer();
            }else System.out.println("    BACKEND SREVER DEBUG: Server thread suspended");
        }
    }

    /*
    Used to handle messages catched from central server
     */
    public void ProcessMessage(Message msg){
        System.out.println("    BACKEND SREVER DEBUG: Got message, type = " + msg.type);
        Thread msgHandler = new Thread(new Runnable() {
            Message msg;
            Server ref;

            @Override
            public void run() {
                try{
                    switch(msg.type){
                        case READING:
                            ReadingMessage rMsg = (ReadingMessage) msg;
                            float reading = rMsg.getCurrentThreshold();

                            //How do you want to handle messags sent to android? Notify UI or store and wait until UI request?

                        case GET_SENSORS:
                            SensorsMessage sMsg = (SensorsMessage) msg;
                            ref.sensorList = sMsg.ar;
                            break;
                        default:
                            System.out.println("    BACKEND SREVER DEBUG: Message type (" + msg.type + ") not supported");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            private Runnable inti(Message msg, Server ref){
                this.msg = msg;
                this.ref = ref;
                return this;
            }
        }.inti(msg, this));
        System.out.println("    BACKEND SREVER DEBUG: Starting mesage handler");
        msgHandler.start();
    }

    /*
    Updates informtion of a sensor to the central server
    Param: sensor's config
    Return: true if success false if fail
    */
    public boolean updateSensor(ClientConfig config) {
        int pos = getSensorPosition(config);
        if(pos < 0) return false;
        UserBackend.SendConfig(config, false, centralServer);
        sensorList.set(pos, config);
        return true;
    }

    /*
    Deletes a sensor from the central server
    Param: ClientConfig
    Return: true if success false if fail
     */
    public boolean deleteSensor(ClientConfig config){
        int pos = getSensorPosition(config);
        if(pos < 0) return false;
        UserBackend.SendConfig(config, true, centralServer);
        sensorList.remove(pos);
        return true;
    }

    /*
    Add a sensor to central server
    Param: a sensor configuration
    Return: true if success false if fail
     */
    public boolean addSensor(ClientConfig config) {
      if(UserBackend.AddSensor(config, this.centralServer)) return true;
      else return false;
    }

    /*
    Get a list of sensor infos (ClientConfig) from android backend server
    Return: ArrayList of clientconfig
     */
    public ArrayList<ClientConfig> getSensorLists(){
        return this.sensorList;
    }

    /*
    Get the ip of current device running the app
    Return: string of ip address, null if unsuccessful
     */
    public static String getMyIP(){
        if (Build.FINGERPRINT.contains("generic")) {
            System.out.println("    BACKEND SREVER DEBUG: IP set to Localhost");
            return "localhost";
        }

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String tmp = inetAddress.getHostAddress().toString();
                        System.out.println("    BACKEND SREVER DEBUG: IP = :" + tmp);
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

    /*
    Checks if ip address is valid
    Param: String of ip
    Return: true if valid false if not valid
    */
    public static boolean isValidIP(String ip){
        //implement this?
        return true;
    }
}