package com.sainfotech.autofare.common;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.sainfotech.autofare.R;
import com.sainfotech.maps.NavigationActivity;

public class Utils {

	private static Utils instance = new Utils();

	public static Utils getInstance() {
		return instance;
	}

	public static void showErrorMsg(final Context context, final String message) {
		AlertDialog alert = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
				.setCancelable(true)
				.setPositiveButton(context.getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});

		alert = builder.create();
		alert.show();
	}
	
	public boolean checkLocationSettings(Context ctx) {
        LocationManager locManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsLocEnable = false;
        boolean isNWLocEnable = false;
        try {
            if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                isGpsLocEnable = true;
            }
            if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                isNWLocEnable = true;
            }
        } catch (Exception ex) {
           
        }
        return isGpsLocEnable || isNWLocEnable;
    }
	
	private Dialog dialog;
	 public void showSpinner(Context ctx) {
	        try {
	            if (dialog == null) {
	                dialog = new Dialog(ctx);
	                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	                dialog.setCanceledOnTouchOutside(false);
	                dialog.setOnKeyListener(new OnKeyListener() {

	                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	                        return true;

	                    }
	                });
	                dialog.setContentView(R.layout.custom_spinner);
	                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
	                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	                dialog.show();
	            }
	        } catch (Exception e) {
	            Log.w(getClass().toString(), e);
	            dialog = null;
	        }
	    }
	 
	 public void dismisssSpinnerDialog() {
	        try {
	            if (dialog != null && dialog.isShowing()) {
	                dialog.dismiss();
	                dialog = null;
	            }
	        } catch (Exception e) {
	            Log.w("dismissspinner", e.toString());
	            dialog = null;
	        }
	    }

	private Context ctx;

	public void startUserStateWatcher(Context ctx) {
		System.out.println("started timer task");
		this.ctx = ctx;

		timer = new Timer();
		timer.scheduleAtFixedRate(new UserLocationMonitor(), 0, // initial delay
				1 * 10000); // subsequent rate
	}

	private final Handler uiThread = new Handler();
	private Timer timer;

	public void stopUerStateWatcher() {
		if (timer != null) {
			timer.cancel();
		}

	}

	final class UserLocationMonitor extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			uiThread.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						System.out.println("inside timer task");
						((NavigationActivity) ctx).getCurrentLocation(false, true,false);
					} catch (Exception e) {
						Log.v(getClass().toString(), e.toString());
					}
				}
			});
		}
	}
}
