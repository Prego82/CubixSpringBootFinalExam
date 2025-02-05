package hu.cubix.logistics.BalazsPeregi.dto;

import hu.cubix.logistics.BalazsPeregi.model.Milestone;
import jakarta.validation.constraints.PositiveOrZero;

public class SectionDto {
	@PositiveOrZero
	private long id;

	private Milestone startMilestone;
	private Milestone endMilestone;
	@PositiveOrZero
	private int orderNr;

	public SectionDto() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
	public String toString() {
		return String.format("SectionDto [id=%s, startMilestone=%s, endMilestone=%s, orderNr=%s]", id, startMilestone,
				endMilestone, orderNr);
	}

}
