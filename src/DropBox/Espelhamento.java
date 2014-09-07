package DropBox;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import Arquivos.Arquivo;
import Arquivos.Pasta;
import Diretorios.Repositorio;

public class Espelhamento {

    public void espelhar(String path, String remote_path, ClienteFTP clienteFTP)
            throws IOException {
//Criação do repositório local e Remoto
        Repositorio repositorioLocal = new Repositorio(clienteFTP);
        Repositorio repositorioRemoto = new Repositorio(clienteFTP);
//Lista as pastas e arquivos Locais da pasta passada
        repositorioLocal.listarLocal(path);
//ArrayList auxiliar para "LOCAL"
        ArrayList<Arquivo> listaArquivosLocais = repositorioLocal.getListaArquivos();
        ArrayList<Pasta> listaPastasLocais = repositorioLocal.getListaPastas();

        if (!(clienteFTP.cwd(remote_path))) {
            clienteFTP.mkd(remote_path);
            clienteFTP.cwd(remote_path);
        }

        StringBuilder remotefiles = clienteFTP.LIST(remote_path);
        repositorioRemoto.listarArquivosRemotos(remotefiles);
//		repositorio.getRemoteTree(remote_path, 0);

        ArrayList<Arquivo> listaArquivosRemoto = repositorioRemoto
                .getArquivosRemotos();
        ArrayList<Pasta> listaPastasRemota = repositorioRemoto.getPastasRemotas();
//Começa a inserir os arquivos
        sincronizarArquivosLocalParaRemoto(listaArquivosLocais, listaArquivosRemoto,
                clienteFTP);

        if (listaPastasLocais != null) {
            for (Pasta f : listaPastasLocais) {
                espelhar(path + "/" + f.getnome(),
                        remote_path + "/" + f.getnome(), clienteFTP);
            }

            for (Pasta f : listaPastasLocais) {
                for (int i = 0; i < listaPastasRemota.size(); i++) {
                    Pasta remote = listaPastasRemota.get(i);
                    if (f.getnome().equals(remote.getnome())) {
                        listaPastasRemota.remove(i);
                    }
                }
            }
        }

        if (listaPastasRemota != null) {
            deletefolders(listaPastasRemota, clienteFTP, remote_path);
        }

    }

    public void espelharRemoto(String path, String remote_path, ClienteFTP clienteFTP)
            throws IOException {

        Repositorio repositorio = new Repositorio(clienteFTP);

        repositorio.listarLocal(path);

        ArrayList<Arquivo> listaArquivos = repositorio.getListaArquivos();
        ArrayList<Pasta> listaPastas = repositorio.getListaPastas();

        if (!(clienteFTP.cwd(remote_path))) {
            clienteFTP.mkd(remote_path);
            clienteFTP.cwd(remote_path);
        }

        StringBuilder remotefiles = clienteFTP.LIST(remote_path);
        repositorio.listarArquivosRemotos(remotefiles);
//		repositorio.getRemoteTree(remote_path, 0);

        ArrayList<Arquivo> listaArquivosRemoto = repositorio
                .getArquivosRemotos();
        ArrayList<Pasta> listaPastasRemota = repositorio.getPastasRemotas();

        //REMOTO PARA LOCAL
        sincronizarArquivosLocalParaRemoto(listaArquivos, listaArquivosRemoto,
                clienteFTP);

        if (listaPastas != null) {
            for (Pasta f : listaPastas) {
                espelhar(path + "/" + f.getnome(),
                        remote_path + "/" + f.getnome(), clienteFTP);
            }

            for (Pasta f : listaPastas) {
                for (int i = 0; i < listaPastasRemota.size(); i++) {
                    Pasta remote = listaPastasRemota.get(i);
                    if (f.getnome().equals(remote.getnome())) {
                        listaPastasRemota.remove(i);
                    }
                }
            }
        }

        if (listaPastasRemota != null) {
            deletefolders(listaPastasRemota, clienteFTP, remote_path);
        }

    }

