package iti.awad.speedometer;

import androidx.lifecycle.ViewModel;

public class SpeedometerActivityViewModel extends ViewModel {


  private double currentSpeed;
  private long currentTime;
  private double previousSpeed;
  private long previousTime;
  private long timeOfThirty;
  private long timeOfTen;
  private long timeTenToThirty;
  private long timeThirtyToTen;
  private double latitude;
  private double longitude;
  private boolean isFirstTime;
  private boolean tenChanged;
  private boolean thirtyChanged;
  private float[] results = new float[1];

  public SpeedometerActivityViewModel() {
    currentSpeed = 0.0;
    currentTime=0;
    previousTime = 0;
    previousSpeed = 0.0;
    timeTenToThirty = 0;
    timeThirtyToTen = 0;
    tenChanged = false;
    thirtyChanged = false;
    isFirstTime = true;
  }

  public double getCurrentSpeed() {
    return currentSpeed;
  }

  public void setCurrentSpeed(double currentSpeed) {
    this.currentSpeed = currentSpeed;
  }

  public long getCurrentTime() {
    return currentTime;
  }

  public void setCurrentTime(long currentTime) {
    this.currentTime = currentTime;
  }

  public double getPreviousSpeed() {
    return previousSpeed;
  }

  public void setPreviousSpeed(double previousSpeed) {
    this.previousSpeed = previousSpeed;
  }

  public long getPreviousTime() {
    return previousTime;
  }

  public void setPreviousTime(long previousTime) {
    this.previousTime = previousTime;
  }

  public long getTimeOfThirty() {
    return timeOfThirty;
  }

  public void setTimeOfThirty(long timeOfThirty) {
    this.timeOfThirty = timeOfThirty;
  }

  public long getTimeOfTen() {
    return timeOfTen;
  }

  public void setTimeOfTen(long timeOfTen) {
    this.timeOfTen = timeOfTen;
  }

  public long getTimeTenToThirty() {
    return timeTenToThirty;
  }

  public void setTimeTenToThirty(long timeTenToThirty) {
    this.timeTenToThirty = timeTenToThirty;
  }

  public long getTimeThirtyToTen() {
    return timeThirtyToTen;
  }

  public void setTimeThirtyToTen(long timeThirtyToTen) {
    this.timeThirtyToTen = timeThirtyToTen;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public boolean isFirstTime() {
    return isFirstTime;
  }

  public void setFirstTime(boolean firstTime) {
    isFirstTime = firstTime;
  }

  public boolean isTenChanged() {
    return tenChanged;
  }

  public void setTenChanged(boolean tenChanged) {
    this.tenChanged = tenChanged;
  }

  public boolean isThirtyChanged() {
    return thirtyChanged;
  }

  public void setThirtyChanged(boolean thirtyChanged) {
    this.thirtyChanged = thirtyChanged;
  }

  public float[] getResults() {
    return results;
  }

  public void setResults(float[] results) {
    this.results = results;
  }
}
