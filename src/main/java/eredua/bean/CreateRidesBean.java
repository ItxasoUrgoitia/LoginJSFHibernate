package eredua.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.primefaces.event.SelectEvent;

import businessLogic.BLFacade;

import domain.Ride;
import exceptions.DriverDoesNotExistException;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;


@Named("CreateRidesBean")
@RequestScoped
public class CreateRidesBean {

    private String departCity;
    private String arrivalCity;
    private String email;
    private Integer seats;
    private Float price;
    private Date data;
    private BLFacade facadeBL;
     

    private List<String> departCities;
    private List<String> arrivalCities;

    private boolean rideCreated = false; // controla el mensaje de éxito
    private String message; // mensaje de error o éxito

    public void showAlert() {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Alerta", "Operación completada"));
    }
    // Getters y setters
    public String getDepartCity() { return departCity; }
    public void setDepartCity(String departCity) { this.departCity = departCity; }
    
	public Date getData() { return data; }
	public void setData(Date data) { this.data = data; }
    	
    public String getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }

    public Float getPrice() { return price; }
    public void setPrice(Float price) { this.price = price; }

    public List<String> getDepartCities() { return departCities; }
    public void setDepartCities(List<String> departCities) { this.departCities = departCities; }

    public List<String> getArrivalCities() { return arrivalCities; }
    public void setArrivalCities(List<String> arrivalCities) { this.arrivalCities = arrivalCities; }

    public boolean isRideCreated() { return rideCreated; }
    public void setRideCreated(boolean rideCreated) { this.rideCreated = rideCreated; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

   
    public void onDateSelect(SelectEvent event) {
  		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Data aukeratua: " + event.getObject()));
  	}

    // Acción del botón "Create Ride"
    public void createRide() {
    	 System.out.println("=== INICIANDO createRide ===");
    	    rideCreated = false;

    	    // Validación de los campos
    	    System.out.println("Validando campos...");
    	    System.out.println("departCity: " + departCity);
    	    System.out.println("arrivalCity: " + arrivalCity);
    	    System.out.println("email: " + email);
    	    System.out.println("seats: " + seats);
    	    System.out.println("price: " + price);
    	    System.out.println("data: " + data); // resetear mensaje de éxito

        // Validación de los campos y asignación de mensaje en pantalla
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
        if (seats == null || seats < 1) {
            message = "Number of seats must be at least 1.";
            return;
        }
        if (price == null || price < 0) {
            message = "Price must be non-negative.";
            return;
        } if (email == null|| email.isEmpty()) {
            message = "Enter email";
            return;
        }

        System.out.println("Todas las validaciones pasadas");

        // Si todo está bien
        System.out.println("Obteniendo facadeBL...");
        facadeBL=FacadeBean.getBusinessLogic();
        System.out.print("Facadesortuda");
        try {
        	System.out.print("kaixo");
        	facadeBL.createRide(departCity, arrivalCity, data, seats, price, email);
        	System.out.print("Egin da");
        } catch (RideMustBeLaterThanTodayException e1) {
        	// TODO Auto-generated catch block
        	message = "Ride Must Be Later Than Today";
        } catch (RideAlreadyExistException e1) {
        	// TODO Auto-generated catch block
        	message = "Ride Already Exist";
        } catch (DriverDoesNotExistException e1) {
        	message = "Driver Does Not Exist";
        }
        
        
        rideCreated = true;
        message = "✓ Ride created successfully!";
        
    }
    
    public void ns() {
    	facadeBL=FacadeBean.getBusinessLogic();
    	this.departCities = facadeBL.getDepartCities();
    	System.out.print(departCities);
    	FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(
            FacesMessage.SEVERITY_INFO,
            "Ciudades cargadas", 
            "DepartCities: " + departCities.toString()
        ));
    }
    
  
}
//	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail) throws  RideAlreadyExistException, RideMustBeLaterThanTodayException {
