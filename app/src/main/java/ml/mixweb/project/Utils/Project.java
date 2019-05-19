package ml.mixweb.project.Utils;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class Project extends Application {

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;


    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso build = builder.build();
        build.setIndicatorsEnabled(true);
        build.setLoggingEnabled(true);
        Picasso.setSingletonInstance(build);

        /////////////////


        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {

            mUserDatabase = FirebaseDatabase.getInstance()
                    .getReference().child("Users").child(mAuth.getCurrentUser().getUid());

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }








        /////////////




//
//        mAuth = FirebaseAuth.getInstance();
//
//        currentUser = mAuth.getCurrentUser();
//
//        if (currentUser != null) {
//
//
//            String online_user_id = mAuth.getCurrentUser().getUid();
//
//            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
//
//
//            mUserDatabase.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot != null) {
//                        mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
//
//
//
//                    } else {
//                        mUserDatabase.child("online").setValue(true);
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//
//        }


    }
}








/////



























