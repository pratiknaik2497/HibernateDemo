# Spring Boot Hibernate MySQL Project Setup Documentation

## Prerequisites
- Java 21
- MySQL Server 8.0
- Gradle 8.10
- IDE (IntelliJ/Eclipse/VS Code)

---

## Step 1: Project Initialization
- Created Gradle project with Spring Boot 3.2.0
- Added dependencies: spring-boot-starter-web, spring-boot-starter-data-jpa, mysql-connector-j

**build.gradle:**
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'org.example'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.mysql:mysql-connector-j'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()
}
```

---

## Step 2: Database Configuration
**File:** src/main/resources/application.properties

| Property | Explanation |
|----------|-------------|
| server.port=8081 | Changes the default Tomcat port from 8080 to 8081 |
| spring.datasource.url | JDBC URL to connect to MySQL database |
| spring.datasource.username | MySQL database username |
| spring.datasource.password | MySQL database password |
| spring.datasource.driver-class-name | Specifies the MySQL JDBC driver class |
| spring.jpa.database-platform | Hibernate dialect for MySQL database |
| spring.jpa.hibernate.ddl-auto=none | Prevents Hibernate from auto-creating/dropping tables on startup |
| spring.jpa.show-sql=true | Enables logging of SQL queries to console for debugging |

---

## Step 3: Entity Class
**File:** src/main/java/org/example/model/Employee.java

| Annotation | Explanation |
|------------|-------------|
| @Entity | Marks the class as a JPA entity (database table) |
| @Table(name="employee") | Maps the entity to a specific database table name |
| @Id | Marks the field as the primary key |
| @Column | Maps the field to a database column |
| @GeneratedValue(strategy = GenerationType.IDENTITY) | Enables auto-increment for the primary key |

```java
package org.example.model;

import jakarta.persistence.*;
import java.sql.Date;

@Entity
@Table(name="employee")
public class Employee {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private String gender;

    @Column
    private String department;

    @Column
    private Date dob;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", department='" + department + '\'' +
                ", dob=" + dob +
                '}';
    }
}
```

---

## Step 4: DAO Interface
**File:** src/main/java/org/example/dao/EmployeeDAO.java

| Method | Explanation |
|--------|-------------|
| List<Employee> get() | Retrieves all employees from database |
| Employee get(int id) | Retrieves employee by ID |
| void save(Employee employee) | Saves or updates employee |
| void delete(int id) | Deletes employee by ID |

```java
package org.example.dao;

import org.example.model.Employee;
import java.util.List;

public interface EmployeeDAO {
    List<Employee> get();
    Employee get(int id);
    void save(Employee employee);
    void delete(int id);
}
```

---

## Step 5: DAO Implementation
**File:** src/main/java/org/example/dao/EmployeeDAOImpl.java

| Annotation | Explanation |
|------------|-------------|
| @Repository | Marks the class as a Spring data access component |
| @Autowired | Injects the EntityManager dependency |
| entityManager.unwrap(Session.class) | Gets Hibernate Session from JPA EntityManager |
| currentSession.createQuery() | Creates a Hibernate query |
| currentSession.get() | Retrieves entity by ID |
| currentSession.saveOrUpdate() | Saves new or updates existing entity |
| currentSession.remove() | Deletes entity from database |

```java
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
```

---

## Step 6: Service Interface
**File:** src/main/java/org/example/service/EmployeeService.java

| Method | Explanation |
|--------|-------------|
| List<Employee> get() | Business logic for getting all employees |
| Employee get(int id) | Business logic for getting employee by ID |
| void save(Employee employee) | Business logic for saving employee |
| void delete(int id) | Business logic for deleting employee |

```java
package org.example.service;

import org.example.model.Employee;
import java.util.List;

public interface EmployeeService {
    List<Employee> get();
    Employee get(int id);
    void save(Employee employee);
    void delete(int id);
}
```

---

## Step 7: Service Implementation
**File:** src/main/java/org/example/service/EmployeeServiceImpl.java

| Annotation | Explanation |
|------------|-------------|
| @Service | Marks the class as a Spring service component |
| @Transactional | Ensures database operations are executed within a transaction (commits on success, rolls back on error) |

```java
package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dao.EmployeeDAO;
import org.example.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeDAO employeeDAO;

    @Override
    @Transactional
    public List<Employee> get() {
        return employeeDAO.get();
    }

    @Override
    @Transactional
    public Employee get(int id) {
       return employeeDAO.get(id);
    }

    @Override
    @Transactional
    public void save(Employee employee) {
        employeeDAO.save(employee);
    }

    @Override
    @Transactional
    public void delete(int id) {
        employeeDAO.delete(id);
    }
}
```

---

## Step 8: Controller
**File:** src/main/java/org/example/controller/EmployeeController.java

| Annotation | Explanation |
|------------|-------------|
| @Controller | Marks the class as a Spring MVC controller |
| @RequestMapping("/api") | Sets base URL path for all endpoints in this controller |
| @Autowired | Injects the EmployeeService dependency |
| @ResponseBody | Returns data as JSON instead of a view name |
| @PostMapping | Maps HTTP POST requests to the method |
| @GetMapping | Maps HTTP GET requests to the method |
| @DeleteMapping | Maps HTTP DELETE requests to the method |
| @PutMapping | Maps HTTP PUT requests to the method |
| @RequestBody | Binds HTTP request body to Employee object |
| @PathVariable | Extracts value from URL path |
| @PutMapping | Maps HTTP PUT requests to the method |

```java
package org.example.controller;

import org.example.model.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @ResponseBody
    @PostMapping("/employee/add")
    public void addEmployee(@RequestBody Employee employee){
        employeeService.save(employee);
        System.out.println("Employee Added");
    }

    @ResponseBody
    @GetMapping("/employee")
    public List<Employee> getEmployee(){
       return employeeService.get();
    }

    @ResponseBody
    @DeleteMapping("/employee/delete/{id}")
    public void deleteEmployee(@PathVariable int id){
        employeeService.delete(id);
        System.out.println("Employee Deleted");
    }

    @ResponseBody
    @PutMapping("/employee/update")
    public void updateEmployee(@RequestBody Employee employee, Model model){
        employeeService.save(employee);
        System.out.println("Employee Updated");
    }
}
```

---

## Database Setup
```sql
CREATE DATABASE crudapi;
USE crudapi;

CREATE TABLE EMPLOYEE(
    ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(50),
    GENDER VARCHAR(10),
    DEPARTMENT VARCHAR(50),
    DOB DATE
);
```

---

## Running the Application
```bash
gradlew.bat bootRun
```

Access endpoints:
- GET http://localhost:8081/api/employee — Returns JSON list of employees
- POST http://localhost:8081/api/employee/add — Add new employee (JSON body)
- PUT http://localhost:8081/api/employee/update — Update employee (JSON body with id)
- DELETE http://localhost:8081/api/employee/delete/{id} — Delete employee
