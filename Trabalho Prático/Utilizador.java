/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leilao;


public class Utilizador {

    private String nome; //id
    private String password;

    private boolean log;

    public Utilizador() {
        this.nome = "";
        this.password = "";
        this.log = false;
    }

    public Utilizador(String nome, String password) {
        this.nome = nome;
        this.password = password;
        this.log = false;
    }

    public Utilizador(Utilizador u) {
        this.nome = u.getNome();
        this.password = u.getPassword();
        this.log = u.getLog();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if ((o == null) || (this.getClass() != o.getClass())) {
            return false;
        } else {
            Utilizador u = (Utilizador) o;
            return (this.getNome().equals(u.getNome()) && this.getPassword().equals(u.getPassword()));
        }
    }

    public boolean validaPass(String passwd) {
        return (this.password.equals(passwd));
    }

    public boolean login() {
        if (!this.log) {
            this.log = true;
            return true;
        } else {
            return false;
        }
    }

    public void logout() {
        this.log = false;
    }

    public boolean getLog() {
        return log;
    }

    @Override
    public String toString() {
        return "Utilizador{" + "nome=" + nome + ", password=" + password + '}';
    }

    public Utilizador clone() {
        return new Utilizador(this);
    }

}
