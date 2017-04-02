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


public class Cliente {

    public static void main(String[] args) throws IOException, InterruptedException {
        try (
            Socket s = new Socket("localhost", 44555)) {
            String comando;
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter output = new PrintWriter(s.getOutputStream());

            //Thread para ler as mensagens do Cliente
            Thread cl = new Thread(new Thread_LeCliente(serverInput));
            cl.start();
            //le as mensagens do user e envia para o Servidor
            while (!(comando = userInput.readLine()).equals("Logout")) {
                output.println(comando);
                output.flush();
            }

            s.shutdownInput();
            s.shutdownOutput();
            s.close();
        }
    }
}
