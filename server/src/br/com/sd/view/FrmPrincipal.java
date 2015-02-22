package br.com.sd.view;

import br.com.sd.controller.Operacoes;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Created by diego on 08/09/2014.
 */
public class FrmPrincipal {

    private JButton sairButton;
    private JPanel contentPane;
    private JTextArea textAreaLog;
    private JTextField enderecotextField;
    private JTextField portatextField;
    private JButton conectaButton;
    private JCheckBox somaCheckBox;
    private JCheckBox subCheckBox;
    private JCheckBox multCheckBox;
    private JCheckBox divCheckBox;

    public FrmPrincipal() {
        init();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            byte[] buffer = new byte[1000];
            try {
                DatagramSocket socket = new DatagramSocket(7004);
                textAreaLog.append("Servidor iniciado" + System.lineSeparator());
                textAreaLog.append("Aguardando requisições..." + System.lineSeparator());
                while (true) {
                    try {
                        DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
                        socket.receive(pacote);
                        String conteudo = new String(pacote.getData(), 0, pacote.getLength());
                        textAreaLog.append("#Requisição de {" + pacote.getAddress() + "}: " + conteudo + "\n" + System.lineSeparator());

                        Scanner scanner = new Scanner(conteudo);
                        scanner.useDelimiter("\\|");

                        String oper = scanner.next();
                        String nA   = scanner.next();
                        String nB   = scanner.next();

                        String res = "";
                        switch (Integer.parseInt(oper)) {
                            case 0: {
                                res = String.valueOf(Operacoes.Soma(Integer.parseInt(nA), Integer.parseInt(nB)));
                            } break;
                            case 1: {
                                res = String.valueOf(Operacoes.Subtracao(Integer.parseInt(nA), Integer.parseInt(nB)));
                            } break;
                            case 2: {
                                res = String.valueOf(Operacoes.Multiplic(Integer.parseInt(nA), Integer.parseInt(nB)));
                            } break;
                            case 3: {
                                res = String.valueOf(Operacoes.Divisao(Integer.parseInt(nA), Integer.parseInt(nB)));
                            } break;
                        }

                        textAreaLog.append("#Retorno para {" + pacote.getAddress() + "}: " + res + "\n" + System.lineSeparator());

                        byte[] bRes = new byte[1000];
                        bRes = res.getBytes();
                        pacote.setData(bRes, 0, bRes.length);
                        socket.send(pacote);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Erro: "+e.getMessage());
                    }
                }
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(null, "Erro: "+ex.getMessage());
                System.exit( -1 );
            }
        }
    };

    Thread thread = new Thread(runnable);

    private void init() {

        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ObjButtons[] = {"Sim", "Não"};
                int PromptResult = JOptionPane.showOptionDialog(null, "Deseja encerrar o echoCalc Server?",
                        "Atenção", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                if (PromptResult == JOptionPane.YES_OPTION) {
                    //Remove server no Servidor de Diretórios
                    removeServer(enderecotextField.getText(), portatextField.getText());
                    System.exit(0);
                }
            }
        });

        conectaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Registra server no Servidor de Diretórios
                if (enderecotextField.getText().equals("") || portatextField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Preencha o endereço e porta do Servidor de Diretórios.");
                } else {
                    if (somaCheckBox.isSelected()) {
                        registraServer(enderecotextField.getText(), portatextField.getText(), "0");
                    };
                    if (subCheckBox.isSelected()) {
                        registraServer(enderecotextField.getText(), portatextField.getText(), "1");
                    };
                    if (multCheckBox.isSelected()) {
                        registraServer(enderecotextField.getText(), portatextField.getText(), "2");
                    };
                    if (divCheckBox.isSelected()) {
                        registraServer(enderecotextField.getText(), portatextField.getText(), "3");
                    };
                    enderecotextField.setEnabled(false);
                    portatextField.setEnabled(false);
                    conectaButton.setEnabled(false);
                    somaCheckBox.setEnabled(false);
                    subCheckBox.setEnabled(false);
                    multCheckBox.setEnabled(false);
                    divCheckBox.setEnabled(false);
                    thread.start();
                }
            }
        });
    }

    private void registraServer(String Ip, String Porta, String Oper) {
        try {
            //conteudo = "register|ip|porta|operacao"
            java.net.InetAddress i = java.net.InetAddress.getLocalHost();
            String ip = i.getHostAddress();
            String conteudo = "register|"+ip+"|7004|"+Oper;
            byte[] dados = conteudo.getBytes();
            InetAddress servidor = InetAddress.getByName(enderecotextField.getText());
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket pacote = new DatagramPacket(dados, dados.length, servidor, Integer.parseInt(portatextField.getText()));
            socket.send(pacote);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro: "+ex.getMessage());
            System.exit(-1);
        }
    }

    public void removeServer(String Ip, String Porta) {
        try {
            //conteudo = "remove|ip"
            java.net.InetAddress i = java.net.InetAddress.getLocalHost();
            String ip = i.getHostAddress();
            String conteudo = "remove|"+ip;
            byte[] dados = conteudo.getBytes();
            InetAddress servidor = InetAddress.getByName(enderecotextField.getText());
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket pacote = new DatagramPacket(dados, dados.length, servidor, Integer.parseInt(portatextField.getText()));
            socket.send(pacote);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro: "+ex.getMessage());
            System.exit(-1);
        }
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    public JTextField getEnderecotextField() {
        return enderecotextField;
    }

    public JTextField getPortatextField() {
        return portatextField;
    }
}
