package android.movieservice.activity;


import java.util.Calendar;

import movieservice.util.CalendarUtil;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.CheckBox;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		///
		Calendar calToday = CalendarUtil.getSystemCalendar();
		String today = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);
		
		CheckBox cbToday = (CheckBox) findViewById(R.id.checkBox_today);		
		cbToday.setText(today);
		
		calToday.add(Calendar.DATE, 1);
		
		String today1 = CalendarUtil.getFormatDateString(calToday, CalendarUtil.DEFAULT_DATE_FORMAT);
		
		CheckBox cbToday1 = (CheckBox) findViewById(R.id.checkBox_today_1);		
		cbToday1.setText(today1);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
