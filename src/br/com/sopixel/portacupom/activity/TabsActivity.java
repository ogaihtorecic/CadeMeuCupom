package br.com.sopixel.portacupom.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.bo.CouponBO.CouponStatus;
import br.com.sopixel.portacupom.utils.Utils;

public class TabsActivity extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs);

		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;

		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(Utils.STATUS, CouponStatus.PENDING);

		spec = tabHost
				.newTabSpec("pending")
				.setIndicator(getString(R.string.pending_coupons),
						res.getDrawable(R.drawable.ic_tab_pending))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent(this, MainActivity.class);
		intent.putExtra(Utils.STATUS, CouponStatus.USED);
		
		spec = tabHost
				.newTabSpec("used")
				.setIndicator(getString(R.string.used_coupons),
						res.getDrawable(R.drawable.ic_tab_used))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent(this, MainActivity.class);
		intent.putExtra(Utils.STATUS, CouponStatus.EXPIRED);
		
		spec = tabHost
				.newTabSpec("expired")
				.setIndicator(getString(R.string.expired_coupons), 
						res.getDrawable(R.drawable.ic_tab_expired))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}
}