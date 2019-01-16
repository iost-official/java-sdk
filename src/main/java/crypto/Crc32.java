package crypto;

public class Crc32 {

	private int poly = 0xeb31d82e;
	private int[] table;

	// build the table
	public void makeTable() {
		int c, n, k;

		for (n = 0; n < 256; n += 1) {
			c = n;
			for (k = 0; k < 8; k += 1) {
				if (c > 0) {
					c = poly ^ (c >>> 1);
				} else {
					c = c >>> 1;
				}
			}
			table[n] = c;
		}
	}

	/*
	 * Compute CRC of array directly.
	 *
	 * This is slower for repeated calls, so append mode is not supported.
	 */
	public int crcDirect(byte[] arr) {
		int crc = -1, // initial contents of LFBSR
				i, j, l, temp;

		for (i = 0, l = arr.length; i < l; i += 1) {
			temp = (crc ^ arr[i]) & 0xff;

			// read 8 bits one at a time
			for (j = 0; j < 8; j += 1) {
				if ((temp & 1) == 1) {
					temp = (temp >>> 1) ^ poly;
				} else {
					temp = (temp >>> 1);
				}
			}
			crc = (crc >>> 8) ^ temp;
		}

		return ~crc;
	}

	/*-2039514538
	 * Compute CRC with the help of a pre-calculated table.
	 *
	 * This supports append mode, if the second parameter is set.
	 */
	public int crcTable(byte[] arr, boolean append) {
		int crc, i, l;

		// if we're in append mode, don't reset crc
		// if arr is null or undefined, reset table and return
		crc = -1;

		if (arr.length <= 0) {
            return 0;
        }

		for (i = 0, l = arr.length; i < l; i += 1) {
			crc = (crc >>> 8) ^ table[(crc ^ arr[i]) & 0xff];
		}

		return ~crc;
	}

	public int getCrc32(byte[] val, boolean direct) {

		// convert to 2's complement hex
		return (direct ? crcDirect(val) : crcTable(val, false));// .toString(16);
	}

	public String getHexFromBytes(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte b : bytes)
			result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}
	
	
	public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
