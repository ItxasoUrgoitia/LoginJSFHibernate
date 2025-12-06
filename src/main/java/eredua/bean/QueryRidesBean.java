package eredua.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.omnifaces.cdi.ViewScoped;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import businessLogic.BLFacade;
import domain.Ride;

@Named("qrBean")
@ViewScoped
public class QueryRidesBean implements Serializable{
	private BLFacade facadeBL;
	private List<String> cities;
	private List<String> cities2;
	private String selectedCity;
	private String selectedCity2;
	private Date selectedDate;
	private List<Ride> availableRides;

	public Date getSelectedDate() { return selectedDate; }
	public void setSelectedDate(Date selectedDate) { this.selectedDate = selectedDate; }

	public List<Ride> getAvailableRides() { return availableRides; }
	public String findRides() {
	    System.out.println(">>> findRides() ejecutado");
	    System.out.println("Parametro from = " + selectedCity);
	    System.out.println("Parametro to   = " + selectedCity2);
	    System.out.println("Parametro date = " + selectedDate);

	    availableRides = facadeBL.getRides(selectedCity, selectedCity2, selectedDate);

	    System.out.println("Rides encontrados = " + availableRides.size());
	    return availableRides.toString();
	}
	




	public QueryRidesBean() {
		facadeBL = FacadeBean.getBusinessLogic(); 
		cities = facadeBL.getDepartCities();
		System.out.print(facadeBL.getDepartCities());
	}
	
	public void updateArrivalCities() {
        System.out.println("Selected depart city: " + selectedCity);
        cities2 = facadeBL.getDestinationCities(selectedCity);
    }
	
	public List<String> getCities() {
		return cities;
	}

	public String getSelectedCity() {
		return selectedCity;
	}

	public void setSelectedCity(String selectedCity) {
		this.selectedCity = selectedCity;
	}
	
	public List<String> getCities2() {
		return cities2;
	}

	public String getSelectedCity2() {
		return selectedCity2;
	}

	public void setSelectedCity2(String selectedCity2) {
		this.selectedCity2 = selectedCity2;
	}
	
	
	
}
