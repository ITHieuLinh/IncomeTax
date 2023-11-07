package Repository;

import DataAccess.IncomeTaxDAO;

public class IncomeTaxRepository implements IIncomeTaxRepository {

    @Override
    public void addFamily() {
        IncomeTaxDAO.Instance().addFamily();
    }

}
