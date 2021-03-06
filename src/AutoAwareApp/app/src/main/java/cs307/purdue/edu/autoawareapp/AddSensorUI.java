package cs307.purdue.edu.autoawareapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

/**
 * Created by Dhairya on 4/8/2016.
 */
public class AddSensorUI extends AppCompatActivity implements View.OnClickListener {
    EditText sensorName;
    EditText ip;
    CheckBox setTimeBox;
    EditText startHour;
    EditText startMinute;
    EditText endHour, endMinute;
    RadioGroup sensorTypeGroup;
    RadioButton typeLight, typeAudio, typeVideo;
    SeekBar setThresholdBar;
    CheckBox desktopNotification, mobileNotification, emailNotification, appNotification;
    EditText emailId, phoneNumber;
    Button addButton;

    //Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);

        String title = getIntent().getStringExtra("Title");
        this.setTitle("Add New Sensor");

        //server = (Server) getIntent().getSerializableExtra("Server");

        sensorName = (EditText) findViewById(R.id.add_set_name_text);
        ip =(EditText) findViewById(R.id.add_set_ip_text);


        setTimeBox = (CheckBox) findViewById(R.id.add_timer_check_box);
        setTimeBox.setOnClickListener(this);

        startHour = (EditText) findViewById(R.id.add_start_time_hour);

        startMinute = (EditText) findViewById(R.id.add_start_time_minute);

        endHour = (EditText) findViewById(R.id.add_end_time_hour);
        endMinute = (EditText) findViewById(R.id.add_end_time_minute);

        sensorTypeGroup = (RadioGroup) findViewById(R.id.add_sensor_type_group);
        typeLight = (RadioButton) findViewById(R.id.add_light_sensor_button);
        typeAudio = (RadioButton) findViewById(R.id.add_sound_sensor_button);
        typeVideo = (RadioButton) findViewById(R.id.add_video_sensor_button);


        setThresholdBar = (SeekBar) findViewById(R.id.add_set_threshold_bar);
        setThresholdBar.setMax(100);

        desktopNotification = (CheckBox) findViewById(R.id.add_desktop_box);
        mobileNotification = (CheckBox) findViewById(R.id.add_text_box);
        emailNotification = (CheckBox) findViewById(R.id.add_email_box);
        appNotification = (CheckBox) findViewById(R.id.add_mobile_box);

        emailId = (EditText) findViewById(R.id.add_email_id);
        phoneNumber = (EditText) findViewById(R.id.add_mobile_number);

        addButton = (Button) findViewById(R.id.add_confirm_add_button);
        addButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_confirm_add_button:
                ClientConfig newSensorConfig = getInfofromForm();
                System.out.println("Debug Message: Got add button");
                if (newSensorConfig == null) {
                    System.out.println("Debug Message: Got null");
                    clearForm();
                    break;
                }
                //TODO: ADD sensor to the server
                //server.addSensor(newSensor);
                Intent mIntent = new Intent(this, SensorClientUI.class);
                mIntent.putExtra("New Sensor", newSensorConfig);
                setResult(RESULT_OK, mIntent);
                System.out.println("Returning back to parent activity, sensor = "); // + newSensorConfig);
                finish();
                break;
            default:
                break;
        }
    }

    public void clearForm() {
        sensorName.setText("");
        ip.setText("");

        if (setTimeBox.isChecked())
            setTimeBox.toggle();

        startHour.setText("");
        startMinute.setText("");
        endHour.setText("");
        endMinute.setText("");

        typeLight.toggle();

        setThresholdBar.setProgress(0);

        if (desktopNotification.isChecked())
            desktopNotification.toggle();
        if (emailNotification.isChecked())
            emailNotification.toggle();
        if (mobileNotification.isChecked())
            mobileNotification.toggle();
        if (appNotification.isChecked())
            appNotification.toggle();

        phoneNumber.setText("");
        emailId.setText("");

    }

    public ClientConfig getInfofromForm() {
        String name = sensorName.getText().toString();
        String ipString = ip.getText().toString();
//        for (int i = 0; i < ipString.length(); i++) {
//            char c = ipString.charAt(i);
//            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
//                ip.setText("");
//                return null;
//            }
//        }
        String strtHour, strtMinute, edHour, edMinute;
        String endTime = null;
        String startTime = null;
        boolean force_on = false;
        boolean force_off = false;
        SensorType sensorType = SensorType.LIGHT;

        if (setTimeBox.isChecked()) {
            strtHour = startHour.getText().toString();
            strtMinute = startMinute.getText().toString();
            startTime = strtHour + ":" + strtMinute;
            if (startTime == null) {
                startTime = "0:00";
            }

            edHour = endHour.getText().toString();
            edMinute = endMinute.getText().toString();
            endTime = edHour + ":" + edMinute;
            if (endTime == null) {
                endTime = "0:00";
            }

            if (startTime != null) {
                force_on = true;
            }
            if (endTime != null) {
                force_off = true;
            }
        }

        if (typeLight.isChecked())
            sensorType = SensorType.LIGHT;
        else if (typeAudio.isChecked())
            sensorType = SensorType.AUDIO;
        else if (typeVideo.isChecked())
            sensorType = SensorType.VIDEO;

        float threshold = setThresholdBar.getProgress();

        boolean desktop = false;
        boolean phone = false;
        boolean email = false;
        boolean magicMirror = false;
        if (desktopNotification.isChecked())
            desktop = true;
        if (mobileNotification.isChecked())
            phone = true;
        if (emailNotification.isChecked())
            email = true;
        if (appNotification.isChecked())
            magicMirror = true;

        String phoneString = phoneNumber.getText().toString();
//        for (int i = 0; i < phoneString.length(); i++) {
//            char c = phoneString.charAt(i);
//            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
//                phoneNumber.setText("");
//                return null;
//            }
//        }
        String emailString = emailId.getText().toString();
//        if (emailString.contains("@") == false)
//            return null;

        return new ClientConfig(ipString, startTime, endTime, force_on, force_off, sensorType, threshold, name, 0, 0, 0, desktop, magicMirror, phone, email, phoneString, emailString, 20, 10);

    }
}
