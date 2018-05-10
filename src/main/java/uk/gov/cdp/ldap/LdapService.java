package uk.gov.cdp.ldap;

/*
  Author: Deepesh Rathore
 */

public interface LdapService {

  boolean login(String userName, String password);

  void createUserAccount(String userName, String password);

  boolean userExist(String userName);
}
