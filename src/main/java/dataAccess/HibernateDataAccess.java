package dataAccess;

import java.util.Date;
import java.util.List;



import domain.Driver;
import domain.Ride;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TemporalType;

public class HibernateDataAccess {
    private EntityManagerFactory emf;
    
    public HibernateDataAccess() {
        System.out.println("Iniciando HibernateDataAccess...");
        emf = Persistence.createEntityManagerFactory("RidesHibernatePU");
        System.out.println("Hibernate configurado OK");
    }
    
    public void test() {
        System.out.println("Probando conexión...");
        EntityManager em = emf.createEntityManager();
        em.close();
        System.out.println("Test completado");
    }
    
    public List<String> getDepartCities() {
        EntityManager em = emf.createEntityManager();
        List<String> result = em.createQuery("SELECT DISTINCT r.from FROM Ride r", String.class).getResultList();
        em.close();
        return result;
    }

    public List<String> getArrivalCities(String from) {
        EntityManager em = emf.createEntityManager();
        List<String> result = em.createQuery("SELECT DISTINCT r.to FROM Ride r WHERE r.from = :from", String.class).setParameter("from", from).getResultList();
        em.close();
        return result;
    }

    public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
            throws RideMustBeLaterThanTodayException, RideAlreadyExistException {
        if (date.before(new Date())) {
            throw new RideMustBeLaterThanTodayException("The ride date must be later than today");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Driver driver = em.find(Driver.class, driverEmail);
            if (driver == null) {
                driver = new Driver(driverEmail, "Unknown");
                em.persist(driver);
            }

            
            for (Ride r : driver.getRides()) {
                if (r.getFrom().equals(from) && r.getTo().equals(to) && r.getDate().equals(date)) {
                    throw new RideAlreadyExistException("Ride already exists for this driver");
                }
            }

            Ride ride = new Ride(from, to, date, nPlaces, price, driver);
            driver.getRides().add(ride);
            em.persist(ride);

            tx.commit();
            return ride;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Ride> getRides(String from, String to, Date date) {
        EntityManager em = emf.createEntityManager();
        try {
            String query = "SELECT r FROM Ride r WHERE r.from = :from AND r.to = :to AND r.date = :date";
            return em.createQuery(query, Ride.class)
                     .setParameter("from", from)
                     .setParameter("to", to)
                     .setParameter("date", date)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
        EntityManager em = emf.createEntityManager();
        try {
            String query = "SELECT DISTINCT r.date FROM Ride r WHERE r.from = :from AND r.to = :to AND MONTH(r.date) = MONTH(:date) AND YEAR(r.date) = YEAR(:date)";
            return em.createQuery(query, Date.class)
                     .setParameter("from", from)
                     .setParameter("to", to)
                     .setParameter("date", date, TemporalType.DATE)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public void initializeDB() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Ejemplo: crear algunos conductores y rides
            Driver d1 = new Driver("alice@example.com", "Alice");
            Driver d2 = new Driver("bob@example.com", "Bob");

            em.persist(d1);
            em.persist(d2);

            Ride r1 = new Ride("CityA", "CityB", new Date(System.currentTimeMillis() + 86400000), 3, 25.0f, d1);
            Ride r2 = new Ride("CityB", "CityC", new Date(System.currentTimeMillis() + 172800000), 2, 30.0f, d2);

            em.persist(r1);
            em.persist(r2);

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    
    } 
}