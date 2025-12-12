package eredua.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import org.primefaces.event.SelectEvent;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import businessLogic.BLFacade;
import domain.Car;
import domain.Driver;
import exceptions.DriverDoesNotExistException;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;

@Named("CreateRidesBean")
@ViewScoped
public class CreateRidesBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private String departCity;
    private String arrivalCity;
    
    private Integer seats;
    private Float price;
    private Date data;
    private BLFacade facadeBL;
    private List<Car> cars;
    private Car selectedCar;
    private String driverEmail;
    private String userName;

    private List<String> departCities;
    private List<String> arrivalCities;

    private boolean rideCreated = false; 
    private String message; 

    
    public String getDepartCity() { return departCity; }
    public void setDepartCity(String departCity) { this.departCity = departCity; }

    public String getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }

    

    public String getDriverEmail() {
		return driverEmail;
	}
	public void setDriverEmail(String driverEmail) {
		this.driverEmail = driverEmail;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }

    public Float getPrice() { return price; }
    public void setPrice(Float price) { this.price = price; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public List<String> getDepartCities() { return departCities; }
    public void setDepartCities(List<String> departCities) { this.departCities = departCities; }

    public List<String> getArrivalCities() { return arrivalCities; }
    public void setArrivalCities(List<String> arrivalCities) { this.arrivalCities = arrivalCities; }

    public boolean isRideCreated() { return rideCreated; }
    public void setRideCreated(boolean rideCreated) { this.rideCreated = rideCreated; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Car> getCars() { return cars; }
    public void setCars(List<Car> cars) { this.cars = cars; }

  
    public Car getSelectedCar() { return selectedCar; }
    public void setSelectedCar(Car selectedCar) { this.selectedCar = selectedCar; }

    
    public void onDateSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Selected date: " + event.getObject()));
    }
    @PostConstruct
    public void init() {
        System.out.println(">>> SeeRequestsBean.init() ejecutado");
        loadDriverFromSession();
        
        if (isDriverLoggedIn()) {
            System.out.println(">>> Driver logged in: " + driverEmail);
            loadCars();
        } else {
            System.out.println(">>> NOT a driver or not logged in");
        }
    }
    
    private void loadDriverFromSession() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            
            System.out.println(">>> Checking session...");
            
            if (session != null) {
                String userEmail = (String) session.getAttribute("userEmail");
                String userType = (String) session.getAttribute("userType");
                
                System.out.println(">>> Session userEmail: " + userEmail);
                System.out.println(">>> Session userType: " + userType);
                
                
                java.util.Enumeration<String> sessionAttr = session.getAttributeNames();
                System.out.println(">>> All session attributes:");
                while (sessionAttr.hasMoreElements()) {
                    String attr = sessionAttr.nextElement();
                    System.out.println("  - " + attr + ": " + session.getAttribute(attr));
                }
                
                if (userEmail != null && "Driver".equals(userType)) {
                    this.driverEmail = userEmail;
                    
                    
                    try {
                        Driver driver = facadeBL.getDriver(userEmail);
                        if (driver != null) {
                            this.userName = driver.getName();
                        }
                    } catch (Exception e) {
                        System.out.println(">>> Could not get driver details: " + e.getMessage());
                    }
                } else {
                    System.out.println(">>> NOT a Driver in session");
                }
            } else {
                System.out.println(">>> No active session found");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(">>> Error in loadDriverFromSession: " + e.getMessage());
        }
    }
    
    public boolean isDriverLoggedIn() {
        boolean loggedIn = (driverEmail != null && !driverEmail.isEmpty());
        System.out.println(">>> isDriverLoggedIn() returning: " + loggedIn + " (email: " + driverEmail + ")");
        return loggedIn;
    }

    public void loadCars() {
        facadeBL = FacadeBean.getBusinessLogic();
        try {
            Driver driver = facadeBL.getDriver(driverEmail);
            if (driver != null) {
                cars = driver.getCars();
            } else {
                cars = null;
            }
            //selectedCar = null;
            
        } catch (Exception e) {
            cars = null;
            //selectedCar = null;
            
        }
    }


  

    
    public void createRide() {
        rideCreated = false;

        
        if (departCity == null || departCity.isEmpty()) {
            message = "Please select a departure city.";
            return;
        }
        if (arrivalCity == null || arrivalCity.isEmpty()) {
            message = "Please select an arrival city.";
            return;
        }
        if (departCity.equals(arrivalCity)) {
            message = "Departure and arrival cities cannot be the same.";
            return;
        }
        if (selectedCar == null) {
            message = "No car selected.";
            return;
        }
        if (price == null || price < 0) {
            message = "Price must be non-negative.";
            return;
        }
        

        
        facadeBL = FacadeBean.getBusinessLogic();
        try {
            facadeBL.createRide(departCity, arrivalCity, data, selectedCar.getPlaces(), price, driverEmail);
        } catch (RideMustBeLaterThanTodayException e) {
            message = "Ride Must Be Later Than Today";
            return;
        } catch (RideAlreadyExistException e) {
            message = "Ride Already Exist";
            return;
        } catch (DriverDoesNotExistException e) {
            message = "Driver Does Not Exist";
            return;
        }

        rideCreated = true;
        message = "✓ Ride created successfully!";
    }

    
    public void ns() {
        facadeBL = FacadeBean.getBusinessLogic();
        this.departCities = facadeBL.getDepartCities();
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Ciudades cargadas",
                "DepartCities: " + departCities.toString()
        ));
    }
}
