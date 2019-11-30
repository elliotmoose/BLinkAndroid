package javanesecoffee.com.blink.social;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.ImageEntityObserver;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ImageManager;
import javanesecoffee.com.blink.managers.UserManager;

public class SocialTabCard_RecyclerViewAdapter extends RecyclerView.Adapter<SocialTabCard_RecyclerViewAdapter.ViewHolder>  {
    private static final String TAG = "SocialNameCard_Recycler";

    private ArrayList<User> users;
    private Context sCardContext;
    User currentUser = UserManager.getLoggedInUser();

    public SocialTabCard_RecyclerViewAdapter(ArrayList<User> items, Context sCardContext) {

        this.users = items;
        this.sCardContext = sCardContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_social_small_card, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final ViewHolder fholder = holder;
        holder.user = users.get(i);
        holder.updateData();


        holder.cardViewProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String username = fholder.user.getUsername();
                Log.d(TAG, "onClick: clicked on view profile");
                Toast.makeText(sCardContext, "loading user profile", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(sCardContext, UserDetailsActivity.class);
                intent.putExtra(IntentExtras.USER.USER_TYPE_KEY, IntentExtras.USER.USER_TYPE_EXPLORE);
                intent.putExtra(IntentExtras.USER.USER_NAME_KEY, username);
                sCardContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements ImageEntityObserver {

        User user;
        CircleImageView cardImage;
        TextView cardUsername;
        TextView cardDesignation;
        TextView cardCompany;
        Button cardViewProfile;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.fragment_social_small_card);
            cardImage = itemView.findViewById(R.id.small_card_profile_pic);
            cardCompany = itemView.findViewById(R.id.small_card_company);
            cardDesignation = itemView.findViewById(R.id.small_card_designation);
            cardUsername = itemView.findViewById(R.id.small_card_username);
            cardViewProfile = itemView.findViewById(R.id.card_view_profile);
        }

        public void updateData() {
            if(user == null) {
                return;
            }

            Bitmap image = ImageManager.getImageOrLoadIfNeeded(user.getUsername(), this, ImageManager.ImageType.PROFILE_IMAGE);

            if(image != null) {
                cardImage.setImageBitmap(image);
            }
            else {
                //resets when view is being reused
                cardImage.setImageBitmap(ImageManager.dpPlaceholder);
            }

            cardUsername.setText(user.getUsername());
            cardDesignation.setText(user.getPosition());
            cardCompany.setText(user.getCompany());
        }

        @Override
        public void onImageUpdated(Bitmap bitmap) {
            updateData();
        }
    }
}
