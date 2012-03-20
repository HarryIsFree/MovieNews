package org.lvxiang.MovieNews.Activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.lvxiang.MovieNews.Utility.ActivityUtil;
import org.lvxiang.MovieNews.Utility.Constants;
import org.lvxiang.MovieNews.Utility.HtmlUtil;
import org.lvxiang.MovieNews.Utility.NetworkUtil;
import org.lvxiang.MovieNews.View.R;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SearchActivity extends MyActivity implements Callback{

	private EditText editText;
	private ImageButton searchButton;
	private Button new_movie;
	private Button my_store;
	private Button box_office;
	private Button history;
	private Handler handler;
	private ProgressDialog pDialog;
	
	private static final String URL_PREFIX="http://movie.douban.com/subject_search?search_text=";
	private static final String URL_SUFFIX="&cat=1002";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.search_layout);
		
		handler=new Handler(SearchActivity.this);
		
		pDialog=new ProgressDialog(SearchActivity.this);
		pDialog.setMessage("loading...");
		
		editText=(EditText) this.findViewById(R.id.search_text);
		editText.setHint(getResources().getText(R.string.SEARCH_MOVIE));
		
		searchButton=(ImageButton) this.findViewById(R.id.search_imageButton);
		searchButton.setAlpha(128);
		searchButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					if(NetworkUtil.checkNetState(SearchActivity.this))
						search(editText.getText().toString());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		new_movie=(Button) this.findViewById(R.id.search_new);
		new_movie.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(NetworkUtil.checkNetState(SearchActivity.this)){
				Intent intent=new Intent();
				intent.setClass(SearchActivity.this, MainActivity.class);
				intent.putExtra(Constants.TAG_INDEX, Constants.TAG_ONE);
				startActivity(intent);
				}
			}
			
		});
		
		my_store=(Button) this.findViewById(R.id.search_store);
		my_store.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(SearchActivity.this, MainActivity.class);
				intent.putExtra(Constants.TAG_INDEX, Constants.TAG_TWO);
				startActivity(intent);
			}
			
		});
		
		box_office=(Button) this.findViewById(R.id.search_box);
		box_office.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(NetworkUtil.checkNetState(SearchActivity.this)){
				Intent intent=new Intent();
				intent.setClass(SearchActivity.this, MainActivity.class);
				intent.putExtra(Constants.TAG_INDEX, Constants.TAG_THREE);
				startActivity(intent);
				}
			}
			
		});
		
		history=(Button) this.findViewById(R.id.search_history);
		history.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(SearchActivity.this, SentencesActivity.class);
				startActivity(intent);
			}
			
		});
		
		ActivityUtil.add(this);
	}
	
	private void search(String str) throws UnsupportedEncodingException{
		String url=URL_PREFIX+URLEncoder.encode(str, "utf-8")+URL_SUFFIX;
		System.out.println(url);
		
		pDialog.show();
		new DownloadThread(url).start();
		
	}
	
	private class DownloadThread extends Thread{
		
		private String url;
		
		public DownloadThread(String url){
			this.url=url;
		}
		
		public void run(){
			try {
				Document doc=Jsoup.connect(url).get();
				
				searchResults=HtmlUtil.getSearchResult(doc);
				
				Message message=new Message();
				handler.sendMessageAtFrontOfQueue(message);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		pDialog.dismiss();
		if(searchResults!=null&&searchResults.size()>0){
			Intent intent=new Intent(SearchActivity.this,SearchResultActivity.class);
			startActivity(intent);
		}
		else{
			Toast.makeText(this, R.string.NO_RESULTS_FOUND, Toast.LENGTH_LONG).show();
		}
		return false;
	}

	public void onDestroy(){
		ActivityUtil.remove(this);
		super.onDestroy();
	}
	
	
}
