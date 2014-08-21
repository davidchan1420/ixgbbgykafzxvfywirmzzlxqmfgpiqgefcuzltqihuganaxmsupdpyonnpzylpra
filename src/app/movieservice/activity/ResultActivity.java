package app.movieservice.activity;

import java.text.DecimalFormat;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.util.StringUtils;

import java.util.zip.GZIPInputStream;

import movieservice.domain.Movie;
import movieservice.domain.Temp1;
import movieservice.util.CalendarUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import app.movieservice.util.ConstantUtil;

public class ResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		onNewIntent(getIntent());
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}


	@Override
	public void onNewIntent(Intent intent) {
		
//		Parcelable[] arrParcelable= (Parcelable[]) intent.getParcelableArrayExtra("searchResult");
		
		byte[] bytes = (byte[]) intent.getByteArrayExtra("searchResult");
		int size = intent.getIntExtra("searchResultSize", 0);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		GZIPInputStream gzipIn;
		ObjectInputStream objectIn;
		Movie[] arrMovies = new Movie[size];
		
		try {
			gzipIn = new GZIPInputStream(bais);
			objectIn = new ObjectInputStream(gzipIn);			
			
			for(int i=0; i < size; i++){
			
				Parcelable parcelable = (Parcelable) objectIn.readObject();
				Movie movie = (Movie) parcelable;
				
				arrMovies[i] = movie;						
			}		
			
			objectIn.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		
//		Movie[] arrMovies = new Movie[arrParcelable.length];
//	
//		for(int i=0; i < arrParcelable.length; i++){
//			arrMovies[i] = (Movie) arrParcelable[i];
//		}
		
		List<Movie> movies = Arrays.asList(arrMovies);
	
		constructResultTable(movies);
	}
	
	private void constructResultTable(List<Movie> movies) {

		TableLayout tableResult = (TableLayout) findViewById(R.id.tablelayout_result);
		String previousMovieName = ConstantUtil.EMPTY_STRING;
		String previousCinema = ConstantUtil.EMPTY_STRING;
		
		String resultDateTimeFormat = "MMM-dd HH:mm (EEE)";
		Locale locale = getResources().getConfiguration().locale;
		
		DecimalFormat decimalFormat = new DecimalFormat("#.#");
		
		for (int i = 0; i < movies.size(); i++) {

			Movie movie = movies.get(i);			
			
			TableRow tableRowMovieName = (TableRow) getLayoutInflater().inflate(R.layout.fragment_result_table_row_moviename, tableResult, false);
			TableRow tableRowCinemaDetail = (TableRow) getLayoutInflater().inflate(R.layout.fragment_result_table_row_cinemadetail, tableResult, false);
			TableRow tableRowMovieDetail = (TableRow) getLayoutInflater().inflate(R.layout.fragment_result_table_row_moviedetail, tableResult, false);
			
//			tableRowMovieName.setBackgroundColor(getResources().getColor(i%2 == 0 ? R.color.result_table_background_even : R.color.result_table_background_odd));
//			tableRowMovieDetail.setBackgroundColor(getResources().getColor(i%2 == 0 ? R.color.result_table_background_even : R.color.result_table_background_odd));
			
			tableRowMovieName.setBackgroundColor(getResources().getColor(R.color.result_table_moviename_background));
			tableRowCinemaDetail.setBackgroundColor(getResources().getColor(R.color.result_table_cinemadetail_background));
			
			if(!previousMovieName.equalsIgnoreCase(movie.getMovieName())){
				TextView textMovieName = (TextView) tableRowMovieName.findViewById(R.id.movie_name);
				textMovieName.setText(movie.getMovieName());
				tableResult.addView(tableRowMovieName);

			}

			if(!previousMovieName.equalsIgnoreCase(movie.getMovieName()) || !previousCinema.equalsIgnoreCase(movie.getCinema())){
				TextView textCinema = (TextView) tableRowCinemaDetail.findViewById(R.id.cinema);
				textCinema.setText(movie.getCinema());
				
				TextView textDistance = (TextView) tableRowCinemaDetail.findViewById(R.id.relative_distance);				
				StringBuilder distance = new StringBuilder();
				
				if(movie.getRelativeDistance() != null){
					
					distance.append(decimalFormat.format(movie.getRelativeDistance()));					
				}else{
					
					distance.append(ConstantUtil.NOT_AVAILABLE).append(ConstantUtil.SPACE);	
				}
				
				distance.append(ConstantUtil.KILOMETER);				

				textDistance.setText(distance);
				tableResult.addView(tableRowCinemaDetail);
			}			
									
			String showingDate = CalendarUtil.getFormatDateString(movie.getShowingDate(), resultDateTimeFormat, locale);
			TextView textShowingDate = (TextView) tableRowMovieDetail.findViewById(R.id.showing_date);
			textShowingDate.setText(showingDate);

			TextView textFee = (TextView) tableRowMovieDetail.findViewById(R.id.fee);
			
			StringBuilder movieFee = new StringBuilder(ConstantUtil.DOLLAR_SIGN);
			movieFee.append(movie.getFee() != null ? movie.getFee().toString() : ConstantUtil.NOT_AVAILABLE);			
			textFee.setText(movieFee);			
			
			tableResult.addView(tableRowMovieDetail);
			
			previousMovieName = movie.getMovieName();
			previousCinema = movie.getCinema();
		}

	}

}
