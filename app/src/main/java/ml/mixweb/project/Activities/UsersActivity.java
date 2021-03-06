package ml.mixweb.project.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.mixweb.project.R;
import ml.mixweb.project.Models.Users;

public class UsersActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        toolbar=findViewById(R.id.userstoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersList=findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));





    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setUserStatus(model.getStatus());
                viewHolder.setUserImage(model.getThumb_image(),getApplicationContext());

                final String user_id=getRef(position).getKey();


                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent Profileintent = new Intent(UsersActivity.this, ProfileActivity.class);
                        Profileintent.putExtra("user_id",user_id);
                        startActivity(Profileintent);


                    }
                });


            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mview;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setName(String  name){
            TextView UsernameView=(TextView)mview.findViewById(R.id.user_single_name);
            UsernameView.setText(name);

        }
        public void setUserStatus(String status){
            TextView userStatusView=(TextView)mview.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }
        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView=(CircleImageView)mview.findViewById(R.id.user_single_imsge);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.circle_small_image).into(userImageView);

        }
    }
}
