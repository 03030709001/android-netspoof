package uk.digitalsquid.netspoofer;

import java.io.File;
import java.io.FileNotFoundException;

import uk.digitalsquid.netspoofer.config.ConfigChecker;
import uk.digitalsquid.netspoofer.config.FileFinder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class NetSpoof extends Activity implements OnClickListener {
	/**
	 * A dialog to tell the user to mount their SD card.
	 */
	static final int DIALOG_R_SD = 1;
	/**
	 * A dialog to tell the user to mount their SD card rw.
	 */
	static final int DIALOG_W_SD = 2;
	static final int DIALOG_ROOT = 3;
	static final int DIALOG_BB = 4;
	
	private Button startButton, setupButton;

	@SuppressWarnings("unused")
	private SharedPreferences prefs;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if(ConfigChecker.checkInstalled(getApplicationContext())) findViewById(R.id.startButton).setEnabled(true);
		startButton = (Button) findViewById(R.id.startButton);
		startButton.setOnClickListener(this);
		setupButton = (Button) findViewById(R.id.setupButton);
		setupButton.setOnClickListener(this);
		
		if(!ConfigChecker.checkInstalledLatest(getApplicationContext())) {
			setupButton.setTypeface(setupButton.getTypeface(), Typeface.BOLD);
		}

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		if(!ConfigChecker.getSDStatus(false)) {
			showDialog(DIALOG_R_SD);
		} else {
			if(!ConfigChecker.getSDStatus(true)) {
				showDialog(DIALOG_R_SD);
			}
			firstTimeSetup();
		}
		startButton.setEnabled(ConfigChecker.checkInstalled(getApplicationContext()));
		
	    statusFilter = new IntentFilter();
	    statusFilter.addAction(InstallService.INTENT_STATUSUPDATE);
		
		registerReceiver(statusReceiver, statusFilter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(statusReceiver);
	}

	private void firstTimeSetup() {
		final File sd = getExternalFilesDir(null);
		File imgDir = new File(sd, "img");
		if(!imgDir.exists()) if(!imgDir.mkdir()) Toast.makeText(this, "Couldn't create 'img' folder.", Toast.LENGTH_LONG).show();
		
		try {
			FileFinder.initialise();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if(e.getMessage().equals("su")) {
				showDialog(DIALOG_ROOT);
			} else if(e.getMessage().equals("busybox")) {
				showDialog(DIALOG_BB);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.setupButton:
				startActivity(new Intent(this, SetupStatus.class));
				break;
			case R.id.startButton:
				startActivity(new Intent(this, HackSelector.class));
				break;
		}
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder;
		switch(id) {
			case DIALOG_R_SD:
				builder = new AlertDialog.Builder(this);
				builder.setMessage("Please connect SD card / exit USB mode to continue.")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							NetSpoof.this.finish();
						}
					});
				dialog = builder.create();
				break;
			case DIALOG_W_SD:
				builder = new AlertDialog.Builder(this);
				builder.setMessage("Please set the SD Card to writable. May work without but expect problems.")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) { }
					});
				dialog = builder.create();
				break;
			case DIALOG_ROOT:
				builder = new AlertDialog.Builder(this);
				builder.setMessage("Please root your phone before using this application. Search the internet for instructions on how to do this for your phone.\nA custom firmware (such as CyanogenMod) is also recommended.")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							NetSpoof.this.finish();
						}
					});
				dialog = builder.create();
				break;
			case DIALOG_BB:
				builder = new AlertDialog.Builder(this);
				builder.setMessage("Please install Busybox (either manually or from the Android Market) before using this application.  Search the internet for instructions on how to do this for your phone.")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							NetSpoof.this.finish();
						}
					});
				dialog = builder.create();
				break;
		}
		return dialog;
	}
	
	private IntentFilter statusFilter;
	private final BroadcastReceiver statusReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch(intent.getIntExtra(InstallService.INTENT_EXTRA_STATUS, InstallService.STATUS_FINISHED)) {
			case InstallService.STATUS_FINISHED:
				switch(intent.getIntExtra(InstallService.INTENT_EXTRA_DLSTATE, InstallService.STATUS_DL_FAIL_DLERROR)) {
				case InstallService.STATUS_DL_SUCCESS:
					startButton.setEnabled(true);
					break;
				default:
					startButton.setEnabled(false);
					break;
				}
				break;
			}
		}
	};
}
