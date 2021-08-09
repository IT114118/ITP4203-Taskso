package com.example.taskso;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskStarredAdapter extends RecyclerView.Adapter<TaskStarredAdapter.ViewHolder> {
    private List<String> taskIds, taskNames;
    private SQLiteHandler dbh;

    public TaskStarredAdapter(List<String> taskIds, List<String> taskNames, SQLiteHandler dbh) {
        this.taskIds = taskIds;
        this.taskNames = taskNames;
        this.dbh = dbh;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tV1, tV2;
        private CheckBox checkBox_Task, checkBox_Star;
        private ImageView imageView_Circle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tV1 = itemView.findViewById(R.id.tV1);
            tV2 = itemView.findViewById(R.id.tV2);
            imageView_Circle = itemView.findViewById(R.id.imageView_Circle);

            checkBox_Task = itemView.findViewById(R.id.checkBox_Task);
            checkBox_Star = itemView.findViewById(R.id.checkBox_Star);
            checkBox_Star.setChecked(true);

            // On group list item click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTaskChecked(getAdapterPosition(), true);
                    checkBox_Task.setChecked(true);
                    setStrikethoughText();
                    removeTaskOnMenu(getAdapterPosition());
                }
            });

            checkBox_Task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTaskChecked(getAdapterPosition(), true);
                    setStrikethoughText();
                    removeTaskOnMenu(getAdapterPosition());
                }
            });

            checkBox_Star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTaskStarred(getAdapterPosition(), false);
                    removeTaskOnMenu(getAdapterPosition());
                }
            });
        }

        private void setStrikethoughText() {
            int paintFlags = tV1.getPaintFlags();
            paintFlags = checkBox_Task.isChecked() ? paintFlags | Paint.STRIKE_THRU_TEXT_FLAG : paintFlags & ~Paint.STRIKE_THRU_TEXT_FLAG;
            tV1.setPaintFlags(paintFlags);
        }
    }

    @NonNull
    @Override
    public TaskStarredAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_starredtask, parent, false);
        return new TaskStarredAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tV1.setText(taskNames.get(position));
        List<String> data = dbh.getGroupNameAndColorByTaskId(taskIds.get(position));
        holder.tV2.setText(data.get(0));

        switch (data.get(1)) {
            case "red": holder.imageView_Circle.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.circle_red)); break;
            case "orange": holder.imageView_Circle.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.circle_orange)); break;
            case "yellow": holder.imageView_Circle.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.circle_yellow)); break;
            case "green": holder.imageView_Circle.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.circle_green)); break;
            case "blue": holder.imageView_Circle.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.circle_blue)); break;
            case "indigo": holder.imageView_Circle.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.circle_indigo)); break;
            case "purple": holder.imageView_Circle.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.circle_purple)); break;
        }
    }

    @Override
    public int getItemCount() {
        return taskNames.size();
    }

    public void removeTaskOnMenu(int position) {
        taskIds.remove(position);
        taskNames.remove(position);
        notifyItemRemoved(position);
    }

    public boolean setTaskChecked(int position, boolean isChecked) {
        return dbh.setTaskChecked(taskIds.get(position), isChecked);
    }

    public boolean setTaskStarred(int position, boolean isChecked) {
        return dbh.setTaskStarred(taskIds.get(position), isChecked);
    }
}
