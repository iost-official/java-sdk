package crypto;

import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import model.transaction.Signature;
import org.web3j.crypto.Keys;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

/**
 * secp256k1 key pair
 */
public class Secp256k1 extends KeyPair {
    private ECKeyPair kp;

    /**
     * new key pair with given private key
     * @param seckey - private key
     */
    public Secp256k1(byte[] seckey)  {
        this.kp = ECKeyPair.create(seckey);
    }

    /**
     * new key pair with crypto-safe random key
     */
    public Secp256k1() {
        try {
            kp = Keys.createEcKeyPair();
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Signature sign(byte[] info) {
        ECDSASignature sig = this.kp.sign(info).toCanonicalised();

        ByteBuffer bb = ByteBuffer.allocate(64);
        bb.put(sig.r.toByteArray());
        bb.put(sig.s.toByteArray());

        Signature signature = new Signature();
        signature.signature = bb.array();
        signature.public_key = this.pubkey();
        signature.algorithm = Algorithm.Secp256k1;
        return signature;
    }

    @Override
    byte[] pubkey() {
        ByteBuffer bb = ByteBuffer.allocate(33);
        bb.put((byte) 0x02);
        bb.put(Arrays.copyOf(this.kp.getPublicKey().toByteArray(), 32));
        return bb.array();
    }

    @Override
    byte[] seckey() {
        return this.kp.getPrivateKey().toByteArray();
    }

}
