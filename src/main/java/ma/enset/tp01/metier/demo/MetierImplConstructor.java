package ma.enset.tp01.metier.demo;

import ma.enset.tp01.dao.IDao;
import ma.enset.tp01.framework.annotations.Autowired;
import ma.enset.tp01.framework.annotations.Component;
import ma.enset.tp01.metier.IMetier;

@Component("metierConstructor")
public class MetierImplConstructor implements IMetier {
    private final IDao dao;

    @Autowired
    public MetierImplConstructor(IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        System.out.println("[MetierImplConstructor] Injection via constructeur");
        return dao.getData() * 2;
    }
}
