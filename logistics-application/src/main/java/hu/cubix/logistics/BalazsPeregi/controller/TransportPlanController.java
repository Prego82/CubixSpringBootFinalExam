package hu.cubix.logistics.BalazsPeregi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.cubix.logistics.BalazsPeregi.dto.DelayDto;
import hu.cubix.logistics.BalazsPeregi.dto.TransportPlanDto;
import hu.cubix.logistics.BalazsPeregi.mapper.TransportPlanMapper;
import hu.cubix.logistics.BalazsPeregi.service.TransportPlanService;

@RestController
@RequestMapping("/api/transportPlans")
public class TransportPlanController {

	@Autowired
	private TransportPlanService service;

	@Autowired
	private TransportPlanMapper mapper;

	@PostMapping("/{transportPlanId}/delay")
	public ResponseEntity<TransportPlanDto> registerDelay(@PathVariable long transportPlanId,
			@RequestBody DelayDto delay) {
		return ResponseEntity.ok(mapper.transportPlanToDto(
				service.addDelay(transportPlanId, delay.getMilestoneId(), delay.getDelayInMinutes())));
	}
}
