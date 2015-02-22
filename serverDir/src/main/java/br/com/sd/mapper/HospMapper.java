package br.com.sd.mapper;

/**
 * Created by diego on 08/09/2014.
 */
import br.com.sd.model.Hosp;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class HospMapper implements ResultSetMapper<Hosp> {
    @Override
    public Hosp map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Hosp h = new Hosp();
        h.setIp(r.getString("ip"));
        h.setPorta(r.getString("porta"));
        h.setOperacao(r.getString("operacao"));
        return h;
    }
}
