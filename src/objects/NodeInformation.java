package objects;

import java.net.InetAddress;

public class NodeInformation {
    private int RMIport;
    private int MyPort;
    private InetAddress IPaddress;

    public NodeInformation(InetAddress IPaddress, int port){
        this.MyPort = port;
        this.IPaddress = IPaddress;
    }

    public NodeInformation(InetAddress IPaddress){
        this.IPaddress = IPaddress;
    }

    public int getRMIport() {
        return RMIport;
    }

    public void setRMIport(int RMIport) {
        this.RMIport = RMIport;
    }

    public InetAddress getIPaddress() {
        return IPaddress;
    }

    public void setIPaddress(InetAddress IPaddress) {
        this.IPaddress = IPaddress;
    }

    public String toString(){
        return this.IPaddress+":"+this.getMyPort();
    }

    public int getMyPort() {
        return MyPort;
    }

    public void setMyPort(int myPort) {
        MyPort = myPort;
    }
}
