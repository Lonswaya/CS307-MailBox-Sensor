package cs307.purdue.edu.autoawareapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Dhairya on 4/8/2016.
 */
public class SetServerIPUI extends AppCompatActivity implements View.OnClickListener {
    EditText ip_text;
    Button set_ip_button;

    String username, password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setip);

        username = getIntent().getStringExtra("Username");
        password = getIntent().getStringExtra("Password");

        ip_text = (EditText) findViewById(R.id.server_ip_text);
        set_ip_button = (Button) findViewById(R.id.set_ip_button);
        set_ip_button.setOnClickListener(this);
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
                Intent mIntent = new Intent(this, SensorClientUI.class);
                mIntent.putExtra("Server IP", ip);
                mIntent.putExtra("Username", username);
                mIntent.putExtra("Password", password);
                startActivity(mIntent);
                break;
            default:
                break;
        }
    }
}
