package hu.cubix.logistics.BalazsPeregi.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Section {
	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	private TransportPlan transportPlan;
	@ManyToOne
	private Milestone startMilestone;
	@ManyToOne
	private Milestone endMilestone;

	private int orderNr;

	public Section() {
	}

	public Section(TransportPlan transportPlan, Milestone startMilestone, Milestone endMilestone, int orderNr) {
		this.transportPlan = transportPlan;
		this.startMilestone = startMilestone;
		this.endMilestone = endMilestone;
		this.orderNr = orderNr;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TransportPlan getTransportPlan() {
		return transportPlan;
	}

	public void setTransportPlan(TransportPlan transportPlan) {
		this.transportPlan = transportPlan;
	}

	public Milestone getStartMilestone() {
		return startMilestone;
	}

	public void setStartMilestone(Milestone startMilestone) {
		this.startMilestone = startMilestone;
	}

	public Milestone getEndMilestone() {
		return endMilestone;
	}

	public void setEndMilestone(Milestone endMilestone) {
		this.endMilestone = endMilestone;
	}

	public int getOrderNr() {
		return orderNr;
	}

	public void setOrderNr(int orderNr) {
		this.orderNr = orderNr;
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
		Section other = (Section) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return String.format("Section [id=%s, transportPlan=%s, startMilestone=%s, endMilestone=%s, orderNr=%s]", id,
				transportPlan, startMilestone, endMilestone, orderNr);
	}

}
