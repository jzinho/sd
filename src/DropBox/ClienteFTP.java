package DropBox;

import Entradas.Entradas;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class ClienteFTP {

	private Socket controle, dados;
	private InputStream isCont, isDados;
	private OutputStream osContr, osDados;
	static int tabulador = 0;

	public ClienteFTP(String host, int port, String login, String senha)
			throws UnknownHostException, IOException {
		this.connect(host, port);
		this.login(login, senha);
	}

	private String getControlResp() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				this.isCont));
		String resp = br.readLine();
		System.out.println(resp);
		return resp;
	}

	public void pwd() throws IOException {
		String msg = "PWD\r\n";
		this.osContr.write(msg.getBytes());
		this.getControlResp();

	}

	public ArrayList<String> list(String path) throws IOException {
		ArrayList<String> resposta = new ArrayList<String>();
		this.PASV();
		String msg = "LIST " + path + "\r\n";
		this.osContr.write(msg.getBytes());
		this.getControlResp();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				this.isDados));
		String resp = "";
		String line;
		while ((line = br.readLine()) != null) {
			resp += "\n" + line;
			resposta.add(line);
		}
		this.getControlResp();
		return resposta;
	}

	public StringBuilder LIST(String path) throws IOException {

		PASV();

		changeType("A");
		String msg = "LIST " + path + "\r\n";
		this.osContr.write(msg.getBytes());

		BufferedInputStream buf = new BufferedInputStream(
				dados.getInputStream());
		StringBuilder files = new StringBuilder();
		byte[] buffer = new byte[4096];
		int bytesRead = 0;

		while ((bytesRead = buf.read(buffer)) != -1) {
			String piece = new String(buffer);
			files.append(piece);
		}

		buf.close();
		String resposta = this.getControlResp();
		return files;
	}

	public ArrayList<String> nlst(String path) throws IOException {
		ArrayList<String> resposta = new ArrayList<String>();
		this.PASV();
		String msg = "NLST " + path + "\r\n";
		this.osContr.write(msg.getBytes());
		this.getControlResp();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				this.isDados));
		String resp = "";
		String line;
		while ((line = br.readLine()) != null) {
			resp += "\n" + line;
			resposta.add(line);
		}
		this.getControlResp();
		return resposta;
	}
        public long mdtm2(String path) throws UnknownHostException, IOException {
        String msg = "MDTM " + path + "\r\n";
        
        this.osContr.write(msg.getBytes());
        String resp = getControlResp();
       long x = Long.parseLong(getControlResp());
      
        return x;
    }

	private void PASV() throws UnknownHostException, IOException {
		String msg = "PASV\r\n";
		this.osContr.write(msg.getBytes());
		String resp = getControlResp();

		StringTokenizer st = new StringTokenizer(resp);
		st.nextToken("(");
		String ip = st.nextToken(",").substring(1) + "." + st.nextToken(",")
				+ "." + st.nextToken(",") + "." + st.nextToken(",");
		int value1 = Integer.parseInt(st.nextToken(","));
		int value2 = Integer.parseInt(st.nextToken(")").substring(1));
		int port = value1 * 256 + value2;

		this.dados = new Socket(ip, port);
		this.isDados = dados.getInputStream();
		this.osDados = dados.getOutputStream();
	}

	public void mkd(String nome) throws UnknownHostException, IOException {
		String msg = "MKD " + nome + "\r\n";
		this.osContr.write(msg.getBytes());
		this.getControlResp();
	}

	public boolean stor(File file) throws IOException {
		String nome = file.getName();
		return stor(new FileInputStream(file), nome);
	}

	public boolean stor(InputStream is, String nome) throws IOException {

		BufferedInputStream buf = new BufferedInputStream(is);
		PASV();

		String msg = "STOR " + nome + "\r\n";
		this.osContr.write(msg.getBytes());

		String resposta = this.getControlResp();

		BufferedOutputStream output = new BufferedOutputStream(
				dados.getOutputStream());
		byte[] buffer = new byte[4096];
		int bytesRead = 0;

		while ((bytesRead = buf.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
		output.flush();
		output.close();
		buf.close();

		resposta = this.getControlResp();
		return resposta.startsWith("226 ");
	}

	public void deleteFile(String nome) throws UnknownHostException,
			IOException {
		String msg = "DELE " + nome + "\r\n";
		this.osContr.write(msg.getBytes());
		this.getControlResp();
	}

	public boolean cwd(String path) throws UnknownHostException, IOException {
		String msg = "CWD " + path + "\r\n";
		this.osContr.write(msg.getBytes());
		String resposta = this.getControlResp();
		return (resposta.startsWith("250 "));
	}

	public String mdtm(String path) throws UnknownHostException, IOException {
		String msg = "MDTM " + path + "\r\n";

		this.osContr.write(msg.getBytes());

		String resp = getControlResp();
		return resp;
	}

	public long getTimeInMillis(String ret) throws NumberFormatException {
		int year = Integer.parseInt(ret.substring(0, 4));
		int month = Integer.parseInt(ret.substring(4, 6));
		int day = Integer.parseInt(ret.substring(6, 8));
		int hrs = Integer.parseInt(ret.substring(8, 10));
		int min = Integer.parseInt(ret.substring(10, 12));
		int sec = Integer.parseInt(ret.substring(12, 14));
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, day, hrs, min, sec);
		c.add(Calendar.HOUR_OF_DAY, -3);
		return c.getTimeInMillis();
	}

	public boolean rmd(String path) throws UnknownHostException, IOException {
		String msg = "RMD " + path + "\r\n";
		this.osContr.write(msg.getBytes());
		String resposta = this.getControlResp();
		return resposta.startsWith("250 ");
	}

	public void connect(String host, int port) throws UnknownHostException,
			IOException {
		this.controle = new Socket(host, port);
		this.isCont = controle.getInputStream();
		this.osContr = controle.getOutputStream();
		this.getControlResp();
	}

	public void login(String user, String pass) throws IOException {
		String comand = "USER " + user + "\r\n";
		this.osContr.write(comand.getBytes());
		this.getControlResp();
		comand = "PASS " + pass + "\r\n";
		this.osContr.write(comand.getBytes());
		this.getControlResp();
	}

	public void downloadFile(String nome, String diretorio) throws IOException {

		this.PASV();
		this.changeType("I");
		String msg = "RETR " + nome + "\r\n";
		this.osContr.write(msg.getBytes());
		this.getControlResp();
		File file = new File(diretorio + nome);
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buf = new byte[1000];
		int len;
		while ((len = this.isDados.read(buf)) != -1) {
			fos.write(buf, 0, len);
		}
		fos.flush();
		fos.close();
		this.getControlResp();
		System.out.println("Download concluido!");
	}

	public void uploadFile(String nome, String diretorio)
			throws UnknownHostException, IOException {

		this.PASV();
		this.changeType("I");

		String msg = "STOR " + nome + "\r\n";
		this.osContr.write(msg.getBytes());
		this.getControlResp();
		File file = new File(diretorio + nome);
		FileInputStream fis = new FileInputStream(file);
		byte[] buf = new byte[1000];
		int len;
		while ((len = fis.read(buf)) > 0) {
			osDados.write(buf, 0, len);
		}

		this.osDados.flush();
		this.osDados.close();
		fis.close();
		this.getControlResp();
		System.out.println("Upload concluido!");
	}

	public void changeType(String type) throws IOException {

		String msg = "TYPE " + type + "\r\n";
		this.osContr.write(msg.getBytes());
		this.getControlResp();

	}

	public void lastModifiedDirectory(File diretorio) throws ParseException {
		Date lastModified = new Date(diretorio.lastModified());
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String formatada = formatter.format(lastModified);
		System.out.println(formatada);

	}

	public void listLocalFiles(File raiz) {

		tabulador++;
		System.out.println(tabulacao() + "* " + raiz.getName());

		for (File f : raiz.listFiles()) {
			if (f.isFile()) {

				System.out.println(tabulacao() + f.getName());
			} else {
				listLocalFiles(f);
			}
		}
		tabulador--;

	}

	static String tabulacao() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < tabulador; i++) {
			sb.append("\t");
		}

		return sb.toString();
	}

	public void listarRemoto(ClienteFTP x, String diretorio) throws IOException {

		System.out.println(x.list(diretorio));

	}

	public boolean isDirectory(String name) {

		char quebra[] = name.toCharArray();
		if (quebra[0] == 'd') {
			return true;
		}

		return false;

	}

	public void Retrieve(String remotepath, String localpath)
			throws IOException {

		PASV();

		changeType("I");
		String msg = "RETR " + remotepath;
		this.osContr.write(msg.getBytes());
		// resposta = resposta();

		BufferedInputStream buf = new BufferedInputStream(
				dados.getInputStream());

		byte[] buffer = new byte[4096];
		int bytesRead = 0;
		// System.out.println(localpath);
		File f = new File(localpath);
		FileOutputStream fos = new FileOutputStream(f);

		while ((bytesRead = buf.read(buffer)) != -1) {
			fos.write(buffer, 0, bytesRead);
		}
		fos.flush();
		fos.close();
		buf.close();

		String resposta = getControlResp();
	}
}
