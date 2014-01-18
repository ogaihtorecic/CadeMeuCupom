package br.com.sopixel.portacupom.widget;

import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.activity.TabsActivity;
import br.com.sopixel.portacupom.bo.CouponBO.CouponStatus;
import br.com.sopixel.portacupom.bo.OfferBO;
import br.com.sopixel.portacupom.dao.CouponDAO;
import br.com.sopixel.portacupom.utils.Utils;

public class CouponAppWidgetProvider extends AppWidgetProvider {

	private static final String EMPTY = "";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Utils.updateWidget(context);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		context.startService(new Intent(context, UpdateService.class));
	}
	
	public static class UpdateService extends Service {

		@Override
        public void onStart(Intent intent, int startId) {
			List<OfferBO> offers = CouponDAO.getInstance(this).selectOffers(CouponStatus.PENDING);
			RemoteViews views = null;
			if (!offers.isEmpty()) {
				OfferBO offer = offers.get(0);

				views = new RemoteViews(this.getPackageName(), R.layout.coupon_appwidget);
				
				clearViews(views);
				
				views.setTextViewText(R.id.exp_days, offer.getDaysToExpire());
				views.setTextViewText(R.id.desc, offer.getDescription());
				views.setTextViewText(R.id.site, offer.getSite());
				
				int listSize = offers.size();
				if(listSize > 1) {
					offer = offers.get(1);
					views.setTextViewText(R.id.exp_days2, offer.getDaysToExpire());
					views.setTextViewText(R.id.desc2, offer.getDescription());
					views.setTextViewText(R.id.site2, offer.getSite());
				}
				
				if(listSize > 2) {
					offer = offers.get(2);
					views.setTextViewText(R.id.exp_days3, offer.getDaysToExpire());
					views.setTextViewText(R.id.desc3, offer.getDescription());
					views.setTextViewText(R.id.site3, offer.getSite());
				}
				
			} else {
				views = new RemoteViews(this.getPackageName(), R.layout.no_coupon_appwidget);
			}
			
			Intent i = new Intent(this, TabsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
			views.setOnClickPendingIntent(R.id.widget_click_area, pendingIntent);
			
			ComponentName thisWidget = new ComponentName(this, CouponAppWidgetProvider.class);
			AppWidgetManager.getInstance(this).updateAppWidget(thisWidget, views);
		}

		protected void clearViews(RemoteViews views) {
			views.setTextViewText(R.id.exp_days, EMPTY);
			views.setTextViewText(R.id.desc, EMPTY);
			views.setTextViewText(R.id.site, EMPTY);
			
			views.setTextViewText(R.id.exp_days2, EMPTY);
			views.setTextViewText(R.id.desc2, EMPTY);
			views.setTextViewText(R.id.site2, EMPTY);
			
			views.setTextViewText(R.id.exp_days3, EMPTY);
			views.setTextViewText(R.id.desc3, EMPTY);
			views.setTextViewText(R.id.site3, EMPTY);
		}
		
		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}
		
	}
}
