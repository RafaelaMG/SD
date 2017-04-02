/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leilao;


import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Gestor {

    private TreeMap<Integer, Leilao> emCurso;
    private TreeMap<Integer, Leilao> terminados;
    private TreeMap<Integer, TreeSet<Licitacao>> historicoLicitacao;
    private HashMap<String, Vendedor> vendedores;
    private HashMap<String, Comprador> compradores;
    private ReentrantLock lockLei;
    private ReentrantLock lockLic;
    private int idLgera = 1;

    public Gestor() {
        this.emCurso = new TreeMap<>();
        this.terminados = new TreeMap<>();
        this.historicoLicitacao = new TreeMap<>();
        this.vendedores = new HashMap<>();
        this.compradores = new HashMap<>();
        this.lockLei = new ReentrantLock();
        this.lockLic = new ReentrantLock();

    }

    public Gestor(TreeMap<Integer, Leilao> emCurso, TreeMap<Integer, Leilao> terminados, TreeMap<Integer, TreeSet<Licitacao>> historicoLicitacao, HashMap<String, Vendedor> vendedores, HashMap<String, Comprador> compradores) {
        this.emCurso = emCurso;
        this.terminados = terminados;
        this.historicoLicitacao = historicoLicitacao;
        this.vendedores = vendedores;
        this.compradores = compradores;
        this.lockLei = new ReentrantLock();
        this.lockLic = new ReentrantLock();
    }

    public Gestor(Gestor g) {
        this.emCurso = g.getEmCurso();
        this.terminados = g.getTerminados();
        this.historicoLicitacao = g.getHistoricoLicitacao();
        this.vendedores = g.getVendedores();
        this.compradores = g.getCompradores();
    }

    public HashMap<String, Vendedor> getVendedores() {
        return vendedores;
    }

    public void setVendedores(HashMap<String, Vendedor> vendedores) {
        this.vendedores = vendedores;
    }

    public HashMap<String, Comprador> getCompradores() {
        return compradores;
    }

    public void setCompradores(HashMap<String, Comprador> compradores) {
        this.compradores = compradores;
    }

    public TreeMap<Integer, Leilao> getEmCurso() {
        return emCurso;
    }

    public void setEmCurso(TreeMap<Integer, Leilao> emCurso) {
        this.emCurso = emCurso;
    }

    public TreeMap<Integer, Leilao> getTerminados() {
        return terminados;
    }

    public void setTerminados(TreeMap<Integer, Leilao> terminados) {
        this.terminados = terminados;
    }

    public TreeMap<Integer, TreeSet<Licitacao>> getHistoricoLicitacao() {
        return historicoLicitacao;
    }

    public void setHistoricoLicitacao(TreeMap<Integer, TreeSet<Licitacao>> historicoLicitacao) {
        this.historicoLicitacao = historicoLicitacao;
    }

    public int getIdLgera() {
        return idLgera;
    }

    public void setIdLgera(int idLgera) {
        this.idLgera = idLgera;
    }

    public Utilizador getUtilizador(String n) {
        Utilizador u = null;
    
        if (this.vendedores.containsKey(n)) {
            u = this.vendedores.get(n);
        } else {
            u = this.compradores.get(n);
      
        }

        return u;
    }

    public Leilao constroi_Leilao(String parse[], String idUti) {
        Leilao l;

        int id = this.idLgera;
        boolean est = true;
        this.idLgera++;
        String descricao = parse[1];
        Float valorinicial = Float.parseFloat(parse[2]);
        l = new Leilao(id, descricao, valorinicial, idUti, est);
        return l;
    }

    public void inicia_Leilao(Leilao l, String idUti) throws LeilaoException {
        lockLei.lock();
        HashMap<String, ArrayList<Leilao>> leiloados = new HashMap<>();
        ArrayList<Leilao> lei = new ArrayList<>();
        try {
            if (this.vendedores.containsKey(idUti)) {
                leiloados = this.vendedores.get(idUti).getItens_Leiloados();
                if (leiloados.containsKey(idUti)) {
                    leiloados.get(idUti).add(l);
                    this.vendedores.get(idUti).setItens_Leiloados(leiloados);
                } else {
                    lei.add(l);
                    leiloados.put(idUti, lei);
                    this.vendedores.get(idUti).setItens_Leiloados(leiloados);
                }
            }else{throw new LeilaoException("Não tem permissões para iniciar um leilão!!");}
            this.emCurso.put(l.getId(), l);
        } finally {
            lockLei.unlock();
        }
    }

    public void termina_Leilao(String id, String idUti) throws LeilaoException{
        lockLei.lock();
        Leilao l;
        Licitacao b;
        int idL = Integer.parseInt(id);
        Iterator it;
        try {
         if (this.vendedores.containsKey(idUti)) {
            if (this.emCurso.containsKey(idL)) {
                l = this.emCurso.get(idL);
                if (l.getVendedor().equals(idUti)) {
                    l.setEstado(false);
                    this.terminados.put(l.getId(), l);
                    this.emCurso.remove(idL);
                    System.out.println("Terminado Leilão com o id " + l.getId() + "!!");
                   
                    if (this.historicoLicitacao.containsKey(idL)) {
                        it = this.historicoLicitacao.get(idL).iterator();
                        b = (Licitacao) it.next();
                        
                        System.out.println("O Vencedor do Leilão com o id "+b.getLeilao()+" é " + b.getComprador() + " e dispendeu " + b.getValor() + " €\n");
                        
                    } else {
                       
                    }
                } else {
                    throw new LeilaoException("O Leilão para o id = " + idL + " não lhe pertence!!\n");
                }
            } else {
                throw new LeilaoException("O Leilão para o id = " + idL + " não existe!!\n");
            }
         }else{
            throw new LeilaoException("Não tem permissões para executar este comando!!");
         }

        } finally {

            lockLei.unlock();

        }

    }

    public Licitacao constroi_Licitacao(String parse[], String idUti) {
        Licitacao l;
        int id = Integer.parseInt(parse[1]);
        Float bid = Float.parseFloat(parse[2]);
        l = new Licitacao(id, idUti, bid);
        return l;
    }
    

    
    public void fazLicitacao(Licitacao l, String idUti) throws InterruptedException, LicitacaoException {
        TreeSet<Licitacao> bids = new TreeSet<>(new ComparatorLicitacao());
        Leilao lei;
        Iterator it;
        lockLic.lock();
        
        try {
            
            if (this.compradores.containsKey(idUti)) {

                if (this.emCurso.containsKey(l.getLeilao())) {
                    lei = this.emCurso.get(l.getLeilao());
                    if (l.getValor() > lei.getValor_inicial()) { 
                        if (this.historicoLicitacao.containsKey(l.getLeilao())) {
                            it = this.historicoLicitacao.get(l.getLeilao()).iterator();
                            Licitacao c = (Licitacao) it.next();
                            if (c.getValor() < l.getValor()) {
                                bids = this.historicoLicitacao.get(l.getLeilao());
                                bids.add(l);
                                this.historicoLicitacao.put(l.getLeilao(), bids);
                                mudaValor(l);
                                throw new LicitacaoException("Licitado com sucesso!!");
                            } else {
                                throw new LicitacaoException("Valor introduzido já foi ultrapassado!!");
                            }
                        } else {
                            bids.add(l);
                            this.historicoLicitacao.put(l.getLeilao(), bids);
                            mudaValor(l);
                            throw new LicitacaoException("Licitado com sucesso!!");
                        }
                    } else {
                        throw new LicitacaoException("Valor introduzido é menor que valor inicialmente pedido!!");
                    }
                } else {
                    throw new LicitacaoException("O id do Leilão introduzido não existe ou já terminou!!");
                }
            } else {
                throw new LicitacaoException("Não tem permissão para fazer Licitação!!");
            }
        } finally {
            lockLic.unlock();

        }

    }

    public void mudaValor(Licitacao l) {
        Leilao e;
        if (this.emCurso.containsKey(l.getLeilao())) {
            e = this.emCurso.get(l.getLeilao());
            e.setValor_actual(l.getValor());
            this.emCurso.put(e.getId(), e);
        }
    }

    class ComparatorLicitacao implements Comparator<Licitacao> {

        public int compare(Licitacao l1, Licitacao l2) {
            float bid1 = l1.getValor();
            float bid2 = l2.getValor();

            if (bid1 > bid2) {
                return -1;
            } else if (bid1 < bid2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public String listaEmCurso() throws LeilaoException {
        Leilao l;
        StringBuilder res = new StringBuilder();
        if (this.emCurso.isEmpty()) {
            throw new LeilaoException("Não há Leilões a decorrer!!!");
   
        } else {
            for (Integer id : this.emCurso.keySet()) {
                l = this.emCurso.get(id);
                res.append("Leilão: Id: " + l.getId() + "; Item Leiloado: " + l.getDescricao() + "; ValorActual/ValorInicial: " + l.getValor_actual() + "/" + l.getValor_inicial());
                res.append("\n");
            }
        }
        return res.toString();
    }

    public String listaTerminados() throws LeilaoException {
        StringBuilder res = new StringBuilder();
        Leilao l;
        Licitacao bid;
        Iterator it;
        if (this.terminados.isEmpty()) {
            throw new LeilaoException("Não há Leilões terminados!!!");

        } else {
            for (Integer id : this.terminados.keySet()) {
                l = this.terminados.get(id);              
                if (this.historicoLicitacao.containsKey(id)) {
                    it = this.historicoLicitacao.get(id).iterator();
                    bid = (Licitacao) it.next();
                    l = this.terminados.get(id);
                    res.append("Leilão: Id: " + l.getId() + "; Item Leiloado: " + l.getDescricao() + "; Vencedor: " + bid.getComprador() + ";\n");
                    res.append("\n");
                }
            }
        }
        return res.toString();
    }

    public String listaMeusLeiloes(String idUti) throws LeilaoException{
        StringBuilder res = new StringBuilder();
        ArrayList<Leilao> lei = new ArrayList<>();
        if(this.vendedores.containsKey(idUti)){
        for (Integer i : this.emCurso.keySet()) {
            lei.add(this.emCurso.get(i));
        }
        for (Integer id : this.terminados.keySet()) {
            lei.add(this.terminados.get(id));
        }

        for (Leilao l : lei) {
            if (l.getVendedor().equals(idUti)) {
                res.append("Leilão: Id: " + l.getId() + "; * \n");
            } else {
                res.append("Leilão: Id: " + l.getId() + ";\n");
            }
        }
        }else{
            throw new LeilaoException("Não tem permissões para executar este comando!!");
        }
        return res.toString();
    }

    public String listaMinhasLicitacoes(String idUti)  throws LeilaoException {
        StringBuilder res = new StringBuilder();
        Licitacao b;
        Iterator it;
        if (this.compradores.containsKey(idUti)) {
        for (Integer idL : this.historicoLicitacao.keySet()) {
            it = this.historicoLicitacao.get(idL).iterator();
            b = (Licitacao) it.next();
            if (b.getComprador().equals(idUti)) {
                res.append("Leilão: Id: " + b.getLeilao() + "; + \n");
                
            } else {
                res.append("Leilão: Id: " + b.getLeilao() + ";\n");

            }

        }}else{
            throw new LeilaoException("Não tem permissões para executar este comando!!");
        }

        return res.toString();
    }

    
    public void avisaVencedor(Licitacao l) throws LeilaoException{
        TreeSet<Licitacao> c;
        Iterator it;
        ArrayList<Comprador> comp = new ArrayList<>();
        TreeSet<String> nomes = new TreeSet<>();

        c = this.historicoLicitacao.get(l.getLeilao());
        for (Licitacao b : c) {
            nomes.add(b.getComprador());
            for (String n : nomes) {
                comp.add(this.compradores.get(n));
            }
            
            Comprador com;
            it = comp.iterator();
            while (it.hasNext()) {
                com = (Comprador) it.next();
                if (com.login() == (true)) {
                    throw new LeilaoException("O Vencedor do Leilão com o id "+l.getLeilao()+" é " + l.getComprador() + " e dispendeu " + l.getValor() + " €\n");
                }

            }
        }

    }
}