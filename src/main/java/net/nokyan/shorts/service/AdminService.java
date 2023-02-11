package net.nokyan.shorts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.nokyan.shorts.repository.UrlRepository;

@Service
public class AdminService {
    @Autowired
    private UrlRepository urlRepository;

    /**
     * Deletes a short URL and its associated data.
     *
     * @param id the shortened ID to be deleted
     */
    public void deleteUrl(String id) {
        urlRepository.deleteById(id);
    }
}
