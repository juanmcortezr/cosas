package pe.gob.sunat.microservices.curso.orders.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import pe.gob.sunat.microservices.curso.orders.model.Order;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order> {
  @Override
  public Order map(ResultSet rs, StatementContext ctx) throws SQLException {
    Order result = new Order();
    result.setId(rs.getLong("id"));
    result.setCreatedAt(rs.getDate("created_at"));
    result.setAmount(rs.getBigDecimal("amount"));
    result.setCustomerId(rs.getLong("customer_id"));
    result.setDeliveryAddressId(rs.getLong("delivery_address_id"));
    return result;
  }
}
