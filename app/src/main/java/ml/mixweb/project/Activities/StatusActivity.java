package ml.mixweb.project.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ml.mixweb.project.R;

public class StatusActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputLayout mStatus;
    private Button mSaveBtn;
//
        private DatabaseReference mStatusDatabase;
        private FirebaseUser mCurrentUser;

        private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentUser.getUid();
        mStatusDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        toolbar=findViewById(R.id.statustoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSaveBtn=findViewById(R.id.status_save_btn);
        mStatus=findViewById(R.id.status_input);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog= new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle("Saving Change");
                progressDialog.setMessage(" please wait while we save your status");
                progressDialog.show();

                String status= mStatus.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Intent intent= new Intent(StatusActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else {
                            String error=task.getException().getMessage();
                            Toast.makeText(StatusActivity.this, "Error:"+error, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });
    }
}
