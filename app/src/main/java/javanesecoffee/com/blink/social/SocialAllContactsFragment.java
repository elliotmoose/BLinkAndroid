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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import javanesecoffee.com.blink.R;
import javanesecoffee.com.blink.constants.IntentExtras;
import javanesecoffee.com.blink.entities.User;
import javanesecoffee.com.blink.managers.ConnectionsManager;
import javanesecoffee.com.blink.managers.UserManager;

import static android.support.constraint.Constraints.TAG;

public class SocialAllContactsFragment extends Fragment {


    RecyclerView recyclerView_NameCard;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frangment_social_all_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadAllContacts(view, savedInstanceState);
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: Commenced");

        ArrayList<User> allConnections= ConnectionsManager.getInstance().getAllConnections();
        SocialNameCard_RecyclerViewAdapter nameCard_adapter = new SocialNameCard_RecyclerViewAdapter(allConnections, getActivity());
        VerticalSpaceItemDecoration spaceDecoration = new VerticalSpaceItemDecoration(40);
        recyclerView_NameCard.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_NameCard.setAdapter(nameCard_adapter);
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int space;

        public VerticalSpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;
        }
    }
    public void loadAllContacts(@NonNull final View view, @Nullable final Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        recyclerView_NameCard = view.findViewById(R.id.socialNameCardRecycler_all);
        initRecyclerView();
        final SwipeRefreshLayout swipeRefreshLayoutSocial = getView().findViewById(R.id.swipeRefreshSocial);
        swipeRefreshLayoutSocial.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAllContacts(view, savedInstanceState);
                swipeRefreshLayoutSocial.setRefreshing(false);
            }
        });
    }
}
