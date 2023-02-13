package net.nokyan.shorts;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import net.nokyan.shorts.controller.LinkController;
import net.nokyan.shorts.model.UrlMapping;
import net.nokyan.shorts.service.AdminService;
import net.nokyan.shorts.service.UrlShortenerService;

import java.net.URL;
import java.util.NoSuchElementException;

class LinkControllerTest {

	@Mock
	private UrlShortenerService urlShortenerService;

	@Mock
	private AdminService adminService;

	@InjectMocks
	private LinkController linkController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testShortenUrlVanity_withValidVanityIdAndAuthToken() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServerName("example.com");
		request.setScheme("http");

		String longUrlString = "http://example.com/long-url";
		String vanityId = "customId";

		ReflectionTestUtils.setField(linkController, "vanityAuthToken", "abc123");

		doNothing().when(urlShortenerService).shortenUrlVanity(eq(vanityId), any(URL.class), any());

		ResponseEntity<String> response = linkController.shortenUrlVanity("abc123", vanityId, request,
				longUrlString);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("http://example.com/customId", response.getBody());
	}

	@Test
	void testShortenUrlVanity_withInvalidVanityAuthToken() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		String longUrlString = "http://example.com/long-url";
		String vanityId = "customId";

		ResponseEntity<String> response = linkController.shortenUrlVanity("invalidToken", vanityId, request,
				longUrlString);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	void testShortenUrlVanity_withMalformedUrl() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		String longUrlString = "invalid-url";

		ResponseEntity<String> response = linkController.shortenUrlVanity(null, null, request, longUrlString);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testRedirectToOriginal_withValidShortUrl() throws Exception {
		String shortUrl = "shortId";
		URL originalUrl = new URL("http://example.com/original-url");
		UrlMapping mapping = new UrlMapping(shortUrl, originalUrl, false);

		when(urlShortenerService.getOriginalUrl(shortUrl)).thenReturn(mapping);
		when(adminService.isBlocked(originalUrl.toString())).thenReturn(false);

		ResponseEntity<Void> response = linkController.redirectToOriginal(shortUrl);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertEquals(originalUrl.toURI(), response.getHeaders().getLocation());
	}

	@Test
	void testRedirectToOriginal_withBlockedUrl() throws Exception {
		String shortUrl = "shortId";
		URL originalUrl = new URL("http://example.com/original-url");
		UrlMapping mapping = new UrlMapping(shortUrl, originalUrl, false);

		when(urlShortenerService.getOriginalUrl(shortUrl)).thenReturn(mapping);
		when(adminService.isBlocked(originalUrl.toString())).thenReturn(true);

		ResponseEntity<Void> response = linkController.redirectToOriginal(shortUrl);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testRedirectToOriginal_withNonExistentShortUrl() {
		String shortUrl = "nonexistent";

		when(urlShortenerService.getOriginalUrl(shortUrl)).thenThrow(new NoSuchElementException());

		ResponseEntity<Void> response = linkController.redirectToOriginal(shortUrl);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testDeleteShort_withValidAdminAuthToken() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		String id = "shortId";

		doNothing().when(adminService).deleteUrl(id);

		ReflectionTestUtils.setField(linkController, "adminAuthToken", "123abc");

		ResponseEntity<String> response = linkController.deleteShort("123abc", id, request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testDeleteShort_withInvalidAdminAuthToken() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		String id = "shortId";

		ResponseEntity<String> response = linkController.deleteShort("invalidToken", id, request);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
}