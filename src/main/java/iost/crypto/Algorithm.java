package iost.crypto;

/**
 * crypto algorithm of IOST
 */
public enum Algorithm {
    Secp256k1,
    Ed25519;

    @Override
    public String toString() {
        switch (this) {
            case Ed25519:
                return "ed25519";
            case Secp256k1:
                return "secp256k1";
        }
        return "";
    }

    public byte toByte() {
        switch (this) {
            case Ed25519:
                return 2;
            case Secp256k1:
                return 1;
        }
        return 0;
    }
}
