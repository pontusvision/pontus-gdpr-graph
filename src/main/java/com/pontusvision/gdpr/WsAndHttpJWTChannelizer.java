package com.pontusvision.gdpr;

import io.netty.channel.ChannelPipeline;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.apache.tinkerpop.gremlin.server.auth.AllowAllAuthenticator;
import org.apache.tinkerpop.gremlin.server.channel.WsAndHttpChannelizer;
import org.apache.tinkerpop.gremlin.server.handler.AbstractAuthenticationHandler;
import org.apache.tinkerpop.gremlin.server.handler.HttpGremlinEndpointHandler;
import org.apache.tinkerpop.gremlin.server.handler.WsAndHttpChannelizerHandler;
import org.apache.tinkerpop.gremlin.server.util.ServerGremlinExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsAndHttpJWTChannelizer extends WsAndHttpChannelizer
{

  private static final Logger logger = LoggerFactory.getLogger(WsAndHttpJWTChannelizer.class);

  private WsAndHttpJWTChannelizerHandler handler;
  private AbstractAuthenticationHandler authenticationHandler;

  @Override
  public void init(final ServerGremlinExecutor serverGremlinExecutor) {
    super.init(serverGremlinExecutor);
    handler = new WsAndHttpJWTChannelizerHandler();
    handler.init(serverGremlinExecutor, new HttpGremlinEndpointHandler(serializers, gremlinExecutor, graphManager, settings));
    authenticator = new JWTToKerberosAuthenticator();

    if (authenticator != null)
      authenticationHandler = authenticator.getClass() == AllowAllAuthenticator.class ?
          null : instantiateAuthenticationHandler(settings.authentication);

  }

  @Override
  public void configure(final ChannelPipeline pipeline) {
    handler.configure(pipeline);
    pipeline.addAfter(PIPELINE_HTTP_REQUEST_DECODER, "WsAndHttpChannelizerHandler", handler);
  }

  private AbstractAuthenticationHandler instantiateAuthenticationHandler(final Settings.AuthenticationSettings authSettings) {
    final String authenticationHandler = authSettings.authenticationHandler;
    if (authenticationHandler == null) {
      //Keep things backwards compatible
      return new WsAndHttpJWTAuthenticationHandler(authenticator, authSettings);
    } else {
      return createAuthenticationHandler(authSettings);
    }
  }

}
