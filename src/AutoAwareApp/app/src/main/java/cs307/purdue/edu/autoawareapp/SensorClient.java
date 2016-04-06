package cs307.purdue.edu.autoawareapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Dhairya M. Doshi on 4/1/2016.
 */
public class SensorClient extends AppCompatActivity {
    private RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter = null;
    RecyclerView.LayoutManager llm;
    private ArrayList<Sensor> sensors = new ArrayList<Sensor>();;
    int in_index = 0;
    private int numOfSensors;
    private ArrayList<SensorInfo> sensorInfoList;
    private int noSensorFlag = 0;

    public int getNumOfSensors() {
        return numOfSensors;
    }

    public void setNumOfSensors(int numOfSensors) {
        this.numOfSensors = numOfSensors;
    }

    public ArrayList<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList<Sensor> sensors) {
        this.sensors = sensors;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.sensorRCView);
        recyclerView.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        mAdapter = new MyAdapter(this, sensors);
        recyclerView.setAdapter(mAdapter);
        //llm.setOrientation(LinearLayoutManager.VERTICAL);

        //int ret = createSensors();
        sensors.add(new Sensor("Sensor 1", 0, "LIGHT", "100.0.0.1"));
        recyclerView.scrollToPosition(sensors.size() - 1);
        //mAdapter.notifyItemInserted(sensors.size() - 1);
        mAdapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public int createSensors() {
        Server s = new Server();
        sensorInfoList = s.getSensors();
        System.out.println(sensorInfoList);
        if (sensorInfoList != null) {
            setNumOfSensors(sensorInfoList.size());
        }
        else {
            noSensorFlag = 1;
            return 0;
        }
        if (numOfSensors == 0) {
            noSensorFlag = 1;
            return 0;
        }
        else {
            try {
                for (int i = 0; i < numOfSensors; i++) {
                    Sensor sensor = convertSensorInfoToSensor(sensorInfoList.get(i));
                    sensors.add(sensor);
                }
            } catch (Exception e) {
                return -1;
            }
        }
        return 0;
    }

    public int updateSensors(Server server) {
        sensorInfoList = server.getSensors();
        if (sensorInfoList != null) {
            setNumOfSensors(sensorInfoList.size());
        }
        else {
            noSensorFlag = 1;
            return 0;
        }
        if (numOfSensors == 0) {
            noSensorFlag = 1;
            return 0;
        }
        else {
            noSensorFlag = 0;
            try {
                sensors.clear();
                for (int i = 0; i < numOfSensors; i++) {
                    Sensor sensor = convertSensorInfoToSensor(sensorInfoList.get(i));
                    int check = checkSensorMatch(sensor.getIp());

                    if (check == -1) {
                        sensors.add(sensor);
                        recyclerView.scrollToPosition(sensors.size() - 1);
                        mAdapter.notifyDataSetChanged();
                    }
                    else if (check != -1) {
                        //TODO: Get value form the client
                        float currentVal = sensorInfoList.get(i).sensing_threshold;
                        sensors.get(check).setSeekCurrentValue((int) currentVal);
                        int val = 0;
                        if (currentVal % 1 < 0.5) {
                            val = (int) currentVal;
                        }
                        else {
                            val = (int) currentVal + 1;
                        }
                        sensors.get(check).getCurrentValBar().setProgress(val);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                return -1;
            }
        }
        return 0;
    }

    public int checkSensorMatch(String ip) {
        for (int i = 0; i < sensors.size(); i++) {
            if (sensors.get(i).getIp() == ip) {
                return i;
            }
        }
        return -1;
    }

    public void updateView() {

    }

    public Sensor convertSensorInfoToSensor(SensorInfo s) {
        Sensor newSensor = new Sensor();
        newSensor.setName(s.name);

        switch (s.sensor_type) {
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
        }

        newSensor.setIp(s.ip);
        newSensor.setSeekDefaultValue(0);
        newSensor.setSeekCurrentValue((int) s.sensing_threshold);
        newSensor.getPlayButton().setVisibility(View.INVISIBLE);

        return newSensor;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onClick(View view) {
//
//        switch (view.getId()) {
//            case R.id.sendButton:
//
//                String messString = messageText.getText().toString();
//                if (!messString.equals("")) {
//                    Message message = new Message("", messString, true, new Date());
//                    messages.add(message);
//                    messageList.scrollToPosition(messages.size() - 1);
//                    mAdapter.notifyDataSetChanged();
//                    sendMessage();
//                    message = null;
//                    messageText.setText("");
//
//                }
//
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    private void sendMessage() {
//
//        String[] incoming = {"Hey, How's it going?",
//                "Super! Let's do lunch tomorrow",
//                "How about Mexican?",
//                "Great, I found this new place around the corner",
//                "Ok, see you at 12 then!"};
//
//        if (in_index < incoming.length) {
//            Message message = new Message("John", incoming[in_index], false,  new Date());
//            messages.add(message);
//            in_index++;
//            messageList.scrollToPosition(messages.size()-1);
//            mAdapter.notifyDataSetChanged();
//        }
}
