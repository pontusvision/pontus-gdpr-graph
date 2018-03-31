package com.pontusvision.gdpr;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.util.ReferenceCountUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.apache.tinkerpop.gremlin.server.auth.Authenticator;
import org.apache.tinkerpop.gremlin.server.handler.AbstractAuthenticationHandler;
import org.apache.tinkerpop.gremlin.server.handler.HttpBasicAuthenticationHandler;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.pontus.JWTClaim;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.apache.tinkerpop.gremlin.groovy.jsr223.dsl.credential.CredentialGraphTokens.PROPERTY_PASSWORD;
import static org.apache.tinkerpop.gremlin.groovy.jsr223.dsl.credential.CredentialGraphTokens.PROPERTY_USERNAME;

@ChannelHandler.Sharable

public class WsAndHttpJWTAuthenticationHandler extends AbstractAuthenticationHandler
{
  private static final Logger logger = LoggerFactory.getLogger(HttpBasicAuthenticationHandler.class);
  private static final Logger auditLogger = LoggerFactory.getLogger(GremlinServer.AUDIT_LOGGER_NAME);
  private final Settings.AuthenticationSettings authenticationSettings;

  private final Base64.Decoder decoder = Base64.getUrlDecoder();
  private SSLContextService sslContextService = new SSLContextService();
  public static final String JWT_ZK_PATH = "jwt_store.zk.path";
  public static final String JWT_ZK_PATH_DEFVAL = "/jwt/users";
  public static final String JWT_ZK_SESSION_TIMEOUT = "jwt_store.zk.sessionTimeoutMs";
  public static final String JWT_ZK_CONNECT_STRING = "jwt_store.zk.connectString";
  public static final String JWT_ZK_CONNECT_STRING_DEFVAL = "sandbox.hortonworks.com";
  public static final String JWT_SECURITY_CLAIM_CACHE_INIT_SIZE = "jwt_store.security_claim_cache.initSizeBytes";
  public static final String JWT_SECURITY_CLAIM_CACHE_LOAD_FACTOR = "jwt_store.security_claim_cache.loadFactor";
  public static final String JWT_SECURITY_CLAIM_CACHE_MAX_SIZE = "jwt_store.security_claim_cache.maxSizeBytes";
  public static final int JWT_SECURITY_CLAIM_CACHE_INIT_SIZE_DEFVAL = 20000;
  public static final float JWT_SECURITY_CLAIM_CACHE_LOAD_FACTOR_DEFVAL = 0.75F;
  public static final long JWT_SECURITY_CLAIM_CACHE_MAX_SIZE_DEFVAL = 50000000L;

  public String zookeeperConnStr = "localhost";
  public String zookeeperPrincipal = "";
  public String zookeeperKeytab = "";

  public WsAndHttpJWTAuthenticationHandler(final Authenticator authenticator,
                                           final Settings.AuthenticationSettings authenticationSettings)
  {
    super(authenticator);
    this.authenticationSettings = authenticationSettings;

    if (this.authenticationSettings != null && this.authenticationSettings.config != null)
    {
      this.zookeeperConnStr =
          (String) this.authenticationSettings.config.get("zookeeperConnStr");

      this.zookeeperPrincipal = (String) this.authenticationSettings.config.get("zookeeperPrincipal");
      this.zookeeperKeytab = (String) this.authenticationSettings.config.get("zookeeperKeytab");

      //zookeeperPrincipal
      //zookeeperKeytab
    }

  }

  public static Key getPublicKey(SSLContextService sslService, String alias)
      throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException
  {
    FileInputStream is = new FileInputStream(sslService.getKeyStoreFile());

    KeyStore keystore = KeyStore.getInstance(sslService.getKeyStoreType());
    keystore.load(is, sslService.getKeyStorePassword().toCharArray());

    //    String alias = sslService.getIdentifier(); //"myalias";

    Key key = keystore.getKey(alias, sslService.getKeyPassword().toCharArray());
    if (key instanceof PrivateKey)
    {
      // Get certificate of public key
      java.security.cert.Certificate cert = keystore.getCertificate(alias);

      // Get public key
      PublicKey publicKey = cert.getPublicKey();

      return publicKey;
      // Return a key pair
      //      new KeyPair(publicKey, (PrivateKey) key);
    }

    return null;

  }

