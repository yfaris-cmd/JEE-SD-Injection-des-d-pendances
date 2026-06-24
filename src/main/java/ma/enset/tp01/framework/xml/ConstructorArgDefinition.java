package ma.enset.tp01.framework.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConstructorArgDefinition {
    @XmlAttribute
    private String ref;

    public String getRef() {
        return ref;
    }
}
