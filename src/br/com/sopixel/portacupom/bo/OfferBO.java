package br.com.sopixel.portacupom.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OfferBO implements Serializable {
	private static final long serialVersionUID = 1L;

	private long _id;
	private String _description;
	private String _daysToExpire;
	private Date _expirationDate;
	private String _site;
	private List<CouponBO> _coupons;

	public OfferBO() {
		_coupons = new ArrayList<CouponBO>();
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		this._description = description;
	}

	public String getDaysToExpire() {
		return _daysToExpire;
	}

	public void setDaysToExpire(String daysToExpire) {
		this._daysToExpire = daysToExpire;
	}

	public Date getExpirationDate() {
		return _expirationDate;
	}
	
	public void setExpirationDate(Date expirationDate) {
		_expirationDate = expirationDate;
	}
	
	public String getSite() {
		return _site;
	}

	public void setSite(String site) {
		this._site = site;
	}

	public void setID(long iD) {
		_id = iD;
	}

	public long getID() {
		return _id;
	}

	public List<CouponBO> getCoupons() {
		return _coupons;
	}

	public void setCoupons(List<CouponBO> coupons) {
		this._coupons = coupons;
	}

	public void addCoupon(CouponBO coupon) {
		getCoupons().add(coupon);
	}
}
