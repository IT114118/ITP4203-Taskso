package com.example.taskso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskStarredListActivity extends AppCompatActivity {

    private RecyclerView _taskList;
    private TaskStarredAdapter _adapter;
    private TextView _textView_starHints;
    private ArrayList<String> _taskIds = new ArrayList<>();
    private ArrayList<String> _taskNames = new ArrayList<>();
    private SQLiteHandler _dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_starred_list);

        _taskList = findViewById(R.id.recyclerView_TaskStarredList);
        _taskList.setLayoutManager(new LinearLayoutManager(this));
        _taskList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        _dbh = new SQLiteHandler(this);
        Cursor tasksData = _dbh.getAllTasks();
        if (tasksData.getCount() > 0) {
            while (tasksData.moveToNext()) {
                String taskCheck = tasksData.getString(2);
                String taskStar = tasksData.getString(3);

                if (taskCheck.equals("0") && taskStar.equals("1")) {
                    _taskIds.add(tasksData.getString(0));
                    _taskNames.add(tasksData.getString(1));
                }
            }
        }

        _textView_starHints = findViewById(R.id.textView_starHints);
        _textView_starHints.setVisibility(_taskIds.size() == 0 ? View.VISIBLE : View.GONE);

        _adapter = new TaskStarredAdapter(_taskIds, _taskNames, _dbh);
        _taskList.setAdapter(_adapter);
    }
}
