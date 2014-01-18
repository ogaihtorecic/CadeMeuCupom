package br.com.sopixel.portacupom.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.bo.CouponBO;
import br.com.sopixel.portacupom.bo.CouponBO.CouponStatus;

public class ViewCouponAdapter extends BaseAdapter {
	private Context _context;
	private List<CouponBO> _coupons;
	
	public ViewCouponAdapter(Context context, List<CouponBO> lista) {
		this._context = context;
		this._coupons = lista;
	}
	
	@Override
	public int getCount() {
		return _coupons.size();
	}

	@Override
	public Object getItem(int position) {
		return _coupons.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CouponBO coupon = _coupons.get(position);
		
		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.coupon_code_item, null);
		
		TextView textView = (TextView) v.findViewById(R.id.coupon_code);
		CheckBox checkBox = (CheckBox) v.findViewById(R.id.coupon_check);
		
		if(coupon.getStatus() == CouponStatus.EXPIRED) {
			textView.setTextColor(Color.GRAY); 
		}
		
		textView.setText(coupon.getCode());
		checkBox.setChecked(coupon.getStatus().equals(CouponBO.CouponStatus.USED));
		
		return v;
	}
	
	public void removeItem(int position) {
		_coupons.remove(position);
	}
}
