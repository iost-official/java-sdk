package model.transaction;

import java.util.ArrayList;
import java.util.List;

public class Transaction  {
    public long gas_ratio, gas_limit, time = 0, expiration = 0, delay = 0;
    public List<Signature> signatures = new ArrayList<>();
    public List<Action> actions = new ArrayList<>();
    public List<Signature> publisher_sigs = new ArrayList<>();
    public List<String> signers = new ArrayList<>();
    public List<AmountLimit> amount_limit = new ArrayList<>();
    public String publisher;
    public int chain_id = 1024;
}