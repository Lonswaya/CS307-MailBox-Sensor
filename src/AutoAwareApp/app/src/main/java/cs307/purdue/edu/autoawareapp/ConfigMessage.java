package cs307.purdue.edu.autoawareapp;

/**
 * Created by Zixuan on 4/4/2016.
 */
import java.io.Serializable;


public class ConfigMessage extends Message implements Serializable  {
    protected boolean delete;

    public ConfigMessage(String message, SensorInfo config) {
        super(message, config, MessageType.CONFIG);
        delete = false;
        //
    }


}