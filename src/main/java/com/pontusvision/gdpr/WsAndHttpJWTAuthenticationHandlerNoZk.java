package com.pontusvision.gdpr;

import com.nimbusds.jose.JWSObject;
import io.netty.channel.ChannelHandler;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.apache.tinkerpop.gremlin.server.auth.Authenticator;
import org.apache.zookeeper.KeeperException;
import uk.gov.homeoffice.pontus.JWTClaim;

import java.io.IOException;

@ChannelHandler.Sharable

public class WsAndHttpJWTAuthenticationHandlerNoZk extends WsAndHttpJWTAuthenticationHandler
{

  public WsAndHttpJWTAuthenticationHandlerNoZk(final Authenticator authenticator,
                                               final Settings.AuthenticationSettings authenticationSettings)
  {
    super(authenticator, authenticationSettings);

  }

  // Method to disconnect from zookeeper server
  @Override
  public void close() throws InterruptedException
  {
    // noop

  }

  // Method to connect zookeeper ensemble.

  @Override
  protected void handleZookeeper (JWSObject jwsObject, JWTClaim sampleClaim )
      throws KeeperException, InterruptedException, IOException
  {
     // noop

  }


}
