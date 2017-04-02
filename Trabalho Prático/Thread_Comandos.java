/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leilao;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Thread_Comandos implements Runnable {

    private Gestor gestor;
    private PrintWriter out;
    private String comando;
    private String idUti;

    public Thread_Comandos(Gestor g, String id, PrintWriter out, String comando) {
        this.gestor = g;
        this.out = out;
        this.comando = comando;
        this.idUti = id;
    }

    public void run() {
        String lista[] = comando.split(":");
        String msg = "";
        int ie = 0;
        for (String s : lista) {
            lista[ie] = remEspaço(s);
            ie++;
        }

        switch (lista[0]) {
            case "Leilao":
                try {
                    Leilao l = this.gestor.constroi_Leilao(lista, this.idUti);
                    this.gestor.inicia_Leilao(l, this.idUti);
                    msg = "Criado Leilão com o id " + l.getId() + " com o valor inicial de " + l.getValor_inicial() + " €";
                } catch (LeilaoException e) {
                    msg = e.getMessage();
                } catch (IndexOutOfBoundsException e) {
                    msg = "comando errado: faltam argumentos";
                } catch (NumberFormatException e) {
                    msg = "comando errado: argumento não numerico";
                }
                break;

            case "Licitacao":
                try {
                    Licitacao b = this.gestor.constroi_Licitacao(lista, this.idUti);
                  
                    this.gestor.fazLicitacao(b, this.idUti);
                   
                } catch (LicitacaoException e) {
                    msg = e.getMessage();
                } catch (IndexOutOfBoundsException e) {
                    msg = "comando errado: faltam argumentos";
                } catch (NumberFormatException e) {
                    msg = "comando errado: argumento não numerico";
                } catch (InterruptedException ex) {
                    Logger.getLogger(Thread_Comandos.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;

            case "Termina":
                try {
                    this.gestor.termina_Leilao(lista[1], this.idUti);

                } catch (IndexOutOfBoundsException e) {
                    System.out.println("comando errado: faltam argumentos");
                } catch (LeilaoException e) {
                    msg = e.getMessage();
                }
                break;

            case "EmCurso":
                try {
                    msg = this.gestor.listaEmCurso();
                } catch (LeilaoException e) {
                    msg = e.getMessage();
                }
                break;
            case "Terminados":
                try {
                    msg = this.gestor.listaTerminados();
                } catch (LeilaoException e) {
                    msg = e.getMessage();
                }
                break;
            case "MeusLeiloes":
                try {
                    msg = this.gestor.listaMeusLeiloes(idUti);
                } catch (LeilaoException e) {
                    msg = e.getMessage();
                }
                break;
            case "MinhasLicitacoes":
                try {
                    msg = this.gestor.listaMinhasLicitacoes(idUti);
                } catch (LeilaoException e) {
                    msg = e.getMessage();
                }
                break;
            default:
                msg = "comando errado!";
                break;
        }
        //print msg
        out.println(msg);

        out.flush();
    }

    public String remEspaço(String s) {
        int tam = s.length();
        char[] st = s.toCharArray();
        char res[] = new char[tam + 1];
        String msg = "";
        int i, j;
        i = 0;
        j = 0;
        while (j < tam && st[j] != ' ') {
            res[i] = st[j];
            i++;
            j++;
        }

        for (; j < tam; j++) {
            if (i > 0) {
                if (res[i - 1] == ' ' && st[j] == ' '); else if (j == tam - 1 && st[j] == ' '); else {
                    res[i] = st[j];
                    i++;
                }
            } else {
                if (st[j] != ' ') {
                    res[i] = st[j];
                    i++;
                }
            }
        }
        if (res[i - 1] == ' ') {
            i -= 1;
        }

        tam = i;
        for (i = 0; i < tam; i++) {
            msg = msg + res[i];
        }
        return msg;
    }

}
