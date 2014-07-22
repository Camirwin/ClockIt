package com.example.clockit.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.clockit.app.db.ClockItDataSource;
import com.example.clockit.app.model.TimeStamp;

import java.text.DecimalFormat;

/**
 * Launch activity that allows user to quickly navigate to clock in, manage
 * clients, or manage services.
 * 
 * @author Cameron Irwin
 * 
 */
public class MainActivity extends Activity {

	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	/** Button view used to clock in or clock out. */
	Button btnClock;

	TextView tvEarnedIncome;

	/**
	 * Chronometer that displays the amount of time that the user has been
	 * clocked in.
	 */
	Chronometer chronTimeWorked;

	/** Value used to determine if user is currently clocked in or not. */
	Boolean clockedIn;

	long startTime;
	TimeStamp currentStamp;

	/**
	 * Sets up the page's display based on whether the user is currently clocked
	 * in or not.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set data source
		datasource = new ClockItDataSource(this);

		// Set reference to views
		btnClock = (Button) findViewById(R.id.btnClock);
		tvEarnedIncome = (TextView) findViewById(R.id.tvEarnedIncome);
		chronTimeWorked = (Chronometer) findViewById(R.id.chronTimeWorked);

		// Set an onClick listener that will call the correct method based on
		// whether the user is clocked in or not
		btnClock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (clockedIn) {
					toClockOut();
				} else {
					toClockIn();
				}
			}

		});

		chronTimeWorked.setText("00:00:00");
		tvEarnedIncome.setText("$0.00");

		// Determine if user is clocked in from the database
		clockedIn = datasource.isClockedIn();

		// If the user is clocked in, change the clock button and text view's
		// text
		if (clockedIn) {
			btnClock.setText("Clock Out");
			chronTimeWorked.setTextColor(Color.GREEN);
			tvEarnedIncome.setTextColor(Color.GREEN);

			currentStamp = datasource.getCurrentTimeStamp();
			startTime = currentStamp.getClockIn();

			chronTimeWorked
					.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

						@Override
						public void onChronometerTick(Chronometer cArg) {
                            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
							long time = System.currentTimeMillis() - startTime;
							int h = (int) (time / 3600000);
							int m = (int) (time - h * 3600000) / 60000;
							int s = (int) (time - h * 3600000 - m * 60000) / 1000;
							String hh = h < 10 ? "0" + h : h + "";
							String mm = m < 10 ? "0" + m : m + "";
							String ss = s < 10 ? "0" + s : s + "";
							cArg.setText(hh + ":" + mm + ":" + ss);
							double earnedIncome = currentStamp.getEarnedIncome();
							tvEarnedIncome.setText("$" + decimalFormat.format(earnedIncome));
						}
					});

			chronTimeWorked.start();
		}
	}

	/**
	 * Sets up the options menu (currently unchanged).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Sends the user to the ChooseClientActivity to select a client to clock in
	 * for
	 */
	public void toClockIn() {
		// Start a new intent to direct to choose client
		Intent intent = new Intent(this, ChooseClientActivity.class);
		startActivity(intent);
	}

	/**
	 * Sends the user to the ClockOutActivity so they may clock out.
	 */
	public void toClockOut() {
		// Start a new intent to direct to clock out
		Intent intent = new Intent(this, ClockOutActivity.class);
		startActivity(intent);
	}

	/**
	 * Sends user to the manage clients activity.
	 * 
	 * @param view
	 *            The button that called the method.
	 */
	public void toManageClients(View view) {
		// Start a new intent directed to ManageClientsActivity
		Intent intent = new Intent(this, ManageClientsActivity.class);
		startActivity(intent);
	}

	/**
	 * Sends user to the manage services activity.
	 * 
	 * @param view
	 *            The button that called the method
	 */
	public void toManageServices(View view) {
		// Start a new intent directed to ManageServicesActivity
		Intent intent = new Intent(this, ManageServicesActivity.class);
		startActivity(intent);
	}

	/**
	 * Sends user to the manage time stamps activity.
	 * 
	 * @param view
	 *            The button that called the method
	 */
	public void toManageTimeStamps(View view) {
		// Start a new intent directed to ManageTimeStampsActivity
		Intent intent = new Intent(this, ManageTimeStampsActivity.class);
		startActivity(intent);
	}

	/**
	 * Closes the data source when the activity is paused (or deleted) to free
	 * resources.
	 */
	@Override
	public void onPause() {
		super.onPause();

		// Close data source if open
		if (datasource.isOpen()) {
			datasource.close();
		}
	}

	/**
	 * Opens the data source when activity is resumed (or started) to allow
	 * access.
	 */
	@Override
	public void onResume() {
		super.onResume();

		// Open data source if closed
		if (!datasource.isOpen()) {
			datasource.open();
		}
	}
}
