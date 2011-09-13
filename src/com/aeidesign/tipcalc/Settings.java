package com.aeidesign.tipcalc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.ToggleButton;

public class Settings extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		loadSavedSettings();
	}
	
	public void modifySettings(View v){
		SharedPreferences settings = getSharedPreferences("settings", 0);
	    SharedPreferences.Editor editor = settings.edit();

		switch (v.getId()) {
		case R.id.rGenderFemale:
			editor.putString("gender", "female");
			break;
		case R.id.rGenderMale:
			editor.putString("gender", "male");
			break;
		case R.id.rRoundUpDown:
			editor.putString("round", "updown");
			break;
		case R.id.rRoundUp:
			editor.putString("round", "up");
			break;
		case R.id.rRoundDown:
			editor.putString("round", "down");
			break;
		case R.id.tbDrunkMode:
			editor.putBoolean("drunk", ((ToggleButton) v).isChecked());
			break;

		default:
			break;
		}
		
		editor.commit();
		
		Intent intent = new Intent();
        setResult(RESULT_OK, intent);
	}
	
	private void loadSavedSettings(){
    	SharedPreferences settings = getSharedPreferences("settings", 0);
    	String roundMode = settings.getString("round", "updown");
    	String genderMode = settings.getString("gender", "male");
    	Boolean drunkMode = settings.getBoolean("drunk", true);
    	
    	((ToggleButton) findViewById(R.id.tbDrunkMode)).setChecked( drunkMode );
    	
    	if ( roundMode.equals( "updown" ) )
    		((RadioButton) findViewById(R.id.rRoundUpDown)).setChecked(true);
    	else if ( roundMode.equals( "up" ) )
    		((RadioButton) findViewById(R.id.rRoundUp)).setChecked(true);
    	else if ( roundMode.equals( "down" ) )
    		((RadioButton) findViewById(R.id.rRoundDown)).setChecked(true);
    	
    	if ( genderMode.equals( "male" ) )
    		((RadioButton) findViewById(R.id.rGenderMale)).setChecked(true);
    	else 
    		((RadioButton) findViewById(R.id.rGenderFemale)).setChecked(true);

    }
}
