package pe.gob.sunat.microservices.curso.customers.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import pe.gob.sunat.microservices.curso.customers.model.Address;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressMapper implements RowMapper<Address> {
  @Override
  public Address map(ResultSet rs, StatementContext ctx) throws SQLException {
    Address result = new Address();
    result.setCountry(rs.getString("country"));
    result.setCustomerId(rs.getLong("owner_id"));
    result.setId(rs.getLong("id"));
    result.setName(rs.getString("name"));
    result.setStreet(rs.getString("street"));
    return result;
  }
}
