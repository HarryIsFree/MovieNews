package org.lvxiang.MovieNews.View;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Locale;

import org.apache.http.util.EncodingUtils;
import org.lvxiang.MovieNews.Utility.Constants;
import org.lvxiang.MovieNews.Utility.FileBuffer;
import org.lvxiang.MovieNews.bean.MyChar;
import org.lvxiang.MovieNews.bean.Sentence;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

public class SentenceView extends View{

	private int w;
	private int h;
	private String sentenceToShow="如果晚上月亮升起的时候，月光照到我的门口，我希望月光女神能满足我一个愿望，我想要一双人类的手。我想用我的双手把我的爱人紧紧地拥在怀中，哪怕只有一次。如果我从来没有品尝过温暖的感觉，也许我不会这样寒冷；如果我从没有感受过爱情的甜美，我也许就不会这样地痛苦。如果我没有遇到善良的佩格，如果我从来没不曾离开过我的房间，我就不会知道我原来是这样的孤独。";
	
	private static int padding=40;
	
	private float  current_y;
	private float  current_x;
	private int    interval=25;
	private static final String ENCODING="utf-8";
	
	private Paint largeTextPaint;
	private Paint headerTextPaint;
	private Paint smallTextPaint;
	
	private Hashtable<String,ArrayList<String>> sentences;
	private ArrayList<String> names;
	private ArrayList<String> currentList;
	private String[] sortedNames;
	private int currentIndex;
	private int currentNameIndex;
	private int currentIndexCopy;
	private int currentNameIndexCopy;
	private boolean searchModel;
	
	public SentenceView(Context context){
		super(context);
		init();
	}
	
	public SentenceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init(){
		searchModel=false;
		sentences=new Hashtable<String,ArrayList<String>>();
		names=new ArrayList<String>();
		
		if(FileBuffer.getNames()!=null){
			sentences=FileBuffer.getSentences();
			sortedNames=FileBuffer.getNames();
		}
		else
			readFile();
		
		currentIndex=0;
		currentNameIndex=0;
		currentList=sentences.get(sortedNames[currentNameIndex]);
		sentenceToShow=currentList.get(currentIndex);
		
		largeTextPaint=new Paint();
		largeTextPaint.setColor(Color.BLACK);
		largeTextPaint.setTextSize(50);
		largeTextPaint.setAntiAlias(true);
		largeTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		
		headerTextPaint=new Paint();
		headerTextPaint.setColor(Color.BLACK);
		headerTextPaint.setTextSize(50);
		headerTextPaint.setAntiAlias(true);
		headerTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		
		smallTextPaint=new Paint();
		smallTextPaint.setColor(Color.BLACK);
		smallTextPaint.setTextSize(20);
		smallTextPaint.setAntiAlias(true);
		smallTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		
		this.setBackgroundColor(Color.WHITE);
		
	}
	
	@Override
	public void draw(Canvas canvas){
		super.draw(canvas);
		
		w=this.getWidth();
		h=this.getHeight();
		
		showSentence(canvas,sentenceToShow);
		
	}
	
	public void showSentence(Canvas canvas,String str){
	
		char[] chars=str.toCharArray();
		int drawableHeight=h-padding*2;
		int drawableWidth =w-padding*2;
		
		current_y=padding;
		current_x=padding;
		
		float totalLength=headerTextPaint.measureText(chars, 0, 1);
		totalLength+=smallTextPaint.measureText(chars,1,chars.length-1);
		
		//the number of lines that this sentence will take on the screen
		int lines=(int) (totalLength/drawableWidth)+1;
		int availableLines=drawableHeight/interval;
		int startLine=(availableLines-lines)/2;
		current_y=interval*startLine;
		
		canvas.drawText("“", 10, current_y-padding/2, largeTextPaint);

		if(!isSearchModel()){
			this.drawPlainChars(chars, canvas, drawableWidth);
		}
		else{
			this.drawHighlightChars(chars, canvas, drawableWidth);
		}
		
		
		canvas.drawText("”",drawableWidth+padding, current_y+interval*2, largeTextPaint);
	}
	
