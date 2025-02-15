package hu.cubix.logistics.BalazsPeregi.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import hu.cubix.logistics.BalazsPeregi.config.DelayCosts;
import hu.cubix.logistics.BalazsPeregi.config.DelayCosts.Decrease;
import hu.cubix.logistics.BalazsPeregi.model.Section;
import hu.cubix.logistics.BalazsPeregi.model.TransportPlan;
import hu.cubix.logistics.BalazsPeregi.repository.MilestoneRepository;
import hu.cubix.logistics.BalazsPeregi.repository.TransportPlanRepository;
import jakarta.transaction.Transactional;

@Service
public class TransportPlanService {

	@Autowired
	private TransportPlanRepository transportPlanRepo;

	@Autowired
	private MilestoneRepository milestoneRepo;

	@Autowired
	private DelayCosts delayCosts;

	@Transactional
	public TransportPlan addDelay(long transportPlanId, long milestoneId, int delay) {
		TransportPlan transportPlan = transportPlanRepo.findByIdWithFullData(transportPlanId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!milestoneRepo.existsById(milestoneId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		List<Section> sections = transportPlan.getSections().stream().sorted(Comparator.comparing(Section::getOrderNr))
				.toList();
		Section selectedSection = sections.stream()
				.filter(section -> section.getStartMilestone().getId() == milestoneId
						|| section.getEndMilestone().getId() == milestoneId)
				.findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

		boolean isStartMilestone = selectedSection.getStartMilestone().getId() == milestoneId;
		Section nextSection = getNextSection(sections, selectedSection);
		applyDelay(delay, selectedSection, nextSection, isStartMilestone);
		decreaseExpectedIncome(transportPlan, delay);
		return transportPlan;
	}

	private void decreaseExpectedIncome(TransportPlan transportPlan, int delay) {
		double reductionPercentage = getReductionPercentage(delay);
		double reducedAmount = transportPlan.getExpectedIncome() * (reductionPercentage / 100.0);
		transportPlan.setExpectedIncome(transportPlan.getExpectedIncome() - reducedAmount);
	}

	private double getReductionPercentage(int delay) {
		return delayCosts.getDecreases().stream()
				.filter(decrease -> delay >= decrease.getFrom() && delay < decrease.getTo())
				.map(Decrease::getDecreasePercentage).findFirst().orElse(0);
	}

	private static Section getNextSection(List<Section> sections, Section currentSection) {
		int index = sections.indexOf(currentSection);
		return (index < sections.size() - 1) ? sections.get(index + 1) : null;
	}

	/**
	 * Applies delays to planned time of the milestone:</br>
	 * If the milestone was a start milestone of a section, add the delay to the
	 * planned time of the end milestone of the section as well</br>
	 * If the milestone was an end milestone of a section, add the delay to the
	 * planned time of the start milestone of the next section
	 *
	 * @param delay
	 * @param section
	 * @param nextSection
	 * @param isStartMilestone
	 */
	private static void applyDelay(int delay, Section section, Section nextSection, boolean isStartMilestone) {
		section.getEndMilestone().setPlannedTime(section.getEndMilestone().getPlannedTime().plusMinutes(delay));
		if (isStartMilestone) {
			section.getStartMilestone().setPlannedTime(section.getStartMilestone().getPlannedTime().plusMinutes(delay));
		} else if (nextSection != null) {
			nextSection.getStartMilestone()
					.setPlannedTime(nextSection.getStartMilestone().getPlannedTime().plusMinutes(delay));
		}
	}
}
