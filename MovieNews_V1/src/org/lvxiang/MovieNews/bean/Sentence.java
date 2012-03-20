package org.lvxiang.MovieNews.bean;

public class Sentence {

	private String name;
	private String content;
	
	public Sentence(String name,String content){
		this.name=name;
		this.content=content;
	}
	
	public void setName(String str){
		this.name=str;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setContent(String cont){
		this.content=cont;
	}
	
	public String getContent(){
		return this.content;
	}
	
	@Override
	public boolean equals(Object obj){
		return ((Sentence)obj).getName()==name&&((Sentence)obj).getContent()==content;
	}
	
}
