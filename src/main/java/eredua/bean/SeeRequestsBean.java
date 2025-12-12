package eredua.bean;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import businessLogic.BLFacade;
import domain.Driver;
import domain.Request;
import domain.User;

@Named("seeRequestsBean")
@ViewScoped
public class SeeRequestsBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<Request> driverRequests;
    private String driverEmail;
    private String userName;
    
    private BLFacade facadeBL;
    private String message;
    
    public SeeRequestsBean() {
        facadeBL = FacadeBean.getBusinessLogic();
    }
    
    @PostConstruct
    public void init() {
        System.out.println(">>> SeeRequestsBean.init() ejecutado");
        loadDriverFromSession();
        
        if (isDriverLoggedIn()) {
            System.out.println(">>> Driver logged in: " + driverEmail);
            loadDriverRequests();
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
    
    public void loadDriverRequests() {
        System.out.println(">>> Loading driver requests for: " + driverEmail);
        if (driverEmail != null && !driverEmail.isEmpty()) {
            try {
                driverRequests = facadeBL.getDriverRequests(driverEmail);
                System.out.println(">>> Found " + driverRequests.size() + " requests");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(">>> Error loading requests: " + e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not load requests: " + e.getMessage()));
            }
        } else {
            System.out.println(">>> No driver email to load requests");
        }
    }
    
    public boolean isDriverLoggedIn() {
        boolean loggedIn = (driverEmail != null && !driverEmail.isEmpty());
        System.out.println(">>> isDriverLoggedIn() returning: " + loggedIn + " (email: " + driverEmail + ")");
        return loggedIn;
    }
    
    public void acceptRequest(Integer requestId) {
        try {
            System.out.println(">>> Accepting request ID: " + requestId);
            boolean success = facadeBL.acceptRequest(requestId);
            if (success) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Request accepted successfully!"));
                loadDriverRequests(); 
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not accept request."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            e.printStackTrace();
        }
    }
    
    public void rejectRequest(Integer requestId) {
        try {
            System.out.println(">>> Rejecting request ID: " + requestId);
            boolean success = facadeBL.rejectRequest(requestId);
            if (success) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Request rejected successfully!"));
                loadDriverRequests(); 
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not reject request."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            e.printStackTrace();
        }
    }
    
    
    public List<Request> getDriverRequests() { 
        return driverRequests; 
    }
    
    public void setDriverRequests(List<Request> driverRequests) { 
        this.driverRequests = driverRequests; 
    }
    
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
    
    public String getMessage() { 
        return message; 
    }
    
    public void setMessage(String message) { 
        this.message = message; 
    }
}