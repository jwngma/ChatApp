package ml.mixweb.project.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.mixweb.project.R;
import ml.mixweb.project.Models.Requests;


public class RequestsFragment extends Fragment {
    private RecyclerView myRequestList;
    private View myMainView;
    private DatabaseReference FriendsRequestsReference;
    private FirebaseAuth mAuth;
    String online_user_id;

    private DatabaseReference UsersReference;
    private DatabaseReference FriendsDatabaseRef;
    private DatabaseReference FriendsReqDatabaseRef;
    private DatabaseReference mRootRef;


    public RequestsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_requests, container, false);
        mRootRef = FirebaseDatabase.getInstance().getReference();



        myRequestList = myMainView.findViewById(R.id.requests_list);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();


        FriendsRequestsReference = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(online_user_id);

        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_req");

        myRequestList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myRequestList.setLayoutManager(linearLayoutManager);

        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Requests, RequestViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>(
                Requests.class,
                R.layout.friend_request_all_users_layout,
                RequestsFragment.RequestViewHolder.class,
                FriendsRequestsReference


        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, final int position) {

                final String list_users_id = getRef(position).getKey();

                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();
                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String request_type = dataSnapshot.getValue().toString();

                            if (request_type.equals("received")) {
                                UsersReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String status = dataSnapshot.child("status").getValue().toString();
                                        final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                                        viewHolder.setName(userName);
                                        viewHolder.setThumb_user_image(thumb_image, getContext());
                                        viewHolder.setUserStatus(status);
                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]{
                                                        "Accept friend Request",
                                                        "Cancell friend Request"

                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Friend Req Options");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int position) {
                                                        if (position == 0) {



//                                                            Calendar calForDATE=Calendar.getInstance();
//                                                            SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
//                                                            final  String savedCurrentDate=currentDate.format(calForDATE.getTime());
//
//
//                                                            FriendsDatabaseRef.child(online_user_id).child("date").setValue(savedCurrentDate)
//                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                        @Override
//                                                                        public void onSuccess(Void aVoid) {
//                                                                            FriendsDatabaseRef.child(list_users_id).child(online_user_id).child("date").setValue(savedCurrentDate)
//                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                                        @Override
//                                                                                        public void onSuccess(Void aVoid) {
//
//                                                                                        }
//                                                                                    });FriendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                @Override
//                                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                                    if (task.isSuccessful()){
//                                                                                        FriendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                            @Override
//                                                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                                                if (task.isSuccessful()){
//                                                                                                    Toast.makeText(getContext(), "Friends Accepted", Toast.LENGTH_SHORT).show();
//                                                                                                }
//
//                                                                                            }
//                                                                                        });
//                                                                                    }
//                                                                                }
//                                                                            });
//                                                                        }
//                                                                    });

                                                        } else if (position == 1) {

                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        FriendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    Toast.makeText(getContext(), "Friend Request declined Successfully", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            else if (request_type.equals("sent"))
                            {

                                Button req_sent_btn=viewHolder.mView.findViewById(R.id.request_accept_btn);
                                req_sent_btn.setText(" Req Sent");
                                viewHolder.mView.findViewById(R.id.request_decline_btn).setVisibility(View.INVISIBLE);

                                UsersReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String status = dataSnapshot.child("status").getValue().toString();
                                        final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                                        viewHolder.setName(userName);
                                        viewHolder.setThumb_user_image(thumb_image, getContext());
                                        viewHolder.setUserStatus(status);

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                CharSequence options[] = new CharSequence[]{

                                                        "Cancel friend Request"

                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Friend Request Sent");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int position) {
                                                          if (position == 0) {

                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        FriendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    Toast.makeText(getContext(), "Friend Request cancelled successfully", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }
                                                });
                                                builder.show();


                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        myRequestList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String userName) {

            TextView userNameDisplay = mView.findViewById(R.id.request_profile_name);
            userNameDisplay.setText(userName);
        }

        public void setThumb_user_image(final String thumb_image, final Context ctx) {

            final CircleImageView thumb_image1 = mView.findViewById(R.id.request_profile_image);
            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.circle_small_image)
                    .into(thumb_image1, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.circle_small_image).into(thumb_image1);

                        }
                    });


        }

        public void setUserStatus(String status) {
            TextView status1 = mView.findViewById(R.id.request_profile_status);
            status1.setText(status);


        }
    }

}
