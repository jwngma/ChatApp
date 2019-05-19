package ml.mixweb.project.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ml.mixweb.project.R;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mDeclineBtn;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationdatabase;
    private FirebaseUser mCurrent_user;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mRootRef;
    private ProgressDialog progressDialog;
    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("user_id");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationdatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mProfileImage = findViewById(R.id.profile_image);
        mProfileName = findViewById(R.id.profile_DisplayName);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = findViewById(R.id.profile_send_req_btn);
        mDeclineBtn = findViewById(R.id.profile_decline_req_btn);

        mCurrent_state = "not_friends";


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User data");
        progressDialog.setMessage(" please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String Display_Name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(Display_Name);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.circle_small_image).into(mProfileImage);

                //-----Friend List/ Request feature---------//
                mDeclineBtn.setVisibility(View.INVISIBLE);
                mDeclineBtn.setEnabled(false);

                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {
                                mCurrent_state = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");
                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);

                            } else if (req_type.equals("sent")) {
                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);


                            }

                            progressDialog.dismiss();
                        } else {
                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)) {
                                        mCurrent_state = "friends";
                                        mProfileSendReqBtn.setText("Unfriend This Person");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);


                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    progressDialog.dismiss();

                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileSendReqBtn.setEnabled(false);

                //---------Not Friend State-------------//

                if (mCurrent_state.equals("not_friends")) {
                    DatabaseReference newNotificationRef=mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId=newNotificationRef.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrent_user.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/"+   mCurrent_user.getUid() + "/" + user_id  + "/request_type", "sent");
                    requestMap.put("Friend_req/"+ user_id + "/" + mCurrent_user.getUid()  + "/request_type", "received");
                    requestMap.put("notifications/"+user_id+"/"+newNotificationId,notificationData);


                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError!=null){
                                Toast.makeText(ProfileActivity.this, "Erroee", Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                            mCurrent_state="req_sent";
                            mProfileSendReqBtn.setText("CANCEL FRIEND REQUEST");


                        }
                    });

                }

                //---------Cancel Request State---------//

                if (mCurrent_state.equals("req_sent")) {
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mNotificationdatabase.child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProfileSendReqBtn.setEnabled(true);
                                            mCurrent_state = "not_friends";
                                            mProfileSendReqBtn.setText("Send Friend Request");

                                            mDeclineBtn.setVisibility(View.INVISIBLE);
                                            mDeclineBtn.setEnabled(false);

                                        }
                                    });

                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Request");

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);
                                }
                            });

                        }
                    });
                }
                //-----------REQ RECEIVED STATE------------//

                if (mCurrent_state.equals("req_received")) {

                    final  String currentDate=DateFormat.getDateTimeInstance().format(new Date());
                    Map friendsMap= new HashMap();
                    friendsMap.put("Friends/"+mCurrent_user.getUid()+"/"+user_id+"/date",currentDate);
                    friendsMap.put("Friends/"+user_id+"/"+mCurrent_user.getUid()+"/date",currentDate);

                    friendsMap.put("Friend_req/"+mCurrent_user.getUid()+"/"+user_id+"/date",null);
                    friendsMap.put("Friend_req/"+user_id+"/"+mCurrent_user.getUid()+"/date",null);



                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError==null){

                                DatabaseReference newNotificationRef=mRootRef.child("notifications").child(user_id).push();
                                String newNotificationId=newNotificationRef.getKey();

                                HashMap<String, String> notificationData = new HashMap<>();
                                notificationData.put("from", null);
                                notificationData.put("type", null);

                                Map requestMap = new HashMap();
                                requestMap.put("notifications/"+user_id+"/"+newNotificationId,notificationData);


                                mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError!=null){
                                            mProfileSendReqBtn.setEnabled(true);
                                            Toast.makeText(ProfileActivity.this, "Erroee", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });

                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state="friends";
                                mProfileSendReqBtn.setText("Unfriend this Friend");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);


                            }
                            else {
                                String e= databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, "Error"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }

                //--------Unfriend Feature, tried by myself-------------------//

                if (mCurrent_state.equals("friends")) {

                        Map unfriendMap= new HashMap();
                        unfriendMap.put("Friends/"+mCurrent_user.getUid()+"/"+user_id,null);
                    unfriendMap.put("Friends/"+user_id+"/"+mCurrent_user.getUid(),null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError==null){


                                mCurrent_state="not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);


                            }
                            else {
                                String e= databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, "Error"+e, Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });


                }

                //-----------------------Decline Friend Request/=------------/
                if (mCurrent_state.equals("req_received")){
                    Map DeclineFriendred= new HashMap();
                    DeclineFriendred.put("Friend_req/"+mCurrent_user.getUid()+"/"+user_id+"/",null);
                    DeclineFriendred.put("Friend_req/"+user_id+"/"+mCurrent_user.getUid()+"",null);
                    mRootRef.updateChildren(DeclineFriendred, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError==null){

                                mCurrent_state="not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);


                            }
                            else {
                                String e= databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, "Error"+e, Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });


                }


            }
        });


    }


}
