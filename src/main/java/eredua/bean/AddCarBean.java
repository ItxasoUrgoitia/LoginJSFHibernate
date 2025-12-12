package eredua.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import businessLogic.BLFacade;
import domain.Driver;

@Named("addCarBean")
@RequestScoped
public class AddCarBean {
    
    private String licensePlate;
    private Integer places;
    private String model;
    private String color;
    private String driverEmail;
    private String userName;
    
    

	private BLFacade facadeBL;
    private String message;
    private boolean success = false;
    
    public AddCarBean() {
        facadeBL = FacadeBean.getBusinessLogic();
    }
    
   
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
    
    @PostConstruct
    public void init() {
        System.out.println(">>> SeeRequestsBean.init() ejecutado");
        loadDriverFromSession();
        
        if (isDriverLoggedIn()) {
            System.out.println(">>> Driver logged in: " + driverEmail);
            
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
    
    public void addCar() {
        success = false;
        message = null;
        
        
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
        
        try {
            boolean result = facadeBL.addCar(licensePlate, places, model, color, driverEmail);
            
            if (result) {
                success = true;
                message = "✓ Car added successfully!";
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Car added successfully!"));
                
                
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