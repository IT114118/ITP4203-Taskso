package com.example.taskso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private String _groupId;
    private RecyclerView _taskList;
    private ProgressBar _progressBar_TaskCompletion;
    private Button _btnCreate, _btnManage;
    private ImageView _imageView_Circle;
    private TextView _textView_GroupName;
    private TaskAdapter _adapter;
    private ArrayList<String> _taskIds = new ArrayList<>();
    private ArrayList<String> _taskNames = new ArrayList<>();
    private SQLiteHandler _dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        _btnCreate = findViewById(R.id.btnCreate);
        _btnManage = findViewById(R.id.btnManage);

        _textView_GroupName = findViewById(R.id.textView_GroupName);
        _textView_GroupName.setText(getIntent().getStringExtra("GROUP_NAME"));

        _taskList = findViewById(R.id.recyclerView_TaskList);
        _taskList.setLayoutManager(new LinearLayoutManager(this));
        _taskList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        _groupId = getIntent().getStringExtra("GROUP_ID");

        _progressBar_TaskCompletion = findViewById(R.id.progressBar_TaskCompletion);

        int tasksChecked = 0;
        _dbh = new SQLiteHandler(this);
        Cursor tasksData = _dbh.getTaskList(_groupId);
        if (tasksData.getCount() > 0) {
            while (tasksData.moveToNext()) {
                _taskIds.add(tasksData.getString(0));
                _taskNames.add(tasksData.getString(1));
                if (tasksData.getString(2).equals("1")) {
                    tasksChecked++;
                }
            }
        }

        if (_taskIds.size() != 0) {
            _progressBar_TaskCompletion.setProgress((int) (tasksChecked * 100 / _taskIds.size()));
        }

        _imageView_Circle = findViewById(R.id.imageView_Circle);
        switch (_dbh.getGroupColor(_groupId)) {
            case "red": _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_red)); break;
            case "orange": _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_orange)); break;
            case "yellow": _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_yellow)); break;
            case "green": _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_green)); break;
            case "blue": _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_blue)); break;
            case "indigo": _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_indigo)); break;
            case "purple": _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_purple)); break;
        }

        _btnManage.setEnabled(_taskIds.size() != 0);
        _adapter = new TaskAdapter(_groupId, _taskIds, _taskNames, _dbh, _btnCreate.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE, _progressBar_TaskCompletion);
        _taskList.setAdapter(_adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_set_theme, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_color_red:
                _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_red));
                _dbh.setGroupColor(_groupId, "red");
                break;
            case R.id.item_color_orange:
                _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_orange));
                _dbh.setGroupColor(_groupId, "orange");
                break;
            case R.id.item_color_yellow:
                _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_yellow));
                _dbh.setGroupColor(_groupId, "yellow");
                break;
            case R.id.item_color_green:
                _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_green));
                _dbh.setGroupColor(_groupId, "green");
                break;
            case R.id.item_color_blue:
                _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_blue));
                _dbh.setGroupColor(_groupId, "blue");
                break;
            case R.id.item_color_indigo:
                _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_indigo));
                _dbh.setGroupColor(_groupId, "indigo");
                break;
            case R.id.item_color_purple:
                _imageView_Circle.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_purple));
                _dbh.setGroupColor(_groupId, "purple");
                break;
        }
        return true;
    }

    public void button_Create_onClick(View view) {
        // Get prompts.xml view
        View promptsView = LayoutInflater.from(this).inflate(R.layout.prompts, null);

        // Get EditText object
        final EditText userInput = (EditText)promptsView.findViewById(R.id.editText_UserInput);
        userInput.setHint(getNewTaskName(getString(R.string.text_defaultName_Task)));
        userInput.setSelection(userInput.getText().length());

        // Set prompt Hints
        TextView promptHints = (TextView)promptsView.findViewById(R.id.textView_prompt);
        promptHints.setText(getString(R.string.btn_promptTaskCreate));

        // Set up AlertDialog with EditText
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.btn_create),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String newTaskName = userInput.getText().toString().trim();
                                _adapter.addTask(newTaskName.length() == 0 ? getNewTaskName(getString(R.string.text_defaultName_Task)) : getNewTaskName(newTaskName));

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

        List<String> taskIds = _adapter.getTaskIds();
        _btnManage.setEnabled(taskIds.size() != 0);
        _adapter = new TaskAdapter(_groupId, taskIds, _adapter.getTaskNames(), _dbh, _btnCreate.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE, _progressBar_TaskCompletion);
        _taskList.setAdapter(_adapter);
    }

    private String getNewTaskName(String newName) {
        if (!_taskNames.contains(newName)) {
            return newName;
        }

        int i = 1;
        String newTaskName;
        do {
            newTaskName = newName + " (" + i++ + ")";
        }
        while (_taskNames.contains(newTaskName));

        return newTaskName;
    }
}
