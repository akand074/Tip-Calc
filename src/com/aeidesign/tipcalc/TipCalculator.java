package com.aeidesign.tipcalc;

import java.text.DecimalFormat;

import com.aeidesign.tipcalc.kankan.wheel.widget.OnWheelScrollListener;
import com.aeidesign.tipcalc.kankan.wheel.widget.WheelView;
import com.aeidesign.tipcalc.kankan.wheel.widget.adapters.NumericWheelAdapter;



import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TipCalculator extends Activity {
	private WheelView percentage;
	private WheelView split;
	private EditText bill;
	private CheckBox round;
	private TextView total;
	private TextView splitTotal;
	private TextView tipTotal;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        percentage = (WheelView) findViewById(R.id.percentage);
        split = (WheelView) findViewById(R.id.split);
        bill = (EditText) findViewById(R.id.bill);
        round = (CheckBox) findViewById(R.id.round);
        total = (TextView) findViewById(R.id.total);
        splitTotal = (TextView) findViewById(R.id.split_total);
        tipTotal = (TextView) findViewById(R.id.tip_total);
        
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
			dTotal = Math.round( dTotal );
			dTip = dTotal - dBill;
		}
		
		double dSplitTotal = dTotal / iSplit;
		
		DecimalFormat twoDecimals = new DecimalFormat("#.##");
		
		tipTotal.setText("Tip  $" + twoDecimals.format(dTip));
		total.setText("Total  $" + twoDecimals.format( dTotal ) );
		if ( iSplit > 1)
			splitTotal.setText("Each pay  $" + twoDecimals.format(dSplitTotal));
		else 
			splitTotal.setText("");
    }
}
