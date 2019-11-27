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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.ImageLoadObserver;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.UserManager;

public class SocialNameCard_RecyclerViewAdapter extends RecyclerView.Adapter<SocialNameCard_RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "SocialNameCard_Recycler";

    private Context mContext;
    ArrayList<User> users = new ArrayList<>();
    User currentUser = UserManager.getLoggedInUser();

    public SocialNameCard_RecyclerViewAdapter(ArrayList<User> items,Context context) {
        super();
        this.users = items;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_social_card, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        Log.d(TAG, "onBindViewHolder:called ");

        holder.user = users.get(i);
        holder.UpdateData();
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
                Log.d(TAG, "onClick: clicked on view connections");
                Toast.makeText(mContext, "loading user connections",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ImageLoadObserver {

        User user;

        CircleImageView cardImage;
        TextView cardUsername;
        TextView cardDesignation;
        LinearLayout cardContactDetails;
        TextView cardEmail;
        TextView cardLinkedin;
        TextView cardFacebook;
        TextView cardInstagram;
        TextView cardCompany;
        ConstraintLayout parentLayout;
        Button cardViewProfile;
        Button cardViewConnections;


        public ViewHolder(@NonNull View itemView) {
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
        }

        public void UpdateData() {
            if(user == null) {
                return;
            }

            Bitmap image = user.getProfilepictureAndLoadIfNeeded(this);

            if(image != null) {
                cardImage.setImageBitmap(image);
            }

            cardUsername.setText(user.getUsername());
            cardDesignation.setText(user.getPosition());
            //holder.cardContactDetails.setText(bCardContactDetails.get(i));
            cardEmail.setText(user.getEmail());
            cardLinkedin.setText(user.getLinkedin());
            cardFacebook.setText(user.getFacebook());
            cardInstagram.setText(user.getInstagram());
            cardCompany.setText(user.getCompany());
        }

        @Override
        public void onImageLoadFailed(BLinkApiException exception) {

        }

        @Override
        public void onImageLoad(Bitmap bitmap) {
            UpdateData();
        }
    }
}
