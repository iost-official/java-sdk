package crypto;

import model.transaction.Signature;

import java.nio.ByteBuffer;
import java.util.Arrays;

public abstract class KeyPair {

    private String ID;

    public String getID() {
        if (this.ID == null) {
            byte[] data3 = this.pubkey();
            Crc32 crc = new Crc32();
            int checksumValue = crc.crcDirect(data3);
            ByteBuffer bb = ByteBuffer.allocate(data3.length + 4);
            bb.put(data3);
            bb.putInt(checksumValue);
            this.ID =  "IOST" + Base58.encode(bb.array());
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

