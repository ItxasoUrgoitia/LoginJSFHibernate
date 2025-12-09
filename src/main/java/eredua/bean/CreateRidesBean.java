package eredua.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
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
    private String email;
    private Integer seats;
    private Float price;
    private Date data;
    private BLFacade facadeBL;
    private List<Car> cars;
    private Car selectedCar;

    private List<String> departCities;
    private List<String> arrivalCities;

    private boolean rideCreated = false; 
    private String message; 

    // Getters y Setters
    public String getDepartCity() { return departCity; }
    public void setDepartCity(String departCity) { this.departCity = departCity; }

    public String getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    // Evento de selección de fecha
    public void onDateSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Selected date: " + event.getObject()));
    }

    public void loadCars() {
        facadeBL = FacadeBean.getBusinessLogic();
        try {
            Driver driver = facadeBL.getDriver(email);
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


  

    // Acción del botón "Create Ride"
    public void createRide() {
        rideCreated = false;

        // Validaciones
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
        if (email == null || email.isEmpty()) {
            message = "Enter email";
            return;
        }

        // Crear ride
        facadeBL = FacadeBean.getBusinessLogic();
        try {
            facadeBL.createRide(departCity, arrivalCity, data, selectedCar.getPlaces(), price, email);
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

    // Método auxiliar para cargar ciudades
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
