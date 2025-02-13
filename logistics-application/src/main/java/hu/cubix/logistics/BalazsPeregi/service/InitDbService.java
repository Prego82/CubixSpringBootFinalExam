package hu.cubix.logistics.BalazsPeregi.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.cubix.logistics.BalazsPeregi.model.Address;
import hu.cubix.logistics.BalazsPeregi.model.Milestone;
import hu.cubix.logistics.BalazsPeregi.model.Section;
import hu.cubix.logistics.BalazsPeregi.model.TransportPlan;
import hu.cubix.logistics.BalazsPeregi.repository.AddressRepository;
import hu.cubix.logistics.BalazsPeregi.repository.MilestoneRepository;
import hu.cubix.logistics.BalazsPeregi.repository.SectionRepository;
import hu.cubix.logistics.BalazsPeregi.repository.TransportPlanRepository;
import jakarta.transaction.Transactional;

@Service
public class InitDbService {
	@Autowired
	AddressRepository addressRepo;
	@Autowired
	MilestoneRepository milestoneRepo;
	@Autowired
	SectionRepository sectionRepo;
	@Autowired
	TransportPlanRepository transportPlanRepo;

	public void clearDb() {
		transportPlanRepo.deleteAll();
		sectionRepo.deleteAll();
		milestoneRepo.deleteAll();
		addressRepo.deleteAll();
	}

	@Transactional
	public void insertTestData() {
		Address address1 = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address address2 = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		Address address3 = addressRepo.save(new Address("HU", "Budapest", "Harmadik utca", "3333", "3", 2d, 2d));
		Address address4 = addressRepo.save(new Address("HU", "Budapest", "Negyedik utca", "4444", "4", 3d, 3d));

		Milestone milestone1 = milestoneRepo.save(new Milestone(address1, LocalDateTime.of(2025, 02, 12, 0, 0)));
		Milestone milestone2 = milestoneRepo.save(new Milestone(address2, LocalDateTime.of(2025, 02, 13, 0, 0)));
		Milestone milestone3 = milestoneRepo.save(new Milestone(address3, LocalDateTime.of(2025, 02, 14, 0, 0)));
		Milestone milestone4 = milestoneRepo.save(new Milestone(address4, LocalDateTime.of(2025, 02, 15, 0, 0)));

		Section section1 = sectionRepo.save(new Section(milestone1, milestone2, 0));
		Section section2 = sectionRepo.save(new Section(milestone3, milestone4, 1));

		TransportPlan transportPlan1 = transportPlanRepo.save(new TransportPlan(1000d));
		transportPlan1.addSection(section1);
		transportPlan1.addSection(section2);
	}
}
