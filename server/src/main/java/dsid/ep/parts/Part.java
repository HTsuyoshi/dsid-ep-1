package dsid.ep.parts;

import java.util.UUID;
import java.util.Vector;

public interface Part {
    UUID getCode();

    void setCode(UUID code);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    void addSubCompPart(UUID code, String name, int quantity, String description);

    Vector getSubCompList();
}