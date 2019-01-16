import crypto.Base58;
import crypto.Secp256k1;
import model.transaction.Signature;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertEquals;

public class TestSecp {
    @Test
    public void testKeySpec()  {

        byte[] seckey = Base58.decode("EhNiaU4DzUmjCrvynV3gaUeuj2VjB1v2DCmbGD5U2nSE");
        Secp256k1 s = new Secp256k1(seckey);

        assertEquals( "02069b1abdaaf8326e7e6fc15607fd9e6c6f595e119a2484a4192b5d6b3878dd25", Hex.toHexString(s.pubkey()));
        byte[] info = (new SHA3.Digest256()).digest("hello".getBytes());
        assertEquals("3338be694f50c5f338814986cdf0686453a888b84f424d792af4b9202398f392", Hex.toHexString(info));
        Signature signature = s.sign(info);
        assertEquals("37f7731bdb9988aa330be8bbd6a791f11d9fe90601edf7f569f6b8ddcfdd7755333b299ca3a8437e71eb14c72a02a2c5cdf3562e891f5a3c1d54d92148236e10",
                Hex.toHexString(signature.signature));
    }
}