  public static JWSVerifier getVerifier(JWSAlgorithm algo, Key key) throws JOSEException
  {

    if (JWSAlgorithm.Family.EC.contains(algo))
    {

      if (key instanceof ECPublicKey)
      {
        return new ECDSAVerifier((ECPublicKey) key);

      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the ECDSASigner; note that DSA Keys are not currently supported by this processor.");
      }

    }

    else if (JWSAlgorithm.Family.RSA.contains(algo))
    {
      if (key instanceof RSAPublicKey)
      {
        return new RSASSAVerifier((RSAPublicKey) key);
      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the RSASSASigner; note that DSA Keys are not currently supported by this processor.\n\n"
            + "  Use the following to create an RSA Key:\n\n"
            + "     keytool -genkeypair -alias jwtkey -keyalg RSA -dname \"CN=Server,OU=Unit,O=Organization,L=City,S=State,C=US\" -keypass pa55word -keystore kafka.client.keystore.jks -storepass pa55word\n");
      }

    }
    else if (JWSAlgorithm.Family.HMAC_SHA.contains(algo))
    {
      if (key instanceof SecretKey)
      {
        return new MACVerifier((SecretKey) key);
      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the MACSigner; note that DSA Keys are not currently supported by this processor.");
      }
    }

    return null;
  }

  public static JWSSigner getSigner(JWSAlgorithm algo, Key key) throws JOSEException
  {

    if (JWSAlgorithm.Family.EC.contains(algo))
    {

      if (key instanceof ECPrivateKey)
      {
        return new ECDSASigner((ECPrivateKey) key);

      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the ECDSASigner; note that DSA Keys are not currently supported by this processor.");
      }

    }

    else if (JWSAlgorithm.Family.RSA.contains(algo))
    {
      if (key instanceof PrivateKey)
      {
        return new RSASSASigner((PrivateKey) key);
      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the RSASSASigner; note that DSA Keys are not currently supported by this processor.\n\n"
            + "  Use the following to create an RSA Key:\n\n"
            + "     keytool -genkeypair -alias jwtkey -keyalg RSA -dname \"CN=Server,OU=Unit,O=Organization,L=City,S=State,C=US\" -keypass pa55word -keystore kafka.client.keystore.jks -storepass pa55word\n");
      }

    }
    else if (JWSAlgorithm.Family.HMAC_SHA.contains(algo))
    {
      if (key instanceof SecretKey)
      {
        return new MACSigner((SecretKey) key);
      }
      else
      {
        throw new JOSEException("Invalid Key Type " + key.getAlgorithm()
            + " not supported by the MACSigner; note that DSA Keys are not currently supported by this processor.");
      }
    }

    return null;
  }

  String zkPath = JWT_ZK_PATH_DEFVAL;
  ZooKeeper zoo = null;

  // Method to disconnect from zookeeper server
  public void close() throws InterruptedException
  {
    zoo.close();
  }

  // Method to connect zookeeper ensemble.
  public static ZooKeeper connect(String host) throws IOException, InterruptedException
  {

    final CountDownLatch connectedSignal = new CountDownLatch(1);
    ZooKeeper zoo = null;

    zoo = new ZooKeeper(host, 5000, new Watcher()
    {
      public void process(WatchedEvent we)
      {
        if (we.getState() == Event.KeeperState.SyncConnected)
        {
          connectedSignal.countDown();
        }
      }
    });

    connectedSignal.await();
    return zoo;
  }

  public void create(String path, byte[] data) throws KeeperException, InterruptedException
  {

    String[] parts = path.split("/");
    StringBuffer strBuf = new StringBuffer();
    // LPPM - the first level is empty, as we start with a slash.
    // skip it by setting i and j = 1.
    for (int i = 1, ilen = parts.length, lastItem = parts.length - 1; i < ilen; i++)
    {
      strBuf.setLength(0);
      for (int j = 1; j <= i; j++)
      {
        strBuf.append("/").append(parts[j]);
      }
      String partialPath = strBuf.toString();
      if (exists(partialPath) == null)
      {
        CreateMode mode;
        if (i == lastItem)
        {
          mode = CreateMode.PERSISTENT;
        }
        else
        {
          mode = CreateMode.PERSISTENT;
        }
        List<ACL> perms = new ArrayList<>();
        if (UserGroupInformation.isSecurityEnabled())
        {
          perms.add(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.AUTH_IDS));
          perms.add(new ACL(ZooDefs.Perms.READ, ZooDefs.Ids.ANYONE_ID_UNSAFE));
        }
        else
        {
          perms.add(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE));
        }

        zoo.create(partialPath, data, perms, mode);

      }
    }
  }

  public Stat exists(String path) throws KeeperException, InterruptedException
  {
    return zoo.exists(path, true);
  }

  public void update(String path, byte[] data) throws KeeperException, InterruptedException
  {
    zoo.setData(path, data, zoo.exists(path, true).getVersion());
  }

  @Override public void channelRead(final ChannelHandlerContext ctx, final Object msg)
  {
    if (msg instanceof FullHttpMessage)
    {
      final FullHttpMessage request = (FullHttpMessage) msg;
      if (!request.headers().contains("Authorization"))
      {
        sendError(ctx, msg);
        return;
      }

      // strip off "Basic " from the Authorization header (RFC 2617)
      String basic = "Bearer ";
      final String jwt = "JWT ";
      final String authorizationHeader = request.headers().get("Authorization");
      if (!authorizationHeader.startsWith(basic))
      {
        if (!authorizationHeader.startsWith(jwt))
        {
          sendError(ctx, msg);
          return;
        }
        basic = jwt;
      }

      final String jwtStr = authorizationHeader.substring(basic.length());
      final String keyAlias = "localhost";

      //      JWSSigner signer = getSigner(keyAlgo,key);

      //To parse the JWS and verify it, e.g. on client-side
      JWSObject jwsObject = null;
      try
      {
        Key key = getPublicKey(sslContextService, keyAlias);

        jwsObject = JWSObject.parse(jwtStr);

        JWSAlgorithm keyAlgo = JWSAlgorithm.parse(JWSAlgorithm.RS512.toString());

        JWSVerifier verifier = getVerifier(keyAlgo, key);

        if (!jwsObject.verify(verifier))
        {
          sendError(ctx, msg);

          auditLogger.error("Failed to verify the JWT with the supplied key.");
          return;

        }

        JWTClaim sampleClaim = JWTClaim.fromJson(jwsObject.getPayload().toString());
        final Map<String, String> credentials = new HashMap<>();
        credentials.put(PROPERTY_USERNAME, sampleClaim.getSub());
        credentials.put(PROPERTY_PASSWORD, jwtStr);

        authenticator.authenticate(credentials);



        StringBuffer strBuf = new StringBuffer(JWT_ZK_PATH_DEFVAL).append("/").append(sampleClaim.getSub());

        if (this.zoo == null)
        {
          UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(zookeeperPrincipal,zookeeperKeytab);
          ugi.checkTGTAndReloginFromKeytab();
          PrivilegedExceptionAction<ZooKeeper> action = () -> connect(zookeeperConnStr);
          this.zoo = ugi.doAs(action);

        }

        if (this.exists(strBuf.toString()) == null)
        {
          this.create(strBuf.toString(), jwsObject.getPayload().toBytes());
        }
        else
        {
          this.update(strBuf.toString(), jwsObject.getPayload().toBytes());
        }

        this.close();


        ctx.fireChannelRead(request);

        // User name logged with the remote socket address and authenticator classname for audit logging
        if (authenticationSettings.enableAuditLog)
        {
          String address = ctx.channel().remoteAddress().toString();
          if (address.startsWith("/") && address.length() > 1)
            address = address.substring(1);
          final String[] authClassParts = authenticator.getClass().toString().split("[.]");
          auditLogger.info("User {} with address {} authenticated by {}", credentials.get(PROPERTY_USERNAME), address,
              authClassParts[authClassParts.length - 1]);
        }
      }
      catch (Exception ae)
      {
        this.zoo = null;

        sendError(ctx, msg);
      }
    }
  }

  private void sendError(final ChannelHandlerContext ctx, final Object msg)
  {
    // Close the connection as soon as the error message is sent.
    ctx.writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, UNAUTHORIZED)).addListener(ChannelFutureListener.CLOSE);
    ReferenceCountUtil.release(msg);
  }

  private static class SSLContextService
  {
    String keyStoreFile = "/etc/pki/java/keystore.jks";
    String keyStoreType = "JKS";
    String keyStorePassword = "pa55word";
    String keyPassword = "pa55word";

    public String getKeyStoreFile()
    {
      return keyStoreFile;
    }

    public String getKeyStoreType()
    {
      return keyStoreType;
    }

    public String getKeyStorePassword()
    {
      return keyStorePassword;
    }

    public String getKeyPassword()
    {
      return keyPassword;
    }
  }
}
