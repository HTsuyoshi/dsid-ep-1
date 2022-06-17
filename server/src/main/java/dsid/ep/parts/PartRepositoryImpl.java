package dsid.ep.parts;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

/**
 * PartRepositoryImpl: Implementa interface PartRepository, vai ser utilizada para poder manipular as Parts que estao
 * disponivel no repositorio
 *
 * @attr partList: Um HashMap que guarda o codigo de uma Part e mapeia ele com a sua respectiva Part
 */
public class PartRepositoryImpl extends UnicastRemoteObject implements PartRepository {

    private HashMap<UUID, PartImpl> partList;

    public PartRepositoryImpl() throws RemoteException {
        super();
        partList = new HashMap<>();
    }

    /**
     * getPartList: Retorna todas as partes que o servidor disponibiliza em formato de Vetor para ser representada como
     * uma JList no lado do Cliente
     */
    @Override
    public Vector<PartImpl> getPartList() throws RemoteException {
        Vector<PartImpl> list = new Vector<PartImpl>();
        list.addAll(partList.values());
        return list;
    }

    /**
     * getPartList: Retorna todas as partes que o servidor disponibiliza em formato de Vetor para ser representada como
     * uma JList no lado do Cliente
     */
    @Override
    public Part getPart(UUID code) throws RemoteException {
        System.err.println(code);
        Part part = partList.get(code);
        System.err.println(part.getCode());
        if(part == null) return null;
        return part;
    }

    @Override
    public void addPart(UUID code, int quantity, String name, String description) throws RemoteException {
        PartImpl part = new PartImpl(code, name, description);
        partList.put(code, part);
    }
}
