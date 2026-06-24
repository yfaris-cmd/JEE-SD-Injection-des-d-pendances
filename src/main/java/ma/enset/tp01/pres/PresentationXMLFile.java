package ma.enset.tp01.pres;

import ma.enset.tp01.metier.IMetier;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Injection des dependances avec Spring Framework - configuration XML.
 */
public class PresentationXMLFile {
    public static void main(String[] args) {
        System.out.println("=== Partie 1c : Spring Framework (XML) ===");

        try (ClassPathXmlApplicationContext context =
                     new ClassPathXmlApplicationContext("ApplicationContext.xml")) {
            IMetier metier = context.getBean("metier", IMetier.class);
            System.out.println("Resultat du calcul : " + metier.calcul());
        }
    }
}
