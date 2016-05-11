package icesword.agent.data;


public class ClientStatus {

    public int    statusCode;

    public String msg;

    public long   timestamp;

    public ClientStatus(int status, String msg) {
        this.statusCode = status;
        this.msg = msg;
        this.timestamp = System.currentTimeMillis();
    }

}
