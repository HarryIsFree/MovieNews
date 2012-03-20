package org.lvxiang.MovieNews.bean;


public class MyChar {

	private float x;
	private float y;
	private char  theChar;
	private int color;
	private int type;
	
	public MyChar(char c,float x,float y,int co,int type){
		this.theChar=c;
		this.x=x;
		this.y=y;
		this.color=co;
		this.type=type;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public char getChar(){
		return theChar;
	}
	
	public int getColor(){
		return this.color;
	}
	
	public void setColor(int c){
		this.color=c;
	}
	
	public int getType(){
		return this.type;
	}
}
