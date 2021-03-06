package app.movieservice.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import movieservice.domain.Movie;
import movieservice.domain.SearchCriteria;
import movieservice.util.CalendarUtil;
import movieservice.util.Coordinate;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import app.movieservice.dialog.LanguageDialogFragment;
import app.movieservice.util.ConstantUtil;

import com.google.gson.Gson;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

	private LocationManager locationManager;
	private Criteria locationCritera;
	private LocationListener locListener;

	private EditText textMovieName, textCinema;
	private CheckBox cbToday, cbToday1, cbToday2, cbToday3, cbToday4;
	private Double latitude, longitude;

	private String providerName;
	private Spinner spinnerDistance;
	private Button buttonSubmit;
	private Button buttonReset;

	private TextView textGps;
	private TextView textNetwork;

	public TextView tvProviderName;
	public TextView tvLatitude;
	public TextView tvLongitude;
	public TextView tvSystemMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
		setContentView(R.layout.activity_main);

//		AdView mAdView = (AdView) findViewById(R.id.adView);		 
//        mAdView.setAdListener(new ToastAdListener(this));
//        mAdView.loadAd(new AdRequest.Builder().build());


		locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

		spinnerDistance = (Spinner) findViewById(R.id.spinner_distance);
		textGps = (TextView) findViewById(R.id.text_gps);
		textNetwork = (TextView) findViewById(R.id.text_network);

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

		buttonReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resetSpinnerValue();
				textMovieName.setText(null);
				textCinema.setText(null);

				cbToday.setChecked(true);
				cbToday1.setChecked(false);
				cbToday2.setChecked(false);
				cbToday3.setChecked(false);
				cbToday4.setChecked(false);

			}

		});

		final Gson gson = new Gson();

		buttonSubmit = (Button) findViewById(R.id.button_submit);

		buttonSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				buttonSubmit.setEnabled(false);

				SearchCriteria searchCriteria = new SearchCriteria();

				Locale locale = getResources().getConfiguration().locale;
				searchCriteria.setLanguage(locale.getLanguage());

				int selPosition = spinnerDistance.getSelectedItemPosition();

				int[] arrDistances = getResources().getIntArray(R.array.array_distance_value);
				Integer distance = arrDistances[selPosition];

				searchCriteria.setDistanceRange(distance);

				// if (searchCriteria.getDistanceRange() != null && latitude != null && longitude != null) {
				if (latitude != null && longitude != null) {

					searchCriteria.setX(latitude);
					searchCriteria.setY(longitude);
				}

				String movieName = textMovieName.getText().toString();
				searchCriteria.setMovieName(movieName);

				String cinema = textCinema.getText().toString();
				searchCriteria.setCinema(cinema);

				List<SearchCriteria.ShowingDate> listShowingDate = new ArrayList<SearchCriteria.ShowingDate>();

				packShowingDates(cbToday, searchCriteria, listShowingDate);
				packShowingDates(cbToday1, searchCriteria, listShowingDate);
				packShowingDates(cbToday2, searchCriteria, listShowingDate);
				packShowingDates(cbToday3, searchCriteria, listShowingDate);
				packShowingDates(cbToday4, searchCriteria, listShowingDate);

				searchCriteria.setShowingDates(listShowingDate);

				// TODO: Using Spring Android to submit the SearchCriteria to HTTP GET METHOD.
				SearchMoviesTask task = new SearchMoviesTask();

				String strSearchCriteria = gson.toJson(searchCriteria);
				task.execute(strSearchCriteria);

			}

		});

		// TODO: DEBUG only
		tvSystemMessage = (TextView) findViewById(R.id.tvSystemMessage);
		tvSystemMessage.append("onCreate ");
		tvLatitude = (TextView) findViewById(R.id.tvLatitude);
		tvLongitude = (TextView) findViewById(R.id.tvLongitude);
	}

	private class SearchMoviesTask extends AsyncTask<String, Void, Movie[]> {
		// private class SearchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

		@Override
		protected Movie[] doInBackground(String... params) {
			// protected List<Movie> doInBackground(String ... params) {

			String url = ConstantUtil.REMOTEHOST_ANDROID + "/movie/getMovies/{searchCriteria}";

			// Create a new RestTemplate instance
			RestTemplate restTemplate = new RestTemplate();

			// Add the Jackson message converter
			restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
			// restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			// Make the HTTP GET request, marshaling the response from JSON to an array of Events
			Movie[] arrMovies = restTemplate.getForObject(url, Movie[].class, params[0]);
			return arrMovies;

			// List<Movie> movies = Arrays.asList(arrMovies);
			// return movies;
		}

		@Override
		protected void onPostExecute(Movie[] arrMovies) {
			// protected void onPostExecute(List<Movie> arrMovies) {

			// List<Movie> movies = Arrays.asList(arrMovies);
			//
			// for(int i=0; i < result.size(); i++){
			//
			// Movie movie = (Movie) result.get(i);
			// System.out.println("Movie Name: " + movie.getMovieName() + ", Cinema: " + movie.getCinema() +
			// ", Distance: " + movie.getRelativeDistance() + ", Time: " + movie.getShowingDate().getTime() + ", Fee: $"
			// + movie.getFee());
			// }

			if (arrMovies.length == 0) {

				buttonSubmit.setEnabled(true);
				Toast.makeText(getApplicationContext(), R.string.empty_result, Toast.LENGTH_SHORT).show();
			} else {

				Intent intent = new Intent(getApplicationContext(), ResultActivity.class);

				// intent.putParcelableArrayListExtra("searchResult", (ArrayList<? extends Parcelable>) movies);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPOutputStream gzipOut;
				ObjectOutputStream objectOut;

				try {
					gzipOut = new GZIPOutputStream(baos);
					objectOut = new ObjectOutputStream(gzipOut);

					for (int i = 0; i < arrMovies.length; i++) {

						objectOut.writeObject(arrMovies[i]);
					}
					objectOut.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

				byte[] bytes = baos.toByteArray();

				// intent.putExtra("searchResult", arrMovies);
				intent.putExtra("searchResult", bytes);
				intent.putExtra("searchResultSize", arrMovies.length);

				startActivity(intent);

			}

		}

	}

	private void packShowingDates(final CheckBox cbDate, final SearchCriteria searchCriteria, final List<SearchCriteria.ShowingDate> listShowingDate) {

		if (cbDate.isChecked()) {
			Calendar calDate = CalendarUtil.getCalendarByString(cbDate.getText().toString(), CalendarUtil.DEFAULT_DATE_FORMAT);
			SearchCriteria.ShowingDate showingDate = searchCriteria.new ShowingDate();
			showingDate.setShowingDate(calDate);
			listShowingDate.add(showingDate);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.item_language:
			new LanguageDialogFragment(this);

			break;
		}

		return super.onOptionsItemSelected(item);
	}

	// Turn off location updates if we're paused
	@Override
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(locListener);
		tvSystemMessage.append("onPause ");
	}

	// Resume location updates when we're resumed
	@Override
	public void onResume() {
		super.onResume();

		buttonSubmit.setEnabled(true);

		setLocationServiceIcon();

		if (isLocationProviderEnabled()) {
			updateLocation();
		} else {
			resetSpinnerValue();
		}

		generate5ShowingDate();
		tvSystemMessage.append("onResume ");
	}

	private boolean isLocationProviderEnabled() {

		locationCritera = new Criteria();
		locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
		locationCritera.setAltitudeRequired(false);
		locationCritera.setBearingRequired(false);
		locationCritera.setCostAllowed(true);
		locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);

		providerName = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? locationManager.getProvider(
				LocationManager.NETWORK_PROVIDER).getName() : locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? locationManager
				.getProvider(LocationManager.GPS_PROVIDER).getName() : null;
		// providerName = locationManager.getBestProvider(locationCritera, true);

		if (providerName != null && !providerName.equalsIgnoreCase("passive") && locationManager.isProviderEnabled(providerName)) {
			// TODO: DEBUG ONLY
			TextView tvProviderName = (TextView) findViewById(R.id.tvProviderName);
			tvProviderName.setText(providerName);
			return true;
		}
		return false;
	}

	private void updateLocation() {

		locationManager.requestLocationUpdates(providerName, 5000L, 0.0f, locListener);
		tvSystemMessage.append("updateLocation ");
	}

	private void promptLocationService() {
		// Prompt user to enable it
		Toast.makeText(MainActivity.this, "Open Location Service Setting...", Toast.LENGTH_LONG).show();
		Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		MainActivity.this.startActivity(myIntent);
	}

	private void setLocationServiceIcon() {

		boolean isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		textGps.setBackgroundResource(isGpsProviderEnabled == true ? R.color.green : R.color.red);
		textNetwork.setBackgroundResource(isNetworkProviderEnabled == true ? R.color.green : R.color.red);

		// buttonSubmit.setEnabled(isGpsProviderEnabled == true ? false : isNetworkProviderEnabled == true ? false :
		// true);
	}

	private class DispLocListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			if (location != null) {

				latitude = location.getLatitude();
				longitude = location.getLongitude();

				String usedProvider = location.getProvider();

				if (usedProvider.equalsIgnoreCase("gps")) {
					textGps.setBackgroundResource(R.color.light_blue);
				}

				if (usedProvider.equalsIgnoreCase("network")) {
					textNetwork.setBackgroundResource(R.color.light_blue);
				}

				// boolean isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				// boolean isNetworkProviderEnabled =
				// locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				//
				// if(isGpsProviderEnabled==true){
				// textGps.setBackgroundResource(R.color.blue);
				// }
				//
				// if(isNetworkProviderEnabled==true){
				// textNetwork.setBackgroundResource(R.color.blue);
				// }

				// buttonSubmit.setEnabled(true);

				// TODO: DEBUG ONLY
				tvLatitude.setText(Double.toString(location.getLatitude()));
				tvLongitude.setText(Double.toString(location.getLongitude()));
				// tvAltitude.setText(Double.toString(location.getAltitude()));
				// tvAccuracy.setText(Double.toString(location.getAccuracy()));
			} else {
				// tvProviderStatus.setText("3");
				// tvProviderStatus.setText("No location info available: " + System.currentTimeMillis());
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			locationManager.removeUpdates(locListener);
			// Reset the latitude and longitude to NULL
			latitude = null;
			longitude = null;

			onResume();
			// setLocationServiceIcon();
			// if(isLocationProviderEnabled()){
			// updateLocation();
			// }
			// resetSpinnerValue();

			tvSystemMessage.append("onProviderDisabled ");
		}

		@Override
		public void onProviderEnabled(String provider) {
			// tvSystemMessage.append("onProviderEnabled ");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}

	private void resetSpinnerValue() {
		// Set Distance Spinner Value to Any Area (NULL)
		spinnerDistance.setSelection(0);
	}

	private void generate5ShowingDate() {

		Locale locale = getResources().getConfiguration().locale;

		Calendar calToday = CalendarUtil.getSystemCalendar();
		String today = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT, locale);
		cbToday.setText(today);

		calToday.add(Calendar.DATE, 1);
		String today1 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT, locale);
		cbToday1.setText(today1);

		calToday.add(Calendar.DATE, 1);
		String today2 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT, locale);
		cbToday2.setText(today2);

		calToday.add(Calendar.DATE, 1);
		String today3 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT, locale);
		cbToday3.setText(today3);

		calToday.add(Calendar.DATE, 1);
		String today4 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT, locale);
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

	/**
	 * A placeholder fragment containing a simple view. This fragment would include your content.
	 */
