package com.example.taskso;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class StatisticsActivity extends AppCompatActivity {

    private SQLiteHandler dbh;
    private TextView textView_GroupCount, textView_TaskCount, textView_Completed, textView_Incomplete, textView_CompletedRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        textView_GroupCount = findViewById(R.id.textView_GroupCount);
        textView_TaskCount = findViewById(R.id.textView_TaskCount);
        textView_Completed = findViewById(R.id.textView_Completed);
        textView_Incomplete = findViewById(R.id.textView_Incomplete);
        textView_CompletedRadio = findViewById(R.id.textView_CompletedRadio);

        dbh = new SQLiteHandler(this);
        textView_GroupCount.setText(String.valueOf(dbh.getGroupList().getCount()));

        Cursor tasksData = dbh.getAllTasks();
        int checked = 0, taskCount = tasksData.getCount();
        if (taskCount > 0) {
            while (tasksData.moveToNext()) {
                if (tasksData.getString(2).equals("1")) {
                    checked++;
                }
            }
        }
        textView_TaskCount.setText(String.valueOf(taskCount));
        textView_Completed.setText(String.valueOf(checked));
        textView_Incomplete.setText(String.valueOf(taskCount - checked));
        textView_CompletedRadio.setText((taskCount == 0) ? "NA" : String.valueOf(checked * 100 / taskCount) + "%");
    }
}
