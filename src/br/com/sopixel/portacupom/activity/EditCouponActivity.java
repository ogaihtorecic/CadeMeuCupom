package br.com.sopixel.portacupom.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.bo.CouponBO;
import br.com.sopixel.portacupom.bo.OfferBO;
import br.com.sopixel.portacupom.bo.CouponBO.CouponStatus;

public class EditCouponActivity extends AbstractNewCouponActivity {

	private OfferBO _offer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		_offer = (OfferBO) bundle.getSerializable(MainActivity.SELECTED_OFFER);
		
		EditText descricaoEdit = (EditText) findViewById(R.id.descricaoEdit);
		descricaoEdit.setText(_offer.getDescription());
		
		Calendar c = Calendar.getInstance();
		c.setTime(_offer.getExpirationDate());
		
		updateExpirationDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		
		Spinner spinner = (Spinner) findViewById(R.id.siteSpinner);
		ArrayAdapter a = (ArrayAdapter) spinner.getAdapter();
		
		int spinnerPosition = a.getPosition(_offer.getSite());
		spinner.setSelection(spinnerPosition);
		
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.manycouponslayout);
		LinearLayout linearChild = (LinearLayout) linearLayout.getChildAt(0);
		
		ImageView addButton = (ImageView) linearChild.getChildAt(1);
		
		for(CouponBO coupon : _offer.getCoupons()) {
			addCouponRow(addButton, coupon.getCode(), false);
		}
		
		descricaoEdit.requestFocus();
		
	}
	
	@Override
	protected void onSave(String description, Date expiration, String site, List<EditText> coupons) {
		_offer.setDescription(description);
		_offer.setExpirationDate(expiration);
		_offer.setSite(site);
		
		MainActivity.db.updateOffer(_offer);
		MainActivity.db.deleteAllCoupons(_offer.getID());
		
		if(_offer.getCoupons().isEmpty()) {
			MainActivity.db.insertCoupon(getResources().getString(R.string.default_coupon_desc), CouponBO.CouponStatus.PENDING, _offer.getID());
		} else {
			List<CouponBO> list = _offer.getCoupons();
			for(int i = 0; i < list.size(); i++) {
				CouponBO coupon = list.get(i);
				coupon.setCode(coupons.get(i).getText().toString());
				MainActivity.db.insertCoupon(coupon.getCode(), coupon.getStatus(), coupon.getOfferID());
			}
		}

	}

	@Override
	protected void onAddCoupon() {
		CouponBO coupon = new CouponBO();
		coupon.setStatus(CouponStatus.PENDING);
		coupon.setOfferID(_offer.getID());
		
		_offer.addCoupon(coupon);
		
	}

	@Override
	protected void onRemoveCoupon(int index) {
		_offer.getCoupons().remove(index);
	}

}
