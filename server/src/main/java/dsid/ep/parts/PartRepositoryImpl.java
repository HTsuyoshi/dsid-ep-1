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
        Vector<PartImpl> list = new Vector<>();
        list.addAll(partList.values());
        return list;
    }

    /**
     * getPart: Usado para retornar uma Part especifica
     *
     * @param code: A partir do codigo UUID ele vai retornar a peca que foi pedida
     */
    @Override
    public Part getPart(UUID code) throws RemoteException {
        Part part = partList.get(code);
        if(part == null) return null;
        return part;
    }

    /**
     * addPart: Usado para adicionar uma Part dentro do servidor
     *
     * @param code: Codigo utilizado para identificar uma determinada peca
     * @param quantity: Quantidade de pecas que vao ser adicionadas
     * @param name: O nome da peca que vai ser adicionada
     * @param description: A descricao da peca que vai ser adicionada
     */
    @Override
    public void addPart(UUID code, int quantity, String name, String description) throws RemoteException {
        PartImpl part = new PartImpl(code, name, description);
        partList.put(code, part);
    }
}
