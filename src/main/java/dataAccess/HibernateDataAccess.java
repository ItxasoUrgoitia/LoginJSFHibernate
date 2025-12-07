package dataAccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import domain.Car;
import domain.Driver;
import domain.Passenger;
import domain.Ride;
import domain.User;
import exceptions.DriverDoesNotExistException;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
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
            throws RideMustBeLaterThanTodayException, RideAlreadyExistException, DriverDoesNotExistException {
        if (date.before(new Date())) {
            throw new RideMustBeLaterThanTodayException("The ride date must be later than today");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            List<Driver> driver = em.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)
                    .setParameter("email", driverEmail)
                    .getResultList();
            if (driver.isEmpty()) {
                throw new DriverDoesNotExistException("There is no user with that email");
            }
            
            for (Ride r : driver.get(0).getRides()) {
                if (r.getFrom().equals(from) && r.getTo().equals(to) && r.getDate().equals(date)) {
                    throw new RideAlreadyExistException("Ride already exists for this driver");
                }
            }

            Ride ride = new Ride(from, to, date, nPlaces, price, driver.get(0));
            driver.get(0).getRides().add(ride);
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
    
    public User isRegistered(String email, String pasahitza) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.email = :email AND u.pasahitza = :pasahitza";
            User user = em.createQuery(jpql, User.class)
                          .setParameter("email", email)
                          .setParameter("pasahitza", pasahitza)
                          .getSingleResult();

            System.out.println("Login successful: " + user.getClass().getSimpleName());
            return user;
        }catch(NoResultException e) {
        	return null;
        } catch (Exception e) {
            e.printStackTrace(); 
            return null;
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
            Driver d1 = new Driver("alice@example.com", "Alice", "456");
            Driver d2 = new Driver("bob@example.com", "Bob", "789");

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
    
    public void createDriver(Driver driver) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            String checkSql = "SELECT email FROM users WHERE email = ?";
            List<?> result = em.createNativeQuery(checkSql)
                               .setParameter(1, driver.getEmail())
                               .getResultList();

            if (!result.isEmpty()) {
                throw new RuntimeException("User with email " + driver.getEmail() + " already exists.");
            }

            // 2. Insertar en la tabla users
            String insertUserSql = "INSERT INTO users (email, name, pasahitza) VALUES (?, ?, ?)";
            em.createNativeQuery(insertUserSql)
              .setParameter(1, driver.getEmail())
              .setParameter(2, driver.getName())
              .setParameter(3, driver.getPasahitza())
              .executeUpdate();

            // 3. Insertar en la tabla Driver (por JOINED)
            String insertDriverSql = "INSERT INTO Driver (email) VALUES (?)";
            em.createNativeQuery(insertDriverSql)
              .setParameter(1, driver.getEmail())
              .executeUpdate();

            tx.commit();
            System.out.println("Driver registered with SQL: " + driver.getEmail());

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void createPassenger(Passenger passenger) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            
            String checkSql = "SELECT email FROM users WHERE email = ?";
            List<?> result = em.createNativeQuery(checkSql)
                               .setParameter(1, passenger.getEmail())
                               .getResultList();

            if (!result.isEmpty()) {
                throw new RuntimeException("User with email " + passenger.getEmail() + " already exists.");
            }

           
            String insertUserSql = "INSERT INTO users (email, name, pasahitza) VALUES (?, ?, ?)";
            em.createNativeQuery(insertUserSql)
              .setParameter(1, passenger.getEmail())
              .setParameter(2, passenger.getName())
              .setParameter(3, passenger.getPasahitza())
              .executeUpdate();

            String insertPassengerSql = "INSERT INTO passenger (email) VALUES (?)";
            em.createNativeQuery(insertPassengerSql)
              .setParameter(1, passenger.getEmail())
              .executeUpdate();

            tx.commit();
            System.out.println("Passenger registered (SQL): " + passenger.getEmail());

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    
    public boolean addCar(String licensePlate, int places, String model, String color, String driverEmail) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            Driver driver = em.find(Driver.class, driverEmail);
            if (driver == null) {
                System.out.println("Driver no encontrado: " + driverEmail);
                tx.rollback();
                return false;
            }
            
            if (driver.doesCarExist(licensePlate)) {
                System.out.println("Car already exists: " + licensePlate);
                tx.rollback();
                return false;
            }
            
            Car car = driver.addCar(licensePlate, places, model, color);
            em.persist(car);
            
            driver.getCars().add(car);
            em.merge(driver);
            
            tx.commit();
            System.out.println("Car added successfully: " + licensePlate);
            return true;
            
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
    
    public boolean deleteUser(String email) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        
        try {
            tx = em.getTransaction();
            tx.begin();
            
            // Delete all rides for the driver
            em.createNativeQuery("DELETE FROM Ride WHERE driver_email = ?")
              .setParameter(1, email)
              .executeUpdate();
            
            // Delete cars
            em.createNativeQuery("DELETE FROM Car WHERE driver_email = ?")
              .setParameter(1, email)
              .executeUpdate();
            
            // Delete from Passenger
            em.createNativeQuery("DELETE FROM Passenger WHERE email = ?")
              .setParameter(1, email)
              .executeUpdate();
            
            // Delete from Driver
            em.createNativeQuery("DELETE FROM Driver WHERE email = ?")
              .setParameter(1, email)
              .executeUpdate();
            
            // Finally delete from users
            em.createNativeQuery("DELETE FROM users WHERE email = ?")
              .setParameter(1, email)
              .executeUpdate();
            
            tx.commit();
            
            // Verify deletion
            List<?> remaining = em.createNativeQuery("SELECT email FROM users WHERE email = ?")
                                 .setParameter(1, email)
                                 .getResultList();
            
            return remaining.isEmpty();
            
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    public boolean userExists(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.find(User.class, email);
            return user != null;
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }
}