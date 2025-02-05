package hu.cubix.logistics.BalazsPeregi.model;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class TransportPlan {
	@Id
	@GeneratedValue
	private long id;
	private int expectedIncome;

	@OneToMany
	private List<Section> sections;

	public TransportPlan() {
	}

	public TransportPlan(int expectedIncome, List<Section> sections) {
		this.expectedIncome = expectedIncome;
		this.sections = sections;
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

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	public void addSection(Section section) {
		this.sections.add(section);
		section.setTransportPlan(this);
		section.setOrderNr(this.sections.size() - 1);
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
		TransportPlan other = (TransportPlan) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return String.format("TransportPlan [id=%s, expectedIncome=%s, sections=%s]", id, expectedIncome, sections);
	}

}
