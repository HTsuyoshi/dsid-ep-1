package dsid.ep.parts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

/**
 * PartImpl: A representacao de uma Part
 *
 * @attr code: O codigo que e usado como identificador unico para representar uma determinada parte
 * @attr name: O nome de uma Part
 * @attr description: A descricao de uma Part
 * @attr subParts: Uma lista contendo todas as Part que compoem essa Part
 */
public class PartImpl implements Part, Serializable {

    private UUID code;
    private String name;
    private String description;
    HashMap<PartImpl, Integer> subParts;

    public PartImpl(UUID code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.subParts = new HashMap<>();
    }

    @Override
    public UUID getCode() {
        return this.code;
    }

    @Override
    public void setCode(UUID code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;

    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void addSubCompPart(UUID code, String name, int quantity, String description) {
        this.subParts.put(new PartImpl(code, name, description), quantity);
    }

    @Override
    public Vector getSubCompList() {
        Vector<Map.Entry<PartImpl, Integer>> list =  new Vector<>();
        for (Map.Entry<PartImpl, Integer> e : this.subParts.entrySet()) { list.add(e);
        }
        return list;
    }
}