	/**
	 * draw characters in a plain form, in which case all the chars are in black
	 * @param chars the characters to be drawn
	 * @param canvas the canvas to draw characters on
	 * @param drawableWidth the available canvas width to draw characters
	 */
	private void drawPlainChars(char[] chars,Canvas canvas,int drawableWidth){
			for(int i=0;i<chars.length;i++){
			
			float currentTextWidth=0;
			
			if(i==0){
				currentTextWidth=headerTextPaint.measureText(chars, 0, 1);
				if(current_x+currentTextWidth<=drawableWidth+padding){
					canvas.drawText(chars,i,1, current_x, current_y, headerTextPaint);
				}
				else{
					current_y+=interval;
					current_x=padding;
					canvas.drawText(chars,i,1, current_x, current_y, headerTextPaint);
				}
				current_x+=currentTextWidth;
			}
			else{
				currentTextWidth=smallTextPaint.measureText(chars,i,1);
				if(current_x+currentTextWidth<=drawableWidth+padding){
					canvas.drawText(chars, i, 1, current_x, current_y, smallTextPaint);
				}
				else{
					current_y+=interval;
					current_x=padding;
					canvas.drawText(chars,i,1, current_x, current_y, smallTextPaint);
				}
				current_x+=currentTextWidth;
			}
		}
	}
	
	/**
	 * to draw some characters in highlighted form, in which case they will be 
	 * drawn in red. 
	 * @param chars the characters to be drawn
	 * @param canvas the canvas to draw the characters on
	 * @param drawableWidth the available width to draw the chars on the canvas
	 */
	private void drawHighlightChars(char[] chars,Canvas canvas,int drawableWidth){
		String key=FileBuffer.getCurrentKey();
		char[] kc =key.toCharArray();
		int    cursor=0;
		int    length=kc.length;
		ArrayList<MyChar> cList=new ArrayList<MyChar>();
		
		for(int i=0;i<chars.length;i++){
			float currentTextWidth=0;
			if(chars[i]==kc[cursor]){
				if(i==0){
					currentTextWidth=headerTextPaint.measureText(chars, 0, 1);
					cList.add(new MyChar(chars[i],current_x,current_y,Color.BLACK,Constants.FIRST_CHAR));
					current_x+=currentTextWidth;
					}
				else{
					currentTextWidth=smallTextPaint.measureText(chars, 0, 1);
					if(current_x+currentTextWidth>drawableWidth+padding){
						current_x=padding;
						current_y+=interval;
					}
					cList.add(new MyChar(chars[i],current_x,current_y,Color.BLACK,Constants.NON_F_CHAR));
					current_x+=currentTextWidth;
					}
				if(++cursor==length){
					for(int j=cList.size()-1;j>cList.size()-length-1;j--){
						cList.get(j).setColor(Color.RED);
					}
					cursor=0;
				}
				continue;
			}
			else{
				cursor=0;
				if(i==0){
					currentTextWidth=headerTextPaint.measureText(chars, 0, 1);
					if(current_x+currentTextWidth<=drawableWidth+padding){
						canvas.drawText(chars,i,1, current_x, current_y,headerTextPaint);
					}
					else{
						current_y+=interval;
						current_x=padding;
						canvas.drawText(chars,i,1, current_x, current_y, headerTextPaint);
					}
					current_x+=currentTextWidth;
				}
				else{
					currentTextWidth=smallTextPaint.measureText(chars,i,1);
					if(current_x+currentTextWidth<=drawableWidth+padding){
						canvas.drawText(chars, i, 1, current_x, current_y, smallTextPaint);
					}
					else{
						current_y+=interval;
						current_x=padding;
						canvas.drawText(chars,i,1, current_x, current_y, smallTextPaint);
					}
					current_x+=currentTextWidth;
				}
			}
		}
		
		for(int i=0;i<cList.size();i++){
			switch(cList.get(i).getType()){
			case Constants.NON_F_CHAR:{
				smallTextPaint.setColor(cList.get(i).getColor()==Color.BLACK?Color.BLACK:Color.RED);
				canvas.drawText(""+cList.get(i).getChar(), cList.get(i).getX(), cList.get(i).getY(), smallTextPaint);
				break;
			}
			case Constants.FIRST_CHAR:{
				
				headerTextPaint.setColor(cList.get(i).getColor()==Color.BLACK?Color.BLACK:Color.RED);
				canvas.drawText(""+cList.get(i).getChar(), cList.get(i).getX(), cList.get(i).getY(), headerTextPaint);
				break;
			}
			}
		}
		headerTextPaint.setColor(Color.BLACK);
		smallTextPaint.setColor(Color.BLACK);
	}
	
