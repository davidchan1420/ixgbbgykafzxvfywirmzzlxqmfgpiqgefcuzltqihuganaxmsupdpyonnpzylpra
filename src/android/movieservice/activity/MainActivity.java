package android.movieservice.activity;


import java.util.Calendar;

import movieservice.util.CalendarUtil;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		generate5ShowingDate();
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void generate5ShowingDate(){
		
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
			
			if(isChecked == true)
				return;			
		
			CheckBox cbToday = (CheckBox) findViewById(R.id.checkBox_today);			
			CheckBox cbToday1 = (CheckBox) findViewById(R.id.checkBox_today_1);
			CheckBox cbToday2 = (CheckBox) findViewById(R.id.checkBox_today_2);
			CheckBox cbToday3 = (CheckBox) findViewById(R.id.checkBox_today_3);
			CheckBox cbToday4 = (CheckBox) findViewById(R.id.checkBox_today_4);
			
			if(cbToday.isChecked()==true)
				return;
			if(cbToday1.isChecked()==true)
				return;
			if(cbToday2.isChecked()==true)
				return;
			if(cbToday3.isChecked()==true)
				return;
			if(cbToday4.isChecked()==true)
				return;
			
			buttonView.setChecked(true);			
		}
		
	}

}
