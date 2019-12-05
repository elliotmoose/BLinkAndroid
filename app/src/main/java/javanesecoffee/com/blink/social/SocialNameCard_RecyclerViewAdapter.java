package javanesecoffee.com.blink.social;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.ViewConnectionActivity;
import javanesecoffee.com.blink.api.ImageEntityObserver;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ImageManager;

public class SocialNameCard_RecyclerViewAdapter extends RecyclerView.Adapter<SocialNameCard_RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "SocialNameCard_Recycler";

    private Context mContext;
    ArrayList<User> users;
    boolean itemWidthMatchParent;

    public SocialNameCard_RecyclerViewAdapter(ArrayList<User> items,Context context, boolean itemWidthMatchParent) {
        super();
        this.users = items;
        this.mContext = context;
        this.itemWidthMatchParent =itemWidthMatchParent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_social_card, parent, false);
        ViewHolder holder = new ViewHolder(view, itemWidthMatchParent);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        Log.d(TAG, "onBindViewHolder:called ");

        holder.user = users.get(i);
        holder.updateData();
        final ViewHolder holderRef = holder;
        holder.cardViewProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String username = holderRef.user.getUsername();
                Log.d(TAG, "onClick: clicked on view profile");
                Toast.makeText(mContext, "loading user profile",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, UserDetailsActivity.class);
                intent.putExtra(IntentExtras.USER.USER_TYPE_KEY,IntentExtras.USER.USER_TYPE_CONNECTION);
                intent.putExtra(IntentExtras.USER.USER_NAME_KEY,username);
                mContext.startActivity(intent);
        }
        });

        holder.cardViewConnections.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String username = holderRef.user.getUsername();
                Log.d(TAG, "onClick: clicked on view connections");
                Toast.makeText(mContext, "loading user connections",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, ViewConnectionActivity.class);
                intent.putExtra(IntentExtras.USER.USER_NAME_KEY,username);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ImageEntityObserver {

        User user;

        ConstraintLayout parentLayout;
        LinearLayout cardContactDetails;

        CircleImageView cardImage;

        TextView cardUsername;
        TextView cardDesignation;
        TextView cardEmail;
        TextView cardLinkedin;
        TextView cardFacebook;
        TextView cardInstagram;
        TextView cardCompany;

        Button cardViewProfile;
        Button cardViewConnections;


        public ViewHolder(@NonNull View itemView, boolean itemWidthMatchParent) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.fragment_social_card);
            cardImage = itemView.findViewById(R.id.card_profile_pic);
            cardCompany = itemView.findViewById(R.id.card_company);
            cardDesignation = itemView.findViewById(R.id.card_designation);
            cardUsername = itemView.findViewById(R.id.card_username);
            cardContactDetails = itemView.findViewById(R.id.card_contact_details);
            cardEmail = itemView.findViewById(R.id.card_email);
            cardLinkedin = itemView.findViewById(R.id.card_linkedin);
            cardFacebook = itemView.findViewById(R.id.card_facebook);
            cardInstagram = itemView.findViewById(R.id.card_instagram);
            cardViewProfile = itemView.findViewById(R.id.card_view_profile);
            cardViewConnections = itemView.findViewById(R.id.card_view_connection);

            ConstraintLayout constraintLayout = itemView.findViewById(R.id.fragment_social_card);
            constraintLayout.setLayoutParams(new ConstraintLayout.LayoutParams(itemWidthMatchParent ? ConstraintLayout.LayoutParams.MATCH_PARENT : ConstraintLayout.LayoutParams.WRAP_CONTENT , ConstraintLayout.LayoutParams.WRAP_CONTENT));
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

            cardUsername.setText(user.getDisplayname());
            cardDesignation.setText(user.getPosition());
            //holder.cardContactDetails.setText(bCardContactDetails.get(i));
            cardEmail.setText(user.getEmail());
            cardLinkedin.setText(user.getLinkedin());
            cardFacebook.setText(user.getFacebook());
            cardInstagram.setText("instagram.com/" + user.getInstagram());
            cardCompany.setText(user.getCompany());
        }

        @Override
        public void onImageUpdated(Bitmap bitmap) {
            updateData();
        }
    }
}
