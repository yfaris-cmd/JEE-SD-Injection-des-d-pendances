package ma.enset.tp01.dao;

import org.springframework.stereotype.Repository;

@Repository
public class DaoImpl implements IDao {
    @Override
    public double getData() {
        System.out.println("[DaoImpl] Lecture des donnees depuis la base...");
        return 100;
    }
}
