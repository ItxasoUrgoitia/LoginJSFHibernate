package domain;


import java.io.Serializable;

import jakarta.persistence.Entity;

@Entity
public class Passenger extends User implements Serializable{

	public Passenger() {}
	
	public Passenger(String email, String name, String pasahitza) {
		super(email, name, pasahitza);
	}

}
