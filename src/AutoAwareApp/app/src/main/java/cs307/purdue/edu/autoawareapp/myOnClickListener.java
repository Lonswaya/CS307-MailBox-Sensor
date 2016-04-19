package cs307.purdue.edu.autoawareapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Dhairya on 4/6/2016.
 */
public class myOnClickListener implements View.OnClickListener {
    MyAdapter.ViewHolder viewHolder;
    private final Context context;
    int id;
    ClientConfig sensorInfo;
    Server server;

    public myOnClickListener() {
        context = null;
    }

    public myOnClickListener(MyAdapter.ViewHolder viewHolder, Context context, int id, ClientConfig sensorInfo, Server server) {
        this.viewHolder = viewHolder;
        this.context = context;
        this.id = id;
        this.sensorInfo = sensorInfo;
        this.server = server;
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
                System.out.println("*****************************BUTTON 1. Context = " + this + "    " + SettingsActivityUI.class);
                Intent myIntent = new Intent(context, SettingsActivityUI.class);
                myIntent.putExtra("ClientConfig", sensorInfo);
                //myIntent.putExtra("Server", server);
                System.out.println("Added server in. Context = " + context);
                context.startService(myIntent);
                System.out.println("sdjsadkshdsakdhaskjdhasjkdhasjdhasjdhasjdhasdasjdhaskjdhasdhasjkdhaskdha");
                break;
            case R.id.button2:
                System.out.println("*****************************BUTTON 2");
                //Intent mIntent2 = new Intent(context, SettingsActivityUI.class);
                //context.startActivity(mIntent2);
                sensorInfo.force_on = false;
                sensorInfo.force_off = true;
                AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
                asyncTaskRunner.execute(sensorInfo);
                break;
            case R.id.removebutton:
                System.out.println("*****************************Remove Button");
                //Intent mIntent2 = new Intent(context, SettingsActivityUI.class);
                //context.startActivity(mIntent2);
                AsyncTaskRunnerDelete asyncTaskRunnerDelete = new AsyncTaskRunnerDelete();
                asyncTaskRunnerDelete.execute(sensorInfo);
                break;
            default:
                return;
        }
    }

    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("In onActivityResult");
        if(resultCode == RESULT_OK){
            System.out.println("Getting newSensor Config from " + data + "looking for new sensor = " + data.hasExtra("New Sensor"));
            //System.out.println("Sensor = " + data.getSerializableExtra("New Sensor"));
            ClientConfig newSensorConfig = (ClientConfig) data.getSerializableExtra("New Sensor");
            System.out.println(newSensorConfig);
            //TODO: Send new SensorCOnfig to the background Thread or AsyncTask and add it to the sensor
            AsyncTaskRunnerUpdate asyncTaskRunnerUpdate = new AsyncTaskRunnerUpdate();
            asyncTaskRunnerUpdate.execute(newSensorConfig);
        }
    }*/

    private class AsyncTaskRunner extends AsyncTask<ClientConfig, Void, Integer> {
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Integer doInBackground(ClientConfig... params) {
            System.out.println("In Do in background");
            boolean check = server.updateSensor(sensorInfo);
            if (check == true)
                return 1;
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                //TODO: See if you need to get the sensor manually and call uodateUI or does the server do it
                System.out.println("Debug Message: In Post execute");
            }
        }
    }

    private class AsyncTaskRunnerDelete extends AsyncTask<ClientConfig, Void, Integer> {
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Integer doInBackground(ClientConfig... params) {
            System.out.println("In Remove Do in background");
            boolean check = server.deleteSensor(sensorInfo);
            if (check == true)
                return 1;
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                //TODO: See if you need to get the sensor manually and call uodateUI or does the server do it
                System.out.println("Debug Message: In Post execute");
            }
        }
    }

    private class AsyncTaskRunnerUpdate extends AsyncTask<ClientConfig, Void, Integer> {
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Integer doInBackground(ClientConfig... params) {
            System.out.println("In Remove Do in background");
            boolean check = server.updateSensor(sensorInfo);
            if (check == true)
                return 1;
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                //TODO: See if you need to get the sensor manually and call uodateUI or does the server do it
                System.out.println("Debug Message: In Post execute");
            }
        }
    }

}
