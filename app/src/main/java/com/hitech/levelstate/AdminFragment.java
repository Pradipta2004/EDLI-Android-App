package com.hitech.levelstate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminFragment extends Fragment {

    Button factoryResetButton;
    Button btnWrite,btnLoad,btnSaveCal,btnWriteCal;
    ToggleButton btn20mACal,btn4mACal,btnNormalCal;
    EditText editTextdeviceAddr,editTextregisterNo,editTextdata,editText4mACh1,editText4mACh2,editText20mACh1,editText20mACh2,editTextSteamLvl,editTextWaterLvl,editTextShortLvl;
    TextView textViewSent,textViewReceived;
    MainActivity mainActivity;
    ProgressDialog progressDialog;
    String[] data_str_new = new String[100];
    private static final int MAX_RETRIES = 6; // Maximum number of retry attempts
    private static final int RETRY_DELAY_MS = 2000; // Delay between retries in milliseconds
    private Handler handler = new Handler();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    int myString;
    int Channel1_4mA;
    int Channel2_4mA;
    int Channel1_20mA;
    int Channel2_20mA;
    int SteamLevel;
    int WaterLevel;
    int ShortLevel;
    String received_data_str_cal="";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context mContext;
    public AdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminFragment newInstance(String param1, String param2) {
        AdminFragment fragment = new AdminFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        MainActivity mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
            myString = mainActivity.getMyString();


            if (myString == 0) {
                view = inflater.inflate(R.layout.fragment_admin, container, false);
                mContext=container.getContext();
                // mainActivity = (MainActivity) getActivity();
                fragmentadmin(view);
            } else {
                view = inflater.inflate(R.layout.fragment_admin_new, container, false);
                mContext=container.getContext();
                fragmentadminnew(view);
            }

        }
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);


        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();

        // Set a listener to be notified when the layout is finished loading
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                showProgressDialog2();
                // This method will be called when the layout is finished loading
                //textViewStatus.setText("Getting details ...");
                // Start a new thread or perform other tasks here
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //get values
                        if(myString==1) {
                            mainActivity.PrepareReadCommandNew("0001");
                            int retryCount = 0;
                            boolean success = false;

                            while (retryCount < MAX_RETRIES && !success) {
                                try {
                                    mainActivity.PrepareReadCommandNew("0001");
                                    int Calibration_Data = process_store_Calibration_Data(mainActivity.received_data_string_new);
                                    // Handle the received data
                                    if(Calibration_Data==1){
                                        success = true; // Data received successfully
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


                            }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (myString == 1) {
                                    populate_details_Calibration(view);
                                    delayRoutine(100);
                                    hideProgressDialog();
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
    private void populate_details_Calibration(View view){
        editTextSteamLvl.setText(""+SteamLevel);
        editTextWaterLvl.setText(""+WaterLevel);
        editTextShortLvl.setText(""+ShortLevel);
        editText4mACh1.setText(""+Channel1_4mA);
        editText20mACh1.setText(""+Channel1_20mA);
        editText4mACh2.setText(""+Channel2_4mA);
        editText20mACh2.setText(""+Channel2_20mA);
    }
    private void fragmentadmin(View view){
        mainActivity = (MainActivity) getActivity();
        factoryResetButton = view.findViewById(R.id.buttonFactoryReset);
        btnWrite = view.findViewById(R.id.buttonSend);
        btnLoad = view.findViewById(R.id.buttonload);
        editTextdeviceAddr = view.findViewById(R.id.editTextDeviceAddress);
        editTextregisterNo = view.findViewById(R.id.editTextStartingAddress);
        editTextdata = view.findViewById(R.id.editTextData);

        textViewReceived =view.findViewById(R.id.textViewReceivedData);
        textViewSent = view.findViewById(R.id.textViewSentData);

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(getActivity());
                int startAddress = Integer.parseInt(editTextregisterNo.getText().toString());
                startAddress--;
                String startAddressHex = Integer.toHexString(startAddress);
                while (startAddressHex.length()<4) startAddressHex = "0" + startAddressHex;

                textViewSent.setText(startAddress + "");
                mainActivity.received_data_string="";
                mainActivity.prepareReadCommand(startAddressHex,"0001");

                Date cmdSendTime = Calendar.getInstance().getTime();
                int count = 0;
                while(mainActivity.received_data_string.equals("") && count<5){
                    Date currentTime = Calendar.getInstance().getTime();
                    long millis = currentTime.getTime() - cmdSendTime.getTime();
                    count = (int) (millis / (1000));
                }

                if(mainActivity.received_data_string.equals(""))
                {
                    textViewReceived.setText("Comms fail");
                    hideKeyboard(getActivity());
                }
                else
                {
                    textViewReceived.setText(mainActivity.received_data_string);
                    process_single_register_recv_data(mainActivity.received_data_string);
                }
            }
        });



        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int startAddress = Integer.parseInt(editTextregisterNo.getText().toString());
                startAddress--;
                String startAddressHex = Integer.toHexString(startAddress);
                while (startAddressHex.length()<4) startAddressHex = "0" + startAddressHex;
                String dataStr = Integer.toHexString(Integer.parseInt(editTextdata.getText().toString()));
                while (dataStr.length()<4) dataStr = "0" + dataStr;
                mainActivity.received_data_string="";
                mainActivity.prepare_write_command(startAddressHex,dataStr);
                delayRoutine(100);
//                mainActivity.received_data_string="";
//                ((MainActivity) getActivity()).prepare_write_command("0010","0003");
//                delayRoutine(100);
                while(mainActivity.received_data_string.equals("")){}
                showMessageBox(view,"Register write complete","INFO");
//                delayRoutine(5000);
//                //textViewStatus.setText("Restarting ...");
//                for(int i=0;i<5;i++) {
//                    mainActivity.received_data_string="";
//                    ((MainActivity) getActivity()).prepare_write_command("0010", "0001");
//                    while (mainActivity.received_data_string.equals("")) { }
//                    delayRoutine(10);
            }

        });



        factoryResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainActivity.received_data_string="";
                ((MainActivity)getActivity()).prepare_write_command("000A","0004");
                while (mainActivity.received_data_string.equals("")) { }

                showMessageBox(view,"Factory RESET complete","INFO");
            }
        });
    }
    private void fragmentadminnew(View view){
        mainActivity = (MainActivity) getActivity();
        btnWriteCal = view.findViewById(R.id.buttonWriteCalibration);
        btnSaveCal = view.findViewById(R.id.buttonSavecalibration);
        editText4mACh1 = view.findViewById(R.id.editText4mACh1);
        editText20mACh1 = view.findViewById(R.id.editText20mACh1);
        editText4mACh2 = view.findViewById(R.id.editText4mACh2);
        editText20mACh2 = view.findViewById(R.id.editText20mACh2);
        editTextSteamLvl = view.findViewById(R.id.editTextSteamLevel);
        editTextShortLvl = view.findViewById(R.id.editTextShortLevel);
        editTextWaterLvl = view.findViewById(R.id.editTextWaterLevel);
        btn4mACal = view.findViewById(R.id.toggleButton4mACalibration);
        btn20mACal = view.findViewById(R.id.toggleButton20mACalibration);
        btnNormalCal = view.findViewById(R.id.toggleButtonNormalMode);
        btn4mACal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    // The toggle is enabled
                    editText4mACh1.setFocusableInTouchMode(true);
                    editText4mACh1.setFocusable(true);
                    editText4mACh2.setFocusableInTouchMode(true);
                    editText4mACh2.setFocusable(true);
                    btn20mACal.setEnabled(false);
                    btnNormalCal.setEnabled(false);
                    Process4mACal();

                } else {
                    // The toggle is disabled
                    editText4mACh1.setFocusableInTouchMode(false);
                    editText4mACh1.setFocusable(false);
                    editText4mACh2.setFocusableInTouchMode(false);
                    editText4mACh2.setFocusable(false);
                    btn20mACal.setEnabled(true);
                    btnNormalCal.setEnabled(true);
                }
            }
        });

        btn20mACal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    editText20mACh1.setFocusableInTouchMode(true);
                    editText20mACh1.setFocusable(true);
                    editText20mACh2.setFocusableInTouchMode(true);
                    editText20mACh2.setFocusable(true);
                    btn4mACal.setEnabled(false);
                    btnNormalCal.setEnabled(false);
                    Process20mACal();
                } else {
                    // The toggle is disabled
                    editText20mACh1.setFocusableInTouchMode(false);
                    editText20mACh1.setFocusable(false);
                    editText20mACh2.setFocusableInTouchMode(false);
                    editText20mACh2.setFocusable(false);
                    btn4mACal.setEnabled(true);
                    btnNormalCal.setEnabled(true);
                }
            }
        });
        btnNormalCal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    editText4mACh1.setFocusableInTouchMode(false);
                    editText4mACh1.setFocusable(false);
                    editText4mACh2.setFocusableInTouchMode(false);
                    editText4mACh2.setFocusable(false);
                    editText20mACh1.setFocusableInTouchMode(false);
                    editText20mACh1.setFocusable(false);
                    editText20mACh2.setFocusableInTouchMode(false);
                    editText20mACh2.setFocusable(false);
                    btn4mACal.setEnabled(false);
                    btn20mACal.setEnabled(false);
                    NormalCalprocess();
                } else {
                    // The toggle is disabled
                    btn4mACal.setEnabled(true);
                    btn20mACal.setEnabled(true);
                }
            }
        });
        btnWriteCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write_data_Cal();
            }
        });
        btnSaveCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_data_Cal();
            }
        });
    }
    private void showProgressDialog2() {
        progressDialog = ProgressDialog.show(requireContext(), "Fetching Data from device", "Please wait...", true, false);
    }
    private void showProgressDialog1() {
        progressDialog = ProgressDialog.show(requireContext(), "Writting Data to device", "Please wait...", true, false);
    }
    private void showProgressDialog() {
       progressDialog = ProgressDialog.show(requireContext(), "Calibrating", "Please wait...", true, false);
    }
    private void showProgressDialog3() {
        progressDialog = ProgressDialog.show(requireContext(), "Saving", "Please wait...", true, false);
    }
    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void Process4mACal(){
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mainActivity.sendSettingCommand("?0006,00F2#");
                    delayRoutine(100);
                    if (mainActivity.received_data_string_new.equals("")){
                        mainActivity.sendSettingCommand("?0006,00F2#");
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
    private void Process20mACal(){
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mainActivity.sendSettingCommand("?0007,00F3#");
                    delayRoutine(100);
                    if (mainActivity.received_data_string_new.equals("")){
                        mainActivity.sendSettingCommand("?0007,00F3#");
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
    private int process_store_Calibration_Data(String recvd_str_new){
        int no_of_bytes = 0;
        String tmp = "";
        if (recvd_str_new.length() < 484) return 0;
        String crc_received = recvd_str_new.substring(480, 484);
        byte[] received_byte = recvd_str_new.substring(0,480).getBytes(StandardCharsets.US_ASCII);
        data_str_new = recvd_str_new.substring(0,480).split(",");
        int Calculatecrc = mainActivity.calculateChecksum(received_byte);
        String final_calculate_crc = mainActivity.checksumToAscii(Calculatecrc);
        if (final_calculate_crc.equals(crc_received)) {
        if(data_str_new[3].length()==4){
            Channel1_4mA = Integer.parseInt(data_str_new[3],16);
        }
        else{
            return 0;
        }
        if(data_str_new[4].length()==4){
            Channel1_20mA = Integer.parseInt(data_str_new[4],16);
        }
        else{
            return 0;
        }
        if(data_str_new[5].length()==4){
            Channel2_4mA = Integer.parseInt(data_str_new[5],16);
        }
        else{
            return 0;
        }
        if(data_str_new[6].length()==4){
            Channel2_20mA = Integer.parseInt(data_str_new[6],16);
        }
        else {
            return 0;
        }
        if(data_str_new[11].length()==4){
            SteamLevel = Integer.parseInt(data_str_new[11],16);
        }
        else{
            return 0;
        }
        if(data_str_new[12].length()==4){
            WaterLevel = Integer.parseInt(data_str_new[12],16);
        }
        else{
            return 0;
        }
        if(data_str_new[13].length()==4){
            ShortLevel = Integer.parseInt(data_str_new[13],16);
        }
        else{
            return 0;
        }
        return 1;
        } else {
            return 0;
        }
    }

    private void NormalCalprocess(){
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mainActivity.sendSettingCommand("?0008,00F4#");
                    delayRoutine(100);
                    if (mainActivity.received_data_string_new.equals("")){
                        mainActivity.sendSettingCommand("?0008,00F4#");
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
    private void write_data_Cal(){
        showProgressDialog1();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String tmp_SteamLvl;
                    String tmp = String.valueOf(editTextSteamLvl.getText());
                    int tmp1 = Integer.parseInt(tmp);
                    tmp_SteamLvl = Integer.toHexString(tmp1).toUpperCase();
                    while (tmp_SteamLvl.length()<4){
                        tmp_SteamLvl = "0" + tmp_SteamLvl;
                    }
                    data_str_new[11] = tmp_SteamLvl;

                    String tmp_WaterLvl;
                     tmp = String.valueOf(editTextWaterLvl.getText());
                     tmp1 = Integer.parseInt(tmp);
                     tmp_WaterLvl = Integer.toHexString(tmp1).toUpperCase();
                     while (tmp_WaterLvl.length()<4) tmp_WaterLvl = "0" + tmp_WaterLvl;
                     data_str_new[12] = tmp_WaterLvl;

                    String tmp_ShortLvl;
                    tmp = String.valueOf(editTextShortLvl.getText());
                    tmp1 = Integer.parseInt(tmp);
                    tmp_ShortLvl = Integer.toHexString(tmp1).toUpperCase();
                    if(tmp_ShortLvl.length()==3){
                        tmp_ShortLvl= "0" + tmp_ShortLvl;
                    }
                    if(tmp_ShortLvl.length()==2){
                        tmp_ShortLvl = "00" + tmp_ShortLvl;
                    }
                    if (tmp_ShortLvl.length()==1){
                        tmp_ShortLvl = "000" + tmp_ShortLvl;
                    }
                    data_str_new[13] = tmp_ShortLvl;

                    String tmp_4mAch1;
                    tmp = String.valueOf(editText4mACh1.getText());
                    tmp1 = Integer.parseInt(tmp);
                    tmp_4mAch1 = Integer.toHexString(tmp1).toUpperCase();
                    while (tmp_4mAch1.length()<4)tmp_4mAch1 = "0" + tmp_4mAch1;
                    data_str_new[3] = tmp_4mAch1;

                    String tmp_20mAch1;
                    tmp = String.valueOf(editText20mACh1.getText());
                    tmp1 = Integer.parseInt(tmp);
                    tmp_20mAch1 = Integer.toHexString(tmp1).toUpperCase();
                    while (tmp_20mAch1.length()<4)tmp_20mAch1 = "0" + tmp_20mAch1;
                    data_str_new[4] = tmp_20mAch1;

                    String tmp_4mAch2;
                    tmp = String.valueOf(editText4mACh2.getText());
                    tmp1 = Integer.parseInt(tmp);
                    tmp_4mAch2 = Integer.toHexString(tmp1).toUpperCase();
                    while (tmp_4mAch2.length()<4)tmp_4mAch2 = "0" + tmp_4mAch2;
                    data_str_new[5] = tmp_4mAch2;

                    String tmp_20mAch2;
                    tmp = String.valueOf(editText20mACh2.getText());
                    tmp1 = Integer.parseInt(tmp);
                    tmp_20mAch2 = Integer.toHexString(tmp1).toUpperCase();
                    while (tmp_20mAch2.length()<4)tmp_20mAch2 = "0" + tmp_20mAch2;
                    data_str_new[6] = tmp_20mAch2;

                    String[] data_str_new_final = new String[data_str_new.length];
                    for (int i = 0; i < data_str_new.length; i++) {
                        data_str_new_final[i] = data_str_new[i] + ",";
                    }
                    received_data_str_cal = convertStringArrayToAsciiString(data_str_new_final);
                    mainActivity.PrepareCommandDatasend(received_data_str_cal);
                    delayRoutine(10);
                    mainActivity.sendSettingCommand("?0002,00EE#");
                    if (mainActivity.received_data_string_new.equals("")){
                        mainActivity.sendSettingCommand("?0002,00EE#");
                    }
                    else{}
                    delayRoutine(100);
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

     private void save_data_Cal(){
        showProgressDialog3();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

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
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static String convertStringArrayToAsciiString(String[] stringArray) {

        StringBuilder sb = new StringBuilder();
        for (String str : stringArray) {

            sb.append(str);
        }

        return sb.toString().trim();
    }
    public void process_single_register_recv_data(String recvd_str) {
        int no_of_bytes = 0;
        String tmp = "";
        String[] data_str = new String[4];
        if (recvd_str.length() > 2) {
            String dev_addr = recvd_str.substring(0, 2);
            if (dev_addr.equals("F7")) {
                //address ok
                if (recvd_str.length() > 4) {
                    String ctyp = recvd_str.substring(3, 5);
                    if (ctyp.equals("03")) {
                        if (recvd_str.length() > 6) {
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
                                        editTextdata.setText((data_str[0] + data_str[1]));
                                        //return tmporary;
                                    }
                                    else
                                    {
                                        editTextdata.setText("Comms fail");
                                    }
                                }
                            }
                            else
                            {
                                editTextdata.setText("Comms fail");
                            }
                        }
                        else
                        {
                            editTextdata.setText("Comms fail");
                        }
                    }
                    else
                    {
                        editTextdata.setText("Comms fail");
                    }
                }
            }
            else
            {
                editTextdata.setText("Comms fail");
            }
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
    public void showMessageBox(View view,String msg,String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // You can add code here to handle the positive button click (if needed)
               dialog.dismiss();
            }
        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // You can add code here to handle the negative button click (if needed)
//                dialog.dismiss();
//            }
//        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}