package ma.enset.tp01.framework;

import ma.enset.tp01.framework.annotations.Component;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class AnnotationApplicationContext extends MiniApplicationContext {

    public AnnotationApplicationContext(String basePackage) throws Exception {
        super(loadBeans(basePackage));
    }

    private static Map<String, Object> loadBeans(String basePackage) throws Exception {
        Map<String, Object> beanMap = new HashMap<>();
        AnnotationApplicationContext context = new AnnotationApplicationContext(beanMap);

        for (Class<?> clazz : context.findComponentClasses(basePackage)) {
            Object instance = context.createWithAutowiredConstructor(clazz);
            beanMap.put(resolveBeanName(clazz), instance);
        }

        for (Object bean : beanMap.values()) {
            context.injectDependencies(bean);
        }

        return beanMap;
    }

    private AnnotationApplicationContext(Map<String, Object> beans) {
        super(beans);
    }

    private static String resolveBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        if (component != null && !component.value().isEmpty()) {
            return component.value();
        }
        return Character.toLowerCase(clazz.getSimpleName().charAt(0)) + clazz.getSimpleName().substring(1);
    }

    private Iterable<Class<?>> findComponentClasses(String basePackage) throws Exception {
        Map<String, Class<?>> discovered = new HashMap<>();
        String path = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if ("file".equals(resource.getProtocol())) {
                Path directory = Paths.get(resource.toURI());
                scanDirectory(basePackage, directory, discovered);
            }
        }

        if (discovered.isEmpty()) {
            scanFallbackPackages(basePackage, discovered);
        }

        return discovered.values();
    }

    private void scanDirectory(String basePackage, Path directory, Map<String, Class<?>> discovered)
            throws Exception {
        if (!Files.isDirectory(directory)) {
            return;
        }

        try (Stream<Path> paths = Files.list(directory)) {
            for (Path path : paths.toList()) {
                if (Files.isDirectory(path)) {
                    scanDirectory(basePackage + "." + path.getFileName(), path, discovered);
                } else if (path.getFileName().toString().endsWith(".class")) {
                    String className = basePackage + '.' + path.getFileName().toString().replace(".class", "");
                    registerIfComponent(className, discovered);
                }
            }
        }
    }

    private void scanFallbackPackages(String basePackage, Map<String, Class<?>> discovered)
            throws ClassNotFoundException {
        String[] packages = {
                basePackage + ".ext",
                basePackage + ".metier.demo"
        };

        for (String pkg : packages) {
            String resourcePath = pkg.replace('.', '/');
            URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
            if (resource == null) {
                continue;
            }

            File directory = new File(resource.getFile());
            if (!directory.exists()) {
                continue;
            }

            File[] files = directory.listFiles();
            if (files == null) {
                continue;
            }

            for (File file : files) {
                if (file.getName().endsWith(".class")) {
                    String className = pkg + '.' + file.getName().replace(".class", "");
                    registerIfComponent(className, discovered);
                }
            }
        }
    }

    private void registerIfComponent(String className, Map<String, Class<?>> discovered)
            throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        if (clazz.isAnnotationPresent(Component.class)) {
            discovered.put(className, clazz);
        }
    }
}
