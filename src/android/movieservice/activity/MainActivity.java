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
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LocationManager locationManager;
	private Criteria locationCritera;
	private LocationListener locListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		locListener = new DispLocListener();

		generate5ShowingDate();

		Spinner spDistance = (Spinner) findViewById(R.id.spinner_distance);

		spDistance.setOnTouchListener(new OnTouchListener() {

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

		String providerName = locationManager.getBestProvider(locationCritera, true);

		if (providerName != null && !providerName.equalsIgnoreCase("passive") && locationManager.isProviderEnabled(providerName)) {

			return true;
		}
		return false;
	}

	private void promptLocationService() {

		// Prompt user to enable it
		Toast.makeText(MainActivity.this, "Open GPS now...", Toast.LENGTH_LONG).show();
		Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		MainActivity.this.startActivity(myIntent);

	}

	private class DispLocListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

//			tvProviderStatus.setText("1");
			if (location != null) {
//				tvProviderStatus.setText("2");
				// update TextViews
//				tvLatitude.setText(Double.toString(location.getLatitude()));
//				tvLongitude.setText(Double.toString(location.getLongitude()));
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
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}

	private void generate5ShowingDate() {

		Calendar calToday = CalendarUtil.getSystemCalendar();
		String today = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);

		CheckBox cbToday = (CheckBox) findViewById(R.id.checkBox_today);
		cbToday.setText(today);

		calToday.add(Calendar.DATE, 1);
		String today1 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);
		CheckBox cbToday1 = (CheckBox) findViewById(R.id.checkBox_today_1);
		cbToday1.setText(today1);

		calToday.add(Calendar.DATE, 1);
		String today2 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);
		CheckBox cbToday2 = (CheckBox) findViewById(R.id.checkBox_today_2);
		cbToday2.setText(today2);

		calToday.add(Calendar.DATE, 1);
		String today3 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);
		CheckBox cbToday3 = (CheckBox) findViewById(R.id.checkBox_today_3);
		cbToday3.setText(today3);

		calToday.add(Calendar.DATE, 1);
		String today4 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);
		CheckBox cbToday4 = (CheckBox) findViewById(R.id.checkBox_today_4);
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

			CheckBox cbToday = (CheckBox) findViewById(R.id.checkBox_today);
			CheckBox cbToday1 = (CheckBox) findViewById(R.id.checkBox_today_1);
			CheckBox cbToday2 = (CheckBox) findViewById(R.id.checkBox_today_2);
			CheckBox cbToday3 = (CheckBox) findViewById(R.id.checkBox_today_3);
			CheckBox cbToday4 = (CheckBox) findViewById(R.id.checkBox_today_4);

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
