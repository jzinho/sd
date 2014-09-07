package DropBox.Main;

import java.io.FileNotFoundException;
import java.io.IOException;

import Arquivos.Arquivo;
import Arquivos.Pasta;
import Diretorios.Repositorio;
import DropBox.ClienteFTP;
import DropBox.Espelhamento;
import Entradas.Entradas;

public class Main {

	public static void main(String[] args)  {

		try {
			Entradas e = new Entradas();
			e.entradasTxt();

			ClienteFTP clienteFTP = new ClienteFTP(e.getHost(), e.getPorta(),
					e.getUsuario(), e.getSenha());
			
			Repositorio repositorio = new Repositorio(clienteFTP);
			repositorio.listarLocal(e.getDiretorioLocal());
			//repositorio.listarRemotos(e.getDiretorioRemoto(),clienteFTP);
						
			while (true) {
				Espelhamento mirror = new Espelhamento();
				mirror.espelhar(e.getDiretorioLocal(), e.getDiretorioRemoto(),
						clienteFTP);
                      
				try {
					Thread.sleep(e.getIntervalo() * 100000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}
