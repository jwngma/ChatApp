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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import ml.mixweb.project.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout memail, mpassword;
    private Button log_btn, log_reg_btn;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        toolbar=findViewById(R.id.log_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog= new ProgressDialog(this);

        memail=findViewById(R.id.log_email);
        mpassword=findViewById(R.id.log_password);
        log_btn=findViewById(R.id.log_btn);
        log_reg_btn=findViewById(R.id.log_reg_btn);
        log_reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=memail.getEditText().getText().toString();
                String password=mpassword.getEditText().getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    progressDialog.setTitle("Logging in ");
                    progressDialog.setMessage(" Please wait while we Check your Credential");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    loginUser(email,password);
                }

            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    String current_user_id=mAuth.getCurrentUser().getUid();
                    String deviceToken= FirebaseInstanceId.getInstance().getToken();

                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Intent intent= new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        }
                    });



                }
                else {
                    progressDialog.hide();
                    String error= task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, "Error:"+error, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
