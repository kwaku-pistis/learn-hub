package deemiensa.com.learnhub.Miscellaneous;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import deemiensa.com.learnhub.App.MainActivity;
import deemiensa.com.learnhub.R;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        int duration = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .getBoolean("isFirstRun", true);

                if (isFirstRun) {
                    // show start activity

                    startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
                    this.finish();
                    // Toast.makeText(MainActivity.this, "First Run", Toast.LENGTH_LONG)
                    //     .show();
                } else {

                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    this.finish();
                }
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("isFirstRun", false).apply();
            }

            private void finish() {

            }
        }, duration);
    }
}




