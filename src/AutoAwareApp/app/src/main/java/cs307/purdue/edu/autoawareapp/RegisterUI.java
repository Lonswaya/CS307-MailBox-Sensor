package cs307.purdue.edu.autoawareapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by Dhairya on 4/24/2016.
 */
public class RegisterUI extends AppCompatActivity implements View.OnClickListener {
    EditText nameTextView;
    EditText emailidTextView;
    EditText usernameTextView;
    EditText passwordTextView, repasswordTextView;
    EditText phoneNoTextView;

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

        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(this);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_button:
                getInfofromForm();
                System.out.println("Debug Message: Did not finish");
                break;
            default:
                break;
        }
    }

    //TODO: Change return type to USER
    public void getInfofromForm() {
        String name, emailid, username, password, repassword, phoneNo;

        name = nameTextView.getText().toString();
        emailid = emailidTextView.getText().toString();
        username = usernameTextView.getText().toString();
        password = passwordTextView.getText().toString();
        repassword = repasswordTextView.getText().toString();
        phoneNo = phoneNoTextView.getText().toString();

        //TODO: Create New User
        if (password.equals(repassword)) {
            Intent mIntent = new Intent(this, SensorClientUI.class);
            mIntent.putExtra("Username", username);
            mIntent.putExtra("Password", password);
            setResult(RESULT_OK, mIntent);
            finish();
        }
    }
}

