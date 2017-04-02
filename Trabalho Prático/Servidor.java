/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leilao;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Servidor {

    public static void main(String[] args) throws Exception {
        
        int port = 44555;
        ServerSocket ss;
        boolean exit = false;
        HashMap<String, Vendedor> vend = new HashMap<>(); //vendedores registados
        HashMap<String, Comprador> comp = new HashMap<>(); //compradores registados
        Gestor gestor = new Gestor();
        Thread t;        
        
        ss = new ServerSocket(port);
        System.out.println("Socket Criado!");

        /*adiciona utilizadores*/
        Vendedor u1 = new Vendedor("joao", "60982", 1);
        Vendedor u2 = new Vendedor("pedro", "kkk", 1);
        Comprador u3 = new Comprador("filipe", "62153", 2);
        Comprador u4 = new Comprador("rafaela", "60991", 2);
        vend.put(u1.getNome(), u1);
        vend.put(u2.getNome(), u2);        
        comp.put(u3.getNome(), u3); 
        comp.put(u4.getNome(), u4);        

        gestor.setCompradores(comp);
        gestor.setVendedores(vend);

        /*Cria aqui uma thread para a consola do servidor*/
        Thread_Consola consola = new Thread_Consola(gestor, exit, ss);        
        consola.start();
        //conexão com Clientes
        while (!exit) {            
            Socket s = ss.accept();            
            t = new Thread(new Thread_Cliente(gestor, s));
            t.start();
        }        
        ss.close();
    }
}
