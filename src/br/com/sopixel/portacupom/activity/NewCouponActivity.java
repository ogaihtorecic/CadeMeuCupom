package br.com.sopixel.portacupom.activity;

import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.widget.EditText;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.bo.CouponBO;

public class NewCouponActivity extends AbstractNewCouponActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onSave(String description, Date expiration, String site, List<EditText> coupons) {
		long offerId = MainActivity.db.insertOffer(description, expiration, site);
		
		if(coupons.isEmpty()) {
			MainActivity.db.insertCoupon(getResources().getString(R.string.default_coupon_desc), CouponBO.CouponStatus.PENDING, offerId);
		} else {
		
			for(EditText codeCoupon: coupons) {
				MainActivity.db.insertCoupon(codeCoupon.getText().toString(), CouponBO.CouponStatus.PENDING, offerId);
			}
		}
		
	}

	@Override
	protected void onAddCoupon() {
		
	}

	@Override
	protected void onRemoveCoupon(int index) {
		
	}

}
