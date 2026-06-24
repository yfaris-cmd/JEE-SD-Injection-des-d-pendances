package ma.enset.tp01.metier.demo;

import ma.enset.tp01.dao.IDao;
import ma.enset.tp01.framework.annotations.Autowired;
import ma.enset.tp01.framework.annotations.Component;
import ma.enset.tp01.metier.IMetier;

@Component("metierField")
public class MetierImplField implements IMetier {
    @Autowired
    private IDao dao;

    @Override
    public double calcul() {
        System.out.println("[MetierImplField] Injection via attribut (field)");
        return dao.getData() * 2;
    }
}
