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
    ArrayList<ClientConfig> sensorInfoList;
    int in_index = 0;
    private int numOfSensors;
    private int noSensorFlag = 0;
    Server server;
    String ip;
    Button addSensorButton, exitButton;

    Sensor newSensor;

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

        newSensor = (Sensor) getIntent().getSerializableExtra("New Sensor");
        if (newSensor != null) {
            System.out.println(newSensor);
        }

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

        System.out.println("Adding 1 sensor");

        long currTime = System.currentTimeMillis();

        //TODO: Comment these lines out
        //int ret = createSensors();
        /*sensors.add(new Sensor("Sensor 1", 0, "LIGHT", "100.0.0.1"));
        sensorInfoList.add(new ClientConfig("100.0.0.1", "0", "0", false, false, SensorType.LIGHT, 90, "Sensor 1", false, false, false, false, "123456789", "abcd@email.com", 10));
        //server.addClientConfigObject(sensorInfoList.get(0));
        recyclerView.scrollToPosition(sensors.size() - 1);
        //mAdapter.notifyItemInserted(sensors.size() - 1);
        mAdapter.notifyDataSetChanged();

        sensors.add(new Sensor("Sensor 5", 0, "AUDIO", "10.182.1.29"));
        sensorInfoList.add(new ClientConfig("10.182.1.29", "6:00", "18:00", true, false, SensorType.AUDIO, 40, "Sensor 5", false, false, true, true, "123456789", "abcd@email.com", 10));
        //server.addClientConfigObject(sensorInfoList.get(0));
        recyclerView.scrollToPosition(sensors.size() - 1);
        //mAdapter.notifyItemInserted(sensors.size() - 1);
        mAdapter.notifyDataSetChanged();

        sensors.add(new Sensor("Spying on ma boy", 0, "VIDEO", "10.182.1.0"));
        sensorInfoList.add(new ClientConfig("10.182.1.0", "6:00", "18:00", true, false, SensorType.VIDEO, 40, "Spying on ma boy", true, false, false, true, "123456789", "abcd@email.com", 10));
        //server.addClientConfigObject(sensorInfoList.get(0));
        recyclerView.scrollToPosition(sensors.size() - 1);
        //mAdapter.notifyItemInserted(sensors.size() - 1);
        mAdapter.notifyDataSetChanged();
        */

        try {
            System.out.println("In try catch");
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
            /*server = new Server(ip);
            while(sensorInfoList == null){
                sensorInfoList = server.getSensorLists();
                System.out.println("*****************************" + sensorInfoList + "***************************");
            }
            for (int i = 0; i < sensorInfoList.size(); i++) {
                System.out.println("Debug message: In for loop");
                Sensor sensor = convertClientConfigToSensor(sensorInfoList.get(i));
                sensors.add(sensor);
                System.out.println("????????????????????????" + sensor + "?????????????????????????");
                recyclerView.scrollToPosition(sensors.size() - 1);
                mAdapter.notifyDataSetChanged();
                server.run();

            }*/
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

    /*public int createSensors() {
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
    }*/



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
                System.out.println("Debug Messsage: Add sensor clicked");
                Intent mIntent = new Intent(this, AddSensorUI.class);
                System.out.println("Debug Messsage: Add sensor clicked 2");
                //mIntent.putExtra("Server", server);
                startActivityForResult(mIntent, 1);
                break;
            case R.id.exitButton:
                System.exit(0);
                break;
            default:
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("In onActivityResult");
        if(resultCode == RESULT_OK){
                ClientConfig newSensorConfig = (ClientConfig) data.getSerializableExtra("New Sensor");
                System.out.println(newSensor);
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
