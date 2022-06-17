package dsid.ep;

import dsid.ep.parts.*;

import java.util.List;

public interface ClienteInterface {
    public Boolean connect();
    public void getRepositoryInformation();
    public List<Part> getRepositoryParts();
    public Part getRepositoryPart();
    public Boolean addRepositoryPart();
}
