package hu.cubix.logistics.BalazsPeregi.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class TransportPlanDto {
	@PositiveOrZero
	private long id;
	@Positive
	private int expectedIncome;
	private List<SectionDto> sections = new ArrayList<>();

	public TransportPlanDto() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getExpectedIncome() {
		return expectedIncome;
	}

	public void setExpectedIncome(int expectedIncome) {
		this.expectedIncome = expectedIncome;
	}

	public List<SectionDto> getSections() {
		return sections;
	}

	public void setSections(List<SectionDto> sections) {
		this.sections = sections;
	}

	@Override
	public String toString() {
		return String.format("TransportPlanDto [id=%s, expectedIncome=%s, sections=%s]", id, expectedIncome, sections);
	}

}
