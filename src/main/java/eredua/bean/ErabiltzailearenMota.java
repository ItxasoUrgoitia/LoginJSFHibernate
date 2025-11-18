package eredua.bean;

public class ErabiltzailearenMota {
	public String erabMota;
	public int kodea;

	public ErabiltzailearenMota() {
	}

	public ErabiltzailearenMota(int kodea, String erabMota) {
		this.erabMota = erabMota;
		this.kodea = kodea;
	}

	public String getErabMota() {
		return erabMota;
	}

	public void setErabMota(String erabMota) {
		this.erabMota = erabMota;
	}

	public int getKodea() {
		return kodea;
	}

	public void setKodea(int kodea) {
		this.kodea = kodea;
	}

	public String toString() {
		return erabMota;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + kodea;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErabiltzailearenMota other = (ErabiltzailearenMota) obj;
		if (kodea != other.kodea)
			return false;
		return true;
	}
	
	
}