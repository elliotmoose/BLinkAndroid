package javanesecoffee.com.blink.social;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.ImageLoadObserver;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.UserManager;

import static android.support.constraint.Constraints.TAG;

public class SocialSummaryFragment extends Fragment implements ImageLoadObserver {

    CircleImageView editProfilePic;
    TextView editUsername;
    Button viewProfile;
    RecyclerView recyclerView_NameCard;
    RecyclerView recyclerView_SmallCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadSocialSummary(view, savedInstanceState);
    }

    private void initRecyclerView(){
        ArrayList<User> recentConnections = ConnectionsManager.getInstance().getRecentConnections();
        ArrayList<User> recommendedConnections = ConnectionsManager.getInstance().getRecommendedConnections();
        SocialNameCard_RecyclerViewAdapter nameCard_adapter = new SocialNameCard_RecyclerViewAdapter(recentConnections, getActivity());
        SocialTabCard_RecyclerViewAdapter smallCard_adapter = new SocialTabCard_RecyclerViewAdapter(recommendedConnections,getActivity());

        recyclerView_NameCard.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView_SmallCard.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        HorizontalSpaceItemDecoration spaceDecoration = new HorizontalSpaceItemDecoration(40);
        recyclerView_NameCard.addItemDecoration(spaceDecoration);
        recyclerView_SmallCard.addItemDecoration(spaceDecoration);


        recyclerView_NameCard.setAdapter(nameCard_adapter);
        recyclerView_SmallCard.setAdapter(smallCard_adapter);

    }

    public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int space;

        public HorizontalSpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
        }
    }

    private void UpdateUserData() {
        User user = UserManager.getLoggedInUser();

        if(user != null) {
            editUsername.setText(user.getUsername());
            Bitmap image = user.getProfilepictureAndLoadIfNeeded(this);
            if(image != null){
                editProfilePic.setImageBitmap(image);
            }
        }
    }

    @Override
    public void onImageLoad(Bitmap bitmap) {
        UpdateUserData();
    }

    @Override
    public void onImageLoadFailed(BLinkApiException exception) {
        UpdateUserData();
    }
    public void loadSocialSummary(@NonNull final View view, @Nullable final Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        editUsername = view.findViewById(R.id.fieldSocialUsername);
        editProfilePic = view.findViewById(R.id.social_profile_pic);
        viewProfile = view.findViewById(R.id.fieldSocialViewProfile);


        recyclerView_NameCard = view.findViewById(R.id.socialNameCardRecycler);
        recyclerView_SmallCard = view.findViewById(R.id.socialSmallCardRecycler);

        //view profile button
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getActivity(), UserDetailsActivity.class);
                intent.putExtra(IntentExtras.USER.USER_TYPE_KEY,IntentExtras.USER.USER_TYPE_SELF);
                startActivity(intent);
            }
        });
        final SwipeRefreshLayout swipeRefreshLayoutSocial2 = getView().findViewById(R.id.swipeRefreshSocial2);
        swipeRefreshLayoutSocial2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSocialSummary(view, savedInstanceState);
                swipeRefreshLayoutSocial2.setRefreshing(false);
            }
        });

        initRecyclerView();
        UpdateUserData();
    }
}
