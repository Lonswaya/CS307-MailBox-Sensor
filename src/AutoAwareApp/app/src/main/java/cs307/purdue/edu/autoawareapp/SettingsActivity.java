package cs307.purdue.edu.autoawareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import java.io.Serializable;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    EditText sensorName;
    Button setTextButton;
    CheckBox setTimeBox;
    EditText startHour, startMinute;
    EditText endHour, endMinute;
    RadioGroup sensorTypeGroup;
    RadioButton typeLight, typeAudio, typeVideo;
    SeekBar setThresholdBar;
    CheckBox desktopNotification, mobileNotification, emailNotification, appNotification;
    Button applyButton, defaultButton;

    SensorInfo sensorInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //TODO: Set all the parameters

        sensorInfo = (SensorInfo) getIntent().getSerializableExtra("SensorInfo");
        System.out.println("*********************" + sensorInfo);

        sensorName = (EditText) findViewById(R.id.set_name_text);
        sensorName.setHint(sensorInfo.name);

        setTextButton = (Button) findViewById(R.id.set_name_button);

        setTimeBox = (CheckBox) findViewById(R.id.timer_check_box);

        startHour = (EditText) findViewById(R.id.start_time_hour);
        startMinute = (EditText) findViewById(R.id.start_time_minute);
        startHour.setHint("");
        startMinute.setHint("");

        endHour = (EditText) findViewById(R.id.end_time_hour);
        endMinute = (EditText) findViewById(R.id.end_time_minute);
        endHour.setHint("");
        endMinute.setHint("");

        sensorTypeGroup = (RadioGroup) findViewById(R.id.sensor_type_group);
        typeLight = (RadioButton) findViewById(R.id.light_sensor_button);
        typeAudio = (RadioButton) findViewById(R.id.sound_sensor_button);
        typeVideo = (RadioButton) findViewById(R.id.video_sensor_button);

        switch(sensorInfo.sensor_type) {
            case LIGHT: typeLight.toggle();
                break;
            case AUDIO: typeAudio.toggle();
                break;
            case VIDEO: typeVideo.toggle();
                break;
            default:
                typeLight.toggle();
        }


        setThresholdBar = (SeekBar) findViewById(R.id.set_threshold_bar);
        setThresholdBar.setProgress((int) sensorInfo.sensing_threshold);

        desktopNotification = (CheckBox) findViewById(R.id.desktop_box);
        mobileNotification = (CheckBox) findViewById(R.id.text_box);
        emailNotification = (CheckBox) findViewById(R.id.email_box);
        appNotification = (CheckBox) findViewById(R.id.mobile_box);

        if (sensorInfo.desktopNotification == true) {
            desktopNotification.toggle();
        }

        if (sensorInfo.emailNotification == true) {
            emailNotification.toggle();
        }

        if (sensorInfo.textNotification == true) {
            mobileNotification.toggle();
        }

        applyButton = (Button) findViewById(R.id.apply_button);
        applyButton.setOnClickListener(this);

        defaultButton = (Button) findViewById(R.id.defualt_button);
        defaultButton.setOnClickListener(this);
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
            case R.id.set_name_button:
                break;
            case R.id.apply_button:
                break;
            case R.id.defualt_button:
                resetDefault();
                break;
        }
    }

    public void resetDefault() {
        sensorName.setText("");
        sensorName.setHint(sensorInfo.name);

        setThresholdBar.setProgress((int) sensorInfo.sensing_threshold);

        startHour.setText("");
        startMinute.setText("");

        endHour.setText("");
        endMinute.setText("");

        if (sensorInfo.desktopNotification == true) {
            if(desktopNotification.isChecked() == false) {
                desktopNotification.toggle();
            }
        }
        else {
            if (desktopNotification.isChecked() == true) {
                desktopNotification.toggle();
            }
        }

        if (sensorInfo.textNotification == true) {
            if(mobileNotification.isChecked() == false) {
                mobileNotification.toggle();
            }
        }
        else {
            if (mobileNotification.isChecked() == true) {
                mobileNotification.toggle();
            }
        }

        if (sensorInfo.emailNotification == true) {
            if(emailNotification.isChecked() == false) {
                emailNotification.toggle();
            }
        }
        else {
            if (emailNotification.isChecked() == true) {
                emailNotification.toggle();
            }
        }

    }
}
