package com.alliander.osgp.shared.security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Helper routines for certificate operations.
 */
public final class CertificateHelper {

    /**
     * private ctor
     */
    private CertificateHelper() {

    }

    /**
     * Create private key from private key file on disk
     * 
     * @param keyPath
     *            path to key
     * @param keyType
     *            type of key
     * @return instance of public key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws IOException
     * @throws NoSuchProviderException
     */
    public static PrivateKey createPrivateKey(final String keyPath, final String keyType, final String provider)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchProviderException {
        final byte[] key = readKeyFromDisk(keyPath);

        final PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory privateKeyFactory;
        privateKeyFactory = KeyFactory.getInstance(keyType, provider);
        return privateKeyFactory.generatePrivate(privateKeySpec);
    }

    /**
     * Create public key from public key file on disk
     * 
     * @param keyPath
     *            path to key
     * @param keyType
     *            type of key
     * @return instance of public key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws IOException
     * @throws NoSuchProviderException
     */
    public static PublicKey createPublicKey(final String keyPath, final String keyType, final String provider)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchProviderException {
        final byte[] key = readKeyFromDisk(keyPath);

        final X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key);
        final KeyFactory publicKeyFactory = KeyFactory.getInstance(keyType, provider);
        return publicKeyFactory.generatePublic(publicKeySpec);
    }

    public static PrivateKey createPrivateKeyFromBase64(final String keyBase64, final String keyType,
            final String provider) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException,
            NoSuchProviderException {
        final byte[] key = Base64.decodeBase64(keyBase64);

        final PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory privateKeyFactory;
        privateKeyFactory = KeyFactory.getInstance(keyType, provider);
        return privateKeyFactory.generatePrivate(privateKeySpec);
    }

    public static PublicKey createPublicKeyFromBase64(final String keyBase64, final String keyType,
            final String provider) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException,
            NoSuchProviderException {
        final byte[] key = Base64.decodeBase64(keyBase64);

        final X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key);
        final KeyFactory publicKeyFactory = KeyFactory.getInstance(keyType, provider);
        return publicKeyFactory.generatePublic(publicKeySpec);
    }

    /**
     * Read certificate bytes from disk
     * 
     * @param keyPath
     * @return bytes of key
     * @throws IOException
     */
    private static byte[] readKeyFromDisk(final String keyPath) throws IOException {
        final File privKeyFile = new File(keyPath);
        final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(privKeyFile));
        final byte[] privateKey = new byte[(int) privKeyFile.length()];
        bis.read(privateKey);
        bis.close();

        return privateKey;
    }
}