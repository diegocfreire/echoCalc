import br.com.sd.view.FrmPrincipal;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by diego on 08/09/14
 */
public class Main {

    static FrmPrincipal m = new FrmPrincipal();                                    // main form

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("echoCalc Client");                           // frame que vai mostrar o meu form

        frame.setContentPane(m.getContentPane());                               // conteúdo do frame vai ser o main form

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);    // quando clicar no botã do fechar ou Alt+F4 não faz nada
        frame.addWindowListener(new WindowAdapter() {                           // quando clicar no botã do fechar ou Alt+F4 pergunta se tem certeza
            @Override
            public void windowClosing(WindowEvent e) {
                String ObjButtons[] = { "Sim", "Não" };
                int PromptResult = JOptionPane.showOptionDialog(null, "Deseja encerrar o echoCalc Client?",
                        "Atenção", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                if (PromptResult == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        frame.pack();                                                           // ajusta os componentes na tela
        frame.setLocationRelativeTo(null);                                      // centraliza na tela
        frame.setVisible(true);                                                 // mostra

    }

}
