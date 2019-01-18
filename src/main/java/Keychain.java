import crypto.KeyPair;
import model.transaction.Signature;
import model.transaction.Transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * 钥匙链
 */
public class Keychain {
    private String account;
    private Map<String, KeyPair> keys = new HashMap<>();

    /**
     * new key chain of account
     * @param account -
     */
    public Keychain(String account) {
        this.account = account;
    }

    /**
     * add sign of tx
     * @param tx -
     * @param perm - permission
     */
    public void sign(Transaction tx, String perm) {
        byte[] buf = tx.getSignHash();
        KeyPair kp = this.keys.get(perm);
        tx.signatures.add(kp.sign(buf));
    }

    /**
     * add publish sign of tx
     * @param tx -
     */
    public void publish(Transaction tx) {
        byte[] buf = tx.getPublishHash();
        KeyPair kp = this.keys.get("active");
        tx.publisher_sigs.add(kp.sign(buf));
        tx.publisher = this.account;
    }

    /**
     * add key to keychain
     * @param perm - add key to permission
     * @param kp - key pair
     */
    public void addKey(String perm, KeyPair kp) {
        this.keys.put(perm, kp);
    }

    /**
     * get key from keychain
     * @param perm - permission
     * @return - key pair
     */
    public KeyPair getKey(String perm) {
        return this.keys.get(perm);
    }
}
