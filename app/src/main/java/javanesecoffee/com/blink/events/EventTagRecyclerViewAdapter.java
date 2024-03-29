package javanesecoffee.com.blink.events;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_interest_list, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventTagRecyclerViewAdapter.ViewHolder holder, int i) {
        holder.tag = tags.get(i);
        int[] colors = {R.drawable.button_gradient_red, R.drawable.button_gradient_green, R.drawable.button_gradient_yellow};
        Log.d("rec", (String) holder.tag_name.getText());
        //System.out.println(holder.tag_name.getText());
        holder.tag_name.setText(holder.tag);
        holder.tag_name.setBackgroundResource(colors[i%3]);

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
