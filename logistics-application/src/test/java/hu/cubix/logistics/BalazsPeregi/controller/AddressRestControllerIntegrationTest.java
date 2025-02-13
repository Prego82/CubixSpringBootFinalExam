package hu.cubix.logistics.BalazsPeregi.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import hu.cubix.logistics.BalazsPeregi.dto.AddressDto;
import hu.cubix.logistics.BalazsPeregi.dto.LoginDto;
import hu.cubix.logistics.BalazsPeregi.repository.AddressRepository;
import hu.cubix.logistics.BalazsPeregi.security.SecurityConfig;
import hu.cubix.logistics.BalazsPeregi.service.AddressService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureWebTestClient(timeout = "10000m")
class AddressRestControllerIntegrationTest {

	private static final String API_ADDRESSES = "/api/addresses";
	@Autowired
	WebTestClient webTestClient;

	@Autowired
	AddressRepository addressRepo;

	@Autowired
	AddressService addressService;

	private String jwt;

	@BeforeEach
	void init() {
		addressRepo.deleteAll();
		LoginDto loginDto = new LoginDto();
		loginDto.setUsername(SecurityConfig.ADDRESS_MANAGER_NAME);
		loginDto.setPassword(SecurityConfig.PASS);
		jwt = webTestClient.post().uri("/api/login").bodyValue(loginDto).exchange().expectBody(String.class)
				.returnResult().getResponseBody();
	}

