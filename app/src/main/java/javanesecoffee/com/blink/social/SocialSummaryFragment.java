package javanesecoffee.com.blink.social;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.api.BLinkApiException;
import javanesecoffee.com.blink.api.BLinkEventObserver;
import javanesecoffee.com.blink.api.ImageEntityObserver;
import javanesecoffee.com.blink.constants.ApiCodes;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.ImageManager;
import javanesecoffee.com.blink.managers.UserManager;

public class SocialSummaryFragment extends Fragment implements ImageEntityObserver, BLinkEventObserver {

    SocialNameCard_RecyclerViewAdapter nameCard_adapter;
    SocialTabCard_RecyclerViewAdapter smallCard_adapter;

    CircleImageView editProfilePic;
    TextView editUsername;
    Button viewProfile;
    RecyclerView recyclerView_NameCard;
    RecyclerView recyclerView_SmallCard;

    SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ConnectionsManager.getInstance().registerObserver(this);
        return inflater.inflate(R.layout.fragment_social_summary, container, false);
    }

    @Override
    public void onDestroy() {
        ConnectionsManager.getInstance().deregisterObserver(this);
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadSocialSummary(view, savedInstanceState);
        initRecyclerView();
        UpdateUserData();
    }

    private void initRecyclerView(){
        ArrayList<User> recentConnections = ConnectionsManager.getInstance().getRecentConnections();
        ArrayList<User> recommendedConnections = ConnectionsManager.getInstance().getRecommendedConnections();
        nameCard_adapter = new SocialNameCard_RecyclerViewAdapter(recentConnections, getActivity());
        smallCard_adapter = new SocialTabCard_RecyclerViewAdapter(recommendedConnections,getActivity());

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
            String username = user.getUsername();
            editUsername.setText(username);
            Bitmap image = ImageManager.getImageOrLoadIfNeeded(username, this, ImageManager.ImageType.PROFILE_IMAGE);
            if(image != null){
                editProfilePic.setImageBitmap(image);
            }
            else {
                //resets when view is being reused
                editProfilePic.setImageBitmap(ImageManager.dpPlaceholder);
            }
        }
    }

    private void UpdateData() {
        UpdateUserData();
        if(smallCard_adapter != null) {
            smallCard_adapter.notifyDataSetChanged();
        }

        if(nameCard_adapter != null) {
            nameCard_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onImageUpdated(Bitmap bitmap) {
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
        swipeRefreshLayout = getView().findViewById(R.id.swipeRefreshSocial2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSocialSummary(view, savedInstanceState);
                swipeRefreshLayout.setRefreshing(false);
                ConnectionsManager.getInstance().loadAllConnections();
                UpdateData();
            }
        });

    }

    @Override
    public void onBLinkEventTriggered(JSONObject response, String taskId) throws BLinkApiException {
        if(taskId == ApiCodes.TASK_LOAD_CONNECTIONS) {
            UpdateData();
            if(swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onBLinkEventException(BLinkApiException exception, String taskId) {

    }
}
