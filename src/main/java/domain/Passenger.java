package domain;


import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "email")
public class Passenger extends User implements Serializable{
	private float money= 0f;
	
	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public Passenger() {this.money = 0f;}
	
	public Passenger(String email, String name, String pasahitza) {
		super(email, name, pasahitza);
		this.money = 0f;
	}

}
