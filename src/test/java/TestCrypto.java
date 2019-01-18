import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.Base58;
import crypto.Ed25519;
import crypto.KeyPair;
import crypto.Secp256k1;
import model.transaction.Signature;
import model.transaction.SignatureAdapter;
import model.transaction.Transaction;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;


import java.io.IOException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertEquals;

public class TestCrypto {
    @Test
    public void testSecp() throws IOException {

        byte[] seckey = Base58.decode("EhNiaU4DzUmjCrvynV3gaUeuj2VjB1v2DCmbGD5U2nSE");
        Secp256k1 s = new Secp256k1(seckey);

        assertEquals("02069b1abdaaf8326e7e6fc15607fd9e6c6f595e119a2484a4192b5d6b3878dd25", Hex.toHexString(s.pubkey()));
        byte[] info = (new SHA3.Digest256()).digest("hello".getBytes());
        assertEquals("3338be694f50c5f338814986cdf0686453a888b84f424d792af4b9202398f392", Hex.toHexString(info));
        Signature signature = s.sign(info);
        assertEquals("37f7731bdb9988aa330be8bbd6a791f11d9fe90601edf7f569f6b8ddcfdd7755333b299ca3a8437e71eb14c72a02a2c5cdf3562e891f5a3c1d54d92148236e10",
                Hex.toHexString(signature.signature));

        KeyPair s2 = new Secp256k1();
        System.out.println(s2.B58PubKey());
    }

    @Test
    public void testEd25519() throws InvalidKeySpecException, IOException {
        byte[] seckey = Base58.decode("1rANSfcRzr4HkhbUFZ7L1Zp69JZZHiDDq5v7dNSbbEqeU4jxy3fszV4HGiaLQEyqVpS1dKT9g7zCVRxBVzuiUzB");

        Ed25519 e = new Ed25519(seckey);
        assertEquals("5731adeb5d1a807ec9c43825389e5edff70412e4643a94629a652af1bfcf2f08", Hex.toHexString(e.pubkey()));

        byte[] info = (new SHA3.Digest256()).digest("hello".getBytes());
        assertEquals("3338be694f50c5f338814986cdf0686453a888b84f424d792af4b9202398f392", Hex.toHexString(info));
        Signature signature = e.sign(info);
        assertEquals("eb436f1af8502d454f8bcea472bac4015f7827d7f90427ce22787f14bf98b83d2c2b4d1b303ad4201b1526a1a269f069dcbf2887a636799b8e733acd2fd75308",
                Hex.toHexString(signature.signature));

        KeyPair s2 = new Ed25519();
        System.out.println(s2.B58SecKey());
    }

    @Test
    public void testTransaction() throws IOException, InvalidKeySpecException {

        Transaction tx = new Transaction();
        tx.time = 1544013436179000000L;
        tx.expiration = 1544013526179000000L;
        tx.gas_ratio = 1L;
        tx.gas_limit = 1234L;
        tx.delay = 0;
        tx.chain_id = 0;

        tx.addSigner("abc");

        tx.addAction("cont", "abi");

        tx.addApprove("iost", "123");

        assertEquals("156d700a27e12ac0156d701f1c4c2ec00000000000000064000000000001e2080000000000000000000000000000000100000003616263000000010000001500000004636f6e7400000003616269000000025b5d000000010000000f00000004696f737400000003313233",
                Hex.toHexString(tx.signBytes()));

        assertEquals("5b75a2e0a5e7c8c462604d90df60893e9714702bd5c0dd1448dd017ed4aa0bc4", Hex.toHexString(tx.getSignHash()));

        Keychain kc = new Keychain("abc");
        kc.addKey("def", new Ed25519(Base58.decode("1rANSfcRzr4HkhbUFZ7L1Zp69JZZHiDDq5v7dNSbbEqeU4jxy3fszV4HGiaLQEyqVpS1dKT9g7zCVRxBVzuiUzB")));
        kc.sign(tx, "def");
        assertEquals("156d700a27e12ac0156d701f1c4c2ec00000000000000064000000000001e2080000000000000000000000000000000100000003616263000000010000001500000004636f6e7400000003616269000000025b5d000000010000000f00000004696f73740000000331323300000001000000690200000040b41b996ea0a47c0a14dd5d6e473828dea4966bc1d8823205b7af6b6ca19626f46c1e86957a6fa2510bb1bf42125368c4add823bcba9c56eb888b5cd23f544203000000205731adeb5d1a807ec9c43825389e5edff70412e4643a94629a652af1bfcf2f08",
                Hex.toHexString(tx.getPublishBytes()));
        assertEquals("1990bf1492c2f9d0ae57c5350a4fe9517ee6889808e534f81f2b28549ebe81fb", Hex.toHexString(tx.getPublishHash()));

        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Signature.class, new SignatureAdapter());

        System.out.println(gb.create().toJson(tx));
    }
}
