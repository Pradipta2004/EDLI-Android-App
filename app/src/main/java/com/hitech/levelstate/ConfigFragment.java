package com.hitech.levelstate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.io.IOException;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText editTextConductivity,editTextSCResistance,editTextSystemFaultTimeDelay,editTextConductivity1,editTextLastremoteaddr,editTextSystemFaultTimeDelay1,editTextnoofchannel,editTextnoofground;
    String[] data_str_new = new String[100];
    int[] sensingTime = new int[5];
    int[] steamSenseVal = new int[5];
    int[] waterSenseVal = new int[5];
    int[] contaminationHigh = new int[5];
    int[] contaminationLow = new int[5];
    int[] openCktDiffVal = new int[5];
    int[] shortCktVal = new int[5];
    int VerticalValidation = 0;
    int no_of_channels=0;
    int myString;
    int Level_Check;
    int Voting_Check;
    int Contamination_check;
    int ShortCircuit_check;
    int Process_Flt_check;
    int Pwr_Flt_check;
    int Sensitivity_selection;
    int mA_steam_mode;
    int Last_remote_addr;
    int Number_of_grounds;
    int Total_no_of_channels;
    int Fault_Relay_Timer;
    int AutoLevel;
    String[] data_str_new_admin = new String[100];

    private static final int MAX_RETRIES = 6; // Maximum number of retry attempts
    private static final int RETRY_DELAY_MS = 2000; // Delay between retries in milliseconds
    String eeprom_write_data = "0001";
    Spinner spinnerSC,spinnerOC,spinnerVV,spinnerContamination,spinnerSC1,spinnerLevelChk,spinnerVotingChk,spinnerContamination1,spinnerPwrFlt,spinnerProcessFlt,spinnermAmode,spinnerautoLvl;
    Button buttonSave, buttonWrite,buttonSave1, buttonWrite1,buttonReset;
    String selectedItemSC, selectedItemOC, selectedItemVV, selectedItemContamination,selectedItemSC1, selectedItemLevelcheck, selectedItemVotingcheck, selectedItemContamination1,selectedItemPwrFlt,selectedItemProcessFlt,selectedItemmAsteammode,selectedItemautolvl;
    ProgressDialog progressDialog;
    private Handler handler = new Handler();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String[] options = {"Yes", "No"};
    Context mContext;
    public static final String store_received_login_data= "bluetoothData";
    public static final String PREFS_NAME = "MyPrefsFile";
    String received_data_str="";
    int system_fault_delay;
    MainActivity mainActivity;
    public ConfigFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfigFragment newInstance(String param1, String param2) {
        ConfigFragment fragment = new ConfigFragment();
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
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        MainActivity mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
             myString = mainActivity.getMyString();


            if (myString == 0) {
                view = inflater.inflate(R.layout.fragment_config, container, false);
                // mainActivity = (MainActivity) getActivity();
                fragmentconfig(view);
            } else {
                view = inflater.inflate(R.layout.fragment_config_new, container, false);
                fragmentconfignew(view);
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
                        if(myString==0) {
                            if (!read_operation("0021", "0009")) {
                                 int processFlag = process_command_set1(mainActivity.received_data_string);
                                while (processFlag == 0) {
                                    read_operation("0021", "0009");
                                    delayRoutine(100);
                                    processFlag = process_command_set1(mainActivity.received_data_string);
                                }
                            }

                            if (read_operation("002A", "0008")) {
                                int processFlag = process_command_set2(mainActivity.received_data_string);
                                while (processFlag == 0) {
                                    read_operation("002A", "0008");
                                    delayRoutine(100);
                                    processFlag = process_command_set2(mainActivity.received_data_string);
                                }
                            }

                            if (read_operation("0032", "0008")) {
                                int processFlag = process_command_set3(mainActivity.received_data_string);
                                while (processFlag == 0) {
                                    read_operation("0032", "0008");
                                    delayRoutine(100);
                                    processFlag = process_command_set3(mainActivity.received_data_string);
                                }
                            }
                            if (read_operation("0043", "0001")) {
                                no_of_channels = get_no_of_channels(mainActivity.received_data_string);
                                while (no_of_channels == 0) {
                                    read_operation("0043", "0001");
                                    delayRoutine(100);
                                    no_of_channels = get_no_of_channels(mainActivity.received_data_string);
                                }
                            }

                            if (read_operation("003F", "0001")) {
                                VerticalValidation = Check_vertical_validation(mainActivity.received_data_string);
                                if (VerticalValidation == 0) {
                                    read_operation("003F", "0001");
                                    delayRoutine(100);
                                    VerticalValidation = Check_vertical_validation(mainActivity.received_data_string);
                                }
                            }

                            if (read_operation("003A", "0004")) {
                                int processFlag = process_command_set4(mainActivity.received_data_string);
                                while (processFlag == 0) {
                                    read_operation("003A", "0004");
                                    delayRoutine(100);
                                    processFlag = process_command_set4(mainActivity.received_data_string);
                                }
                            }
                        }
                        if(myString==1){
                            mainActivity.PrepareReadCommandNew("0001");
                            int retryCount = 0;
                            boolean success = false;

                            while (retryCount < MAX_RETRIES && !success) {
                            try {
                                mainActivity.PrepareReadCommandNew("0001");
                                int eeprom_received_data = process_store_eeprom_data(mainActivity.received_data_string_new);
                                    // Handle the received data
                                    if(eeprom_received_data==1){
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

//                            for (int i = 0; i < 4; i++) {
//                                data_str_new[i] = data_str_new[i] + ",";
//                            }
//                             received_data_str = Arrays.toString(data_str_new);
//                            try {
//                              //  mainActivity.receiveResponse();
//                            } catch (Exception e) {
//                                throw new RuntimeException(e);
//                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (myString == 0) {
                                    delayRoutine(100);
                                    populate_details(view);
                                    //cardContainer.invalidate();
                                    //delayRoutine(100);
                                    hideProgressDialog();
                                } else {
                                    populate_details_new(view);
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

    private void fragmentconfig(View view) {
                mainActivity = (MainActivity) getActivity();
                // Initialize layout one components and set up listeners
                editTextConductivity = view.findViewById(R.id.editTextConductivity);
                editTextSCResistance = view.findViewById(R.id.editTextSCresistance);
                editTextSystemFaultTimeDelay = view.findViewById(R.id.editTextSystemFault);

                spinnerOC = view.findViewById(R.id.spinnerOC);
                spinnerSC = view.findViewById(R.id.spinnerSC);
                spinnerVV = view.findViewById(R.id.spinnerVerticalValidation);
                spinnerContamination = view.findViewById(R.id.spinnerContamination);
                buttonWrite = view.findViewById(R.id.buttonWriteSettings);
                buttonSave = view.findViewById(R.id.buttonSaveSettings);
                spinnerSC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // Handle item selection
                        selectedItemSC = (String) parentView.getItemAtPosition(position);
                        // performActionBasedOnSCSelection(selectedItem);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Do nothing
                    }
                });
                spinnerOC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // Handle item selection
                        selectedItemOC = (String) parentView.getItemAtPosition(position);
                        // performActionBasedOnOCSelection(selectedItem);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Do nothing
                    }
                });
                spinnerVV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // Handle item selection
                        selectedItemVV = (String) parentView.getItemAtPosition(position);
                        // performActionBasedOnVVSelection(selectedItem);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Do nothing
                    }
                });

                spinnerContamination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // Handle item selection
                        selectedItemContamination = (String) parentView.getItemAtPosition(position);
                        //  performActionBasedOnContaminationSelection(selectedItem);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Do nothing
                    }
                });

                buttonWrite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        write_data();
                    }
                });
                buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        save_data();
                    }
                });

            }
    private void fragmentconfignew(View view) {
        mainActivity = (MainActivity) getActivity();
        // Initialize layout one components and set up listeners
        editTextConductivity1 = view.findViewById(R.id.editTextConductivity1);
        editTextLastremoteaddr = view.findViewById(R.id.editTextLastRemoteAddr);
        editTextSystemFaultTimeDelay1 = view.findViewById(R.id.editTextSystemFault1);
        editTextnoofchannel = view.findViewById(R.id.editTextChannels);
        editTextnoofground = view.findViewById(R.id.editTextGroundCount);
        spinnerVotingChk = view.findViewById(R.id.spinnerVotingCheck);
        spinnerSC1 = view.findViewById(R.id.spinnerSC1);
        spinnerLevelChk= view.findViewById(R.id.spinnerLevelCheck);
        spinnerContamination1 = view.findViewById(R.id.spinnerContamination1);
        spinnerPwrFlt = view.findViewById(R.id.spinnerPWRFLT);
        spinnerProcessFlt = view.findViewById(R.id.spinnerProcFlt);
        spinnermAmode = view.findViewById(R.id.spinner4_20mA);
        spinnerautoLvl = view.findViewById(R.id.spinnerautolevel);
        buttonWrite1 = view.findViewById(R.id.buttonWriteSettings2);
        buttonSave1 = view.findViewById(R.id.buttonSaveSettings2);
        buttonReset = view.findViewById(R.id.buttonFactoryReset);
        spinnerSC1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection
                selectedItemSC1 = (String) parentView.getItemAtPosition(position);
                // performActionBasedOnSCSelection(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        spinnerLevelChk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection
                selectedItemLevelcheck = (String) parentView.getItemAtPosition(position);
                // performActionBasedOnOCSelection(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        spinnerVotingChk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection
                 selectedItemVotingcheck = (String) parentView.getItemAtPosition(position);
                // performActionBasedOnVVSelection(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        spinnerContamination1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection
                selectedItemContamination1 = (String) parentView.getItemAtPosition(position);
                //  performActionBasedOnContaminationSelection(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        spinnerProcessFlt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection
                selectedItemProcessFlt = (String) parentView.getItemAtPosition(position);
                // performActionBasedOnSCSelection(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        spinnerPwrFlt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection
                selectedItemPwrFlt = (String) parentView.getItemAtPosition(position);
                // performActionBasedOnSCSelection(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        spinnermAmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection
                selectedItemmAsteammode = (String) parentView.getItemAtPosition(position);
                // performActionBasedOnSCSelection(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        spinnerautoLvl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection
               selectedItemautolvl = (String) parentView.getItemAtPosition(position);
                // performActionBasedOnSCSelection(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        buttonWrite1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write_data_new();
            }
        });
        buttonSave1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_data_new();
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_data();
            }
        });
    }

    private void populate_details_new(View view){
      editTextSystemFaultTimeDelay1.setText(""+ Fault_Relay_Timer);

      editTextnoofchannel.setText(""+Total_no_of_channels);

      editTextnoofground.setText(""+Number_of_grounds);

      if(Sensitivity_selection == 1){
          editTextConductivity1.setText(""+"0.5");
      }
      else if(Sensitivity_selection == 2){
            editTextConductivity1.setText(""+"1");

      }
      else if(Sensitivity_selection == 3){
            editTextConductivity1.setText(""+"2");

      }


      editTextLastremoteaddr.setText(""+Last_remote_addr);

      editTextSystemFaultTimeDelay1.setText(""+Fault_Relay_Timer);

      if(ShortCircuit_check==1){
          spinnerSC1.setSelection(1);
      }
      if(ShortCircuit_check==0){
          spinnerSC1.setSelection(0);
      }

      if(Voting_Check==1){
          spinnerVotingChk.setSelection(0);
      }
        if(Voting_Check==0){
            spinnerVotingChk.setSelection(1);
        }

        if(Level_Check==1){
            spinnerLevelChk.setSelection(0);
        }
        if(Level_Check==0){
            spinnerLevelChk.setSelection(1);
        }

        if(Contamination_check==1){
            spinnerContamination1.setSelection(1);
        }
        if(Contamination_check==0){
            spinnerContamination1.setSelection(0);
        }

        if(Process_Flt_check==1){
         spinnerProcessFlt.setSelection(1);
        }
        if(Process_Flt_check==0){
            spinnerProcessFlt.setSelection(0);
        }

        if(Pwr_Flt_check==1){
           spinnerPwrFlt.setSelection(1);
        }
        if(Pwr_Flt_check==0){
            spinnerPwrFlt.setSelection(0);
        }

        if(mA_steam_mode==1){
           spinnermAmode.setSelection(0);
        }
        if(mA_steam_mode==0){
            spinnermAmode.setSelection(1);
        }
        if(AutoLevel==1){
            spinnerautoLvl.setSelection(0);
        }
        if(AutoLevel==0){
            spinnerautoLvl.setSelection(1);
        }
    }
    private void populate_details(View view) {

                    editTextSystemFaultTimeDelay.setText("" + system_fault_delay);
                    editTextSCResistance.setText("" + shortCktVal[1]);
                    if (waterSenseVal[0] == 1370) {
                        editTextConductivity.setText("" + 2);
                    }
                    if (waterSenseVal[0] == 1470) {
                        editTextConductivity.setText("" + 1);
                    }
                    if (waterSenseVal[0] == 1509) {
                        editTextConductivity.setText("" + 0.5);
                    }
                    if (contaminationHigh[0] == 30 && contaminationLow[0] == 11) {
                        spinnerContamination.setSelection(0);
                    }
                    if (contaminationHigh[0] == 0 && contaminationLow[0] == 2000)
                    {
                        spinnerContamination.setSelection(1);
                    }
                    if (shortCktVal[1] == 10) {
                        spinnerSC.setSelection(0);
                    }
                    if(shortCktVal[1] == 0){
                        spinnerSC.setSelection(1);
                    }
                    if (openCktDiffVal[0] == 50) {
                        spinnerOC.setSelection(0);
                    }
                    if (openCktDiffVal[0] == 2000) {
                        spinnerOC.setSelection(1);
                    }
                    if (VerticalValidation == 1) {
                        spinnerVV.setSelection(0);
                    }
                    if (VerticalValidation == 0) {
                        spinnerVV.setSelection(1);
                    }

            }

    private void showProgressDialog2() {
        progressDialog = ProgressDialog.show(requireContext(), "Fetching Data from device", "Please wait...", true, false);
    }
    private void showProgressDialog1() {
        progressDialog = ProgressDialog.show(requireContext(), "Writting to Device", "Please wait...", true, false);
    }
    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(requireContext(), "Saving", "Please wait...", true, false);
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void write_data_new(){
        showProgressDialog1();
        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
        if(selectedItemLevelcheck.equals("Yes")){
            Level_Check = 1;
        }
        if(selectedItemLevelcheck.equals("No")){
            Level_Check = 0;
        }
        if(selectedItemVotingcheck.equals("Yes")){
            Voting_Check = 1;
        }
        if(selectedItemVotingcheck.equals("No")) {
            Voting_Check = 0;
        }
        if(selectedItemautolvl.equals("Yes")){
            AutoLevel = 1;
        }
        if(selectedItemautolvl.equals("No")){
            AutoLevel = 0;
        }
        data_str_new[7] =String.valueOf(Level_Check) + String.valueOf(Voting_Check) + String.valueOf(AutoLevel) + data_str_new[7].substring(3,4);
        if(selectedItemContamination1.equals("Yes")){
           Contamination_check=0;
        }
        if(selectedItemContamination1.equals("No"))
        {
            Contamination_check=1;
        }
        if(selectedItemSC1.equals("Yes")){
            ShortCircuit_check = 0;
        }
        if(selectedItemSC1.equals("No"))
        {
            ShortCircuit_check = 1;
        }
        if(selectedItemProcessFlt.equals("Yes"))
        {
            Process_Flt_check = 0;
        }
        if(selectedItemProcessFlt.equals("No"))
        {
            Process_Flt_check = 1;
        }
        data_str_new[8] = String.valueOf(Contamination_check) + String.valueOf(ShortCircuit_check) + data_str_new[8].substring(2,3) + String.valueOf(Process_Flt_check);
        if(selectedItemPwrFlt.equals("Yes")){
            Pwr_Flt_check = 0;
        }
        if(selectedItemPwrFlt.equals("No")){
            Pwr_Flt_check = 1;
        }
        if(selectedItemmAsteammode.equals("Yes")){
            mA_steam_mode = 1;
        }
        if(selectedItemmAsteammode.equals("No")){
            mA_steam_mode = 0;
        }

        String condStr = editTextConductivity1.getText().toString().trim();



        if (condStr.equals("0.5")) {
                condStr = "1";
                String tmp_SteamLvl;
                int tmp1 = 651;
                tmp_SteamLvl = Integer.toHexString(tmp1).toUpperCase();
                while (tmp_SteamLvl.length()<4){
                    tmp_SteamLvl = "0" + tmp_SteamLvl;
                }
                data_str_new[11] = tmp_SteamLvl;

                String tmp_WaterLvl;
                tmp1 = 470;
                tmp_WaterLvl = Integer.toHexString(tmp1).toUpperCase();
                while (tmp_WaterLvl.length()<4) tmp_WaterLvl = "0" + tmp_WaterLvl;
                data_str_new[12] = tmp_WaterLvl;



        } else if (condStr.equals("1")) {
                    condStr = "2";
                    String tmp_SteamLvl;
                    int tmp1 = 665;
                    tmp_SteamLvl = Integer.toHexString(tmp1).toUpperCase();
                    while (tmp_SteamLvl.length()<4){
                        tmp_SteamLvl = "0" + tmp_SteamLvl;
                    }
                    data_str_new[11] = tmp_SteamLvl;

                    String tmp_WaterLvl;
                    tmp1 = 660;
                    tmp_WaterLvl = Integer.toHexString(tmp1).toUpperCase();
                    while (tmp_WaterLvl.length()<4) tmp_WaterLvl = "0" + tmp_WaterLvl;
                    data_str_new[12] = tmp_WaterLvl;

           } else if (condStr.equals("2")) {
                    condStr = "3";
                    String tmp_SteamLvl;
                    int tmp1 = 420;
                    tmp_SteamLvl = Integer.toHexString(tmp1).toUpperCase();
                    while (tmp_SteamLvl.length()<4){
                        tmp_SteamLvl = "0" + tmp_SteamLvl;
                    }
                    data_str_new[11] = tmp_SteamLvl;

                    String tmp_WaterLvl;
                    tmp1 = 415;
                    tmp_WaterLvl = Integer.toHexString(tmp1).toUpperCase();
                    while (tmp_WaterLvl.length()<4) tmp_WaterLvl = "0" + tmp_WaterLvl;
                    data_str_new[12] = tmp_WaterLvl;
            }

                data_str_new[9]= String.valueOf(Pwr_Flt_check) + String.valueOf(condStr) + String.valueOf(mA_steam_mode) + String.valueOf(editTextLastremoteaddr.getText());
        String tmp_noofchannels ;
        String tmp_noofgrounds;
        String tmp_Flt_timer;
        if(editTextnoofchannel.getText().length()<2){
             tmp_noofchannels = "0" + (editTextnoofchannel.getText().toString());
        }else{
            String tmp = String.valueOf(editTextnoofchannel.getText());
            int tmp1 = Integer.parseInt(tmp);
            tmp_noofchannels = Integer.toHexString(tmp1).toUpperCase();
            while (tmp_noofchannels.length()<2)tmp_noofchannels = "0" + tmp_noofchannels;
        }
        if(editTextnoofground.getText().length()<2){
            tmp_noofgrounds = "0" + (editTextnoofground.getText().toString());
        }else{
            String tmp = String.valueOf(editTextnoofground.getText());
            int tmp1 = Integer.parseInt(tmp);
            tmp_noofgrounds = Integer.toHexString(tmp1).toUpperCase();
            while (tmp_noofgrounds.length()<2)tmp_noofgrounds = "0" + tmp_noofgrounds;
        }
        data_str_new[10] = tmp_noofgrounds + tmp_noofchannels;
        if(editTextSystemFaultTimeDelay1.getText().length()<2){
            tmp_Flt_timer= "00" + String.valueOf(editTextSystemFaultTimeDelay1.getText());
        }else{
            String tmp = String.valueOf(editTextSystemFaultTimeDelay1.getText());
            int tmp1 = Integer.parseInt(tmp);
            tmp_Flt_timer = Integer.toHexString(tmp1).toUpperCase();
             if(tmp_Flt_timer.length()<2){
                 tmp_Flt_timer = "00" + tmp_Flt_timer;
             }
             else{
                 tmp_Flt_timer = "0"+tmp_Flt_timer;
             }
        }
        data_str_new[62] = data_str_new[62].substring(0,1) + tmp_Flt_timer;
        String[] data_str_new_final = new String[data_str_new.length];
                for (int i = 0; i < data_str_new.length; i++) {
                        data_str_new_final[i] = data_str_new[i] + ",";
                        }
                received_data_str = convertStringArrayToAsciiString(data_str_new_final);
//                byte[] Asciibyte = received_data_str.getBytes(StandardCharsets.US_ASCII);
//                byte[] dataWithoutStartEnd = new byte[Asciibyte.length - 2];
//                System.arraycopy(Asciibyte, 1, dataWithoutStartEnd, 0, Asciibyte.length- 2);
//             String Final_receivedData = new String(dataWithoutStartEnd,StandardCharsets.US_ASCII);
 //               delayRoutine(100);
                mainActivity.PrepareCommandDatasend(received_data_str);
 //               mainActivity.sendCommand("|AA55,0000,3FAC,0500,425F,0500,425F,1110,0000,0101,0308,04B0,03E8,0064,0002,0004,0000,0000,0100,0100,0106,0108,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0002,0000,0000,0000,0000,0000,0000,0000,0000,0000!");
                delayRoutine(10);
                mainActivity.sendSettingCommand("?0002,00EE#");
                if (mainActivity.received_data_string_new.equals("")){
                    mainActivity.sendSettingCommand("?0002,00EE#");
                }
                else{}
            //    delayRoutine(200);
//                mainActivity.PrepareReadCommandNew("0005");
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


    private String stringToHex(String input) {
        StringBuilder hexString = new StringBuilder();
        for (char ch : input.toCharArray()) {
            String hex = Integer.toHexString((int) ch);
            hexString.append(hex);
        }
        return hexString.toString();
    }
    private void save_data_new(){
        showProgressDialog();
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

    private void write_data()
    {
        showProgressDialog1();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   if(selectedItemSC.equals("Yes")){
                       write_short_circuit_resistance();
                   }
                   if(selectedItemSC.equals("No")){
                       for (int i = 0; i < no_of_channels; i++) {
                           String registerNo = "00" + Integer.toHexString(58 + i);
                           String dataStr = "0000";
                           boolean x = write_operataion(registerNo, dataStr);
                           while (!x) {
                               x = write_operataion(registerNo, dataStr);

                           }
                           delayRoutine(100);
                       }
                   }
                   if(selectedItemOC.equals("Yes")){
                       for (int i = 0; i < no_of_channels; i++) {
                           String registerNo = "00" + Integer.toHexString(54 + i);
                           String dataStr = "0032";
                           boolean x = write_operataion(registerNo, dataStr);
                           while (!x) {
                               x = write_operataion(registerNo, dataStr);

                           }
                           delayRoutine(100);

                       }
                   }
                   if(selectedItemOC.equals("No")){
                       for (int i = 0; i < no_of_channels; i++) {
                           String registerNo = "00" + Integer.toHexString(54 + i);
                           String dataStr = "07D0";
                           boolean x = write_operataion(registerNo, dataStr);
                           while (!x) {
                               x = write_operataion(registerNo, dataStr);

                           }
                           delayRoutine(100);
                       }
                   }
                   if(selectedItemContamination.equals("Yes")){
                       write_contamination();
                   }
                   if(selectedItemContamination.equals("No")){
                       for (int i = 0; i < no_of_channels; i++) {
                           String registerNo = "00" + Integer.toHexString(46 + i);
                           String dataStr = "0000";
                           boolean x = write_operataion(registerNo, dataStr);
                           while (!x) {
                               x = write_operataion(registerNo, dataStr);
                           }
                           delayRoutine(100);
                       }
                       for (int i = 0; i < no_of_channels; i++) {
                           String registerNo = "00" + Integer.toHexString(50 + i);
                           String dataStr = "07D0";
                           boolean x = write_operataion(registerNo, dataStr);
                           while (!x) {
                               x = write_operataion(registerNo, dataStr);

                           }
                           delayRoutine(100);
                       }
                    }
                   if(selectedItemVV.equals("Yes")){
                       String registerNo = "00" + Integer.toHexString(63);
                       String dataStr = "0001";
                       boolean x = write_operataion(registerNo, dataStr);
                       while (!x) {
                           x = write_operataion(registerNo, dataStr);
                       }
                       delayRoutine(100);
                   }
                   if(selectedItemVV.equals("No")){
                      String registerNo = "00" + Integer.toHexString(63);
                      String dataStr = "0000";
                       boolean x = write_operataion(registerNo, dataStr);
                       while (!x) {
                           x = write_operataion(registerNo, dataStr);
                       }
                       delayRoutine(100);
                   }
                    String tmp = String.valueOf(editTextSystemFaultTimeDelay.getText());
                    int tmp1 = Integer.parseInt(tmp)*100;
                    String SF_Delay = Integer.toHexString(tmp1);
                    while (SF_Delay.length()<4)SF_Delay = "0" + SF_Delay;
                    boolean write_success = write_operataion("0021",SF_Delay);
                    while (!write_success) {
                        write_success = write_operataion("0021",SF_Delay);
                    }
                    delayRoutine(100);
                    String tmp2 = String.valueOf(editTextConductivity.getText());

                    if (tmp2.equals("2")){
                        for(int i=0; i<4; i++) {
                            String registerNo = "00" + Integer.toHexString(42 + i);
                            String dataStr = "05E5"; //055A
                            boolean x = write_operataion(registerNo, dataStr);
                            while (!x) {
                                x = write_operataion(registerNo, dataStr);
                            }
                            delayRoutine(100);
                        }
                    }
                    if (tmp2.equals("1")){
                        for(int i=0; i<4; i++) {
                            String registerNo = "00" + Integer.toHexString(42 + i);
                            String dataStr = "05BE";
                            boolean x = write_operataion(registerNo, dataStr);
                            while (!x) {
                                x = write_operataion(registerNo, dataStr);
                            }
                            delayRoutine(100);
                        }
                    }
                    if (tmp2.equals("0.5")){
                        for(int i=0; i<4; i++) {
                            String registerNo = "00" + Integer.toHexString(42 + i);
                            String dataStr = "055A";  //05E5
                            boolean x = write_operataion(registerNo, dataStr);
                            while (!x) {
                                x = write_operataion(registerNo, dataStr);
                            }
                            delayRoutine(100);
                        }
                    }

                    if(tmp2!="") {
                        for (int i = 0; i < 4; i++) {
                            String registerNo = "00" + Integer.toHexString(38 + i);
                            String dataStr = "05E6";
                            boolean x = write_operataion(registerNo, dataStr);
                            while (!x) {
                                x = write_operataion(registerNo, dataStr);
                            }
                            delayRoutine(100);
                        }
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
    private void reset_data(){
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mainActivity.sendSettingCommand("?0004,00F0#");
                    delayRoutine(300);
                    if (mainActivity.received_data_string_new.equals("")){
                        mainActivity.sendSettingCommand("?0004,00F0#");
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
    private void save_data()
    {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                 boolean   write_success = write_operataion("0010","0003");
                    //((MainActivity)getActivity()).prepare_write_command("0010","0003");

                    while (!write_success) {
                        write_success = write_operataion("0010", "0003");
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
    public int Check_vertical_validation(String recvd_str)
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

    private int process_command_set1(String recvd_str)
    {
        int no_of_bytes = 0;
        String tmp = "";
        String[] data_str = new String[20];
        if (recvd_str.length() < 1) return 0;
        String dev_addr = recvd_str.substring(0, 2);
        if (!dev_addr.equals("F7")) return 0;
        if (recvd_str.length() < 4) return 0;
        String ctyp = recvd_str.substring(3, 5);
        if (!ctyp.equals("03")) return 0;         //command type ok

        if (recvd_str.length() > 6) {
            no_of_bytes = mainActivity.hexToDec(recvd_str.substring(6, 8));
            if(no_of_bytes<18) return 0;
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
                        if(no_of_bytes>18) no_of_bytes=18;
                        for (int i = 0; i < no_of_bytes / 2; i++)
                        {
                            if (i == 0) {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                system_fault_delay = tmporary/100;
                            }
                            else if (i>0 && i<5) {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                sensingTime[i-1] = tmporary;
                            }
                            else if (i >= 5 && i < 9)
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                steamSenseVal[i-5] = tmporary;
                            }

                           // else if ( i>=9 && i<13){
                            //    int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                            //    VerticalValidation[i - 9] = tmporary;
                           // }
                         /*   else if(i>=13 && i<17)
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                contaminationHigh[i-13] = tmporary;
                            }
                            else if(i>=17 && i<21)
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                contaminationLow[i-17] = tmporary;
                            }
                            else if(i>=21 && i<25)
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                openCktDiffVal[i-21] = tmporary;
                            }
                            else
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                shortCktVal[i-24] = tmporary;
                            }
                            */

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

    private int process_command_set2(String recvd_str)
    {
        int no_of_bytes = 0;
        String tmp = "";
        String[] data_str = new String[18];
        if (recvd_str.length() < 1) return 0;
        String dev_addr = recvd_str.substring(0, 2);
        if (!dev_addr.equals("F7")) return 0;
        if (recvd_str.length() < 4) return 0;
        String ctyp = recvd_str.substring(3, 5);
        if (!ctyp.equals("03")) return 0;         //command type ok

        if (recvd_str.length() > 6) {
            no_of_bytes = mainActivity.hexToDec(recvd_str.substring(6, 8));
            if(no_of_bytes<16) return 0;
            int r_str_len = recvd_str.length();
            if (r_str_len > (11 + (no_of_bytes * 3))) {
                for (int i = 0; i < no_of_bytes / 2; i++) {
                    data_str[i * 2] = recvd_str.substring(9 + (i * 6), 9 + (i * 6) + 2);
                    data_str[i * 2 + 1] = recvd_str.substring(12 + (i * 6), 12 + (i * 6) + 2);
                    tmp += data_str[i * 2] + data_str[i * 2 + 1];
                }
                //String data_str = recvd_str.substring(9, 9 + no_of_bytes*3 + 1);
                String crc_recv = recvd_str.substring(9 + (no_of_bytes) * 3, 11 + (no_of_bytes) * 3) + recvd_str.substring(12 + (no_of_bytes) * 3, 14 + (no_of_bytes) * 3);


                byte[] hexBytes = mainActivity.hexStringToByteArray(dev_addr + ctyp + recvd_str.substring(6, 8) + tmp);
                int crcValue = mainActivity.calculateCRC(hexBytes);
                String crc_str = Integer.toHexString(crcValue).toUpperCase();

                if (crc_str.length() < 4) crc_str = "0" + crc_str;
                if (crc_str.length() == 4) {
                    crc_str = crc_str.substring(2, 4) + crc_str.substring(0, 2);
                    if (crc_str.equals(crc_recv)) {         //CRC OK
                        if(no_of_bytes>16) no_of_bytes=16;
                        for (int i = 0; i < no_of_bytes / 2; i++)
                        {

                            if ( i>=0 && i<=3){
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                waterSenseVal[i] = tmporary;

                            }
                            else if(i>3)
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                contaminationHigh[i-4] = tmporary;
                            }
                            /*
                            else if(i>=17 && i<21)
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                contaminationLow[i-17] = tmporary;
                            }
                            else if(i>=21 && i<25)
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                openCktDiffVal[i-21] = tmporary;
                            }
                            else
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                shortCktVal[i-24] = tmporary;
                            }
                            */

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

    private int process_command_set3(String recvd_str)
    {
        int no_of_bytes = 0;
        String tmp = "";
        String[] data_str = new String[18];
        if (recvd_str.length() < 1) return 0;
        String dev_addr = recvd_str.substring(0, 2);
        if (!dev_addr.equals("F7")) return 0;
        if (recvd_str.length() < 4) return 0;
        String ctyp = recvd_str.substring(3, 5);
        if (!ctyp.equals("03")) return 0;         //command type ok

        if (recvd_str.length() > 6) {
            no_of_bytes = mainActivity.hexToDec(recvd_str.substring(6, 8));
            if(no_of_bytes<16) return 0;
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
                        if(no_of_bytes>16) no_of_bytes=16;
                        for (int i = 0; i < no_of_bytes / 2; i++)
                        {
                            if(i<4)
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                contaminationLow[i] = tmporary;
                            }
                            else
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                openCktDiffVal[i-4] = tmporary;
                            }
                            /*
                            else
                            {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                shortCktVal[i-24] = tmporary;
                            }
                            */

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
    private int process_store_eeprom_data(String recvd_str_new) {
        int no_of_bytes = 0;
        String tmp = "";
        if (recvd_str_new.length() < 484) return 0;
        String crc_received = recvd_str_new.substring(480, 484);
        byte[] received_byte = recvd_str_new.substring(0,480).getBytes(StandardCharsets.US_ASCII);
        data_str_new = recvd_str_new.substring(0,480).split(",");
        int Calculatecrc = mainActivity.calculateChecksum(received_byte);
        String final_calculate_crc = mainActivity.checksumToAscii(Calculatecrc);
        if (final_calculate_crc.equals(crc_received)) {
            if(data_str_new[7].length()==4){
                  Level_Check = Integer.parseInt(data_str_new[7].substring(0,1));
                  Voting_Check = Integer.parseInt(data_str_new[7].substring(1,2));
                  AutoLevel = Integer.parseInt(data_str_new[7].substring(2,3));
              }
              else {
                  return 0;
              }
              if(data_str_new[8].length()==4){
                  Contamination_check = Integer.parseInt(data_str_new[8].substring(0,1));
                  ShortCircuit_check = Integer.parseInt(data_str_new[8].substring(1,2));
                  Process_Flt_check = Integer.parseInt(data_str_new[8].substring(3,4));
              }
              else{
                  return 0;
              }
              if(data_str_new[9].length()==4){
                  Pwr_Flt_check = Integer.parseInt(data_str_new[9].substring(0,1));
                  Sensitivity_selection = Integer.parseInt(data_str_new[9].substring(1,2));
                  mA_steam_mode = Integer.parseInt(data_str_new[9].substring(2,3));
                  Last_remote_addr = Integer.parseInt(data_str_new[9].substring(3,4));
              }
              else{
                  return 0;
              }
              if(data_str_new[10].length()==4){
                  Number_of_grounds = Integer.parseInt(data_str_new[10].substring(0,2),16);
                  Total_no_of_channels = Integer.parseInt(data_str_new[10].substring(2,4),16);
//                  String substring_channels = data_str_new[10].substring(3,4);
//                  no_of_channels = (int) substring_channels.charAt(0);
              }
              else{
                return 0;
              }
              if(data_str_new[62].length()==4){
                  Fault_Relay_Timer =  Integer.parseInt(data_str_new[62].substring(1,4),16);
              }
              else{
                  return 0;
              }
       return 1;
        } else {
            return 0;
        }

    }

    private int process_command_set4(String recvd_str)
    {
        int no_of_bytes = 0;
        String tmp = "";
        String[] data_str = new String[10];
        if (recvd_str.length() < 1) return 0;
        String dev_addr = recvd_str.substring(0, 2);
        if (!dev_addr.equals("F7")) return 0;
        if (recvd_str.length() < 4) return 0;
        String ctyp = recvd_str.substring(3, 5);
        if (!ctyp.equals("03")) return 0;         //command type ok

        if (recvd_str.length() > 6) {
            no_of_bytes = mainActivity.hexToDec(recvd_str.substring(6, 8));
            if(no_of_bytes<8) return 0;
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
                        if(no_of_bytes>8) no_of_bytes=8;
                        for (int i = 0; i < no_of_bytes / 2; i++)
                        {
                                int tmporary = Integer.parseInt(data_str[i * 2] + data_str[i * 2 + 1],16);
                                shortCktVal[i] = tmporary;
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
    private int process_command_set5(String recvd_str) {
        int no_of_bytes = 0;
        String tmp = "";
        String[] data_str = new String[512];
        return 1;
    }
    private boolean readoperation_eeprom(String data)
    {
        mainActivity.received_data_string_new ="";
        ((MainActivity)getActivity()).PrepareReadCommandNew(data);
        delayRoutine(300);

        Date cmdSendTime = Calendar.getInstance().getTime();
        int elapsed_time = 0;
        while (mainActivity.received_data_string_new.equals("") && elapsed_time<20) {
            Date currentTime = Calendar.getInstance().getTime();
            long millis = currentTime.getTime() - cmdSendTime.getTime();
            elapsed_time = (int) (millis / (1000));
        }
        if(mainActivity.received_data_string_new.equals(""))
        {
            return false;
        }
        else
        {
            return true;
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

    private void write_short_circuit_resistance()
    {
        for (int i = 0; i < no_of_channels; i++) {
            String registerNo = "00" + Integer.toHexString(58 + i);
            String dataStr = "000A";
            boolean x = write_operataion(registerNo, dataStr);
            while (!x) {
                x = write_operataion(registerNo, dataStr);
            }
            delayRoutine(100);
        }
    }
    private void write_contamination()
    {
        for (int i = 0; i < no_of_channels; i++) {
            String registerNo = "00" + Integer.toHexString(46 + i);
            String dataStr = "001E";
            boolean x = write_operataion(registerNo, dataStr);
            while (!x) {
                x = write_operataion(registerNo, dataStr);
            }
            delayRoutine(100);
        }
        for (int i = 0; i < no_of_channels; i++) {
            String registerNo = "00" + Integer.toHexString(50 + i);
            String dataStr = "000B";
            boolean x = write_operataion(registerNo, dataStr);
            while (!x) {
                x = write_operataion(registerNo, dataStr);

            }
            delayRoutine(100);
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
    private void performActionBasedOnSCSelection(String selectedItem) {
        switch (selectedItem) {
            case "Yes":
                write_short_circuit_resistance();
                break;

            case "No":
                for (int i = 0; i < no_of_channels; i++) {
                    String registerNo = "00" + Integer.toHexString(58 + i);
                    String dataStr = "0000";
                    boolean x = write_operataion(registerNo, dataStr);
                    while (!x) {
                        x = write_operataion(registerNo, dataStr);

                    }
                    delayRoutine(100);
                }
                break;
        }
    }
    private void performActionBasedOnContaminationSelection(String selectedItem) {
        switch (selectedItem) {
            case "Yes":
                write_contamination();
                break;

            case "No":
                for (int i = 0; i < no_of_channels; i++) {
                    String registerNo = "00" + Integer.toHexString(46 + i);
                    String dataStr = "0000";
                    boolean x = write_operataion(registerNo, dataStr);
                    while (!x) {
                        x = write_operataion(registerNo, dataStr);
                    }
                    delayRoutine(100);
                }
                for (int i = 0; i < no_of_channels; i++) {
                    String registerNo = "00" + Integer.toHexString(50 + i);
                    String dataStr = "07D0";
                    boolean x = write_operataion(registerNo, dataStr);
                    while (!x) {
                        x = write_operataion(registerNo, dataStr);

                    }
                    delayRoutine(100);
                }
        }
    }
    private void performActionBasedOnOCSelection(String selectedItem) {
        switch (selectedItem) {
            case "Yes":
                for (int i = 0; i < no_of_channels; i++) {
                    String registerNo = "00" + Integer.toHexString(54 + i);
                    String dataStr = "0032";
                    boolean x = write_operataion(registerNo, dataStr);
                    while (!x) {
                        x = write_operataion(registerNo, dataStr);

                    }
                    delayRoutine(100);

                }
                break;

            case "No":
                for (int i = 0; i < no_of_channels; i++) {
                    String registerNo = "00" + Integer.toHexString(54 + i);
                    String dataStr = "07D0";
                    boolean x = write_operataion(registerNo, dataStr);
                    while (!x) {
                        x = write_operataion(registerNo, dataStr);

                    }
                    delayRoutine(100);
                }
                break;
        }
    }
    private void performActionBasedOnVVSelection(String selectedItem) {
        switch (selectedItem) {
            case "Yes":
                    String registerNo = "00" + Integer.toHexString(63);
                    String dataStr = "0001";
                    boolean x = write_operataion(registerNo, dataStr);
                    while (!x) {
                        x = write_operataion(registerNo, dataStr);
                }
                delayRoutine(100);
                break;

            case "No":

                     registerNo = "00" + Integer.toHexString(63);
                     dataStr = "0000";
                     x = write_operataion(registerNo, dataStr);
                    while (!x) {
                        x = write_operataion(registerNo, dataStr);

                }
                    delayRoutine(100);
                break;
        }
    }

        }