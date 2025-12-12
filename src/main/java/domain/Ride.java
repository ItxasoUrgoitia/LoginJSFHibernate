package domain;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@SuppressWarnings("serial")
@Entity
public class Ride implements Serializable {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rideNumber;
    
    @Column(name = "origin")
    private String from;
    
    @Column(name = "destination")
    private String to;
    
    private int nPlaces;
    private Date date;
    private float price;
    private boolean cancelled = false;
    
    @ManyToOne
    @JoinColumn(name = "driver_email")
    private Driver driver;  
    
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Request> requests = new ArrayList<>();
    
    public Ride(){
        super();
    }
    
    public Ride(Integer rideNumber, String from, String to, Date date, int nPlaces, float price, Driver driver) {
        super();
        this.rideNumber = rideNumber;
        this.from = from;
        this.to = to;
        this.nPlaces = nPlaces;
        this.date=date;
        this.price=price;
        this.driver = driver;
    }

    public Ride(String from, String to,  Date date, int nPlaces, float price, Driver driver) {
        super();
        this.from = from;
        this.to = to;
        this.nPlaces = nPlaces;
        this.date=date;
        this.price=price;
        this.driver = driver;
    }
    
    public Integer getRideNumber() {
        return rideNumber;
    }
    
    public void setRideNumber(Integer rideNumber) {
        this.rideNumber = rideNumber;
    }

    public String getFrom() {
        return from;
    }
    
    public void setFrom(String origin) {
        this.from = origin;
    }

    public String getTo() {
        return to;
    }
    
    public void setTo(String destination) {
        this.to = destination;
    }

    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }

    public int getnPlaces() {
        return nPlaces;
    }

    public void setnPlaces(int nPlaces) {
        this.nPlaces = nPlaces;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public boolean isCancelled() {
        return cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    @Override
    public String toString(){
        return rideNumber+";"+from+";"+to+";"+date;  
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ride ride = (Ride) o;
        return rideNumber != null ? rideNumber.equals(ride.rideNumber) : ride.rideNumber == null;
    }

    @Override
    public int hashCode() {
        return rideNumber != null ? rideNumber.hashCode() : 0;
    }
}