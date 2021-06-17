package threads;

import contexts.ActualNodeData;
import utils.FilesUtil;
import utils.Print;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerUpload extends Thread{
    private ActualNodeData nodeData = ActualNodeData.getInstance();
    private static ServerUpload instance;

    public static ServerUpload getInstance(){
        if(instance==null){
            instance=new ServerUpload();
        }

        return instance;
    }

    private ServerUpload(){}

    @Override
    public void run(){
        try{
            int port=nodeData.getMyRMIPort()+100;
            ServerSocket s = new ServerSocket(port);
            int bufferSize=2048;
            Print.info("Server to receive request of download is ready on port "+port+"...");
            while(true){
                Socket client = s.accept();
                client.setReceiveBufferSize(bufferSize);
                client.setReceiveBufferSize(bufferSize);
                DataInputStream inputClient = new DataInputStream(client.getInputStream());
                DataOutputStream outputClient = new DataOutputStream(client.getOutputStream());
                //Getting file from folder
                String fileName= inputClient.readUTF();
                File fileToUpload= FilesUtil.returnFile(fileName,nodeData.getSource());
                String path= fileToUpload.getAbsolutePath();
                long size= fileToUpload.length();
                Print.info("Sending request of download of the file \""+fileName+"\"");
                //Sending file properties
                outputClient.writeUTF(fileName);
                outputClient.flush();
                outputClient.writeLong(size);
                outputClient.flush();
                DataInputStream inputFile = new DataInputStream(new FileInputStream(path));
                byte[] b = new byte[bufferSize];
                long send = 0;
                int n;
                while (send < size){
                    n = inputFile.read(b);
                    outputClient.write(b,0,n);
                    outputClient.flush();
                    send = send+n;
                }//While
                outputClient.flush();
                inputFile.close();
                inputClient.close();
                outputClient.close();
                client.close();
                Print.strong("File \""+fileName+"\" uploaded correctly to response!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
