package eredua.bean;

import domain.Car;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.util.List;




@FacesConverter(value="carConverter", managed=false)
public class CarConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;

        
        @SuppressWarnings("unchecked")
        List<Car> cars = (List<Car>) component.getAttributes().get("cars");
        if (cars != null) {
            for (Car c : cars) {
                if (c.getLicensePlate().equals(value)) {
                    return c;
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object car) {
        if (car == null) return "";
        return ((Car) car).getLicensePlate();
    }
}

