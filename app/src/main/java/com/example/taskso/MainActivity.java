package com.example.taskso;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void button_ViewGroupList_onClick(View view) {
        Intent intent = new Intent(MainActivity.this, GroupListActivity.class);
        startActivity(intent);
    }

    public void button_ViewStarredTasks_onClick(View view) {
        Intent intent = new Intent(MainActivity.this, TaskStarredListActivity.class);
        startActivity(intent);
    }

    public void button_ViewStatistics_onClick(View view) {
        Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
        startActivity(intent);
    }
}
