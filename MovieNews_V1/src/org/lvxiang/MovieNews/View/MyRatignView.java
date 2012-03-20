package org.lvxiang.MovieNews.View;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Self defined class which extends android.View. 
 * This View is used to show stars according to its rating by calling the overridden 
 * {@link #onDraw(android.graphics.Canvas)} method.
 * 
 * A final array defined in the class will be used to determine which stars to show.
 * There are five kinds of stars: Empty star;A fifth star;Half star;Three fifths star;
 * Full star. Each of them will be used accordingly to the rating. 
 * 
 * For example a rating of 5.5 will render 2 and a 3 fifths star.
 * @see android.View
 * @author lvxiang
 *
 */
public class MyRatignView extends View{

	public MyRatignView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void onDraw(Canvas canvas){
		
	}
}
