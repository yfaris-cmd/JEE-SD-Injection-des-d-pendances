package ma.enset.tp01.pres;

import ma.enset.tp01.dao.DaoImpl;
import ma.enset.tp01.dao.IDao;
import ma.enset.tp01.metier.IMetier;
import ma.enset.tp01.metier.MetierImpl;

/**
 * Injection par instanciation statique : les classes sont creees explicitement dans le code.
 */
public class Presentation1 {
    public static void main(String[] args) {
        System.out.println("=== Partie 1a : Instanciation statique ===");

        IDao dao = new DaoImpl();
        IMetier metier = new MetierImpl();
        ((MetierImpl) metier).setDao(dao);

        System.out.println("Resultat du calcul : " + metier.calcul());
    }
}
