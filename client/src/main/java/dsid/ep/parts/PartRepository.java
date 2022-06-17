package dsid.ep.parts;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.Vector;

public interface PartRepository extends Remote {
    Vector<PartImpl> getPartList() throws RemoteException;

    Part getPart(UUID code) throws RemoteException;

    void addPart(UUID code, int quantity, String name, String description) throws RemoteException;
}