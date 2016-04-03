package cs307.purdue.edu.autoawareapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Dhairya M. Doshi on 4/1/2016.
 */
public class SensorClient extends AppCompatActivity {
    RecyclerView sensorList;
    RecyclerView.Adapter mAdapter = null;
    ArrayList<Sensor> sensors = null;
    int in_index = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // messages = new ArrayList<String>();
        sensors = new ArrayList<Sensor>();

        // mAdapter = new ArrayAdapter<String>(this, R.layout.mymessage, R.id.mymessageTextView, messages);
        mAdapter = new MyAdapter(this, sensors);

        sensorList = (RecyclerView) findViewById(R.id.messageList);
        sensorList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        sensorList.setLayoutManager(llm);
        sensorList.setAdapter(mAdapter);

        Intent in = getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

//    @Override
//    public void onClick(View view) {
//
//        switch (view.getId()) {
//            case R.id.sendButton:
//
//                String messString = messageText.getText().toString();
//                if (!messString.equals("")) {
//                    Message message = new Message("", messString, true, new Date());
//                    messages.add(message);
//                    messageList.scrollToPosition(messages.size() - 1);
//                    mAdapter.notifyDataSetChanged();
//                    sendMessage();
//                    message = null;
//                    messageText.setText("");
//
//                }
//
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    private void sendMessage() {
//
//        String[] incoming = {"Hey, How's it going?",
//                "Super! Let's do lunch tomorrow",
//                "How about Mexican?",
//                "Great, I found this new place around the corner",
//                "Ok, see you at 12 then!"};
//
//        if (in_index < incoming.length) {
//            Message message = new Message("John", incoming[in_index], false,  new Date());
//            messages.add(message);
//            in_index++;
//            messageList.scrollToPosition(messages.size()-1);
//            mAdapter.notifyDataSetChanged();
//        }
}
