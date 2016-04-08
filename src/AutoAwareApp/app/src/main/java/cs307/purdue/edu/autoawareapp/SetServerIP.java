package cs307.purdue.edu.autoawareapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Dhairya on 4/8/2016.
 */
public class SetServerIP extends DialogFragment implements View.OnClickListener {
    EditText ip_text;
    Button set_ip_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setip, container);
        ip_text = (EditText) view.findViewById(R.id.server_ip_text);
        set_ip_button = (Button) view.findViewById(R.id.set_ip_button);
        set_ip_button.setOnClickListener(this);

        getDialog().setTitle("Set Server IP");

        return view;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.set_ip_button:
                String ip = ip_text.getText().toString();
                //Intent mIntent = new Intent(this, SensorClient.class);
                //mIntent.putExtra("Server IP", ip);
                //startActivity(mIntent);
                break;
            default:
                break;
        }
    }
}
