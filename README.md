# TP01 — Injection des dépendances et mini-framework IoC

**Module :** Architecture JEE et Systèmes Distribués  
**Ressource vidéo :** [Cours injection des dépendances](https://www.youtube.com/watch?v=vOLqabN-n2k)

---

## Introduction

L'injection de dépendances (Dependency Injection) et l'inversion de contrôle (IoC) sont des concepts centraux en architecture JEE. Ils permettent de réduire le couplage entre les composants d'une application en déléguant la création et l'assemblage des objets à un conteneur ou à un framework.

Ce TP reprend l'exemple traité en cours :
- une couche **DAO** (`IDao`) qui fournit des données ;
- une couche **Métier** (`IMetier`) qui applique un traitement (`calcul = getData() × 2`) ;
- une couche **Présentation** qui assemble et exécute l'application.

L'objectif est de comparer plusieurs stratégies d'injection, puis de concevoir un **mini-framework IoC** inspiré de Spring.

---

## Structure du projet

```
tp01/
├── config.txt                          # Configuration pour l'instanciation dynamique
├── pom.xml
├── src/main/java/ma/enset/tp01/
│   ├── dao/
│   │   ├── IDao.java                   # Interface DAO
│   │   └── DaoImpl.java                # Implémentation DAO (Spring @Repository)
│   ├── ext/
│   │   ├── DaoImpl2.java               # 2e implémentation (couplage faible)
│   │   └── DaoImplCapteur.java         # Implémentation capteur (mini-framework)
│   ├── metier/
│   │   ├── IMetier.java                # Interface métier
│   │   ├── MetierImpl.java             # Implémentation avec couplage faible
│   │   └── demo/
│   │       ├── MetierImplConstructor.java
│   │       ├── MetierImplSetter.java
│   │       └── MetierImplField.java
│   ├── pres/                           # Couche présentation (points d'entrée)
│   │   ├── Presentation1.java
│   │   ├── PresentationFile.java
│   │   ├── PresentationXMLFile.java
│   │   ├── PresentationAnnotation.java
│   │   ├── PresentationMiniXml.java
│   │   └── PresentationMiniAnnotation.java
│   └── framework/                      # Partie 2 : mini-framework IoC
│       ├── annotations/
│       │   ├── Component.java
│       │   └── Autowired.java
│       ├── xml/
│       │   ├── BeansConfig.java
│       │   ├── BeanDefinition.java
│       │   ├── PropertyDefinition.java
│       │   └── ConstructorArgDefinition.java
│       ├── MiniApplicationContext.java
│       ├── XmlApplicationContext.java
│       └── AnnotationApplicationContext.java
└── src/main/resources/
    ├── ApplicationContext.xml          # Configuration Spring (XML)
    └── beans.xml                       # Configuration mini-framework (JAXB)
```

---

## Partie 1 : Interfaces, couplage faible et injection

### 1. Interface `IDao` et implémentation

```java
public interface IDao {
    double getData();
}
```

`DaoImpl` retourne la valeur `100`. `DaoImpl2` (dans le package `ext`) retourne `200` et montre qu'on peut remplacer l'implémentation sans modifier le métier.

### 2. Interface `IMetier` et implémentation avec couplage faible

```java
public interface IMetier {
    double calcul();
}
```

`MetierImpl` dépend de l'**interface** `IDao` et non de `DaoImpl`. C'est le **couplage faible** : le métier ignore l'implémentation concrète du DAO.

### 3. Injection des dépendances

| Approche | Classe | Principe |
|----------|--------|----------|
| **a. Instanciation statique** | `Presentation1` | `new DaoImpl()` puis `setDao()` manuellement |
| **b. Instanciation dynamique** | `PresentationFile` | Lecture de `config.txt` + réflexion (`Class.forName`) |
| **c. Spring XML** | `PresentationXMLFile` | Beans déclarés dans `ApplicationContext.xml` |
| **c. Spring Annotations** | `PresentationAnnotation` | `@Repository`, `@Service`, `@Autowired`, `@ComponentScan` |

#### a. Instanciation statique

Le programmeur crée explicitement les objets et injecte la dépendance via le setter :

```java
IDao dao = new DaoImpl();
IMetier metier = new MetierImpl();
((MetierImpl) metier).setDao(dao);
```

**Avantage :** simple. **Inconvénient :** couplage fort au niveau de la couche présentation.

#### b. Instanciation dynamique

Le fichier `config.txt` contient :

```
dao=ma.enset.tp01.dao.DaoImpl
metier=ma.enset.tp01.metier.MetierImpl
```

La classe `PresentationFile` charge les noms de classes par réflexion, instancie les objets et appelle `setDao` dynamiquement. On peut changer d'implémentation en modifiant le fichier, sans recompiler.

#### c. Spring Framework

**Version XML** (`ApplicationContext.xml`) :

```xml
<bean id="dao" class="ma.enset.tp01.dao.DaoImpl"/>
<bean id="metier" class="ma.enset.tp01.metier.MetierImpl">
    <property name="dao" ref="dao"/>
</bean>
```

**Version annotations** : `@Repository` sur `DaoImpl`, `@Service` sur `MetierImpl`, `@Autowired` sur le setter, et `@ComponentScan` dans la configuration Spring.

---

## Partie 2 : Mini-framework d'injection des dépendances

Un mini-framework IoC a été développé pour simuler le comportement de Spring avec :

1. **Configuration XML** via **JAXB** (OXM — Object/XML Mapping)
2. **Configuration par annotations** (`@Component`, `@Autowired`)
3. **Trois modes d'injection** :
   - Constructeur (`MetierImplConstructor`)
   - Setter (`MetierImplSetter`)
   - Attribut / Field (`MetierImplField`)

### Architecture du framework

```
┌─────────────────────────────────────────────────────────┐
│              MiniApplicationContext                     │
│  - Map<String, Object> beans                            │
│  - injectDependencies() : field + setter                │
│  - createWithAutowiredConstructor()                     │
└─────────────────────────────────────────────────────────┘
           ▲                           ▲
           │                           │
┌──────────┴──────────┐    ┌──────────┴──────────────────┐
│ XmlApplicationContext│    │ AnnotationApplicationContext│
│ JAXB + beans.xml     │    │ Scan @Component + @Autowired│
└─────────────────────┘    └─────────────────────────────┘
```

### Configuration XML (JAXB)

Fichier `beans.xml` :

```xml
<beans>
    <bean id="dao" class="ma.enset.tp01.ext.DaoImplCapteur"/>
    <bean id="metierConstructor" class="...MetierImplConstructor">
        <constructor-arg ref="dao"/>
    </bean>
    <bean id="metierSetter" class="...MetierImplSetter">
        <property name="dao" ref="dao"/>
    </bean>
    <bean id="metierField" class="...MetierImplField">
        <property name="dao" ref="dao"/>
    </bean>
</beans>
```

Le contexte `XmlApplicationContext` :
1. désérialise le XML avec JAXB ;
2. instancie les beans (constructeur ou constructeur par défaut) ;
3. injecte les propriétés (setter ou accès direct au champ).

### Configuration par annotations

```java
@Component
public class DaoImplCapteur implements IDao { ... }

@Component("metierField")
public class MetierImplField implements IMetier {
    @Autowired
    private IDao dao;
}
```

`AnnotationApplicationContext` scanne le classpath, détecte les classes `@Component`, instancie les beans puis résout les `@Autowired` (constructeur, setter, field).

---

## Exécution

Prérequis : **Java 17+**, **Maven 3.x**

```bash
cd tp01
mvn compile
```

| Démonstration | Commande |
|---------------|----------|
| Instanciation statique | `mvn exec:java -Dexec.mainClass=ma.enset.tp01.pres.Presentation1` |
| Instanciation dynamique | `mvn exec:java -Dexec.mainClass=ma.enset.tp01.pres.PresentationFile` |
| Spring XML | `mvn exec:java -Dexec.mainClass=ma.enset.tp01.pres.PresentationXMLFile` |
| Spring Annotations | `mvn exec:java -Dexec.mainClass=ma.enset.tp01.pres.PresentationAnnotation` |
| Mini-framework XML | `mvn exec:java -Dexec.mainClass=ma.enset.tp01.pres.PresentationMiniXml` |
| Mini-framework Annotations | `mvn exec:java -Dexec.mainClass=ma.enset.tp01.pres.PresentationMiniAnnotation` |

### Résultats attendus

| Scénario | Source de données | Résultat `calcul()` |
|----------|-------------------|---------------------|
| Partie 1 (DaoImpl) | Base de données simulée | **200.0** (100 × 2) |
| Partie 2 (DaoImplCapteur) | Capteur simulé | **100.0** (50 × 2) |

---

## Conclusion

Ce TP a permis de :

- Mettre en place une architecture en couches avec **couplage faible** via les interfaces `IDao` et `IMetier`.
- Comparer **quatre approches** d'injection : statique, dynamique (réflexion), Spring XML et Spring annotations.
- Concevoir un **mini-framework IoC** supportant la configuration XML (JAXB) et les annotations, avec injection par constructeur, setter et attribut.

Le framework maison reproduit les mécanismes fondamentaux de Spring : conteneur de beans, résolution des dépendances par type, et configuration externalisée. Spring industrialise ces concepts en ajoutant le cycle de vie des beans, l'AOP, et un écosystème complet — mais le principe reste identique : **inverser le contrôle de la création des objets**.

---
