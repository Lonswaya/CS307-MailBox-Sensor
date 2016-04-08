package cs307.purdue.edu.autoawareapp;

import java.io.*;
import java.util.Calendar;

public class Message implements Serializable {

    protected String message;
    protected SensorInfo config;
    protected MessageType type;
    protected String from;
    protected String creationTime;

    public Message(String message, SensorInfo config, MessageType type) {
        setString(message);
        setConfig(config);
        generateCreateTime();
        this.type = type;
    }

    public String getString() {
        return this.message;
    }

    public void setConfig(SensorInfo config) {
        this.config = config;
    }

    public void setString(String message) {
        this.message = message;
    }

    public SensorInfo getConfig() {
        return this.config;
    }

    public void setFrom(String from){
        this.from = from;
    }

    public String getFrom(){
        return this.from;
    }

    public String getCreateTime(){
        return this.creationTime;
    }

    public void generateCreateTime(){
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        this.creationTime = hour + ":" + minute;
    }

    public String toString(){
        String s;
        s = "Message: " + this.message +
                "\nFrom: " + this.from +
                "\nCreated at: " + this.creationTime +
                "\nType: " + this.type;
        return s;
    }




}