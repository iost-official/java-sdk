package iost.model.transaction;

import iost.crypto.Algorithm;

public class Signature {
    public Algorithm algorithm;
    public byte[] public_key,signature;
}
