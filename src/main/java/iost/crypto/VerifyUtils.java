package iost.crypto;

import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Sign;

import java.math.BigInteger;
import java.util.Arrays;

public class VerifyUtils {

    /**
     * verify ed25519 signature
     * ref: https://github.com/iost-official/java-sdk/issues/4
     *
     * @param info      info
     * @param signature signature
     * @param pubKey    pubKey
     * @return verify result
     */
    public static boolean Ed25519Verify(byte[] info, byte[] signature, byte[] pubKey) {
        byte[] messageWithSignature = new byte[signature.length + info.length];
        System.arraycopy(signature, 0, messageWithSignature, 0, signature.length);
        System.arraycopy(info, 0, messageWithSignature, signature.length, info.length);
        byte[] openMessage = TweetNaCl.crypto_sign_open(messageWithSignature, pubKey);
        return Arrays.equals(info, openMessage);
    }

    /**
     * verify Secp256k1 signature
     * ref: https://github.com/web3j/web3j/blob/v3.6.0/crypto/src/test/java/org/web3j/crypto/ECRecoverTest.java
     *
     * @param info      info
     * @param signature signature
     * @param pubKey    pubKey
     * @return verify result
     */
    public static boolean Secp256k1Verify(byte[] info, byte[] signature, byte[] pubKey) {
        byte[] r = Arrays.copyOfRange(signature, 0, 32);
        byte[] s = Arrays.copyOfRange(signature, 32, 64);
        ECDSASignature ecdsaSignature = new ECDSASignature(new BigInteger(r), new BigInteger(s));

        byte[] publicKey = null;
        // remove head
        if (pubKey.length == 33) {
            if (pubKey[0] != (byte) 0x02) {
                throw new RuntimeException("Secp256k1Verify failure : pubKey is invalidate");
            }
            publicKey = Arrays.copyOfRange(pubKey, 1, 33);
        } else if (pubKey.length == 32 || pubKey.length == 64) {
            publicKey = Arrays.copyOfRange(pubKey, 0, 32);
        } else {
            throw new RuntimeException("Secp256k1Verify failure : pubKey length is invalidate");
        }

        boolean match = false;
        // Iterate for each possible key to recover
        for (int i = 0; i < 4; i++) {
            BigInteger recoverPublicKey = Sign.recoverFromSignature(i, ecdsaSignature, info);

            if (recoverPublicKey != null) {
                byte[] recoverPubKey = Arrays.copyOfRange(
                        recoverPublicKey.toByteArray(), 0, publicKey.length);
                if (Arrays.equals(recoverPubKey, publicKey)) {
                    match = true;
                    break;
                }
            }
        }
        return match;
    }
}
