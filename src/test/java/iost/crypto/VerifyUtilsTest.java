package iost.crypto;

import iost.model.transaction.Signature;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VerifyUtilsTest {
    @Test
    public void ed25519Verify() throws Exception {

        byte[] seckey = Base58.decode("1rANSfcRzr4HkhbUFZ7L1Zp69JZZHiDDq5v7dNSbbEqeU4jxy3fszV4HGiaLQEyqVpS1dKT9g7zCVRxBVzuiUzB");

        Ed25519 e = new Ed25519(seckey);
        byte[] pubKey = e.pubkey();
        assertEquals("5731adeb5d1a807ec9c43825389e5edff70412e4643a94629a652af1bfcf2f08", Hex.toHexString(pubKey));

        byte[] info = (new SHA3.Digest256()).digest("hello".getBytes());
        assertEquals("3338be694f50c5f338814986cdf0686453a888b84f424d792af4b9202398f392", Hex.toHexString(info));
        Signature signature = e.sign(info);
        assertEquals("eb436f1af8502d454f8bcea472bac4015f7827d7f90427ce22787f14bf98b83d2c2b4d1b303ad4201b1526a1a269f069dcbf2887a636799b8e733acd2fd75308",
                Hex.toHexString(signature.signature));
        assertTrue(e.verify(info, signature.signature));

        // verify without seckey
        assertTrue(VerifyUtils.Ed25519Verify(info, signature.signature, pubKey));
    }

    @Test
    public void secp256k1Verify() throws Exception {
        byte[] seckey = Base58.decode("EhNiaU4DzUmjCrvynV3gaUeuj2VjB1v2DCmbGD5U2nSE");
        Secp256k1 s = new Secp256k1(seckey);
        byte[] pubKey = s.pubkey();
        assertEquals("02069b1abdaaf8326e7e6fc15607fd9e6c6f595e119a2484a4192b5d6b3878dd25", Hex.toHexString(pubKey));
        byte[] info = (new SHA3.Digest256()).digest("hello".getBytes());
        Signature signature = s.sign(info);
        assertEquals("37f7731bdb9988aa330be8bbd6a791f11d9fe90601edf7f569f6b8ddcfdd7755333b299ca3a8437e71eb14c72a02a2c5cdf3562e891f5a3c1d54d92148236e10",
                Hex.toHexString(signature.signature));

        assertTrue(s.verify(info, signature.signature));

        // verify without seckey
        assertTrue(VerifyUtils.Secp256k1Verify(info, signature.signature, pubKey));
    }

}