package eredua.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import domain.Ride;

@Named
@RequestScoped
public class QueryRidesBean {


    private BLFacade facadeBL;

    private String selectedDepartCity;
    private String selectedArrivalCity;
    private Date selectedDate;

    private List<String> departCities;
    private List<String> arrivalCities;

    private List<Ride> availableRides;

    public QueryRidesBean() {
    	System.out.print("QueryRidesBean kontructor");
        facadeBL = FacadeBean.getBusinessLogic();
        System.out.print("QueryRidesBean kontructor 2");
        dCities();
        System.out.print("QueryRidesBean kontructor3");
    }
    public List<String> getDepartCities() {
        return departCities;
    }
    public void dCities() {
    	System.out.print("QueryRidesBean dCities");
    	this.departCities = facadeBL.getDepartCities();
    	System.out.print(departCities);
    }
    
    /*public void aCities() {
    	this.departCities = facadeBL.getDepartCities();
    	System.out.print(arrivalCities);
    }*/

    // ----- GETTERS DE DATOS -----

    /*public List<String> getDepartCities() {
        if (departCities == null) {
            departCities = facadeBL.getDepartCities();
        }
        return departCities;
    }

    public List<String> getArrivalCities() {
        if (selectedDepartCity != null) {
            arrivalCities = facadeBL.getDestinationCities(selectedDepartCity);
        }
        return arrivalCities;
    }*/

    // ----- EVENTOS -----

   /* public void onDepartCityChange() {
        if (selectedDepartCity != null) {
            arrivalCities = facadeBL.getDestinationCities(selectedDepartCity);
        }
    }*/

    // ----- ACCIÓN -----

  /*  public String findRides() {
        availableRides = facadeBL.getRides(selectedDepartCity, selectedArrivalCity, selectedDate);
        return null;
    }

    // ----- GETTERS & SETTERS -----

    public String getSelectedDepartCity() { return selectedDepartCity; }
    public void setSelectedDepartCity(String selectedDepartCity) { this.selectedDepartCity = selectedDepartCity; }

    public String getSelectedArrivalCity() { return selectedArrivalCity; }
    public void setSelectedArrivalCity(String selectedArrivalCity) { this.selectedArrivalCity = selectedArrivalCity; }

    public Date getSelectedDate() { return selectedDate; }
    public void setSelectedDate(Date selectedDate) { this.selectedDate = selectedDate; }

    public List<Ride> getAvailableRides() { return availableRides; }
    public void setAvailableRides(List<Ride> availableRides) { this.availableRides = availableRides; }
    */
}
