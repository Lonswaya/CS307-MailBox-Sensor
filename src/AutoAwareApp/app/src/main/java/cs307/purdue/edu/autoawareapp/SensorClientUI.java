package cs307.purdue.edu.autoawareapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Dhairya M. Doshi on 4/1/2016.
 */
public class SensorClientUI extends AppCompatActivity implements View.OnClickListener, Server.ServerCallback {
    private RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter = null;
    RecyclerView.LayoutManager llm;

    private ArrayList<Sensor> sensors = new ArrayList<Sensor>();
    ArrayList<ClientConfig> sensorInfoList = new ArrayList<ClientConfig>();
    int in_index = 0;
    private int numOfSensors;
    private int noSensorFlag = 0;
    static Server server;
    String ip;
    Button addSensorButton, exitButton;
    String username, password;

    Sensor newSensor;
    UIInfo user;

    Context thisContext;

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
        username = getIntent().getStringExtra("Username");
        password = getIntent().getStringExtra("Password");

        System.out.println("*********************" + ip);

        recyclerView = (RecyclerView) findViewById(R.id.sensorRCView);
        recyclerView.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        //server = new Server(ip);
        mAdapter = new MyAdapter(this, sensors, sensorInfoList, server);
        recyclerView.setAdapter(mAdapter);
        thisContext = this;

        addSensorButton = (Button) findViewById(R.id.addButton);
        addSensorButton.setOnClickListener(this);
        exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(this);

        //llm.setOrientation(LinearLayoutManager.VERTICAL);

        System.out.println("Adding 1 sensor");

        long currTime = System.currentTimeMillis();

        //int ret = createSensors();
        sensors.add(new Sensor("Sensor 1", 0, "LIGHT", "100.0.0.1"));
        sensorInfoList.add(new ClientConfig("100.0.0.1", "0:00", "0:00", false, false, SensorType.LIGHT, 90, "Sensor 1", 0, 0, 0, false, false, false, false, "123456789", "abcd@email.com", 10, 15));
        //server.addClientConfigObject(sensorInfoList.get(0));
        //recyclerView.scrollToPosition(sensors.size() - 1);
        mAdapter.notifyItemInserted(sensors.size() - 1);
        mAdapter.notifyDataSetChanged();


        sensors.add(new Sensor("Sensor 5", 0, "AUDIO", "10.182.1.29"));
        sensorInfoList.add(new ClientConfig("10.182.1.29", "6:00", "18:00", true, false, SensorType.AUDIO, 40, "Sensor 5", 0, 0, 0, false, false, true, true, "123456789", "abcd@email.com", 10, 19));
        //server.addClientConfigObject(sensorInfoList.get(0));
        recyclerView.scrollToPosition(sensors.size() - 1);
        //mAdapter.notifyItemInserted(sensors.size() - 1);
        mAdapter.notifyDataSetChanged();

        sensors.add(new Sensor("Spying on ma boy", 0, "VIDEO", "10.182.1.0"));
        sensorInfoList.add(new ClientConfig("10.182.1.0", "6:00", "18:00", true, false, SensorType.VIDEO, 40, "Spying on ma boy", 0, 0, 0, true, false, false, true, "123456789", "abcd@email.com", 10, 5));
        //server.addClientConfigObject(sensorInfoList.get(0));
        recyclerView.scrollToPosition(sensors.size() - 1);
        //mAdapter.notifyItemInserted(sensors.size() - 1);
        mAdapter.notifyDataSetChanged();


        try {
            System.out.println("In try catch");
            server = new Server(ip, this);
            //server.serverInit();
            //server.setUpConnector();
            user = new UIInfo(server.centralServer, username, password);
            server.setUser(user);
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

    public void notifyUser(SensorType type, String message){
        NotificationFragmentDialog dialog = new NotificationFragmentDialog();
        dialog.show(getSupportFragmentManager(), "Notification");

    }

    public void handleMessage(final ArrayList<Sensor> newSensorList, final ArrayList<ClientConfig> newSensorInfoList){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sensors.clear();
                if (sensorInfoList != null)
                    sensorInfoList.clear();
                System.out.println("*************************************************************************************************************");
                sensors = (ArrayList<Sensor>) newSensorList.clone();
                sensorInfoList = (ArrayList<ClientConfig>) newSensorInfoList.clone();
                //System.out.println("Sensor = " + sensors.get(0).getName());
                mAdapter = new MyAdapter(thisContext, sensors, sensorInfoList, server);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /*@Override
    public void notifyUser(String message, String time) {
        super();
    }*/

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
        System.out.println("Debug Message: In OnActivityResult********************");
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("In onActivityResult");
        if(resultCode == RESULT_OK){
                System.out.println("Getting new user from " + data + "looking for new sensor = " + data.hasExtra("New Sensor"));
                //System.out.println("Sensor = " + data.getSerializableExtra("New Sensor"));
                ClientConfig newSensorConfig = (ClientConfig) data.getSerializableExtra("New Sensor");
                System.out.println(newSensorConfig);
                //TODO: Send new SensorCOnfig to the background Thread or AsyncTask and add it to the sensor
                AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
                asyncTaskRunner.execute(newSensorConfig);
            }
    }

    private class AsyncTaskRunner extends AsyncTask<ClientConfig, Void, Integer> {
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Integer doInBackground(ClientConfig... params) {
            System.out.println("In Do in background");
            boolean check = server.addSensor(params[0]);
            if (check == true)
                return 1;
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                //TODO: See if you need to get the sensor manually and call uodateUI or does the server do it
                System.out.println("Debug Message: In Post execute");
                //mAdapter.notifyDataSetChanged();
            }
        }
    }
}
