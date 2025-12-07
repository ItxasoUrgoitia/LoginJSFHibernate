package domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Car implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id 
    private String licensePlate;
    private int places;
    private String model;
    private String color;
    
    @ManyToOne
    @JoinColumn(name = "driver_email", referencedColumnName = "email")
    private Driver driver;
    
    public Car() {
        super();
    }
    
    public Car(String licensePlate, int places, String model, String color, Driver driver) {
        this.licensePlate = licensePlate;
        this.places = places;
        this.model = model;
        this.color = color;
        this.driver = driver;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public int getPlaces() {
        return places;
    }
    
    public void setPlaces(int places) {
        this.places = places;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public Driver getDriver() {
        return driver;
    }
    
    public void setDriver(Driver driver) {
        this.driver = driver;
    }
    
    @Override
    public String toString() {
        return "[licensePlate=" + licensePlate + ", places=" + places + ", model=" + model + "]";
    }
}