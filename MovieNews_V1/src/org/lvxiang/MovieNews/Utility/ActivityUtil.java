package org.lvxiang.MovieNews.Utility;

import java.util.ArrayList;

import android.app.Activity;

public class ActivityUtil {

	private static ArrayList<Activity> activityList=new ArrayList<Activity>();
	
	public static void add(Activity ac){
		activityList.add(ac);
	}
	
	public static void remove(Activity a){
		activityList.remove(a);
	}
	
	public static void finishAll(){
		for(Activity a:activityList){
			a.finish();
		}
	}
	
}
