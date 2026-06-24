package ma.enset.tp01.metier.demo;

import ma.enset.tp01.dao.IDao;
import ma.enset.tp01.framework.annotations.Autowired;
import ma.enset.tp01.framework.annotations.Component;
import ma.enset.tp01.metier.IMetier;

@Component("metierSetter")
public class MetierImplSetter implements IMetier {
    private IDao dao;

    public MetierImplSetter() {
    }

    @Autowired
    public void setDao(IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        System.out.println("[MetierImplSetter] Injection via setter");
        return dao.getData() * 2;
    }
}
