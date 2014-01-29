package android.movieservice.dialog;

import java.util.Locale;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.movieservice.activity.R;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class LanguageDialogFragment {

	public LanguageDialogFragment(final Activity activity){
		
		final Dialog dialog = new Dialog(activity);
		
		// Get the layout inflater
		LayoutInflater inflater = activity.getLayoutInflater();

//		builder.setMessage(R.string.choose_language);
		dialog.setTitle(R.string.choose_language);

		View viewLanguage = inflater.inflate(R.layout.dialog_language, null);
		
		// Add action buttons		
		RadioGroup radioGroupLanguage = (RadioGroup) viewLanguage.findViewById(R.id.radio_language);		
		RadioButton radioEnglish = (RadioButton) viewLanguage.findViewById(R.id.radio_english);		
		RadioButton radioChinese = (RadioButton) viewLanguage.findViewById(R.id.radio_chinese);
		
		Locale locale = activity.getBaseContext().getResources().getConfiguration().locale;
		
		final String english = activity.getString(R.string.en);
		final String chinese = activity.getString(R.string.zh);
		
		if(locale.getLanguage().equalsIgnoreCase(english)){
			radioEnglish.setChecked(true);
		}
		
		if(locale.getLanguage().equalsIgnoreCase(chinese)){
			radioChinese.setChecked(true);
		}		
		
		radioGroupLanguage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
		
//				System.out.println(checkedId);

				if(checkedId == R.id.radio_english){
//					System.out.println("english");
					setLocale(english, activity);
				}
				if(checkedId == R.id.radio_chinese){
//					System.out.println("chinese");
					setLocale(chinese, activity);
				}				
				dialog.dismiss();
				
				Intent intent = activity.getIntent();
				activity.finish();
				activity.startActivity(intent);			
				
			}
		});
				
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		dialog.setContentView(viewLanguage);	
//		dialog.setCancelable(false);
		dialog.show();	
	}
	
	private void setLocale(String lang, Context context) {
		Locale myLocale = new Locale(lang);
		Resources res = context.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		
	}
	
	
}
