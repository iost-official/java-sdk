package model.transaction;

import java.util.Map;

public class TxReceipt {
    public String status_code;
    public String message;

    public String tx_hash;
    public double gas_usage;
    public Map<String, Integer> ram_usage;
    public String[] returns;
    public Receipt[] receipts;
}
