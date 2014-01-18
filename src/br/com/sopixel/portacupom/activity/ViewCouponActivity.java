package br.com.sopixel.portacupom.activity;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.adapter.ViewCouponAdapter;
import br.com.sopixel.portacupom.bo.CouponBO;
import br.com.sopixel.portacupom.bo.OfferBO;
import br.com.sopixel.portacupom.utils.Utils;

public class ViewCouponActivity extends Activity implements OnItemClickListener {
	
	private ListView _listView;
	private List<CouponBO> _coupons;
	
	private TextView _pendingCoupons;
	private TextView _usedCoupons;
	private TextView _expiredCoupons;
	
	private SimpleDateFormat _sdf = new SimpleDateFormat("d/MMMM/y");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.view_coupon);
		
		Bundle bundle = getIntent().getExtras();
		OfferBO offer = (OfferBO) bundle.getSerializable(MainActivity.SELECTED_OFFER);
		this._coupons = offer.getCoupons();
		
		TextView couponDescription = (TextView) findViewById(R.id.coupon_desc);
		couponDescription.setText(offer.getDescription());
		
		TextView couponSite = (TextView) findViewById(R.id.coupon_site);
		couponSite.setText(offer.getSite());

		TextView couponExpirationDate = (TextView) findViewById(R.id.coupon_exp_desc);
		couponExpirationDate.setText(new StringBuilder().append(getResources().getString(R.string.expiration_date)).append(" ").append(_sdf.format(offer.getExpirationDate())));

		_pendingCoupons = (TextView) findViewById(R.id.coupons_pending);
		_usedCoupons 	= (TextView) findViewById(R.id.coupons_used);
		_expiredCoupons = (TextView) findViewById(R.id.coupons_expired);
		
		updateScreenStatuses();
		
		List<CouponBO> coupons = offer.getCoupons();
		
		_listView = (ListView) findViewById(R.id.coupon_code_list);
		_listView.setAdapter(new ViewCouponAdapter(this, coupons));
		_listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CouponBO coupon = (CouponBO) _listView.getAdapter().getItem(position);
		CheckBox cBox = (CheckBox) view.findViewById(R.id.coupon_check);
		
		if(coupon.getStatus() != CouponBO.CouponStatus.EXPIRED) {
			boolean isChecked = coupon.getStatus().equals(CouponBO.CouponStatus.USED);
			String toastText = "";
			
			if(isChecked) {
				coupon.setStatus(CouponBO.CouponStatus.PENDING);
				MainActivity.db.updateStatusCoupon(CouponBO.CouponStatus.PENDING, coupon.getId());
				
				toastText = getResources().getString(R.string.coupon_marked_as_pending);
			} else {
				coupon.setStatus(CouponBO.CouponStatus.USED);
				MainActivity.db.updateStatusCoupon(CouponBO.CouponStatus.USED, coupon.getId());
				
				toastText = getResources().getString(R.string.coupon_marked_as_used);
			}
			
			Toast t = Toast.makeText(this, toastText, Toast.LENGTH_SHORT);
			t.show();
			
			updateScreenStatuses();
			
			cBox.toggle();
			
			Utils.updateWidget(this);
			
		} else {
			Toast t = Toast.makeText(this, R.string.coupon_already_expired, Toast.LENGTH_SHORT);
			t.show();
		}
	}
	
	private void updateScreenStatuses() {
		_pendingCoupons.setText("(" + Utils.getNumberOfCouponsByStatus(_coupons, CouponBO.CouponStatus.PENDING) + ")");
		
		_usedCoupons.setText("(" + Utils.getNumberOfCouponsByStatus(_coupons, CouponBO.CouponStatus.USED) + ")");
		
		_expiredCoupons.setText("(" + Utils.getNumberOfCouponsByStatus(_coupons, CouponBO.CouponStatus.EXPIRED) + ")");
	}
}
