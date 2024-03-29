package javanesecoffee.com.blink.events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.ImageEntityObserver;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.ImageManager;
import javanesecoffee.com.blink.social.UnconnectedUserDetailsActivity;
import javanesecoffee.com.blink.social.UserDetailsActivity;

public class UserDPListAdapter extends RecyclerView.Adapter<UserDPListAdapter.ViewHolder> {
    private static final String TAG = "AlsoAttending_Recycler";
    private Context mContext;
    ArrayList<User> users = new ArrayList<>();

    public UserDPListAdapter(ArrayList<User> items, Context context) {
        super();
        this.users = items;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_also_attending, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        holder.user = users.get(i);
        holder.updateData();
        final ViewHolder holderfinal = holder;

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = holderfinal.user.getUsername();
                Log.d(TAG, "onClick: clicked on view profile");
                Toast.makeText(mContext, "loading user image",Toast.LENGTH_SHORT).show();
                if(ConnectionsManager.getInstance().usernameIsConnection(username)) {

                    Intent intent = new Intent(mContext, UserDetailsActivity.class);
                    intent.putExtra(IntentExtras.USER.USER_NAME_KEY,username);
                    mContext.startActivity(intent);
                }
                else { //if he is not a connection, go to locked UserDetailsActivity
                    Intent intent = new Intent(mContext, UnconnectedUserDetailsActivity.class);
                    intent.putExtra(IntentExtras.USER.USER_NAME_KEY,username);
                    mContext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements ImageEntityObserver {
        User user;

        CircleImageView also_attendedImage;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.event_also_attending);
            also_attendedImage = itemView.findViewById(R.id.also_attending_profile_pic);
        }

        public void updateData() {
            if(user == null) {
                return;
            }

            Bitmap image = ImageManager.getImageOrLoadIfNeeded(user.getUsername(), this, ImageManager.ImageType.PROFILE_IMAGE);

            if(image != null) {
                also_attendedImage.setImageBitmap(image);
            }
            else {
                //resets when view is being reused
                also_attendedImage.setImageBitmap(ImageManager.dpPlaceholder);
            }
        }

        @Override
        public void onImageUpdated(Bitmap bitmap) {
            updateData();
        }
    }
}
