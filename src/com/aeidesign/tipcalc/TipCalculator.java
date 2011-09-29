package com.aeidesign.tipcalc;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.aeidesign.tipcalc.kankan.wheel.widget.OnWheelScrollListener;
import com.aeidesign.tipcalc.kankan.wheel.widget.WheelView;
import com.aeidesign.tipcalc.kankan.wheel.widget.adapters.NumericWheelAdapter;

public class TipCalculator extends Activity {
	private WheelView percentage;
	private WheelView split;
	private EditText bill;
	private CheckBox round;
	private TextView total;
	private TextView splitTotal;
	private TextView tipTotal;
	private TextView drunkText;
	
	private boolean drunkMode;
	private String genderMode;
	private String roundMode;
	
	private Map<String, String[]> tipText;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getSavedSettings();
        createMap();
        
        percentage = (WheelView) findViewById(R.id.percentage);
        split = (WheelView) findViewById(R.id.split);
        bill = (EditText) findViewById(R.id.bill);
        round = (CheckBox) findViewById(R.id.round);
        total = (TextView) findViewById(R.id.total);
        splitTotal = (TextView) findViewById(R.id.split_total);
        tipTotal = (TextView) findViewById(R.id.tip_total);
        drunkText = (TextView) findViewById(R.id.drunk_text);
        
        percentage.setVisibility(3);
        percentage.setViewAdapter(new NumericWheelAdapter(this, 1, 30, "%d%%"));
        percentage.setCurrentItem(14);
        
        
        split.setVisibility(3);
        split.setViewAdapter(new NumericWheelAdapter(this, 1, 20));
        
        percentage.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {}
			
