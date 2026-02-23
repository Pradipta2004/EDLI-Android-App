package com.hitech.levelstate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChannelGridViewAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_TEXT_BOX = 0;
    private static final int VIEW_TYPE_SPINNER = 1;
    private static final int VIEW_TYPE_CHECK_BOX = 2;

    private List<String> dataList;
    private LayoutInflater inflater;

    public ChannelGridViewAdapter(Context context, List<String> dataList) {
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // Return the view type based on the position or other conditions
        // For example, you can use the position to determine the view type
        // For simplicity, we'll use an alternating pattern for this example
        return position % 3;
    }

    @Override
    public int getViewTypeCount() {
        return 3; // Three types: TextBox, Spinner, and CheckBox
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);

        if (convertView == null) {
            switch (viewType) {
                case VIEW_TYPE_TEXT_BOX:
                    convertView = inflater.inflate(R.layout.grid_item_text_box, parent, false);
                    break;
                case VIEW_TYPE_SPINNER:
                    convertView = inflater.inflate(R.layout.grid_item_spinner, parent, false);
                    break;
                case VIEW_TYPE_CHECK_BOX:
                    convertView = inflater.inflate(R.layout.grid_item_check_box, parent, false);
                    break;
            }
        }

        // Bind data based on view type
        switch (viewType) {
            case VIEW_TYPE_TEXT_BOX:
                EditText textBox = convertView.findViewById(R.id.editText);
                textBox.setText(dataList.get(position));
                break;
            case VIEW_TYPE_SPINNER:
                Spinner spinner = convertView.findViewById(R.id.spinner);
                // Set up the spinner data and adapter here
                break;
            case VIEW_TYPE_CHECK_BOX:
                CheckBox checkBox = convertView.findViewById(R.id.checkBox);
                checkBox.setText(dataList.get(position));
                break;
        }

        return convertView;
    }
}
