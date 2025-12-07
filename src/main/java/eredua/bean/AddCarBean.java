package eredua.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import businessLogic.BLFacade;

@Named("addCarBean")
@RequestScoped
public class AddCarBean {
    
    private String licensePlate;
    private Integer places;
    private String model;
    private String color;
    private String driverEmail;
    
    private BLFacade facadeBL;
    private String message;
    private boolean success = false;
    
    public AddCarBean() {
        facadeBL = FacadeBean.getBusinessLogic();
    }
    
    // Getters y Setters
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public Integer getPlaces() {
        return places;
    }
    
    public void setPlaces(Integer places) {
        this.places = places;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getDriverEmail() {
        return driverEmail;
    }
    
    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public void addCar() {
        success = false;
        message = null;
        
        // Validaciones
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            message = "License plate is required";
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "License plate is required"));
            return;
        }
        
        if (places == null || places < 1) {
            message = "Number of places must be at least 1";
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Number of places must be at least 1"));
            return;
        }
        
        if (model == null || model.trim().isEmpty()) {
            message = "Model is required";
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Model is required"));
            return;
        }
        
        if (driverEmail == null || driverEmail.trim().isEmpty()) {
            message = "Driver email is required";
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Driver email is required"));
            return;
        }
        
        try {
            boolean result = facadeBL.addCar(licensePlate, places, model, color, driverEmail);
            
            if (result) {
                success = true;
                message = "✓ Car added successfully!";
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Car added successfully!"));
                
                // Limpiar campos después de éxito
                licensePlate = null;
                places = null;
                model = null;
                color = null;
                driverEmail = null;
            } else {
                message = "Error adding car. Car might already exist or driver not found.";
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "Error adding car. Car might already exist or driver not found."));
            }
        } catch (Exception e) {
            message = "Error: " + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error adding car: " + e.getMessage()));
        }
    }
}