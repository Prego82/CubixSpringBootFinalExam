package hu.cubix.logistics.BalazsPeregi.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import hu.cubix.logistics.BalazsPeregi.dto.DelayDto;
import hu.cubix.logistics.BalazsPeregi.dto.LoginDto;
import hu.cubix.logistics.BalazsPeregi.model.Address;
import hu.cubix.logistics.BalazsPeregi.model.Milestone;
import hu.cubix.logistics.BalazsPeregi.model.Section;
import hu.cubix.logistics.BalazsPeregi.model.TransportPlan;
import hu.cubix.logistics.BalazsPeregi.repository.AddressRepository;
import hu.cubix.logistics.BalazsPeregi.repository.MilestoneRepository;
import hu.cubix.logistics.BalazsPeregi.repository.SectionRepository;
import hu.cubix.logistics.BalazsPeregi.repository.TransportPlanRepository;
import hu.cubix.logistics.BalazsPeregi.security.SecurityConfig;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureWebTestClient(timeout = "10000m")
public class TransportPlanRestControllerIntegrationTest {
	private static final String API_TRANSPORTPLAN = "/api/transportPlans";
	@Autowired
	WebTestClient webTestClient;

	@Autowired
	AddressRepository addressRepo;
	@Autowired
	MilestoneRepository milestoneRepo;
	@Autowired
	SectionRepository sectionRepo;
	@Autowired
	TransportPlanRepository transportPlanRepo;

	private String jwt;

	@BeforeEach
	void init() {
		sectionRepo.deleteAll();
		transportPlanRepo.deleteAll();
		milestoneRepo.deleteAll();
		addressRepo.deleteAll();

		LoginDto loginDto = new LoginDto();
		loginDto.setUsername(SecurityConfig.TRANSPORT_MANAGER_NAME);
		loginDto.setPassword(SecurityConfig.PASS);
		jwt = webTestClient.post().uri("/api/login").bodyValue(loginDto).exchange().expectBody(String.class)
				.returnResult().getResponseBody();
	}

	@Test
	void testValidation() {
		// Arrange
		LoginDto loginDto = new LoginDto();
		loginDto.setUsername(SecurityConfig.ADDRESS_MANAGER_NAME);
		loginDto.setPassword(SecurityConfig.PASS);
		String jwt = webTestClient.post().uri("/api/login").bodyValue(loginDto).exchange().expectBody(String.class)
				.returnResult().getResponseBody();
		// Act
		int delay = 15;
		DelayDto delayDto = new DelayDto(1, delay);
		webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(0)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus()
				.isForbidden();
		// Assert
	}

