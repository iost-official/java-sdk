package iost;

import iost.crypto.Base58;
import iost.model.transaction.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public class IOST {
    private long gasLimit, gasRatio;
    private long expiration, delay;

    /**
     * set default params of this IOST generated transaction
     *
     * @param gasLimit           -
     * @param gasRatio           -
     * @param expirationInMillis -
     * @param delay              -
     */
    public IOST(long gasLimit, long gasRatio, long expirationInMillis, long delay) {
        this.gasLimit = gasLimit;
        this.gasRatio = gasRatio;
        this.expiration = expirationInMillis;
        this.delay = delay;
    }


    /**
     * Use default values
     */
    public IOST() {
        this.gasLimit = 1000000;
        this.gasRatio = 1;
        this.delay = 0;
        this.expiration = 90000000000L;
    }

    /**
     * @param cid  - contract id
     * @param abi  - abi to call
     * @param data - params of abi
     * @return - Transaction with single action
     */
    public Transaction callABI(String cid, String abi, Object... data) {
        Transaction tx = new Transaction();
        tx.time = new Date().getTime() * 1000000;
        tx.expiration = tx.time + this.expiration * 1000000;
        tx.gas_limit = this.gasLimit;
        tx.gas_ratio = this.gasRatio;
        tx.delay = this.delay;
        tx.addAction(cid, abi, data);
        return tx;
    }

    /**
     * @param token  -
     * @param from   -
     * @param to     -
     * @param amount -
     * @param memo   -
     * @return -
     */
    public Transaction transfer(String token, String from, String to, BigDecimal amount, String memo) {
        Transaction tx = this.callABI("token.iost", "transfer", token, from, to, amount.toString(), memo);
        tx.addApprove(token, amount.toString());

        return tx;
    }

    /**
     * @param name -
     * @param creator -
     * @param ownerkey -
     * @param activekey -
     * @param initialRAM -
     * @param initialGasPledge -
     * @return -
     */
    public Transaction newAccount(String name, String creator, String ownerkey, String activekey, long initialRAM,
                                  double initialGasPledge) throws IOException {
        if (!this.checkPubkey(ownerkey) || !this.checkPubkey(activekey))
            throw new IOException("illegal public key");
        Transaction t = this.callABI("auth.iost", "signUp", name, ownerkey, activekey);
        if (initialRAM > 0) {
            t.addAction("ram.iost", "buy", creator, name, initialRAM);
        }
        if (initialGasPledge > 0) {
            t.addAction("gas.iost", "pledge", creator, name, String.format ("%.4f", initialGasPledge));
        }
        t.addApprove("iost", "unlimited");
        return t;
    }

    private boolean checkPubkey(String key) {
        try {
            byte[] k = Base58.decode(key);
            if (k.length != 32) {
                return false;
            }
            return true;

        } catch (IOException e) {
            return false;
        }
    }

}
