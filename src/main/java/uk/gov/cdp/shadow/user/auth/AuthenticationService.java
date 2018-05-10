package uk.gov.cdp.shadow.user.auth;

public interface AuthenticationService {

  void authenticate(String userName, String subject, String bizContext);
}
