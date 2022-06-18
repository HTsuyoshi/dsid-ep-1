package dsid.ep.parts;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.Vector;

/**
 * PartRepository: Interface que vai ser utilizada para poder manipular as Parts que estao disponivel no repositorio
 */
public interface PartRepository extends Remote {
    Vector<Part> getPartList() throws RemoteException;

    Part getPart(UUID code) throws RemoteException;

    void addPart(UUID code, int quantity, String name, String description) throws RemoteException;

    void addSubCompPart(UUID receive, UUID code, int quantity, String name, String description) throws RemoteException;

}