package pe.gob.sunat.microservices.curso.customers.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import pe.gob.sunat.microservices.curso.customers.model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerMapper implements RowMapper<Customer> {
  @Override
  public Customer map(ResultSet rs, StatementContext ctx) throws SQLException {
    Customer result = new Customer();
    result.setId(rs.getLong("id"));
    result.setBusinessName(rs.getString("business_name"));
    result.setEmail(rs.getString("email"));
    result.setFirstName(rs.getString("first_name"));
    result.setLastName(rs.getString("last_name"));
    result.setTaxId(rs.getString("tax_id"));
    return result;
  }
}
