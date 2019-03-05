package iost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iost.model.account.*;
import iost.model.block.*;
import iost.model.info.*;
import iost.model.transaction.*;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Client IOST RPC interface
 */
public class Client {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType TXT_PLAIN = MediaType.parse("text/plain; charset=utf-8");

    private OkHttpClient client;
    private String host;
    private Gson gson;

    /**
     * new client
     *
     * @param url - IOST full node http port, often at port 30001，eg. http://localhost:30001/
     */
    public Client(String url) {
        this.host = url;
        this.client = new OkHttpClient();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Signature.class, new SignatureAdapter());
        this.gson = gb.create();
    }

    private String post(String url, Object json) throws IOException {
        RequestBody body = RequestBody.create(TXT_PLAIN, this.gson.toJson(json));
        Request request = new Request.Builder().url(host + url).header("Connection", "close").post(body).build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) throw new IOException(response.body().string());
        return response.body().string();
    }

    private String get(String url) throws IOException {
        this.client = new OkHttpClient();
        Request request = new Request.Builder().url(host + url).build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) throw new IOException(response.body().string());
        return response.body().string();
    }


    /**
     * get node info
     *
     * @return - node info
     * @throws IOException - network error
     */
    public NodeInfo getNodeInfo() throws IOException {
        String json = this.get("getNodeInfo");
        return this.gson.fromJson(json, NodeInfo.class);
    }


    /**
     * get blockchain info
     *
     * @return - chain info
     * @throws IOException -
     */
    public ChainInfo getChainInfo() throws IOException {
        String json = this.get("getChainInfo");
        return this.gson.fromJson(json, ChainInfo.class);

    }

    /**
     * get block by block head hash
     *
     * @param hash     - hash in base58
     * @param complete - is full block instead of block info
     * @return Block
     * @throws IOException -
     */
    public BlockResponse getBlockByHash(String hash, boolean complete) throws IOException {
        String api = "getBlockByHash/" + hash + "/" + (complete ? "true" : "false");
        String json = this.get(api);
        return this.gson.fromJson(json, BlockResponse.class);
    }


    /**
     * get block by block number
     *
     * @param num      - height of block
     * @param complete - is full block instead of block info
     * @return {response}
     * @throws IOException -
     */
    public BlockResponse getBlockByNumber(String num, boolean complete) throws IOException {
        String api = "getBlockByNumber/" + num + "/" + (complete ? "true" : "false");
        return this.gson.fromJson(this.get(api), BlockResponse.class);
    }

    /**
     * get current ram info
     *
     * @return -
     * @throws IOException -
     */
    public RamInfo getRamInfo() throws IOException {
        return this.gson.fromJson(this.get("getRAMInfo"), RamInfo.class);
    }


    /**
     * get balance of some token
     *
     * @param account        - account
     * @param token          - token name
     * @param byLongestChain - is query in longest chain (instead of irreversible chain)
     * @return - balance
     * @throws IOException - net error
     */
    public TokenBalance getBalance(String account, String token, boolean byLongestChain) throws IOException {
        String api = "getTokenBalance/" + account + "/" + token + "/" + byLongestChain;
        return this.gson.fromJson(this.get(api), TokenBalance.class);
    }

    /**
     * get balance of some token721
     *
     * @param address         - account
     * @param tokenSymbol     - token name
     * @param useLongestChain - is query in longest chain (instead of irreversible chain)
     * @return {json String}
     * @throws IOException -
     */
    public TokenBalance getToken721Balance(String address, String tokenSymbol, boolean useLongestChain) throws IOException {
        String api = "getToken721Balance/" + address + "/" + tokenSymbol + "/" + useLongestChain;
        return this.gson.fromJson(this.get(api), TokenBalance.class);
    }

    /**
     * get metadata of token721
     *
     * @param tokenSymbol     - account
     * @param tokenID         - token name
     * @param useLongestChain - is query in longest chain (instead of irreversible chain)
     * @return - metadata
     * @throws IOException -
     */
    public String getToken721Metadata(String tokenSymbol, String tokenID, boolean useLongestChain) throws IOException {
        String api = "getToken721Metadata/" + tokenSymbol + "/" + tokenID + "/" + useLongestChain;
        String s = this.get(api);
        Map json = this.gson.fromJson(s, Map.class);
        return (String) json.get("metadata");

    }

    /**
     * get owner of token721
     *
     * @param tokenSymbol     - account
     * @param tokenID         - token name
     * @param useLongestChain - is query in longest chain (instead of irreversible chain)
     * @return - owner
     * @throws IOException -
     */
    public String getToken721Owner(String tokenSymbol, String tokenID, boolean useLongestChain) throws IOException {
        String api = "getToken721Owner/" + tokenSymbol + "/" + tokenID + "/" + useLongestChain;
        String s = this.get(api);
        Map json = this.gson.fromJson(s, Map.class);
        return (String) json.get("owner");

    }

    /**
     * get smart contract
     *
     * @param id             - contract id
     * @param byLongestChain - is query in longest chain (instead of irreversible chain)
     * @return - code of contract
     * @throws IOException -
     */
    public String getContract(String id, boolean byLongestChain) throws IOException {
        String api = "getContract/" + id + "/" + byLongestChain;
        return this.get(api);
    }

    /**
     * get smart contract states
     *
     * @param contractID     - contract id
     * @param key            - key
     * @param field          - field (set to null if it's not a map)
     * @param byLongestChain - is query in longest chain (instead of irreversible chain)
     * @return - value to string
     * @throws IOException -
     */
    public String getContractStorage(String contractID, String key, String field, boolean byLongestChain) throws IOException {

        HashMap<String, Object> json = new HashMap<>();
        json.put("by_longest_chain", byLongestChain);
        json.put("field", field);
        json.put("key", key);
        json.put("id", contractID);
        String api = "getContractStorage";
        return this.post(api, json);
    }


    /**
     * get smart contract keys of map
     *
     * @param contractID       - contract id
     * @param fields           - key
     * @param by_longest_chain - is query in longest chain (instead of irreversible chain)
     * @return keys in json array
     * @throws IOException -
     */
    public String getContractStorageFields(String contractID, String fields, boolean by_longest_chain) throws IOException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("by_longest_chain", by_longest_chain);
        json.put("fields", fields);
        json.put("id", contractID);
        String api = "getContractStorageFields";
        return this.post(api, json);
    }


    /**
     * get account info
     *
     * @param name             - account name
     * @param by_longest_chain - is query in longest chain (instead of irreversible chain)
     * @return {response}
     * @throws IOException -
     */
    public Account getAccountInfo(String name, boolean by_longest_chain) throws IOException {
        String api = "getAccount/" + name + "/" + by_longest_chain;
        String s = this.get(api);
        return this.gson.fromJson(s, Account.class);
    }

    /**
     * get current gas ratio
     *
     * @return - gas ratio
     * @throws IOException -
     */
    public GasRatio getGasRatio() throws IOException {
        String s = this.get("getGasRatio");
        return this.gson.fromJson(s, GasRatio.class);
    }

    /**
     * get gas usage this acton will cost
     *
     * @param actionName - your action name
     * @return - gas usage
     */
    public long getGasUsage(String actionName) {
        switch (actionName) {
            case "transfer":
                return 7800;
            case "newAccount":
                return 115000;
        }
        return 0;
    }


    /**
     * sent transaction
     *
     * @param tx - transaction
     * @return - transaction hash
     * @throws IOException -
     */
    public String sendTx(Transaction tx) throws IOException {
        String api = "sendTx";
        String jsonHash = this.post(api, tx);
        Map m = gson.fromJson(jsonHash, Map.class);
        return (String) m.get("hash");
    }

    class wrappedTx {
        String status;
        Transaction transaction;
    }

    /**
     * find tx by tx hash
     *
     * @param hash - transaction hash
     * @return transaction in json
     * @throws IOException while net error
     */
    public Transaction getTxByHash(String hash) throws IOException { // todo return a transaction object

        String api = "getTxByHash/" + hash;
        String s = this.get(api);
        wrappedTx wtx = this.gson.fromJson(s, wrappedTx.class);
        return wtx.transaction;
    }

    /**
     * get TxReceipt by transaction hash
     *
     * @param txHash - Tx hash in Base58
     * @return tx receipt
     * @throws IOException -
     */
    public TxReceipt getTxReceiptByTxHash(String txHash) throws IOException {
        String api = "getTxReceiptByTxHash/" + txHash;
        String s = this.get(api);
        return this.gson.fromJson(s, TxReceipt.class);
    }


    /**
     * polling transaction receipt
     *
     * @param hash             - tx hash
     * @param intervalInMillis -
     * @param times            -
     * @return - TxReceipt
     * @throws TimeoutException -
     */
    public TxReceipt polling(String hash, long intervalInMillis, int times) throws TimeoutException {
        TxReceipt receipt;
        for (int i = 0; i < times; i++) {
            try {
                receipt = this.getTxReceiptByTxHash(hash);
                return receipt;
            } catch (IOException e) {
                try {
                    Thread.sleep(intervalInMillis);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        throw new TimeoutException();

    }


}
