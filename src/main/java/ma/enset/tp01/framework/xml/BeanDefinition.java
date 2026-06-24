package ma.enset.tp01.framework.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class BeanDefinition {
    @XmlAttribute
    private String id;

    @XmlAttribute(name = "class")
    private String className;

    @XmlElement(name = "property")
    private List<PropertyDefinition> properties = new ArrayList<>();

    @XmlElement(name = "constructor-arg")
    private List<ConstructorArgDefinition> constructorArgs = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public List<PropertyDefinition> getProperties() {
        return properties;
    }

    public List<ConstructorArgDefinition> getConstructorArgs() {
        return constructorArgs;
    }
}
