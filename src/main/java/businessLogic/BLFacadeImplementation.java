package businessLogic;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import dataAccess.HibernateDataAccess;
import domain.Ride;
import domain.User;
import domain.Driver;
import domain.Passenger;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.DriverDoesNotExistException;
import exceptions.RideAlreadyExistException;

/**
 * It implements the business logic as a web service.
 */
public class BLFacadeImplementation implements BLFacade {
	HibernateDataAccess dbManager;

	public BLFacadeImplementation() {
		System.out.println("Creating BLFacadeImplementation instance");

		dbManager = new HibernateDataAccess();
		
		// dbManager.close();

	}

	public BLFacadeImplementation(HibernateDataAccess da) {

		System.out.println("Creating BLFacadeImplementation instance with DataAccess parameter");
		// ConfigXML c=ConfigXML.getInstance();

		dbManager = da;
		dbManager.initializeDB();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDepartCities() {

		return dbManager.getDepartCities();

	}

	/**
	 * {@inheritDoc}
	 */
	
	public List<String> getDestinationCities(String from) {
		return dbManager.getArrivalCities(from);

	}

	/**
	 * {@inheritDoc}
	 */
	
	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
			throws RideMustBeLaterThanTodayException, RideAlreadyExistException, DriverDoesNotExistException {

		
		return  dbManager.createRide(from, to, date, nPlaces, price, driverEmail);
		
	};

	/**
	 * {@inheritDoc}
	 */
	
	public List<Ride> getRides(String from, String to, Date date) {
		return dbManager.getRides(from, to, date);	
		
	}

	/**
	 * {@inheritDoc}
	 */
	
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
		return dbManager.getThisMonthDatesWithRides(from, to, date);
		
	}

	

	/**
	 * {@inheritDoc}
	 */
	
	public void initializeBD() {
		dbManager.initializeDB();	
	}
	
	public User isRegistered(String email, String pasahitza) {
		return dbManager.isRegistered(email, pasahitza);
	}
	
	public void createDriver(Driver driver) {
		dbManager.createDriver(driver);
	}
	public void createPassenger(Passenger pasenger) {
		dbManager.createPassenger(pasenger);
	}

}
