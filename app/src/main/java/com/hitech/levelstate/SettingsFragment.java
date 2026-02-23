package com.hitech.levelstate;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean[] waterSteamFlag = new boolean[10];
    private boolean[] energisedDeenergisedFlag = new boolean[10];
    private boolean[] channelSta = new boolean[10];
    private int[] trippingDelay = new int[10];
    private boolean[] waterSteamFlag_new = new boolean[50];
    private boolean[] EnergisedDeenergisedFlag_new = new boolean[50];
    private boolean[] channelSta_new = new boolean[50];
    private int[] tripRelayNo = new int[50];
    private int[] trippingDelay_new = new int[50];
    String[] tmp_relayno = new String[50];
    String[] tmp_channel_sta = new String[50];
    String[] tmp_energisedDeenergised = new String[50];
    String[] tmp_waterSteam = new String[50];
    String[] tmp_tripDelay = new String[50];
    String received_data_str="";
    ProgressDialog progressDialog;
    private static RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    Button btnSave;
    GridView channelGV;
    private ChannelGridViewAdapter gridViewAdapter;
    private List<String> dataList;
    private static RecyclerView.Adapter RVadapter;
    int myString;
    private static final int MAX_RETRIES = 6; // Maximum number of retry attempts
    private static final int RETRY_DELAY_MS = 2000; // Delay between retries in milliseconds
    private List<Boolean> switchStates;
    MainActivity mainActivity;
    int no_of_channels=0;
    int total_no_of_channels;
    String[] data_str_new = new String[100];
    TextView headingText,textViewStatus;
    Context mContext;
    private Handler handler = new Handler();
    channelAdapter adapter;
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint({"MissingInflatedId", "SuspiciousIndentation"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            myString = mainActivity.getMyString();
        }
        View view = inflater.inflate(R.layout.fragment_settings,container, false);
        mContext = container.getContext();


        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        headingText = view.findViewById(R.id.textViewFragmentHeading);
        ArrayList<channelModel> channelModelArrayList = new ArrayList<channelModel>();
        textViewStatus = view.findViewById(R.id.textViewStatus);
        btnSave = view.findViewById(R.id.buttonSave);
        btnSave.setEnabled(false);
        //CardView cardView = view.findViewById(R.id.card_view);
        switchStates = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            switchStates.add(true);
        }

