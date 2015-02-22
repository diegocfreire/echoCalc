package br.com.sd.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Created by diego on 08/09/2014.
 */
public class FrmPrincipal {
    private JPanel contentPane;
    private JTable tableHosps;
    private JTextArea textAreaLog;

    private static final Logger logger = LoggerFactory.getLogger(FrmPrincipal.class);

    public JPanel getContentPane() {
        return contentPane;
    }

    public JTable getTableHosps() {
        return tableHosps;
    }

    public JTextArea getTextAreaLog() {
        return textAreaLog;
    }

    public void info(String msg) {
        textAreaLog.append(msg + System.lineSeparator());
        logger.info(msg);
    }

    public void error(String msg) {
        textAreaLog.append(msg + System.lineSeparator());
        logger.error(msg);
    }

    public void error(String msg, boolean soNoArquivo) {
        logger.error(msg);
    }

}
