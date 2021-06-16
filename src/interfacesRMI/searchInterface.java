package interfacesRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface searchInterface extends Remote {
    int searchNumberRemote(int indexNode, int number) throws RemoteException;
    boolean searchRemoteFile(int indexNode, String fileName) throws RemoteException;
}
