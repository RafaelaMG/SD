/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leilao;

import java.io.PrintWriter;
import static java.lang.Thread.MIN_PRIORITY;
import java.net.ServerSocket;

import java.util.Scanner;


public class Thread_Consola extends Thread {

    private Gestor gestor;
    boolean exit;
    Utilizador user;
    ServerSocket ss;

    public Thread_Consola(Gestor g, boolean exit, ServerSocket ss) {
        this.gestor = g;
        this.exit = exit;
        this.ss = ss;
    }

    public boolean login() {
        boolean continua = true;
        Scanner in = new Scanner(System.in);
        String l = "";
        System.out.println("###EFECTUE LOGIN PARA USAR A CONSOLA DE COMANDOS###");
        while (continua && (!"Sair".equals(l = in.nextLine()))) {
            String parse[] = l.split(":");
            if (parse[0].equals("Login") && (parse.length >= 3)) {
                Utilizador j = this.gestor.getUtilizador(parse[1]);
                if (j != null) {
                    Utilizador u = j;
                    if (!u.getLog()) { //verifica se já esta autenticado
                        if (u.validaPass(parse[2])) { // valida password              
                            if (u.login()) {
                                System.out.println("Login efectuado com sucesso!!");
                                this.user = u;
                                continua = false;
                                if(this.gestor.getCompradores().containsValue(u))
                                    try{
                                        System.out.println("\n" + this.gestor.listaEmCurso());
                                    }catch (LeilaoException e){
                                    System.out.println(e.getMessage());
                                            }
                            } else {
                                System.out.println("Utilizador já de encontra autenticado!!");
                            }
                        } else {
                            System.out.println("Password Errada!!");
                        }
                    } else {
                        System.out.println("Utilizador já de encontra autenticado!!");
                    }
                } else {
                    System.out.println("Utilizador não existe!!");
                }
            } else {
                System.out.println("Comando errado!!");
            }
            if (continua) {
                System.out.println("###EFECTUE LOGIN PARA USAR A CONSOLA DE COMANDOS###");
            }
        }
        return !l.equals("Sair");
    }

    @Override
    public void run() {
        Scanner in = new Scanner(System.in);
        if (login()) {
        } else {
            this.exit = true;
        }
        String comando;
        while (!exit) {

            comando = in.nextLine();
            switch (comando) {
                case "Sair":
                    exit = true;
                    user.logout();
                    break;
                case "Logout":
                    this.user.logout();
                    if (!login()) {
                        this.exit = true;
                    }
                    break;
                default:
                   
                    PrintWriter out = new PrintWriter(System.out);
                    Thread cm = new Thread(new Thread_Comandos(this.gestor, this.user.getNome(),out, comando));
                    cm.start();
                    break;
            }
        }
        System.exit(MIN_PRIORITY);
    }

}
