package net.nokyan.shorts.repository;

import java.net.URL;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.nokyan.shorts.model.UrlMapping;

public interface UrlRepository extends JpaRepository<UrlMapping, String> {
    @Query("SELECT u \n" + //
            "FROM UrlMapping u \n" + //
            "WHERE u.longUrl = ?1 \n" + //
            "ORDER BY u.vanity DESC, u.creationTimestamp ASC")
    List<UrlMapping> findByLongUrl(URL longUrl);
}
