package iost.crypto;

import iost.model.transaction.Signature;

import java.util.Arrays;

/**
 * Ed25519 key pair
 */
public class Ed25519 extends KeyPair {
    private byte[] privateKey;
    private byte[] publicKey;

    /**
     * new key pair with crypto-safe random key
     */
    public Ed25519() {
        publicKey = new byte[TweetNaCl.SIGN_PUBLIC_KEY_BYTES];
        privateKey = new byte[TweetNaCl.SIGN_SECRET_KEY_BYTES];
        TweetNaCl.crypto_sign_keypair(publicKey, privateKey, false);
    }

    /**
     * new key pair with given private key
     * @param seckey - private key
     */
    public Ed25519(byte[] seckey){
        int l = 32;
        if (seckey.length < 32) {
            l = seckey.length;
        }
        publicKey = new byte[TweetNaCl.SIGN_PUBLIC_KEY_BYTES];
        privateKey = new byte[TweetNaCl.SIGN_SECRET_KEY_BYTES];
        System.arraycopy(seckey, 0, privateKey, 0, l);
        TweetNaCl.crypto_sign_keypair(publicKey, privateKey, true);
    }


    @Override
    public Signature sign(byte[] info) {
        byte[]sig = TweetNaCl.crypto_sign(info, this.privateKey);

        Signature signature = new Signature();
        signature.signature = sig;
        signature.algorithm = Algorithm.Ed25519;
        signature.public_key = this.pubkey();

        return signature;
    }

    @Override
    public byte[] pubkey() {
        return this.publicKey;
    }

    @Override
    public byte[] seckey() {
        return this.privateKey;
    }

    @Override
    public boolean verify(byte[] hash, byte[] signature) {
        throw new RuntimeException("not implement yet");
    }
}
