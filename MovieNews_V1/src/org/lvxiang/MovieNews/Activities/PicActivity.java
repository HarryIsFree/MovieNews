package org.lvxiang.MovieNews.Activities;


import org.lvxiang.MovieNews.Adapters.ImageAdapter;
import org.lvxiang.MovieNews.Utility.ImageBuffer;
import org.lvxiang.MovieNews.View.R;


import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.GridView;

public class PicActivity extends Activity{

	private GridView mGridView;
	private DisplayMetrics dm;
	private String[]  pic_ids;
	private int current_film_id;
	
	private static final String FILM_ID="film_id";
	private static final String IMAGE_ID="ids";
	
	@Override 
	public void onStart(){
		super.onStart();
		dm=new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_view_layout);
		
		Bundle bundle=this.getIntent().getExtras();
		
		//get current film id from last activity passed by bundle
		current_film_id=bundle.getInt(FILM_ID);
		
		//get the pic id array from bundle passed from last activity
		pic_ids=bundle.getStringArray(IMAGE_ID);
		
		Log.d("Movie News", ""+pic_ids.length);
		//num of grids in the grid will be decided by the pic array's length
		mGridView=(GridView)findViewById(R.id.gridView01);
		
		ImageAdapter adapter=new ImageAdapter(this,pic_ids,current_film_id);
		
		mGridView.setAdapter(adapter);
		
		//if we have pics of the same film in buffer, we will load them from
		//buffer, instead of from the Internet
		if(ImageBuffer.contains(current_film_id)){
			adapter.setImageArray(ImageBuffer.getImageArray(current_film_id));
		}
		else
		//if the film is not found in buffer, we have to download it from the Internet
		{
			adapter.startDownloadThread();
		}
	}

	public void onConfigurationChanged(Configuration config){
		
		super.onConfigurationChanged(config);
	}
	
}
