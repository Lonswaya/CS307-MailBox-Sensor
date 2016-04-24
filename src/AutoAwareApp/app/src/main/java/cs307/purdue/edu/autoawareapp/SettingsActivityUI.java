package cs307.purdue.edu.autoawareapp;

import android.content.Intent;
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

public class SettingsActivityUI extends AppCompatActivity implements View.OnClickListener {
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
    Button applyButton, defaultButton;

    ClientConfig sensorInfo;
    String ip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sensorInfo = (ClientConfig) getIntent().getSerializableExtra("ClientConfig");
        System.out.println("*********************" + sensorInfo);

        String title = sensorInfo.name + " Settings";
        this.setTitle(title);

        ip = sensorInfo.ip;

        sensorName = (EditText) findViewById(R.id.set_name_text);
        sensorName.setHint(sensorInfo.name);

        setTextButton = (Button) findViewById(R.id.set_name_button);
        setTextButton.setOnClickListener(this);

        setTimeBox = (CheckBox) findViewById(R.id.timer_check_box);
        setTimeBox.setOnClickListener(this);

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
        setThresholdBar.setMax(100);
        int threshold = (int) sensorInfo.sensing_threshold;
        setThresholdBar.setProgress(threshold);

        desktopNotification = (CheckBox) findViewById(R.id.desktop_box);
        mobileNotification = (CheckBox) findViewById(R.id.text_box);
        emailNotification = (CheckBox) findViewById(R.id.email_box);
        appNotification = (CheckBox) findViewById(R.id.mobile_box);

        emailId = (EditText) findViewById(R.id.email_id);
        phoneNumber = (EditText) findViewById(R.id.mobile_number);

        if (sensorInfo.desktopNotification == true) {
            desktopNotification.toggle();
        }

        if (sensorInfo.emailNotification == true) {
            emailNotification.toggle();
            emailId.setText(sensorInfo.emailAddress);
        }

        if (sensorInfo.textNotification == true) {
            mobileNotification.toggle();
            phoneNumber.setText(sensorInfo.phoneNumber);
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
                String text = sensorName.getText().toString();
                sensorInfo.name = text;
                //TODO: Update server sensroInfoList
                this.setTitle(text + " Settings");
                break;
            case R.id.apply_button:
                ClientConfig clientConfig = getInfofromForm();
                Intent mIntent = new Intent(this, SensorClientUI.class);
                mIntent.putExtra("New Sensor", clientConfig);
                setResult(RESULT_OK, mIntent);
                System.out.println("********************************Returning back to parent activity, sensor = " + clientConfig);
                finish();
                break;
            case R.id.defualt_button:
                resetDefault();
                break;
            case R.id.timer_check_box:
                if (setTimeBox.isChecked() == false) {
                    startHour.setText("");
                    startHour.setHint("");
                    startMinute.setText("");
                    startMinute.setHint("");
                    endHour.setText("");
                    endHour.setHint("");
                    endMinute.setText("");
                    endMinute.setHint("");
                }
                else {
                    startHour.setHint(Integer.toString(sensorInfo.start_hours));
                    if (sensorInfo.start_minutes <= 9) {
                        startMinute.setHint("0" + Integer.toString(sensorInfo.start_minutes));
                    } else {
                        startMinute.setHint(Integer.toString(sensorInfo.start_minutes));
                    }
                    endHour.setHint(Integer.toString(sensorInfo.stop_hours));
                    if (sensorInfo.stop_minutes <= 9) {
                        endMinute.setHint("0" + Integer.toString(sensorInfo.stop_minutes));
                    } else {
                        endMinute.setHint(Integer.toString(sensorInfo.stop_minutes));
                    }
                }
        }
    }

    public ClientConfig getInfofromForm() {
        String name = sensorName.getText().toString();
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
        String emailString = emailId.getText().toString();

        return new ClientConfig(ip, startTime, endTime, force_on, force_off, sensorType, threshold, name, 0, 0, 0, desktop, magicMirror, phone, email, phoneString, emailString, 10);

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

        if (sensorInfo.magicMirrorNotification == true) {
            if(appNotification.isChecked() == false) {
                appNotification.toggle();
            }
        }
        else {
            if (appNotification.isChecked() == true) {
                appNotification.toggle();
            }
        }

        emailId.setText("");
        phoneNumber.setText("");

        setThresholdBar.setProgress((int) sensorInfo.sensing_threshold);

        switch (sensorInfo.sensor_type) {
                case LIGHT: typeLight.toggle();
                    break;
                case AUDIO: typeAudio.toggle();
                    break;
                case VIDEO: typeVideo.toggle();
                    break;
                default:
                    typeLight.toggle();
        }
    }
}
