package hu.cubix.logistics.BalazsPeregi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public class AddressDto {
	@PositiveOrZero
	private long id;
	@Pattern(regexp = "^[A-Z]{2}$")
	@NotEmpty
	private String country;
	@NotEmpty
	private String city;
	@NotEmpty
	private String street;
	@NotEmpty
	private String zip;
	@NotEmpty
	private String houseNumber;
	@NotEmpty
	private String latitude;
	@NotEmpty
	private String longitude;

	public AddressDto() {
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
	public String toString() {
		return String.format(
				"AddressDto [id=%s, country=%s, city=%s, street=%s, zip=%s, houseNumber=%s, latitude=%s, longitude=%s]",
				id, country, city, street, zip, houseNumber, latitude, longitude);
	}

}
