package ma.enset.tp01.pres;

import ma.enset.tp01.metier.IMetier;
import ma.enset.tp01.framework.AnnotationApplicationContext;

/**
 * Demonstration du mini-framework IoC - configuration par annotations.
 */
public class PresentationMiniAnnotation {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Partie 2 : Mini-Framework IoC (Annotations) ===");

        AnnotationApplicationContext context = new AnnotationApplicationContext("ma.enset.tp01");

        System.out.println("\n--- Injection par constructeur ---");
        IMetier metierConstructor = context.getBean("metierConstructor", IMetier.class);
        System.out.println("Resultat : " + metierConstructor.calcul());

        System.out.println("\n--- Injection par setter ---");
        IMetier metierSetter = context.getBean("metierSetter", IMetier.class);
        System.out.println("Resultat : " + metierSetter.calcul());

        System.out.println("\n--- Injection par attribut (field) ---");
        IMetier metierField = context.getBean("metierField", IMetier.class);
        System.out.println("Resultat : " + metierField.calcul());
    }
}
