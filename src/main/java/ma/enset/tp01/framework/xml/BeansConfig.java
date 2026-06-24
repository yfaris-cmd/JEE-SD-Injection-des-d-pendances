package ma.enset.tp01.framework.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "beans")
@XmlAccessorType(XmlAccessType.FIELD)
public class BeansConfig {
    @XmlElement(name = "bean")
    private List<BeanDefinition> beans = new ArrayList<>();

    public List<BeanDefinition> getBeans() {
        return beans;
    }
}
