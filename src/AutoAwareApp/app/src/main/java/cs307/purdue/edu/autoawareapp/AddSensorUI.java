package cs307.purdue.edu.autoawareapp;

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
    Button setTextButton;
    CheckBox setTimeBox;
    EditText startHour, startMinute;
    EditText endHour, endMinute;
    RadioGroup sensorTypeGroup;
    RadioButton typeLight, typeAudio, typeVideo;
    SeekBar setThresholdBar;
    CheckBox desktopNotification, mobileNotification, emailNotification, appNotification;
    EditText emailId, phoneNumber;
    Button addButton, defaultButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);

        String title = getIntent().getStringExtra("Title");
        this.setTitle("Add New Sensor");

        sensorName = (EditText) findViewById(R.id.set_name_text);

        setTimeBox = (CheckBox) findViewById(R.id.timer_check_box);
        setTimeBox.setOnClickListener(this);

        startHour = (EditText) findViewById(R.id.start_time_hour);
        startMinute = (EditText) findViewById(R.id.start_time_minute);

        endHour = (EditText) findViewById(R.id.end_time_hour);
        endMinute = (EditText) findViewById(R.id.end_time_minute);

        sensorTypeGroup = (RadioGroup) findViewById(R.id.sensor_type_group);
        typeLight = (RadioButton) findViewById(R.id.light_sensor_button);
        typeAudio = (RadioButton) findViewById(R.id.sound_sensor_button);
        typeVideo = (RadioButton) findViewById(R.id.video_sensor_button);


        setThresholdBar = (SeekBar) findViewById(R.id.set_threshold_bar);
        setThresholdBar.setMax(100);

        desktopNotification = (CheckBox) findViewById(R.id.desktop_box);
        mobileNotification = (CheckBox) findViewById(R.id.text_box);
        emailNotification = (CheckBox) findViewById(R.id.email_box);
        appNotification = (CheckBox) findViewById(R.id.mobile_box);

        emailId = (EditText) findViewById(R.id.email_id);
        phoneNumber = (EditText) findViewById(R.id.mobile_number);

        addButton = (Button) findViewById(R.id.confirm_add_button);
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
            case R.id.confirm_add_button:
                break;
            default:
                break;
        }
    }

    public void resetDefault() {
        sensorName.setText("");
        sensorName.setHint("Sensor Name");

        startHour.setText("");
        startMinute.setText("");

        endHour.setText("");
        endMinute.setText("");

        if(desktopNotification.isChecked() == false) {
                desktopNotification.toggle();
        }

        if(mobileNotification.isChecked() == false) {
                mobileNotification.toggle();
        }

        if(emailNotification.isChecked() == false) {
            emailNotification.toggle();
        }

        if(appNotification.isChecked() == false) {
            appNotification.toggle();
        }

        emailId.setText("");
        phoneNumber.setText("");

        typeLight.toggle();
    }
}
