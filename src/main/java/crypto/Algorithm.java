package crypto;

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
}
