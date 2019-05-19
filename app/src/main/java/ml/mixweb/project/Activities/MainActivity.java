package ml.mixweb.project.Activities;

//        import android.content.Intent;
//        import android.support.design.widget.TabLayout;
//        import android.support.v4.view.ViewPager;
//        import android.support.v7.app.AppCompatActivity;
//        import android.os.Bundle;
//        import android.support.v7.widget.Toolbar;
//        import android.view.Menu;
//        import android.view.MenuItem;
//        import android.widget.Toast;
//
//        import com.google.firebase.auth.FirebaseAuth;
//        import com.google.firebase.auth.FirebaseUser;
//        import com.google.firebase.database.DatabaseReference;
//        import com.google.firebase.database.FirebaseDatabase;
//        import com.google.firebase.database.ServerValue;
//
//public class MainActivity extends AppCompatActivity {
//    private FirebaseAuth mAuth;
//    private Toolbar mtoolbar;
//
//    private ViewPager mviewPager;
//    private SectionsPagerAdapter msectionsPagerAdapter;
//    private TabLayout mtabLayout;
//    private DatabaseReference mUserRef;
//    private FirebaseUser Currentuser;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mAuth = FirebaseAuth.getInstance();
//        Currentuser=mAuth.getCurrentUser();
//
//if (mAuth.getCurrentUser()!=null){
//    mUserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
//}
//
//        mtoolbar = findViewById(R.id.main_page_toolbar);
//        setSupportActionBar(mtoolbar);
//        getSupportActionBar().setTitle("Social Chat App");
//
//
//
//        mviewPager=findViewById(R.id.main_tabPager);
//        msectionsPagerAdapter= new SectionsPagerAdapter(getSupportFragmentManager());
//        mviewPager.setAdapter(msectionsPagerAdapter);
//
//        mtabLayout=findViewById(R.id.main_tabs);
//        mtabLayout.setupWithViewPager(mviewPager);
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//
//        if (currentUser == null) {
//            sendToStart();
//        }
//        else  {
//            if (Currentuser!=null){
//                String current_userid=mAuth.getCurrentUser().getUid();
//
//                mUserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(current_userid);
//
//            }
//            mUserRef.child("online").setValue("true");
//
//        }
//
//
//
//
//    }
//
////    @Override
////    protected void onStop() {
////        super.onStop();
////        mAuth=FirebaseAuth.getInstance();
////        if (Currentuser!=null){
////            String current_userid=mAuth.getCurrentUser().getUid();
////
////            mUserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(current_userid);
////            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
////
////
////
////
////        }
////        else {
////            Toast.makeText(this, "Your are logged out", Toast.LENGTH_SHORT).show();
////        }
////
////    }
//
//
//
//
//    private void sendToStart() {
//
//        Intent intent = new Intent(MainActivity.this, StartActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.main_log_out:
//                mAuth.signOut();
//                sendToStart();
//                break;
//            case R.id.main_account_settings:
//                Intent intent= new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.main_all_user_btn:
//                Intent intentAllUser= new Intent(MainActivity.this, UsersActivity.class);
//                startActivity(intentAllUser);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//
//
//    }
//}

///////////////////////


import android.content.Intent;
        import android.support.design.widget.TabLayout;
        import android.support.v4.view.ViewPager;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ServerValue;

import ml.mixweb.project.R;
import ml.mixweb.project.Adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mUserRef;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Social ChatApp");

        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }


        //Tabs
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);




    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            sendToStart();

        } else {

            mUserRef.child("online").setValue("true");

        }

    }


    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_log_out){

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        if(item.getItemId() == R.id.main_account_settings){

            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }

        if(item.getItemId() == R.id.main_all_user_btn){

            Intent settingsIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(settingsIntent);

        }

        return true;
    }
}







/////////////////



