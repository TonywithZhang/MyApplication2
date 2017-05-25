package com.tec.zhang.prv.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.tec.zhang.prv.Abandon;
import com.tec.zhang.prv.R;

/**
 * Created by zhang on 2017/4/27.
 */

public class SearchWithProjectNumber extends Fragment {
    private TextView textView;
    private MultiAutoCompleteTextView multiAutoCompleteTextView;
    private FloatingActionButton confirm;

    private String[] words;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_with_project_number,container,false);
        textView = (TextView) view.findViewById(R.id.textView3);
        multiAutoCompleteTextView = (MultiAutoCompleteTextView) view.findViewById(R.id.auto_complete);
        words = new String[]{
                "1","2","3","12","13","14","15","16"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,words);
        multiAutoCompleteTextView.setAdapter(adapter);
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        multiAutoCompleteTextView.addTextChangedListener(watcher);

        confirm = (FloatingActionButton) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //confirmCheck();
                Intent intent = new Intent(getActivity(), Abandon.class);
                startActivity(intent);
            }
        });
        return view;

    }

    private void confirmCheck() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setCancelable(true)
                .setIcon(R.drawable.ic_warning_outline_white)
                .setMessage("您要查找的项目号为" + multiAutoCompleteTextView.getText().toString())
                .setPositiveButton("确定",new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String projectNumber = multiAutoCompleteTextView.getText().toString();
            int length = projectNumber.length();
            if (length != 8){
                textView.setText("位数不对,目前已输入" + length + "位");
            }
        }
    };
}
