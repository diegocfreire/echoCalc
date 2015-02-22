package br.com.sd.view;

import com.zaxxer.hikari.HikariDataSource;
import org.skife.jdbi.v2.DBI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by diego on 08/09/14.
 */
public class Config extends JDialog {
    private JPanel contentPane;
    private JButton salvarButton;
    private JButton testarConexaoButton;
    private JButton limparCamposButton;
    private JTextField enderecoTextField;
    private JTextField portaTextField;
    private JTextField baseTextField;
    private JTextField usuarioTextField;
    private JTextField senhaTextField;

    private static HikariDataSource dataSource = new HikariDataSource();
    private static DBI dbi = new DBI(dataSource);
    private static Properties prop = new Properties();

    public Config() {
        configuraForm();
        init();
    }

    private void init() {

        limparCamposButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enderecoTextField.setText("");
                portaTextField.setText("");
                baseTextField.setText("");
                usuarioTextField.setText("");
                senhaTextField.setText("");
            }
        });

        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties");
                try {
                    prop.load(input);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    dataSource.setMaximumPoolSize(Integer.valueOf(prop.getProperty("pg.pool")));
                    dataSource.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
                    dataSource.addDataSourceProperty("serverName", enderecoTextField.getText());
                    dataSource.addDataSourceProperty("portNumber", Integer.valueOf(portaTextField.getText()));
                    dataSource.addDataSourceProperty("databaseName", baseTextField.getText());
                    dataSource.addDataSourceProperty("user", usuarioTextField.getText());
                    dataSource.addDataSourceProperty("password", senhaTextField.getText());
                    dataSource.setPoolName("DB_POOL");
                    dataSource.setInitializationFailFast(true);
                    dataSource.setConnectionTestQuery("SELECT 1");
                    dbi.open();
                    prop.setProperty("pg.server", enderecoTextField.getText());
                    prop.setProperty("pg.port", portaTextField.getText());
                    prop.setProperty("pg.db", baseTextField.getText());
                    prop.setProperty("pg.user", usuarioTextField.getText());
                    prop.setProperty("pg.password", senhaTextField.getText());
                    JOptionPane.showMessageDialog(null, "Conexao realizada com sucesso.");
                    setVisible(false);
                } catch (Exception exx) {
                    JOptionPane.showMessageDialog(null, "Erro: "+exx.getMessage());
                }
            }
        });
    }

    private void configuraForm() {
        setTitle("Configurações");
        setContentPane(contentPane);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        pack();
        setLocationRelativeTo(null);
    }


    public JPanel getContentPane() {
        return contentPane;
    }


}
