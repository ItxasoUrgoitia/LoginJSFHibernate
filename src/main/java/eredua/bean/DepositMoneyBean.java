package eredua.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import domain.Passenger;

@Named("DepositBean")
@RequestScoped
public class DepositMoneyBean {

    private String email; 
    private Float amount; 

    private BLFacade facadeBL;

    public DepositMoneyBean() {
        facadeBL = FacadeBean.getBusinessLogic();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Float getAmount() { return amount; }
    public void setAmount(Float amount) { this.amount = amount; }

    // Acción del botón
    public void depositMoney() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (email == null || email.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Enter your email"));
            return;
        }

        if (amount == null || amount <= 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Enter a positive amount"));
            return;
        }

        try {
            boolean ondo = facadeBL.depositMoney(email, amount);
            if (!ondo) {
            	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred"));
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred"));
            e.printStackTrace();
        }
    }
}
