package hu.cubix.logistics.BalazsPeregi.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;

public class MilestoneDto {
	@PositiveOrZero
	private long id;
	private AddressDto address;
	@Future
	private LocalDateTime plannedTime;

	public MilestoneDto() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AddressDto getAddress() {
		return address;
	}

	public void setAddress(AddressDto address) {
		this.address = address;
	}

	public LocalDateTime getPlannedTime() {
		return plannedTime;
	}

	public void setPlannedTime(LocalDateTime plannedTime) {
		this.plannedTime = plannedTime;
	}

	@Override
	public String toString() {
		return String.format("MilestoneDto [id=%s, address=%s, plannedTime=%s]", id, address, plannedTime);
	}

}
