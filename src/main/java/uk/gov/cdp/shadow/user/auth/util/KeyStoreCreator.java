package uk.gov.cdp.shadow.user.auth.util;

import static uk.gov.cdp.shadow.user.auth.util.PropertiesUtil.property;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyStoreCreator {

    private static final String KEY_ALGO = "HmacSHA512";
    private static final String KEYSTORE_TYPE = "JCEKS";
    private static final String keyStoreLocation = property("shadow.user.keystore.location");
    private static final String keyStorePassword = property("shadow.user.keystore.pwd");
    private static final String keyAlias = property("shadow.user.key.alias");
    private static final String keyPassword = property("shadow.user.key.pwd");
    private static final String keySize = property("shadow.user.key.size");
    private static final String keyAlgo = property("shadow.user.key.algo", KEY_ALGO);
    private static final String keyStoreType = property("shadow.user.key.store.type", KEYSTORE_TYPE);


    public static void main(String[] args) throws Exception {



    File file = new File(keyStoreLocation);
    if (file.exists()) {
      System.out.println("Key store already exists.");
      System.out.println("Exiting...");
      System.exit(0);
    }

    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
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
      KeyGenerator keyGen = KeyGenerator.getInstance(keyAlgo);
      keyGen.init(Integer.parseInt(keySize));
    return keyGen.generateKey();
  }
}
