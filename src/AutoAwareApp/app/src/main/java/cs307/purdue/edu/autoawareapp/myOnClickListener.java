package cs307.purdue.edu.autoawareapp;

import android.content.Context;
import android.view.View;

/**
 * Created by Dhairya on 4/6/2016.
 */
public class myOnClickListener implements View.OnClickListener {
    MyAdapter.ViewHolder viewHolder;
    int id;

    public myOnClickListener(MyAdapter.ViewHolder viewHolder, int id) {
        this.viewHolder = viewHolder;
        this.id = id;
    }


    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        System.out.println("GOT CLICKED!!!!!!!!!!!!!!!!!!");
        switch(view.getId()) {
            case R.id.button1:
                System.out.println("*****************************BUTTON 1");
                //openConfigActivity();
                break;
            case R.id.button2:
                System.out.println("*****************************BUTTON 2");
                //enableDisableCall();
                break;
            default:
                return;
        }
    }
}
