package com.example.taskso;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<String> _groupIds, _groupNames;
    private SQLiteHandler _dbh;
    private int _visible;
    private int currentItem = 0;

    public GroupAdapter(List<String> groupIds, List<String> groupNames, SQLiteHandler dbh, int visible) {
        _groupIds = groupIds;
        _groupNames = groupNames;
        _dbh = dbh;
        _visible = visible;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView_Circle;
        private TextView tV1;
        private Button btnDel;

        public ViewHolder(@NonNull View itemView, int visible) {
            super(itemView);
            imageView_Circle = itemView.findViewById(R.id.imageView_Circle);
            tV1 = itemView.findViewById(R.id.tV1);
            btnDel = itemView.findViewById(R.id.btnDel);
            btnDel.setVisibility(visible);

            switch (getGroupColor(currentItem++)) {
                case "red": imageView_Circle.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.circle_red)); break;
                case "orange": imageView_Circle.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.circle_orange)); break;
                case "yellow": imageView_Circle.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.circle_yellow)); break;
                case "green": imageView_Circle.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.circle_green)); break;
                case "blue": imageView_Circle.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.circle_blue)); break;
                case "indigo": imageView_Circle.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.circle_indigo)); break;
                case "purple": imageView_Circle.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.circle_purple)); break;
            }

            // On group list item click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), TaskListActivity.class);
                    intent.putExtra("GROUP_ID", _groupIds.get(getAdapterPosition()));
                    intent.putExtra("GROUP_NAME", _groupNames.get(getAdapterPosition()));
                    v.getContext().startActivity(intent);
                }
            });

            // On Delete button click
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnDel.setVisibility(removeGroup(getAdapterPosition()) ? View.GONE : View.VISIBLE);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group, parent, false);
        return new ViewHolder(view, _visible);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tV1.setText(_groupNames.get(position));
    }

    @Override
    public int getItemCount() {
        return _groupNames.size();
    }

    public boolean removeGroup(int position) {
        if (_dbh.deleteGroup(_groupIds.get(position))) {
            _groupIds.remove(position);
            _groupNames.remove(position);
            notifyItemRemoved(position);
            return true;
        }

        return false;
    }

    public boolean addGroup(String groupName) {
        int groupId = _dbh.createGroup(groupName, "blue");
        if (groupId == -1) {
            return false;
        }

        int newPosition = _groupNames.size();
        _groupIds.add(newPosition, String.valueOf(groupId));
        _groupNames.add(newPosition, groupName);
        notifyItemInserted(newPosition);
        return true;
    }

    public String getGroupColor(int position) {
        return _dbh.getGroupColor(_groupIds.get(position));
    }

    public List<String> getGroupIds() {
        return _groupIds;
    }

    public List<String> getGroupNames() {
        return _groupNames;
    }
}