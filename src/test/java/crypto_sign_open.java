import iost.crypto.Ed25519;
import iost.crypto.KeyPair;
import iost.crypto.TweetNaCl;
import org.junit.Test;

// ref: https://github.com/iost-official/java-sdk/issues/4
public class crypto_sign_open {

    @Test
    public void test() {
        String message = "hello world";
        KeyPair k = new Ed25519();

        System.out.println(k.B58SecKey());
        System.out.println(k.B58PubKey());

        byte[] info = message.getBytes();

        byte[] signature = TweetNaCl.crypto_sign(info, k.seckey());

        byte[] messageWithSignature = new byte[signature.length + info.length];
        System.arraycopy(signature, 0, messageWithSignature, 0, signature.length);
        System.arraycopy(info, 0, messageWithSignature, signature.length, info.length);
        byte[] openMessage = TweetNaCl.crypto_sign_open(messageWithSignature, k.pubkey());

        System.out.println(new String(signature));
        System.out.println(new String(openMessage));
    }
}
