package br.com.sd.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego on 08/09/2014.
 */
public class HospTableModel extends AbstractTableModel {

    private List<Hosp> hospList;
    private String[] colunas = new String[] { "End. IP", "Porta", "Operação" };
    private static final int ip = 0;
    private static final int porta = 1;
    private static final int operacao = 2;

    public HospTableModel() {
        this.hospList = new ArrayList<>();
    }

    public HospTableModel(List<Hosp> hospList) {
        this.hospList = new ArrayList<>(hospList);
    }


    @Override
    public int getRowCount() {
        return hospList.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Hosp hosp = hospList.get(rowIndex);
        switch (columnIndex) {
            case ip: return hosp.getIp();
            case porta: return hosp.getPorta();
            case operacao: return hosp.getOperacao();
            default: throw new IndexOutOfBoundsException("columnIndex out of bounds");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Hosp hosp = hospList.get(rowIndex);
        switch (columnIndex) {
            case ip: hosp.setIp((String) aValue); break;
            case porta: hosp.setPorta((String) aValue); break;
            case operacao: hosp.setOperacao((String) aValue); break;
            default: throw new IndexOutOfBoundsException("columnIndex out of bounds");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }
}
