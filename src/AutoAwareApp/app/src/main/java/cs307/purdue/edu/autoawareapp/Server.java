package cs307.purdue.edu.autoawareapp;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresPermission;
import android.view.View;

import org.apache.http.conn.util.InetAddressUtils;

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
    public boolean workingSensorList = false;
    public ServerCallback UI;
    boolean looperPrepared = false;

    public Server(String ip, Context UI) {
        this.server_ip = ip;
        //System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");  //get ssl working
        //System.setProperty("javax.net.ssl.trustStorePassword", "sensor"); //get ssl working
        my_ip = Server.getMyIP();
        this.UI = (ServerCallback) UI;
    }

    /*
    Initializes the connection to central server
    Return: true if connection is set up, false if connection is not setup
     */
    private boolean serverInit(){
        System.out.println("!!!!!!!!!!!!!!!!!!!!!In Server Init()!!!!!!!!!!!!!!!!!!!!!!");
        this.centralServer = UserBackend.SetServerConnection(server_ip, this);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!Returning!!!!!!!!!!!!!!!!!!!!!!");
        if(this.centralServer == null)
            return false;
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

    public boolean needUpdateSensors(ArrayList<ClientConfig> oldSensorList) {
       if(this.sensorList == null) return true;
        ArrayList<ClientConfig> list = (ArrayList<ClientConfig>) this.sensorList.clone();

        if(list.size() != oldSensorList.size()) return true;
        try {
            for (int i = 0; i < oldSensorList.size(); i++) {
                int idx = containSensor(list, oldSensorList.get(i));
                if(idx != -1){
                    if(!compareClientConfig(list.get(idx), oldSensorList.get(i))) return true;
                }
                else
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public int containSensor(ArrayList<ClientConfig> s, ClientConfig c){
        int idx = -1;
        for(int i = 0; i < s.size(); i++){
            if(s.get(i).ip.compareTo(c.ip) == 0) idx = i;
        }
        return idx;
    }
    /*
    Compares if two configs are equal
    Param: ClientConfig, ClientConfig
    Return: true if same, false if different
     */
    public boolean compareClientConfig(ClientConfig s1, ClientConfig s2) {
        if(s1 == null || s2 == null) return false;
        if(s1.start_hours != s2.start_hours) return false;
        if(s1.start_minutes != s2.start_minutes) return false;
        if(s1.stop_hours != s2.stop_hours) return false;
        if(s1.stop_minutes != s2.stop_minutes) return false;
        if(s1.force_off != s2.force_off) return false;
        if(s1.force_on != s2.force_on) return false;
        if(s1.sensor_type != s2.sensor_type) return false;
        if(s1.sensing_threshold != s2.sensing_threshold) return false;
        if(s1.name.compareTo(s2.name) == 0) return false;
        if(s1.desktopNotification != s2.desktopNotification) return false;
        if(s1.emailNotification != s2.emailNotification) return false;
        if(s1.magicMirrorNotification != s2.magicMirrorNotification) return false;
        if(s1.textNotification != s2.textNotification) return false;
        if(s1.phoneNumber.compareTo(s2.phoneNumber) == 0) return false;
        if(s1.emailAddress.compareTo(s2.emailAddress) == 0) return false;
        if(s1.interval != s2.interval) return false;
        if(s1.ip.compareTo(s2.ip) == 0) return false;
        return true;
    }

    public ArrayList<Sensor> generateUISensorsList(){
        System.out.println("In UpdateUI if statement 2.1");
        ArrayList<Sensor> list = new ArrayList<Sensor>();
        System.out.println("In UpdateUI if statement 2.2");
        for(ClientConfig config : this.sensorList){
            System.out.println("In UpdateUI if statement 2.3");
            Sensor s = convertClientConfigToSensor(config);
            System.out.println("In UpdateUI if statement 2.4");
            list.add(s);
        }
        System.out.println("In UpdateUI if statement 2.5");
        return list;
    }


    public Sensor convertClientConfigToSensor(ClientConfig s) {
        Sensor newSensor = new Sensor();
        newSensor.setName(s.name);

        /*switch (s.sensor_type) {
            case VIDEO: newSensor.setType("VIDEO");
                newSensor.setSensorTypeImage(R.mipmap.ic_video_icon);
                break;
            case AUDIO: newSensor.setType("AUDIO");
                newSensor.setSensorTypeImage(R.mipmap.ic_sound_icon);
                break;
            case LIGHT: newSensor.setType("LIGHT");
                newSensor.setSensorTypeImage(R.mipmap.ic_bulb);
                break;
            default: return null;
        }*/

        newSensor.setIp(s.ip);
        newSensor.setSeekDefaultValue(0);
        newSensor.setSeekCurrentValue((int) s.sensing_threshold);
        //newSensor.getPlayButton().setVisibility(View.INVISIBLE);

        return newSensor;

    }

    /*
    Run of the thread
     */
    @Override
    public void run() {
        System.out.println("Debug message: In run() method");
        if(centralServer == null){
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            if(!serverInit()){
                System.out.println("    BACKEND SREVER DEBUG: Can't get central server connection");
            }
        }

        //addSensor(new ClientConfig("100.0.0.1", "0:00", "0:00", false, false, SensorType.LIGHT, 90, "Sensor 1", false, false, false, false, "123456789", "abcd@email.com", 10));
        //System.out.println(sensorList.get(0));
        //sensorList.add(new ClientConfig("100.0.0.1", "0:00", "0:00", false, false, SensorType.LIGHT, 90, "Sensor 1", false, false, false, false, "123456789", "abcd@email.com", 10));
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
        }
        */
        System.out.println("Debug Message: Going into running");
        while(running) {
            //shitty timer
            if (!suspended) {
                //boolean check = addSensor(new ClientConfig("100.0.0.1", "0:00", "0:00", false, false, SensorType.LIGHT, 90, "Sensor 1", false, false, false, false, "123456789", "abcd@email.com", 10));
                //System.out.println("Debug Message: In Server call " + check);

                workingSensorList = true;
                ArrayList<ClientConfig> oldSensorList;
                if (sensorList == null) {
                    oldSensorList = null;
                } else {
                    oldSensorList = (ArrayList<ClientConfig>) sensorList.clone();
                }

                getSensorsFromServer();

                boolean updateUI = needUpdateSensors(oldSensorList);
                if (oldSensorList != null) {
                    System.out.println("    BACKEND SREVER DEBUG: updateUI = " + updateUI);

                    if (updateUI) {
                        System.out.println("Doing Looper Prepare");
                        if (looperPrepared == false) {
                            Looper.prepare();
                            looperPrepared = true;
                        }
                        System.out.println("In UpdateUI if statement " + oldSensorList);
                        ArrayList<Sensor> sensors;
                        System.out.println("In UpdateUI if statement 2");
                        sensors = generateUISensorsList();
                        System.out.println("In UpdateUI if statement 3");
                        UI.handleMessage(sensors, sensorList);
                        System.out.println("In UpdateUI if statement 4");
                        //TODO: Call UI thread and send update sensors ArrayList
                    }
                } else System.out.println("    BACKEND SREVER DEBUG: Server thread suspended");
                //sensorList.add(new ClientConfig("100.0.0." + i*5, "0:00", "0:00", false, false, SensorType.LIGHT, 90, "Sensor 1", false, false, false, false, "123456789", "abcd@email.com", 10));
                //i++;
                workingSensorList = false;

                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    Used to handle messages catched from central server
     */
    public void ProcessMessage (Message msg){
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
                            boolean stop;
                            //do {
                              //  stop = ref.workingSensorList;
                                //System.out.println("    BACKEND SREVER DEBUG: SensorList update status: " + stop);
                            //}while(stop);

                            System.out.println("    BACKEND SREVER DEBUG: SensorList update");
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
        System.out.println("In Add sensor");
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
        /*if (Build.FINGERPRINT.contains("generic")) {
            System.out.println("    BACKEND SREVER DEBUG: IP set to " + BuildConfig.LOCAL_IP);
            return BuildConfig.LOCAL_IP;
        }*/

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    String ipv4;
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {
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

    public interface ServerCallback{
        void  handleMessage(ArrayList<Sensor> newSensorList, ArrayList<ClientConfig> newSensorInfoList);
    }
}