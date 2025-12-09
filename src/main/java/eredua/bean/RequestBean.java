package eredua.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import domain.Ride;

import java.io.Serializable;
import java.util.List;

@Named("RequestBean")
@ViewScoped
public class RequestBean implements Serializable {
	
    private static final long serialVersionUID = 1L;
    private String email;
    private Ride selectedRide;  // Cambiado: ahora solo usamos Ride
    private int seats;
    private List<Ride> rides;

    private BLFacade facadeBL;

    public RequestBean() {
        facadeBL = FacadeBean.getBusinessLogic();
    }

    public List<Ride> getRides() { return rides; }

    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email;
    }

    public Ride getSelectedRide() { return selectedRide; }
    public void setSelectedRide(Ride selectedRide) { 
        this.selectedRide = selectedRide;
    }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public void requestRide() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (email == null || email.isEmpty()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error1", "Enter your email"));
            return;
        }
        if (selectedRide == null) {  // Cambiado: ahora comprobamos selectedRide
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error2", "Select a ride"));
            return;
        }
        if (seats <= 0) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error3", "Enter number of seats"));
            return;
        }

        boolean ok = facadeBL.createRequest(email, selectedRide.getRideNumber(), seats);
        if (ok) {
            loadRides();
            // Limpiamos la selección después de la reserva exitosa
            this.selectedRide = null;
            this.seats = 0;
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Request created and accepted"));
        } else {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error4", "Request failed (insufficient funds or seats or invalid data)"));
        }
    }
    
    @PostConstruct
    public void init() {
        loadRides();
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public void loadRides() {
        this.rides = facadeBL.getAllRides();
    }
    
    // Método para limpiar la selección (opcional)
    public void clearSelection() {
        this.selectedRide = null;
        this.seats = 0;
    }
}