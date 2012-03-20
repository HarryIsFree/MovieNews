package org.lvxiang.MovieNews.Activities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lvxiang.MovieNews.Adapters.EfficientAdapter;
import org.lvxiang.MovieNews.Adapters.FavoriteAdapter;
import org.lvxiang.MovieNews.Adapters.TopTenAdapter;
import org.lvxiang.MovieNews.DAO.DataBaseUtil;
import org.lvxiang.MovieNews.Utility.ActivityUtil;
import org.lvxiang.MovieNews.Utility.Constants;
import org.lvxiang.MovieNews.Utility.FileBuffer;
import org.lvxiang.MovieNews.Utility.HtmlUtil;
import org.lvxiang.MovieNews.Utility.ImageBuffer;
import org.lvxiang.MovieNews.Utility.NetworkUtil;
import org.lvxiang.MovieNews.View.R;
import org.lvxiang.MovieNews.bean.Movie;



import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

public class MainActivity extends TabActivity implements Callback{

	private TabHost tabHost;
	private ListView newMoviesListView;
	private ListView favorates;
	private ListView topTenMovieListView;
	private LinearLayout     top_ten_view;
	private List<Movie> newMovieList;
	private List<Movie> favoriteMovieList;
	private List<Movie> topTenMovieList;
	private LayoutInflater mInflater;
	private Button  store;
	private Button  exit;
	private Button  pics;
	private ProgressDialog pDialog;
	private Dialog dialog;
	private WebView webView;
	private Handler handler;
	
	private DataBaseUtil myDB;
	private Movie[]      loadedMovies;
	private LinearLayout detailView;
	private int     initialIndex;
	private FavoriteAdapter favoriteAdapter;
	private boolean first=true;
	
	
	private static SharedPreferences settings;
	
	private static final String NUM_START="num_of_start";
	private static final String APP_NAME="MOVIE NEWS";
	private static final String DB_CHANGED="DB CHANGED";
	private static final String TAB_ONE="tab1";
	private static final String TAB_TWO="tab2";
	private static final String TAB_THREE="tab3";
	private static final String MESSAGE_TYPE="instruction";
	private static final String IMAGE_ID="ids";
	private static final String FILM_ID="film_id";
	
