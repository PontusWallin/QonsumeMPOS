package com.smoothsys.qonsume_pos.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

/**
 * Created by Pontu on 2017-05-29.
 * Refactored 18-02-22
 */

public class EncryptionServices {

    private static KeyStore keyStore = null;

    public static String getPassword(String encPassword) {

        if(keyStore == null) {
            try {
                setupKeyStore();

            } catch (CertificateException e) {
                
                e.printStackTrace();
                return "";
            } catch (NoSuchAlgorithmException e) {
                
                e.printStackTrace();
                return "";
            } catch (IOException e) {
                
                e.printStackTrace();
                return "";
            } catch (KeyStoreException e) {
                
                e.printStackTrace();

                return fallback_decryptPassword(encPassword);
            }
        }

        try {
            return decryptString(encPassword);

        } catch (UnrecoverableEntryException e) {
            
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            
            e.printStackTrace();
        } catch (KeyStoreException e) {
            
            e.printStackTrace();
        }
        return "";
    }

    public static void encryptPassword(String password, Context context) {
        try {
            initKeyStore();
        } catch (Exception e) {
            
            fallback_encryptPassword(password, PreferenceManager.getDefaultSharedPreferences(context));
           return;
        }
        createKeyIfNotExists(context);
        encryptAndStorePassword(password, PreferenceManager.getDefaultSharedPreferences(context));
    }

    private static void initKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        // get keystore

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

    }

    private static void createKeyIfNotExists(Context context) {
        try {
            if(!keyStore.containsAlias("PasswordKeyPOS")){
                //If not, create it!
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);

                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context).setAlias("PasswordKeyPOS").setSubject(new X500Principal("CN=QonsumePOS, O=SmoothSys"))
                        .setSerialNumber(BigInteger.ONE).setStartDate(start.getTime()).setEndDate(end.getTime()).build();

                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA","AndroidKeyStore");
                generator.initialize(spec);

                // - DO NOT REMOVE THIS LINE -
                KeyPair keyPair = generator.generateKeyPair();
            }
        } catch (InvalidAlgorithmParameterException | NoSuchProviderException | NoSuchAlgorithmException | KeyStoreException e) {
            
            e.printStackTrace();
        }
    }

    private static void encryptAndStorePassword(String password, SharedPreferences prefs) {

        if(!prefs.contains("_password")) {
            try {

                ByteArrayOutputStream outputStream = createOutputStream(password);

                byte[] vals = outputStream.toByteArray();
                String encryptedPassword = Base64.encodeToString(vals, Base64.DEFAULT);
                prefs.edit().putString("_password",encryptedPassword).commit();

            } catch (IOException | InvalidKeyException | NoSuchProviderException | NoSuchPaddingException | KeyStoreException | UnrecoverableEntryException | NoSuchAlgorithmException e) {
                
                e.printStackTrace();
            }
        }
    }

    private static ByteArrayOutputStream createOutputStream(String password) throws IOException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, UnrecoverableEntryException, KeyStoreException {

        Cipher cipher = createCipherEnc();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream,cipher);
        cipherOutputStream.write(password.getBytes("UTF-8"));
        cipherOutputStream.close();

        return outputStream;
    }

    private static String decryptString(String s) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {

        CipherInputStream cipherInputStream = setupCipherStream(s);

        ArrayList<Byte> values = grabDataFromInputStream(cipherInputStream);
        byte[] bytes = putValuesIntoByteArray(values);

        try {
            return new String(bytes, 0,bytes.length, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            
            e.printStackTrace();
        }
        return "";
    }

    private static ArrayList<Byte> grabDataFromInputStream(CipherInputStream stream) {
        int nextByte;
        ArrayList<Byte> values = new ArrayList<>();
        try {
            while ((nextByte = stream.read()) != -1) {
                values.add((byte)nextByte);
            }
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        return values;
    }

    private static byte[] putValuesIntoByteArray(ArrayList<Byte> values) {
        byte[] bytes = new byte[values.size()];
        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i).byteValue();
        }
        return bytes;
    }

    private static void setupKeyStore() throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

    }

    private static CipherInputStream setupCipherStream(String encPassword) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {

        byte[] decodedPass = Base64.decode(encPassword, Base64.DEFAULT);

        Cipher c = createCipherDec();
        if( c== null) {
            throw  new KeyStoreException("private key entry is null");
        }
        return new CipherInputStream( new ByteArrayInputStream(decodedPass), c);
    }

    private static Cipher createCipherDec() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {

        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry("PasswordKeyPOS", null);

        if(privateKeyEntry == null) {
            return null;
        }

        Cipher output = null;
        try {
            output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException e) {
            
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            
            e.printStackTrace();
        }

        try {
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
        } catch (InvalidKeyException e) {
            
            e.printStackTrace();
        }

        return output;
    }

    private static Cipher createCipherEnc() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, UnrecoverableEntryException, KeyStoreException {

        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry("PasswordKeyPOS", null);
        RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher;
    }


    // == Fallbacks ==
    private static final String ALGORITHM = ("AES");

    private static final String KEY = "1Hbfh667adfDEJ78";

    private static void fallback_encryptPassword(String password, SharedPreferences prefs) {

        try {
            // == Generate Key ==
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // == Encrypts bytes ==
            byte[] encryptedByteValues = cipher.doFinal(password.getBytes("utf-8"));
            String encryptedValue = Base64.encodeToString(encryptedByteValues, Base64.DEFAULT);

            // == Save to preferences ==
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("_password", encryptedValue);
            editor.commit();
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }

    private static String fallback_decryptPassword(String password) {

        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decryptedValue64 = Base64.decode(password, Base64.DEFAULT);
            byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
            String decryptedValue = new String(decryptedByteValue,"utf-8");
            return decryptedValue;
        } catch (Exception e) {
            
            e.printStackTrace();
            return "";
        }
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(KEY.getBytes(),ALGORITHM);
        return key;
    }

}
