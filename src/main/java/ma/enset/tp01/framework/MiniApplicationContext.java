package ma.enset.tp01.framework;

import ma.enset.tp01.framework.annotations.Autowired;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public abstract class MiniApplicationContext {
    protected final Map<String, Object> beans;

    protected MiniApplicationContext(Map<String, Object> beans) {
        this.beans = beans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String id, Class<T> type) {
        Object bean = beans.get(id);
        if (bean == null) {
            throw new IllegalArgumentException("Bean introuvable : " + id);
        }
        return (T) bean;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        for (Object bean : beans.values()) {
            if (type.isAssignableFrom(bean.getClass())) {
                return (T) bean;
            }
        }
        throw new IllegalArgumentException("Aucun bean de type : " + type.getName());
    }

    protected void injectDependencies(Object instance) throws Exception {
        injectFields(instance);
        injectSetters(instance);
    }

    protected Object resolveDependency(Class<?> type) {
        for (Object bean : beans.values()) {
            if (type.isAssignableFrom(bean.getClass())) {
                return bean;
            }
        }
        throw new IllegalStateException("Dependance introuvable pour le type : " + type.getName());
    }

    protected Object createWithAutowiredConstructor(Class<?> clazz) throws Exception {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                constructor.setAccessible(true);
                Object[] args = resolveConstructorArgs(constructor.getParameterTypes());
                return constructor.newInstance(args);
            }
        }
        Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
        defaultConstructor.setAccessible(true);
        return defaultConstructor.newInstance();
    }

    private Object[] resolveConstructorArgs(Class<?>[] parameterTypes) {
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            args[i] = resolveDependency(parameterTypes[i]);
        }
        return args;
    }

    private void injectFields(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    Object dependency = resolveDependency(field.getType());
                    field.set(instance, dependency);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private void injectSetters(Object instance) throws Exception {
        for (Method method : instance.getClass().getMethods()) {
            if (method.isAnnotationPresent(Autowired.class) && method.getName().startsWith("set")
                    && method.getParameterCount() == 1) {
                method.setAccessible(true);
                Object dependency = resolveDependency(method.getParameterTypes()[0]);
                method.invoke(instance, dependency);
            }
        }
    }

    protected void applyPropertyOrField(Object instance, String propertyName, Object value) throws Exception {
        String setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        for (Method method : instance.getClass().getMethods()) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1
                    && method.getParameterTypes()[0].isAssignableFrom(value.getClass())) {
                method.invoke(instance, value);
                return;
            }
        }

        Field field = findField(instance.getClass(), propertyName);
        if (field != null) {
            field.setAccessible(true);
            field.set(instance, value);
            return;
        }

        throw new NoSuchMethodException("Impossible d'injecter la propriete : " + propertyName);
    }

    protected Constructor<?> findCompatibleConstructor(Class<?> clazz, Object[] args) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() != args.length) {
                continue;
            }
            boolean compatible = true;
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (int i = 0; i < args.length; i++) {
                if (!parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                    compatible = false;
                    break;
                }
            }
            if (compatible) {
                return constructor;
            }
        }
        throw new IllegalStateException("Constructeur compatible introuvable pour : " + clazz.getName());
    }

    private Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    public Collection<Object> getAllBeans() {
        return beans.values();
    }
}
