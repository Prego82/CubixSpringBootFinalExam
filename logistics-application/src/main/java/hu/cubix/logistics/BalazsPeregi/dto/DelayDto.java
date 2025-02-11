package hu.cubix.logistics.BalazsPeregi.dto;

public class DelayDto {
	private long milestoneId;
	private int delayInMinutes;

	public DelayDto() {
	}

	public DelayDto(long milestoneId, int delayInMinutes) {
		super();
		this.milestoneId = milestoneId;
		this.delayInMinutes = delayInMinutes;
	}

	public void setMilestoneId(long milestoneId) {
		this.milestoneId = milestoneId;
	}

	public void setDelayInMinutes(int delayInMinutes) {
		this.delayInMinutes = delayInMinutes;
	}

	public long getMilestoneId() {
		return milestoneId;
	}

	public int getDelayInMinutes() {
		return delayInMinutes;
	}

}
