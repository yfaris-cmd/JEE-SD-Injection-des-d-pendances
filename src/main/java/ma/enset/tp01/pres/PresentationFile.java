package ma.enset.tp01.pres;

import ma.enset.tp01.dao.IDao;
import ma.enset.tp01.metier.IMetier;
import ma.enset.tp01.metier.MetierImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;

/**
 * Injection par instanciation dynamique : les noms de classes sont lus depuis config.txt.
 */
public class PresentationFile {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Partie 1b : Instanciation dynamique ===");

        String daoClassName = null;
        String metierClassName = null;

        try (BufferedReader reader = new BufferedReader(new FileReader("config.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("dao=")) {
                    daoClassName = line.substring("dao=".length()).trim();
                } else if (line.startsWith("metier=")) {
                    metierClassName = line.substring("metier=".length()).trim();
                }
            }
        }

        Class<?> daoClass = Class.forName(daoClassName);
        Class<?> metierClass = Class.forName(metierClassName);

        IDao dao = (IDao) daoClass.getDeclaredConstructor().newInstance();
        IMetier metier = (IMetier) metierClass.getDeclaredConstructor().newInstance();

        Method setDao = metierClass.getMethod("setDao", IDao.class);
        setDao.invoke(metier, dao);

        System.out.println("Resultat du calcul : " + metier.calcul());
    }
}
