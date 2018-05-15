package uk.gov.cdp.shadow.user.auth.integration;

import static junit.framework.TestCase.assertTrue;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.cdp.shadow.user.auth.AuthenticationService;
import uk.gov.cdp.shadow.user.auth.guice.TestApplicationModule;

import java.util.Collections;

@RunWith(JukitoRunner.class)
public class AuthenticationServiceTest {

  public static class TestModule extends JukitoModule {

    @Override
    protected void configureTest() {
      install(new TestApplicationModule());
    }
  }

  //  @Inject private LdapService ldapService;

  @Before
  public void setup() throws LdapException {
    LdapConnection connection = new LdapNetworkConnection("127.0.0.1", 389);

    connection.add(
        new DefaultEntry(
            "cn=testadd,ou=system", // The Dn
            "ObjectClass: top",
            "ObjectClass: person",
            "cn: testadd_cn",
            "sn: testadd_sn"));

    assertTrue(connection.exists("cn=testadd,ou=system"));
  }

  @Test
  public void whenUserDoesntExist_ItsCreated_AndAuthenticated(AuthenticationService underTest) {

    underTest.authenticate("cdp_test_deepesh", "pa55w0rdDSR", "biz/org", Collections.emptyList());
    //    verify(ldapService).createUserAccount(any(), any());
  }
}
