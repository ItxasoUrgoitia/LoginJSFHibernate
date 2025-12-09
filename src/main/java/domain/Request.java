package domain;

import java.io.Serializable;
import java.util.Date;


import jakarta.persistence.*;

@Entity
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reqNumber;
    
    private int seatsRequested;
    private float totalPrice;
    public enum EskaeraEgoera{
		ACCEPTED,
		REJECTED,
		PENDING
	}
    private EskaeraEgoera egoera;
    private Date requestDate;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "passenger_email")
    private Passenger passenger;

    public Request() {
        this.requestDate = new Date();
        this.egoera=EskaeraEgoera.PENDING;
    }

    public Request(Ride ride, Passenger passenger, int seatsRequested, float totalPrice) {
        this();
        this.ride = ride;
        this.passenger = passenger;
        this.seatsRequested = seatsRequested;
        this.totalPrice = totalPrice;
        this.egoera=EskaeraEgoera.PENDING;
    }

    // getters / setters

    

    public int getSeatsRequested() { return seatsRequested; }
    
    public Integer getReqNumber() {
		return reqNumber;
	}

	public void setReqNumber(Integer reqNumber) {
		this.reqNumber = reqNumber;
	}

	public void setSeatsRequested(int seatsRequested) { this.seatsRequested = seatsRequested; }

    public float getTotalPrice() { return totalPrice; }
    public void setTotalPrice(float totalPrice) { this.totalPrice = totalPrice; }

    public EskaeraEgoera getEgoera() {
		return egoera;
	}

	public void setEgoera(EskaeraEgoera egoera) {
		this.egoera = egoera;
	}

	public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }

    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }
}
