package br.com.sopixel.portacupom.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.bo.CouponBO.CouponStatus;
import br.com.sopixel.portacupom.utils.Utils;

public class SplashScreenActivity extends Activity implements Runnable {

	private final int DELAY = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		
		Handler handler = new Handler();
		handler.postDelayed(this, DELAY);
	}
	
	@Override
	public void run() {
		Intent intent = new Intent(this, TabsActivity.class);
		intent.putExtra(Utils.STATUS, CouponStatus.PENDING);
		
		startActivity(intent);
		finish();
	}
}
