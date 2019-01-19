import com.google.gson.Gson;
import crypto.Base58;
import crypto.Ed25519;
import crypto.KeyPair;
import model.block.BlockResponse;
import model.info.ChainInfo;
import model.info.NodeInfo;
import model.transaction.Transaction;
import model.transaction.TxReceipt;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TestClient {
    private Client client = new Client("http://47.244.109.92:30001/");
    private Gson gson = new Gson();

    private byte[] privateKey;
    private Keychain account;

    @Test
    public void testGetNodeInfo() {
        try {
            NodeInfo ni = client.getNodeInfo();
            System.out.println(gson.toJson(ni));
        } catch (IOException e) {
            System.out.println("network error");
            e.printStackTrace();
        }
    }

    @Test
    public void testGetChainInfo() {
        try {
            ChainInfo ci = client.getChainInfo();
            System.out.println(gson.toJson(ci));
        } catch (IOException e) {
            System.out.println("network error");
            e.printStackTrace();
        }
    }

    public void testGetBlockByHash() {
        try {

            BlockResponse br = client.getBlockByHash("CgK2PpohVgU3FcNwnopa8ps9NEMBUmBuVKYHuhEe7wBo", true);
            System.out.println(br.status);
            System.out.println(gson.toJson(br.block));
        } catch (IOException e) {
            System.out.println("network error");
            e.printStackTrace();
        }
    }

    public void testTransfer() throws IOException, TimeoutException {
        try {
            IOST iost = new IOST();
            Transaction tx = iost.transfer("iost", "admin", "admin", 10.00, ""); //将 10.00 个 iost 从 admin 转账给 receiver

            try {
                this.privateKey = Base58.decode("2yquS3ySrGWPEKywCPzX4RTJugqRh7kJSo5aehsLYPEWkUxBWA39oMrZ7ZxuM4fgyXYs2cPwh5n8aNNpH5x2VyK1");
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.account = new Keychain("admin");
            KeyPair kp = new Ed25519(privateKey);
            account.addKey("owner", kp);
            account.addKey("active", kp);

            this.account.publish(tx);

            String txHash = this.client.sendTx(tx);
            TxReceipt receipt = this.client.polling(txHash, 1000, 90);
            if (receipt.status_code.equals("SUCCESS")) {
                System.out.println("tx success : gas usage " + receipt.gas_usage);
            } else {
                System.out.println("tx failed :");
                System.out.println(receipt.message);
            }
        } catch (IOException | TimeoutException e) {
            System.out.println("network error:");
            e.printStackTrace();
        }
    }
}
