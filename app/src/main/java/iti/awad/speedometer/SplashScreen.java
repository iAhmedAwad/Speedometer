package iti.awad.speedometer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
  private static final long SPLASH_TIME_OUT = 4000;
  private Animation mTop, mBottom;
  private ImageView mImageView;
  private TextView xSlogan, xAppName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    mImageView = findViewById(R.id.imageView);
    xAppName = findViewById(R.id.xAppName);
    xSlogan = findViewById(R.id.xSlogan);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    mTop = AnimationUtils.loadAnimation(this, R.anim.top_animation);
    mBottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

    mImageView.setAnimation(mTop);

    xAppName.setAnimation(mBottom);
    xSlogan.setAnimation(mBottom);

    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent intent = new Intent(SplashScreen.this,
            SpeedometerActivity.class);
        startActivity(intent);
        finish();
      }

    }, SPLASH_TIME_OUT);

  }
}