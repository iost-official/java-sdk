package crypto;

import model.transaction.Signature;

import java.nio.ByteBuffer;
import java.util.Arrays;

public abstract class KeyPair {

    private String ID;

    public String getID() {
        if (this.ID == null) {
            this.ID = Base58.encode(this.pubkey());
        }
        return this.ID;
    }

    public String B58PubKey() {
        return Base58.encode(this.pubkey());
    }

    public String B58SecKey() {
        return Base58.encode(this.seckey());
    }

    abstract public Signature sign(byte[] info);
    abstract public byte[] pubkey();
    abstract public byte[] seckey();
}

