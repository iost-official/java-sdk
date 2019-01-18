import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.account.*;
import model.block.*;
import model.info.*;
import model.transaction.*;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Client {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType TXT_PLAIN = MediaType.parse("text/plain; charset=utf-8");

    private OkHttpClient client;
    private String host;
    private Gson gson;


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
     * @return -
     * @throws IOException -
     */
    public ChainInfo getChainInfo() throws IOException {
        String json = this.get("getChainInfo");
        return this.gson.fromJson(json, ChainInfo.class);

    }

    /**
     * 通过Hash获取区块
     *
     * @param hash     - hash in base58
     * @param complete - 是否获取完整的block
     * @return Block
     * @throws IOException -
     */
    public BlockResponse getBlockByHash(String hash, boolean complete) throws IOException {
        String api = "getBlockByHash/" + hash + "/" + (complete?"true":"false");
        String json = this.get(api);
        return this.gson.fromJson(json, BlockResponse.class);
    }


    /**
     * 通过区块高度获取区块
     *
     * @param num      - 区块高度
     * @param complete - 是否获取完整的block
     * @return {response}
     * @throws IOException -
     */
    public BlockResponse getBlockByNumber(String num, boolean complete) throws IOException {
        String api = "getBlockByNumber/" + num + "/" + (complete ? "true" : "false");
        return this.gson.fromJson(this.get(api), BlockResponse.class);
    }

    /**
     * @return -
     * @throws IOException -
     */
    public RamInfo getRamInfo() throws IOException {
        return this.gson.fromJson(this.get("getRAMInfo"), RamInfo.class);
    }


    /**
     * 获取某个用户的余额
     *
     * @param account        -
     * @param token          -
     * @param byLongestChain -
     * @return {balance}
     * @throws IOException -
     */
    public TokenBalance getBalance(String account, String token, String byLongestChain) throws IOException {
        String api = "getTokenBalance/" + account + "/" + token + "/" + byLongestChain;
        return this.gson.fromJson(this.get(api), TokenBalance.class);
    }

    /**
     * 获取某个用户的余额
     *
     * @param address         -
     * @param tokenSymbol     -
     * @param useLongestChain -
     * @return {json String}
     * @throws IOException -
     */
    public TokenBalance getToken721Balance(String address, String tokenSymbol, boolean useLongestChain) throws IOException {
        String api = "getToken721Balance/" + address + "/" + tokenSymbol + "/" + useLongestChain;
        return this.gson.fromJson(this.get(api), TokenBalance.class);
    }

    /**
     * 获取某个token721类型token的 metadata
     *
     * @param tokenSymbol     -
     * @param tokenID         -
     * @param useLongestChain -
     * @return {json String}
     * @throws IOException -
     */
    public String getToken721Metadata(String tokenSymbol, String tokenID, boolean useLongestChain) throws IOException {
        String api = "getToken721Metadata/" + tokenSymbol + "/" + tokenID + "/" + useLongestChain;
        String s = this.get(api);
        Map json = this.gson.fromJson(s, Map.class);
        return (String) json.get("metadata");

    }

    /**
     * 获取某个token721类型token的 owner
     *
     * @param tokenSymbol     -
     * @param tokenID         -
     * @param useLongestChain -
     * @return {json String}
     * @throws IOException -
     */
    public String getToken721Owner(String tokenSymbol, String tokenID, boolean useLongestChain) throws IOException {
        String api = "getToken721Owner/" + tokenSymbol + "/" + tokenID + "/" + useLongestChain;
        String s = this.get(api);
        Map json = this.gson.fromJson(s, Map.class);
        return (String) json.get("owner");

    }

    /**
     * 获取智能合约
     *
     * @param id - 智能合约的ID
     * @return {response}
     * @throws IOException -
     */
    public String getContract(String id, String byLongestChain) throws IOException {
        String api = "getContract/" + id + "/" + byLongestChain;
        return this.get(api);
    }

    /**
     * * 获取智能合约下的某个键值
     *
     * @param contractID - 智能合约ID
     * @param key        - 需查询的key
     * @param field      - 需查询的field
     * @param pending    - 是否从最长链上查询
     * @return {String}
     * @throws IOException -
     */
    public String getContractStorage(String contractID, String key, String field, boolean pending) throws IOException {

        HashMap<String, Object> json = new HashMap<>();
        json.put("by_longest_chain", pending);
        json.put("field", field);
        json.put("key", key);
        json.put("id", contractID);
        String api = "getContractStorage";
        return this.post(api, json);
    }


    /**
     * 获取智能合约下的fields集合
     *
     * @param contractID - 智能合约ID
     * @param fields     - 需查询的key
     * @param pending    - 是否从最长链上查询
     * @return {json String}
     * @throws IOException -
     */
    public String getContractStorageFields(String contractID, String fields, boolean pending) throws IOException {
        HashMap<String, Object> json = new HashMap<>();
        json.put("by_longest_chain", pending);
        json.put("fields", fields);
        json.put("id", contractID);
        String api = "getContractStorageFields";
        return this.post(api, json);
    }


    /**
     * 获�?�account信�?�
     *
     * @param name         - 用户�??
     * @param by_longest_chain - 是�?�从�?�逆链上查询
     * @return {response}
     * @throws IOException -
     */
    public Account getAccountInfo(String name, String by_longest_chain) throws IOException {
        String api = "getAccount/" + name + "/" + by_longest_chain;
        String s = this.get(api);
        return this.gson.fromJson(s, Account.class);
    }

    /**
     * 获取当前Gas费率
     *
     * @return {promise}
     * @throws IOException -
     */
    public GasRatio getGasRatio() throws IOException {
        String s = this.get("getGasRatio");
        return this.gson.fromJson(s, GasRatio.class);
    }

    /**
     * 获取预估的gas消耗
     *
     * @return -
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
     * Send a transactionObject
     *
     * @param tx transactionObject tobe sent
     * @return transactionObject hash
     * @throws IOException      throw while send failed
     */
    public String sendTx(Transaction tx) throws IOException {
        String api = "sendTx";
        String jsonHash = this.post(api, tx);
        Map m = gson.fromJson(jsonHash, Map.class);
        return (String) m.get("hash");
    }

    /**
     * find transactionObject by transactionObject hash
     *
     * @param hash - transactionObject hash
     * @return transactionObject in json
     * @throws IOException while net error
     */
    public Transaction getTxByHash(String hash) throws IOException { // todo return a transaction object
        String api = "getTxByHash/" + hash;
        String s = this.get(api);
        return this.gson.fromJson(s, Transaction.class);
    }

    /**
     * 通过交易哈希查询交易结果
     *
     * @param txHash - base58编�?的hash
     * @return {promise}
     * @throws IOException -
     */
    public TxReceipt getTxReceiptByTxHash(String txHash) throws IOException {
        String api = "getTxReceiptByTxHash/" + txHash;
        String s = this.get(api);
        return this.gson.fromJson(s, TxReceipt.class);
    }

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
