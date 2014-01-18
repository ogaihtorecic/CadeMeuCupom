package br.com.sopixel.portacupom.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import br.com.sopixel.portacupom.R;
import br.com.sopixel.portacupom.utils.Utils;

public abstract class AbstractNewCouponActivity extends Activity {

	private Button _expDateButton;
	
	private int _year;
	private int _month;
	private int _day;
	
	private SimpleDateFormat _dateFormat = new SimpleDateFormat("EEEE, d/MMMM/y");
	
	private String _siteValue;
	
	private List<EditText> _codeCoupons;
	
	static final int DATE_DIALOG_ID = 0;
	
	private final int COUPON_MAX_LENGTH = 50;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_coupon);

		_codeCoupons = new ArrayList<EditText>();
		
		configureManyCouponsComponent();
		
		Spinner s = (Spinner) findViewById(R.id.siteSpinner);
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
				Object item = parent.getItemAtPosition(position);
				_siteValue = (String) item;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.sites, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);

		_expDateButton = (Button) findViewById(R.id.expDateButton);

		_expDateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		final Calendar c = Calendar.getInstance();
		_year = c.get(Calendar.YEAR);
		_month = c.get(Calendar.MONTH);
		_day = c.get(Calendar.DAY_OF_MONTH);

		updateDisplay();

		Button okButton = (Button) findViewById(R.id.okBtn);
		okButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				c.set(_year, _month, _day);
				
				String description = ((EditText) findViewById(R.id.descricaoEdit)).getText().toString();
				Date expiration = c.getTime();
				
				onSave(description, expiration, _siteValue, _codeCoupons);
				
				Utils.updateWidget(AbstractNewCouponActivity.this);
				
				finish();
			}
		});
		
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	protected abstract void onSave(String description, Date expiration, String site, List<EditText> coupons);
	
	private void updateDisplay() {
		Calendar c = Calendar.getInstance();
		c.set(_year, _month, _day);
		_expDateButton.setText(_dateFormat.format(c.getTime()));
	}

	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker v, int y, int m, int d) {
			updateExpirationDate(y, m, d);
		}
	};

	protected void updateExpirationDate(int year, int month, int day) {
		_year = year;
		_month = month;
		_day = day;
		updateDisplay();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, dateSetListener, _year, _month, _day);
		}
		return null;
	}
	
	private void configureManyCouponsComponent() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.manycouponslayout);
		LinearLayout linearChild = (LinearLayout) linearLayout.getChildAt(0);
		
		ImageView addButton = (ImageView) linearChild.getChildAt(1);
		
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				addCouponRow(v, "", true);
			}
		});
	}

	protected void addCouponRow(View v, String couponText, boolean triggerAddEvent) {
		LinearLayout newLinearLayout = new LinearLayout(getApplicationContext());
		EditText newEditText = new EditText(getApplicationContext());
		
		ImageButton removeButton = new ImageButton(getApplicationContext());
		removeButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_delete));
		
		newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		newLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		int width = 0;
		int height = LayoutParams.WRAP_CONTENT;
		int weight = 85;
		
		LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(width, height, weight);
		editTextParams.leftMargin = 5;
		
		newEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(COUPON_MAX_LENGTH) });
		newEditText.setLayoutParams(editTextParams);
		newEditText.setText(couponText);
		
		weight = 15;
		
		LinearLayout.LayoutParams removeButtonParams = new LinearLayout.LayoutParams(width, height, weight);
		removeButtonParams.rightMargin = 5;
		
		removeButton.setLayoutParams(removeButtonParams);
		
		newLinearLayout.addView(newEditText);
		newLinearLayout.addView(removeButton);
		
		removeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LinearLayout lineartoRemove = (LinearLayout) v.getParent();
				LinearLayout linearParent = (LinearLayout) lineartoRemove.getParent();
				
				linearParent.removeView(lineartoRemove);
				View editText = lineartoRemove.getChildAt(0);
				
				int removeIndex = _codeCoupons.indexOf(editText); 
				
				_codeCoupons.remove(editText);
				
				onRemoveCoupon(removeIndex);
			}
		});
		
		LinearLayout mainLinear = (LinearLayout) v.getParent();
		LinearLayout linearParent = (LinearLayout) mainLinear.getParent();
		
		int childCount = linearParent.getChildCount();
		linearParent.addView(newLinearLayout, childCount - 1);
		
		_codeCoupons.add(newEditText);
		
		if(triggerAddEvent) {
			onAddCoupon();
		}
		
		newEditText.requestFocus();
	}
	
	protected abstract void onAddCoupon();
	
	protected abstract void onRemoveCoupon(int index);
}
