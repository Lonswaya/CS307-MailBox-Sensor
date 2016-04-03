package cs307.purdue.edu.autoawareapp;

import java.io.Serializable;


public class LightMessage extends Message implements Serializable {

    //private float light_intensity;

    public LightMessage(String message, SensorInfo config) {
        super(message, config, MessageType.LIGHT);
        //
    }

}
