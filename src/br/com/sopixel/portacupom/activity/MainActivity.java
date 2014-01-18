package br.com.sopixel.portacupom.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.adapter.MainListAdapter;
import br.com.sopixel.portacupom.bo.ConfigParamsBO;
import br.com.sopixel.portacupom.bo.CouponBO;
import br.com.sopixel.portacupom.bo.CouponBO.CouponStatus;
import br.com.sopixel.portacupom.bo.OfferBO;
import br.com.sopixel.portacupom.dao.CouponDAO;
import br.com.sopixel.portacupom.utils.Utils;

public class MainActivity extends Activity {

	public static String SELECTED_OFFER = "SELECTED_OFFER";

	public static CouponDAO db;

	private List<OfferBO> _offers;

	private CouponStatus _status = CouponStatus.PENDING;

	private MainListAdapter _adapter;

	public static final String EXECUTE_ALARM = "EXECUTE_ALARM";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent i = getIntent();
		Bundle bundle = i.getExtras();
		if(bundle == null) {
			_status = CouponStatus.PENDING;
		} else {
			_status = (CouponStatus)bundle.getSerializable(Utils.STATUS);
		}
		
		db = CouponDAO.getInstance(getApplicationContext());

		ListView lv = (ListView) findViewById(R.id.offers);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showOffer(position);
			}
		});

		registerForContextMenu(lv);

		Utils.startWidgetAlarm(this);
		
		ConfigParamsBO config = (ConfigParamsBO) db.getConfig();
		if(config.isNotificationsEnabled() && !config.isScheduled()) {
			Utils.startAlarm(this);
			
			config.setSchedule(true);
			db.setConfig(config);
		}
	}

	/**
	 * Updates the listview element whenever this activity gets focus.
	 */
	@Override
	protected void onResume() {
		super.onResume();

		ListView lv = (ListView) findViewById(R.id.offers);

		_offers = MainActivity.db.selectOffers(_status);
		
		if(_adapter == null) {
			_adapter = new MainListAdapter(this, _offers, _status);
			lv.setAdapter(_adapter);
		} else {
			_adapter.setOffers(_offers);
			_adapter.setStatus(_status);
			_adapter.notifyDataSetChanged();
		}
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.item_menu, menu);
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		OfferBO offer = _offers.get(info.position);
		
		int daysForExpiration = Integer.valueOf(offer.getDaysToExpire());
		boolean expiredOffer = daysForExpiration < 0;
		
		if(expiredOffer && areTherePendingCoupons(offer)) {
			MenuItem item = (MenuItem) menu.findItem(R.id.move_offer_item);
			item.setVisible(true);
		}
	}

	private boolean areTherePendingCoupons(OfferBO offer) {
		
		for(CouponBO coupon : offer.getCoupons()) {
			if(coupon.getStatus() == CouponStatus.PENDING) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		OfferBO offer = _offers.get(info.position);
		
		switch (item.getItemId()) {
			case R.id.view_offer_item:
				showOffer(info.position);
				return true;
			
			case R.id.remove_offer_item:
				doRemoveOperation(offer);
				return true;
			
			case R.id.edit_offer_item:
				editOffer(info.position);
				return true;
				
			case R.id.move_offer_item:
				doMoveOperation(offer);
				return true;
				
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem item = menu.add(0, 0, 0, R.string.new_offer);
		item.setIcon(android.R.drawable.ic_menu_add);
		
		item = menu.add(0, 1, 0, R.string.settings);
		item.setIcon(android.R.drawable.ic_menu_preferences);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == 0) {
			Intent i = new Intent(MainActivity.this, NewCouponActivity.class);
			startActivity(i);
		} else if(item.getItemId() == 1) {
			Intent i = new Intent(MainActivity.this, SettingsActivity.class);
			i.putExtra(Utils.CONFIG_PARAMS, db.getConfig());
			startActivity(i);
		} else {
			return super.onOptionsItemSelected(item);
		}
		
		return true;
	}
	
	@Override
	public void finish() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setIcon(R.drawable.ic_dialog_alert);
		alert.setTitle(R.string.request_exit_confirmation);
		alert.setPositiveButton(R.string.confirm, new OnClickListener() { 
			public void onClick(DialogInterface dialog, int which) {
				finishApp();
			}
		});
		alert.setNegativeButton(R.string.deny, new OnClickListener() { 
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		alert.show();
	}

	private void finishApp() {
		MainActivity.super.finish();
		try {
			System.exit(0); //XXX
		} finally {
			MainActivity.db.closeDatabase();
		}
	}
	
	private void showOffer(int position) {
		OfferBO offer = _offers.get(position);

		Intent intent = new Intent(MainActivity.this, ViewCouponActivity.class);
		intent.putExtra(MainActivity.SELECTED_OFFER, offer);
		startActivity(intent);
	}
	
	private void editOffer(int position) {
		OfferBO offer = _offers.get(position);

		Intent intent = new Intent(MainActivity.this, EditCouponActivity.class);
		intent.putExtra(MainActivity.SELECTED_OFFER, offer);
		startActivity(intent);
	}
	
	private void doRemoveOperation(final OfferBO offer) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setIcon(R.drawable.ic_dialog_alert);
		alert.setTitle(R.string.request_confirmation);
		alert.setPositiveButton(R.string.confirm, new OnClickListener() { 
			public void onClick(DialogInterface dialog, int which) {
				removeOffer(offer);
			}
		});
		alert.setNegativeButton(R.string.deny, new OnClickListener() { 
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		alert.show();
	}
	
	private void doMoveOperation(final OfferBO offer) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setIcon(R.drawable.ic_dialog_alert);
		alert.setTitle(R.string.request_confirmation);
		alert.setPositiveButton(R.string.confirm, new OnClickListener() { 
			public void onClick(DialogInterface dialog, int which) {
				moveOffer(offer);
			}
		});
		alert.setNegativeButton(R.string.deny, new OnClickListener() { 
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		alert.show();
	}
	
	private void moveOffer(OfferBO offer) {
		
		for(CouponBO coupon: offer.getCoupons()) {
			if(coupon.getStatus() == CouponBO.CouponStatus.PENDING) {
				MainActivity.db.updateStatusCoupon(CouponBO.CouponStatus.EXPIRED, coupon.getId());
			}
		}
		
		Utils.updateWidget(this);
		
		Toast.makeText(this, R.string.offer_moved_message, Toast.LENGTH_SHORT).show();
		this.onResume();
	}
	
	private void removeOffer(OfferBO offer) {
		MainActivity.db.deleteOfferById(offer.getID());
		
		Utils.updateWidget(this);
		
		Toast.makeText(this, R.string.offer_removed_message, Toast.LENGTH_SHORT).show();
		this.onResume();
	}
}