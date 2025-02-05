package hu.cubix.logistics.BalazsPeregi.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Milestone {
	@Id
	@GeneratedValue
	private long id;
	private Address address;
	private LocalDateTime plannedTime;

	public Milestone() {
	}

	public Milestone(Address address, LocalDateTime plannedTime) {
		this.address = address;
		this.plannedTime = plannedTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public LocalDateTime getPlannedTime() {
		return plannedTime;
	}

	public void setPlannedTime(LocalDateTime plannedTime) {
		this.plannedTime = plannedTime;
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
		Milestone other = (Milestone) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return String.format("Milestone [id=%s, address=%s, plannedTime=%s]", id, address, plannedTime);
	}

}
