public class Config {
    public double gasLimit, gasRatio;
    public long expiration, delay;

    public static Config Default() {
        Config c = new Config();
        c.gasLimit =1000000;
        c.gasRatio = 1;
        c.delay = 0;
        c.expiration = 90000000000L;
        return c;
    }
}

