package br.com.sopixel.portacupom.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import br.com.sopixel.portacupom.bo.ConfigParamsBO;
import br.com.sopixel.portacupom.bo.CouponBO;
import br.com.sopixel.portacupom.bo.OfferBO;
import br.com.sopixel.portacupom.utils.Utils;

public class CouponDAO {
	private static final String DATABASE_NAME = "cademeucupom.db";
	private static final int DATABASE_VERSION = 2;
	
	private static final String TABLE_OFFER = "CMC_OFFER";
	private static final String TABLE_COUPON = "CMC_COUPON";
	private static final String TABLE_CONFIG = "CMC_CONFIG";

	private Context context;
	private SQLiteDatabase db;
	private OpenHelper openHelper;
	
	private SimpleDateFormat sdf;
	private SimpleDateFormat _alarmTimeFormat;

	private SQLiteStatement insertStmtOffer;
	private SQLiteStatement insertStmtCoupon;
	
	private static final String INSERT_OFFER = "insert into " + TABLE_OFFER
			+ "(id, description, expiration_date, site) values (?, ?, ?, ?)";
	
	private static final String INSERT_COUPON = "insert into " + TABLE_COUPON
	+ "(id, code, status, offer_id) values (?, ?, ?, ?)";

	private static final String INSERT_CONFIG = "insert into " + TABLE_CONFIG
	+ "(notifications_enabled, seven_days, three_days, one_day, alarm_time, is_scheduled) values (?, ?, ?, ?, ?, ?)";
	
	private static CouponDAO _instance = null;
	
	public static CouponDAO getInstance(Context context) {
		if(_instance == null) {
			_instance = new CouponDAO(context);
		}
		return _instance;
	}
	
