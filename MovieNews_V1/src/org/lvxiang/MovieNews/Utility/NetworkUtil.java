package org.lvxiang.MovieNews.Utility;

import org.lvxiang.MovieNews.View.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkUtil {

	public static boolean checkNetState(Context c){
		Context context=c.getApplicationContext();
		ConnectivityManager connectivity=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivity==null){
			showNetFailInfo(c);
			return false;
		}
		else{
			NetworkInfo[] info=connectivity.getAllNetworkInfo();
			if(info!=null){
				for(int i=0;i<info.length;i++){
					if(info[i].getState()==NetworkInfo.State.CONNECTED){
						return true;
					}
				}
			}
		}
		showNetFailInfo(c);
		return false;
	}
	
	public static void showNetFailInfo(Context context){
		Toast.makeText(context,context.getResources().getString(R.string.NOT_NETWORK_CONNECTION), Toast.LENGTH_LONG).show();
	}
	
}
