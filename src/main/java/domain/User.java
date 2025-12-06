package domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;



@Entity
@Table(name="users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String email;
	private String name;
	private String pasahitza;
	
	public User(){}
	
	public User(String email, String name, String pasahitza) {
		super();
		this.email = email;
		this.name = name;
		this.pasahitza = pasahitza;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPasahitza() {
		return pasahitza;
	}
	public void setPasahitza(String pasahitza) {
		this.pasahitza = pasahitza;
	}
	@Override
	public String toString() {
		return "User [email=" + email + ", name=" + name + ", pasahitza=" + pasahitza + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(email, name, pasahitza);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(email, other.email) && Objects.equals(name, other.name)
				&& Objects.equals(pasahitza, other.pasahitza);
	}
	
}
