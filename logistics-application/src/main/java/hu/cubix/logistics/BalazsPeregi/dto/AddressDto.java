package hu.cubix.logistics.BalazsPeregi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AddressDto {
	private Long id;
	@NotNull
	@Pattern(regexp = "^[A-Z]{2}$")
	private String country;
	@NotNull
	@NotEmpty
	private String city;
	@NotNull
	@NotEmpty
	private String street;
	@NotNull
	@NotEmpty
	private String zip;
	@NotNull
	@NotEmpty
	private String houseNumber;
	private Double latitude;
	private Double longitude;

	public AddressDto() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return String.format(
				"AddressDto [id=%s, country=%s, city=%s, street=%s, zip=%s, houseNumber=%s, latitude=%s, longitude=%s]",
				id, country, city, street, zip, houseNumber, latitude, longitude);
	}

}
