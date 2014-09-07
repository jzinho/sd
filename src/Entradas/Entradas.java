package Entradas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Entradas {

	private String diretorioLocal;

	private String host;
	private String usuario;
	private String senha;

	private int porta;
	private int intervalo;
	private String tipoArquivo;
	private String ip;
	private boolean on;
	private boolean conexao;
	Socket sContr;
	Socket sData;
	InputStream isContr;
	OutputStream osContr;
	BufferedReader brContr;
	String resp;
	private String diretorioRemoto;

	public void entradasTxt() throws FileNotFoundException, IOException {
		File f = new File("entradas.txt");
		InputStream is = new FileInputStream(f);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String hostTxt = br.readLine();
		String porta2 = br.readLine();
		int portaTxt = Integer.parseInt(porta2);
		int intervaloTxt = Integer.parseInt(br.readLine());
		String usuarioTxt = br.readLine();
		String senhaTxt = br.readLine();
		String dirLocal = br.readLine();
		String dirRemoto = br.readLine();
		String nomeDiretorio = dirLocal;
		String[] arrayS = nomeDiretorio.split("\\\\");
		String nomeArquivo = arrayS[arrayS.length - 1];

		this.setDiretorioLocal(dirLocal);
		this.setDiretorioRemoto(dirRemoto);
		this.setIntervalo(intervaloTxt);
		this.setUsuario(usuarioTxt);
		this.setPorta(portaTxt);
		this.setHost(hostTxt);
		this.setUsuario(usuarioTxt);
		this.setSenha(senhaTxt);
		this.setTipoArquivo(tipoArquivo);

	}

	public String getDiretorioLocal() {

		return diretorioLocal;

	}

	public String getDiretorioRemoto() {

		return diretorioRemoto;

	}

	public String getHost() {
		return host;

	}

	public String getUsuario() {
		return usuario;

	}

	public int getPorta() {
		return porta;

	}

	public String getSenha() {
		return senha;
	}

	public int getIntervalo() {
		return intervalo;
	}

	public String getTipoArquivo() {
		return tipoArquivo;
	}

	public String getIp() {
		return ip;
	}

	public boolean checarLogin() {
		return on;
	}

	public boolean checarSeConectado() {
		return conexao;
	}

	// Setters

	public void setDiretorioLocal(String diretorioLocal) {
		this.diretorioLocal = diretorioLocal;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setDiretorioRemoto(String diretorioRemoto) {
		this.diretorioRemoto = diretorioRemoto;
	}

	public void setPorta(int porta) {
		this.porta = porta;

	}

	public void setIntervalo(int intervalo) {
		this.intervalo = intervalo;
	}

	public void setTipoArquivo(String tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setIp(String ip) {
		this.ip = ip;

	}

}
