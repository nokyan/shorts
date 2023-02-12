package net.nokyan.shorts.repository;

import java.net.URL;
import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import net.nokyan.shorts.model.UrlMapping;

public interface UrlRepository extends JpaRepository<UrlMapping, String> {
    @Query("SELECT u FROM UrlMapping u WHERE u.longUrl = ?1 ORDER BY u.vanity DESC, u.creationTimestamp ASC")
    List<UrlMapping> findByLongUrl(URL longUrl);

    @Modifying
    @Transactional
    @Query("DELETE FROM UrlMapping u WHERE u.lastAccessedTimestamp IS NULL AND u.creationTimestamp < :cutoffTimestamp AND u.vanity = false")
    void deleteOlderThan(@Param("cutoffTimestamp") Instant cutoffTimestamp);
}
