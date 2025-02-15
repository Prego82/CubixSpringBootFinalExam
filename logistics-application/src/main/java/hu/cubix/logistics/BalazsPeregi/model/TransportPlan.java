package hu.cubix.logistics.BalazsPeregi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;

@NamedEntityGraph(name = "TransportPlan.Full", attributeNodes = {
		@NamedAttributeNode(value = "sections", subgraph = "sectionsGraph") }, subgraphs = {
				@NamedSubgraph(name = "sectionsGraph", attributeNodes = {
						@NamedAttributeNode(value = "startMilestone", subgraph = "milestoneGraph"),
						@NamedAttributeNode(value = "endMilestone", subgraph = "milestoneGraph") }),
				@NamedSubgraph(name = "milestoneGraph", attributeNodes = { @NamedAttributeNode("address") }) })
@Entity
public class TransportPlan {
	@Id
	@GeneratedValue
	private long id;
	private double expectedIncome;

	@OneToMany(mappedBy = "transportPlan")
	private List<Section> sections = new ArrayList<>();

	public TransportPlan() {
	}

	public TransportPlan(double expectedIncome) {
		this.expectedIncome = expectedIncome;
	}

	public void addSection(Section section) {
		this.sections.add(section);
		section.setTransportPlan(this);
		section.setOrderNr(this.sections.size() - 1);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getExpectedIncome() {
		return expectedIncome;
	}

	public void setExpectedIncome(double expectedIncome) {
		this.expectedIncome = expectedIncome;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
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
