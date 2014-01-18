package br.com.sopixel.portacupom.bo;

import java.io.Serializable;

public class CouponBO implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum CouponStatus {
		PENDING, USED, EXPIRED
	}

	private long _id;
	private String _code;
	private CouponStatus _status;
	private long _offerID;

	public long getId() {
		return _id;
	}

	public void setId(long id) {
		this._id = id;
	}

	public String getCode() {
		return _code;
	}

	public void setCode(String code) {
		this._code = code;
	}

	public CouponStatus getStatus() {
		return _status;
	}

	public void setStatus(CouponStatus status) {
		this._status = status;
	}

	public long getOfferID() {
		return _offerID;
	}

	public void setOfferID(long offerID) {
		this._offerID = offerID;
	}
}