	@Test
	void testValidation() {
		// Arrange
		LoginDto loginDto = new LoginDto();
		loginDto.setUsername(SecurityConfig.TRANSPORT_MANAGER_NAME);
		loginDto.setPassword(SecurityConfig.PASS);
		String jwt = webTestClient.post().uri("/api/login").bodyValue(loginDto).exchange().expectBody(String.class)
				.returnResult().getResponseBody();
		// Act
		webTestClient.get().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt)).exchange().expectStatus()
				.isForbidden();
		// Assert
	}

	@Test
	void testAddAddressWithEmptyBody() {
		// Arrange
		// Act
		webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt)).exchange().expectStatus()
				.isBadRequest();
		// Assert
	}

	@Test
	void testAddAddressWithIdFilled() {
		// Arrange
		AddressDto newAddress = new AddressDto();
		newAddress.setId(100l);
		newAddress.setCountry("HU");
		newAddress.setZip("1111");
		newAddress.setCity("Budapest");
		newAddress.setStreet("Elso utca");
		newAddress.setHouseNumber("1");
		// Act
		webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt)).bodyValue(newAddress)
				.exchange().expectStatus().isBadRequest();
		// Assert
	}

	@Test
	void testAddAddressWithCountryNull() {
		// Arrange
		AddressDto newAddress = new AddressDto();
		newAddress.setZip("1111");
		newAddress.setCity("Budapest");
		newAddress.setStreet("Elso utca");
		newAddress.setHouseNumber("1");
		// Act
		webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt)).bodyValue(newAddress)
				.exchange().expectStatus().isBadRequest();
		// Assert
	}

	@Test
	void testAddAddressWithCityNull() {
		// Arrange
		AddressDto newAddress = new AddressDto();
		newAddress.setCountry("HU");
		newAddress.setZip("1111");
		newAddress.setStreet("Elso utca");
		newAddress.setHouseNumber("1");
		// Act
		webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt)).bodyValue(newAddress)
				.exchange().expectStatus().isBadRequest();
		// Assert
	}

	@Test
	void testAddAddressWithZipNull() {
		// Arrange
		AddressDto newAddress = new AddressDto();
		newAddress.setCountry("HU");
		newAddress.setCity("Budapest");
		newAddress.setStreet("Elso utca");
		newAddress.setHouseNumber("1");
		// Act
		webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt)).bodyValue(newAddress)
				.exchange().expectStatus().isBadRequest();
		// Assert
	}

	@Test
	void testAddAddressWithStreetNull() {
		// Arrange
		AddressDto newAddress = new AddressDto();
		newAddress.setCountry("HU");
		newAddress.setZip("1111");
		newAddress.setCity("Budapest");
		newAddress.setHouseNumber("1");
		// Act
		webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt)).bodyValue(newAddress)
				.exchange().expectStatus().isBadRequest();
		// Assert
	}

	@Test
	void testAddAddressWithHouseNumberNull() {
		// Arrange
		AddressDto newAddress = new AddressDto();
		newAddress.setCountry("HU");
		newAddress.setZip("1111");
		newAddress.setCity("Budapest");
		newAddress.setStreet("Elso utca");
		// Act
		webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt)).bodyValue(newAddress)
				.exchange().expectStatus().isBadRequest();
		// Assert
	}

	@Test
	void testAddAddress() {
		// Arrange
		AddressDto newAddress = new AddressDto();
		newAddress.setCountry("HU");
		newAddress.setZip("1111");
		newAddress.setCity("Budapest");
		newAddress.setStreet("Elso utca");
		newAddress.setHouseNumber("1");
		// Act
		AddressDto responseBody = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody();
		// Assert
		assertThat(responseBody.getId()).isNotNull();
		assertThat(responseBody.getCountry()).isEqualTo(newAddress.getCountry());
		assertThat(responseBody.getZip()).isEqualTo(newAddress.getZip());
		assertThat(responseBody.getCity()).isEqualTo(newAddress.getCity());
		assertThat(responseBody.getStreet()).isEqualTo(newAddress.getStreet());
		assertThat(responseBody.getHouseNumber()).isEqualTo(newAddress.getHouseNumber());
	}

	@Test
	void testGetAllAddress() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt)).bodyValue(newAddress1)
				.exchange();
		webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt)).bodyValue(newAddress2)
				.exchange();
		webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt)).bodyValue(newAddress3)
				.exchange();
		// Act
		List<AddressDto> allAddresses = webTestClient.get().uri(API_ADDRESSES)
				.headers(headers -> headers.setBearerAuth(jwt)).exchange().expectStatus().isOk()
				.expectBodyList(AddressDto.class).returnResult().getResponseBody();
		// Assert
		Collections.sort(allAddresses, Comparator.comparing(AddressDto::getHouseNumber));
		assertThat(allAddresses.size()).isEqualTo(3);
		assertThat(allAddresses.get(0).getId()).isNotNull();
		assertThat(allAddresses.get(0).getCountry()).isEqualTo(newAddress1.getCountry());
		assertThat(allAddresses.get(0).getZip()).isEqualTo(newAddress1.getZip());
		assertThat(allAddresses.get(0).getCity()).isEqualTo(newAddress1.getCity());
		assertThat(allAddresses.get(0).getStreet()).isEqualTo(newAddress1.getStreet());
		assertThat(allAddresses.get(0).getHouseNumber()).isEqualTo(newAddress1.getHouseNumber());
		assertThat(allAddresses.get(1).getId()).isNotNull();
		assertThat(allAddresses.get(1).getCountry()).isEqualTo(newAddress2.getCountry());
		assertThat(allAddresses.get(1).getZip()).isEqualTo(newAddress2.getZip());
		assertThat(allAddresses.get(1).getCity()).isEqualTo(newAddress2.getCity());
		assertThat(allAddresses.get(1).getStreet()).isEqualTo(newAddress2.getStreet());
		assertThat(allAddresses.get(1).getHouseNumber()).isEqualTo(newAddress2.getHouseNumber());
		assertThat(allAddresses.get(2).getId()).isNotNull();
		assertThat(allAddresses.get(2).getCountry()).isEqualTo(newAddress3.getCountry());
		assertThat(allAddresses.get(2).getZip()).isEqualTo(newAddress3.getZip());
		assertThat(allAddresses.get(2).getCity()).isEqualTo(newAddress3.getCity());
		assertThat(allAddresses.get(2).getStreet()).isEqualTo(newAddress3.getStreet());
		assertThat(allAddresses.get(2).getHouseNumber()).isEqualTo(newAddress3.getHouseNumber());
	}

	@Test
	void testFindNonExistingAddress() {
		// Arrange
		// Act
		webTestClient.get().uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("{id}").build("100"))
				.headers(headers -> headers.setBearerAuth(jwt)).exchange().expectStatus().isNotFound();
		// Assert

	}

	@Test
	void testFindAddress() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		// Act
		AddressDto responseBody = webTestClient.get()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/{id}").build(id1))
				.headers(headers -> headers.setBearerAuth(jwt)).exchange().expectStatus().isOk()
				.expectBody(AddressDto.class).returnResult().getResponseBody();
		// Assert
		assertThat(responseBody.getCountry()).isEqualTo(newAddress1.getCountry());
		assertThat(responseBody.getZip()).isEqualTo(newAddress1.getZip());
		assertThat(responseBody.getCity()).isEqualTo(newAddress1.getCity());
		assertThat(responseBody.getStreet()).isEqualTo(newAddress1.getStreet());
		assertThat(responseBody.getHouseNumber()).isEqualTo(newAddress1.getHouseNumber());
	}

	@Test
	void testDeleteNonExistingAddress() {
		// Arrange
		// Act
		webTestClient.delete().uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/{id}").build("100"))
				.headers(headers -> headers.setBearerAuth(jwt)).exchange().expectStatus().isOk();
		// Assert

	}

	@Test
	void testDeleteAddress() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		// Act
		webTestClient.delete().uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/{id}").build(id1))
				.headers(headers -> headers.setBearerAuth(jwt)).exchange().expectStatus().isOk();
		List<AddressDto> allAddresses = webTestClient.get().uri(API_ADDRESSES)
				.headers(headers -> headers.setBearerAuth(jwt)).exchange().expectStatus().isOk()
				.expectBodyList(AddressDto.class).returnResult().getResponseBody();
		// Assert
		Collections.sort(allAddresses, Comparator.comparing(AddressDto::getHouseNumber));
		assertThat(allAddresses.size()).isEqualTo(2);
		assertThat(allAddresses.get(0).getId()).isNotNull();
		assertThat(allAddresses.get(0).getCountry()).isEqualTo(newAddress2.getCountry());
		assertThat(allAddresses.get(0).getZip()).isEqualTo(newAddress2.getZip());
		assertThat(allAddresses.get(0).getCity()).isEqualTo(newAddress2.getCity());
		assertThat(allAddresses.get(0).getStreet()).isEqualTo(newAddress2.getStreet());
		assertThat(allAddresses.get(0).getHouseNumber()).isEqualTo(newAddress2.getHouseNumber());
		assertThat(allAddresses.get(1).getId()).isNotNull();
		assertThat(allAddresses.get(1).getCountry()).isEqualTo(newAddress3.getCountry());
		assertThat(allAddresses.get(1).getZip()).isEqualTo(newAddress3.getZip());
		assertThat(allAddresses.get(1).getCity()).isEqualTo(newAddress3.getCity());
		assertThat(allAddresses.get(1).getStreet()).isEqualTo(newAddress3.getStreet());
		assertThat(allAddresses.get(1).getHouseNumber()).isEqualTo(newAddress3.getHouseNumber());
	}

	@Test
	void testUpdateAddress() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto modifiedAddress = new AddressDto();
		modifiedAddress.setId(id1);
		modifiedAddress.setCountry("IT");
		modifiedAddress.setZip("9999");
		modifiedAddress.setCity("Roma");
		modifiedAddress.setStreet("Elso utca modositva");
		modifiedAddress.setHouseNumber("9");

		// Act
		AddressDto updatedAddress = webTestClient.put()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/{id}").build(id1))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(modifiedAddress).exchange().expectStatus()
				.isOk().expectBody(AddressDto.class).returnResult().getResponseBody();
		// Assert
		assertThat(updatedAddress.getId()).isEqualTo(modifiedAddress.getId());
		assertThat(updatedAddress.getCountry()).isEqualTo(modifiedAddress.getCountry());
		assertThat(updatedAddress.getZip()).isEqualTo(modifiedAddress.getZip());
		assertThat(updatedAddress.getCity()).isEqualTo(modifiedAddress.getCity());
		assertThat(updatedAddress.getStreet()).isEqualTo(modifiedAddress.getStreet());
		assertThat(updatedAddress.getHouseNumber()).isEqualTo(modifiedAddress.getHouseNumber());
	}

	@Test
	void testUpdateAddressEmptyBody() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto modifiedAddress = new AddressDto();
		modifiedAddress.setId(id1);
		modifiedAddress.setCountry("IT");
		modifiedAddress.setZip("9999");
		modifiedAddress.setCity("Roma");
		modifiedAddress.setStreet("Elso utca modositva");
		modifiedAddress.setHouseNumber("9");

		// Act
		webTestClient.put().uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/{id}").build(id1))
				.headers(headers -> headers.setBearerAuth(jwt)).exchange().expectStatus().isBadRequest();
	}

	@Test
	void testUpdateAddressIdMismatch() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto modifiedAddress = new AddressDto();
		modifiedAddress.setId(id1 + 1);
		modifiedAddress.setCountry("IT");
		modifiedAddress.setZip("9999");
		modifiedAddress.setCity("Roma");
		modifiedAddress.setStreet("Elso utca modositva");
		modifiedAddress.setHouseNumber("9");

		// Act
		webTestClient.put().uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/{id}").build(id1))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(modifiedAddress).exchange().expectStatus()
				.isBadRequest();
	}

	@Test
	void testUpdateAddressValidationFail() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto modifiedAddress = new AddressDto();
		modifiedAddress.setId(id1);
		modifiedAddress.setCountry("");
		modifiedAddress.setZip("");
		modifiedAddress.setCity("");
		modifiedAddress.setStreet("");
		modifiedAddress.setHouseNumber("");

		// Act
		webTestClient.put().uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/{id}").build(id1))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(modifiedAddress).exchange().expectStatus()
				.isBadRequest();
	}

	@Test
	void testUpdateNotExistingAddress() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		long nonExistentId = Math.max(id1, Math.max(id2, id3)) + 1;
		AddressDto modifiedAddress = new AddressDto();
		modifiedAddress.setId(nonExistentId);
		modifiedAddress.setCountry("IT");
		modifiedAddress.setZip("9999");
		modifiedAddress.setCity("Roma");
		modifiedAddress.setStreet("Elso utca modositva");
		modifiedAddress.setHouseNumber("9");

		// Act
		webTestClient.put()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/{id}").build(Long.toString(nonExistentId)))
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(modifiedAddress).exchange().expectStatus()
				.isBadRequest();
	}

	@Test
	void testFilterPrefixNotExistentCity() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setCity("Deb");
		// Act
		List<AddressDto> resultList = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "0")
						.queryParam("size", "3").queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectBodyList(AddressDto.class).returnResult().getResponseBody();
		// Assert
		assertThat(resultList).isEmpty();
	}

	@Test
	void testFilterPrefixExistingCity() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setCity("Bud");
		// Act
		EntityExchangeResult<List<AddressDto>> result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "0")
						.queryParam("size", "3").queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectHeader().exists("X-Total-Count").expectBodyList(AddressDto.class).returnResult();
		// Assert
		String totalCountHeader = result.getResponseHeaders().getFirst("X-Total-Count");
		assertThat(Integer.parseInt(totalCountHeader)).isEqualTo(3);
		List<AddressDto> resultList = result.getResponseBody();
		assertThat(resultList.size()).isEqualTo(3);
		assertThat(resultList.get(0).getId()).isEqualTo(id1);
		assertThat(resultList.get(0).getCountry()).isEqualTo(newAddress1.getCountry());
		assertThat(resultList.get(0).getZip()).isEqualTo(newAddress1.getZip());
		assertThat(resultList.get(0).getCity()).isEqualTo(newAddress1.getCity());
		assertThat(resultList.get(0).getStreet()).isEqualTo(newAddress1.getStreet());
		assertThat(resultList.get(0).getHouseNumber()).isEqualTo(newAddress1.getHouseNumber());
		assertThat(resultList.get(1).getId()).isEqualTo(id2);
		assertThat(resultList.get(1).getCountry()).isEqualTo(newAddress2.getCountry());
		assertThat(resultList.get(1).getZip()).isEqualTo(newAddress2.getZip());
		assertThat(resultList.get(1).getCity()).isEqualTo(newAddress2.getCity());
		assertThat(resultList.get(1).getStreet()).isEqualTo(newAddress2.getStreet());
		assertThat(resultList.get(1).getHouseNumber()).isEqualTo(newAddress2.getHouseNumber());
		assertThat(resultList.get(2).getId()).isEqualTo(id3);
		assertThat(resultList.get(2).getCountry()).isEqualTo(newAddress3.getCountry());
		assertThat(resultList.get(2).getZip()).isEqualTo(newAddress3.getZip());
		assertThat(resultList.get(2).getCity()).isEqualTo(newAddress3.getCity());
		assertThat(resultList.get(2).getStreet()).isEqualTo(newAddress3.getStreet());
		assertThat(resultList.get(2).getHouseNumber()).isEqualTo(newAddress3.getHouseNumber());
	}

	@Test
	void testFilterPrefixExistingCityWithPagination1() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setCity("Bud");
		// Act
		EntityExchangeResult<List<AddressDto>> result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "0")
						.queryParam("size", "1").queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectHeader().exists("X-Total-Count").expectBodyList(AddressDto.class).returnResult();
		// Assert
		String totalCountHeader = result.getResponseHeaders().getFirst("X-Total-Count");
		assertThat(Integer.parseInt(totalCountHeader)).isEqualTo(3);
		List<AddressDto> resultList = result.getResponseBody();
		assertThat(resultList.size()).isEqualTo(1);
		assertThat(resultList.get(0).getId()).isEqualTo(id1);
		assertThat(resultList.get(0).getCountry()).isEqualTo(newAddress1.getCountry());
		assertThat(resultList.get(0).getZip()).isEqualTo(newAddress1.getZip());
		assertThat(resultList.get(0).getCity()).isEqualTo(newAddress1.getCity());
		assertThat(resultList.get(0).getStreet()).isEqualTo(newAddress1.getStreet());
		assertThat(resultList.get(0).getHouseNumber()).isEqualTo(newAddress1.getHouseNumber());
	}

	@Test
	void testFilterPrefixExistingCityWithPagination2() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setCity("Bud");
		// Act
		EntityExchangeResult<List<AddressDto>> result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "1")
						.queryParam("size", "1").queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectHeader().exists("X-Total-Count").expectBodyList(AddressDto.class).returnResult();
		// Assert
		String totalCountHeader = result.getResponseHeaders().getFirst("X-Total-Count");
		assertThat(Integer.parseInt(totalCountHeader)).isEqualTo(3);
		List<AddressDto> resultList = result.getResponseBody();
		assertThat(resultList.size()).isEqualTo(1);
		assertThat(resultList.get(0).getId()).isEqualTo(id2);
		assertThat(resultList.get(0).getCountry()).isEqualTo(newAddress2.getCountry());
		assertThat(resultList.get(0).getZip()).isEqualTo(newAddress2.getZip());
		assertThat(resultList.get(0).getCity()).isEqualTo(newAddress2.getCity());
		assertThat(resultList.get(0).getStreet()).isEqualTo(newAddress2.getStreet());
		assertThat(resultList.get(0).getHouseNumber()).isEqualTo(newAddress2.getHouseNumber());
	}

	@Test
	void testFilterPrefixExistingCityWithPagination3() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setCity("Bud");
		// Act
		EntityExchangeResult<List<AddressDto>> result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "2")
						.queryParam("size", "1").queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectHeader().exists("X-Total-Count").expectBodyList(AddressDto.class).returnResult();
		// Assert
		String totalCountHeader = result.getResponseHeaders().getFirst("X-Total-Count");
		assertThat(Integer.parseInt(totalCountHeader)).isEqualTo(3);
		List<AddressDto> resultList = result.getResponseBody();
		assertThat(resultList.size()).isEqualTo(1);
		assertThat(resultList.get(0).getId()).isEqualTo(id3);
		assertThat(resultList.get(0).getCountry()).isEqualTo(newAddress3.getCountry());
		assertThat(resultList.get(0).getZip()).isEqualTo(newAddress3.getZip());
		assertThat(resultList.get(0).getCity()).isEqualTo(newAddress3.getCity());
		assertThat(resultList.get(0).getStreet()).isEqualTo(newAddress3.getStreet());
		assertThat(resultList.get(0).getHouseNumber()).isEqualTo(newAddress3.getHouseNumber());
	}

	@Test
	void testFilterPrefixExistingCityPaginationSizeParameterMissing() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setCity("Bud");
		// Act
		EntityExchangeResult<List<AddressDto>> result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "0")
						.queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectHeader().exists("X-Total-Count").expectBodyList(AddressDto.class).returnResult();
		// Assert
		String totalCountHeader = result.getResponseHeaders().getFirst("X-Total-Count");
		assertThat(Integer.parseInt(totalCountHeader)).isEqualTo(3);
		List<AddressDto> resultList = result.getResponseBody();
		assertThat(resultList.size()).isEqualTo(3);
		assertThat(resultList.get(0).getId()).isEqualTo(id1);
		assertThat(resultList.get(0).getCountry()).isEqualTo(newAddress1.getCountry());
		assertThat(resultList.get(0).getZip()).isEqualTo(newAddress1.getZip());
		assertThat(resultList.get(0).getCity()).isEqualTo(newAddress1.getCity());
		assertThat(resultList.get(0).getStreet()).isEqualTo(newAddress1.getStreet());
		assertThat(resultList.get(0).getHouseNumber()).isEqualTo(newAddress1.getHouseNumber());
		assertThat(resultList.get(1).getId()).isEqualTo(id2);
		assertThat(resultList.get(1).getCountry()).isEqualTo(newAddress2.getCountry());
		assertThat(resultList.get(1).getZip()).isEqualTo(newAddress2.getZip());
		assertThat(resultList.get(1).getCity()).isEqualTo(newAddress2.getCity());
		assertThat(resultList.get(1).getStreet()).isEqualTo(newAddress2.getStreet());
		assertThat(resultList.get(1).getHouseNumber()).isEqualTo(newAddress2.getHouseNumber());
		assertThat(resultList.get(2).getId()).isEqualTo(id3);
		assertThat(resultList.get(2).getCountry()).isEqualTo(newAddress3.getCountry());
		assertThat(resultList.get(2).getZip()).isEqualTo(newAddress3.getZip());
		assertThat(resultList.get(2).getCity()).isEqualTo(newAddress3.getCity());
		assertThat(resultList.get(2).getStreet()).isEqualTo(newAddress3.getStreet());
		assertThat(resultList.get(2).getHouseNumber()).isEqualTo(newAddress3.getHouseNumber());
	}

	@Test
	void testFilterPrefixExistingCityPaginationPageParameterMissing() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setCity("Bud");
		// Act
		EntityExchangeResult<List<AddressDto>> result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("size", "3")
						.queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectHeader().exists("X-Total-Count").expectBodyList(AddressDto.class).returnResult();
		// Assert
		String totalCountHeader = result.getResponseHeaders().getFirst("X-Total-Count");
		assertThat(Integer.parseInt(totalCountHeader)).isEqualTo(3);
		List<AddressDto> resultList = result.getResponseBody();
		assertThat(resultList.size()).isEqualTo(3);
		assertThat(resultList.get(0).getId()).isEqualTo(id1);
		assertThat(resultList.get(0).getCountry()).isEqualTo(newAddress1.getCountry());
		assertThat(resultList.get(0).getZip()).isEqualTo(newAddress1.getZip());
		assertThat(resultList.get(0).getCity()).isEqualTo(newAddress1.getCity());
		assertThat(resultList.get(0).getStreet()).isEqualTo(newAddress1.getStreet());
		assertThat(resultList.get(0).getHouseNumber()).isEqualTo(newAddress1.getHouseNumber());
		assertThat(resultList.get(1).getId()).isEqualTo(id2);
		assertThat(resultList.get(1).getCountry()).isEqualTo(newAddress2.getCountry());
		assertThat(resultList.get(1).getZip()).isEqualTo(newAddress2.getZip());
		assertThat(resultList.get(1).getCity()).isEqualTo(newAddress2.getCity());
		assertThat(resultList.get(1).getStreet()).isEqualTo(newAddress2.getStreet());
		assertThat(resultList.get(1).getHouseNumber()).isEqualTo(newAddress2.getHouseNumber());
		assertThat(resultList.get(2).getId()).isEqualTo(id3);
		assertThat(resultList.get(2).getCountry()).isEqualTo(newAddress3.getCountry());
		assertThat(resultList.get(2).getZip()).isEqualTo(newAddress3.getZip());
		assertThat(resultList.get(2).getCity()).isEqualTo(newAddress3.getCity());
		assertThat(resultList.get(2).getStreet()).isEqualTo(newAddress3.getStreet());
		assertThat(resultList.get(2).getHouseNumber()).isEqualTo(newAddress3.getHouseNumber());
	}

	@Test
	void testFilterPrefixExistingCityPaginationSortMissing() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setCity("Bud");
		// Act
		EntityExchangeResult<List<AddressDto>> result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "0")
						.queryParam("size", "3").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectHeader().exists("X-Total-Count").expectBodyList(AddressDto.class).returnResult();
		// Assert
		String totalCountHeader = result.getResponseHeaders().getFirst("X-Total-Count");
		assertThat(Integer.parseInt(totalCountHeader)).isEqualTo(3);
		List<AddressDto> resultList = result.getResponseBody();
		assertThat(resultList.get(0).getId()).isEqualTo(id1);
		assertThat(resultList.get(0).getCountry()).isEqualTo(newAddress1.getCountry());
		assertThat(resultList.get(0).getZip()).isEqualTo(newAddress1.getZip());
		assertThat(resultList.get(0).getCity()).isEqualTo(newAddress1.getCity());
		assertThat(resultList.get(0).getStreet()).isEqualTo(newAddress1.getStreet());
		assertThat(resultList.get(0).getHouseNumber()).isEqualTo(newAddress1.getHouseNumber());
		assertThat(resultList.get(1).getId()).isEqualTo(id2);
		assertThat(resultList.get(1).getCountry()).isEqualTo(newAddress2.getCountry());
		assertThat(resultList.get(1).getZip()).isEqualTo(newAddress2.getZip());
		assertThat(resultList.get(1).getCity()).isEqualTo(newAddress2.getCity());
		assertThat(resultList.get(1).getStreet()).isEqualTo(newAddress2.getStreet());
		assertThat(resultList.get(1).getHouseNumber()).isEqualTo(newAddress2.getHouseNumber());
		assertThat(resultList.get(2).getId()).isEqualTo(id3);
		assertThat(resultList.get(2).getCountry()).isEqualTo(newAddress3.getCountry());
		assertThat(resultList.get(2).getZip()).isEqualTo(newAddress3.getZip());
		assertThat(resultList.get(2).getCity()).isEqualTo(newAddress3.getCity());
		assertThat(resultList.get(2).getStreet()).isEqualTo(newAddress3.getStreet());
		assertThat(resultList.get(2).getHouseNumber()).isEqualTo(newAddress3.getHouseNumber());
	}

	@Test
	void testFilterPrefixNotExistentStreet() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setStreet("Negyedik");
		// Act
		List<AddressDto> resultList = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "0")
						.queryParam("size", "3").queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectBodyList(AddressDto.class).returnResult().getResponseBody();
		// Assert
		assertThat(resultList).isEmpty();
	}

	@Test
	void testFilterPrefixExistingStreet() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setStreet("Mas");
		// Act
		EntityExchangeResult<List<AddressDto>> result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "0")
						.queryParam("size", "3").queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectHeader().exists("X-Total-Count").expectBodyList(AddressDto.class).returnResult();
		// Assert
		String totalCountHeader = result.getResponseHeaders().getFirst("X-Total-Count");
		assertThat(Integer.parseInt(totalCountHeader)).isEqualTo(1);
		List<AddressDto> resultList = result.getResponseBody();
		assertThat(resultList.size()).isEqualTo(1);
		assertThat(resultList.get(0).getId()).isEqualTo(id2);
		assertThat(resultList.get(0).getCountry()).isEqualTo(newAddress2.getCountry());
		assertThat(resultList.get(0).getZip()).isEqualTo(newAddress2.getZip());
		assertThat(resultList.get(0).getCity()).isEqualTo(newAddress2.getCity());
		assertThat(resultList.get(0).getStreet()).isEqualTo(newAddress2.getStreet());
		assertThat(resultList.get(0).getHouseNumber()).isEqualTo(newAddress2.getHouseNumber());
	}

	@Test
	void testFilterCountryCodeNotExactMatch() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setCountry("H");
		// Act
		List<AddressDto> resultList = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "0")
						.queryParam("size", "3").queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectBodyList(AddressDto.class).returnResult().getResponseBody();
		// Assert
		assertThat(resultList).isEmpty();
	}

	@Test
	void testFilterCountryCodeExactMatch() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setCountry("HU");
		// Act
		EntityExchangeResult<List<AddressDto>> result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "0")
						.queryParam("size", "3").queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectHeader().exists("X-Total-Count").expectBodyList(AddressDto.class).returnResult();
		// Assert
		String totalCountHeader = result.getResponseHeaders().getFirst("X-Total-Count");
		assertThat(Integer.parseInt(totalCountHeader)).isEqualTo(3);
		List<AddressDto> resultList = result.getResponseBody();
		assertThat(resultList.size()).isEqualTo(3);
		assertThat(resultList.get(0).getId()).isEqualTo(id1);
		assertThat(resultList.get(0).getCountry()).isEqualTo(newAddress1.getCountry());
		assertThat(resultList.get(0).getZip()).isEqualTo(newAddress1.getZip());
		assertThat(resultList.get(0).getCity()).isEqualTo(newAddress1.getCity());
		assertThat(resultList.get(0).getStreet()).isEqualTo(newAddress1.getStreet());
		assertThat(resultList.get(0).getHouseNumber()).isEqualTo(newAddress1.getHouseNumber());
		assertThat(resultList.get(1).getId()).isEqualTo(id2);
		assertThat(resultList.get(1).getCountry()).isEqualTo(newAddress2.getCountry());
		assertThat(resultList.get(1).getZip()).isEqualTo(newAddress2.getZip());
		assertThat(resultList.get(1).getCity()).isEqualTo(newAddress2.getCity());
		assertThat(resultList.get(1).getStreet()).isEqualTo(newAddress2.getStreet());
		assertThat(resultList.get(1).getHouseNumber()).isEqualTo(newAddress2.getHouseNumber());
		assertThat(resultList.get(2).getId()).isEqualTo(id3);
		assertThat(resultList.get(2).getCountry()).isEqualTo(newAddress3.getCountry());
		assertThat(resultList.get(2).getZip()).isEqualTo(newAddress3.getZip());
		assertThat(resultList.get(2).getCity()).isEqualTo(newAddress3.getCity());
		assertThat(resultList.get(2).getStreet()).isEqualTo(newAddress3.getStreet());
		assertThat(resultList.get(2).getHouseNumber()).isEqualTo(newAddress3.getHouseNumber());
	}

	@Test
	void testFilterZipNotExactMatch() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setZip("11");
		// Act
		List<AddressDto> resultList = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "0")
						.queryParam("size", "3").queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectBodyList(AddressDto.class).returnResult().getResponseBody();
		// Assert
		assertThat(resultList).isEmpty();
	}

	@Test
	void testFilterZipCodeExactMatch() {
		// Arrange
		AddressDto newAddress1 = new AddressDto();
		newAddress1.setCountry("HU");
		newAddress1.setZip("1111");
		newAddress1.setCity("Budapest");
		newAddress1.setStreet("Elso utca");
		newAddress1.setHouseNumber("1");

		AddressDto newAddress2 = new AddressDto();
		newAddress2.setCountry("HU");
		newAddress2.setZip("2222");
		newAddress2.setCity("Budapest");
		newAddress2.setStreet("Masodik utca");
		newAddress2.setHouseNumber("2");

		AddressDto newAddress3 = new AddressDto();
		newAddress3.setCountry("HU");
		newAddress3.setZip("3333");
		newAddress3.setCity("Budapest");
		newAddress3.setStreet("Harmadik utca");
		newAddress3.setHouseNumber("3");

		Long id1 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress1).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id2 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress2).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();
		Long id3 = webTestClient.post().uri(API_ADDRESSES).headers(headers -> headers.setBearerAuth(jwt))
				.bodyValue(newAddress3).exchange().expectStatus().isOk().expectBody(AddressDto.class).returnResult()
				.getResponseBody().getId();

		AddressDto filter = new AddressDto();
		filter.setZip("1111");
		// Act
		EntityExchangeResult<List<AddressDto>> result = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path(API_ADDRESSES).path("/search").queryParam("page", "0")
						.queryParam("size", "3").queryParam("sort", "id,asc").build())
				.headers(headers -> headers.setBearerAuth(jwt)).bodyValue(filter).exchange().expectStatus().isOk()
				.expectHeader().exists("X-Total-Count").expectBodyList(AddressDto.class).returnResult();
		// Assert
		String totalCountHeader = result.getResponseHeaders().getFirst("X-Total-Count");
		assertThat(Integer.parseInt(totalCountHeader)).isEqualTo(1);
		List<AddressDto> resultList = result.getResponseBody();
		assertThat(resultList.size()).isEqualTo(1);
		assertThat(resultList.get(0).getId()).isEqualTo(id1);
		assertThat(resultList.get(0).getCountry()).isEqualTo(newAddress1.getCountry());
		assertThat(resultList.get(0).getZip()).isEqualTo(newAddress1.getZip());
		assertThat(resultList.get(0).getCity()).isEqualTo(newAddress1.getCity());
		assertThat(resultList.get(0).getStreet()).isEqualTo(newAddress1.getStreet());
		assertThat(resultList.get(0).getHouseNumber()).isEqualTo(newAddress1.getHouseNumber());
	}
}
