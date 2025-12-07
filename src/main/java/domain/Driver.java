package domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;


@Entity
@PrimaryKeyJoinColumn(name = "email")
public class Driver extends User implements Serializable {
    
    @OneToMany(mappedBy = "driver", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides=new Vector<Ride>();

	@OneToMany(mappedBy = "driver", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Car> cars = new Vector<Car>();
	
	public Driver() {}

	public Driver(String email, String name, String pasahitza) {
		super(email, name, pasahitza);
	}
	
	public Ride addRide(String from, String to, Date date, int nPlaces, float price)  {
        Ride ride=new Ride(from,to,date,nPlaces,price, this);
        rides.add(ride);
        return ride;
	}

	public boolean doesRideExists(String from, String to, Date date)  {	
		for (Ride r:rides)
			if ( (java.util.Objects.equals(r.getFrom(),from)) && (java.util.Objects.equals(r.getTo(),to)) && (java.util.Objects.equals(r.getDate(),date)) )
			 return true;
		
		return false;
	}
		

	public Ride removeRide(String from, String to, Date date) {
		boolean found=false;
		int index=0;
		Ride r=null;
		while (!found && index<=rides.size()) {
			r=rides.get(++index);
			if ( (java.util.Objects.equals(r.getFrom(),from)) && (java.util.Objects.equals(r.getTo(),to)) && (java.util.Objects.equals(r.getDate(),date)) )
			found=true;
		}
			
		if (found) {
			rides.remove(index);
			return r;
		} else return null;
	}

	public List<Ride> getRides() {
		return rides;
	}

	public void setRides(List<Ride> rides) {
		this.rides = rides;
	}
	
	public Car addCar(String licensePlate, int places, String model, String color) {
        Car car = new Car(licensePlate, places, model, color, this);
        cars.add(car);
        return car;
    }
    
    public boolean doesCarExist(String licensePlate) {
        for (Car c : cars) {
            if (c.getLicensePlate().equals(licensePlate)) {
                return true;
            }
        }
        return false;
    }
    
 
    
    public List<Car> getCars() {
        return cars;
    }
    
    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
	
}
