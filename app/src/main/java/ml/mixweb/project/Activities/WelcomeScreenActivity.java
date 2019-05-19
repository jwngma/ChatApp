package ml.mixweb.project.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import ml.mixweb.project.R;

public class WelcomeScreenActivity extends AppCompatActivity {
    private TextView welcomeText;
    Animation fromTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        welcomeText=findViewById(R.id.welcome_text);
        fromTop= AnimationUtils.loadAnimation(this,R.anim.fromtop);
        welcomeText.setAnimation(fromTop);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeScreenActivity.this, MainActivity.class));

                finish();

            }
        },3000);

    }
}
