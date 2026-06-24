package ma.enset.tp01.framework;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import ma.enset.tp01.framework.xml.BeansConfig;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class XmlApplicationContext extends MiniApplicationContext {

    public XmlApplicationContext(String xmlClasspathLocation) throws Exception {
        super(initializeBeans(xmlClasspathLocation));
    }

    private static Map<String, Object> initializeBeans(String xmlClasspathLocation) throws Exception {
        Map<String, Object> beanMap = new HashMap<>();
        MiniApplicationContext helper = new MiniApplicationContext(beanMap) {};

        InputStream inputStream = XmlApplicationContext.class.getClassLoader().getResourceAsStream(xmlClasspathLocation);
        if (inputStream == null) {
            throw new IllegalArgumentException("Fichier XML introuvable : " + xmlClasspathLocation);
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(BeansConfig.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        BeansConfig config = (BeansConfig) unmarshaller.unmarshal(inputStream);

        for (var beanDefinition : config.getBeans()) {
            if (beanDefinition.getConstructorArgs().isEmpty()) {
                Class<?> clazz = Class.forName(beanDefinition.getClassName());
                Object instance = helper.createWithAutowiredConstructor(clazz);
                beanMap.put(beanDefinition.getId(), instance);
            }
        }

        for (var beanDefinition : config.getBeans()) {
            if (!beanDefinition.getConstructorArgs().isEmpty()) {
                Class<?> clazz = Class.forName(beanDefinition.getClassName());
                Object[] args = beanDefinition.getConstructorArgs().stream()
                        .map(arg -> beanMap.get(arg.getRef()))
                        .toArray();
                Constructor<?> constructor = helper.findCompatibleConstructor(clazz, args);
                beanMap.put(beanDefinition.getId(), constructor.newInstance(args));
            }
        }

        for (var beanDefinition : config.getBeans()) {
            Object instance = beanMap.get(beanDefinition.getId());
            for (var property : beanDefinition.getProperties()) {
                Object dependency = beanMap.get(property.getRef());
                helper.applyPropertyOrField(instance, property.getName(), dependency);
            }
            helper.injectDependencies(instance);
        }

        return beanMap;
    }
}
