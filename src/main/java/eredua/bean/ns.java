package eredua.bean;

import java.util.List;

import businessLogic.BLFacade;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named("nsBean")
@RequestScoped
public class ns {

	private BLFacade facadeBL;
	private List<String> cities;
	private String selectedCity;

	public ns() {
		facadeBL = FacadeBean.getBusinessLogic(); 
		cities = facadeBL.getDepartCities(); 
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
}
