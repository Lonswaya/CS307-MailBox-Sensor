package cs307.purdue.edu.autoawareapp;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dhairya on 4/2/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final ArrayList<Sensor> sensors;

    private static final int VIEW_HOLDER=1;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sensorNameTextView, sensorIpTextView;
        public ImageView sensorTypeImage;
        public ImageButton streamButton;
        public SeekBar seekBar;
        public Button configButton, enableDisbaleButton;


        public ViewHolder(View v) {
            super(v);

            System.out.println("HI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            this.sensorNameTextView = (TextView) v.findViewById(R.id.sensor_name);
            this.sensorIpTextView = (TextView) v.findViewById(R.id.sensor_ip);
            this.sensorTypeImage = (ImageView) v.findViewById(R.id.sensor_type_image);
            this.streamButton = (ImageButton) v.findViewById(R.id.rec_button);
            this.seekBar = (SeekBar) v.findViewById(R.id.current_val_bar);
            this.configButton = (Button) v.findViewById(R.id.button1);
            this.enableDisbaleButton = (Button) v.findViewById(R.id.button2);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, ArrayList<Sensor> sensors) {
        System.out.println("Constructor!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        this.context = context;
        this.sensors = sensors;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("OnCREATE VIEW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        System.out.println("onBind!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        ViewHolder viewholder = (ViewHolder) holder;

        TextView sensorNameView = (TextView) viewholder.sensorNameTextView;
        sensorNameView.setText(sensors.get(position).getName());

        TextView sensorIpView = (TextView) viewholder.sensorIpTextView;
        sensorIpView.setText(sensors.get(position).getIp());

        ImageView sensorTypeImageView = (ImageView) viewholder.sensorTypeImage;
        switch (sensors.get(position).getType()) {
            case "VIDEO": sensorTypeImageView.setImageResource(R.mipmap.ic_video_icon);
                break;
            case "AUDIO": sensorTypeImageView.setImageResource(R.mipmap.ic_sound_icon);
                break;
            case "LIGHT": sensorTypeImageView.setImageResource(R.mipmap.ic_bulb);
                break;
            default:
                break;
        }

        ImageButton streamButtonView = (ImageButton) viewholder.streamButton;
        streamButtonView.setImageResource(R.mipmap.ic_rec);
        streamButtonView.setVisibility(View.INVISIBLE);

        Button configButtonView = (Button) viewholder.configButton;
        configButtonView.setText("Configure");
        configButtonView.setOnClickListener(new myOnClickListener(viewholder, R.id.button1));

        Button enableDisableButtonView = (Button) viewholder.enableDisbaleButton;
        enableDisableButtonView.setText("Disable");
        enableDisableButtonView.setOnClickListener(new myOnClickListener(viewholder, R.id.button2));

        SeekBar seekBarView = (SeekBar) viewholder.seekBar;
        seekBarView.setProgress(sensors.get(position).getSeekCurrentValue());
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 1 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
       return VIEW_HOLDER;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sensors.size();
    }
}
