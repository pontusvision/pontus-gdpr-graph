package com.pontusvision.gdpr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.UserProvider;
import org.apache.hadoop.security.UserGroupInformation;
import org.janusgraph.diskstorage.hbase.ConnectionMask;
import org.janusgraph.diskstorage.hbase.HBaseCompat1_0;
import org.janusgraph.diskstorage.hbase.HConnection1_0;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;

public class HBaseMultiUserCompat1_0 extends HBaseCompat1_0
{
  @Override
  public ConnectionMask createConnection(Configuration conf) throws IOException
  {
    String proxyUser = conf.get("storage.hbase.proxy_user");
    String proxyPass = conf.get("storage.hbase.proxy_pass");

    try
    {
      LoginContext lc = JWTToKerberosAuthenticator.kinit(proxyUser, proxyPass);
      lc.login();
      UserProvider provider = UserProvider.instantiate(conf);
      User user = provider.create(UserGroupInformation.getUGIFromSubject(lc.getSubject()));

      Connection conn = ConnectionFactory.createConnection(conf, user);

      return new HConnection1_0(conn);
    }

    catch (LoginException e)
    {
      e.printStackTrace();

    }

    return new HConnection1_0(ConnectionFactory.createConnection(conf));

  }
}
