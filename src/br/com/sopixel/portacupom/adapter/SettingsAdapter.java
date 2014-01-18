package br.com.sopixel.portacupom.adapter;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.bo.ConfigParamsBO;
import br.com.sopixel.portacupom.utils.Utils;

public class SettingsAdapter extends BaseAdapter {

	private Context _context;
	
	private ConfigParamsBO _configParams;
	
	private final int LIST_ITEMS = 3;
	
	public SettingsAdapter(Context context, ConfigParamsBO configParams) {
		super();
		_context = context;
		_configParams = configParams;
	}
	
	@Override
	public int getCount() {
		return LIST_ITEMS;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View v = null;
		
		boolean firstItem = position == 0;
		boolean secondItem = position == 1;
		boolean thirdItem = position == 2;
		
		if(firstItem) {
			v = inflater.inflate(R.layout.notifications_enabled_item, null);
			
			CheckBox checkBox = (CheckBox)v.findViewById(R.id.notifications_check);
			checkBox.setChecked(_configParams.isNotificationsEnabled());
			
		} else if(secondItem) {
			v = inflater.inflate(R.layout.notification_days_item, null);
			
			TextView daysDesc = (TextView)v.findViewById(R.id.n_days_desc);
			daysDesc.setText(Utils.getDaysDescText(_context, _configParams));
			
		} else if(thirdItem) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			
			v = inflater.inflate(R.layout.alarm_time_item, null);
			
			TextView alarmTimeDesc = (TextView)v.findViewById(R.id.alarm_time_text);
			alarmTimeDesc.setText(sdf.format(_configParams.getAlarmTime()));
		}
		
		return v;
	}

}
