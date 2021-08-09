package com.example.taskso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends AppCompatActivity {

    private RecyclerView _taskList;
    private Button _btnCreate, _btnManage;
    private GroupAdapter _adapter;
    private ArrayList<String> _groupIds = new ArrayList<>();
    private ArrayList<String> _groupNames = new ArrayList<>();
    private SQLiteHandler _dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        _btnCreate = findViewById(R.id.btnCreate);
        _btnManage = findViewById(R.id.btnManage);

        _taskList = findViewById(R.id.recyclerView_TaskList);
        _taskList.setLayoutManager(new LinearLayoutManager(this));
        _taskList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        _dbh = new SQLiteHandler(this);
        Cursor groupsData = _dbh.getGroupList();
        if (groupsData.getCount() > 0) {
            while (groupsData.moveToNext()) {
                _groupIds.add(groupsData.getString(0));
                _groupNames.add(groupsData.getString(1));
            }
        }

        _btnManage.setEnabled(_groupIds.size() != 0);
        _adapter = new GroupAdapter(_groupIds, _groupNames, _dbh, _btnCreate.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        _taskList.setAdapter(_adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        _adapter = new GroupAdapter(_groupIds, _groupNames, _dbh, _btnCreate.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        _taskList.setAdapter(_adapter);
    }

    public void button_Create_onClick(View view) {
        // Get prompts.xml view
        View promptsView = LayoutInflater.from(this).inflate(R.layout.prompts, null);

        // Get EditText object
        final EditText userInput = (EditText)promptsView.findViewById(R.id.editText_UserInput);
        userInput.setHint(getNewGroupName(getString(R.string.text_defaultName_Group)));
        userInput.setSelection(userInput.getText().length());

        // Set prompt Hints
        TextView promptHints = (TextView)promptsView.findViewById(R.id.textView_prompt);
        promptHints.setText(getString(R.string.btn_promptGroupCreate));

        // Set up AlertDialog with EditText
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.btn_create),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String newGroupName = userInput.getText().toString().trim();
                                _adapter.addGroup(newGroupName.length() == 0 ? getNewGroupName(getString(R.string.text_defaultName_Group)) : getNewGroupName(newGroupName));
                                _btnManage.setEnabled(true);
                            }
                        })
                .setNegativeButton(getString(R.string.btn_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // Show the AlertDialog with EditText
        alertDialogBuilder.create().show();
    }

    public void button_Manage_onClick(View view) {
        String strManage = getString(R.string.btn_manage);
        _btnManage.setText(_btnManage.getText() == strManage ? getString(R.string.btn_back) : strManage);
        _btnCreate.setVisibility(_btnManage.getText() == strManage ? View.VISIBLE : View.GONE);

        List<String> groupIds = _adapter.getGroupIds();
        _btnManage.setEnabled(groupIds.size() != 0);
        _adapter = new GroupAdapter(groupIds, _adapter.getGroupNames(), _dbh, _btnCreate.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        _taskList.setAdapter(_adapter);
    }

    private String getNewGroupName(String newName) {
        if (!_groupNames.contains(newName)) {
            return newName;
        }

        int i = 1;
        String newGroupName;
        do {
            newGroupName = newName + " (" + i++ + ")";
        }
        while (_groupNames.contains(newGroupName));

        return newGroupName;
    }
}
