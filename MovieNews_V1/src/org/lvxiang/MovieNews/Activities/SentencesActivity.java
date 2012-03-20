package org.lvxiang.MovieNews.Activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.lvxiang.MovieNews.Utility.ActivityUtil;
import org.lvxiang.MovieNews.Utility.Constants;
import org.lvxiang.MovieNews.Utility.HtmlUtil;
import org.lvxiang.MovieNews.Utility.NetworkUtil;
import org.lvxiang.MovieNews.View.R;
import org.lvxiang.MovieNews.View.SentenceView;

import com.sileria.android.Kit;
import com.sileria.android.SlidingTray;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SentencesActivity extends MyActivity implements Callback{

	private SentenceView sv;
	private ImageButton left;
	private ImageButton right;
	private ImageButton search;
	private TextView textView;
	private ProgressDialog pDialog;
	private Handler handler;
	private ImageView handle;
	private EditText edit;
	private Button   button;
	private LayoutInflater inflater;
	private ImageButton restoreButton;
	private SlidingTray st;
	
	private static final String URL_PREFIX="http://movie.douban.com/subject_search?search_text=";
	private static final String URL_SUFFIX="&cat=1002";
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
			initPortrait();
		}
		else if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
			initLandscape();
		}
		
		handler=new Handler(this);
		
		pDialog=new ProgressDialog(this);
		pDialog.setIndeterminate(true);
		pDialog.setMessage("loading");
		
		ActivityUtil.add(this);
		
	}
	
	
	private void initPortrait(){
		this.setContentView(R.layout.sentence_activity_layout);
		addSlidingDrawer(true);
		if(sv!=null){
			copeWithSentenceView(Constants.PROTRAIT);
		}
		else
			sv=(SentenceView) this.findViewById(R.id.sentence_sentenceView);
		left=(ImageButton) this.findViewById(R.id.sentence_left);
		right=(ImageButton) this.findViewById(R.id.sentence_right);
		textView=(TextView) this.findViewById(R.id.sentence_textView);
		search=(ImageButton) this.findViewById(R.id.sentence_imageButton);
		restoreButton=(ImageButton) this.findViewById(R.id.sentence_restore);
		
		initComponents();
	}
	
	private void initLandscape(){
		this.setContentView(R.layout.sentences_activity_landscape);
		addSlidingDrawer(false);
		if(sv!=null){
			copeWithSentenceView(Constants.LANDSCAPE);
		}
		else
			sv=(SentenceView) this.findViewById(R.id.sentence_sentenceView_land);
		left=(ImageButton) this.findViewById(R.id.sentence_left_land);
		right=(ImageButton) this.findViewById(R.id.sentence_right_land);
		textView=(TextView) this.findViewById(R.id.sentence_textView_land);
		search=(ImageButton) this.findViewById(R.id.sentence_imageButton_land);
		restoreButton=(ImageButton) this.findViewById(R.id.sentence_restore_land);
		
		initComponents();
	}
	
	private void initComponents(){
		
		left.setAlpha(100);
		left.setVisibility(View.INVISIBLE);
		left.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				textView.setText(sv.previous());
			}
			
		});
		
		right.setAlpha(100);
		right.setVisibility(View.INVISIBLE);
		right.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				textView.setText(sv.next());
			}
			
		});
		
		textView.setTextSize(20);
		textView.setText(sv.getCurrentName());
		textView.setTextColor(Color.BLACK);
		
		sv.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(left.isShown()){
					left.setVisibility(View.INVISIBLE);
					right.setVisibility(View.INVISIBLE);
				}
				else{
					left.setVisibility(View.VISIBLE);
					right.setVisibility(View.VISIBLE);
				}
			}
			
		});
		
		search.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(NetworkUtil.checkNetState(SentencesActivity.this)){
				pDialog.show();
				try {
					search(textView.getText().toString());
					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
			
		});
		
		if(sv.isSearchModel()){
			restoreButton.setVisibility(View.VISIBLE);
		}
		else{
			restoreButton.setVisibility(View.INVISIBLE);
		}
		
		restoreButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				sv.restoreState();
				restoreButton.setVisibility(View.INVISIBLE);
				st.setVisibility(View.VISIBLE);
				textView.setText(sv.getCurrentName());
			}
		});
		
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
			Intent intent=new Intent(SentencesActivity.this,SearchResultActivity.class);
			startActivity(intent);
		}
		else{
			Toast.makeText(this, R.string.NO_RESULTS_FOUND, Toast.LENGTH_LONG).show();
		}
		return false;
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0, 0, 0, R.string.SEND_SMS);
		menu.add(0, 1, 1, R.string.MOVIE_LIST);
		menu.add(0, 2, 2, R.string.KEYWORD);
		menu.add(1,3,0,R.string.HOME_PAGE);
		menu.add(1,4,1,R.string.EXIT_APP);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * add a slidingdrawer to the current view
	 */
	private void addSlidingDrawer(boolean portrait){
		
		inflater=LayoutInflater.from(this);
		View slidingView=inflater.inflate(R.layout.sliding_tray_layout, null);
		edit=(EditText) slidingView.findViewById(R.id.sentence_searchText);
		button=(Button) slidingView.findViewById(R.id.sentence_confirm);
		button.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sv.find(edit.getText().toString(),SentencesActivity.this)){
				restoreButton.setVisibility(View.VISIBLE);
				st.animateClose();
				st.setVisibility(View.INVISIBLE);
				textView.setText(sv.getCurrentName());
				
				Toast.makeText(SentencesActivity.this,getResources().getString(R.string.RETURN_HINT), Toast.LENGTH_LONG).show();
				}
			}
			
		});
		
		handle=new ImageButton(this);
		handle.setLayoutParams(new LayoutParams(100,30));
		handle.setBackgroundResource(R.drawable.down);

		Kit.init(this);
		st=new SlidingTray(this,handle,slidingView,SlidingTray.TOP);
		if(portrait)
			st.setTopOffset(630);
		else
			st.setTopOffset(310);
		
		
		
		this.addContentView(st, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case 0:{
			Intent intent=new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"));
			intent.putExtra("sms_body", sv.getCurrentSentence());
			startActivity(intent);
			break;
		}
		case 1:{
			Builder builder=new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.CHOOSE_MOVIE));
			builder.setItems(sv.getNames(), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					sv.setCurrentMovie(sv.getNames()[which]);
					textView.setText(sv.getNames()[which]);
				}
			});
			AlertDialog dialog=builder.create();
			dialog.show();
			break;
		}
		case 2:{
			if(!st.isOpened()){
				st.open();
			}
			break;
		}
		case 3:{
			SentencesActivity.this.finish();
			break;
		}
		case 4:{
			ActivityUtil.finishAll();
			break;
		}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onConfigurationChanged(Configuration config){
		super.onConfigurationChanged(config);
		if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
			this.initPortrait();
		}
		else if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
			this.initLandscape();
		}
	}
	
	private void copeWithSentenceView(int type){
		if(sv.isSearchModel()){
			st.setVisibility(View.INVISIBLE);
			Hashtable<String,ArrayList<String>> table=sv.getCurrentTable();
			String[] name=sv.getCurrentNames();
			int nameIndex=sv.getNameIndex();
			int index=sv.getIndex();
			if(type==Constants.PROTRAIT)
				sv=(SentenceView) this.findViewById(R.id.sentence_sentenceView);
			else
				sv=(SentenceView) this.findViewById(R.id.sentence_sentenceView_land);
			sv.setCurrentArgs(nameIndex, index,table,name);
			sv.setSearchModel();
		}
		else{
			int nameIndex=sv.getNameIndex();
			int index=sv.getIndex();
			if(type==Constants.PROTRAIT)
				sv=(SentenceView) this.findViewById(R.id.sentence_sentenceView);
			else
				sv=(SentenceView) this.findViewById(R.id.sentence_sentenceView_land);
			sv.setCurrentArgs(nameIndex, index);
		}
	}
	
	@Override
	public void onDestroy(){
		ActivityUtil.remove(this);
		super.onDestroy();
	}
}
