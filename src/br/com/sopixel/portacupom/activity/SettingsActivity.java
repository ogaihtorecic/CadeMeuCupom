package br.com.sopixel.portacupom.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.adapter.SettingsAdapter;
import br.com.sopixel.portacupom.bo.ConfigParamsBO;
import br.com.sopixel.portacupom.utils.Utils;

public class SettingsActivity extends Activity implements OnItemClickListener {

	private ConfigParamsBO _configParams;
	
	private final int TIME_DIALOG_ID = 0;
	
	private TextView _timeDisplay;
	
	private SimpleDateFormat _alarmTimeFormat = new SimpleDateFormat("HH:mm");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings);
		
		Intent i = getIntent();
		Bundle bundle = i.getExtras();
		_configParams = (ConfigParamsBO)bundle.getSerializable(Utils.CONFIG_PARAMS);
		
		ListView listView = (ListView) findViewById(R.id.settings_list);
		listView.setAdapter(new SettingsAdapter(this, _configParams));
		listView.setOnItemClickListener(this);
		
		Button okButton = (Button) findViewById(R.id.settings_ok);
		okButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.db.setConfig(_configParams);
				
				if(_configParams.isNotificationsEnabled()) {
					Utils.startAlarm(SettingsActivity.this);
				} else {
					Utils.cancelAlarm(SettingsActivity.this);
				}
				
				finish();
			}
		});
		
		Button cancelButton = (Button) findViewById(R.id.settings_cancel);
		cancelButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	public void onItemClick(AdapterView<?> l, final View v, int position, long id) {
		
		boolean firstItem = position == 0;
		boolean secondItem = position == 1;
		boolean thirdItem = position == 2;
		
		if(firstItem) {
			_configParams.setNotificationsEnabled(!_configParams.isNotificationsEnabled());
			
			CheckBox checkBox = (CheckBox) v.findViewById(R.id.notifications_check);
			checkBox.toggle();
			
		} else if(secondItem) {
			final boolean[] checkedItems = new boolean[]{ _configParams.isSevenDaysSet(), _configParams.isThreeDaysSet(), _configParams.isOneDaySet() };
			
			final int sevenDays = 0;
			final int threeDays = 1;
			final int oneDay = 2;
			
			final boolean sevenDaysSelected = checkedItems[0];
			final boolean threeDaysSelected = checkedItems[1];
			final boolean oneDaySelected = checkedItems[2];
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMultiChoiceItems(R.array.days, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					switch (which) {
					case sevenDays:
						_configParams.setSevenDays(isChecked);
						break;

					case threeDays:
						_configParams.setThreeDays(isChecked);
						break;	
						
					case oneDay:
						_configParams.setOneDay(isChecked);
						break;	
						
					default:
						break;
					}
					
				}
			});
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					TextView daysDesc = (TextView)v.findViewById(R.id.n_days_desc);
					daysDesc.setText(Utils.getDaysDescText(SettingsActivity.this, _configParams));
				}
			});
			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					_configParams.setSevenDays(sevenDaysSelected);
					_configParams.setThreeDays(threeDaysSelected);
					_configParams.setOneDay(oneDaySelected);
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			
		} else if(thirdItem) {
			_timeDisplay = (TextView) v.findViewById(R.id.alarm_time_text);
			showDialog(TIME_DIALOG_ID);
		}
	}
	
	private void updateDisplay() {
		_timeDisplay.setText(_alarmTimeFormat.format(_configParams.getAlarmTime()));
	}
	
	private TimePickerDialog.OnTimeSetListener _timeSetListener =
	    new TimePickerDialog.OnTimeSetListener() {
	        
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	        	
	        	Calendar c = Calendar.getInstance();
	        	c.set(Calendar.HOUR_OF_DAY, hourOfDay);
	        	c.set(Calendar.MINUTE, minute);
	        	c.set(Calendar.SECOND, 0);
	        	
	        	_configParams.setAlarmTime(c.getTime());
	        	
	            updateDisplay();
	        }
	    };
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case TIME_DIALOG_ID:
	    	Calendar c = Calendar.getInstance();
	    	c.setTime(_configParams.getAlarmTime());
	    	
	        return new TimePickerDialog(this, _timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
	    }
	    return null;
	}
}