			@Override
			public void onScrollingFinished(WheelView wheel) {
				calculateTip();
				if ( drunkMode )
					displayTipText();
			}
		});
        
        split.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {}
			
			@Override
			public void onScrollingFinished(WheelView wheel) {
				calculateTip();
			}
		});

        round.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				calculateTip();
			}
		});
        
        bill.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				calculateTip();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
    }
    
    private void calculateTip(){
    	String selectedItem = ((NumericWheelAdapter) percentage.getViewAdapter()).getItemText( percentage.getCurrentItem() ).toString();
    	selectedItem = selectedItem.substring(0, selectedItem.length() - 1);
    	Integer iPercentage = Integer.valueOf( selectedItem );
    	
		selectedItem = ((NumericWheelAdapter) split.getViewAdapter()).getItemText( split.getCurrentItem() ).toString();
		Integer iSplit = Integer.valueOf( selectedItem );
    
		if ( bill.getText().length() < 1)
			return;
		
		Double dBill = Double.valueOf( bill.getText().toString() );
		
		boolean bRound = round.isChecked();
		
		double dTip = dBill * iPercentage / 100;
		double dTotal = dBill + dTip;
		
		if ( bRound ){
			if ( roundMode.equals("updown") )
				dTotal = Math.round( dTotal );
			else if ( roundMode.equals("up") )
				dTotal = Math.ceil( dTotal );
			else if ( roundMode.equals("down") )
				dTotal = Math.floor( dTotal );
			
			if ( dTotal < dBill )
				dTotal += 1.00;
			
			dTip = dTotal - dBill;
		}
		
		double dSplitTotal = dTotal / iSplit;
		
		DecimalFormat twoDecimals = new DecimalFormat("#.##");
		DecimalFormat noDecimals = new DecimalFormat("#");
		
		tipTotal.setText("Tip  $" + twoDecimals.format(dTip) + " (" + noDecimals.format(dTip * 100/dTotal) + "%)");
		total.setText("Total  $" + twoDecimals.format( dTotal ) );
		if ( iSplit > 1)
			splitTotal.setText("Each pay  $" + twoDecimals.format(dSplitTotal));
		else 
			splitTotal.setText("");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
   	
    	switch (item.getItemId()) {
		case R.id.settingsMenu:
	    	Intent settingsIntent = new Intent(this, Settings.class);
	    	startActivityForResult( settingsIntent, 1 );	
	    	return true;
		default:
			return super.onOptionsItemSelected(item);
		}
    }

    
    public void onActivityResult(int requestCode, int resultCode, Intent data){
   		super.onActivityResult(requestCode, resultCode, data);
   		
   		if ( resultCode == RESULT_OK){
   			Toast.makeText(getApplicationContext(), "Settings saved ...", Toast.LENGTH_SHORT).show();
   			getSavedSettings();
   			createMap();   				
   			calculateTip();
   		}
    }
    
    private void getSavedSettings(){
    	SharedPreferences settings = getSharedPreferences("settings", 0);
    	roundMode = settings.getString("round", "updown");
    	genderMode = settings.getString("gender", "male");
    	drunkMode = settings.getBoolean("drunk", true);
    	
    	if ( drunkMode )
	    	if ( genderMode.equals("male") )
				findViewById(R.id.lMain).setBackgroundDrawable( getResources().getDrawable(R.drawable.waitress));
			else
				findViewById(R.id.lMain).setBackgroundDrawable( getResources().getDrawable(R.drawable.waiter));
    	else {
			findViewById(R.id.lMain).setBackgroundDrawable( getResources().getDrawable(R.drawable.background));
			drunkText.setText("");
    	}
    }
    
    private void createMap(){
    	tipText = new HashMap<String, String[]>();
    	
    	if (genderMode == "male"){
	    	tipText.put("cheap", new String[]{
	    		"Damn! You're damn cheap.",
	    		"The waitress must be damn ugly...",
	    		"Was the service really that bad?",
	    		"Easy tiger... you're not breaking the bank if you bump up the tip.",
	    		"Leaving a slightly larger tip won't break your stripper budget!",
	    		"The waitress might be ugly but she's still human...tip her a little more.",
	    		"The waitress isn't into you, but you don't have to be a jerk."
	    	});
	    	tipText.put("broke", new String[]{
	    		"You must be broke... and now the waitress will be too.",
	    		"You're not cheap but clearly your wallet is empty. So, why are you out spending money?",
	    		"Mediocre service = mediocre tip.",
	    		"The waitress isn't going home with you if you don't tip her a bit more."
	    	});
	    	tipText.put("average", new String[]{
	    		"Decent tip. When did you turn into a good guy.",
	    		"Service must have been good.",
	    		"Is the waitress hot because you're definitely not tipping for the food."
	    	});
	    	tipText.put("generous", new String[]{
	    		"The food must have been good, and the waitress must be cute too.",
	    		"Maybe she'll give you her number since you're so generous.",
	    		"She may just go home with you!",
	    		"Hot ass and nice tits, right?"
	    	});
	    	tipText.put("showoff", new String[]{
	    		"Calm down...you're not that rich!",
	    		"You better be getting serviced with a tip this big - if you catch my drift!",
	    		"Are you that drunk?",
	    		"The waitress is definitely going home with you!",
	    		"Are you trying to pay the waitress' tuition?",
	    		"Easy tiger...you're gonna break the bank at this rate.",
	    		"There goes your stripper fund, but hopefully the waitress will pick up the stripper's job from here",
	    		"Looking forward to a hot night with the waitress?"
	    	});
    	} else {
	    	tipText.put("cheap", new String[]{
	    		"Damn girl! You're cheap.",
	    		"The waitor must be damn ugly...",
	    		"Are you jealous of the waitress",
	    		"Was the service really that bad?",
	    		"Easy chicka... you're not breaking the bank if you bump up the tip.",
	    		"Leaving a slightly larger tip won't break tonight's drinks budget!",
	    		"Just because the waitor isn't into you doesn't mean you don't leave a decent tip."
	    	});
	    	tipText.put("broke", new String[]{
	    		"You must be broke... and now the waitor will be too.",
	    		"You're not cheap but clearly your wallet is empty. So, why are you out spending money?",
	    		"Mediocre service = mediocre tip.",
	    		"The waitor isn't going home with you if you don't tip him a bit more."
	    	});
	    	tipText.put("average", new String[]{
	    		"Decent tip. When did you turn into a nice girl?",
	    		"Service must have been good.",
	    		"Is the waitor hot because you're definitely not tipping for the food."
	    	});
	    	tipText.put("generous", new String[]{
	    		"The food must have been good, and the waitor must be cute too.",
	    		"Maybe the waitor will call you tonight since you're so generous.",
	    		"The waitor might just go home with you after this tip!",
	    		"Dark, tall, and handsome, right?"
	    	});
	    	tipText.put("showoff", new String[]{
	    		"Calm down...you're not that rich!",
	    		"You better be getting serviced with a tip this big - if you catch my drift!",
	    		"Are you that drunk?",
	    		"The waitor is definitely going home with you!",
	    		"Are you trying to pay the waitor's tuition?",
	    		"Easy tiger...you're gonna break the bank at this rate.",
	    		"There goes your male stripper fund, but hopefully the waitor will pick up the stripper's job from here.",
	    		"Look forward to a hot night with the waitor?"
	    	});
    	}
    }
    
    private void displayTipText( ){    	
    	String selectedItem = ((NumericWheelAdapter) percentage.getViewAdapter()).getItemText( percentage.getCurrentItem() ).toString();
    	selectedItem = selectedItem.substring(0, selectedItem.length() - 1);
    	Integer iPercentage = Integer.valueOf( selectedItem );
    	
    	String textCategory = "";
    	
    	if ( iPercentage <= 5 ){
    		textCategory = "cheap";    	
    	} else if ( iPercentage > 5 && iPercentage <= 10 ){
    		textCategory = "broke";	
    	} else if ( iPercentage > 10 && iPercentage <= 15 ){
    		textCategory = "average";
    	} else if ( iPercentage > 15 && iPercentage <= 20 ){
    		textCategory = "generous";
    	} else if ( iPercentage > 20 ){
    		textCategory = "showoff";
    	}
    	
    	String[] category = tipText.get( textCategory );
    	String displayText= category[ (int) Math.floor(Math.random() * category.length) ];
    	
    	drunkText.setText(displayText);
    }
}