//	public static class PlaceholderFragment extends Fragment {
//
//		public PlaceholderFragment() {
//		}
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//			View rootView = inflater.inflate(R.layout.fragment_my, container, false);
//			return rootView;
//		}
//	}

	/**
	 * This class makes the ad request and loads the ad.
	 */
	public static class AdFragment extends Fragment {

		private AdView mAdView;

		public AdFragment() {
		}

		@Override
		public void onActivityCreated(Bundle bundle) {
			super.onActivityCreated(bundle);

			// Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
			// values/strings.xml.
			mAdView = (AdView) getView().findViewById(R.id.adView);

			// Create an ad request. Check logcat output for the hashed device ID to
			// get test ads on a physical device. e.g.
			// "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
			//AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
			AdRequest adRequest = new AdRequest.Builder().build();

			// Start loading the ad in the background.
			mAdView.loadAd(adRequest);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_ad, container, false);
		}

		/** Called when leaving the activity */
		@Override
		public void onPause() {
			if (mAdView != null) {
				mAdView.pause();
			}
			super.onPause();
		}

		/** Called when returning to the activity */
		@Override
		public void onResume() {
			super.onResume();
			if (mAdView != null) {
				mAdView.resume();
			}
		}

		/** Called before the activity is destroyed */
		@Override
		public void onDestroy() {
			if (mAdView != null) {
				mAdView.destroy();
			}
			super.onDestroy();
		}

	}

}
