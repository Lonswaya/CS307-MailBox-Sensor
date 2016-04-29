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
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
/**
 * Used to bridge android to server
 */
public class Server implements Runnable, MessageProcessor, Serializable {

    public volatile ArrayList<ClientConfig> sensorList;
    public Queue<Message> notificationQueue = new LinkedList<Message>();
    public ArrayList<ClientConfig> oldSensorList = new ArrayList<ClientConfig>();

    public String server_ip;
    public String my_ip;
    public int server_port = StaticPorts.serverPort;
    public SocketWrapper centralServer = null;
    public boolean running = true;
    public boolean suspended = false;
    public boolean updatedList = false;
    boolean looperPrepared = false;
    public int sleepTime = 2000;
    public ServerCallback UI;
    public UIInfo user;
    public int msgCooldown = 0;
    public boolean dialogBoxClosed = true;

    public Server(String ip, Context UI) {
        this.server_ip = ip;
        //System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");  //get ssl working
        //System.setProperty("javax.net.ssl.trustStorePassword", "sensor"); //get ssl working
        my_ip = Server.getMyIP();
        this.UI = (ServerCallback) UI;

    }

    public void setUser(UIInfo user){
        this.user = user;
    }

    /*
    Initializes the connection to central server
    Return: true if connection is set up, false if connection is not setup
     */
    private boolean serverInit(){
        System.out.println("!!!!!!!!!!!!!!!!!!!!!In Server Init()!!!!!!!!!!!!!!!!!!!!!!");
        //TODO: insert username and password in place of null, null
        this.centralServer = UserBackend.SetServerConnection(server_ip, this, "", "");
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

    /*Checks if sever needs to update the sensor list
    Param: new list, old list
    Return: true if yes false if no
     */
    public boolean needUpdateSensors(ArrayList<ClientConfig> list, ArrayList<ClientConfig> oldSensorList) {
       if(list == null) return true;
        if(list.size() != oldSensorList.size()) return true;
        if(list.size() == 0 && oldSensorList.size() == 0) return true;
        try {
            if(list.size() != 0) {
                for (int i = 0; i < oldSensorList.size(); i++) {
                    int idx = containSensor(list, oldSensorList.get(i)); //if the sensor exist in list
                    if (idx != -1) {//does contain
                        if (!compareClientConfig(list.get(idx), oldSensorList.get(i))) return true;
                    } else {
                        return true;
                    }
                }
            }else return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    Checks if s contains C
    Param: S list and config c
    Return index if contains
     */
    public int containSensor(ArrayList<ClientConfig> s, ClientConfig c){
        int idx = -1;
        for(int i = 0; i < s.size(); i++){
            if(s.get(i).ip.compareTo(c.ip) == 0) {idx = i; break;}
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
        if(s1.start_hours != s2.start_hours ||
                s1.start_minutes != s2.start_minutes ||
                s1.stop_hours != s2.stop_hours ||
                s1.stop_minutes != s2.stop_minutes ||
                s1.force_off != s2.force_off ||
                s1.force_on != s2.force_on ||
                s1.sensor_type != s2.sensor_type ||
                s1.sensing_threshold != s2.sensing_threshold ||
                s1.name.compareTo(s2.name) != 0 ||
                s1.desktopNotification != s2.desktopNotification ||
                s1.emailNotification != s2.emailNotification ||
                s1.magicMirrorNotification != s2.magicMirrorNotification ||
                s1.textNotification != s2.textNotification ||
                s1.phoneNumber.compareTo(s2.phoneNumber) != 0 ||
                s1.emailAddress.compareTo(s2.emailAddress) != 0 ||
                s1.sensorInterval != s2.sensorInterval ||
                s1.ip.compareTo(s2.ip) == 0) return false;
        return true;
    }

    public ArrayList<Sensor> generateUISensorsList(ArrayList<ClientConfig> sl){
        ArrayList<Sensor> list = new ArrayList<Sensor>();
        for(ClientConfig config : sl){
            Sensor s = convertClientConfigToSensor(config);
            list.add(s);
        }
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
            }else getSensorsFromServer();
        }

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
            if(!suspended) {
                if(msgCooldown <= 0){
                    if(dialogBoxClosed) {
                        ReadingMessage msg = (ReadingMessage) notificationQueue.poll();
                        if(msg != null){
                            ArrayList<ClientConfig> currentSensors = (ArrayList<ClientConfig>) this.sensorList.clone();
                            Calendar c = Calendar.getInstance();
                            String time = c.get(Calendar.HOUR) +  ":" + c.get(Calendar.SECOND);
                            String name = null;
                            ClientConfig s = null;
                            for(int i = 0; i < currentSensors.size(); i++) {
                                if (currentSensors.get(i).ip.compareTo(msg.from) == 0) {
                                    name = currentSensors.get(i).name;
                                    s = currentSensors.get(i);
                                    break;
                                }
                            }
                            if(name != null && s != null){
                                String str = "Alert: " + name + "(" +s.sensor_type +  ")" + "above threshold! at " + time;
                                UI.notifyUser(name, time);
                            }
                        }
                        msgCooldown = 5000;
                    }
                }else{
                    msgCooldown -= sleepTime;
                }


                ArrayList<ClientConfig> temp = new ArrayList<ClientConfig>();
                if(this.sensorList != null) temp = (ArrayList<ClientConfig>) this.sensorList.clone();
                // setting up temp list to compare
                // use this just to make sure we don't get a new sensorlist while processing

                getSensorsFromServer();
                if (oldSensorList == null) oldSensorList = new ArrayList<ClientConfig>();
                boolean updateUI = needUpdateSensors(temp, oldSensorList);
                System.out.println("    BACKEND SREVER DEBUG: tempsize = " + temp.size() + " old size = " + oldSensorList.size());
                if (oldSensorList != null) {
                    System.out.println("    BACKEND SREVER DEBUG: updateUI = " + updateUI);
                    if (updateUI) {
                        System.out.println("Doing Looper Prepare");
                        if (looperPrepared == false) {
                            Looper.prepare();
                            looperPrepared = true;
                        }
                        ArrayList<Sensor> sensors = generateUISensorsList(temp);
                        UI.handleMessage(sensors, temp);
                    }
                }
                oldSensorList = (ArrayList<ClientConfig>) temp.clone();
                try {
                    Thread.sleep(sleepTime);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else System.out.println("    BACKEND SREVER DEBUG: Server thread suspended");
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
                        case AUDIO:
                            AudioMessage aMsg = (AudioMessage) msg;
                            if(aMsg.getCurrentThreshold() >= 0){
                                ArrayList<ClientConfig> currentSensors = (ArrayList<ClientConfig>) ref.sensorList.clone();
                                for(int i = 0; i < currentSensors.size(); i++) {
                                    if (currentSensors.get(i).ip.compareTo(msg.from) == 0) {
                                        if(currentSensors.get(i).sensing_threshold < aMsg.getCurrentThreshold()){
                                            ref.notificationQueue.add(aMsg);
                                            break;
                                        }
                                    }
                                }
                            }

                        case READING:
                            ReadingMessage rMsg = (ReadingMessage) msg;
                            //float reading = rMsg.getCurrentThreshold();
                            notificationQueue.add(rMsg);

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
        if(pos == -1){
            System.out.println("    BACKEND SREVER DEBUG: Sensor doesn't exist");
            UI.notifyUser("Sensor doesn't exist!", "");
            return false;
        }
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
        if(pos == -1){
            System.out.println("    BACKEND SREVER DEBUG: Sensor doesn't exist");
            UI.notifyUser("Sensor doesn't exist!", "");
            return false;
        }
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
        if(containSensor(this.sensorList, config) != -1){
            System.out.println("    BACKEND SREVER DEBUG: Sensor Already exist");
            UI.notifyUser("Sensor already exist!", "");
            return false;
        }
        if(!UserBackend.AddSensor(config, this.centralServer)){
            System.out.println("    BACKEND SREVER DEBUG: Conenction issue!");
            return false;
        }
        else return true;
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
        void handleMessage(ArrayList<Sensor> newSensorList, ArrayList<ClientConfig> newSensorInfoList);
        void notifyUser(String message, String time);
    }
}