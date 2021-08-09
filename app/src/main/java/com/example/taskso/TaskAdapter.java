package com.example.taskso;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private String _groupId;
    private List<String> _taskIds, _taskNames;
    private SQLiteHandler _dbh;
    private int _visible;
    private ProgressBar _progressBar_TaskCompletion;
    private int currentItem, checkedCount = 0;

    public TaskAdapter(String groupId, List<String> taskIds, List<String> taskNames, SQLiteHandler dbh, int visible, ProgressBar progressBar_TaskCompletion) {
        _groupId = groupId;
        _taskIds = taskIds;
        _taskNames = taskNames;
        _dbh = dbh;
        _visible = visible;
        _progressBar_TaskCompletion = progressBar_TaskCompletion;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tV1;
        private Button btnDel;
        private CheckBox checkBox_Task, checkBox_Star;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tV1 = itemView.findViewById(R.id.tV1);
            btnDel = itemView.findViewById(R.id.btnDel);
            checkBox_Task = itemView.findViewById(R.id.checkBox_Task);
            checkBox_Star = itemView.findViewById(R.id.checkBox_Star);

            btnDel.setVisibility(_visible);
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnDel.setVisibility(removeTask(getAdapterPosition()) ? View.GONE : View.VISIBLE);
                    if (checkBox_Task.isChecked()) {
                        checkedCount--;
                    }
                    _progressBar_TaskCompletion.setProgress(_taskIds.size() <= 0 ? 0 : (int)(checkedCount * 100 / _taskIds.size()));
                }
            });

            checkBox_Task.setVisibility(_visible == View.VISIBLE ? View.GONE : View.VISIBLE);
            checkBox_Star.setVisibility(_visible == View.VISIBLE ? View.GONE : View.VISIBLE);

            if (getTaskChecked(currentItem)) {
                checkBox_Task.setChecked(true); // cannot getAdapterPosition()
                checkedCount++;
                _progressBar_TaskCompletion.setProgress(_taskIds.size() <= 0 ? 0 : (int)(checkedCount * 100 / _taskIds.size()));
            }

            if (_visible == View.GONE) {
                setStrikethoughText();
                checkBox_Star.setChecked(getTaskStarred(currentItem));

                // On group list item click
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = !checkBox_Task.isChecked();
                        setTaskChecked(getAdapterPosition(), checked);
                        checkBox_Task.setChecked(checked);
                        setStrikethoughText();
                        checkedCount += (checked) ? 1 : -1;
                        _progressBar_TaskCompletion.setProgress(_taskIds.size() <= 0 ? 0 : (int)(checkedCount * 100 / _taskIds.size()));
                    }
                });

                checkBox_Task.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTaskChecked(getAdapterPosition(), checkBox_Task.isChecked());
                        setStrikethoughText();
                        checkedCount += (checkBox_Task.isChecked()) ? 1 : -1;
                        _progressBar_TaskCompletion.setProgress(_taskIds.size() <= 0 ? 0 : (int)(checkedCount * 100 / _taskIds.size()));
                    }
                });

                checkBox_Star.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTaskStarred(getAdapterPosition(), checkBox_Star.isChecked());
                    }
                });
            }

            currentItem++;
        }

        private void setStrikethoughText() {
            int paintFlags = tV1.getPaintFlags();
            paintFlags = checkBox_Task.isChecked() ? paintFlags | Paint.STRIKE_THRU_TEXT_FLAG : paintFlags & ~Paint.STRIKE_THRU_TEXT_FLAG;
            tV1.setPaintFlags(paintFlags);
        }
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_task, parent, false);
        return new TaskAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        holder.tV1.setText(_taskNames.get(position));
    }

    @Override
    public int getItemCount() {
        return _taskNames.size();
    }

    public boolean removeTask(int position) {
        if (_dbh.deleteTask(_taskIds.get(position))) {
            _taskIds.remove(position);
            _taskNames.remove(position);
            notifyItemRemoved(position);
            return true;
        }

        return false;
    }

    public boolean addTask(String taskName) {
        int taskId = _dbh.createTask(_groupId, taskName);
        if (taskId == -1) {
            return false;
        }

        int newPosition = _taskNames.size();
        _taskIds.add(newPosition, String.valueOf(taskId));
        _taskNames.add(newPosition, taskName);
        notifyItemInserted(newPosition);
        _progressBar_TaskCompletion.setProgress(_taskIds.size() <= 0 ? 0 : (int)(checkedCount * 100 / _taskIds.size()));

        return true;
    }

    public boolean getTaskChecked(int position) {
        return _dbh.getTaskChecked(_taskIds.get(position));
    }

    public boolean setTaskChecked(int position, boolean isChecked) {
        return _dbh.setTaskChecked(_taskIds.get(position), isChecked);
    }

    public boolean getTaskStarred(int position) {
        return _dbh.getTaskStarred(_taskIds.get(position));
    }

    public boolean setTaskStarred(int position, boolean isChecked) {
        return _dbh.setTaskStarred(_taskIds.get(position), isChecked);
    }

    public List<String> getTaskIds() {
        return _taskIds;
    }

    public List<String> getTaskNames() {
        return _taskNames;
    }
}
