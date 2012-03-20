package org.lvxiang.MovieNews.bean;

import android.graphics.Bitmap;

/**
 * 
 * @author lvxiang
 *
 */
public class Movie implements Cloneable{

	private String name;
	private String director;
	private String douban_URL;
	private String IMDB_URL;
	private String actors;
	private String introduction;
	private String img_URL;
	private String douban_rank;
	private String img_html;
	private String info_html;
	private String detail_html;
	private String weeklyBox;
	private String totalBox;
	private String producer;
	private int    id;
	private Bitmap cover;
	private String dir_act_html;
	private String pic_id;
	
	public Movie(){
		
	}
	
	public Movie(String url){
		this.douban_URL=url;
	}
	
	@Override
	public boolean equals(Object movie){
		Movie m=(Movie)movie;
		if(this.douban_URL.equals(m.douban_URL)){
			return true;
		}
		return false;
	}
	
	@Override
	public Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void setID(int id){
		this.id=id;
	}
	public int getID(){
		return this.id;
	}
	public void setMovieName(String name){
		this.name=name;
	}
	public String getMovieName(){
		return this.name;
	}
	public void setDoubanUrl(String url){
		this.douban_URL=url;
	}
	public String getDoubanUrl(){
		return this.douban_URL;
	}
	public void setIMDBUrl(String url){
		this.IMDB_URL=url;
	}
	public String getIMDBUrl(){
		return this.IMDB_URL;
	}
	public void setDiretor(String director){
		this.director=director;
	}
	public String getDirevtor(){
		return this.director;
	}
	public void setActors(String actors){
		this.actors=actors;
	}
	public String getActors(){
		return this.actors;
	}
	public void setIntroduction(String introduction){
		this.introduction=introduction;
	}
	public String getIntroduction(){
		return this.introduction;
	}
	public void setImgUrl(String url){
		this.img_URL=url;
	}
	public String getImgUrl(){
		return this.img_URL;
	}
	public void setDoubanRank(String rank){
		this.douban_rank=rank;
	}
	public String getDoubanRank(){
		return this.douban_rank;
	}
	public void setHtml(String html){
		this.img_html=html;
	}
	public String getHtml(){
		return this.img_html;
	}
	public void setImgHtml(String html){
		this.img_html=html;
	}
	public String getImgHtml(){
		return this.img_html;
	}
	public void setInfoHtml(String html){
		this.info_html=html;
	}
	public String getInfoHtml(){
		return this.info_html;
	}
	public void setDetailHtml(String html){
		this.detail_html=html;
	}
	public String getDetailHtml(){
		return this.detail_html;
	}
	public void setProducer(String producer){
		this.producer=producer;
	}
	public String getProducer(){
		return this.producer;
	}
	public void setWeeklyBox(String weekly){
		this.weeklyBox=weekly;
	}
	public String getWeeklyBox(){
		return this.weeklyBox;
	}
	public void setTotalBox(String total){
		this.totalBox=total;
	}
	public String getTotalBox(){
		return this.totalBox;
	}
	public void setCover(Bitmap cover){
		this.cover=cover;
	}
	public Bitmap getCover(){
		return this.cover;
	}
	public void setDirActHtml(String html){
		this.dir_act_html=html;
	}
	public String getDirActHtml(){
		return this.dir_act_html;
	}
	public void setPicID(String id){
		this.pic_id=id;
	}
	public String getPicID(){
		return this.pic_id;
	}
	
}
