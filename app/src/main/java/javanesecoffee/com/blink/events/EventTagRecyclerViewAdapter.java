package javanesecoffee.com.blink.events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import javanesecoffee.com.blink.R;

public class EventTagRecyclerViewAdapter extends RecyclerView.Adapter<EventTagRecyclerViewAdapter.ViewHolder> {
    private Context context;
    ArrayList<String> tags;
    public EventTagRecyclerViewAdapter(ArrayList<String> items, Context context){
        this.tags = items;
        this.context = context;
    }
    @NonNull
    @Override
    public EventTagRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_event, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventTagRecyclerViewAdapter.ViewHolder holder, int i) {
        holder.tag = tags.get(i);
        holder.tag_name.setText(holder.tag);
    }

    @Override
    public int getItemCount() {
        return this.tags.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        String tag;
        TextView tag_name;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View view){
            super(view);
            tag_name = itemView.findViewById(R.id.tag_name);
            parentLayout = itemView.findViewById(R.id.tag_parent);
        }
    }
}
