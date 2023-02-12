package net.nokyan.shorts.service;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.nokyan.shorts.model.BlocklistEntry;
import net.nokyan.shorts.repository.BlocklistRepository;
import net.nokyan.shorts.repository.UrlRepository;

@Service
public class AdminService {
    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private BlocklistRepository blocklistRepository;

    /**
     * Deletes a short URL and its associated data.
     *
     * @param id the shortened ID to be deleted
     */
    public void deleteUrl(String id) {
        urlRepository.deleteById(id);
    }

    /**
     * Add a Regex to the blocklist.
     *
     * @param regex Regex that matches with URLs that are supposed to be blocked
     */
    public void appendBlocklist(Pattern regex) {
        BlocklistEntry blocklistEntry = new BlocklistEntry(regex);
        blocklistRepository.save(blocklistEntry);
    }

    /**
     * Extends the blocklist by a list of new regexes
     *
     * @param regexes Collection of regexes to be added
     */
    public void extendBlocklist(Collection<Pattern> regexes) {
        regexes.stream().forEach(regex -> {
            this.appendBlocklist(regex);
        });
    }

    /**
     * Remove a Regex to the blocklist.
     *
     * @param regex Regex that is supposed to be removed
     */
    public void popBlocklist(Pattern regex) {
        blocklistRepository.deleteById(regex);
    }

    /**
     * Remove a list of Regexes to the blocklist
     *
     * @param regexes Collection of regexes to be removed
     */
    public void removeBlocklist(Collection<Pattern> regexes) {
        blocklistRepository.deleteAllById(regexes);
    }

    /**
     * Checks if a given URL matches any of the blocked URLs in the blocklist.
     *
     * @param url the URL to check
     * @return true if the URL is blocked, false otherwise
     */
    public boolean isBlocked(String url) {
        for (BlocklistEntry entry : blocklistRepository.findAll()) {
            if (entry.getRegex().matcher(url).find()) {
                return true;
            }
        }
        return false;
    }

    public List<Pattern> getBlocklist() {
        return blocklistRepository.findAll().stream().map(entry -> entry.getRegex()).toList();
    }
}
