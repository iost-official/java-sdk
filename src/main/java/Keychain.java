import crypto.KeyPair;
import model.transaction.Signature;
import model.transaction.Transaction;

import java.util.HashMap;
import java.util.Map;

public class Keychain {
    private String account;
    private Map<String, KeyPair> keys = new HashMap<>();

    public Keychain(String account) {
        this.account = account;
    }

    public void sign(Transaction tx, String perm) {
        byte[] buf = tx.getSignHash();
        KeyPair kp = this.keys.get(perm);
        tx.signatures.add(kp.sign(buf));
    }

    public void publish(Transaction tx) {
        byte[] buf = tx.getPublishHash();
        KeyPair kp = this.keys.get("active");
        tx.signatures.add(kp.sign(buf));
    }

    public void AddKey(String perm, KeyPair kp) {
        this.keys.put(perm, kp);
    }
}
