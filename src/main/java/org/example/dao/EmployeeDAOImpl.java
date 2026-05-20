package org.example.dao;

import jakarta.persistence.EntityManager;
import org.example.model.Employee;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Employee> get() {
       Session currentSession = entityManager.unwrap(Session.class);
       Query<Employee> query = currentSession.createQuery("From Employee", Employee.class);
        return query.getResultList();
    }

    @Override
    public Employee get(int id) {
        Session currentSession = entityManager.unwrap(Session.class);
        Employee employee = currentSession.get(Employee.class,id);
        if(employee==null){
            throw new RuntimeException("Employee with id "+id+" does not exist");
        }
        return employee;
    }

    @Override
    public void save(Employee employee) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.saveOrUpdate(employee);
    }

    @Override
    public void delete(int id) {
        Session currentSession = entityManager.unwrap(Session.class);
        Employee employee = get(id);
        currentSession.remove(employee);
    }
}
