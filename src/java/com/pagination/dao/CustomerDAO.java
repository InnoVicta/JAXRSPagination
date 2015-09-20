package com.pagination.dao;

import com.pagination.entities.Customer;
import com.pagination.entities.Customer_;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author Michael.Orokola
 */
@Stateless
public class CustomerDAO {

    @PersistenceContext(unitName = "JAXRSPaginationPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Customer> getCustomers(int startingFrom, int pageSize, String order) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
        Root<Customer> customer = cq.from(Customer.class);
        if("desc".equals(order)){
            cq.orderBy(cb.desc(customer.<String>get(Customer_.lastName)));
        }else{
            cq.orderBy(cb.asc(customer.<String>get(Customer_.lastName)));
        }
        TypedQuery<Customer> tp = getEntityManager().createQuery(cq);
        tp.setFirstResult(startingFrom);
        tp.setMaxResults(pageSize);
        return tp.getResultList();
    }
    
    public JSONArray getCustomerJson(int startingFrom, int pageSize, String order) throws JSONException{
        List<Customer> customers = getCustomers(startingFrom, pageSize, order);
        JSONObject cust;
        JSONArray customerArray = new JSONArray();
        for (Customer c : customers) {
            cust = new JSONObject();
            cust.put("lastName", c.getLastName());
            cust.put("firstName", c.getFirstName());
            cust.put("email", c.getEmail());
            cust.put("dateCreated", c.getCreateDate());
            cust.put("lastUpdated", c.getLastUpdate());
            customerArray.put(cust);
        }
        return customerArray;
    }
    
    public int count() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Customer> rt = cq.from(Customer.class);
        cq.select(cb.count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    

}
