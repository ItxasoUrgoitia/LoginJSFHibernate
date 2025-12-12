package dataAccess;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import domain.Car;
import domain.Driver;
import domain.Passenger;
import domain.Request;
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
import jakarta.persistence.TypedQuery;

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

	//Irteera hiri guztiak lortzeko metodoa
	public List<String> getDepartCities() {
		EntityManager em = emf.createEntityManager();
		List<String> result = em.createQuery("SELECT DISTINCT r.from FROM Ride r", String.class).getResultList();
		em.close();
		return result;
	}

	//Helmuga-hiriak lortzeko metodoa (irteera-hiriaren arabera)
	public List<String> getArrivalCities(String from) {
		EntityManager em = emf.createEntityManager();
		List<String> result = em.createQuery("SELECT DISTINCT r.to FROM Ride r WHERE r.from = :from", String.class)
				.setParameter("from", from).getResultList();
		em.close();
		return result;
	}

	//Ride berri bat sortzeko metodoa
	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
			throws RideMustBeLaterThanTodayException, RideAlreadyExistException, DriverDoesNotExistException {
		
		//Data egungo data baino bernaduago izan behar da
		if (date.before(new Date())) {
			throw new RideMustBeLaterThanTodayException("The ride date must be later than today");
		}

		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			//Gidaria bilatu
			List<Driver> driver = em.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class)
					.setParameter("email", driverEmail).getResultList();
			if (driver.isEmpty()) {
				throw new DriverDoesNotExistException("There is no user with that email");
			}

			//Ride bakoitza egiaztatu
			for (Ride r : driver.get(0).getRides()) {
				if (r.getFrom().equals(from) && r.getTo().equals(to) && r.getDate().equals(date)) {
					throw new RideAlreadyExistException("Ride already exists for this driver");
				}
			}

			//Ride berria sortu
			Ride ride = new Ride(from, to, date, nPlaces, price, driver.get(0));
			driver.get(0).getRides().add(ride);
			em.persist(ride);

			tx.commit();
			return ride;

		} catch (RuntimeException e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	
	//Ride-ak bilatu irteera, helmuga eta dataren arabera
	public List<Ride> getRides(String from, String to, Date date) {
		EntityManager em = emf.createEntityManager();
		try {
			String query = "SELECT r FROM Ride r WHERE r.from = :from AND r.to = :to AND r.date = :date";
			return em.createQuery(query, Ride.class).setParameter("from", from).setParameter("to", to)
					.setParameter("date", date).getResultList();
		} finally {
			em.close();
		}
	}

	//Gidaria lortu emailaren arabera
	public Driver getDriver(String email) {
		EntityManager em = emf.createEntityManager();
		try {
			TypedQuery<Driver> query = em.createQuery("SELECT d FROM Driver d WHERE d.email = :email", Driver.class);
			query.setParameter("email", email);
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		} finally {
			em.close();
		}
	}
	
	public Passenger getPassenger(String email) {
		EntityManager em = emf.createEntityManager();
		try {
			TypedQuery<Passenger> query = em.createQuery("SELECT d FROM Passenger d WHERE d.email = :email", Passenger.class);
			query.setParameter("email", email);
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		} finally {
			em.close();
		}
	}

	//Ridea lortu IDaren arabera
	public Ride getRideById(Integer id) {
		EntityManager em = emf.createEntityManager();
		try {
			return em.find(Ride.class, id);
		} finally {
			em.close();
		}
	}

	//Erabiltzailea erregistratuta dagoen egiaztatu
	public User isRegistered(String email, String pasahitza) {
		EntityManager em = emf.createEntityManager();
		try {
			String jpql = "SELECT u FROM User u WHERE u.email = :email AND u.pasahitza = :pasahitza";
			User user = em.createQuery(jpql, User.class).setParameter("email", email)
					.setParameter("pasahitza", pasahitza).getSingleResult();

			System.out.println("Login successful: " + user.getClass().getSimpleName());
			return user;
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

	//Hilabete bereko datak lortu ride-ekin
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
		EntityManager em = emf.createEntityManager();
		try {
			String query = "SELECT DISTINCT r.date FROM Ride r WHERE r.from = :from AND r.to = :to AND MONTH(r.date) = MONTH(:date) AND YEAR(r.date) = YEAR(:date)";
			return em.createQuery(query, Date.class).setParameter("from", from).setParameter("to", to)
					.setParameter("date", date, TemporalType.DATE).getResultList();
		} finally {
			em.close();
		}
	}

	//Datuak hasieratzeko metodoa (datuak gehitzen ditu)
	public void initializeDB() {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			//Gidariak sortu
			Driver d1 = new Driver("alice@example.com", "Alice", "456");
			Driver d2 = new Driver("bob@example.com", "Bob", "789");
			em.persist(d1);
			em.persist(d2);

			//Bidaiaria sortu
			Passenger p1 = new Passenger("a@gmail.com", "a", "123");
			p1.setMoney(100);
			em.persist(p1);

			//Rideak sortu
			Ride r1 = new Ride("CityA", "CityB", new Date(System.currentTimeMillis() + 86400000), 3, 2.0f, d1);
			Ride r2 = new Ride("CityB", "CityC", new Date(System.currentTimeMillis() + 172800000), 2, 3.0f, d2);
			Ride r3 = new Ride("CityC", "CityD", new Date(System.currentTimeMillis() + 86400000), 3, 2.0f, d1);
			Ride r4 = new Ride("CityD", "CityE", new Date(System.currentTimeMillis() + 172800000), 2, 3.0f, d2);
			Ride r5 = new Ride("CityE", "CityF", new Date(System.currentTimeMillis() + 86400000), 3, 2.0f, d1);
			Ride r6 = new Ride("CityF", "CityG", new Date(System.currentTimeMillis() + 172800000), 2, 3.0f, d2);

			em.persist(r1);
			em.persist(r2);
			em.persist(r3);
			em.persist(r4);
			em.persist(r5);
			em.persist(r6);

			//Kotxeak sortu
			Car c1 = new Car("1234ABC", 3, "fsdf", "black", d1);
			Car c2 = new Car("2345ABC", 4, "fsf", "blue", d1);

			em.persist(c1);
			em.persist(c2);
			
			
			tx.commit();
			
			
            

		} catch (RuntimeException e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.close();
		}

	}
	
	//Gidari bat sortzeko metodoa
	public void createDriver(Driver driver) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			
			//Emaila dagoeneko existitzen den begiratu
			String checkSql = "SELECT email FROM users WHERE email = ?";
			List<?> result = em.createNativeQuery(checkSql).setParameter(1, driver.getEmail()).getResultList();

			if (!result.isEmpty()) {
				throw new RuntimeException("User with email " + driver.getEmail() + " already exists.");
			}

			// Erabiltzailea users taulan sartu
			String insertUserSql = "INSERT INTO users (email, name, pasahitza) VALUES (?, ?, ?)";
			em.createNativeQuery(insertUserSql).setParameter(1, driver.getEmail()).setParameter(2, driver.getName())
					.setParameter(3, driver.getPasahitza()).executeUpdate();

			//Gidaria gidarien taulan sartu
			String insertDriverSql = "INSERT INTO Driver (email) VALUES (?)";
			em.createNativeQuery(insertDriverSql).setParameter(1, driver.getEmail()).executeUpdate();

			tx.commit();
			System.out.println("Driver registered with SQL: " + driver.getEmail());

		} catch (RuntimeException e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	//Bidaiari berri bat sortu
	public void createPassenger(Passenger passenger) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			// Existitzen den koprobatu
			String checkSql = "SELECT email FROM users WHERE email = ?";
			List<?> result = em.createNativeQuery(checkSql).setParameter(1, passenger.getEmail()).getResultList();
			if (!result.isEmpty()) {
				throw new RuntimeException("User with email " + passenger.getEmail() + " already exists.");
			}

			//Usersen gorde
			String insertUserSql = "INSERT INTO users (email, name, pasahitza) VALUES (?, ?, ?)";
			em.createNativeQuery(insertUserSql).setParameter(1, passenger.getEmail())
					.setParameter(2, passenger.getName()).setParameter(3, passenger.getPasahitza()).executeUpdate();

			//Passenger taulan sartu (dirua 0rekin hasieratu)
			passenger.setMoney(0f);
			String insertPassengerSql = "INSERT INTO passenger (email, money) VALUES (?, ?)";
			em.createNativeQuery(insertPassengerSql).setParameter(1, passenger.getEmail())
					.setParameter(2, passenger.getMoney()).executeUpdate();

			tx.commit();
			System.out.println("Passenger registered (SQL): " + passenger.getEmail());

		} catch (RuntimeException e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	//Kotxe berria gehitzeko metodoa
	public boolean addCar(String licensePlate, int places, String model, String color, String driverEmail) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			//Gidaria bilatu
			Driver driver = em.find(Driver.class, driverEmail);
			if (driver == null) {
				System.out.println("Driver no encontrado: " + driverEmail);
				tx.rollback();
				return false;
			}

			//Kotxearen matrikula dagoeneko existitzen den egiaztatu
			if (driver.doesCarExist(licensePlate)) {
				System.out.println("Car already exists: " + licensePlate);
				tx.rollback();
				return false;
			}

			//Kotxe berria sortu
			Car car = driver.addCar(licensePlate, places, model, color);
			em.persist(car);

			//Gidariaren kotxe zerrenda eguneratu
			driver.getCars().add(car);
			em.merge(driver);

			tx.commit();
			System.out.println("Car added successfully: " + licensePlate);
			return true;

		} catch (RuntimeException e) {
			if (tx.isActive())
				tx.rollback();
			e.printStackTrace();
			return false;
		} finally {
			em.close();
		}
	}

	//Erabiltzailea ezabatzeko metodoa
	public boolean deleteUser(String email) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;

		try {
			tx = em.getTransaction();
			tx.begin();

			// Gidariaren ride guztiak ezabatu
			em.createNativeQuery("DELETE FROM Ride WHERE driver_email = ?").setParameter(1, email).executeUpdate();

			// Kotxeak ezabatu
			em.createNativeQuery("DELETE FROM Car WHERE driver_email = ?").setParameter(1, email).executeUpdate();

			// Bidaiaria ezabatu
			em.createNativeQuery("DELETE FROM Passenger WHERE email = ?").setParameter(1, email).executeUpdate();

			// Gidaria ezabatu
			em.createNativeQuery("DELETE FROM Driver WHERE email = ?").setParameter(1, email).executeUpdate();

			// Erabiltzailea users-etik ezabatu
			em.createNativeQuery("DELETE FROM users WHERE email = ?").setParameter(1, email).executeUpdate();

			tx.commit();

			// Egiaztatu ezabatu dela
			List<?> remaining = em.createNativeQuery("SELECT email FROM users WHERE email = ?").setParameter(1, email)
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

	//Erabiltzailea existitzen den egiaztatzeko
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

	//Kotxe guztiak lortzeko
	public List<Car> getAllCars() {
		EntityManager em = emf.createEntityManager();
		try {
			TypedQuery<Car> query = em.createQuery("SELECT c FROM Car c", Car.class);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//Dirua gehitzeko metodoa bidaiari baten kontuan
	public boolean depositMoney(String email, float amount) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction(); 
		try {
			tx.begin(); 

            // Bidaiaria bilatu
			Passenger p = em.createQuery("SELECT p FROM Passenger p WHERE p.email = :email", Passenger.class)
					.setParameter("email", email).getSingleResult();

			if (p == null) {
				tx.rollback(); 
				return false;
			}

            // Dirua gehitu
			p.setMoney(p.getMoney() + amount);
			em.merge(p);
			tx.commit(); 
			return true;

		} catch (Exception e) {
			if (tx.isActive()) {
				tx.rollback(); 
			}
			e.printStackTrace();
			return false;
		} finally {
			em.close();
		}
	}

    // Ride guztiak lortzeko metodoa 
	public List<Ride> getAllRides() {
		EntityManager em = emf.createEntityManager();
		try {
			String q = "SELECT r FROM Ride r WHERE r.cancelled = false";
			TypedQuery<Ride> query = em.createQuery(q, Ride.class);
			return query.getResultList();
		} finally {
			em.close();
		}
	}

    // Eskaera berri bat sortzeko metodoa
	public boolean createRequest(String passengerEmail, Integer rideNumber, int seatsRequested) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			// Ride-a bilatu
			Ride ride = em.find(Ride.class, rideNumber);
			if (ride == null || ride.isCancelled()) {
				tx.rollback();
				return false;
			}

			//Bidaiaria bilatu
			Passenger passenger;
			try {
				passenger = em.createQuery("SELECT p FROM Passenger p WHERE p.email = :email", Passenger.class)
						.setParameter("email", passengerEmail).getSingleResult();
			} catch (NoResultException ex) {
				tx.rollback();
				return false; 
			}

			//Balidazioak
			if (seatsRequested <= 0) {
				tx.rollback();
				return false;
			}

			if (seatsRequested > ride.getnPlaces()) {
				//Ez daude plazak
				tx.rollback();
				return false;
			}

			float totalPrice = ride.getPrice() * seatsRequested;
			if (passenger.getMoney() < totalPrice) {
				//Ez du nahiko diru
				tx.rollback();
				return false;
			}

			//Request sortu
			Request req = new Request(ride, passenger, seatsRequested, totalPrice);

			em.persist(req);
			em.merge(passenger);
			em.merge(ride);

			tx.commit();
			return true;
		} catch (RuntimeException e) {
			if (tx.isActive())
				tx.rollback();
			e.printStackTrace();
			return false;
		} finally {
			em.close();
		}
	}
	
    // Gidari baten eskaera guztiak lortzeko metodoa
	public List<Request> getDriverRequests(String driverEmail) {
	    EntityManager em = emf.createEntityManager();
	    try {
	        String query = "SELECT r FROM Request r " +
	                      "WHERE r.ride.driver.email = :driverEmail " +
	                      "ORDER BY r.requestDate DESC";
	        return em.createQuery(query, Request.class)
	                 .setParameter("driverEmail", driverEmail)
	                 .getResultList();
	    } finally {
	        em.close();
	    }
	}

    // Eskaera bat onartzeko metodoa
	public boolean acceptRequest(Integer requestId) {
	    EntityManager em = emf.createEntityManager();
	    EntityTransaction tx = em.getTransaction();
	    
	    try {
	        tx.begin();
	        
	        Request request = em.find(Request.class, requestId);
	        if (request == null) {
	            tx.rollback();
	            return false;
	        }
	     // Egoera aldatu
	        request.setEgoera(Request.EskaeraEgoera.ACCEPTED);
	        
	        // Ride-aren plaza kopurua eguneratu
	        Ride ride = request.getRide();
	        ride.setnPlaces(ride.getnPlaces() - request.getSeatsRequested());
	        
	        em.merge(request);
	        em.merge(ride);
	        
	        tx.commit();
	        return true;
	        
	    } catch (RuntimeException e) {
	        if (tx.isActive()) tx.rollback();
	        e.printStackTrace();
	        return false;
	    } finally {
	        em.close();
	    }
	}
	
    // Eskaera bat ez onartzeko metodoa
	public boolean rejectRequest(Integer requestId) {
	    EntityManager em = emf.createEntityManager();
	    EntityTransaction tx = em.getTransaction();
	    
	    try {
	        tx.begin();
	        
	        Request request = em.find(Request.class, requestId);
	        if (request == null) {
	            tx.rollback();
	            return false;
	        }
	        
	        // Egoera aldatu
	        request.setEgoera(Request.EskaeraEgoera.REJECTED);
	        
	        // Passenger-en dirua itzuli (aukerakoa)
	        Passenger passenger = request.getPassenger();
	        passenger.setMoney(passenger.getMoney() + request.getTotalPrice());
	        
	        em.merge(request);
	        em.merge(passenger);
	        
	        tx.commit();
	        return true;
	        
	    } catch (RuntimeException e) {
	        if (tx.isActive()) tx.rollback();
	        e.printStackTrace();
	        return false;
	    } finally {
	        em.close();
	    }
	    
	}

}