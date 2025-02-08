package hu.cubix.logistics.BalazsPeregi.dto;

import jakarta.validation.constraints.PositiveOrZero;

public class SectionDto {
	@PositiveOrZero
	private long id;

	private MilestoneDto startMilestone;
	private MilestoneDto endMilestone;
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

	public MilestoneDto getStartMilestone() {
		return startMilestone;
	}

	public void setStartMilestone(MilestoneDto startMilestone) {
		this.startMilestone = startMilestone;
	}

	public MilestoneDto getEndMilestone() {
		return endMilestone;
	}

	public void setEndMilestone(MilestoneDto endMilestone) {
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
