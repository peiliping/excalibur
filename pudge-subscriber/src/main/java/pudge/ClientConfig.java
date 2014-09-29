package pudge;


public class ClientConfig {

    private long   interval      = 1000 * 60;

    private long   initDelay     = 0;

    private long   excuteTimeOut = 1000 * 10;

    private int    retryTimes    = 3;

    private int    serverPort    = 80;

    private String serverAddress;

    private String urlPath;

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getExcuteTimeOut() {
        return excuteTimeOut;
    }

    public void setExcuteTimeOut(long excuteTimeOut) {
        this.excuteTimeOut = excuteTimeOut;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public long getInitDelay() {
        return initDelay;
    }

    public void setInitDelay(long initDelay) {
        this.initDelay = initDelay;
    }

}
