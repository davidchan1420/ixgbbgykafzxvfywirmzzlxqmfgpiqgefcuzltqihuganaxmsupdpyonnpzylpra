package android.movieservice.activity;

import java.util.Calendar;

import movieservice.util.CalendarUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LocationManager locationManager;
	private Criteria locationCritera;
	private LocationListener locListener;
	
	private EditText textMovieName, textCinema;
	private CheckBox cbToday, cbToday1, cbToday2, cbToday3, cbToday4;

	private String providerName;
	private Spinner spinnerDistance;
	private Button buttonReset;
	
	public TextView tvProviderName;
	public TextView tvLatitude;
	public TextView tvLongitude;
	public TextView tvSystemMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		spinnerDistance = (Spinner) findViewById(R.id.spinner_distance);
		textMovieName = (EditText) findViewById(R.id.text_movie_name);
		textCinema = (EditText) findViewById(R.id.text_cinema);
		
		cbToday = (CheckBox) findViewById(R.id.checkBox_today);
		cbToday1 = (CheckBox) findViewById(R.id.checkBox_today_1);
		cbToday2 = (CheckBox) findViewById(R.id.checkBox_today_2);
		cbToday3 = (CheckBox) findViewById(R.id.checkBox_today_3);
		cbToday4 = (CheckBox) findViewById(R.id.checkBox_today_4);		
		
		locListener = new DispLocListener();
	
		spinnerDistance.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (!isLocationProviderEnabled()) {
						promptLocationService();
						return true;
					}
				}
				return false;
			}
		});

		generate5ShowingDate();
		
		buttonReset = (Button) findViewById(R.id.button_reset);
		
		buttonReset.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				spinnerDistance.setSelection(0);
				textMovieName.setText(null);
				textCinema.setText(null);
				cbToday.setChecked(true);
				cbToday1.setChecked(false);
				cbToday2.setChecked(false);
				cbToday3.setChecked(false);
				cbToday4.setChecked(false);				
			}
			
		});

		tvSystemMessage = (TextView) findViewById(R.id.tvSystemMessage);		
		tvSystemMessage.append("onCreate ");
		tvLatitude = (TextView) findViewById(R.id.tvLatitude);
		tvLongitude = (TextView) findViewById(R.id.tvLongitude);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	private boolean isLocationProviderEnabled() {

		locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

		locationCritera = new Criteria();
		locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
		locationCritera.setAltitudeRequired(false);
		locationCritera.setBearingRequired(false);
		locationCritera.setCostAllowed(true);
		locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);

		providerName = locationManager.getBestProvider(locationCritera, true);

		if (providerName != null && !providerName.equalsIgnoreCase("passive") && locationManager.isProviderEnabled(providerName)) {
			
			TextView tvProviderName = (TextView) findViewById(R.id.tvProviderName);		
			tvProviderName.setText(providerName);			
			return true;
		}
		return false;
	}
	
	private void updateLocation(){
		
		locationManager.requestLocationUpdates(providerName, 5000L, 0.0f, locListener);		
		tvSystemMessage.append("updateLocation ");
	}

	private void promptLocationService() {

		// Prompt user to enable it
		Toast.makeText(MainActivity.this, "Open GPS now...", Toast.LENGTH_LONG).show();
		Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		MainActivity.this.startActivity(myIntent);
	}

	private void setLocationServiceIcon(){
		
		boolean isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		TextView textGps = (TextView) findViewById(R.id.text_gps);
		TextView textNetwork = (TextView) findViewById(R.id.text_network);
		
		textGps.setBackgroundResource(isGpsProviderEnabled==true ? R.color.green : R.color.red);
		textNetwork.setBackgroundResource(isNetworkProviderEnabled==true ? R.color.green : R.color.red);	
	}
	
	private class DispLocListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

//			tvProviderStatus.setText("1");
			if (location != null) {
//				tvProviderStatus.setText("2");
				// update TextViews
				tvLatitude.setText(Double.toString(location.getLatitude()));
				tvLongitude.setText(Double.toString(location.getLongitude()));
//				tvAltitude.setText(Double.toString(location.getAltitude()));
//				tvAccuracy.setText(Double.toString(location.getAccuracy()));
			} else {
//				tvProviderStatus.setText("3");
//				tvProviderStatus.setText("No location info available: " + System.currentTimeMillis());
			}
//			tvProviderStatus.setText("4");
//			lm.removeUpdates(this);
		}

		@Override
		public void onProviderDisabled(String provider) {
			locationManager.removeUpdates(locListener);
			tvSystemMessage.append("onProviderDisabled ");
		}

		@Override
		public void onProviderEnabled(String provider) {
			tvSystemMessage.append("onProviderEnabled ");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}
	
//	Turn off location updates if we're paused
	@Override
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(locListener);
		tvSystemMessage.append("onPause ");
	}

	//	Resume location updates when we're resumed
	@Override
	public void onResume() {
		super.onResume();
		if(isLocationProviderEnabled()){
			updateLocation();
		}else{
			//Set Distance Spinner Value to Any Area (NULL)
			spinnerDistance.setSelection(0);			
		}
		setLocationServiceIcon();
		tvSystemMessage.append("onResume ");
	}
	
	
	private void generate5ShowingDate() {

		Calendar calToday = CalendarUtil.getSystemCalendar();
		String today = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);

		cbToday.setText(today);

		calToday.add(Calendar.DATE, 1);
		String today1 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);
		cbToday1.setText(today1);

		calToday.add(Calendar.DATE, 1);
		String today2 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);
		cbToday2.setText(today2);

		calToday.add(Calendar.DATE, 1);
		String today3 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);
		cbToday3.setText(today3);

		calToday.add(Calendar.DATE, 1);
		String today4 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);
		cbToday4.setText(today4);

		CheckBoxListener listener = new CheckBoxListener();

		cbToday.setOnCheckedChangeListener(listener);
		cbToday1.setOnCheckedChangeListener(listener);
		cbToday2.setOnCheckedChangeListener(listener);
		cbToday3.setOnCheckedChangeListener(listener);
		cbToday4.setOnCheckedChangeListener(listener);

	}

	private class CheckBoxListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

			if (isChecked == true)
				return;

			if (cbToday.isChecked() == true)
				return;
			if (cbToday1.isChecked() == true)
				return;
			if (cbToday2.isChecked() == true)
				return;
			if (cbToday3.isChecked() == true)
				return;
			if (cbToday4.isChecked() == true)
				return;

			buttonView.setChecked(true);
		}
	}

	
	
	
	
}
