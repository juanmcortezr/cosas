package pe.gob.sunat.microservices.curso.authenticator.dao;

import org.jdbi.v3.core.Jdbi;
import pe.gob.sunat.microservices.curso.authenticator.model.User;

import java.util.Optional;

public class UserDaoImpl {
  private final Jdbi jdbi;

  public UserDaoImpl(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  public User create(User user) {
    jdbi.inTransaction(handle -> {
      String insert = "INSERT INTO users " +
        "(username, password) " +
        "VALUES (:username, :password);";
      return handle.createUpdate(insert)
        .bind("username", user.getUsername())
        .bind("password", user.getPassword())
        .execute();
    });

    return user;
  }

  public void delete(String username) {
    jdbi.inTransaction(handle -> {
      String delete = "DELETE FROM users where username=:username";
      return handle.createUpdate(delete)
        .bind("username", username)
        .execute();
    });
  }

  public Optional<User> findByCustomer(String username, String password) {
    return jdbi.withHandle(handle ->
      handle.createQuery("SELECT * from users where username=:username AND password=:password")
        .bind("username", username)
        .bind("password", password)
        .mapToBean(User.class)
        .findFirst());
  }
}
