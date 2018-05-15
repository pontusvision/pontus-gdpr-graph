package uk.gov.cdp.ldap;

/*
  Author: Deepesh Rathore
 */

import java.util.List;

public interface LdapService {

    boolean login(String userName, String password);

    void createUserAccount(String userName, String password, List<String> groups);

    boolean userExist(String userName);
}
