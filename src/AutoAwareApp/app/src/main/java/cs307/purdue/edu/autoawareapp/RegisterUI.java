package cs307.purdue.edu.autoawareapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by Dhairya on 4/24/2016.
 */
public class RegisterUI extends AppCompatActivity {
    EditText nameTextView;
    EditText emailidTextView;
    EditText usernameTextView;
    EditText passwordTextView, repasswordTextView;
    EditText phoneNoTextView;

    CheckBox emailBox, textBox, desktopBox, appBox;

    Button signUpButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameTextView = (EditText) findViewById(R.id.set_name_text);
        emailidTextView = (EditText) findViewById(R.id.set_email_text);
        usernameTextView = (EditText) findViewById(R.id.username_text);
        passwordTextView = (EditText) findViewById(R.id.password_text);
        repasswordTextView = (EditText) findViewById(R.id.re_password_text);
        phoneNoTextView = (EditText) findViewById(R.id.phone_text);

        desktopBox = (CheckBox) findViewById(R.id.desktop_box);
        emailBox = (CheckBox) findViewById(R.id.email_box);
        textBox = (CheckBox) findViewById(R.id.text_box);
        appBox = (CheckBox) findViewById(R.id.app_box);

        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Implement this listener
            }
        });
    }
}