	private static final int    DISMISS_DIALOG=1;
	private static final int    REFRESH_LIST=2;
	private static final int    START_IMAGE_ACTIVITY=3;
	private static final int    DISMISS_PDIALOG=4;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		ActivityUtil.add(this);
		init();
		
	}
	
	/**
	 * initial function to call on activity start
	 */
	public void init(){
		Intent intent=this.getIntent();
		initialIndex=intent.getIntExtra(Constants.TAG_INDEX, 0);
		handler=new Handler(MainActivity.this);
		showProgressDialog("loading...");
		new InitThread().start();
				
	}
	
	private void continueInit(){
		
		myDB=new DataBaseUtil(this);
		
		tabHost=getTabHost();
		
		newMoviesListView=(ListView)findViewById(R.id.listView1);
		newMoviesListView.setAdapter(new EfficientAdapter(this,newMovieList));
		newMoviesListView.setBackgroundColor(Color.WHITE);
		newMoviesListView.setOnItemLongClickListener(new MyOnItemLongClickListener());
		newMoviesListView.setCacheColorHint(Color.TRANSPARENT);
		
		favorates=(ListView)findViewById(R.id.listView2);
		favoriteAdapter=new FavoriteAdapter(this,myDB);
		favorates.setAdapter(favoriteAdapter);
		favorates.setBackgroundColor(Color.WHITE);
		favorates.setCacheColorHint(Color.TRANSPARENT);

		top_ten_view=(LinearLayout)findViewById(R.id.top_ten_view);
		mInflater=LayoutInflater.from(this);
		View top_ten_sub_view=mInflater.inflate(R.layout.top_ten_layout,null);
		topTenMovieListView=(ListView) top_ten_sub_view.findViewById(R.id.top_listView);
		topTenMovieListView.setAdapter(new TopTenAdapter(this,topTenMovieList));
		top_ten_view.addView(top_ten_sub_view);
		
		
		tabHost.addTab(tabHost.newTabSpec(TAB_ONE)
				.setIndicator(getResources().getString(R.string.NEW_MOVIE))
				.setContent(R.id.listView1)
				);
		tabHost.addTab(tabHost.newTabSpec(TAB_TWO)
				.setIndicator(getResources().getString(R.string.STORE_MOVIE))
				.setContent(R.id.listView2)
				);
		tabHost.addTab(tabHost.newTabSpec(TAB_THREE)
				.setIndicator(getResources().getString(R.string.BOX_OFFICE))
				.setContent(R.id.top_ten_view)
				);
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){

			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if(tabId.equals(TAB_TWO)){
					//showProgressDialog("loading...");
					if(first){
					if(initialIndex!=1)
						new DBLoadThread().start();
					first=false;
					}
					else{
						new DBLoadThread().start();
					}
				}
			}
			
		});
		//tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.WHITE);
		
		if(firstStart()){
			myDB.firstStart();
		}

		if(initialIndex==Constants.TAG_TWO){
			//showProgressDialog("loading...");
			new DBLoadThread().start();
		}
		tabHost.setCurrentTab(initialIndex);
	}
	
	/**
	 * initialize the movie list. call the method from HtmlUtil
	 */
	private void initMovieList(){
		
		if((newMovieList=FileBuffer.getNewMovieList())!=null){
			loadedMovies=new Movie[newMovieList.size()];
		}
		else{
		newMovieList=HtmlUtil.getIntroduction("http://movie.douban.com/");
		
		while(newMovieList==null){
			Dialog dialog=null;
			Builder builder=new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("抱歉");
			builder.setMessage("新片榜单加载失败");
			builder.setPositiveButton("重试", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					newMovieList=HtmlUtil.getIntroduction("http://movie.douban.com/");
				}
			});
			
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
				
			});
			dialog=builder.create();
			dialog.show();
		}
			if(newMovieList!=null){
				loadedMovies=new Movie[newMovieList.size()];
				FileBuffer.setNewMovieList(newMovieList);
			}
		}
		
		if((topTenMovieList=FileBuffer.getTopMovieList())!=null);
		else{
			topTenMovieList=HtmlUtil.getTopTenMovieList("http://ent.qq.com/c/pf.shtml");
			
			if(topTenMovieList==null){
				topTenMovieList=HtmlUtil.getTopTenMovieList("http://ent.qq.com/c/pf.shtml");
			}
			FileBuffer.setTopMovieList(topTenMovieList);
		}
		
		
	}

	/**
	 * the thread that downloads initial web page from the Internet
	 * @author lvxiang
	 *
	 */
	private class InitThread extends Thread{
		
		public void run(){
			Looper.prepare();
			initMovieList();
			Message message=new Message();
			Bundle bundle=new Bundle();
			bundle.putInt(MESSAGE_TYPE, DISMISS_PDIALOG);
			message.setData(bundle);
			handler.sendMessage(message);
		}
		
	}
	
	/**
	 * show a progress dialog with a specified message
	 * @param msg the message to show
	 */
	private void showProgressDialog(String msg){
		pDialog=new ProgressDialog(MainActivity.this,ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage(msg);
		pDialog.setIndeterminate(true);
		pDialog.show();
	}
	
	private class MyOnItemLongClickListener implements OnItemLongClickListener{

		
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int arg2, long arg3) {
			// TODO Auto-generated method stub
			
			dialog=new Dialog(MainActivity.this);
			dialog.setTitle(newMovieList.get(arg2).getMovieName());
			
			View view=mInflater.inflate(R.layout.detail_info_layout, null);
			dialog.setContentView(view);
			
			webView=new WebView(MainActivity.this);
			detailView=(LinearLayout)view.findViewById(R.id.detail_introduction);
			detailView.addView(webView,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			
			store=(Button) view.findViewById(R.id.detail_button1);
			store.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(myDB.contains(loadedMovies[arg2].getID())){
						Toast.makeText(MainActivity.this, R.string.MOVIE_EXIST, Toast.LENGTH_SHORT).show();
					}
					else{
						if(myDB.addMovie(loadedMovies[arg2])){
							SharedPreferences.Editor editor=settings.edit();
							editor.putBoolean(DB_CHANGED, true);
							editor.commit();
							Toast.makeText(MainActivity.this, R.string.STORE_SUCCESS, Toast.LENGTH_SHORT).show();
						}
					}
				}
				
			});
			pics=(Button)view.findViewById(R.id.detail_button3);
			pics.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					if(NetworkUtil.checkNetState(MainActivity.this)){
					dialog.dismiss();
					pDialog=new ProgressDialog(MainActivity.this);
					pDialog.setIndeterminate(true);
					pDialog.setMessage("正在加载");
					pDialog.show();
					new ImageLoadThread(newMovieList.get(arg2).getID()).start(); 
					}
				}
				
			});
			exit= (Button) view.findViewById(R.id.detail_button2);
			exit.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
				
			});
			
			Thread t=new MyThread(arg2);
			
			if(loadedMovies[arg2]==null){
				if(NetworkUtil.checkNetState(MainActivity.this)){
				pDialog=new ProgressDialog(MainActivity.this,ProgressDialog.STYLE_SPINNER);
				pDialog.setIndeterminate(true);
				pDialog.setMessage("正在加载");
				pDialog.show();
				t.start();}
			}
			
			else{
				webView.loadDataWithBaseURL(null, loadedMovies[arg2].getDetailHtml(), 
				MainActivity.this.getResources().getString(R.string.MIME_TYPE),
				MainActivity.this.getResources().getString(R.string.ENCODING),
				null);
				dialog.show();
			}
			return true;
		}
		
	}

	
	/**
	 * check the saved preferences to see if the application is first started.
	 * If first started, database need to be initialized.
	 * @return
	 */
	private boolean firstStart(){
		settings=this.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
		if(settings.contains(NUM_START))//the preference does not contains the key, which indicates that 
										//it is not the first time the application is started
		{
			Log.i("Movie_News:", "application not first stared.");
			return false;
		}
		else{
			Log.i("Movie_News:", "application first stared.");
			SharedPreferences.Editor editor=settings.edit();
			editor.putInt(NUM_START, 1);
			editor.putBoolean(DB_CHANGED, true);
			editor.commit();
		}
		return true;
	}
	
	/**
	 * get selected picture from cache
	 * @param position
	 * @return
	 */
	private Bitmap getBitmapFromCache(int position){
		
		Bitmap pic=null;

		try {
			Log.d("Movie News:", newMovieList.get(position).getImgUrl());
			URL url=new URL(newMovieList.get(position).getImgUrl());
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			pic=BitmapFactory.decodeStream(conn.getInputStream());
		    } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
				
		return pic;
	}
	
	@Override
	public void onDestroy(){
		SharedPreferences.Editor editor=settings.edit();
		if(!settings.contains(NUM_START)){
			editor.putInt(NUM_START, 1);
		}
		editor.putBoolean(DB_CHANGED, true);
		editor.commit();
		myDB.closeDatabase();
		ActivityUtil.remove(this);
		super.onDestroy();
	}
	
	/**
	 * Self defined thread used to load detailed info of a movie. 
	 * when the loading process is over, use a handler to send message to the main thread to dismiss the ProcessDialog and
	 * show the info dialog which contains detailed info a movie.
	 * @author lvxiang
	 *
	 */
	public class MyThread extends Thread{
		
		int arg2;
		
		public MyThread(int index){
			arg2=index;
		}
		
		public void run(){
			
			Looper.prepare();
			try{
				loadedMovies[arg2]=HtmlUtil.getDetailedMovieInfo(newMovieList.get(arg2),MainActivity.this);
			}catch(SocketException e){
				try {
					loadedMovies[arg2]=HtmlUtil.getDetailedMovieInfo(newMovieList.get(arg2),MainActivity.this);
				} catch (SocketException e1) {
					// TODO Auto-generated catch blo
					
				}
			}
			loadedMovies[arg2].setCover(getBitmapFromCache(arg2));
			webView.loadDataWithBaseURL(null, loadedMovies[arg2].getDetailHtml(), 
					MainActivity.this.getResources().getString(R.string.MIME_TYPE),
					MainActivity.this.getResources().getString(R.string.ENCODING),
					null);
			Bundle bundle=new Bundle();
			bundle.putInt(MESSAGE_TYPE, DISMISS_DIALOG);
			Message message=new Message();
			message.setData(bundle);
			handler.sendMessage(message);
		}
	}
	
	/**
	 * the thread to load movies from database
	 * @author lvxiang
	 *
	 */
	private class DBLoadThread extends Thread{
		
		public void run(){
				favoriteMovieList=myDB.getMoviesInDatabase();
				favoriteAdapter.setMovieList(favoriteMovieList);
				if(pDialog!=null&&pDialog.isShowing())
					pDialog.dismiss();
				
				Bundle bundle=new Bundle();
				bundle.putInt(MESSAGE_TYPE, REFRESH_LIST);
				Message message=new Message();
				message.setData(bundle);
				handler.sendMessage(message);
					
		}
		
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
			bundle.putInt(MESSAGE_TYPE, START_IMAGE_ACTIVITY);
			
			Message message=new Message();
			message.setData(bundle);
			handler.sendMessage(message);
		}
		
	}
	
	
	/**
	 * override method that will deal with message got.
	 */
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		Bundle bundle=arg0.getData();
		switch(bundle.getInt(MESSAGE_TYPE)){
			case DISMISS_PDIALOG:{
				pDialog.dismiss();
				continueInit();
				break;
			}
			case DISMISS_DIALOG:{
				pDialog.dismiss();
				dialog.show();
				break;
			}
			case REFRESH_LIST:{
				pDialog.dismiss();
				favoriteAdapter.notifyDataSetChanged();
				break;
			}
			case START_IMAGE_ACTIVITY:{
				//an array of integers that specify pictures s
				//ids is also sent through the bundle
				pDialog.dismiss();
				
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, PicActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		
		menu.add(0, 0,0 ,R.string.HOME_PAGE);
		menu.add(0, 1, 1, R.string.EXIT_APP);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch(item.getItemId()){
		case 0:{
			MainActivity.this.finish();
			break;
		}
		case 1:{
			ActivityUtil.finishAll();
			break;
		}
		}
		
		return true;
		
	}
}
