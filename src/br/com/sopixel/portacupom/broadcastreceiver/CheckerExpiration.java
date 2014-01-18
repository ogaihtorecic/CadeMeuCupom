package br.com.sopixel.portacupom.broadcastreceiver;

import java.util.Calendar;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.activity.TabsActivity;
import br.com.sopixel.portacupom.bo.ConfigParamsBO;
import br.com.sopixel.portacupom.bo.OfferBO;
import br.com.sopixel.portacupom.dao.CouponDAO;

public class CheckerExpiration extends BroadcastReceiver {

	public static final int NOTIFICATION_ID = 123466771; // XXX: solve this
	private Context context;
	private CouponDAO db;
	private NotificationManager nm;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		this.db = CouponDAO.getInstance(context);
		this.nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		ConfigParamsBO config = (ConfigParamsBO) db.getConfig();
		
		boolean isExpiringOffers = false;
	
		// Veriry if there is expiring offers TODAY
		isExpiringOffers = isExpiringOffersToday();
		
		// Veriry if there is expiring offers in ONE day
		if(config.isOneDaySet() && isExpiringOffers == false) {
			isExpiringOffers = isExpiringOffersInOneDay();
		}
		
		// Veriry if there is expiring offers in THREE days
		if(config.isThreeDaysSet() && isExpiringOffers == false) {
			isExpiringOffers = isExpiringOffersInThreeDays();
		}
		
		// Veriry if there is expiring offers in SEVEN days
		if(config.isSevenDaysSet() && isExpiringOffers == false) {
			isExpiringOffers = isExpiringOffersInSevenDays();
		}
		
		// Send notification if there is expiring offers
		if(isExpiringOffers) {
			this.sendNotification();
		}
	}
	
	private boolean isExpiringOffersToday() {
		List<OfferBO> offers = db.getOffersFromExpirationDate(Calendar.getInstance().getTime());
		
		if(!offers.isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	private boolean isExpiringOffersInOneDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
		List<OfferBO> offers = db.getOffersFromExpirationDate(calendar.getTime());
		
		if(!offers.isEmpty()) {
			return true;
		}
		
		return false;
	}

	private boolean isExpiringOffersInThreeDays() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 3);
		List<OfferBO> offers = db.getOffersFromExpirationDate(calendar.getTime());
		
		if(!offers.isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	private boolean isExpiringOffersInSevenDays() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 7);
		List<OfferBO> offers = db.getOffersFromExpirationDate(calendar.getTime());
		
		if(!offers.isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	private void sendNotification() {
		Notification notification = new Notification(R.drawable.ic_pending_mini, context.getResources().getString(R.string.notification_title), System.currentTimeMillis());
		
		Intent intent = new Intent(context, TabsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		notification. setLatestEventInfo(context, context.getResources().getString(R.string.app_name), context.getResources().getString(R.string.notification_desc), pendingIntent);
		notification.defaults = Notification.DEFAULT_ALL;
		
		// Set the notification to be cleared by the click of the user
		notification.flags |= Notification.FLAG_AUTO_CANCEL; 
		
		nm.notify(NOTIFICATION_ID, notification);
	}
}
