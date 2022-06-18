package dsid.ep.parts;

import java.io.Serializable;
import java.util.UUID;
import java.util.Vector;

/**
 * Part: Interface que vai ser utilizada para represenar todas as informacoes de uma Part
 */
public interface Part extends Serializable {
    UUID getCode();

    void setCode(UUID code);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    void addSubCompPart(UUID code, String name, int quantity, String description);

    Vector getSubCompList();
}