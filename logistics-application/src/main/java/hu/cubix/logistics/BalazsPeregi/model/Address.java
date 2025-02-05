package hu.cubix.logistics.BalazsPeregi.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Address {
	@Id
	@GeneratedValue
	private long id;
	private String country;
	private String city;
	private String street;
	private String zip;
	private String houseNumber;
	private String latitude;
	private String longitude;

	public Address() {
	}

	public Address(String country, String city, String street, String zip, String houseNumber, String latitude,
			String longitude) {
		this.country = country;
		this.city = city;
		this.street = street;
		this.zip = zip;
		this.houseNumber = houseNumber;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Address other = (Address) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return String.format(
				"Address [id=%s, country=%s, city=%s, street=%s, zip=%s, houseNumber=%s, latitude=%s, longitude=%s]",
				id, country, city, street, zip, houseNumber, latitude, longitude);
	}

}
