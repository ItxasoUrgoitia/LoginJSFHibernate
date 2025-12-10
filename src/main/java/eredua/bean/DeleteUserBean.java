package eredua.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import businessLogic.BLFacade;
import domain.Driver;
import domain.Passenger;

import java.io.IOException;
import java.io.Serializable;  

@Named("deleteUserBean")
@ViewScoped 
public class DeleteUserBean implements Serializable { 

    private static final long serialVersionUID = 1L;
    
    private String userEmail;
    private String confirmationEmail;
    private BLFacade facadeBL;
    private String message;
    private boolean success = false;
    private boolean showConfirmation = false;
    private String userName;
    private String userType;  // Nuevo: para saber si es Driver o Passenger
    
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public DeleteUserBean() {
        facadeBL = FacadeBean.getBusinessLogic();
    }
    
    @PostConstruct
    public void init() {
        System.out.println(">>> deleteUserBean.init() ejecutado");
        loadUserFromSession();
        
        if (isUserLoggedIn()) {
            System.out.println(">>> User logged in: " + userEmail + " (Type: " + userType + ")");
        } else {
            System.out.println(">>> NOT logged in");
        }
    }
    
    private void loadUserFromSession() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            
            System.out.println(">>> Checking session...");
            
            if (session != null) {
                String sessionUserEmail = (String) session.getAttribute("userEmail");
                String sessionUserType = (String) session.getAttribute("userType");
                
                System.out.println(">>> Session userEmail: " + sessionUserEmail);
                System.out.println(">>> Session userType: " + sessionUserType);
                
                // DEPURACIÓN: Mostrar todas las variables de sesión
                java.util.Enumeration<String> sessionAttr = session.getAttributeNames();
                System.out.println(">>> All session attributes:");
                while (sessionAttr.hasMoreElements()) {
                    String attr = sessionAttr.nextElement();
                    System.out.println("  - " + attr + ": " + session.getAttribute(attr));
                }
                
                if (sessionUserEmail != null && (sessionUserType != null)) {
                    this.userEmail = sessionUserEmail;
                    this.userType = sessionUserType;
                    
                    // Obtener nombre del usuario para mostrar
                    try {
                        if ("Driver".equals(userType)) {
                            Driver driver = facadeBL.getDriver(userEmail);
                            if (driver != null) {
                                this.userName = driver.getName();
                            }
                        } else if ("Passenger".equals(userType)) {
                            Passenger passenger = facadeBL.getPassenger(userEmail);
                            if (passenger != null) {
                                this.userName = passenger.getName();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(">>> Could not get user details: " + e.getMessage());
                    }
                } else {
                    System.out.println(">>> No valid user in session");
                }
            } else {
                System.out.println(">>> No active session found");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(">>> Error in loadUserFromSession: " + e.getMessage());
        }
    }
    
    public boolean isUserLoggedIn() {
        boolean loggedIn = (userEmail != null && !userEmail.isEmpty() && userType != null);
        System.out.println(">>> isUserLoggedIn() returning: " + loggedIn + 
                         " (email: " + userEmail + ", type: " + userType + ")");
        return loggedIn;
    }
    
    public void checkUser() {
        success = false;
        message = null;
        showConfirmation = false;
        
        if (userEmail == null || userEmail.trim().isEmpty()) {
            message = "Email is required";
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Email is required"));
            return;
        }
        
        if (!facadeBL.userExists(userEmail)) {
            message = "User with email " + userEmail + " does not exist";
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "User does not exist"));
            return;
        }
        
        message = "User found. Enter the email again to confirm deletion.";
        showConfirmation = true;
    }
    
    public void deleteUser() {
        success = false;
        message = null;
        
        if (userEmail == null || userEmail.trim().isEmpty()) {
            message = "Email is required";
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Email is required"));
            return;
        }
        
        if (!userEmail.equals(confirmationEmail)) {
            message = "Emails do not match";
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Emails do not match"));
            return;
        }
        
        try {
            // Guardar el email antes de eliminarlo para usar en el mensaje
            String emailToDelete = userEmail;
            boolean result = facadeBL.deleteUser(emailToDelete);
            
            if (result) {
                success = true;
                message = "User " + emailToDelete + " deleted successfully!";
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "User deleted successfully!"));
                
                // Invalidar sesión si el usuario eliminado es el que está logeado
                invalidateSessionIfDeletedUserIsLogged(emailToDelete);
                
                // Limpiar campos pero mantener el mensaje de éxito
                userEmail = null;
                confirmationEmail = null;
                showConfirmation = false;
                userType = null;
                userName = null;
                
            } else {
                message = "Error deleting user. There might be active dependencies.";
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                    "Error deleting user. There might be active dependencies."));
            }
            
        } catch (Exception e) {
            message = "Error deleting user: " + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error deleting user: " + e.getMessage()));
        }
    }
    
    private void invalidateSessionIfDeletedUserIsLogged(String deletedEmail) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            
            if (session != null) {
                String loggedUserEmail = (String) session.getAttribute("userEmail");
                System.out.println(">>> Invalidating session for deleted user: " + deletedEmail);
                System.out.println(">>> Currently logged in user: " + loggedUserEmail);
                
                if (deletedEmail != null && deletedEmail.equals(loggedUserEmail)) {
                    System.out.println(">>> Session invalidated for user: " + deletedEmail);
                    session.invalidate();
                    try {
                        context.getExternalContext().redirect("Register.xhtml");
                        context.responseComplete(); // Importante: completar la respuesta
                    } catch (IOException e) {
                        System.out.println(">>> Error redirecting to login: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(">>> Error invalidating session: " + e.getMessage());
        }
    }
    
    public String cancel() {
        userEmail = null;
        confirmationEmail = null;
        message = null;
        success = false;
        showConfirmation = false;
        userType = null;
        userName = null;
        return "Hasierahasiera.xhtml?faces-redirect=true";
    }
    
    // Getters and Setters
    
    public String getConfirmationEmail() { return confirmationEmail; }
    public void setConfirmationEmail(String confirmationEmail) { this.confirmationEmail = confirmationEmail; }
    public BLFacade getFacadeBL() { return facadeBL; }
    public void setFacadeBL(BLFacade facadeBL) { this.facadeBL = facadeBL; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public boolean isShowConfirmation() { return showConfirmation; }
    public void setShowConfirmation(boolean showConfirmation) { this.showConfirmation = showConfirmation; }
}