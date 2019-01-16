package crypto;

import model.transaction.Signature;

public class Ed25519 extends KeyPair {

    @Override
    public Signature sign(byte[] info) {
        return null;
    }

    @Override
    public byte[] pubkey() {
        return new byte[0];
    }

    @Override
    public byte[] seckey() {
        return new byte[0];
    }
}