//        mainActivity = (MainActivity) getActivity();
 //       mainActivity.received_data_string ="";

        delayRoutine(500);
       // channelAdapter adapter = new channelAdapter(mContext, channelModelArrayList,switchStates);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                if (myString == 0) {
                    savingData();
                } else {
                   savingData_new();
                }
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();

        // Set a listener to be notified when the layout is finished loading
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // This method will be called when the layout is finished loading
                textViewStatus.setText("Getting details ...");
                // Start a new thread or perform other tasks here
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //get Water/Energy Flags & delay time
                        if(myString==0) {

                            if (read_operation("0014", "000E")) {
                                int processFlag = process_command_flagsAndDelay(mainActivity.received_data_string);
                                while (processFlag == 0) {
                                    read_operation("0014", "000E");
                                    delayRoutine(100);
                                    processFlag = process_command_flagsAndDelay(mainActivity.received_data_string);
                                }
                            }


                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewStatus.setText("Got details");
                                }
                            });

                            delayRoutine(100);
                            //  textViewStatus.setText("Got details ...");
                            //get no of channels
                            if (read_operation("0043", "0001")) {
                                no_of_channels = get_no_of_channels(mainActivity.received_data_string);
                                while (no_of_channels == 0) {
                                    read_operation("0043", "0001");
                                    delayRoutine(100);
                                    no_of_channels = get_no_of_channels(mainActivity.received_data_string);
                                }
                            }

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewStatus.setText("Got Chn Nos");
                                }
                            });

                            delayRoutine(100);

                            //get channel status

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewStatus.setText("Reading chn status");
                                }
                            });

                            while (!read_operation("0047", "0004")) {
                            }


                            int c_status = 0;
                            int retry_cnt = 0;
                            c_status = get_channel_sta(mainActivity.received_data_string);


                            while (c_status == 0) {
                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textViewStatus.setText("Reading chn sta 2");
                                    }
                                });

                                while (!read_operation("0047", "0004")) {
                                }
                                retry_cnt++;
                                c_status = get_channel_sta(mainActivity.received_data_string);
                                delayRoutine(200);
                                int finalRetry_cnt = retry_cnt;
                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textViewStatus.setText("Retrying ..." + finalRetry_cnt);
                                    }
                                });
                            }
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewStatus.setText("Got chn status");
                                }
                            });


                            // channelModelArrayList.add(new channelModel("Channel", 1, false, false, false));

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewStatus.setText("");
                                }
                            });
                        }
                        if (myString==1) {
                            mainActivity = (MainActivity) getActivity();
                            if(mainActivity!=null) {
                                mainActivity.PrepareReadCommandNew("0001");
                            }
                            int retryCount = 0;
                            boolean success = false;
                            while (retryCount < MAX_RETRIES && !success) {
                                try {
                                    mainActivity.PrepareReadCommandNew("0001");
                                    int ChannelStat_received_data = process_store_Channel_data(mainActivity.received_data_string_new);
                                    // Handle the received data
                                    if(ChannelStat_received_data==1){
                                        success = true;
                                        // Data received successfully
                                        mainActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                textViewStatus.setText("Got details");
                                            }
                                        });
                                        mainActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                textViewStatus.setText("");
                                            }
                                        });
                                    } else {
                                        retryCount++;
                                        mainActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                textViewStatus.setText("Retrying ...");
                                            }
                                        });
                                        if (retryCount < MAX_RETRIES) {
                                            // Wait before retrying
                                            Thread.sleep(RETRY_DELAY_MS);
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    retryCount++;
                                    if (retryCount < MAX_RETRIES) {
                                        try {
                                            // Wait before retrying
                                            Thread.sleep(RETRY_DELAY_MS);
                                        } catch (InterruptedException ie) {
                                            ie.printStackTrace();
                                        }
                                    }
                                }
                            }

                            if (!success) {
                                // Handle the case where data could not be received after retries
                                System.out.println("Failed to receive data after " + MAX_RETRIES + " attempts.");
                            }
                            for(int i=0; i<total_no_of_channels;i++){
                                tmp_relayno[i] = data_str_new[i+14].substring(2,4);
                            }
                            for(int i=0; i<total_no_of_channels;i++){
                                tmp_channel_sta[i] = data_str_new[i+14].substring(0,1);
                            }
                            for(int i=0; i<total_no_of_channels;i++){
                                tmp_energisedDeenergised[i] = data_str_new[i+63].substring(0,1);
                            }
                            for(int i=0; i<total_no_of_channels;i++){
                                tmp_tripDelay[i] = data_str_new[i+63].substring(1,4);
                            }
                            for(int i=0; i<total_no_of_channels;i++){
                                tmp_waterSteam[i] = data_str_new[i+14].substring(1,2);
                            }


                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (myString == 0) {
                                    populate_channels(view);
                                    btnSave.setEnabled(true);
                                    //cardContainer.invalidate();
                                }
                                else{
                                    populate_channels_new(view);
                                    btnSave.setEnabled(true);
                                }
                            }
                        });
                    }
                });
                thread.start();

                // Remove the listener to avoid duplicate calls (optional)
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });




    }

    private void populate_channels_new(View view) {
        LinearLayout cardContainer = view.findViewById(R.id.cardContainer);
        for (int i = 1; i <=total_no_of_channels; i++) {
            CardView cardView = new CardView(requireContext());

            cardView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            cardView.setCardElevation(8);
            cardView.setContentPadding(16, 1, 16, 1);
            View cardContent = getLayoutInflater().inflate(R.layout.channel_item_new, null);

            // Find ToggleButton and Buttons within the card's content
            CardView cv = cardContent.findViewById(R.id.card_view_new);
            Switch toggleButton = cardContent.findViewById(R.id.switch_active_mrk_new);
            Button buttonPlus = cardContent.findViewById(R.id.buttonPlus_new);
            Button buttonMinus = cardContent.findViewById(R.id.buttonMinus_new);
            Button buttonPlus_relay = cardContent.findViewById(R.id.buttonPlus_Relay);
            Button buttonMinus_relay = cardContent.findViewById(R.id.buttonMinus_Relay);
            TextView txtDelay = cardContent.findViewById(R.id.textViewDelay_new);
            TextView chnNo = cardContent.findViewById(R.id.textView_channelNumber);
            Switch waterFlag = cardContent.findViewById(R.id.switch_water_mrk_new);
            Switch energisedFlag = cardContent.findViewById(R.id.switch_energised_mrk_new);
            TextView relayno = cardContent.findViewById(R.id.textViewRelayno);

            chnNo.setTag(i);
            chnNo.setText("Channel No"+i);
            toggleButton.setTag(i);

            if(channelSta_new[i-1])
            {
                cv.setBackgroundColor(Color.parseColor("#152B38"));
                toggleButton.setChecked(true);
                energisedFlag.setEnabled(true);
                waterFlag.setEnabled(true);
                buttonMinus.setEnabled(true);
                buttonPlus.setEnabled(true);
                buttonPlus_relay.setEnabled(true);
                buttonMinus_relay.setEnabled(true);
                txtDelay.setEnabled(true);
                txtDelay.setText(trippingDelay_new[i-1] + " sec");
                relayno.setEnabled(true);
                relayno.setText("" + tripRelayNo[i-1]);
            }
            else
            {
                toggleButton.setChecked(false);
                cv.setBackgroundColor(Color.LTGRAY); // Replace with the desired color
                energisedFlag.setEnabled(false);
                waterFlag.setEnabled(false);
                buttonMinus.setEnabled(false);
                buttonPlus.setEnabled(false);
                txtDelay.setEnabled(false);
                txtDelay.setText("");
                buttonPlus_relay.setEnabled(false);
                buttonMinus_relay.setEnabled(false);
                relayno.setEnabled(false);
                relayno.setText("");
            }
            // Set click listeners for toggle button and buttons
            toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Do something when toggle button is checked or unchecked
                int position = (int) buttonView.getTag();
                if (toggleButton.isChecked() == true) {
                    cv.setBackgroundColor(Color.parseColor("#152B38"));
                    if (waterFlag.isChecked() == true) {
                        //cardView.setBackgroundColor(Color.GREEN);
                        waterFlag.setText("Water");
                    } else {
                        //cardView.setBackgroundColor(Color.RED);
                        waterFlag.setText("Steam");
                    }
                    energisedFlag.setEnabled(true);
                    waterFlag.setEnabled(true);
                    buttonMinus.setEnabled(true);
                    buttonPlus.setEnabled(true);
                    txtDelay.setEnabled(true);
                    txtDelay.setText("1 sec");
                    buttonPlus_relay.setEnabled(true);
                    buttonMinus_relay.setEnabled(true);
                    relayno.setEnabled(true);
                    relayno.setText("" + tripRelayNo[position-1]);
                    channelSta_new[position-1] = true;
                    update_channel_status_new(position-1,1);
                }
                else {
                    channelSta_new[position-1] = false;
                    cv.setBackgroundColor(Color.LTGRAY); // Replace with the desired color
                    energisedFlag.setEnabled(false);
                    waterFlag.setEnabled(false);
                    buttonMinus.setEnabled(false);
                    buttonPlus.setEnabled(false);
                    txtDelay.setEnabled(false);
                    txtDelay.setText("");
                    buttonPlus_relay.setEnabled(false);
                    buttonMinus_relay.setEnabled(false);
                    relayno.setEnabled(false);
                    relayno.setText("");
                    update_channel_status_new(position-1,0);
                }

            });
            waterFlag.setTag(i);
            if(waterSteamFlag_new[i-1])
            {
                waterFlag.setText("Steam");
                waterFlag.setChecked(false);
            }
            else
            {
                waterFlag.setText("Water");
                waterFlag.setChecked(true);
            }
            waterFlag.setOnCheckedChangeListener((buttonView,isChecked)-> {
                int position = (int) buttonView.getTag();
                if (waterFlag.isChecked()) {
                    waterFlag.setText("Water");
                    waterSteamFlag_new[position-1] = false;
                    update_water_steam_status_new(position - 1, 0);

                } else {
                    waterFlag.setText("Steam");
                    waterSteamFlag_new[position-1] = true;
                    update_water_steam_status_new(position-1,1);
                }
            });

            energisedFlag.setTag(i);
            if(EnergisedDeenergisedFlag_new[i-1])
            {
                energisedFlag.setText("Energised");
                energisedFlag.setChecked(true);
            }
            else
            {
                energisedFlag.setText("De-Energised");
                energisedFlag.setChecked(false);
            }
            energisedFlag.setOnCheckedChangeListener((buttonView,isChecked)-> {
                int relay_no = Integer.parseInt(String.valueOf(relayno.getText()));
                int position = (int) buttonView.getTag();
                if (energisedFlag.isChecked() == true) {
                    energisedFlag.setText("Energised");
                    //energisedFlag[position] = true;
                    if(relay_no>0) {
                        EnergisedDeenergisedFlag_new[relay_no-1] = true;
                        update_energised_status_new(relay_no-1, 1);
                    }
                    else{

                    }
                } else {
                    energisedFlag.setText("De-Energised");
                    //energisedFlag[position] = false;
                    if(relay_no>0) {
                        EnergisedDeenergisedFlag_new[relay_no-1] = false;
                        update_energised_status_new(relay_no-1, 0);
                    }
                    else{

                    }
                }
            });

            buttonPlus.setTag(i);
            buttonPlus.setOnClickListener(v -> {
                int relay_no = Integer.parseInt(String.valueOf(relayno.getText()));
                int position = (int) buttonPlus.getTag();
                String tmp = txtDelay.getText().toString().substring(1,2);
                int x =0;
                if(tmp.equals(" ")) x = Integer.parseInt(txtDelay.getText().toString().substring(0,1));

                else                x = Integer.parseInt(txtDelay.getText().toString().substring(0,2));
                if(x<25)
                {
                    x++;
                    txtDelay.setText(x + " sec");
                    buttonMinus.setEnabled(true);
                }
                else
                {
                    buttonPlus.setEnabled(false);
                }
                if(relay_no>0) {
                    update_trip_time_new(relay_no - 1, x);
                }
            });

            buttonMinus.setTag(i);
            buttonMinus.setOnClickListener(v -> {
                int relay_no = Integer.parseInt(String.valueOf(relayno.getText()));
                int position = (int) buttonMinus.getTag();
                String tmp = txtDelay.getText().toString().substring(1,2);
                int x =0;
                if(tmp.equals(" ")) x = Integer.parseInt(txtDelay.getText().toString().substring(0,1));

                else                x = Integer.parseInt(txtDelay.getText().toString().substring(0,2));
                if(x>1)
                {
                    x--;
                    txtDelay.setText(x + " sec");
                    buttonPlus.setEnabled(true);

                }
                else   buttonMinus.setEnabled(false);
                if(relay_no>0) {
                    update_trip_time_new(relay_no - 1, x);
                }
            });
            buttonPlus_relay.setTag(i);
            buttonPlus_relay.setOnClickListener(v -> {
                int position = (int) buttonPlus_relay.getTag();
                int x ;
                 x = Integer.parseInt( relayno.getText().toString());

                if(x<25)
                {
                    x++;
                    relayno.setText("" + x);
                    buttonMinus_relay.setEnabled(true);
                }
                else
                {
                    buttonPlus_relay.setEnabled(false);
                }

                update_trip_Relay_no(position - 1, x);

            });

            buttonMinus_relay.setTag(i);
            buttonMinus_relay.setOnClickListener(v -> {
                int position = (int) buttonMinus_relay.getTag();
                int x ;
                 x = Integer.parseInt(relayno.getText().toString());

                if(x>0)
                {
                    x--;
                    relayno.setText("" + x);
                    buttonPlus_relay.setEnabled(true);

                }
                else   buttonMinus_relay.setEnabled(false);

                update_trip_Relay_no(position - 1, x);

            });

            // Add the card's content to the CardView
            cardView.addView(cardContent);

            // Add the CardView to the container
            cardContainer.addView(cardView);
        }
    }
    private void populate_channels(View view)
    {
        LinearLayout cardContainer = view.findViewById(R.id.cardContainer);

        for (int i = 1; i < 5; i++) {
            CardView cardView = new CardView(requireContext());

            cardView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            cardView.setCardElevation(8);
            cardView.setContentPadding(16, 1, 16, 1);


            // Inflate the card's content
            View cardContent = getLayoutInflater().inflate(R.layout.channel_item, null);

            // Find ToggleButton and Buttons within the card's content
            CardView cv = cardContent.findViewById(R.id.card_view);
            Switch toggleButton = cardContent.findViewById(R.id.switch_active_mrk);
            Button buttonPlus = cardContent.findViewById(R.id.buttonPlus);
            Button buttonMinus = cardContent.findViewById(R.id.buttonMinus);
            TextView txtDelay = cardContent.findViewById(R.id.textViewDelay);
            TextView chnNo = cardContent.findViewById(R.id.textView_channelName);
            Switch waterFlag = cardContent.findViewById(R.id.switch_water_mrk);
            Switch energisedFlag = cardContent.findViewById(R.id.switch_energised_mrk);

            chnNo.setTag(i);
            chnNo.setText("Channel No"+i);
            toggleButton.setTag(i);

            if(channelSta[i-1])
            {
                cv.setBackgroundColor(Color.parseColor("#152B38"));
                toggleButton.setChecked(true);
                energisedFlag.setEnabled(true);
                waterFlag.setEnabled(true);
                buttonMinus.setEnabled(true);
                buttonPlus.setEnabled(true);
                txtDelay.setText(trippingDelay[i-1] + " sec");
                txtDelay.setEnabled(true);
            }
            else
            {
                toggleButton.setChecked(false);
                cv.setBackgroundColor(Color.LTGRAY); // Replace with the desired color
                energisedFlag.setEnabled(false);
                waterFlag.setEnabled(false);
                buttonMinus.setEnabled(false);
                buttonPlus.setEnabled(false);
                txtDelay.setEnabled(false);
                txtDelay.setText("");
            }

            // Set click listeners for toggle button and buttons
            toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Do something when toggle button is checked or unchecked
                int position = (int) buttonView.getTag();
                if (toggleButton.isChecked() == true) {
                    cv.setBackgroundColor(Color.parseColor("#152B38"));
                    if (waterFlag.isChecked() == true) {
                        //cardView.setBackgroundColor(Color.GREEN);
                        waterFlag.setText("Water");
                    } else {
                        //cardView.setBackgroundColor(Color.RED);
                        waterFlag.setText("Steam");
                    }
                    energisedFlag.setEnabled(true);
                    waterFlag.setEnabled(true);
                    buttonMinus.setEnabled(true);
                    buttonPlus.setEnabled(true);
                    txtDelay.setText("1 sec");
                    txtDelay.setEnabled(true);
                    channelSta[position-1] = true;
                }
                else {
                    channelSta[position-1] = false;
                    cv.setBackgroundColor(Color.LTGRAY); // Replace with the desired color
                    energisedFlag.setEnabled(false);
                    waterFlag.setEnabled(false);
                    buttonMinus.setEnabled(false);
                    buttonPlus.setEnabled(false);
                    txtDelay.setEnabled(false);
                    txtDelay.setText("");
                }
                update_channel_status(position-1);
            });

            waterFlag.setTag(i);

            if(waterSteamFlag[i-1])
            {
                waterFlag.setText("Steam");
                waterFlag.setChecked(false);
            }
            else
            {
                waterFlag.setText("Water");
                waterFlag.setChecked(true);
            }
            waterFlag.setOnCheckedChangeListener((buttonView,isChecked)-> {
                int position = (int) buttonView.getTag();
                if (waterFlag.isChecked()) {
                    waterFlag.setText("Water");
                    update_water_steam_status_new(position,0);
                } else {
                    waterFlag.setText("Steam");
                    update_water_steam_status_new(position,1);
                }
            });

            energisedFlag.setTag(i);
            if(energisedDeenergisedFlag[i-1])
            {
                energisedFlag.setText("Energised");
                energisedFlag.setChecked(true);
            }
            else
            {
                energisedFlag.setText("De-Energised");
                energisedFlag.setChecked(false);
            }
            energisedFlag.setOnCheckedChangeListener((buttonView,isChecked)-> {
                int position = (int) buttonView.getTag();
                if (energisedFlag.isChecked() == true) {
                    energisedFlag.setText("Energised");
                    //energisedFlag[position] = true;
                    update_energised_status(position,1);
                } else {
                    energisedFlag.setText("De-Energised");
                    //energisedFlag[position] = false;
                    update_energised_status(position,0);
                }
            });

            buttonPlus.setTag(i);
            buttonPlus.setOnClickListener(v -> {
                int position = (int) buttonPlus.getTag();
                String tmp = txtDelay.getText().toString().substring(1,2);
                int x =0;
                if(tmp.equals(" ")) x = Integer.parseInt(txtDelay.getText().toString().substring(0,1));

                else                x = Integer.parseInt(txtDelay.getText().toString().substring(0,2));
                if(x<25)
                {
                    x++;
                    txtDelay.setText(x + " sec");
                    buttonMinus.setEnabled(true);
                }
                else
                {
                    buttonPlus.setEnabled(false);
                }
                update_trip_time(position,x);
            });

            buttonMinus.setTag(i);
            buttonMinus.setOnClickListener(v -> {
                int position = (int) buttonMinus.getTag();
                String tmp = txtDelay.getText().toString().substring(1,2);
                int x =0;
                if(tmp.equals(" ")) x = Integer.parseInt(txtDelay.getText().toString().substring(0,1));

                else                x = Integer.parseInt(txtDelay.getText().toString().substring(0,2));
                if(x>1)
                {
                    x--;
                    txtDelay.setText(x + " sec");
                    buttonPlus.setEnabled(true);

                }
                else   buttonMinus.setEnabled(false);
                update_trip_time(position,x);
            });

            // Add the card's content to the CardView
            cardView.addView(cardContent);

            // Add the CardView to the container
            cardContainer.addView(cardView);

            
        }
    }
    private int process_store_Channel_data(String recvd_str_new) {
        int no_of_bytes = 0;
        String tmp = "";
        if (recvd_str_new.length() < 484) return 0;
        String crc_received = recvd_str_new.substring(480, 484);
        byte[] received_byte = recvd_str_new.substring(0,480).getBytes(StandardCharsets.US_ASCII);
        data_str_new = recvd_str_new.substring(0,480).split(",");
        int Calculatecrc = mainActivity.calculateChecksum(received_byte);
        String final_calculate_crc = mainActivity.checksumToAscii(Calculatecrc);
        if (final_calculate_crc.equals(crc_received)) {
            if(data_str_new[10].length()==4){
                total_no_of_channels = Integer.parseInt(data_str_new[10].substring(2,4),16);
            }
            else{
                return 0;
            }
            for(int i=14;i<(total_no_of_channels+14);i++){
                int tmp_channelsta = Integer.parseInt(data_str_new[i].substring(0,1));
                if(tmp_channelsta==1){
                    channelSta_new[i-14] = false;
                }
                else {
                    channelSta_new[i-14] = true;
                }

                int tmp_ws = Integer.parseInt(data_str_new[i].substring(1,2));
                if(tmp_ws==1){
                    waterSteamFlag_new[i-14] = true;
                }
                else {
                    waterSteamFlag_new[i-14] = false;
                }

                tripRelayNo[i-14] = Integer.parseInt(data_str_new[i].substring(2,4),16);
            }

            for(int i=0;i<24;i++){
                int tmp_edflag = Integer.parseInt(data_str_new[63+i].substring(0,1));
                if(tmp_edflag==1){
                    EnergisedDeenergisedFlag_new[i]=true;
                }
                else{
                    EnergisedDeenergisedFlag_new[i]=false;
                }

                trippingDelay_new[i] = Integer.parseInt(data_str_new[63+i].substring(1,4),16);
            }

            return 1;
        } else {
            return 0;
        }

    }
    public static String convertStringArrayToAsciiString(String[] stringArray) {
//        StringBuilder asciiStringBuilder = new StringBuilder();
//
//        for (String str : stringArray) {
//            for (char ch : str.toCharArray()) {
//                asciiStringBuilder.append((int) ch).append(" ");
//            }
//        }
//
//        return asciiStringBuilder.toString().trim();
        StringBuilder sb = new StringBuilder();
        for (String str : stringArray) {
//            if (sb.length() != 0) {
//             //   sb.append(" "); // Adding a space between elements
//            }
            sb.append(str);
        }

        return sb.toString().trim();
    }
    private void savingData_new(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i=0;i<total_no_of_channels;i++){
                        data_str_new[i+14] = tmp_channel_sta[i] + tmp_waterSteam[i] + tmp_relayno[i];
                    }
                    for(int i=0; i<total_no_of_channels; i++){
                        data_str_new[i+63] = tmp_energisedDeenergised[i] + tmp_tripDelay[i];
                    }
                    String[] data_str_new_final = new String[data_str_new.length];
                    for (int i = 0; i < data_str_new.length; i++) {
                        data_str_new_final[i] = data_str_new[i] + ",";
                    }
                    received_data_str = convertStringArrayToAsciiString(data_str_new_final);
                    mainActivity.PrepareCommandDatasend(received_data_str);
                    delayRoutine(10);
                    mainActivity.sendSettingCommand("?0002,00EE#");
                    if (mainActivity.received_data_string_new.equals("")){
                        mainActivity.sendSettingCommand("?0002,00EE#");
                    }
                    else{}
                    delayRoutine(200);
                    mainActivity.sendSettingCommand("?0005,00F1#");
                    delayRoutine(300);
                    if (mainActivity.received_data_string_new.equals("")){
                        mainActivity.sendSettingCommand("?0005,00F1#");
                    }
                    else{}
                    Thread.sleep(1); // Simulate loading time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Hide the ProgressDialog after loading is done
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                    }
                });
            }
        }).start();
    }
    private void savingData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    no_of_channels=0;
                    for(int i=0; i<4; i++)
                    {
                        if(channelSta[i]) no_of_channels++;
                    }

                    String noOfChannels = "000" + no_of_channels;

                    boolean write_success = write_operataion("0043",noOfChannels);
                    if(!write_success) write_operataion("0043",noOfChannels);

                    delayRoutine(100);

                    write_success = write_operataion("0010","0003");
                    //((MainActivity)getActivity()).prepare_write_command("0010","0003");

                    while (!write_success) {
                        write_success = write_operataion("0010","0003");
                    }

                    delayRoutine(5000);
                    //textViewStatus.setText("Restarting ...");
                    for(int i=0;i<5;i++) {
                        mainActivity.received_data_string="";
                        ((MainActivity) getActivity()).prepare_write_command("0010", "0001");
                        while (mainActivity.received_data_string.equals("")) { }
                        delayRoutine(10);
                    }
                    Thread.sleep(1); // Simulate loading time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Hide the ProgressDialog after loading is done
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                    }
                });
            }
        }).start();
    }
    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(requireContext(), "Saving", "Please wait...", true, false);
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private boolean read_operation(String startingRegister,String no_of_Registers)
    {
        mainActivity.received_data_string ="";
        ((MainActivity)getActivity()).prepareReadCommand(startingRegister,no_of_Registers);
        delayRoutine(100);

        Date cmdSendTime = Calendar.getInstance().getTime();
        int elapsed_time = 0;
        while (mainActivity.received_data_string.equals("") && elapsed_time<3) {
            Date currentTime = Calendar.getInstance().getTime();
            long millis = currentTime.getTime() - cmdSendTime.getTime();
            elapsed_time = (int) (millis / (1000));
        }
        if(mainActivity.received_data_string.equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean write_operataion(String addr,String data)
    {
        mainActivity.received_data_string="";
        ((MainActivity)getActivity()).prepare_write_command(addr,data);

        Date cmdSendTime = Calendar.getInstance().getTime();
        int elapsed_time = 0;
        while (mainActivity.received_data_string.equals("") && elapsed_time<5) {
            Date currentTime = Calendar.getInstance().getTime();
            long millis = currentTime.getTime() - cmdSendTime.getTime();
            elapsed_time = (int) (millis / (1000));
        }

        if(mainActivity.received_data_string.equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    private void update_trip_time(int pos,int val)
    {
        String registerNo = Integer.toHexString(28+pos);
        while (registerNo.length()<4) registerNo = "0" + registerNo;
        String dataStr = "" + Integer.toHexString(val * 100);
        while (dataStr.length()<4) dataStr = "0" + dataStr;
        boolean x = write_operataion(registerNo,dataStr);
        while (!x) x = write_operataion(registerNo,dataStr);
    }
    private  void update_energised_status(int pos,int flag)
    {
        String registerNo = Integer.toHexString(23+pos);
        while (registerNo.length()<4) registerNo = "0" + registerNo;
        String dataStr = "000" + flag;
        boolean x = write_operataion(registerNo,dataStr);
        while (!x) x = write_operataion(registerNo,dataStr);
    }
    private void update_water_steam_status(int pos,int flag)
    {
        String registerNo = Integer.toHexString(19+pos);
        while (registerNo.length()<4) registerNo = "0" + registerNo;
        String dataStr = "000" + flag;
        boolean x = write_operataion(registerNo,dataStr);
        while (!x) x = write_operataion(registerNo,dataStr);
    }
    private void update_channel_status_new(int no, int flag){

           if(flag==1){
                tmp_channel_sta[no] = "0";

           }
           else{
               tmp_channel_sta[no] = "1";

           }

    }
    private void update_water_steam_status_new(int pos,int flag){

        if(flag == 1){
            tmp_waterSteam[pos]= "1";
        }
        else{
            tmp_waterSteam[pos] = "0";
        }
    }
    private  void update_energised_status_new(int pos,int flag){

        if(flag == 1){
            tmp_energisedDeenergised[pos] = "1";
        }
        else{
            tmp_energisedDeenergised[pos] = "0";
        }
    }
    private void update_trip_time_new(int pos,int val){

        tmp_tripDelay[pos] = Integer.toHexString(val).toUpperCase();
        if(tmp_tripDelay[pos].length()<2){
            tmp_tripDelay[pos] = "00" + tmp_tripDelay[pos];
        }
        else{
            tmp_tripDelay[pos] = "0"+ tmp_tripDelay[pos];
        }
    }
    private void update_trip_Relay_no(int pos,int val){
        tmp_relayno[pos] = Integer.toHexString(val).toUpperCase();
        if(tmp_relayno[pos].length()<2){
            tmp_relayno[pos] ="0" + tmp_relayno[pos];
        }
    }
    private void update_channel_status(int no)
    {
        int no_of_channel = 0;
        for(int i=0;i<4;i++)
        {
            if(channelSta[i])
            {
                String registerNo = "00" + (47+no_of_channel);
                no_of_channel++;
                String dataStr = "000" + i;
                boolean x = write_operataion(registerNo,dataStr);
                while(!x) {
                    x = write_operataion(registerNo,dataStr);
                    delayRoutine(100);
                }
            }
            else
            {
                //((MainActivity)getActivity()).prepare_write_command(registerNo,"0000");
            }

        }
        String no_of_channel_str = "" + no_of_channel;
        while(no_of_channel_str.length()<4) no_of_channel_str = "0" + no_of_channel_str;

        boolean y = write_operataion("0043",no_of_channel_str);
        //write no of channels
        while(!y) {
            y = write_operataion("0043",no_of_channel_str);
        }
        //factory reset command
        // ((MainActivity)getActivity()).prepare_write_command("000A","0004");

    }
    public void delayRoutine(long x)
    {
        try {
            Thread.sleep(x);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int get_no_of_channels(String recvd_str)
    {
        int no_of_bytes=0;
        String tmp="";
        String[] data_str = new String[30];
        if(recvd_str.length()<2) return 0;

        String dev_addr = recvd_str.substring(0, 2);
        if (!dev_addr.equals("F7"))  return 0;          //address ok
        if (recvd_str.length() < 4)  return 0;
        String ctyp = recvd_str.substring(3, 5);
        if (!ctyp.equals("03"))      return 0;      //command type ok

        if (recvd_str.length() > 6)
        {
            no_of_bytes = mainActivity.hexToDec(recvd_str.substring(6, 8));
            int r_str_len = recvd_str.length();
            if (r_str_len > (11 + (no_of_bytes * 3))) {
                for (int i = 0; i < no_of_bytes / 2; i++) {
                    data_str[i * 2] = recvd_str.substring(9 + (i * 6), 9 + (i * 6) + 2);
                    data_str[i * 2 + 1] = recvd_str.substring(12 + (i * 6), 12 + (i * 6) + 2);
                    tmp += data_str[i * 2] + data_str[i * 2 + 1];
                }

                String crc_recv = recvd_str.substring(9 + (no_of_bytes) * 3, 11 + (no_of_bytes) * 3) + recvd_str.substring(12 + (no_of_bytes) * 3, 14 + (no_of_bytes) * 3);

                byte[] hexBytes = mainActivity.hexStringToByteArray(dev_addr + ctyp + recvd_str.substring(6, 8) + tmp);
                int crcValue = mainActivity.calculateCRC(hexBytes);
                String crc_str = Integer.toHexString(crcValue).toUpperCase();

                if (crc_str.length() < 4) crc_str = "0" + crc_str;
                if (crc_str.length() == 4) {
                    crc_str = crc_str.substring(2, 4) + crc_str.substring(0, 2);
                    if (crc_str.equals(crc_recv)) {
                        int tmporary = Integer.parseInt(data_str[0] + data_str[1]);
                        return tmporary;
                    }
                }
            }
        }
        return 0;
    }

    public int get_channel_sta(String recvd_str)
    {
        int no_of_bytes=0;
        String tmp="";

        if(recvd_str.length()<2) return 0;

        String dev_addr = recvd_str.substring(0, 2);
        if (!dev_addr.equals("F7"))  return 0;          //address ok
        if (recvd_str.length() < 4)  return 0;
        String ctyp = recvd_str.substring(3, 5);
        if (!ctyp.equals("03"))      return 0;      //command type ok

        if (recvd_str.length() > 6)
        {
             no_of_bytes = mainActivity.hexToDec(recvd_str.substring(6, 8));
             String[] data_str = new String[no_of_bytes];
             int r_str_len = recvd_str.length();
             if (r_str_len > (11 + (no_of_bytes * 3))) {
                 for (int i = 0; i < no_of_bytes / 2; i++) {
                                    data_str[i * 2] = recvd_str.substring(9 + (i * 6), 9 + (i * 6) + 2);
                                    data_str[i * 2 + 1] = recvd_str.substring(12 + (i * 6), 12 + (i * 6) + 2);
                                    tmp += data_str[i * 2] + data_str[i * 2 + 1];
                    }

                 String crc_recv = recvd_str.substring(9 + (no_of_bytes) * 3, 11 + (no_of_bytes) * 3) + recvd_str.substring(12 + (no_of_bytes) * 3, 14 + (no_of_bytes) * 3);


                 byte[] hexBytes = mainActivity.hexStringToByteArray(dev_addr + ctyp + recvd_str.substring(6, 8) + tmp);
                 int crcValue = mainActivity.calculateCRC(hexBytes);
                 String crc_str = Integer.toHexString(crcValue).toUpperCase();

                 if (crc_str.length() < 4) crc_str = "0" + crc_str;
                 if (crc_str.length() == 4) {
                       crc_str = crc_str.substring(2, 4) + crc_str.substring(0, 2);
                       if (crc_str.equals(crc_recv)) {

                               for(int i=0;i<4;i++) channelSta[i] = false;
                               for(int i=0;i<no_of_channels*2;i+=2)
                               {
                                   if(data_str[i]!=null) {
                                       int tmporary = Integer.parseInt(data_str[i] + data_str[i + 1]);
                                       channelSta[tmporary] = true;
                                   }
                               }
                               return 1;
                           }
                    }
             }
        }
        return 0;
    }

    public int process_command_flagsAndDelay(String recvd_str) {
        int no_of_bytes = 0;
        String tmp = "";
        String[] data_str = new String[30];
        if (recvd_str.length() < 1) return 0;
        String dev_addr = recvd_str.substring(0, 2);
        if (!dev_addr.equals("F7")) return 0;
        if (recvd_str.length() < 4) return 0;
        String ctyp = recvd_str.substring(3, 5);
        if (!ctyp.equals("03")) return 0;         //command type ok

        if (recvd_str.length() > 6) {
            no_of_bytes = mainActivity.hexToDec(recvd_str.substring(6, 8));
            if(no_of_bytes<28) return 0;
            int r_str_len = recvd_str.length();
            if (r_str_len > (11 + (no_of_bytes * 3))) {
                for (int i = 0; i < no_of_bytes / 2; i++) {
                    data_str[i * 2] = recvd_str.substring(9 + (i * 6), 9 + (i * 6) + 2);
                    data_str[i * 2 + 1] = recvd_str.substring(12 + (i * 6), 12 + (i * 6) + 2);
                    tmp += data_str[i * 2] + data_str[i * 2 + 1];
                }
                //String data_str = recvd_str.substring(9, 9 + no_of_bytes*3 + 1);
                String crc_recv = recvd_str.substring(9 + (no_of_bytes) * 3, 11 + (no_of_bytes) * 3) + recvd_str.substring(12 + (no_of_bytes) * 3, 14 + (no_of_bytes) * 3);
                ;


                byte[] hexBytes = mainActivity.hexStringToByteArray(dev_addr + ctyp + recvd_str.substring(6, 8) + tmp);
                int crcValue = mainActivity.calculateCRC(hexBytes);
                String crc_str = Integer.toHexString(crcValue).toUpperCase();

                if (crc_str.length() < 4) crc_str = "0" + crc_str;
                if (crc_str.length() == 4) {
                    crc_str = crc_str.substring(2, 4) + crc_str.substring(0, 2);
                    if (crc_str.equals(crc_recv)) {         //CRC OK
                        if(no_of_bytes>28) no_of_bytes=28;
                        for (int i = 0; i < no_of_bytes / 2; i++)
                        {
                            if (i < 4) {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                if (tmporary == 1)
                                    waterSteamFlag[i] = true;
                                else
                                    waterSteamFlag[i] = false;
                            }
                            else if (i >= 4 && i < 8)
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1], 16);
                                if (tmporary == 1) energisedDeenergisedFlag[i-4] = true;
                                else energisedDeenergisedFlag[i - 4] = false;
                            }
                            else if (i >= 9 && i < 13)
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                trippingDelay[i - 9] = tmporary/100;
                            }
                        }
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

}