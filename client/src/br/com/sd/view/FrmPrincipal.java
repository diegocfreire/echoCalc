package br.com.sd.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Created by diego on 08/09/14.
 */
public class FrmPrincipal {
    private JPanel contentPane;
    private JButton sairButton;
    private JButton processarButton;
    private JTextField enderecoTextField;
    private JTextField portaTextField;
    private JRadioButton somaRadioButton;
    private JRadioButton subRadioButton;
    private JRadioButton mulRadioButton;
    private JRadioButton divRadioButton;
    private JTextField aTextField;
    private JTextField bTextField;

    public FrmPrincipal() {
        init();
    }

    private void init() {
        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ObjButtons[] = {"Sim", "Não"};
                int PromptResult = JOptionPane.showOptionDialog(null, "Deseja encerrar o echoCalc Client?",
                        "Atenção", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                if (PromptResult == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        processarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int porta = Integer.parseInt(portaTextField.getText());
                    InetAddress servidor = InetAddress.getByName(enderecoTextField.getText());
                    String oper = "";
                    if (somaRadioButton.isSelected()) {
                        oper = "0";
                    } else if (subRadioButton.isSelected()) {
                        oper = "1";
                    } else if (mulRadioButton.isSelected()) {
                        oper = "2";
                    } else if (divRadioButton.isSelected()) {
                        oper = "3";
                    }

                    String conteudo = "search|"+oper+"|aumentandostringparaoretorno";
                    byte[] dados = conteudo.getBytes();
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket pacote = new DatagramPacket(dados, dados.length, servidor, porta);
                    socket.send(pacote);

                    socket.receive(pacote);
                    String retorno = new String(pacote.getData(), 0, pacote.getLength());

                    if (!retorno.equals("")) {
                        executaOperacao(retorno, oper);
                    } else {

                    }
                } catch (Exception e1) {

                }
            }
        });
    }

    private void executaOperacao(String dados, String oper) {
        try {
            Scanner scanner = new Scanner(dados);
            scanner.useDelimiter("\\|");
            String ip = scanner.next();
            int porta = Integer.parseInt(scanner.next());
            InetAddress servidor = InetAddress.getByName(ip);
            String conteudo = oper+"|"+aTextField.getText()+"|"+bTextField.getText();
            byte[] b = conteudo.getBytes();

            DatagramSocket socket = new DatagramSocket();
            DatagramPacket pacote = new DatagramPacket(b, b.length, servidor, porta);
            socket.send(pacote);

            socket.receive(pacote);
            String retorno = new String(pacote.getData(), 0, pacote.getLength());

            String Msg = "";

            if (somaRadioButton.isSelected()) {
                Msg = "A soma de "+aTextField.getText()+" e "+bTextField.getText()+" = ";
            } else if (subRadioButton.isSelected()) {
                Msg = "A subtração de "+aTextField.getText()+" e "+bTextField.getText()+" = ";
            } else if (mulRadioButton.isSelected()) {
                Msg = "A multiplicação de "+aTextField.getText()+" e "+bTextField.getText()+" = ";
            } else if (divRadioButton.isSelected()) {
                Msg = "A divisão de "+aTextField.getText()+" e "+bTextField.getText()+" = ";
            }

            JOptionPane.showMessageDialog(null, Msg+retorno);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro: "+e.getMessage());
            System.exit(-1);
        }
    }


    public JPanel getContentPane() {
        return contentPane;
    }
}
