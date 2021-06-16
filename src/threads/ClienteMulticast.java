package threads;

import contexts.ActualNodeData;
import objects.NodeInformation;
import utils.Print;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class ClienteMulticast extends Thread{
    private String host="228.1.1.1";
    private int port=2000;
    private ActualNodeData nodeData= ActualNodeData.getInstance();;
    private static ClienteMulticast instance;

    public static ClienteMulticast getInstance(){
        if(instance==null){
            instance= new ClienteMulticast();
        }
        return instance;
    }

    public void run(){
        try {
            NetworkInterface netInterface = NetworkInterface.getByName("wlan0");
            InetSocketAddress dir = new InetSocketAddress(port);
            DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);
            datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, netInterface);
            InetAddress group = InetAddress.getByName(host);
            datagramChannel.join(group, netInterface);
            datagramChannel.configureBlocking(false);
            datagramChannel.socket().bind(dir);
            Selector sel = Selector.open();
            datagramChannel.register(sel, SelectionKey.OP_READ);
            ByteBuffer buffer = ByteBuffer.allocate(4);
            Print.info("Starting to get members of net...");
            while(true){
                sel.select();
                Iterator<SelectionKey> it = sel.selectedKeys().iterator();
                while(it.hasNext()){
                    SelectionKey key = (SelectionKey)it.next();
                    it.remove();
                    if(key.isReadable()){
                        DatagramChannel channel = (DatagramChannel)key.channel();
                        buffer.clear();
                        SocketAddress emisor = channel.receive(buffer);
                        buffer.flip();
                        int RMIPortOfMember=buffer.getInt();
                        //If the node do not contains the member, just added.
                        if(!nodeData.containsMember(RMIPortOfMember)){
                            InetSocketAddress address = (InetSocketAddress)emisor;
                            nodeData.addNewMemberToRing(new NodeInformation(address.getAddress(), RMIPortOfMember));
                        }
                        continue;
                    }
                }//while
            }//while
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
