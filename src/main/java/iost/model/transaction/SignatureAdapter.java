package iost.model.transaction;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import iost.crypto.Algorithm;
import iost.crypto.Ed25519;
import iost.crypto.Secp256k1;

import java.io.IOException;
import java.util.Base64;

public class SignatureAdapter extends TypeAdapter<Signature> {
    @Override
    public void write(JsonWriter out, Signature value) throws IOException {
        out.beginObject();
        out.name("algorithm").value(value.algorithm.toByte());
        out.name("public_key").value(Base64.getEncoder().encodeToString(value.public_key));
        out.name("signature").value(Base64.getEncoder().encodeToString(value.signature));
        out.endObject();
    }

    @Override
    public Signature read(JsonReader in) throws IOException {
        final Signature s = new Signature();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "algorithm":
                    s.algorithm = in.nextInt() == 1? Algorithm.Secp256k1: Algorithm.Ed25519;
                    break;
                case "public_key":
                    s.public_key = Base64.getDecoder().decode(in.nextString());
                    break;
                case "signature":
                    s.signature = Base64.getDecoder().decode(in.nextString());
                    break;
            }
        }
        in.endObject();

        return s;
    }
}
