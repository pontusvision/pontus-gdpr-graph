package uk.gov.cdp.shadow.user.auth.util;

import static uk.gov.cdp.shadow.user.auth.util.PropertiesUtil.property;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyStoreCreator {

  private static final String KEY_ALGO = "AES";
  private static final String KEYSTORE_TYPE = "JCEKS";

  public static void main(String[] args) throws Exception {

    String keyStoreLocation = property("shadow.user.keystore.location");
    String keyStorePassword = property("shadow.user.keystore.pwd");
    String keyAlias = property("shadow.user.key.alias");
    String keyPassword = property("shadow.user.key.pwd");

    File file = new File(keyStoreLocation);
    if (file.exists()) {
      System.out.println("Key store already exists.");
      System.out.println("Exiting...");
      System.exit(0);
    }

    KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
    keyStore.load(null, keyStorePassword.toCharArray());
    saveKeyStore(keyStore, keyStoreLocation, keyStorePassword);

    SecretKey secretKey = getSecretKey();
    KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);

    KeyStore.ProtectionParameter entryPassword =
        new KeyStore.PasswordProtection(keyPassword.toCharArray());
    keyStore.setEntry(keyAlias, secretKeyEntry, entryPassword);

    saveKeyStore(keyStore, keyStoreLocation, keyStorePassword);
  }

  private static void saveKeyStore(
      KeyStore keyStore, String keyStoreLocation, String keyStorePassword) throws Exception {
    try (FileOutputStream keyStoreOutputStream = new FileOutputStream(keyStoreLocation)) {
      keyStore.store(keyStoreOutputStream, keyStorePassword.toCharArray());
    }
  }

  private static SecretKey getSecretKey() throws NoSuchAlgorithmException {
    KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGO);
    keyGen.init(128);
    return keyGen.generateKey();
  }
}
