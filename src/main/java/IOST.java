import model.transaction.Transaction;

import java.util.Date;

public class IOST {
    private long gasLimit, gasRatio;
    private long expiration, delay;

    IOST(long gasLimit, long gasRatio, long expirationInMillis, long delay) {
        this.gasLimit = gasLimit;
        this.gasRatio = gasRatio;
        this.expiration = expirationInMillis;
        this.delay = delay;
    }


    IOST() {
        this.gasLimit = 1000000;
        this.gasRatio = 1;
        this.delay = 0;
        this.expiration = 90000000000L;
    }

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

    public Transaction transfer(String token, String from, String to, double amount, String memo) {
        Transaction tx = this.callABI("token.iost", "transfer", token, from, to, String.valueOf(amount), memo);
        tx.addApprove("iost", String.valueOf(amount));

        return tx;
    }

    public Transaction newAccount(String name, String creator, String ownerkey, String activekey, long initialRAM,
                                  double initialGasPledge) {
        Transaction t = this.callABI("auth.iost", "signUp", name, ownerkey, activekey);
        t.addAction("ram.iost", "buy", creator, name, initialRAM);
        t.addAction("gas.iost", "pledge",  creator, name, String.valueOf(initialGasPledge));
        t.addApprove("iost", "unlimited");
        return t;
    }

}
