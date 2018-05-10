PVGDPR_DISABLE_HADOOP_CLASSPATH_LOOKUP=true
PVGDPR_CLASSPATH=/opt/pontus/pontus-hbase/current/conf
#PVGDPR_OPTS="-Djava.security.auth.login.config=/opt/graphdb/current/conf/jaas.conf -Dsun.security.krb5.debug=true"
PVGDPR_OPTS="-Djava.security.auth.login.config=/opt/pontus/pontus-graph/current/conf/jaas.conf"
PVGDPR_DISABLE_HADOOP_CLASSPATH_LOOKUP=true
PVGDPR_CLASSPATH=/opt/graphdb/default/current/conf
#PVGDPR_OPTS="-Djava.security.auth.login.config=/opt/graphdb/default/current/conf/jaas.conf -Dsun.security.krb5.debug=true"
#PVGDPR_OPTS=" \
  #-Djava.security.auth.login.config=/opt/pontus/pontus-graph/current/conf/jaas.conf \
  #-Dldap.create.user=false \
  #-Dkerberos.authentication=true \
  #-Dldap.protocol=ldap \
  #-Dldap.server.hostname=127.0.0.1 \
  #-Dldap.port=389 \
  #-Dldap.domain.name=pontusvision.com \
  #-Dldap.domain.root=CN=Users,DC=pontusvision,DC=com \
  #-Dldap.user.group=CN=Administrators,CN=Builtin,DC=pontusvision,DC=com \
  #-Dldap.admin.user=Administrator \
  #-Dldap.admin.user.pwd=pa55wordpa55wordPASSWD999 \
  #-Dldap.create.user=false \
  #-Dshadow.user.keystore.location=/etc/pki/java/jwt_keystore.jks \
  #-Dshadow.user.keystore.pwd= \
  #-Dshadow.user.key.alias=jwt_keystore \
  #-Dshadow.user.key.pwd= \
  #-Dshadow.user.key.store.type=JKS \
  #-Dshadow.user.key.algo=HmacSHA512 \
  #-Dshadow.user.salt.password.enable=false \
  #-Dkerberos.authentication=true"
#
CLASS=com.pontusvision.gdpr.App
#CLASS=uk.gov.cdp.pole.bootstrap.Bootstrap
