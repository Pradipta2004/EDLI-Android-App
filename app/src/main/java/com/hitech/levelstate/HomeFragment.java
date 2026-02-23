package com.hitech.levelstate;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Paint paint[] = new Paint[10];
    boolean[] isGreen = new boolean[10];
    boolean[] isYellow = new boolean[10];
    boolean[] isRed = new boolean[10];
    String[] data_str_new = new String[100];
    Thread thread;
    int myString;
    boolean  fullScreenFlag=false;
    int[] delay_cnt = new int[10];
    int[] Channels_data = new int[48];
    Runnable runnableDashBd,runnableDashBd1hz,runnableDashBd2hz;
    Handler handlerDashBd,handlerDashBd1hz,handlerDashBd2hz;
    int imageResource;
    int no_of_channels;
    private Handler handler = new Handler();
    private int intervalMillis = 250; // 0.1 second interval
    private int interval1Hz = 500; // 0.1 second interval
    private int interval2Hz = 500; // 0.1 second interval
    public int[] LED_status = {0,0,0,0,0,0,0,0,0,0};
    Context mContext;
    View view;
    ProgressDialog progressDialog;
    TextView headingText,textViewStatus;
    private Button refreshButton;
    private ConstraintLayout constraintLayout;
    private List<TextView> textViewList = new ArrayList<>();;
    private LinearLayout linearLayoutContainer;
    private TextView[] textViewArray = new TextView[48];
    private static final int MAX_RETRIES = 6; // Maximum number of retry attempts
    private static final int RETRY_DELAY_MS = 2000;
    TextView chn_data;
    // Delay between retries in milliseconds
    MainActivity mainActivity;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = null;

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            myString = mainActivity.getMyString();
        }

       if(myString==0) {
           view = inflater.inflate(R.layout.fragment_home, container, false);
           mContext = container.getContext();
           // Inflate the layout for this fragment
           //mainActivity = (MainActivity) getActivity();
           //mainActivity.bottomNavigation.setSelectedItemId(R.id.navigation_home);
           ImageButton imgButton = view.findViewById(R.id.fullScreenButton);

           imgButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   fullScreenFlag = true;
                   mainActivity.FullScreencall();
                   imgButton.setVisibility(View.GONE);
               }
           });
           //float cornerRadius = getResources().getDimension(R.dimen.corner_radius); // define the corner radius in resources
           imageResource = R.drawable.lslogo;

           for (int i = 0; i < 10; i++) {
               isRed[i] = false;
               isYellow[i] = false;
               isGreen[i] = false;
               delay_cnt[i] = 1;
           }


           thread = new Thread(new Dashbd_data_pulling_thread());
           thread.start();
           //mainActivity.delayRoutine(1000);
           Thread thread2 = new Thread(new Led_refresh_thread());
           thread2.start();
       }
       if(myString==1){
           view = inflater.inflate(R.layout.fragment_home_new,container, false);
           mContext = container.getContext();


           //recyclerView.setItemAnimator(new DefaultItemAnimator());
           headingText = view.findViewById(R.id.textViewFragmentHeading);
           ArrayList<channelModel> channelModelArrayList = new ArrayList<channelModel>();
           textViewStatus = view.findViewById(R.id.textViewStatus);
           refreshButton = view.findViewById(R.id.refreshButton);
           refreshButton.setEnabled(false);
           refreshButton.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View v) {
                   showProgressDialog();
                   refreshBluetoothData();


               }
           });
       }

        //handlerDashBd = new Handler();

        return view;
    }
    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(requireContext(), "Refreshing", "Please wait...", true, false);
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void populate_channel_data(View view){


        LinearLayout cardContainer = view.findViewById(R.id.cardContainer);
        for (int i = 1; i <=no_of_channels; i++) {
            CardView cardView = new CardView(requireContext());

            cardView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            cardView.setCardElevation(8);
            cardView.setContentPadding(16, 1, 16, 1);
            View cardContent = getLayoutInflater().inflate(R.layout.home_item, null);
            CardView cv = cardContent.findViewById(R.id.card_view_home);
            chn_data = cardContent.findViewById(R.id.textView_channelName_home);
            chn_data.setTag(i);
            chn_data.setText("Channel No"+i+": "+Channels_data[i-1]);
            textViewArray[i-1] = chn_data;
            // Add the card's content to the CardView
            cardView.addView(cardContent);

            // Add the CardView to the container
            cardContainer.addView(cardView);
        }

    }

    private void refreshBluetoothData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mainActivity = (MainActivity) getActivity();
                    if(mainActivity!=null) {
                        mainActivity.PrepareReadCommandChannelStat("0003");
                    }
                    int retryCount = 0;
                    boolean success = false;
                    while (retryCount < MAX_RETRIES && !success) {
                        try {
                            mainActivity.PrepareReadCommandChannelStat("0003");
                            int ChannelStat_received_data= process_store_Channel_data(mainActivity.received_data_string_new);
                            // Handle the received data
                            if( ChannelStat_received_data==1){
                                success = true;

                            } else {
                                retryCount++;

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


                    Thread.sleep(1); // Simulate loading time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Hide the ProgressDialog after loading is done
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < no_of_channels; i++) {
                            textViewArray[i].setText("Channel No"+(i+1)+": "+Channels_data[i]);
                        }
                        hideProgressDialog();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();

        // Set a listener to be notified when the layout is finished loading
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textViewStatus.setText("Getting details ...");
                // This method will be called when the layout is finished loading
                // Start a new thread or perform other tasks here
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //get Water/Energy Flags & delay time
                        if(myString==0) {


                        }
                        if (myString==1) {
                            mainActivity = (MainActivity) getActivity();
                            if(mainActivity!=null) {
                                mainActivity.PrepareReadCommandChannelStat("0003");
                            }
                            int retryCount = 0;
                            boolean success = false;
                            while (retryCount < MAX_RETRIES && !success) {
                                try {
                                    mainActivity.PrepareReadCommandChannelStat("0003");
                                    int ChannelStat_received_data= process_store_Channel_data(mainActivity.received_data_string_new);
                                    // Handle the received data
                                    if( ChannelStat_received_data==1){
                                        success = true;
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
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (myString == 0) {

                                    //cardContainer.invalidate();
                                }
                                else{
                               populate_channel_data(view);
                               refreshButton.setEnabled(true);
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

    private int process_store_Channel_data(String recvd_str_new) {

        int no_of_bytes = 0;
        String tmp = "";
        if (recvd_str_new.length() < 259) return 0;
        String crc_received = recvd_str_new.substring(255, 259);
        byte[] received_byte = recvd_str_new.substring(0, 255).getBytes(StandardCharsets.US_ASCII);
        data_str_new = recvd_str_new.substring(0, 255).split(",");
        int Calculatecrc = mainActivity.calculateChecksum(received_byte);
        String final_calculate_crc = mainActivity.checksumToAscii(Calculatecrc);
        if (final_calculate_crc.equals(crc_received)) {
            if (data_str_new[0].length() == 4) {

                no_of_channels = Integer.parseInt(data_str_new[0].substring(0, 4), 16);
            } else {
                return 0;
            }
            for(int i=0; i< no_of_channels; i++){
                if(data_str_new[i+3].length()==4){
                   Channels_data[i] = Integer.parseInt(data_str_new[i+3].substring(0,3),16);
                }
                else{
                    return 0;
                }
            }
        return 1;
        }
        else{
            return 0;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the thread when the Fragment is destroyed
        if (thread != null) {
            thread.interrupt();
            //thread.stop();
            thread = null;
        }
    }
    class Led_refresh_thread extends Thread{
       
        @Override
        public void run() {
            mainActivity = (MainActivity) getActivity();
            if (myString == 0) {
                while (mainActivity.dashboard_on) {
                    if (LED_status != null) {
                        for (int i = 0; i < 10; i++) {
                            paint[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
                            //paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                            if (LED_status[i] == 0) {
                                paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                            } else if (LED_status[i] == 1 && i > 7) //amber Led
                            {
                                paint[i].setColor(mContext.getResources().getColor(R.color.yellow));
                            } else if (LED_status[i] == 1 && i % 2 == 0 && i < 8) //Red Led
                            {
                                paint[i].setColor(mContext.getResources().getColor(R.color.red));
                            } else if (LED_status[i] == 1 && i % 2 != 0 && i < 8) //green Led
                            {
                                paint[i].setColor(mContext.getResources().getColor(R.color.green));
                            } else if (LED_status[i] == 3 && i > 7) //amber Led
                            {
                                if (isYellow[i]) {
                                    isYellow[i] = false;
                                    paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                } else {
                                    paint[i].setColor(mContext.getResources().getColor(R.color.yellow));
                                    isYellow[i] = true;
                                }
                            } else if (LED_status[i] == 3 && i % 2 == 0 && i < 8) //Red Led
                            {
                                if (isRed[i]) {
                                    paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                    isRed[i] = false;
                                } else {
                                    paint[i].setColor(mContext.getResources().getColor(R.color.red));
                                    isRed[i] = true;
                                }
                            } else if (LED_status[i] == 3 && i % 2 != 0 && i < 8) //green Led
                            {
                                if (isGreen[i]) {
                                    paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                    isGreen[i] = false;
                                } else {
                                    paint[i].setColor(mContext.getResources().getColor(R.color.green));
                                    isGreen[i] = true;
                                }
                            } else if (LED_status[i] == 2 && i > 7) //amber Led
                            {
                                if (delay_cnt[i] > 1) {
                                    delay_cnt[i] = 0;
                                    if (!isYellow[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.yellow));
                                        isYellow[i] = true;
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                        isYellow[i] = false;
                                    }
                                } else {
                                    delay_cnt[i]++;
                                    if (isYellow[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.yellow));
                                        isYellow[i] = true;
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                        isYellow[i] = false;
                                    }
                                }
                            } else if (LED_status[i] == 2 && i % 2 == 0 && i < 8) //Red Led
                            {
                                if (delay_cnt[i] > 1) {
                                    delay_cnt[i] = 0;
                                    if (!isRed[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.red));
                                        isRed[i] = true;
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                        isRed[i] = false;
                                    }
                                } else {
                                    delay_cnt[i]++;
                                    if (isRed[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.red));
                                        isRed[i] = true;
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                        isRed[i] = false;
                                    }
                                }
                            } else if (LED_status[i] == 2 && i % 2 != 0 && i < 8) //green Led
                            {
                                if (delay_cnt[i] > 1) {
                                    delay_cnt[i] = 0;
                                    if (!isGreen[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.green));
                                        isGreen[i] = true;
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                        isGreen[i] = false;
                                    }
                                } else {
                                    delay_cnt[i]++;
                                    if (isGreen[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.green));
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                    }
                                }
                            }


                        }
                        RoundCornerRectangleDrawable drawable = new RoundCornerRectangleDrawable(mContext, imageResource, 10.0f, paint, fullScreenFlag);

                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.setBackground(drawable);
                                view.refreshDrawableState();
                            }
                        });

                        //Toast.makeText(mContext, "refresh", Toast.LENGTH_SHORT).show();
                    }

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
    }
    class Dashbd_data_pulling_thread   extends Thread{
        private boolean isRunning = false;
        
        @Override
        public void run() {
            // Perform the repetitive task here
            // For example, update the timerTextView with the current time
            //long currentTimeMillis = System.currentTimeMillis();
            //timerTextView.setText("Current Time: " + currentTimeMillis);
            mainActivity = (MainActivity) getActivity();
            if (myString == 0) {

                while (mainActivity.dashboard_on) {
                    //System.out.println("Working.");


                    //read_operation("0004", "000A");

                    ((MainActivity) getActivity()).prepareReadCommand("0004", "000A");

                    process_data();
                    if (LED_status != null) {
                        for (int i = 0; i < 10; i++) {
                            paint[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
                            //paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                            if (LED_status[i] == 0) {
                                paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                            } else if (LED_status[i] == 1 && i > 7) //amber Led
                            {
                                paint[i].setColor(mContext.getResources().getColor(R.color.yellow));
                            } else if (LED_status[i] == 1 && i % 2 == 0 && i < 8) //Red Led
                            {
                                paint[i].setColor(mContext.getResources().getColor(R.color.red));
                            } else if (LED_status[i] == 1 && i % 2 != 0 && i < 8) //green Led
                            {
                                paint[i].setColor(mContext.getResources().getColor(R.color.green));
                            } else if (LED_status[i] == 3 && i > 7) //amber Led
                            {
                                if (isYellow[i]) {
                                    isYellow[i] = false;
                                    paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                } else {
                                    paint[i].setColor(mContext.getResources().getColor(R.color.yellow));
                                    isYellow[i] = true;
                                }
                            } else if (LED_status[i] == 3 && i % 2 == 0 && i < 8) //Red Led
                            {
                                if (isRed[i]) {
                                    paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                    isRed[i] = false;
                                } else {
                                    paint[i].setColor(mContext.getResources().getColor(R.color.red));
                                    isRed[i] = true;
                                }
                            } else if (LED_status[i] == 3 && i % 2 != 0 && i < 8) //green Led
                            {
                                if (isGreen[i]) {
                                    paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                    isGreen[i] = false;
                                } else {
                                    paint[i].setColor(mContext.getResources().getColor(R.color.green));
                                    isGreen[i] = true;
                                }
                            } else if (LED_status[i] == 2 && i > 7) //amber Led
                            {
                                if (delay_cnt[i] > 1) {
                                    delay_cnt[i] = 0;
                                    if (!isYellow[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.yellow));
                                        isYellow[i] = true;
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                        isYellow[i] = false;
                                    }
                                } else {
                                    delay_cnt[i]++;
                                    if (isYellow[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.yellow));
                                        isYellow[i] = true;
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                        isYellow[i] = false;
                                    }
                                }
                            } else if (LED_status[i] == 2 && i % 2 == 0 && i < 8) //Red Led
                            {
                                if (delay_cnt[i] > 1) {
                                    delay_cnt[i] = 0;
                                    if (!isRed[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.red));
                                        isRed[i] = true;
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                        isRed[i] = false;
                                    }
                                } else {
                                    delay_cnt[i]++;
                                    if (isRed[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.red));
                                        isRed[i] = true;
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                        isRed[i] = false;
                                    }
                                }
                            } else if (LED_status[i] == 2 && i % 2 != 0 && i < 8) //green Led
                            {
                                if (delay_cnt[i] > 1) {
                                    delay_cnt[i] = 0;
                                    if (!isGreen[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.green));
                                        isGreen[i] = true;
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                        isGreen[i] = false;
                                    }
                                } else {
                                    delay_cnt[i]++;
                                    if (isGreen[i]) {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.green));
                                    } else {
                                        paint[i].setColor(mContext.getResources().getColor(R.color.off_color));
                                    }
                                }
                            }


                        }
                        RoundCornerRectangleDrawable drawable = new RoundCornerRectangleDrawable(mContext, imageResource, 10.0f, paint, fullScreenFlag);

                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.setBackground(drawable);
                                view.refreshDrawableState();
                            }
                        });

                        //Toast.makeText(mContext, "refresh", Toast.LENGTH_SHORT).show();
                    }


                    // Schedule the Runnable to run again after the interval
                    //handlerDashBd.postDelayed(this, intervalMillis);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
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

        public void delayRoutine(long x)
        {
            try {
                Thread.sleep(x);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void stopThread() {
            isRunning = false;
        }

        public int calculateCRC(byte[] data) {
            int crc = 0xFFFF;

            for (int i = 0; i < data.length; i++) {
                crc ^= (int) data[i] & 0xFF;
                for (int j = 0; j < 8; j++) {
                    if ((crc & 0x0001) == 1) {
                        crc >>= 1;
                        crc ^= 0xA001;
                    } else {
                        crc >>= 1;
                    }
                }
            }

            // Swap low and high bytes to match Modbus endianness
            // crc = (crc << 8) | ((crc >> 8) & 0xFF);

            return crc;
        }

        public int hexToDec(String hexString) {
            try {
                return Integer.parseInt(hexString, 16);
            } catch (NumberFormatException e) {
                // Handle the case when the hexString is invalid
                e.printStackTrace();
                return -1; // Or any other value to indicate an error
            }
        }

        public byte[] hexStringToByteArray(String hexString) {
            int len = hexString.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                        + Character.digit(hexString.charAt(i + 1), 16));
            }
            return data;
        }

        public void process_data()
        {
            String recvd_str="";

            String[] data_str = new String[30];
            int no_of_bytes=0;
            String tmp="";
            recvd_str = mainActivity.received_data_string;
            if(recvd_str.length()>1) {
                String dev_addr = recvd_str.substring(0, 2);
                if (dev_addr.equals("F7")) {            //address ok
                    if(recvd_str.length()>4)
                    {
                        String ctyp = recvd_str.substring(3, 5);
                        if(ctyp.equals("03"))           //command type ok
                        {
                            if(recvd_str.length()>6)
                            {
                                no_of_bytes = hexToDec(recvd_str.substring(6, 8));
                                int r_str_len = recvd_str.length();
                                if(r_str_len>(11 + (no_of_bytes * 3))) {
                                    for (int i = 0; i < no_of_bytes/2; i++) {
                                        data_str[i*2] = recvd_str.substring(9 + (i * 6), 9 + (i * 6) + 2);
                                        data_str[i*2+1] = recvd_str.substring(12 + (i * 6), 12 + (i * 6) + 2);
                                        tmp += data_str[i*2] + data_str[i*2+1];
                                    }
                                    //String data_str = recvd_str.substring(9, 9 + no_of_bytes*3 + 1);
                                    String crc_recv = recvd_str.substring(9 + (no_of_bytes) * 3, 11 + (no_of_bytes) * 3) + recvd_str.substring(12 + (no_of_bytes) * 3, 14 + (no_of_bytes) * 3);;


                                    byte[] hexBytes = hexStringToByteArray(dev_addr + ctyp + recvd_str.substring(6, 8) + tmp);
                                    int crcValue = calculateCRC(hexBytes);
                                    String crc_str = Integer.toHexString(crcValue).toUpperCase();

                                    if(crc_str.length()<4) crc_str = "0" + crc_str;
                                    if(crc_str.length()==4) {
                                        crc_str = crc_str.substring(2, 4) + crc_str.substring(0, 2);
                                        if (crc_str.equals(crc_recv)) {
                                            for (int i = 0; i < no_of_bytes / 2; i++)
                                                LED_status[i] = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1]);
                                        }
                                        else
                                        {

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}