	private CouponDAO(Context context) {
		this.context = context;
		
		// Create (on the 1st access) or open a database
		openHelper = new OpenHelper(this.context);
		db = openHelper.getWritableDatabase();
		
		insertStmtOffer = db.compileStatement(INSERT_OFFER);
		insertStmtCoupon = db.compileStatement(INSERT_COUPON);
		
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		_alarmTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	private static class OpenHelper extends SQLiteOpenHelper {
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
			
			if (!db.isReadOnly()) {
				// Enable foreign key constraints
				db.execSQL("PRAGMA foreign_keys=ON;");
		    }
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_OFFER + "(" + 
						"id INTEGER PRIMARY KEY, " +
						"description TEXT, " +
						"expiration_date TEXT, " +
						"site TEXT" + 
						") "); 
			
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COUPON + "(" + 
						"id INTEGER PRIMARY KEY, " +
						"code TEXT, " +
						"status INTEGER, " + 
						"offer_id INTEGER," +
						"FOREIGN KEY(offer_id) REFERENCES " + TABLE_OFFER + "(id)" +
						")");
			
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CONFIG + "(" + 
						"notifications_enabled INTEGER, " + 
						"seven_days INTEGER, " +
						"three_days INTEGER, " +
						"one_day INTEGER, " +
						"alarm_time TEXT, " +
						"is_scheduled INTEGER" +
						")");
			
			insertConfig(db);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onCreate(db);
		}
		
		private void insertConfig(SQLiteDatabase db) {
			Cursor cursor = db.query(TABLE_CONFIG, new String[] { "notifications_enabled, seven_days, three_days, one_day, alarm_time, is_scheduled" }, null, null, null, null, null);
			boolean hasData = cursor.moveToFirst();
			if(!hasData) {
				SQLiteStatement insertStmtConfig = db.compileStatement(INSERT_CONFIG);
				
				final int true_ = 1;
				final int false_ = 0;
				insertStmtConfig.bindLong(1, true_);
				insertStmtConfig.bindLong(2, true_);
				insertStmtConfig.bindLong(3, true_);
				insertStmtConfig.bindLong(4, true_);
				insertStmtConfig.bindString(5, "00:00");
				insertStmtConfig.bindLong(6, false_);
				insertStmtConfig.executeInsert();
			}
			
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
	
	/**
	 * Insert a new row in the table CMC_OFFER
	 * and return de ID of the new row inserted
	 * @return ID of the new row
	 */
	public long insertOffer(String description, Date date, String site) {
		this.insertStmtOffer.bindLong(1, getNextID(TABLE_OFFER));
		this.insertStmtOffer.bindString(2, description);
		this.insertStmtOffer.bindString(3, sdf.format(date));
		this.insertStmtOffer.bindString(4, site);
		
		return this.insertStmtOffer.executeInsert();
	}
	
	/**
	 * Insert a new row in the table CMC_COUPON
	 * and return de ID of the new row inserted
	 * @return ID of the new row
	 */
	public long insertCoupon(String code, CouponBO.CouponStatus status, long offer_id) {
		this.insertStmtCoupon.bindLong(1, getNextID(TABLE_COUPON));
		this.insertStmtCoupon.bindString(2, code);
		this.insertStmtCoupon.bindLong(3, status.ordinal());
		this.insertStmtCoupon.bindLong(4, offer_id);
		
		return this.insertStmtCoupon.executeInsert();
	}
	
	public void updateOffer(OfferBO offer) {
		if(offer != null) {
			ContentValues cv = new ContentValues();
			cv.put("description", offer.getDescription());
			cv.put("expiration_date", sdf.format(offer.getExpirationDate()));
			cv.put("site", offer.getSite());
			
			db.update(TABLE_OFFER, cv, "id = ?", new String[]{ String.valueOf(offer.getID()) });
		}
	}
	
	public void deleteAllCoupons(long offerId) {
		this.db.delete(TABLE_COUPON, "offer_id = ?", new String[] { String.valueOf(offerId) });
	}
	
	public ConfigParamsBO getConfig() {
		ConfigParamsBO configParams = null;
		
		Cursor cursor = this.db.query(TABLE_CONFIG, new String[] { "notifications_enabled, seven_days, three_days, one_day, is_scheduled, alarm_time" }, null, null, null, null, null);
		boolean hasData = cursor.moveToFirst();
		if(hasData) {
			final boolean notificationsEnabled = cursor.getLong(0) == 1;
			final boolean sevenDays = cursor.getLong(1) == 1;
			final boolean threeDays = cursor.getLong(2) == 1;
			final boolean oneDay = cursor.getLong(3) == 1;
			final boolean isScheduled = cursor.getLong(4) == 1;

			Date alarmTime = null;
			try {
				alarmTime = _alarmTimeFormat.parse(cursor.getString(5));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(alarmTime == null) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				
				alarmTime = c.getTime();
			}
			
			configParams = new ConfigParamsBO(notificationsEnabled, sevenDays, threeDays, oneDay, isScheduled, alarmTime);
		}
		
		closeCursor(cursor);
		
		return configParams;
	}

	public void setConfig(ConfigParamsBO configParams) {
		ContentValues cv = new ContentValues();
		cv.put("notifications_enabled", configParams.isNotificationsEnabled() ? 1 : 0);
		cv.put("seven_days", configParams.isSevenDaysSet() ? 1 : 0);
		cv.put("three_days", configParams.isThreeDaysSet() ? 1 : 0);
		cv.put("one_day", configParams.isOneDaySet() ? 1 : 0);
		cv.put("alarm_time", _alarmTimeFormat.format(configParams.getAlarmTime()));
		cv.put("is_scheduled", configParams.isScheduled() ? 1 : 0);
		
		db.update(TABLE_CONFIG, cv, null, null);
	}
	
	/**
	 * Updates the coupon status.
	 */
	public long updateStatusCoupon(CouponBO.CouponStatus status, long couponID) {
		ContentValues cv = new ContentValues();
		cv.put("status", status.ordinal());
		
		return db.update(TABLE_COUPON, cv, "id = ?", new String[]{ String.valueOf(couponID) });
	}
	
	/**
	 * Delete all rows in the selected table
	 */
	public void deleteAll(String TABLE) {
		this.db.delete(TABLE, null, null);
	}
	
	/**
	 * Fetches all offers from the database in the following 
	 * order: description, expiration_date, site;
	 */
	public List<OfferBO> selectOffers() {
		List<OfferBO> offers = new ArrayList<OfferBO>();
		Cursor cursor = null;
		
		// Query the base for offers
		cursor = this.db.query(TABLE_OFFER, new String[] { "id", "description", "expiration_date", "site" },
				null, null, null, null, "expiration_date");
		
		for(boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
			try {
				OfferBO offer = new OfferBO();
				
				Calendar c = Calendar.getInstance();
				c.setTime(sdf.parse(cursor.getString(2)));
				
				offer.setID(cursor.getLong(0));
				offer.setDescription(cursor.getString(1));
				offer.setExpirationDate(c.getTime());
				offer.setDaysToExpire(String.valueOf(Utils.diffDates(c)));
				offer.setSite(cursor.getString(3));
				offer.setCoupons(this.selectCoupons(offer.getID()));
				
				offers.add(offer);
			} catch (ParseException e) {
				Log.e("CupomDAO: EXAMPLE", "Erro no parser de data do metodo selectCoupons");
			}
		}
				
		closeCursor(cursor);

		return offers;
	}
	
	public List<OfferBO> selectOffers(CouponBO.CouponStatus status) {
		List<OfferBO> offers = new ArrayList<OfferBO>();
		Cursor cursor = null;
		
		String sql = 
				"SELECT DISTINCT o.id, o.description, o.expiration_date, o.site " +
				"FROM CMC_OFFER o, CMC_COUPON c " +
				"WHERE o.id = c.offer_id " +
				"AND c.status = ? " + 
				"ORDER BY o.expiration_date";
		
		cursor = db.rawQuery(sql, new String[] {String.valueOf(status.ordinal())});
		
		for(boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
			try {
				OfferBO offer = new OfferBO();
				
				Calendar c = Calendar.getInstance();
				c.setTime(sdf.parse(cursor.getString(2)));
				
				offer.setID(cursor.getLong(0));
				offer.setDescription(cursor.getString(1));
				offer.setExpirationDate(c.getTime());
				offer.setDaysToExpire(String.valueOf(Utils.diffDates(c)));
				offer.setSite(cursor.getString(3));
				offer.setCoupons(this.selectCoupons(offer.getID()));
				
				offers.add(offer);
			} catch (ParseException e) {
				Log.e("CupomDAO: EXAMPLE", "Erro no parser de data do metodo selectCoupons");
			}
		}
				
		closeCursor(cursor);

		return offers;
	}

	
	/**
	 * Fetches all coupons of the specified offer from the database 
	 * in the following order: description, expiration_date, site;
	 */
	public List<CouponBO> selectCoupons(long offerID) {
		List<CouponBO> coupons = new ArrayList<CouponBO>();
		
		// Query the base for coupons
		Cursor cursor = this.db.query(TABLE_COUPON, new String[] { "id, code, status, offer_id" },
				"offer_id = " + offerID, null, null, null, null);
		
		for(boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
			CouponBO coupon = new CouponBO();
			
			coupon.setId(cursor.getLong(0));
			coupon.setCode(cursor.getString(1));
			coupon.setStatus(CouponBO.CouponStatus.values()[cursor.getInt(2)]);
			coupon.setOfferID(offerID);
			
			coupons.add(coupon);
		}
				
		closeCursor(cursor);
		
		return coupons;
	}
	
	/**
	 * Gets an offer by ID
	 * @param id
	 * @return
	 */
	public OfferBO selectOfferById(long id) {
		OfferBO offer = new OfferBO();
		
		// Query the base for offers
		Cursor cursor = this.db.query(TABLE_OFFER, new String[] { "description", "expiration_date", "site" },
				"id = " + id, null, null, null, null);
		
		if(cursor.moveToFirst()) {
			try {
				Calendar c = Calendar.getInstance();
				c.setTime(sdf.parse(cursor.getString(1)));
				
				offer.setDescription(cursor.getString(0));
				offer.setExpirationDate(c.getTime());
				offer.setDaysToExpire(String.valueOf(Utils.diffDates(c)));
				offer.setSite(cursor.getString(2));
			} catch(ParseException e) {
				Log.e("CupomDAO: EXAMPLE", "Erro no parser de data do metodo selectOfferById");
			}
		}
		
		return offer;
	}
	
	/**
	 * Delete the selected offer, starting 
	 * by the coupons from that offer.
	 * @param id
	 * @return
	 */
	public int deleteOfferById(long id) {
		
		// Delete all coupons from the offer
		db.delete(TABLE_COUPON, "offer_id = ?", new String[] {String.valueOf(id)});
		
		// Delete the offer
		int rows = db.delete(TABLE_OFFER, "id = ?", new String[] { String.valueOf(id) });
		
		return rows;
	}
	
	/**
	 * Retrieve the active SQLiteDatabase
	 */
	public SQLiteDatabase getDb() {
		return this.db;
	}
	
	/**
	 * Closes the database
	 */
	public void closeDatabase() {
		openHelper.close();
	}
	
	/**
	 * Get the next ID of the table
	 * passed in the argument list
	 * @return
	 */
	private long getNextID(String TABLE) {
		long nextID = 0;
		
		Cursor cursor = this.db.query(TABLE, new String[] { "MAX(id)" },
				null, null, null, null, null);
		if(cursor.moveToFirst()) {
			nextID = cursor.getLong(0);
		}
		
		closeCursor(cursor);
		
		return nextID + 1;
	}
	
	public List<OfferBO> getOffersFromExpirationDate(Date date) {
		List<OfferBO> offers = new ArrayList<OfferBO>();
		Cursor cursor = null;
		
		String dateString = sdf.format(date);
		
		String sql = 
				"SELECT DISTINCT o.id, o.description, o.expiration_date, o.site " +
				"FROM CMC_OFFER o, CMC_COUPON c " +
				"WHERE o.id = c.offer_id " +
				"AND o.expiration_date = ? " +
				"AND c.status = ?";
		
		cursor = db.rawQuery(sql, new String[] {dateString, String.valueOf(CouponBO.CouponStatus.PENDING.ordinal()) });
		
		for(boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
			try {
				OfferBO offer = new OfferBO();
				
				Calendar c = Calendar.getInstance();
				c.setTime(sdf.parse(cursor.getString(2)));
				
				offer.setID(cursor.getLong(0));
				offer.setDescription(cursor.getString(1));
				offer.setExpirationDate(c.getTime());
				offer.setDaysToExpire(String.valueOf(Utils.diffDates(c)));
				offer.setSite(cursor.getString(3));
				offer.setCoupons(this.selectCoupons(offer.getID()));
				
				offers.add(offer);
			} catch (ParseException e) {
				Log.e("CupomDAO: EXAMPLE", "Erro no parser de data do metodo selectCoupons");
			}
		}
				
		closeCursor(cursor);

		return offers;
	}
	
	private void closeCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}
}