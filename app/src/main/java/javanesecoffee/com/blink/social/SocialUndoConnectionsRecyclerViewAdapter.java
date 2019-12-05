package javanesecoffee.com.blink.social;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.entities.Connection;
import javanesecoffee.com.blink.managers.ConnectionsManager;

public class SocialUndoConnectionsRecyclerViewAdapter extends RecyclerView.Adapter<SocialUndoConnectionsRecyclerViewAdapter.ViewHolder>{

    private Context mContext;
    ArrayList<Connection> connections;

    public SocialUndoConnectionsRecyclerViewAdapter(ArrayList<Connection> items, Context context) {
        super();
        this.connections = items;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_social_undo_connection, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        holder.connection = connections.get(i);
        holder.updateData();
    }

    @Override
    public int getItemCount() {
        return this.connections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        Connection connection;
        TextView usernameTextView;
        Button undoButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.undoUsernameTextView);
            undoButton = itemView.findViewById(R.id.undoButton);
        }

        public void updateData() {
            if(connection == null) {
                return;
            }

            usernameTextView.setText("@" + connection.getUsername());

            undoButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(connection != null) {
                        ConnectionsManager.getInstance().undoConnection(connection);
                    }
                }
            });
        }
    }
}