	@Test
	void testRegisterDelayWithNonExistentMilestoneId() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		Milestone startMilestone = milestoneRepo
				.save(new Milestone(startAddress, LocalDateTime.of(2025, 02, 12, 0, 0)));
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, LocalDateTime.of(2025, 02, 13, 0, 0)));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(1000d));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long nonExistentMilestoneId = Math.max(startMilestone.getId(), endMilestone.getId()) + 1;
		int delay = 15;
		DelayDto delayDto = new DelayDto(nonExistentMilestoneId, delay);
		// Act
		webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlan.getId())))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus()
				.isNotFound();
		// Assert
	}

	@Test
	void testRegisterDelayWithNonExistentTransportPlanId() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		Milestone startMilestone = milestoneRepo
				.save(new Milestone(startAddress, LocalDateTime.of(2025, 02, 12, 0, 0)));
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, LocalDateTime.of(2025, 02, 13, 0, 0)));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(1000d));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long nonExistentTransportPlanId = transportPlan.getId() + 1;
		int delay = 15;
		DelayDto delayDto = new DelayDto(endMilestone.getId(), delay);
		// Act
		webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(nonExistentTransportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus()
				.isNotFound();
		// Assert
	}

	@Test
	void testRegisterDelayMilestoneNotPartOfTransportPlanId() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		Milestone startMilestone = milestoneRepo
				.save(new Milestone(startAddress, LocalDateTime.of(2025, 02, 12, 0, 0)));
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, LocalDateTime.of(2025, 02, 13, 0, 0)));
		Milestone otherMilestone = milestoneRepo.save(new Milestone(endAddress, LocalDateTime.of(2025, 02, 14, 0, 0)));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(1000d));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 15;
		DelayDto delayDto = new DelayDto(otherMilestone.getId(), delay);
		// Act
		webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus()
				.isBadRequest();
		// Assert
	}

	@Test
	void testRegisterDelayMilestoneAddDelayToStartMilestone() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone = milestoneRepo.save(new Milestone(startAddress, startMilestoneTime));
		LocalDateTime endMilestoneTime = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, endMilestoneTime));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(1000d));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 15;
		DelayDto delayDto = new DelayDto(startMilestone.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getSections().get(0).getStartMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(startMilestoneTime.plusMinutes(delay));
		assertThat(result.getSections().get(0).getEndMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(endMilestoneTime.plusMinutes(delay));
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getSections().get(0).getStartMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(startMilestoneTime.plusMinutes(delay));
		assertThat(savedTP.getSections().get(0).getEndMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(endMilestoneTime.plusMinutes(delay));
	}

	@Test
	void testRegisterDelayMilestoneAddDelayToEndMilestoneWithoutNextSection() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone = milestoneRepo.save(new Milestone(startAddress, startMilestoneTime));
		LocalDateTime endMilestoneTime = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, endMilestoneTime));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(1000d));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 15;
		DelayDto delayDto = new DelayDto(endMilestone.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getSections().get(0).getStartMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(startMilestoneTime);
		assertThat(result.getSections().get(0).getEndMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(endMilestoneTime.plusMinutes(delay));
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getSections().get(0).getStartMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(startMilestoneTime);
		assertThat(savedTP.getSections().get(0).getEndMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(endMilestoneTime.plusMinutes(delay));
	}

	@Test
	void testRegisterDelayMilestoneAddDelayToEndMilestoneWithTWoSection() {
		// Arrange
		Address startAddress0 = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress0 = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime0 = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone0 = milestoneRepo.save(new Milestone(startAddress0, startMilestoneTime0));
		LocalDateTime endMilestoneTime0 = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone0 = milestoneRepo.save(new Milestone(endAddress0, endMilestoneTime0));
		Section section0 = sectionRepo.save(new Section(startMilestone0, endMilestone0, 0));

		Address startAddress1 = addressRepo.save(new Address("HU", "Budapest", "Harmadik utca", "3333", "3", 2d, 2d));
		Address endAddress1 = addressRepo.save(new Address("HU", "Budapest", "Negyedik utca", "4444", "4", 3d, 3d));
		LocalDateTime startMilestoneTime1 = LocalDateTime.of(2025, 02, 14, 0, 0);
		Milestone startMilestone1 = milestoneRepo.save(new Milestone(startAddress1, startMilestoneTime1));
		LocalDateTime endMilestoneTime1 = LocalDateTime.of(2025, 02, 15, 0, 0);
		Milestone endMilestone1 = milestoneRepo.save(new Milestone(endAddress1, endMilestoneTime1));
		Section section1 = sectionRepo.save(new Section(startMilestone1, endMilestone1, 1));

		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(1000d));
		transportPlan.addSection(section0);
		transportPlan.addSection(section1);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section0);
		sectionRepo.save(section1);
		long transportPlanId = transportPlan.getId();
		int delay = 15;
		DelayDto delayDto = new DelayDto(endMilestone0.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getSections().get(0).getStartMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(startMilestoneTime0);
		assertThat(result.getSections().get(0).getEndMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(endMilestoneTime0.plusMinutes(delay));
		assertThat(result.getSections().get(1).getStartMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(startMilestoneTime1.plusMinutes(delay));
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getSections().get(0).getStartMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(startMilestoneTime0);
		assertThat(savedTP.getSections().get(0).getEndMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(endMilestoneTime0.plusMinutes(delay));
		assertThat(savedTP.getSections().get(1).getStartMilestone().getPlannedTime())
				.isEqualToIgnoringNanos(startMilestoneTime1.plusMinutes(delay));
	}

	@Test
	void testRegisterDelayMilestoneDecreasePercentageAt29Mins() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone = milestoneRepo.save(new Milestone(startAddress, startMilestoneTime));
		LocalDateTime endMilestoneTime = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, endMilestoneTime));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		double expectedIncome = 1000d;
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(expectedIncome));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 29;
		DelayDto delayDto = new DelayDto(startMilestone.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getExpectedIncome()).isEqualTo(expectedIncome);
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getExpectedIncome()).isEqualTo(expectedIncome);
	}

	@Test
	void testRegisterDelayMilestoneDecreasePercentageAt30Mins() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone = milestoneRepo.save(new Milestone(startAddress, startMilestoneTime));
		LocalDateTime endMilestoneTime = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, endMilestoneTime));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		double expectedIncome = 1000d;
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(expectedIncome));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 30;
		DelayDto delayDto = new DelayDto(startMilestone.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getExpectedIncome()).isEqualTo(expectedIncome * 0.98);
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getExpectedIncome()).isEqualTo(expectedIncome * 0.98);
	}

	@Test
	void testRegisterDelayMilestoneDecreasePercentageAt31Mins() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone = milestoneRepo.save(new Milestone(startAddress, startMilestoneTime));
		LocalDateTime endMilestoneTime = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, endMilestoneTime));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		double expectedIncome = 1000d;
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(expectedIncome));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 31;
		DelayDto delayDto = new DelayDto(startMilestone.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getExpectedIncome()).isEqualTo(expectedIncome * 0.98);
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getExpectedIncome()).isEqualTo(expectedIncome * 0.98);
	}

	@Test
	void testRegisterDelayMilestoneDecreasePercentageAt59Mins() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone = milestoneRepo.save(new Milestone(startAddress, startMilestoneTime));
		LocalDateTime endMilestoneTime = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, endMilestoneTime));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		double expectedIncome = 1000d;
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(expectedIncome));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 59;
		DelayDto delayDto = new DelayDto(startMilestone.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getExpectedIncome()).isEqualTo(expectedIncome * 0.98);
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getExpectedIncome()).isEqualTo(expectedIncome * 0.98);
	}

	@Test
	void testRegisterDelayMilestoneDecreasePercentageAt60Mins() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone = milestoneRepo.save(new Milestone(startAddress, startMilestoneTime));
		LocalDateTime endMilestoneTime = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, endMilestoneTime));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		double expectedIncome = 1000d;
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(expectedIncome));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 60;
		DelayDto delayDto = new DelayDto(startMilestone.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getExpectedIncome()).isEqualTo(expectedIncome * 0.95);
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getExpectedIncome()).isEqualTo(expectedIncome * 0.95);
	}

	@Test
	void testRegisterDelayMilestoneDecreasePercentageAt61Mins() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone = milestoneRepo.save(new Milestone(startAddress, startMilestoneTime));
		LocalDateTime endMilestoneTime = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, endMilestoneTime));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		double expectedIncome = 1000d;
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(expectedIncome));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 61;
		DelayDto delayDto = new DelayDto(startMilestone.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getExpectedIncome()).isEqualTo(expectedIncome * 0.95);
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getExpectedIncome()).isEqualTo(expectedIncome * 0.95);
	}

	@Test
	void testRegisterDelayMilestoneDecreasePercentageAt119Mins() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone = milestoneRepo.save(new Milestone(startAddress, startMilestoneTime));
		LocalDateTime endMilestoneTime = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, endMilestoneTime));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		double expectedIncome = 1000d;
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(expectedIncome));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 119;
		DelayDto delayDto = new DelayDto(startMilestone.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getExpectedIncome()).isEqualTo(expectedIncome * 0.95);
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getExpectedIncome()).isEqualTo(expectedIncome * 0.95);
	}

	@Test
	void testRegisterDelayMilestoneDecreasePercentageAt120Mins() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone = milestoneRepo.save(new Milestone(startAddress, startMilestoneTime));
		LocalDateTime endMilestoneTime = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, endMilestoneTime));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		double expectedIncome = 1000d;
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(expectedIncome));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 120;
		DelayDto delayDto = new DelayDto(startMilestone.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getExpectedIncome()).isEqualTo(expectedIncome * 0.9);
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getExpectedIncome()).isEqualTo(expectedIncome * 0.9);
	}

	@Test
	void testRegisterDelayMilestoneDecreasePercentageAt200Mins() {
		// Arrange
		Address startAddress = addressRepo.save(new Address("HU", "Budapest", "Elso utca", "1111", "1", 0d, 0d));
		Address endAddress = addressRepo.save(new Address("HU", "Budapest", "Masodik utca", "2222", "2", 1d, 1d));
		LocalDateTime startMilestoneTime = LocalDateTime.of(2025, 02, 12, 0, 0);
		Milestone startMilestone = milestoneRepo.save(new Milestone(startAddress, startMilestoneTime));
		LocalDateTime endMilestoneTime = LocalDateTime.of(2025, 02, 13, 0, 0);
		Milestone endMilestone = milestoneRepo.save(new Milestone(endAddress, endMilestoneTime));
		Section section = sectionRepo.save(new Section(startMilestone, endMilestone, 0));
		double expectedIncome = 1000d;
		TransportPlan transportPlan = transportPlanRepo.save(new TransportPlan(expectedIncome));
		transportPlan.addSection(section);
		transportPlanRepo.save(transportPlan);
		sectionRepo.save(section);
		long transportPlanId = transportPlan.getId();
		int delay = 200;
		DelayDto delayDto = new DelayDto(startMilestone.getId(), delay);
		// Act
		TransportPlan result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_TRANSPORTPLAN).path("/{transportPlanId}/delay")
						.build(Long.toString(transportPlanId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(delayDto).exchange().expectStatus().isOk()
				.expectBody(TransportPlan.class).returnResult().getResponseBody();
		// Assert
		assertThat(result.getExpectedIncome()).isEqualTo(expectedIncome * 0.9);
		TransportPlan savedTP = transportPlanRepo.findByIdWithFullData(transportPlanId).get();
		assertThat(savedTP.getExpectedIncome()).isEqualTo(expectedIncome * 0.9);
	}
}
