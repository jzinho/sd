package Arquivos; 

import java.util.Date;

public class Pasta {
	private String nome;	
	private Date date;
	private String path;

	public Pasta(String nome, long date, String path){
	//	int i = nome.lastIndexOf("/");
	//	nome.substring(0, i);
//		i = nome.lastIndexOf("/");
	//	this.nome = nome.substring(i);
	this.nome = nome;	
		this.date = new Date(date);
		this.path = path;
	}
	
	public String getnome(){
		return this.nome;
	}
	
	public Date getdate(){
		return this.date;
	}
	
	public String getpath(){
		return path;
	}
}
