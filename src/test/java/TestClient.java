import com.google.gson.Gson;
import model.block.BlockResponse;
import model.info.ChainInfo;
import model.info.NodeInfo;
import org.junit.Test;

import java.io.IOException;

public class TestClient {
    private Client client = new Client("http://47.244.109.92:30001/");
    private Gson gson = new Gson();

    @Test
    public void testGetNodeInfo() throws IOException {
        NodeInfo ni = client.getNodeInfo();
        System.out.println(gson.toJson(ni));
    }

    @Test
    public void testGetChainInfo() throws IOException {
        ChainInfo ci = client.getChainInfo();
        System.out.println(gson.toJson(ci));
    }

    @Test
    public void testGetBlockByHash() throws IOException {
        BlockResponse br = client.getBlockByHash("CgK2PpohVgU3FcNwnopa8ps9NEMBUmBuVKYHuhEe7wBo", true);
        System.out.println(br.status);
        System.out.println(gson.toJson(br.block));
    }
}
