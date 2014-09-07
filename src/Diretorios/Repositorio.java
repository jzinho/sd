package Diretorios;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

import Arquivos.Arquivo;
import Arquivos.Pasta;
import DropBox.ClienteFTP;

public class Repositorio {
	
	ClienteFTP cliente;
	
	public Repositorio(ClienteFTP cli){
		this.cliente = cli;
	}
	

	private ArrayList<Arquivo> arquivosLocal;
	private ArrayList<Pasta> pastasLocal;

	private ArrayList<Arquivo> arquivosRemoto;
	private ArrayList<Pasta> pastasRemoto;

	public void listarLocal(String path) {

		arquivosLocal = new ArrayList<Arquivo>();
		pastasLocal = new ArrayList<Pasta>();
		File directory = new File(path);

		File[] listaDeArquivos = directory.listFiles();

		if (listaDeArquivos != null) {
			for (File file : listaDeArquivos) {
				if (file.isFile()) {
					Arquivo arquivo = new Arquivo(file.getName(),
							file.lastModified(), file.getAbsolutePath());
					arquivosLocal.add(arquivo);
				} else {
                                  
					Pasta pasta = new Pasta(file.getName(),
							file.lastModified(), file.getAbsolutePath());
					pastasLocal.add(pasta);
				}
			}
		}
	}
	
	public void listarRemotos(String path, ClienteFTP c) throws IOException {
            c.cwd(path);
     int tamDentroPasta= c.list(path).size();
    //  System.out.println(tamDentroPasta);
       System.out.println(c.list(path).get(0));
          System.out.println(c.list(path).get(1));
      
		arquivosRemoto = new ArrayList<Arquivo>();
		pastasRemoto = new ArrayList<Pasta>();
              for(int i=0; i<tamDentroPasta; i++){
                  
                //  long dataMod = c.mdtm2(path);
                  char[] letra = c.list(path).get(i).toCharArray();
                  String aux =c.nlst(path).get(i);
                  String nomeArq = aux.substring(aux.lastIndexOf("/") + 1);
                  if(letra[0] =='d'){
                      Pasta pasta = new Pasta(nomeArq,
							8, c.nlst(path).get(i));
					pastasRemoto.add(pasta);
                  }
                  else{
                      if(letra[0] =='-'){
                      Arquivo arquivo = new Arquivo(nomeArq,
							8, c.nlst(path).get(i));
					arquivosRemoto.add(arquivo);
                      }
                  }
                  
              } 
              
               
		
	}
	

	public void listarArquivosRemotos(StringBuilder listarquivosRemoto) {
		int i = 0;
		boolean flag = false;
		arquivosRemoto = new ArrayList<Arquivo>();
		pastasRemoto = new ArrayList<Pasta>();
		while (i < listarquivosRemoto.length()) {
			char atual = listarquivosRemoto.charAt(i);
			if (atual == '-') {
				addArquivoRemoto(i, listarquivosRemoto);
				flag = true;
			}
			if (atual == 'd') {
				addPastaRemota(i, listarquivosRemoto);
				flag = true;
			}
			if (flag == true) {
				while (listarquivosRemoto.charAt(i) != '\n') {
					i++;
				}
				flag = false;
			} else {
				i++;
			}
		}
	}
	
	public void getRemoteTree(String arg, int nivel) throws UnknownHostException, IOException {
		
		arquivosRemoto = new ArrayList<Arquivo>();
		pastasRemoto = new ArrayList<Pasta>();
		ArrayList<String> arr = cliente.nlst(".");

		if (arr == null) {
			cliente.cwd("..");
			return;
		}

		/*if (pastasRemoto.size() <= nivel) {
			pastasRemoto.add(new TreeSet<String>());
		}*/
		Pasta pasta = new Pasta(arg, 0, arg); //REVER ISSO AQUI
		pastasRemoto.add(pasta);

		for (String s : arr) {

			if (cliente.cwd(s) == true) {
				getRemoteTree(arg + "/" + s, nivel + 1);
			} else {
				Arquivo arquivo = new Arquivo(s, 0, arg+"/"+s);
				arquivosRemoto.add(arquivo);
			}

		}

		cliente.cwd("..");
	}

	private String filename(int i, StringBuilder nome) {
		int index = i;
		while (nome.charAt(index) != ':') {
			index++;
		}
		index += 4;

		int end = index;
		while (nome.charAt(end) != '\n') {
			end++;
		}
		return nome.substring(index, end - 1);
	}

	private void addArquivoRemoto(int i, StringBuilder nome) {
		Arquivo arq = new Arquivo(filename(i, nome), 0, null);
		arquivosRemoto.add(arq);

	}

	private void addPastaRemota(int i, StringBuilder nome) {
		Pasta fld = new Pasta(filename(i, nome), 0, null);
		pastasRemoto.add(fld);
	}

	public ArrayList<Arquivo> getArquivosRemotos() {
		return arquivosRemoto;
	}

	public ArrayList<Pasta> getPastasRemotas() {
		return pastasRemoto;
	}

	public ArrayList<Arquivo> getListaArquivos() {
		return arquivosLocal;
	}

	public ArrayList<Pasta> getListaPastas() {
		return pastasLocal;
	}
}
