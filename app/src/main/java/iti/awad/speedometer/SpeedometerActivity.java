package iti.awad.speedometer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

public class SpeedometerActivity extends AppCompatActivity {

  //Constants
  private static final int LOCATION_PERMISSION_ID = 9;
  private static final int LOCATION_UPDATE_INTERVAL = 3000; // in milliseconds
  private static final double UPPER_SPEED = 60; //KMH
  private static final double LOWER_SPEED = 25; //KMH
  //Views
  ProgressBar mProgressBar;
  //Variables
  private LocationManager mLocationManager;
  private LocationListener mLocationListener;
  private SpeedometerActivityViewModel mViewModel;
  private ToggleButton xStartEndButton;
  private TextView xCurrentSpeed, xTimeAccelerate, xTimeDeceleration, xCurrentSpeedTitle;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_speedometer);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    mViewModel = new ViewModelProvider(this).get(SpeedometerActivityViewModel.class);
    //initialize views
    xStartEndButton = findViewById(R.id.xStartEndButton);
    xCurrentSpeed = findViewById(R.id.xCurrentSpeed);
    xTimeAccelerate = findViewById(R.id.xTimeAccelerate);
    xTimeDeceleration = findViewById(R.id.xTimeDeceleration);
    xCurrentSpeed.setText(String.format("%.1f", mViewModel.getCurrentSpeed()));
    xTimeAccelerate.setText(String.valueOf(mViewModel.getTimeThirtyToTen() / 1000));
    xTimeDeceleration.setText(String.valueOf(mViewModel.getTimeTenToThirty() / 1000));
    xCurrentSpeedTitle = findViewById(R.id.xCurrentSpeedTitle);

    initButton();
    initProgressBar();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void initButton() {
    if (xStartEndButton.isChecked()) {
      xStartEndButton.setBackground(getDrawable(R.drawable.ic_offbutton));
    } else {
      xStartEndButton.setBackgroundDrawable(getDrawable(R.drawable.ic_onbutton));
    }
    xStartEndButton.setOnCheckedChangeListener(initCheckListener());

  }

  private CompoundButton.OnCheckedChangeListener initCheckListener() {
    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
      @SuppressLint("NewApi")
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          xStartEndButton.setBackground(getDrawable(R.drawable.ic_offbutton));
          init();
          xCurrentSpeedTitle.setText(R.string.currentSpeed);
        } else {
          xStartEndButton.setBackgroundDrawable(getDrawable(R.drawable.ic_onbutton));
          stopTracking();
        }
      }
    };
    return listener;
  }


  private void init() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

      if (!isPermissionGranted()) {
        if (shouldExplainMotivesToUser()) {
          showPermissionExplanationDialogue();
        } else {
          requestPermission();
        }
      }
    } else {
      startTracking();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == LOCATION_PERMISSION_ID) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        startTracking();
      } else {
        Toast.makeText(this,
            "Permission is needed to access this feature",
            Toast.LENGTH_LONG).show();
        xStartEndButton.setOnCheckedChangeListener(null);
        xStartEndButton.setChecked(false);
        xStartEndButton.setBackgroundDrawable(getDrawable(R.drawable.ic_onbutton));
        xStartEndButton.setOnCheckedChangeListener(initCheckListener());

      }
    }
  }

  @SuppressLint("MissingPermission")
  private void startTracking() {

    mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    mLocationListener = new LocationListener() {

      @Override
      public void onLocationChanged(Location location) {

        hideProgressBar();
        if (mViewModel.isFirstTime()) {
          mViewModel.setLatitude(location.getLatitude());
          mViewModel.setLongitude(location.getLongitude());
          showProgressBar();
        }

        Location.distanceBetween(mViewModel.getLatitude(), mViewModel.getLongitude(),
            location.getLatitude(), location.getLongitude(),
            mViewModel.getResults());
        //Set previous speed and with the *last* current speed and time
        mViewModel.setPreviousSpeed(mViewModel.getCurrentSpeed());
        mViewModel.setPreviousTime(mViewModel.getCurrentTime());
        //set current speed with the new speed
        mViewModel.setCurrentSpeed(mViewModel.getResults()[0] / (LOCATION_UPDATE_INTERVAL / 1000.0) * 3.6);
        mViewModel.setCurrentTime(location.getTime());
        xCurrentSpeed.setText(String.format("%.1f", mViewModel.getCurrentSpeed()));

        setTimeDifference();

        mViewModel.setLatitude(location.getLatitude());
        mViewModel.setLongitude(location.getLongitude());
        mViewModel.setFirstTime(false);
      }

      @Override
      public void onStatusChanged(String provider, int status, Bundle extras) {

      }

      @Override
      public void onProviderEnabled(String provider) {

      }

      @Override
      public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
      }
    };

    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        LOCATION_UPDATE_INTERVAL,
        0, mLocationListener);

  }

  @SuppressLint("MissingPermission")
  private void stopTracking() {
    mLocationManager.removeUpdates(mLocationListener);
    xCurrentSpeedTitle.setText(R.string.lastRecorded);
    Toast.makeText(this, R.string.meterStopped, Toast.LENGTH_LONG).show();
  }

  /**
   *
    * @return true if permission is granted
   */
  private boolean isPermissionGranted() {
    if (ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    ) {
      return true;
    }
    return false;
  }

  /**
   * returns true if a dialogue should be shown to the user
   * explaining the need for permission
   * @return
   */

  private boolean shouldExplainMotivesToUser() {
    return ActivityCompat.shouldShowRequestPermissionRationale(
        SpeedometerActivity.this,
        Manifest.permission.ACCESS_FINE_LOCATION)
        && ActivityCompat.shouldShowRequestPermissionRationale(
        SpeedometerActivity.this,
        Manifest.permission.ACCESS_COARSE_LOCATION);
  }

  /**
   * shows explanation dialogue to the user
   */
  private void showPermissionExplanationDialogue() {
    new AlertDialog.Builder(this)
        .setTitle("Required permission")
        .setMessage("You have to give this permission for this feature to work!")
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            requestPermission();
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create()
        .show();
  }

  private void requestPermission() {
    ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION},
        LOCATION_PERMISSION_ID
    );

  }

  /**
   * sets the time fields in the ViewModel
   */
  private void setTimeDifference() {

    if (!mViewModel.isFirstTime()) {

      if ((mViewModel.getPreviousSpeed() <= UPPER_SPEED && UPPER_SPEED <= mViewModel.getCurrentSpeed())
          || (mViewModel.getPreviousSpeed() >= UPPER_SPEED && UPPER_SPEED >= mViewModel.getCurrentSpeed())
      ) {
        long avg = (mViewModel.getCurrentTime() + mViewModel.getPreviousTime()) / 2;
        mViewModel.setTimeOfThirty(avg);
        mViewModel.setThirtyChanged(true);
      }

      if ((mViewModel.getPreviousSpeed() <= LOWER_SPEED && LOWER_SPEED <= mViewModel.getCurrentSpeed())
          || (mViewModel.getPreviousSpeed() >= LOWER_SPEED && LOWER_SPEED >= mViewModel.getCurrentSpeed())
      ) {
        long avg = (mViewModel.getCurrentTime() + mViewModel.getPreviousTime()) / 2;

        mViewModel.setTimeOfTen(avg);
        mViewModel.setTenChanged(true);
      }


      if (mViewModel.getTimeOfThirty() != 0 && mViewModel.getTimeOfTen() != 0
          && mViewModel.isTenChanged() && mViewModel.isThirtyChanged()) {

        long deltaTime = mViewModel.getTimeOfThirty() - mViewModel.getTimeOfTen();
        if (mViewModel.getPreviousTime() != 0 && deltaTime > 0) {
          //10 happened before 30 .. deceleration
          mViewModel.setTimeThirtyToTen(deltaTime); // milliseconds
          xTimeAccelerate.setText(String.valueOf(mViewModel.getTimeThirtyToTen() / 1000));

        } else {
          if (mViewModel.getPreviousTime() != 0 && deltaTime < 0) {
            // 10 happened before 30 .. Acceleration
            mViewModel.setTimeTenToThirty(-deltaTime);
            xTimeDeceleration.setText(String.valueOf(mViewModel.getTimeTenToThirty() / 1000));
          }
        }

        mViewModel.setTenChanged(false);
        mViewModel.setThirtyChanged(false);

      }
    }
  }

  private void initProgressBar() {
    mProgressBar = findViewById(R.id.progressBar);
    mProgressBar.setVisibility(View.INVISIBLE);
  }

  private void showProgressBar() {
    mProgressBar.setVisibility(View.VISIBLE);

  }

  private void hideProgressBar() {
    if (mProgressBar.getVisibility() == View.VISIBLE) {
      mProgressBar.setVisibility(View.INVISIBLE);
    }
  }

}