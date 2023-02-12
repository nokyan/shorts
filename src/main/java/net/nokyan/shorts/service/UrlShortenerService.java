package net.nokyan.shorts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ky.korins.blake3.Blake3;
import ky.korins.blake3.Hasher;
import net.nokyan.shorts.exception.AlreadyExistsException;
import net.nokyan.shorts.model.UrlMapping;
import net.nokyan.shorts.repository.UrlRepository;
import net.nokyan.shorts.utils.RandomUtils;

import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Service
public class UrlShortenerService {

    private static final List<String> FORBIDDEN_IDS = Arrays.asList("admin");

    @Autowired
    private UrlRepository urlRepository;

    @Value("${random-ids}")
    private boolean RANDOM_IDS;

    private static final int MAX_LENGTH = 64;

    @Value("${id-length}")
    private int ID_LENGTH;

    @Value("${shorten-unique-urls}")
    private boolean SHORTEN_UNIQUE_URLS;

    /**
     * Generates a unique identifier for a URL. If RANDOM_IDS is true, this method
     * generates a random string.
     * Otherwise, it uses the Blake3 hash function to generate a 256-bit hash and
     * truncates it to the desired length.
     *
     * @param longUrl the URL to be shortened
     * @return a short identifier for the URL
     */
    private String generateId(URL longUrl) {
        int length = Integer.min(MAX_LENGTH, ID_LENGTH);
        if (RANDOM_IDS) {
            // make sure we only generate IDs that are not in the FORBIDDEN_IDS list
            while (true) {
                String generated = RandomUtils.generateRandomString(length);
                if (FORBIDDEN_IDS.contains(generated)) {
                    continue;
                }
                return generated;
            }
        } else {
            Hasher hasher = Blake3.newHasher();
            hasher.update(longUrl.toString());

            String rawHash = hasher.doneBase64Url(256);

            // filter out any forbidden ID
            for (String id : FORBIDDEN_IDS) {
                rawHash.replace(id, "");
            }

            // cut string to right length
            rawHash.substring(0, length);

            return hasher.doneBase64Url(256).substring(0, length);
        }
    }

    /**
     * Shortens a URL and returns the shortened ID. If SHORTEN_UNIQUE_URLS is true
     * and the URL has already been
     * shortened before, this method returns the previously shortened ID.
     *
     * @param longUrl   the URL to be shortened
     * @param creatorIp the IP address of the user who created the short URL
     * @return the shortened ID
     */
    public String shortenUrl(URL longUrl, InetAddress creatorIp) {
        // If this URL has already been shortened before, return the best short
        List<UrlMapping> existingShorts = urlRepository.findByLongUrl(longUrl);
        if (SHORTEN_UNIQUE_URLS && !existingShorts.isEmpty()) {
            return existingShorts.get(0).getId();
        }

        String id = generateId(longUrl);
        UrlMapping mapping = new UrlMapping(id, longUrl, false, creatorIp);
        urlRepository.save(mapping);
        return id;
    }

    /**
     * Shortens a URL and assigns it a specific vanity ID.
     *
     * @param id        the desired vanity ID
     * @param longUrl   the URL to be shortened
     * @param creatorIp the IP address of the user who created the short URL
     * @throws AlreadyExistsException if the specified vanity ID is already in use
     */
    public void shortenUrlVanity(String id, URL longUrl, InetAddress creatorIp) throws AlreadyExistsException {
        if (urlRepository.findById(id).isPresent()) {
            throw new AlreadyExistsException("This vanity ID has already been taken");
        }

        UrlMapping mapping = new UrlMapping(id, longUrl, true, creatorIp);
        urlRepository.save(mapping);
    }

    /**
     * Retrieves the original URL associated with a shortened ID.
     *
     * @param id the shortened ID
     * @return the original URL
     */
    public UrlMapping getOriginalUrl(String id) {
        UrlMapping mapping = urlRepository.findById(id).orElseThrow();
        mapping.updateLastAccessedTimestamp();
        urlRepository.save(mapping);
        return mapping;
    }

}