package br.com.sopixel.portacupom.bo;

import java.io.Serializable;
import java.util.Date;

public class ConfigParamsBO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private boolean _notificationsEnabled;
	private boolean _sevenDays;
	private boolean _threeDays;
	private boolean _oneDay;
	private boolean _isScheduled;
	private Date _alarmTime;
	
	
	public ConfigParamsBO(boolean notificationsEnabled, boolean sevenDays, boolean threeDays, boolean oneDay, boolean isScheduled, Date alarmTime) {
		_notificationsEnabled = notificationsEnabled;
		_sevenDays = sevenDays;
		_threeDays = threeDays;
		_oneDay = oneDay;
		_isScheduled = isScheduled;
		_alarmTime = alarmTime;
	}
	
	public boolean isNotificationsEnabled() {
		return _notificationsEnabled;
	}
	
	public void setNotificationsEnabled(boolean notificationsEnabled) {
		_notificationsEnabled = notificationsEnabled;
	}
	
	public boolean isSevenDaysSet() {
		return _sevenDays;
	}
	
	public void setSevenDays(boolean sevenDays) {
		_sevenDays = sevenDays;
	}
	
	public boolean isThreeDaysSet() {
		return _threeDays;
	}
	
	public void setThreeDays(boolean threeDays) {
		_threeDays = threeDays;
	}
	
	public boolean isOneDaySet() {
		return _oneDay;
	}
	
	public void setOneDay(boolean oneDay) {
		_oneDay = oneDay;
	}
	
	public Date getAlarmTime() {
		return _alarmTime;
	}
	
	public void setAlarmTime(Date alarmTime) {
		_alarmTime = alarmTime;
	}

	public boolean isScheduled() {
		return _isScheduled;
	}

	public void setSchedule(boolean _isScheduled) {
		this._isScheduled = _isScheduled;
	}
}
