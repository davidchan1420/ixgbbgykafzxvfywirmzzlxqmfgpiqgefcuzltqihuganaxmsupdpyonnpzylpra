package android.movieservice.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import movieservice.domain.Movie;
import movieservice.domain.Temp1;
import movieservice.util.CalendarUtil;
import android.app.Activity;
import android.content.Intent;
import android.movieservice.util.ConstantUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
		
		Parcelable[] arrParcelable= (Parcelable[]) intent.getParcelableArrayExtra("searchResult");
		
		Movie[] arrMovies = new Movie[arrParcelable.length];
	
		for(int i=0; i < arrParcelable.length; i++){
			arrMovies[i] = (Movie) arrParcelable[i];
		}
		
		List<Movie> movies = Arrays.asList(arrMovies);
	
		constructResultTable(movies);
	}
	
	private void constructResultTable(List<Movie> movies) {

		TableLayout tableResult = (TableLayout) findViewById(R.id.tablelayout_result);
		String previousMovieName = null;
		String resultDateTimeFormat = "MM-dd HH:mm (EEE)";
		
		for (int i = 0; i < movies.size(); i++) {

			Movie movie = movies.get(i);
			
			
			TableRow tableRowMovieName = (TableRow) getLayoutInflater().inflate(R.layout.fragment_result_table_row_moviename, tableResult, false);
			TableRow tableRowMovieDetail = (TableRow) getLayoutInflater().inflate(R.layout.fragment_result_table_row_moviedetail, tableResult, false);
			
//			int rowColor;
//			if (i < 3) {
//				rowColor = R.color.ranking_top;
//			} else if (i < 15) {
//				rowColor = R.color.ranking_middle;
//			} else {
//				rowColor = R.color.ranking_other;
//			}
//			tableRow.setBackgroundColor(getResources().getColor(rowColor));
			if(previousMovieName == null || !previousMovieName.equalsIgnoreCase(movie.getMovieName())){
				TextView textMovieName = (TextView) tableRowMovieName.findViewById(R.id.movie_name);
				textMovieName.setText(movie.getMovieName());
				tableResult.addView(tableRowMovieName);
			}

			TextView textCinema = (TextView) tableRowMovieDetail.findViewById(R.id.cinema);
			textCinema.setText(movie.getCinema());

			TextView textDistance = (TextView) tableRowMovieDetail.findViewById(R.id.relative_distance);
			textDistance.setText(movie.getRelativeDistance() != null ? movie.getRelativeDistance().toString() : ConstantUtil.NOT_AVAILABLE);
						
			Locale locale = getResources().getConfiguration().locale;
			String showingDate = CalendarUtil.getFormatDateString(movie.getShowingDate(), resultDateTimeFormat, locale);
			TextView textShowingDate = (TextView) tableRowMovieDetail.findViewById(R.id.showing_date);
			textShowingDate.setText(showingDate);

			TextView textFee = (TextView) tableRowMovieDetail.findViewById(R.id.fee);
			textFee.setText(movie.getFee() != null ? movie.getFee().toString() : ConstantUtil.NOT_AVAILABLE);
			
			tableResult.addView(tableRowMovieDetail);
		}

	}

}
