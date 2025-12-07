package eredua.bean;

import jakarta.faces.view.ViewScoped;  
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import businessLogic.BLFacade;
import java.io.Serializable;  

@Named("deleteUserBean")
@ViewScoped 
public class DeleteUserBean implements Serializable { 

    private static final long serialVersionUID = 1L;
    
    private String email;
    private String confirmationEmail;
    private BLFacade facadeBL;
    private String message;
    private boolean success = false;
    private boolean showConfirmation = false;
    
    public DeleteUserBean() {
        facadeBL = FacadeBean.getBusinessLogic();
    }
    
    public void checkUser() {
        success = false;
        message = null;
        showConfirmation = false;
        
        if (email == null || email.trim().isEmpty()) {
            message = "Email is required";
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Email is required"));
            return;
        }
        
        if (!facadeBL.userExists(email)) {
            message = "User with email " + email + " does not exist";
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
        
        if (email == null || email.trim().isEmpty()) {
            message = "Email is required";
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Email is required"));
            return;
        }
        
        if (!email.equals(confirmationEmail)) {
            message = "Emails do not match";
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Emails do not match"));
            return;
        }
        
        try {
            boolean result = facadeBL.deleteUser(email);
            
            if (result) {
                success = true;
                message = "User " + email + " deleted successfully!";
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "User deleted successfully!"));
                
                email = null;
                confirmationEmail = null;
                showConfirmation = false;
                
                invalidateSessionIfDeletedUserIsLogged(email);
                
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
                if (deletedEmail != null && deletedEmail.equals(loggedUserEmail)) {
                    session.invalidate();
                }
            }
        } catch (Exception e) {
            // Silent fail - logging out is not critical
        }
    }
    
    public void cancel() {
        email = null;
        confirmationEmail = null;
        message = null;
        success = false;
        showConfirmation = false;
    }
    
    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
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