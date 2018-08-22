package pe.gob.sunat.microservices.curso.authenticator.service;

import pe.gob.sunat.microservices.curso.authenticator.dao.UserDaoImpl;
import pe.gob.sunat.microservices.curso.authenticator.model.User;

public class UserService {
  private final UserDaoImpl dao;

  public UserService(UserDaoImpl dao) {
    this.dao = dao;
  }

  public User create(User user) {
    return dao.create(user);
  }

  public User login(String username, String password) {
    return dao.findByCustomer(username, password)
      .orElseThrow(() -> new InvalidCredentialsException("Estan mal las credenciales", username));
  }

  public void delete(String username) {
    dao.delete(username);
  }
}
