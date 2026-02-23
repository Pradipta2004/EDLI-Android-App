package com.hitech.levelstate;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

public class channelAdapter extends ArrayAdapter<channelModel> {

    CardView cardview;
    private Context context;
    CheckBox chkBox;
    private List<Boolean> switchStates;
    private OnItemClickListener onItemClickListener;
    public channelAdapter(@NonNull Context context, ArrayList<channelModel> courseModelArrayList, List<Boolean> switchStates) {
        super(context, 0, courseModelArrayList);

        this.context = context;
        this.switchStates = switchStates;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onWaterFlagClick(int position);

        void onEnergisedFlagClick(int position);

        void onMinusButtonClick(int position);

        void onPlusButtonClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.channel_item, parent, false);
        }

        channelModel chnModel = getItem(position);

        cardview = listitemView.findViewById(R.id.card_view);
        TextView channelName = listitemView.findViewById(R.id.textView_channelName);
        Switch swActive = listitemView.findViewById(R.id.switch_active_mrk);
        Switch swEnergised = listitemView.findViewById(R.id.switch_energised_mrk);
        Switch swWater = listitemView.findViewById(R.id.switch_water_mrk);
        TextView delayTimeTxt = listitemView.findViewById(R.id.textViewDelay);

        Button btnMinus = listitemView.findViewById(R.id.buttonMinus);
        Button btnPlus = listitemView.findViewById(R.id.buttonPlus);


        delayTimeTxt.setText(""+chnModel.getDelayTime());
        channelName.setText(chnModel.getChannel_name());
        swActive.setChecked(chnModel.getChannelActive());
        swEnergised.setChecked(chnModel.getCahnnelEnergised());
        swWater.setChecked(chnModel.getCahnnelWaterSteam());

        if(swActive.isChecked())
        {
            cardview.setBackgroundColor(Color.parseColor("#152B38"));
            swEnergised.setEnabled(true);
            btnMinus.setEnabled(true);
            btnPlus.setEnabled(true);
            swWater.setEnabled(true);
            delayTimeTxt.setEnabled(true);
        }
        else
        {
            cardview.setBackgroundColor(Color.LTGRAY);
            swEnergised.setEnabled(false);
            btnMinus.setEnabled(false);
            btnPlus.setEnabled(false);
            swWater.setEnabled(false);
            delayTimeTxt.setEnabled(false);
        }


        if(swEnergised.isChecked())
            swEnergised.setText("Energised");
        else
            swEnergised.setText("De-Energised");


        if(swWater.isChecked())
        {
            swWater.setText("Water");
        }
        else
            swWater.setText("Steam");

        swWater.setChecked(switchStates.get(position));


        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onItemClickListener != null) {
                    onItemClickListener.onMinusButtonClick(position);
                }
                
                /*String tmp = delayTimeTxt.getText().toString().substring(1,2);
                int x =0;
                if(tmp.equals(" "))
                {
                    x = Integer.parseInt(delayTimeTxt.getText().toString().substring(0,1));
                }
                else
                {
                    x = Integer.parseInt(delayTimeTxt.getText().toString().substring(0,2));
                }
                if(x>1)
                {
                    x--;
                    delayTimeTxt.setText(x + " sec");
                    btnPlus.setEnabled(true);
                }
                else
                {
                    btnMinus.setEnabled(false);
                }
*/
            }

                
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onPlusButtonClick(position);
                }
            }
        });

/*
        swActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
*/

        swActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if(swActive.isChecked()==true)
                {
                    cardview.setCardBackgroundColor(Color.WHITE);
                    swWater.setActivated(true);
                    swEnergised.setActivated(true);
                    delayTimeTxt.setActivated(true);
                }
                else
                {
                    cardview.setCardBackgroundColor(Color.LTGRAY);
                    swWater.setActivated(false);
                    swEnergised.setActivated(false);
                    delayTimeTxt.setActivated(false);
                }
                */
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }

            }
        });

        
        swWater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (onItemClickListener != null) {
                    onItemClickListener.onWaterFlagClick(position);
                }
            }
        });

        swEnergised.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (onItemClickListener != null) {
                    onItemClickListener.onEnergisedFlagClick(position);
                }
            }
        });
        
/*
        swWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(swWater.isChecked()==true)
                {
                    swWater.setText("Water");
                    //delayTimeTxt.setEnabled(true);
                }
                else
                {
                    swWater.setText("Steam");
                    //delayTimeTxt.setEnabled(false);
                }
            }
        });

        swEnergised.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(swEnergised.isChecked()==true)
                {
                    swEnergised.setText("Energised");
                    btnPlus.setEnabled(true);
                    btnMinus.setEnabled(true);
                    delayTimeTxt.setText("1 sec");
                    delayTimeTxt.setBackgroundColor(Color.parseColor("#FFB300"));
                }
                else
                {
                    swEnergised.setText("De-Energised");
                    btnPlus.setEnabled(false);
                    btnMinus.setEnabled(false);
                    delayTimeTxt.setText("");
                    delayTimeTxt.setBackgroundColor(Color.LTGRAY);
                }

            }
        });
*/
        return listitemView;
    }
}