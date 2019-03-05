package iost.model.transaction;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import iost.crypto.Base58;
import iost.crypto.SimpleEncoder;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private String hash = "";
    public long gas_ratio, gas_limit, time = 0, expiration = 0, delay = 0;
    public List<Signature> signatures = new ArrayList<>();
    public List<Action> actions = new ArrayList<>();
    public List<Signature> publisher_sigs = new ArrayList<>();
    public List<String> signers = new ArrayList<>();
    public List<AmountLimit> amount_limit = new ArrayList<>();
    public String publisher;
    public int chain_id = 1024;
    public byte[] reserved = {};
    public String referred_tx = "";

    public byte[] signBytes() {
        SimpleEncoder se = new SimpleEncoder(65536);
        se.buffer.putLong(this.time);
        se.buffer.putLong(this.expiration);
        se.buffer.putLong(this.gas_ratio * 100);
        se.buffer.putLong(this.gas_limit * 100);
        se.buffer.putLong(this.delay);
        se.buffer.putInt(this.chain_id);

        se.buffer.putInt(this.reserved.length);
        for (byte b : this.reserved) {
            se.buffer.put(b);
        }

        se.buffer.putInt(this.signers.size());
        for (String signer : this.signers) {
            se.putString(signer);
        }

        se.buffer.putInt(this.actions.size());
        for (Action act : this.actions) {
            SimpleEncoder c2 = new SimpleEncoder(1024);
            c2.putString(act.contract);
            c2.putString(act.action_name);
            if (act.data != null) {
                c2.putString(act.data);
            }
            se.putBytes(c2.toBytes());
        }

        se.buffer.putInt(this.amount_limit.size());
        for (AmountLimit alo : this.amount_limit) {
            SimpleEncoder c2 = new SimpleEncoder(1024);
            c2.putString(alo.token);
            c2.putString(alo.value);
            se.putBytes(c2.toBytes());
        }


        return se.toBytes();
    }

    public byte[] getSignHash() {
        return (new SHA3.Digest256()).digest(this.signBytes());
    }

    public byte[] getPublishBytes() {
        SimpleEncoder se = new SimpleEncoder(this.signBytes());
        se.buffer.putInt(this.signatures.size());
        for (Signature sign : this.signatures) {
            SimpleEncoder c2 = new SimpleEncoder(1024);
            c2.buffer.put(sign.algorithm.toByte());
            c2.putBytes((sign.signature));
            c2.putBytes((sign.public_key));
            se.putBytes(c2.toBytes());
        }
        return se.toBytes();

    }

    public String getHash() {
        if (!this.hash.equals("")) return this.hash;

        SimpleEncoder se = new SimpleEncoder(this.getPublishBytes());
        se.buffer.putInt(0); // referred_tx
        System.out.println("publisher:" +this.publisher);
        se.putString(this.publisher);

        se.buffer.putInt(this.publisher_sigs.size());
        for (Signature sign : this.publisher_sigs) {
            SimpleEncoder c2 = new SimpleEncoder(1024);
            c2.buffer.put(sign.algorithm.toByte());
            c2.putBytes((sign.signature));
            c2.putBytes((sign.public_key));
            se.putBytes(c2.toBytes());
        }
        this.hash = Base58.encode((new SHA3.Digest256()).digest(se.toBytes()));
        return this.hash;
    }

    public byte[] getPublishHash() {
        return (new SHA3.Digest256()).digest(this.getPublishBytes());
    }


    /**
     * add approve of this transaction
     * approve means how much this transaction can at most use your token
     *
     * @param type  - token name
     * @param value - token value in String, or "unlimited"
     * @return -
     */
    public Transaction addApprove(String type, String value) {

        AmountLimit al = new AmountLimit();
        al.token = type;
        al.value = value;

        this.amount_limit.add(al);
        return this;
    }

    /**
     * add action to transaction
     *
     * @param contract - contract id
     * @param abi      - abi name
     * @param data     - abi params
     * @return -
     */
    public Transaction addAction(String contract, String abi, Object... data) {
        Action act = new Action();
        act.contract = contract;
        act.action_name = abi;
        JsonArray ja = new JsonArray();
        Gson gson = new Gson();
        act.data = gson.toJson(data);
        this.actions.add(act);
        return this;
    }

    /**
     * add co-signer to this transaction, publisher should not added here
     * @param s - signer@permission
     * @return -
     */
    public Transaction addSigner(String s) {
        this.signers.add(s);
        return this;
    }

    /**
     * set time, if you want send tx at some future time, use it.
     * @param now - this transaction's sending time
     * @param lifetime - expiration
     * @param delay - delay time
     * @return -
     */
    public Transaction setTime(long now, long lifetime, long delay) {
        this.time = now;
        this.expiration = now + lifetime;
        this.delay = delay;
        return this;
    }

}