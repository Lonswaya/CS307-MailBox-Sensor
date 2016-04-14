package cs307.purdue.edu.autoawareapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Dhairya M. Doshi on 4/1/2016.
 */
public class SensorClientUI extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter = null;
    RecyclerView.LayoutManager llm;
    private ArrayList<Sensor> sensors = new ArrayList<Sensor>();
    int in_index = 0;
    private int numOfSensors;
    private ArrayList<ClientConfig> sensorInfoList = new ArrayList<ClientConfig>();
    private int noSensorFlag = 0;
    Server server;
    String ip;
    Button addSensorButton, exitButton;

    public SensorClientUI() {
    }

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

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>TEST MESSAGE<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        ip  = (String) getIntent().getStringExtra("Server IP");
        System.out.println("*********************" + ip);

        recyclerView = (RecyclerView) findViewById(R.id.sensorRCView);
        recyclerView.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        //server = new Server(ip);
        mAdapter = new MyAdapter(this, sensors, sensorInfoList, server);
        recyclerView.setAdapter(mAdapter);

        addSensorButton = (Button) findViewById(R.id.addButton);
        addSensorButton.setOnClickListener(this);
        exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(this);

        //llm.setOrientation(LinearLayoutManager.VERTICAL);

        /*//int ret = createSensors();
        sensors.add(new Sensor("Sensor 1", 0, "LIGHT", "100.0.0.1"));
        sensorInfoList.add(new ClientConfig("100.0.0.1", "0", "0", false, false, SensorType.LIGHT, 90, "Sensor 1", false, false, false, false, "123456789", "abcd@email.com", 10));
        //server.addClientConfigObject(sensorInfoList.get(0));
        recyclerView.scrollToPosition(sensors.size() - 1);
        //mAdapter.notifyItemInserted(sensors.size() - 1);
        mAdapter.notifyDataSetChanged();*/

        try {
            server = new Server(ip);
            //server.serverInit();
            //server.setUpConnector();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            Thread t = new Thread(server);
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            t.start();
            //t.run();
            System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");

            //sensorInfoList = server.getSensorLists();
            while(sensorInfoList == null){
                sensorInfoList = server.getSensorLists();
                System.out.println("*****************************" + sensorInfoList + "***************************");
            }
            for (int i = 0; i < sensorInfoList.size(); i++) {
                Sensor sensor = convertClientConfigToSensor(sensorInfoList.get(i));
                sensors.add(sensor);
                System.out.println("????????????????????????" + sensor + "?????????????????????????");
                recyclerView.scrollToPosition(sensors.size() - 1);
                mAdapter.notifyDataSetChanged();
            }
        } catch(NullPointerException e) {
            //TODO: Display a waiting screen
            System.out.println("NULL POINTER EXCEPTION");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public Context getContext() {
        return this;
    }

    public int createSensors() {
        Server server = new Server(ip);
        sensorInfoList = server.getSensorLists();
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
                    Sensor sensor = convertClientConfigToSensor(sensorInfoList.get(i));
                    sensors.add(sensor);
                }
            } catch (Exception e) {
                return -1;
            }
        }
        return 0;
    }

    public int updateSensors(Server server) {
        sensorInfoList = server.getSensorLists();
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
                    Sensor sensor = convertClientConfigToSensor(sensorInfoList.get(i));
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

    public Sensor convertClientConfigToSensor(ClientConfig s) {
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

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addButton:
                Intent mIntent = new Intent(this, AddSensorUI.class);
                mIntent.putExtra("Name", "Add Sensor");
                startActivity(mIntent);
                break;
            case R.id.exitButton:
                System.exit(0);
                break;
            default:
                break;
        }
    }
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
