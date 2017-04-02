/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leilao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Thread_Cliente implements Runnable {

    private Gestor gestor;
    private final Socket mySocket;
    Utilizador user;
    PrintWriter out;
    BufferedReader in;

    //Cria a thread
    public Thread_Cliente(Gestor g, Socket s) {
        this.gestor = g;
        this.mySocket = s;

    }

    public void run() {
        try {
            this.out = new PrintWriter(mySocket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            String comando;
            String l;
            boolean continua = true;
            while (continua && ((l = in.readLine()) != null)) {
                String parse[] = l.split(":");
                if (parse[0].equals("Login") && (parse.length >= 3)) {
                    Utilizador j = this.gestor.getUtilizador(parse[1]);
                    if (j != null) { 
                        Utilizador u = j;
                        if (!u.getLog()) { //verifica se já esta autenticado
                            if (u.validaPass(parse[2])) { // valida pass              
                                if (u.login()) {
                                    out.println("Login efectuado com Sucesso!!");
                                    this.user = u;
                                    out.flush();
                                    continua = false;
                                     if(this.gestor.getCompradores().containsValue(u))
                                    try{
                                        out.println("\n" + this.gestor.listaEmCurso());
                                        out.flush();
                                    }catch (LeilaoException e){
                                    out.println(e.getMessage());
                                    out.flush();
                                            }
                                } else {
                                    out.println("Utilizador já de encontra autenticado!!");
                                    out.flush();
                                }
                            } else {
                                out.println("Password Errada!!");
                                out.flush();
                            }
                        } else {
                            out.println("Utilizador já de encontra autenticado!!");
                            out.flush();
                        }
                    } else {
                        out.println("Utilizador não existe!!");
                        out.flush();
                    }
                } else {
                    out.println("Comando errado!!");
                    out.flush();
                }
            }

            //le o comando do Cliente e cria Thread 
            while ((comando = in.readLine()) != null) {
                Thread cm = new Thread(new Thread_Comandos(this.gestor, this.user.getNome(), out, comando));
                cm.start();
            }

            this.user.logout();
            this.mySocket.shutdownInput();
            this.mySocket.shutdownOutput();
            this.mySocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Thread_Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
