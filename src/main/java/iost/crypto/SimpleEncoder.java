package iost.crypto;

import java.nio.ByteBuffer;

public class SimpleEncoder {
    public ByteBuffer buffer;

    public SimpleEncoder(int cap) {
        this.buffer = ByteBuffer.allocate(cap);
    }

    public SimpleEncoder(byte[] buf) {
        this.buffer = ByteBuffer.allocate(65536);
        this.buffer.put(buf);
    }

    public SimpleEncoder putString(String s) {
        this.buffer.putInt(s.length());
        this.buffer.put(s.getBytes());
        return this;
    }

    public SimpleEncoder putBytes(byte[] bb) {
        this.buffer.putInt(bb.length);
        this.buffer.put(bb);
        return this;
    }

    public byte[] toBytes() {
        byte[] buf = new byte[this.buffer.flip().remaining()];
        this.buffer.get(buf);
        return buf;
    }
}
