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

    private HashMap<UUID, Part> partList;

    public PartRepositoryImpl() throws RemoteException {
        super();
        partList = new HashMap<>();
    }

    /**
     * getPartList: Retorna todas as partes que o servidor disponibiliza em formato de Vetor para ser representada como
     * uma JList no lado do Cliente
     */
    @Override
    public Vector<Part> getPartList() throws RemoteException {
        Vector<Part> list = new Vector<>();
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
     * addPart: Usado para adicionar uma referencia de Part dentro do servidor
     *
     * @param part: A part que vai ser adicionada
     */
    @Override
    public void addPart(Part part) {
        partList.put(part.getCode(), part);
    }

    /**
     * addPart: Usado para adicionar uma Part dentro do servidor, caso a parte ja exista ele modifica o nome e a
     * descricao da Part
     *
     * @param code: Codigo utilizado para identificar uma determinada peca
     * @param quantity: Quantidade de pecas que vao ser adicionadas
     * @param name: O nome da peca que vai ser adicionada
     * @param description: A descricao da peca que vai ser adicionada
     */
    @Override
    public void addPart(UUID code, int quantity, String name, String description) throws RemoteException {
        Part novaPart = partList.get(code);
        if (novaPart == null) {
            novaPart = new PartImpl(code, name, description);
            partList.put(code, novaPart);
        } else {
            novaPart.setName(name);
            novaPart.setDescription(description);
        }
    }

    /**
     * addSubCompPart: Usado para adicionar uma Part dentro dos subcomponentes de outra Part
     *
     * @param receive: A parte que vai receber a nova Part
     * @param code: O codigo da nova Part
     * @param description: A descricao da nova Part
     * @param name: O nome da nova Part
     * @param quantity: A quantidade que vai ser inserida
     */
    @Override
    public void addSubCompPart(UUID receive, UUID code, int quantity, String name, String description) throws RemoteException {
        Part receivePart = partList.get(receive);
        if (receivePart == null) return;

        receivePart.addSubCompPart(code, name, quantity, description);
    }
}
