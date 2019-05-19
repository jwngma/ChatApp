package ml.mixweb.project.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import ml.mixweb.project.R;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName, mEmail, mpassword;
    private Button mCreateBtn, reg_log_btn;
    private FirebaseAuth mAuth;

    private Toolbar toolbar;
    private DatabaseReference mDatabase;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        toolbar = findViewById(R.id.reg_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mpassword = findViewById(R.id.reg_password);
        reg_log_btn=findViewById(R.id.reg_log_btn);
        reg_log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
        mCreateBtn = findViewById(R.id.reg_btn);
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dispaly_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mpassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(dispaly_name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage(" Please wait while we create your account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    register_user(dispaly_name, email, password);
                }


            }
        });
    }

    private void register_user(final String dispaly_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {


                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    String device_token= FirebaseInstanceId.getInstance().getToken();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", dispaly_name);
                    userMap.put("status", "Hi, I am using Social Chat App");
                    userMap.put("image", " default");
                    userMap.put("thumb_image", "default");
                    userMap.put("device_token", device_token);
                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                progressDialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }
                        }
                    });


                } else {
                    progressDialog.hide();
                    String Error1 = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, "Error :" + Error1, Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
}
