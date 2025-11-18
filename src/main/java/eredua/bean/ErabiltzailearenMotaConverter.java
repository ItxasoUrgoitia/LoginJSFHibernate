package eredua.bean;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

@FacesConverter("ErabiltzailearenMotaConverter")
public class ErabiltzailearenMotaConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String newValue) throws ConverterException {
		return LoginBean.getObject(newValue);
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) throws ConverterException {
		if (value == null)
			return "";
		return value.toString();
	}
}
