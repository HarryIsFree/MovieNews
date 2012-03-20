package org.lvxiang.MovieNews.Activities;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.lvxiang.MovieNews.Adapters.SearchResultAdapter;
import org.lvxiang.MovieNews.DAO.DataBaseUtil;
import org.lvxiang.MovieNews.Utility.HtmlUtil;
import org.lvxiang.MovieNews.Utility.ImageBuffer;
import org.lvxiang.MovieNews.Utility.NetworkUtil;
import org.lvxiang.MovieNews.View.R;
import org.lvxiang.MovieNews.bean.Movie;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class SearchResultActivity extends MyActivity implements Callback{

	private ListView mListView;
	private ProgressDialog pDialog;
	private Dialog dialog;
	private WebView webView;
	private Button store;
	private Button pics;
	private Button exit;
	private LayoutInflater inflater;
	private Movie currentMovie;
	private LinearLayout detailView;
	private Handler handler;
	private Hashtable<Integer,Movie> loadedMovies;
	
	private static final String MESSAGE_TYPE="type";	
	private static final String IMAGE_ID="ids";
	private static final String FILM_ID="film_id";
	
	private static final int DISMISS_PROGRESS_DIALOG=0x001;
	private static final int SHOW_PIC_ACTIVITY=0x002;
	
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result_activity);
		
		init();
	}
	
	private void init(){
		
		handler=new Handler(this);
		
		myDB=new DataBaseUtil(this);
		loadedMovies=new Hashtable<Integer,Movie>();
		pDialog=new ProgressDialog(this);
		
		mListView=(ListView) this.findViewById(R.id.search_result_listView);
		
		mListView.setAdapter(new SearchResultAdapter(this,MyActivity.searchResults));
		mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				int id=MyActivity.searchResults.get(arg2).getID();
				if(loadedMovies.containsKey(id)){
					currentMovie=loadedMovies.get(id);
					showDialog();
				}
				else{
					if(NetworkUtil.checkNetState(SearchResultActivity.this)){
					showProgressDialog("loading...");
					new PageLoadThread(arg2).start();
					}
				
				}
			}
			
		});
		
		initDialog();
	}
	
	private void initDialog(){
		
		inflater=LayoutInflater.from(this);
		
		dialog=new Dialog(this);
		
		pDialog=new ProgressDialog(this);
		pDialog.setIndeterminate(true);
		
		View view=inflater.inflate(R.layout.detail_info_layout, null);
		dialog.setContentView(view);
		
		webView=new WebView(this);
		detailView=(LinearLayout)view.findViewById(R.id.detail_introduction);
		detailView.addView(webView,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		store=(Button) view.findViewById(R.id.detail_button1);
		store.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(myDB.contains(currentMovie.getID())){
					Toast.makeText(SearchResultActivity.this, R.string.MOVIE_EXIST, Toast.LENGTH_SHORT).show();
				}
				else{
					if(myDB.addMovie(currentMovie)){
						Toast.makeText(SearchResultActivity.this, R.string.STORE_SUCCESS, Toast.LENGTH_SHORT).show();
					}
				}
			}
			
		});
		pics=(Button)view.findViewById(R.id.detail_button3);
		pics.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				dialog.dismiss();
				showProgressDialog("loading...");
				new ImageLoadThread(currentMovie.getID()).start(); 
			}
			
		});
		exit= (Button) view.findViewById(R.id.detail_button2);
		exit.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
			
		});
		
	}
	
	private void showDialog(){
		webView.loadDataWithBaseURL(null, currentMovie.getDetailHtml(), 
				getResources().getString(R.string.MIME_TYPE), 
				getResources().getString(R.string.ENCODING), 
				null);
		dialog.show();
	}
	
	private void showProgressDialog(String msg){
		pDialog.setMessage(msg);
		pDialog.show();
	}
	
	/**
	 * the thread to load pictures from douban.com
	 * @author lvxiang
	 *
	 */
	private class ImageLoadThread extends Thread{
		
		private int image_id;
		
		public ImageLoadThread(int imageID){
			this.image_id=imageID;
		}
		
		public void run(){

			Bundle bundle=new Bundle();
			
			Set<String> ids=new HashSet<String>();
			String[] array=new String[20];
			if(ImageBuffer.contains(image_id)){
				array=ImageBuffer.getPicIds(image_id);
			}
			else{
				ids=HtmlUtil.getImageIDs(image_id);
				array=ids.toArray(array);
				ImageBuffer.addPicIds(image_id, array);
			}
			
			bundle.putInt(FILM_ID, image_id);
			bundle.putStringArray(IMAGE_ID, array);
			bundle.putInt(MESSAGE_TYPE, SHOW_PIC_ACTIVITY);
			
			Message message=new Message();
			message.setData(bundle);
			handler.sendMessage(message);
		}
		
	}
	
	private class PageLoadThread extends Thread{
		
		private int index;
		
		public PageLoadThread(int i){
			this.index=i;
		}
		
		public void run(){
			try {
				currentMovie=HtmlUtil.getDetailedMovieInfo(MyActivity.searchResults.get(index), SearchResultActivity.this);
				loadedMovies.put(currentMovie.getID(), currentMovie);
				Bundle bundle=new Bundle();
				bundle.putInt(MESSAGE_TYPE,DISMISS_PROGRESS_DIALOG);
				Message message=new Message();
				message.setData(bundle);
				handler.sendMessage(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		Bundle bundle=msg.getData();
		int type=bundle.getInt(MESSAGE_TYPE);
		switch(type){
		case DISMISS_PROGRESS_DIALOG:{
			pDialog.dismiss();
			showDialog();
			break;
		}
		case SHOW_PIC_ACTIVITY:{
			
			pDialog.dismiss();
			Intent intent=new Intent(SearchResultActivity.this,PicActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		}
		return false;
	}
}
