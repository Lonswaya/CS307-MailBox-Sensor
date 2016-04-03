package cs307.purdue.edu.autoawareapp;

import java.util.ArrayList;


public class SensorsMessage extends Message {
    public ArrayList<SensorInfo> ar;

    public SensorsMessage(String message, ArrayList<SensorInfo> ar) {
        super(message, null, MessageType.GET_SENSORS);
        this.ar = ar;
    }


}
