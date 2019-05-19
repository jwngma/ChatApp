package ml.mixweb.project.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.mixweb.project.Activities.ChatActivity;
import ml.mixweb.project.Models.Friends;
import ml.mixweb.project.Activities.ProfileActivity;
import ml.mixweb.project.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private DatabaseReference mFriendsDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView mFriendsList;
    private String mCurrent_user_id;
    private View mMainView;

    private DatabaseReference mUsersDatabase;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mFriendsList.hasFixedSize();
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, final Friends model, int position) {
                viewHolder.setDate(model.getDate());

                final String list_user_id = getRef(position).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")){

                            String  userOnline=dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);
                        }

//                        String userOnline=dataSnapshot.child("online").getValue().toString();

                        viewHolder.setName(userName);
                        viewHolder.setUserImage(userThumb, getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence option[] = new CharSequence[]{"Open Profile", "Send Message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select option");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Intent Profileintent = new Intent(getContext(), ProfileActivity.class);
                                            Profileintent.putExtra("user_id", list_user_id);
                                            startActivity(Profileintent);
                                        }
                                        if (which == 1) {
                                            Intent Profileintent = new Intent(getContext(), ChatActivity.class);
                                            Profileintent.putExtra("user_id", list_user_id);
                                            Profileintent.putExtra("user_name", userName);
                                            startActivity(Profileintent);

                                        }

                                    }
                                });
                                builder.show();
                            }
                        });
//                        if (dataSnapshot.hasChild("online")) {
//                            Boolean userOnline = (boolean) dataSnapshot.child("online").getValue();
//                            viewHolder.setUserOnline(userOnline);
//
//                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        mFriendsList.setAdapter(firebaseRecyclerViewAdapter);


    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


        }

        public void setDate(String date) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);

        }

        public void setName(String name) {
            TextView usernameView = (TextView) mView.findViewById(R.id.user_single_name);
            usernameView.setText(name);
        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView UserImageView = (CircleImageView) mView.findViewById(R.id.user_single_imsge);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.circle_small_image).into(UserImageView);

        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if (online_status.equals("true")) {
                userOnlineView.setVisibility(View.VISIBLE);

            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
