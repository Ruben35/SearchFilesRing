package interfacesRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface searchInterface extends Remote {
    boolean searchRemoteFile(int indexNode, String fileName) throws RemoteException;
}
