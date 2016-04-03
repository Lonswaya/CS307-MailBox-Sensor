package cs307.purdue.edu.autoawareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class Sensor extends AppCompatActivity {
    private String name;
    private String type;
    private int seekDefaultValue;

    private RelativeLayout sensorLayout;
    private TextView sensorName;
    private ImageButton playButton;
    private ImageView sensorTypeImage;
    private SeekBar currentValBar;
    private Button configButton;
    private Button enableDisableButton;

    public Sensor() {
    }

    public Sensor(String name, int seekDefaultValue, String type) {
        this.name = name;
        this.seekDefaultValue = seekDefaultValue;
        this.type = type;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor);

        sensorLayout = (RelativeLayout) findViewById(R.id.sensor_info_form);
        sensorName = (TextView) findViewById(R.id.sensor_name);
        playButton = (ImageButton) findViewById(R.id.rec_button);
        sensorTypeImage = (ImageView) findViewById(R.id.sensor_type_image);
        currentValBar = (SeekBar) findViewById(R.id.current_val_bar);
        configButton = (Button) findViewById(R.id.button1);
        enableDisableButton = (Button) findViewById(R.id.button2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSeekDefaultValue() {
        return seekDefaultValue;
    }

    public void setSeekDefaultValue(int seekDefaultValue) {
        this.seekDefaultValue = seekDefaultValue;
    }

    public RelativeLayout getSensorLayout() {
        return sensorLayout;
    }

    public void setSensorLayout(RelativeLayout sensorLayout) {
        this.sensorLayout = sensorLayout;
    }

    public ImageButton getPlayButton() {
        return playButton;
    }

    public void setPlayButton(ImageButton playButton) {
        this.playButton = playButton;
    }

    public TextView getSensorName() {
        return sensorName;
    }

    public void setSensorName(TextView sensorName) {
        this.sensorName = sensorName;
    }

    public ImageView getSensorTypeImage() {
        return sensorTypeImage;
    }

    public void setSensorTypeImage(int sensorTypeImage) {
        this.sensorTypeImage.setImageResource(sensorTypeImage);
    }

    public SeekBar getCurrentValBar() {
        return currentValBar;
    }

    public void setCurrentValBar(SeekBar currentValBar) {
        this.currentValBar = currentValBar;
    }

    public Button getConfigButton() {
        return configButton;
    }

    public void setConfigButton(Button configButton) {
        this.configButton = configButton;
    }

    public Button getEnableDisableButton() {
        return enableDisableButton;
    }

    public void setEnableDisableButton(Button enableDisableButton) {
        this.enableDisableButton = enableDisableButton;
    }

    public Sensor convertSensorInfotoSensor(SensorInfo s) {
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

        newSensor.setSeekDefaultValue(0);
        newSensor.playButton.setVisibility(View.INVISIBLE);

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
}
