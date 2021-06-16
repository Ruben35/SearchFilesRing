package threads;

import contexts.ActualNodeData;

public class ConnectNoBlocking extends Thread{

    private ActualNodeData nodeData= ActualNodeData.getInstance();
    private static ConnectNoBlocking instance;

    public static ConnectNoBlocking getInstance(){
        if(instance==null){
            instance= new ConnectNoBlocking();
        }
        return instance;
    }

    @Override
    public void run(){
        nodeData.connect();
    }
}
