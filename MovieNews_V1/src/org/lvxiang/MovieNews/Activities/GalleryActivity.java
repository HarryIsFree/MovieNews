package org.lvxiang.MovieNews.Activities;

import org.lvxiang.MovieNews.Adapters.MySpinnerAdapter;
import org.lvxiang.MovieNews.Utility.Constants;
import org.lvxiang.MovieNews.Utility.ImageBuffer;
import org.lvxiang.MovieNews.View.R;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Gallery;

public class GalleryActivity extends Activity{

	private String[] ids;
	private Gallery gallery;
	private int start_point;
	private int film_id;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_view);
		
		Bundle bundle=this.getIntent().getExtras();
		
		start_point=bundle.getInt(Constants.START_POINT);
		ids=bundle.getStringArray(Constants.PIC_ARRAY);
		film_id=bundle.getInt(Constants.FILM_ID);
		
		//MySpinnerAdapter adapter=new MySpinerAdapter(this)
		gallery=(Gallery) this.findViewById(R.id.gallery01);
		gallery.setAdapter(new MySpinnerAdapter(this,ids,start_point,ImageBuffer.getImageArray(film_id)));
		gallery.setSelection(start_point);
		
	}
	
}
