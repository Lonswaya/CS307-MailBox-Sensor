package cs307.purdue.edu.autoawareapp;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;

import java.io.Serializable;

/**
 * Created by Dhairya on 4/6/2016.
 */
public class myOnClickListener implements View.OnClickListener {
    MyAdapter.ViewHolder viewHolder;
    Context context;
    int id;
    SensorInfo sensorInfo;
    Server server;

    public myOnClickListener(MyAdapter.ViewHolder viewHolder, Context context, int id, SensorInfo sensorInfo, Server server) {
        this.viewHolder = viewHolder;
        this.context = context;
        this.id = id;
        this.sensorInfo = sensorInfo;
        this.server = server;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        System.out.println("GOT CLICKED!!!!!!!!!!!!!!!!!!");
        switch(view.getId()) {
            case R.id.button1:
                System.out.println("*****************************BUTTON 1");
                Intent mIntent = new Intent(context, SettingsActivity.class);
                mIntent.putExtra("SensorInfo", sensorInfo);
                mIntent.putExtra("Server", server);
                context.startActivity(mIntent);
                break;
            case R.id.button2:
                System.out.println("*****************************BUTTON 2");
                //Intent mIntent2 = new Intent(context, SettingsActivity.class);
                //context.startActivity(mIntent2);
                break;
            default:
                return;
        }
    }

}
