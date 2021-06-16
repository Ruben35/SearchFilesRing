package threads;

import contexts.ActualNodeData;
import objects.NodeInformation;
import utils.Print;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.Socket;

public class ClientDownload extends Thread{
    private ActualNodeData nodeData=ActualNodeData.getInstance();
    private String fileName;
    private NodeInformation nodeToDownload;

    public ClientDownload(String fileName, NodeInformation nodeToDownload){
        this.fileName=fileName;
        this.nodeToDownload=nodeToDownload;
    }

    @Override
    public void run(){
        try{
            String ip=nodeToDownload.getIPaddress().toString();
            ip=ip.substring(1,ip.length());
            int port=nodeToDownload.getRMIport()+100;
            int bufferSize=2048;
            Print.info("Sending request of download to "+ip+":"+port+"...");
            Socket socket = new Socket(ip,port);
            socket.setReceiveBufferSize(bufferSize);
            socket.setSendBufferSize(bufferSize);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            //Send request
            output.writeUTF(fileName);
            output.flush();
            //Receiving file
            String name = input.readUTF();
            System.err.println(name);
            System.err.println(nodeData.getDestination());
            name = nodeData.getDestination().getAbsolutePath()+"/"+name;
            byte[] b = new byte[bufferSize];
            long size= input.readLong();
            DataOutputStream fileOutput = new DataOutputStream(new FileOutputStream(name));
            long receive=0;
            int n;
            while(receive < size){
                if(size-receive<bufferSize){
                    n = input.read(b,0,(int)(size-receive));
                }else{
                    n = input.read(b);
                }
                fileOutput.write(b,0,n);
                fileOutput.flush();
                receive = receive + n;
            }//While
            fileOutput.flush();
            fileOutput.close();
            input.close();
            output.close();
            socket.close();
            Print.strong("File \""+fileName+"\" downloaded correctly!");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
