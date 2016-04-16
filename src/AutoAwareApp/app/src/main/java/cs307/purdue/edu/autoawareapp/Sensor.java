package cs307.purdue.edu.autoawareapp;

import android.content.Intent;
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

public class Sensor extends AppCompatActivity  {
    private String name;
    private String type;
    private int seekDefaultValue;
    private int seekCurrentValue;
    private String ip;

    private RelativeLayout sensorLayout;
    private TextView sensorName;
    private ImageButton playButton;
    private ImageView sensorTypeImage;
    private SeekBar currentValBar;
    private Button configButton;
    private Button enableDisableButton;

    public Sensor() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Sensor(String name, int seekDefaultValue, String type, String ip) {
        this.name = name;
        this.seekDefaultValue = seekDefaultValue;
        this.type = type;
        this.ip = ip;
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

    public int getSeekCurrentValue() {
        return this.seekCurrentValue;
    }

    public void setSeekCurrentValue (int seekCurrentValue) {
        this.seekCurrentValue = seekCurrentValue;
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
