package net.nokyan.shorts.controller;

import net.nokyan.shorts.exception.AlreadyExistsException;
import net.nokyan.shorts.model.UrlMapping;
import net.nokyan.shorts.service.AdminService;
import net.nokyan.shorts.service.UrlShortenerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.google.common.net.InetAddresses;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping()
public class LinkController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private AdminService adminService;

    @Value("${vanity-ignore-blocklist}")
    private boolean vanityIgnoreBlocklist;

    @Value("${vanity-auth-token}")
    private String vanityAuthToken;

    @Value("${admin-auth-token}")
    private String adminAuthToken;

    /**
     * Shortens a given long URL and returns a shortened URL or an error if it
     * already exists. Allows for vanity short URLs if the correct token is
     * supplied, otherwise an ID will be generated.
     *
     * @param vanityAuth    authentication token for vanity URL (optional)
     * @param vanityId      ID of the vanity URL (optional)
     * @param request       HTTP request object
     * @param longUrlString long URL to be shortened
     * @return ResponseEntity with a shortened URL or an error code
     */
    @PostMapping(value = { "/{vanityId}", "/" })
    public ResponseEntity<String> shortenUrlVanity(
            @RequestHeader(value = "VANITY_AUTH", required = false) String vanityAuth,
            @PathVariable(required = false) String vanityId, HttpServletRequest request,
            @RequestBody String longUrlString) {
        boolean isVanity = StringUtils.hasText(vanityId);

        if (isVanity && (!StringUtils.hasText(vanityAuth) || !vanityAuth.equals(vanityAuthToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        URL longUrl;
        try {
            longUrl = new URL(longUrlString);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }

        if (!(vanityIgnoreBlocklist && isVanity) && adminService.isBlocked(longUrlString)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        InetAddress creatorIp = InetAddresses.forString(request.getRemoteAddr());

        String scheme = request.getScheme();
        String ownUrl = request.getServerName();
        String id = vanityId;

        if (isVanity) {
            try {
                urlShortenerService.shortenUrlVanity(vanityId, longUrl, creatorIp);
            } catch (AlreadyExistsException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } else {
            id = urlShortenerService.shortenUrl(longUrl, creatorIp);
        }

        String response = String.format("%s://%s/%s", scheme, ownUrl, id);

        return ResponseEntity.ok(response);
    }

    /**
     * Redirects to the original URL of a shortened URL.
     *
     * @param shortUrl ID of the shortened URL
     * @return ResponseEntity with HTTP status code 302 (Found)
     */
    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String shortUrl) {
        try {
            UrlMapping mapping = urlShortenerService.getOriginalUrl(shortUrl);

            // check if the URL has been blocklisted
            if (!(mapping.isVanity() && vanityIgnoreBlocklist)
                    && adminService.isBlocked(mapping.getLongUrl().toString())) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(HttpStatus.FOUND).location(mapping.getLongUrl().toURI()).build();
        } catch (URISyntaxException e) {
            log.error("Exception when trying to get long URL!", e);
            return ResponseEntity.internalServerError().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a URL.
     *
     * @param adminAuth authentication token for administrator access
     * @param id        ID of the URL to be deleted
     * @return ResponseEntity with an error code if not authorized or null
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteShort(@RequestHeader("ADMIN_AUTH") String adminAuth,
            @PathVariable String id, HttpServletRequest request) {
        if (!StringUtils.hasText(adminAuth) || !adminAuth.equals(adminAuthToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        adminService.deleteUrl(id);

        return ResponseEntity.ok().build();
    }
}