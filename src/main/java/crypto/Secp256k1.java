package crypto;

import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import model.transaction.Signature;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

public class Secp256k1 extends KeyPair {
    private ECKeyPair kp;

    public Secp256k1(byte[] seckey)  {
        this.kp = ECKeyPair.create(seckey);
    }

    public Secp256k1() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA", "BC");
            ECGenParameterSpec ecsp;
            ecsp = new ECGenParameterSpec(Algorithm.Secp256k1.toString());
            kpg.initialize(ecsp);

            kp = ECKeyPair.create(kpg.generateKeyPair());
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
        signature.algorithm = Algorithm.Secp256k1.toByte();
        return signature;
    }

    @Override
    public byte[] pubkey() {
        ByteBuffer bb = ByteBuffer.allocate(33);
        bb.put((byte) 0x02);
        bb.put(Arrays.copyOf(this.kp.getPublicKey().toByteArray(), 32));
        return bb.array();
    }

    @Override
    public byte[] seckey() {
        return this.kp.getPrivateKey().toByteArray();
    }

}
