package ma.enset.tp01.ext;

import ma.enset.tp01.dao.IDao;

public class DaoImpl2 implements IDao {
    @Override
    public double getData() {
        System.out.println("[DaoImpl2] Lecture des donnees depuis un fichier...");
        return 200;
    }
}
