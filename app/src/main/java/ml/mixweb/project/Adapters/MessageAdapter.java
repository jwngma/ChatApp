package ml.mixweb.project.Adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.mixweb.project.Models.Messages;
import ml.mixweb.project.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages>mMessageList;
    private DatabaseReference mUserDatabase;

    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages>mMessageList){
        this.mMessageList=mMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        return new MessageViewHolder(v);
    }
    public class MessageViewHolder extends  RecyclerView.ViewHolder

    {


        public TextView messageText;
        public CircleImageView profileImage;
        public  TextView displayName;
        public ImageView messageImage;

        public MessageViewHolder(View itemView) {

            super(itemView);

            messageText= itemView.findViewById(R.id.message_text_layout);
            profileImage=itemView.findViewById(R.id.message_profile_layout);
            displayName=itemView.findViewById(R.id.name_text_layout);
            messageImage=itemView.findViewById(R.id.message_image_layout);


        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {
        mAuth=FirebaseAuth.getInstance();
        String current_user_id=mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();

        if (from_user.equals(current_user_id)){
            viewHolder.messageText.setBackgroundColor(Color.WHITE);
            viewHolder.messageText.setTextColor(Color.BLACK);
            viewHolder.messageText.setGravity(Gravity.LEFT);
            viewHolder.messageText.setTextSize(22);
        }
        else {
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setTextColor(Color.WHITE);
            viewHolder.messageText.setGravity(Gravity.LEFT);
            viewHolder.messageText.setTextSize(22);

        }



        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.displayName.setText(name);

                Picasso.with(viewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.circle_small_image).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.circle_small_image).into(viewHolder.messageImage);

        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}


//    @Override
//    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
//        mAuth=FirebaseAuth.getInstance();
//
//        String current_user_id=mAuth.getCurrentUser().getUid();
//
//        Messages c= mMessageList.get(position);
//
//        String from_user=c.getFrom();
//
//        if (from_user.equals(current_user_id)){
//            holder.messageText.setBackgroundColor(Color.WHITE);
//            holder.messageText.setTextColor(Color.BLACK);
//            holder.messageText.setGravity(Gravity.RIGHT);
//        }
//        else {
//            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
//            holder.messageText.setTextColor(Color.WHITE);
//            holder.messageText.setGravity(Gravity.LEFT);
//
//        }
//        holder.messageText.setText(c.getMessage());
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mMessageList.size();
//    }
//}
