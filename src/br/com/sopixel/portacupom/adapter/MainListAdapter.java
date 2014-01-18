package br.com.sopixel.portacupom.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.bo.CouponBO.CouponStatus;
import br.com.sopixel.portacupom.bo.OfferBO;
import br.com.sopixel.portacupom.utils.Utils;

public class MainListAdapter extends BaseAdapter {

	private Context _context;
	
	private List<OfferBO> _offers;
	
	private CouponStatus _status;
	
	public MainListAdapter(Context context, List<OfferBO> offers, CouponStatus status) {
		super();
		this._context = context;
		this._offers = offers;
		this._status = status;
	}

	@Override
	public int getCount() {
		return _offers.size();
	}

	@Override
	public Object getItem(int position) {
		return _offers.get(position);
	}

	public void removeItem(int position) {
		_offers.remove(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		OfferBO offer = _offers.get(position);
		
		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v;
		
		if(_status == CouponStatus.PENDING) {
		
			v = inflater.inflate(R.layout.list_item, null);
			
			TextView expDays = (TextView) v.findViewById(R.id.exp_days);
			expDays.setText(offer.getDaysToExpire());
			
		} else {
			v = inflater.inflate(R.layout.list_item2, null);
			
			ImageView statusImage = (ImageView)v.findViewById(R.id.statusImage);
			statusImage.setImageResource(_status == CouponStatus.USED ? R.drawable.ic_tab_used : R.drawable.ic_tab_expired);
		}
		
		TextView description = (TextView) v.findViewById(R.id.desc);
		TextView site = (TextView) v.findViewById(R.id.site);
		TextView numCoupons = (TextView) v.findViewById(R.id.num_coupons);
		
		description.setText(offer.getDescription());
		site.setText(offer.getSite());
		numCoupons.setText(Utils.getCouponsDescription(offer, _context, _status));
		
		return v;
	}

	public void setOffers(List<OfferBO> offers) {
		_offers = offers;
	}
	
	public void setStatus(CouponStatus status) {
		_status = status;
	}
	
}
