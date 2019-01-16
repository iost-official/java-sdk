package model.block;

import model.transaction.Transaction;

public class Block {
    public String hash;
    public long version;
    public String parent_hash;
    public String tx_merkle_hash;
    public String tx_receipt_merkle_hash;
    public long number;
    public String witness;
    public long time;
    public double gas_usage;
    public long tx_count;
    public Info info;
    public Transaction[] transactions;
}