    private void deletefolders(ArrayList<Pasta> remotelist,
            ClienteFTP clienteFTP, String remote_path) throws IOException {
        if (!(remotelist.isEmpty())) {
            for (Pasta r : remotelist) {
                String npath = remote_path + "/" + r.getnome();
                boolean delete = clienteFTP.rmd(npath);
                if (!(delete)) {
                    clienteFTP.cwd(npath);
                    Repositorio repositorio = new Repositorio(clienteFTP);
                    StringBuilder remotefiles = clienteFTP.LIST(npath);
                    repositorio.listarArquivosRemotos(remotefiles);
                    ArrayList<Arquivo> remotelista = repositorio
                            .getArquivosRemotos();
                    if (remotelista != null) {
                        for (Arquivo a : remotelista) {
                            clienteFTP.deleteFile(a.getnome());
                        }
                    }
                    ArrayList<Pasta> remotelistf = repositorio
                            .getPastasRemotas();
                    if (remotelistf != null) {
                        deletefolders(remotelistf, clienteFTP, npath);
                    }
                    clienteFTP.rmd(npath);
                }
            }
        }
    }

    private void remotetime(ArrayList<Arquivo> list, ClienteFTP clienteFTP)
            throws IOException {
        for (Arquivo a : list) {
            String x = clienteFTP.mdtm(a.getnome());
            x = x.substring(4, x.length());

            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                a.setDate(sd.parse(x));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void sincronizarArquivosLocalParaRemoto(ArrayList<Arquivo> listLocal,
            ArrayList<Arquivo> listRemote, ClienteFTP clienteFTP)
            throws IOException {
        boolean flag = false;
        File file;
        if (listLocal != null) // se a lista de arquivos locais nao estiver vazia
        {
            for (Arquivo local : listLocal) {
                flag = false;
                if (listRemote != null) // se a lista de arquivos remotos nao
                // tiver vazia
                {
                    for (Arquivo remote : listRemote) {

                        if (local.getnome().equals(remote.getnome())) { // se o
                            // nome
                            // do
                            // arquivo
                            // local
                            // for
                            // igual
                            // ao
                            // remoto
                            flag = true;
                            if (local.getdate().compareTo(remote.getdate()) > 0) {
                                // local mais atualizado
                                clienteFTP.deleteFile(remote.getnome());
                                file = new File(local.getpath());
                                clienteFTP.stor(file);
                            } else if (local.getdate().compareTo(
                                    remote.getdate()) < 0) {
                                // remoto mais atualizado
                                file = new File(local.getpath());
                                clienteFTP.Retrieve(remote.getnome(),
                                        local.getpath());

                            } else if (local.getdate().compareTo(
                                    remote.getdate()) == 0) {
                            } else {
                            }
                        }
                    }
                }
                if (flag == false) {
                    file = new File(local.getpath());
                    clienteFTP.stor(file);
                }

            }
        }

        if (!(listLocal.isEmpty())) {
            for (Arquivo local : listLocal) {
                for (int i = 0; i < listRemote.size(); i++) {
                    Arquivo remote = listRemote.get(i);
                    if (local.getnome().equals(remote.getnome())) {
                        listRemote.remove(i);
                    }
                }
            }
        }

        if (!(listRemote.isEmpty())) {
            for (Arquivo remote : listRemote) {
                clienteFTP.deleteFile(remote.getnome());
            }
        }
    }

    private void sincronizarArquivosRemotoParaLocal(ArrayList<Arquivo> list,
            ArrayList<Arquivo> listremote, ClienteFTP clienteFTP)
            throws IOException {

        for (Arquivo arquivo : listremote) {

            if (!(list.contains(arquivo))) {
                System.out.println(arquivo.getnome());
                int i = arquivo.getpath().lastIndexOf("/");
                String path = arquivo.getpath().substring(0, i);
                path = "/local" + path.substring(7);

                clienteFTP.Retrieve(arquivo.getpath(), path);
            }
        }

    }
}
