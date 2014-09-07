package Arquivos;

import java.util.Date;

public class Arquivo {

	private String nome;
	private Date date;
	private String path;

	public Arquivo(String nome, long date, String path) {
		this.nome = nome;
		this.date = new Date(date);
		this.path = path;
	}

	public String getnome() {
		return this.nome;
	}

	public Date getdate() {
		return this.date;
	}

	public String getpath() {
		return path;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