	/**
	 * read the file "sentences_zh" from assets to load the sentences
	 */
	@SuppressWarnings("unchecked")
	private void readFile(){
		
		InputStream in = null;
		try {
			in = getResources().getAssets().open("sentences_zh");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int length = 0;
		try {
			length = in.available();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] bytes=new byte[length];
		try {
			in.read(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] lines=EncodingUtils.getString(bytes, ENCODING).split("\n");
		
		String currentName="";
		
		for(int i=0;i<lines.length;i++){
			String temp=lines[i];
			System.out.println(temp);
			if(temp!=""){
				if(temp.startsWith("+")){
					temp=temp.replace("+", "");
					currentName=temp;
					if(!sentences.containsKey(temp)){
						ArrayList<String> list=new ArrayList<String>();
						sentences.put(temp, list);
						names.add(temp);
					}
				}else{
					sentences.get(currentName).add(temp);
				}
			}
			}
		sortedNames=new String[10];
		sortedNames=names.toArray(sortedNames);
		@SuppressWarnings("rawtypes")
		Comparator cmp=Collator.getInstance(Locale.CHINA);
		Arrays.sort(sortedNames, cmp);
		
		FileBuffer.setSentences(sentences);
		FileBuffer.setNames(sortedNames);
	}
	
	public void setNewSentence(String str){
		this.sentenceToShow=str;
		this.invalidate();
	}
	
	/**
	 * show the sentence previous to the shown one
	 * @return the name of the movie that the previous sentence is from
	 */
	public String previous(){
		if(currentIndex==0){
			if(currentNameIndex==0){
				currentNameIndex=sortedNames.length-1;
			}
			else
				currentNameIndex=(currentNameIndex-1)%sortedNames.length;
			currentList=sentences.get(sortedNames[currentNameIndex]);
			currentIndex=currentList.size()-1;
			sentenceToShow=currentList.get(currentIndex);
		}
		else
			sentenceToShow=currentList.get(--currentIndex);
		
		this.invalidate();
		
		return sortedNames[currentNameIndex];
	}
	
	/**
	 * show the sentence next to the shown one
	 * @return the name of the movie that the next sentence is from
	 */
	public String next(){
		if(currentIndex==currentList.size()-1){
			currentNameIndex=(currentNameIndex+1)%sortedNames.length;
			currentList=sentences.get(sortedNames[currentNameIndex]);
			currentIndex=0;
			sentenceToShow=currentList.get(currentIndex);
		}
		else
			sentenceToShow=currentList.get(++currentIndex);
		
		this.invalidate();
		
		return sortedNames[currentNameIndex];
	}
	
	public String getCurrentName(){
		return sortedNames[currentNameIndex];
	}
	
	public String getCurrentSentence(){
		return currentList.get(currentIndex);
	}
	
	public String[] getNames(){
		return sortedNames;
	}
	
	public int getNameIndex(){
		return this.currentNameIndex;
	}
	
	public int getIndex(){
		return this.currentIndex;
	}
	
	public void setCurrentMovie(String name){
		currentList=sentences.get(name);
		currentIndex=0;
		for(int i=0;i<sortedNames.length;i++){
			if(name.equals(sortedNames[i])){
				currentNameIndex=i;
				break;
			}
		}
		sentenceToShow=currentList.get(currentIndex);
		this.invalidate();
	}
	
	public void setCurrentArgs(int nameIndex,int index){
		this.currentNameIndex=nameIndex;
		this.currentIndex=index;
		this.currentList=sentences.get(sortedNames[nameIndex]);
		this.sentenceToShow=currentList.get(this.currentIndex);
		this.invalidate();
	}
	
	public void setCurrentArgs(int nameIndex,int index,Hashtable<String,ArrayList<String>> ss,String[] nams){
		this.currentNameIndex=nameIndex;
		this.currentIndex=index;
		this.sentences=ss;
		this.sortedNames=nams;
		this.currentList=sentences.get(sortedNames[nameIndex]);
		this.sentenceToShow=currentList.get(this.currentIndex);
		this.invalidate();
	}
	
	@SuppressWarnings("unchecked")
	public boolean find(String key,Context context){
		this.searchModel=true;
		FileBuffer.setCurrentKey(key);
		ArrayList<Sentence> result=new ArrayList<Sentence>();
		 
		for(int i=0;i<sortedNames.length;i++){
			ArrayList<String> temp=sentences.get(sortedNames[i]);
			if(sortedNames[i].contains(key)){
				for(int j=0;j<temp.size();j++){
					result.add(new Sentence(sortedNames[i],temp.get(j)));
				}
				continue;
			}
			else{
				for(int j=0;j<temp.size();j++){
					if(temp.get(j).contains(key))
					result.add(new Sentence(sortedNames[i],temp.get(j)));
				}
			}
		}
		
		if(result.size()>0){
			Hashtable<String,ArrayList<String>> newTable=new Hashtable<String,ArrayList<String>>();
			ArrayList<String> tempNames=new ArrayList<String>();
			for(int i=0;i<result.size();i++){
				if(!newTable.containsKey(result.get(i).getName())){
					ArrayList<String> ss=new ArrayList<String>();
					newTable.put(result.get(i).getName(), ss);
					tempNames.add(result.get(i).getName());
				}
				newTable.get(result.get(i).getName()).add(result.get(i).getContent());
			}
			String [] newNames=new String[tempNames.size()];
			newNames=tempNames.toArray(newNames);
			@SuppressWarnings("rawtypes")
			Comparator cmp=Collator.getInstance(Locale.CHINA);
			Arrays.sort(newNames, cmp);
			
			sentences=newTable;
			sortedNames=newNames;
			
			saveState();
			this.currentIndex=0;
			this.currentNameIndex=0;
			this.sentenceToShow=sentences.get(sortedNames[currentNameIndex]).get(currentIndex);
			this.invalidate();
			return true;
		}
		
		Toast.makeText(context,context.getResources().getString(R.string.KEY_NOT_FOUND), Toast.LENGTH_LONG).show();
		return false;
	}
	
	public void saveState(){
		currentIndexCopy=currentIndex;
		currentNameIndexCopy=currentNameIndex;
	}
	
	public void restoreState(){
		this.searchModel=false;
		currentIndex=currentIndexCopy;
		currentNameIndex=currentNameIndexCopy;
		this.sentences=FileBuffer.getSentences();
		this.sortedNames=FileBuffer.getNames();
		this.sentenceToShow=sentences.get(sortedNames[currentNameIndex]).get(currentIndex);
		this.invalidate();
	}
	
	public boolean isSearchModel(){
		return this.searchModel;
	}
	
	public Hashtable<String,ArrayList<String>> getCurrentTable(){
		return this.sentences;
	}
	
	public String[] getCurrentNames(){
		return this.sortedNames;
	}
	
	public void setSearchModel(){
		this.searchModel=true;
	}
}
