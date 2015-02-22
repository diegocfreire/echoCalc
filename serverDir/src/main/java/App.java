/**
 * Created by diego on 08/09/2014.
 */

import br.com.sd.mapper.HospMapper;
import br.com.sd.model.Hosp;
import br.com.sd.model.HospTableModel;
import br.com.sd.view.Config;
import br.com.sd.view.FrmPrincipal;
import com.zaxxer.hikari.HikariDataSource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class App {


    private static HikariDataSource dataSource = new HikariDataSource();
    private static DBI dbi = new DBI(dataSource);
    private static Properties prop = new Properties();
    static FrmPrincipal m = new FrmPrincipal();

    public static void main(String[] args) {

        //Inicia Form Principal
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("echoCalc - Servidor de Diretórios");         // frame que vai mostrar o meu form
                                            // main form
        frame.setContentPane(m.getContentPane());                               // conteúdo do frame vai ser o main form

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);    // quando clicar no botã do fechar ou Alt+F4 não faz nada
        frame.addWindowListener(new WindowAdapter() {                           // quando clicar no botã do fechar ou Alt+F4 pergunta se tem certeza
            @Override
            public void windowClosing(WindowEvent e) {
                String ObjButtons[] = { "Sim", "Não" };
                int PromptResult = JOptionPane.showOptionDialog(null, "Deseja encerrar o Servidor de Diretórios?",
                        "Atenção", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                if (PromptResult == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        frame.pack();                                                           // ajusta os componentes na tela
        frame.setLocationRelativeTo(null);                                      // centraliza na tela
        frame.setVisible(true);                                                 // mostra

        init();
    }

    public static void init() {
        m.info("Iniciando Aplicação do Servidor de Diretórios...");

        m.info("Carregando configurações..." + System.lineSeparator());

        InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties");
        if (input == null) {
            m.error("Arquivo de configurações não encontrado.");
            return;
        }

        try {

            prop.load(input);
        } catch (IOException e) {
            m.error("Erro ao carregar informações da base de dados: "+e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            dataSource.setMaximumPoolSize(Integer.valueOf(prop.getProperty("pg.pool")));
            dataSource.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
            dataSource.addDataSourceProperty("serverName", prop.getProperty("pg.server"));
            dataSource.addDataSourceProperty("portNumber", Integer.valueOf(prop.getProperty("pg.port")));
            dataSource.addDataSourceProperty("databaseName", prop.getProperty("pg.db"));
            dataSource.addDataSourceProperty("user", prop.getProperty("pg.user"));
            dataSource.addDataSourceProperty("password", prop.getProperty("pg.password"));
            dataSource.setPoolName("DB_POOL");
            dataSource.setInitializationFailFast(true);
            dataSource.setConnectionTestQuery("SELECT 1");
            m.info("Configurações carregadas." + System.lineSeparator());
            //testaConexao();
        } catch (Exception e) {
            m.error("Erro ao conectar na base de dados: "+e.getMessage());
            throw new RuntimeException(e);
        }

        listaHospedeiros();
        m.info("Base de dados conectada {"+prop.getProperty("pg.db")+"}." + System.lineSeparator());

        addShutdownHook(dataSource);
        m.info("Aguardando consultas de servidores disponíveis..." + System.lineSeparator());
        thread.start();

    }

    private static void testaConexao() {
        try {
            dbi.open();
            m.info("Conexão realizada {Database=" + prop.getProperty("pg.db") + "}");
        } catch (Exception e) {

            Config cfg = new Config();
            cfg.setVisible(true);

        }
    }

    static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            byte[] buffer = new byte[1000];
            try {
                DatagramSocket socket = new DatagramSocket(7005);
                while (true) {
                    try {
                        DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
                        socket.receive(pacote);
                        String conteudo = new String(pacote.getData(), 0, pacote.getLength());

                        Scanner scanner = new Scanner(conteudo);
                        scanner.useDelimiter("\\|");

                        String tipo = scanner.next();

                        String hospedeiro = "";

                        //Se for Registro de novo servidor, são passados 4 parametros na string
                        //Senão é uma busca por algum servidor que faça determinada operação, então é passado 2 parametros
                        if (tipo.equals("register")) {
                            String ip = scanner.next();
                            String porta = scanner.next();
                            String operacao = scanner.next();
                            registraHospedeiro(ip, porta, operacao);
                        } else if (tipo.equals("search")) {
                            String oper = scanner.next();
                            hospedeiro = buscaHospedeiro(oper)+"|aumentandoastringpararetorno";
                            byte[] bRes = new byte[1000];
                            bRes = hospedeiro.getBytes();
                            pacote.setData(bRes, 0, bRes.length);
                            socket.send(pacote);
                        } else if (tipo.equals("remove")) {
                            String ip = scanner.next();
                            removeHospedeiro(ip);
                        }

                    } catch (IOException e) {
                        m.error("Erro: "+e.getMessage());
                    }
                }
            } catch(IOException ex) {
                m.error("Erro: "+ex.getMessage());
            }
        }
    };

    private static void listaHospedeiros() {
        List<Hosp> hospsList = null;
        try (Handle db = dbi.open()) {
            hospsList = db.createQuery("SELECT * FROM hospedeiro")
                    .map(new HospMapper()).list();
        }

        HospTableModel model = new HospTableModel(hospsList);
        m.getTableHosps().setModel(model);
    }

    private static String buscaHospedeiro(String oper) {
        try (Handle qry = dbi.open()) {
            Hosp hosp = qry.createQuery("SELECT * FROM hospedeiro where operacao=:op").bind("op", oper).
                    map(new HospMapper()).first();
            if (hosp != null) {
                return hosp.getIp()+"|"+hosp.getPorta()+"|"+hosp.getOperacao();
            } else {
                return "";
            }
        } catch (Exception e) {
            m.error("Erro: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void registraHospedeiro(String ip, String porta, String operacao) {
        try (Handle qry = dbi.open()) {
            qry.begin();
            qry.createStatement("INSERT INTO hospedeiro VALUES (:ip, :porta, :oper)").
                    bind("ip", ip).
                    bind("porta", porta).
                    bind("oper", operacao).
                    execute();
            qry.commit();
            listaHospedeiros();
        } catch (Exception e) {
            m.error("Erro: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void removeHospedeiro(String ip) {
        try (Handle qry = dbi.open()) {
            qry.begin();
            qry.createStatement("DELETE FROM hospedeiro WHERE ip=:ip").
                    bind("ip", ip).
                    execute();
            qry.commit();
            listaHospedeiros();
        } catch (Exception e) {
            m.error("Erro: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static Thread thread = new Thread(runnable);

    private static void addShutdownHook(final HikariDataSource dataSource) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                m.info("Encerrando conexões...");
                dataSource.close();
                m.info("Programa finalizado.");
            }
        });
    }

}
