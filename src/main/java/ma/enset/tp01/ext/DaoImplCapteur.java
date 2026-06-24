package ma.enset.tp01.ext;

import ma.enset.tp01.dao.IDao;
import ma.enset.tp01.framework.annotations.Component;

@Component
public class DaoImplCapteur implements IDao {
    @Override
    public double getData() {
        System.out.println("[DaoImplCapteur] Lecture des donnees depuis un capteur...");
        return 50;
    }
}
