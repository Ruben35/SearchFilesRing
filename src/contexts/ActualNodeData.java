package contexts;

import interfacesRMI.searchInterface;
import objects.NodeInformation;
import threads.ClientDownload;
import threads.ClientMulticast;
import threads.ServerMulticast;
import threads.ServerUpload;
import utils.FilesUtil;
import utils.Print;
import utils.portSorter;
import view.SearchWindow;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ActualNodeData implements searchInterface{
    private NodeInformation myInfo, successor, predecessor;
    private ArrayList<NodeInformation> membersOfRing;
    private boolean initializedNode;
    private static Registry registry;
    private static ActualNodeData instance;
    private File source, destination;

    /**
     * Method to get instance
     * @return
     */
    public static ActualNodeData getInstance(){
        if(instance==null) {
            try {
                NetworkInterface networkInterface = NetworkInterface.getByName("wlan0");
                instance = new ActualNodeData(networkInterface.getInetAddresses().nextElement());
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * Constructor
     * @param myAddress
     */
    private ActualNodeData(InetAddress myAddress){
        this.myInfo= new NodeInformation(myAddress);
        this.membersOfRing= new ArrayList<>();
        this.initializedNode = false;
    }

    /**
     * Getter of RMIPort
     * @return
     * @throws Exception
     */
    public int getMyRMIPort() throws Exception{
        if(!initializedNode)
            throw new Exception("Node not initialized", new Throwable("You need to initialize your node first in order to use this method..."));
        return this.myInfo.getRMIport();
    }

    public int getMyPort(){
        return this.myInfo.getMyPort();
    }

    public void setMyPort(int Port){
        this.myInfo.setMyPort(Port);
    }

    /**
     * Getter of IPAddress
     * @return
     * @throws Exception
     */
    public InetAddress getMyIPAddress(){
        return this.myInfo.getIPaddress();
    }

    public NodeInformation getMyInfo() throws Exception{
        if(!initializedNode)
            throw new Exception("Node not initialized", new Throwable("You need to initialize your node first in order to use this method..."));
        return this.myInfo;
    }

    /**
     * Getter of Successor Node Info
     * @return
     * @throws Exception
     */
    public NodeInformation getSuccessor() throws Exception{
        if(!initializedNode)
            throw new Exception("Node not initialized", new Throwable("You need to initialize your node first in order to use this method..."));
        return this.successor;
    }

    /**
     * Getter of Predecessor Node Info
     * @return
     * @throws Exception
     */
    public NodeInformation getPredecessor() throws Exception{
        if(!initializedNode)
            throw new Exception("Node not initialized", new Throwable("You need to initialize your node first in order to use this method..."));
        return this.predecessor;
    }

    /**
     * Getter of MembersOfRing
     * @return
     * @throws Exception
     */
    public ArrayList<NodeInformation> getMembersOfRing() {
        return membersOfRing;
    }

    /**
     * Getter of Source Directory
     * @return
     */
    public File getSource() {
        return source;
    }

    /**
     * Setter of Source Directory
     * @return
     */
    public void setSource(File source) {
        this.source = source;
    }

    /**
     * Getter of Destination Directory
     * @return
     */
    public File getDestination() {
        return destination;
    }

    /**
     * Setter of Destination Directory
     * @return
     */
    public void setDestination(File destination) {
        this.destination = destination;
    }

    /**
     * Method to initialize the node and setting it in the net
     */
    public void initializeNode(){
            this.membersOfRing.add(myInfo);
            updateSuccessorPredecessorAndRMIPorts();
            this.initializedNode=true;
            SearchWindow.getWindow().updateTableAndLables();
    }

    /**
     * Method to see if the node was initialized
     * @return boolean
     */
    public boolean isInitialized(){
        return this.initializedNode;
    }

    /**
     * Method to update the Successor and Predeccessor and Resset all the RMIports (and obviously their RMIservers)
     */
    private void updateSuccessorPredecessorAndRMIPorts(){
        membersOfRing.sort(new portSorter());
        //Updating RMIports
        int RMIPort=9000;
        for(int i=0; i<membersOfRing.size();i++){
            membersOfRing.get(i).setRMIport(RMIPort);
            RMIPort++;
        }
        if(membersOfRing.size()==1){
            successor=predecessor=myInfo;
        }else{
            int myIndex=membersOfRing.indexOf(myInfo);
            //If it is the last in the list, the succesor is the first in list, otherwise is the one next to it.
            successor=(myIndex==(membersOfRing.size()-1))?membersOfRing.get(0):membersOfRing.get(myIndex+1);
            //If it is the first in the list, the predecessor is the last, otherwise is the one behind it.
            predecessor=(myIndex==0)?membersOfRing.get(membersOfRing.size()-1):membersOfRing.get(myIndex-1);
        }
        if(!initializedNode){
            initializeRMIServer(); // If first time in the net, just initializeHisServer
        }else{
            restartRMIServer(); // If it was already on the net, it also needs to restart his RMIserver (unexporting his object)
        }
    }

    /**
     * Method to see if we have already the member in the list of members
     * @param portMember
     * @return
     */
    public boolean containsMember(final int portMember){
        return membersOfRing.stream().anyMatch(o -> o.getMyPort()==portMember);
    }

    /**
     * Method to start connection with the net (getting members if exist or creat it if not)
     * then it initialize the node and start to echo the RMIPort
     */
    public void connect(){
        ClientMulticast.getInstance().start(); //Starting getting members of the net
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Print.info("Initialiazing node...");
        initializeNode(); //Initialazing node
        ServerMulticast.getInstance().start(); //Stating server to echo my RMIport
        SearchWindow.getWindow().enableSearch(); //Enabling search
    }

    /**
     * Method to add a new member to the ring
     * @param newMember
     */
    public void addNewMemberToRing(NodeInformation newMember) {
        this.membersOfRing.add(newMember);
        if(initializedNode){
            updateSuccessorPredecessorAndRMIPorts();
            SearchWindow.getWindow().updateTableAndLables();
        }
        Print.log("\n"+this.toString());
    }

    /**
     * Method to see if the given index is my position on the ring
     * @param index
     * @return
     */
    public boolean isMyIndex(int index){
        return membersOfRing.get(index).equals(myInfo);
    }

    /**
     * Method to intialize the RMIServer
     */
    private void initializeRMIServer(){
        String ip=this.getMyIPAddress().toString();
        ip=ip.substring(1,ip.length());
        int port=this.myInfo.getRMIport();
        registry=null;
        try {
            //puerto default del rmiregistry
            try{
                registry=java.rmi.registry.LocateRegistry.createRegistry(port);
            }catch(Exception f){
                registry=java.rmi.registry.LocateRegistry.getRegistry(port); //If it already exists
            }
        } catch (Exception e) {
            System.err.println("Excepcion RMI del registry:");
            e.printStackTrace();
        }
        try {
            System.setProperty("java.rmi.server.codebase","file:/C:/Temp/searchInterface/");
            System.setProperty("java.rmi.server.hostname", ip);
            searchInterface stub = (searchInterface) UnicastRemoteObject.exportObject(instance, 0);
            try {
                registry.bind("searchInterface", stub); //Binding for the first time
            }catch(Exception f){
                registry.rebind("searchInterface", stub); //If the name was already on registry, just rebind.
            }
            Print.info("RMI server ready...");
        } catch (Exception e) {
            System.err.println("Exception on server: " + e.toString());
            e.printStackTrace();
        }
    }

    private void restartRMIServer(){
        try {
            UnicastRemoteObject.unexportObject(instance, true);
            Thread.sleep(2000);
            initializeRMIServer();
        } catch (NoSuchObjectException | InterruptedException e) {
            System.err.println("Exception on server: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Local method to initiate a search of a file on the Net and if exist, download it.
     * @param fileName
     */
    public void searchFileOnNet(String fileName){
        //First check on my own files
        if(FilesUtil.isFileOnDirectory(fileName,this.source)){
            Print.strong("I already have the file to search on my own sources folder!");
        }else{ //If i don't have it in my sources folder, then ask in the net
            String ip=successor.getIPaddress().toString();
            ip=ip.substring(1, ip.length());
            int indexToAsk=membersOfRing.indexOf(successor); //
            boolean founded=false;
            try{
                Registry registry = LocateRegistry.getRegistry(ip, successor.getRMIport());
                searchInterface stub = (searchInterface) registry.lookup("searchInterface");
                do {
                    Print.info("Asking to "+membersOfRing.get(indexToAsk).toString()+"...");
                    founded = stub.searchRemoteFile(indexToAsk, fileName);
                    if (!founded) {
                        Print.error(membersOfRing.get(indexToAsk).toString()+" didn't find the file...");
                        indexToAsk=indexToAsk!=(membersOfRing.size()-1)?indexToAsk+1:0;
                    }else{
                        Print.strong("The file was found by "+membersOfRing.get(indexToAsk).toString()+"!");
                        new ClientDownload(fileName, membersOfRing.get(indexToAsk)).start();//Start download
                    }
                }while(indexToAsk!=membersOfRing.indexOf(myInfo) && !founded); //While to round de ring in order to ask each of them succesor by succesor
                if(!founded)
                    Print.error("The file is not on the net!");
            }catch (Exception e){
                System.err.println("Something went wrong...");
                e.printStackTrace();
            }
        }
    }

    /**
     * Remote method to see if Im the node that needs to check if I have the file or i need to ask to my succesor.
     * @param indexNode
     * @param fileName
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean searchRemoteFile(int indexNode, String fileName) throws RemoteException {
        if (isMyIndex(indexNode)){
            Print.info("Someone asks if I have \""+fileName+"\"");
            boolean IHaveIt=FilesUtil.isFileOnDirectory(fileName,this.source);
            if(IHaveIt){
                Print.strong("Yes!, I have \""+fileName+"\"");
                new ServerUpload().start();
                return true;
            }else{
                Print.error("Sorry, I don't have \""+fileName+"\"");
                return false;
            }
        }else{
            try {
                Print.info("Asking to "+membersOfRing.get(indexNode).toString()+"...");
                String ip=successor.getIPaddress().toString();
                Registry registry = LocateRegistry.getRegistry(ip.substring(1, ip.length()), successor.getRMIport());
                searchInterface stub = (searchInterface) registry.lookup("searchInterface");
                boolean founded= stub.searchRemoteFile(indexNode,fileName);
                if(founded)
                    Print.strong("It seems that "+membersOfRing.get(indexNode).toString()+" have \""+fileName+"\"!");
                else
                    Print.error("It seems that "+membersOfRing.get(indexNode).toString()+" have \""+fileName+"\"!");
                return founded;
            }catch (Exception e){
                System.err.println("Something went wrong...");
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Override of method toString()
     * @return
     */
    @Override
    public String toString(){
        return ((initializedNode)?("MyInfo: "+myInfo.toString()+"\n"
                +"Successor: "+successor.toString()+"\n"
                +"Predecessor: "+predecessor.toString()+"\n"):"")
                +"Members: "+membersOfRing.toString();
    }
}
