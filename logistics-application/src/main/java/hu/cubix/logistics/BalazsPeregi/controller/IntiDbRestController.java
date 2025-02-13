package hu.cubix.logistics.BalazsPeregi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.cubix.logistics.BalazsPeregi.service.InitDbService;

@RestController
public class IntiDbRestController {

	@Autowired
	private InitDbService service;

	@GetMapping("/clear")
	public void clearDb() {
		service.clearDb();
	}

	@GetMapping("/init")
	public void insertTestData() {
		service.insertTestData();
	}
}
