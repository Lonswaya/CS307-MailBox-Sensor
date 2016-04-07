package cs307.purdue.edu.autoawareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import java.io.Serializable;

public class SettingsActivity extends AppCompatActivity {
    EditText sensorName;
    Button setTextButton;
    CheckBox setTimeBox;
    EditText startHour, startMinute;
    EditText endHour, endMinute;
    RadioGroup sensorTypeGroup;
    RadioButton typeLight, typeAudio, typeVideo;
    SeekBar setThresholdBar;
    CheckBox desktopNotification, mobileNotification, emailNotification, appNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //TODO: Set all the parameters

        SensorInfo sensorInfo = (SensorInfo) getIntent().getSerializableExtra("SensorInfo");
        System.out.println("*********************" + sensorInfo);

        sensorName = (EditText) findViewById(R.id.set_name_text);

        setTextButton = (Button) findViewById(R.id.set_name_button);

        setTimeBox = (CheckBox) findViewById(R.id.timer_check_box);

        startHour = (EditText) findViewById(R.id.start_time_hour);
        startMinute = (EditText) findViewById(R.id.start_time_minute);

        endHour = (EditText) findViewById(R.id.end_time_hour);
        endMinute = (EditText) findViewById(R.id.end_time_minute);

        sensorTypeGroup = (RadioGroup) findViewById(R.id.sensor_type_group);
        typeLight = (RadioButton) findViewById(R.id.light_sensor_button);
        typeAudio = (RadioButton) findViewById(R.id.sound_sensor_button);
        typeVideo = (RadioButton) findViewById(R.id.video_sensor_button);

        setThresholdBar = (SeekBar) findViewById(R.id.set_threshold_bar);

        desktopNotification = (CheckBox) findViewById(R.id.desktop_box);
        mobileNotification = (CheckBox) findViewById(R.id.text_box);
        emailNotification = (CheckBox) findViewById(R.id.email_box);
        appNotification = (CheckBox) findViewById(R.id.mobile_box);
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
}
