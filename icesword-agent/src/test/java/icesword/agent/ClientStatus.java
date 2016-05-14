package icesword.agent;


public class ClientStatus {

    public int    statusCode; // 0 = error 1 = normal

    public String msg;

    public long   timestamp;

    public ClientStatus(int status, String msg) {
        this.statusCode = status;
        this.msg = msg;
        this.timestamp = System.currentTimeMillis();
    }

}
