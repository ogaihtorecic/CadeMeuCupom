package br.com.sopixel.portacupom.utils;

import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.activity.MainActivity;
import br.com.sopixel.portacupom.bo.ConfigParamsBO;
import br.com.sopixel.portacupom.bo.CouponBO;
import br.com.sopixel.portacupom.bo.OfferBO;
import br.com.sopixel.portacupom.dao.CouponDAO;
import br.com.sopixel.portacupom.widget.CouponAppWidgetProvider.UpdateService;

public class Utils {

	public static final String CONFIG_PARAMS = "CONFIG_PARAMS";
	public static final String STATUS = "STATUS";
	
	public static int getNumberOfCouponsByStatus(List<CouponBO> coupons, CouponBO.CouponStatus status) {
		int count = 0;
		
		for(CouponBO coupon: coupons) {
			if(coupon.getStatus() == status) {
				count++;
			}
		}

		return count;
	}
	
	public static String getCouponsDescription(OfferBO offer, Context context, CouponBO.CouponStatus status) {
		
		StringBuilder builder = new StringBuilder();
		int numberOfCoupons = Utils.getNumberOfCouponsByStatus(offer.getCoupons(), status);
		
		if(numberOfCoupons == 1) {
			builder.append(numberOfCoupons).append(" ").append(context.getResources().getString(R.string.coupon_desc));
		} else {
			builder.append(numberOfCoupons).append(" ").append(context.getResources().getString(R.string.coupons_desc));
		}
		
		return builder.toString();
	}
	
	/**
	 * Get the difference, in days, of 
	 * expiration date for the current date
	 * @param expirationDate
	 * @return difference in days 
	 */
	public static long diffDates(Calendar expirationDate) {
		Calendar today = Calendar.getInstance();
		
		long diffDays = 0;
		
		if(today.get(Calendar.YEAR) == expirationDate.get(Calendar.YEAR)
				&& today.get(Calendar.MONTH) == expirationDate.get(Calendar.MONTH)) {
			diffDays = expirationDate.get(Calendar.DATE) - today.get(Calendar.DATE);
		} else {
			long milliseconds1 = today.getTimeInMillis();
		    long milliseconds2 = expirationDate.getTimeInMillis();
		    
		    long diff = milliseconds2 - milliseconds1;

		    diffDays = diff / (24 * 60 * 60 * 1000);
		    diffDays++;
		}
		
			    
	    return diffDays;
	}
	
	public static CharSequence getDaysDescText(Context context, ConfigParamsBO configParams) {
		
		StringBuilder builder = new StringBuilder();
		
		if(configParams.isSevenDaysSet()) {
			builder.append("7d").append(" ");
		} 
		if(configParams.isThreeDaysSet()) {
			builder.append("3d").append(" ");
		} 
		if(configParams.isOneDaySet()) {
			builder.append("1d");
		}
		
		String s = builder.toString().trim().replaceAll(" ", ", ");
		builder = new StringBuilder().append(s);
		
		if(!configParams.isSevenDaysSet() && !configParams.isThreeDaysSet() && !configParams.isOneDaySet()) {
			builder.append(context.getString(R.string.only));
		} else {
			builder.append(" ").append(context.getString(R.string.before));
		}
		
		return builder;
	}
	
	public static void startAlarm(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(MainActivity.EXECUTE_ALARM);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		long interval = 24 * 60 * 60 * 1000;
		
		ConfigParamsBO config = CouponDAO.getInstance(context).getConfig();
		long timeToTrigger = config.getAlarmTime().getTime();
		
		alarmManager.setRepeating(AlarmManager.RTC, timeToTrigger, interval, pIntent);
	}
	
	public static void startWidgetAlarm(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		long interval = 24 * 60 * 60 * 1000;
		
		Calendar c = Calendar.getInstance();
    	c.set(Calendar.HOUR_OF_DAY, 0);
    	c.set(Calendar.MINUTE, 0);
    	c.set(Calendar.SECOND, 0);
		
		long timeToTrigger = c.getTime().getTime();
		
		alarmManager.setRepeating(AlarmManager.RTC, timeToTrigger, interval, pIntent);
	}
	
	public static void cancelAlarm(Context context) {
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(MainActivity.EXECUTE_ALARM);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		alarm.cancel(pIntent);
	}
	
	public static void updateWidget(Context context) {
		context.startService(new Intent(context, UpdateService.class));
	}
	
}
