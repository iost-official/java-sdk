import com.google.gson.Gson;
import iost.Client;
import iost.IOST;
import iost.Keychain;
import iost.crypto.Base58;
import iost.crypto.Ed25519;
import iost.crypto.KeyPair;
import iost.model.block.BlockResponse;
import iost.model.info.ChainInfo;
import iost.model.info.NodeInfo;
import iost.model.transaction.Transaction;
import iost.model.transaction.TxReceipt;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

public class TestClient {
    private Client client = new Client("http://localhost:30001/");
    private Gson gson = new Gson();

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

    @Test
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

    @Test
    public void testTransfer() throws IOException, TimeoutException {
        try {
            IOST iost = new IOST();
            Transaction tx = iost.transfer("iost", "admin", "admin", new BigDecimal("10.00"), ""); //将 10.00 个 iost 从 admin 转账给 receiver

            byte[] privateKey = Base58.decode("2yquS3ySrGWPEKywCPzX4RTJugqRh7kJSo5aehsLYPEWkUxBWA39oMrZ7ZxuM4fgyXYs2cPwh5n8aNNpH5x2VyK1");

            Keychain account = new Keychain("admin");
            KeyPair kp = new Ed25519(privateKey);
            account.addKey("owner", kp);
            account.addKey("active", kp);

            account.publish(tx);

            String txHash = this.client.sendTx(tx);

            if (!txHash.equals(tx.getHash())) {
                System.out.println("tx hash error, should be: " + txHash + ", actual: " + tx.getHash());
            }
            TxReceipt receipt = this.client.polling(txHash, 1000, 90);
            if (receipt.status_code.equals("SUCCESS")) {
                System.out.println("tx success : gas usage " + receipt.gas_usage);
            } else {
                System.out.println("tx failed :");
                System.out.println(receipt.message);
            }

            Transaction tx2 = this.client.getTxByHash(txHash);
            if (!tx2.getHash().equals(tx.getHash())) {
                System.out.println("tx2 hash error, should be: " + tx2.getHash() + ", actual: " + tx.getHash());
            }

        } catch (IOException | TimeoutException e) {
            System.out.println("network error:");
            e.printStackTrace();
        }
    }

    @Test
    public void debugTransfer() {
        IOST iost = new IOST();
        Transaction tx = iost.transfer("iost", "admin", "admin", new BigDecimal("1000000000.00"), ""); //将 10.00 个 iost 从 admin 转账给 receiver

        System.out.println(tx.actions.get(0).data);
    }
}
