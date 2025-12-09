package eredua.bean;

import domain.Ride;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import java.util.List;

@FacesConverter(value="rideConverter", managed=false)
public class RideConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;

        try {
            @SuppressWarnings("unchecked")
            List<Ride> rides = (List<Ride>) component.getAttributes().get("rides");
            if (rides != null) {
                Integer rideNumber = Integer.parseInt(value);
                for (Ride r : rides) {
                    if (r.getRideNumber() != null && r.getRideNumber().equals(rideNumber)) {
                        return r;
                    }
                }
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object ride) {
        if (ride == null) return "";
        return ((Ride) ride).getRideNumber().toString();
    }
}