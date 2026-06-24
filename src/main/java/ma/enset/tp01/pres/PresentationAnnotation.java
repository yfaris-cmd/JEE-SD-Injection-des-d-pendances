package ma.enset.tp01.pres;

import ma.enset.tp01.metier.IMetier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Injection des dependances avec Spring Framework - configuration par annotations.
 */
public class PresentationAnnotation {
    @Configuration
    @ComponentScan(basePackages = "ma.enset.tp01")
    static class SpringConfig {
    }

    public static void main(String[] args) {
        System.out.println("=== Partie 1c : Spring Framework (Annotations) ===");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SpringConfig.class)) {
            IMetier metier = context.getBean(IMetier.class);
            System.out.println("Resultat du calcul : " + metier.calcul());
        }
    }
}
