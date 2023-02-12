package net.nokyan.shorts.model;

import java.net.InetAddress;
import java.net.URL;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
public class UrlMapping {
    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    @NonNull
    private URL longUrl;

    @Getter
    @Setter
    @NonNull
    private Instant creationTimestamp;

    @Getter
    @Setter
    private Instant lastAccessedTimestamp;

    @Getter
    @Setter
    private InetAddress creatorIp;

    @Getter
    @Setter
    private boolean vanity;

    @PrePersist
    public void prePersistHook() {
        this.creationTimestamp = Instant.now();
    }

    public UrlMapping() {
    }

    public UrlMapping(String id, URL longUrl, boolean vanity) {
        this.id = id;
        this.longUrl = longUrl;
        this.vanity = vanity;
    }

    public UrlMapping(String id, URL longUrl, boolean vanity, InetAddress creatorIp) {
        this.id = id;
        this.longUrl = longUrl;
        this.creatorIp = creatorIp;
        this.vanity = vanity;
    }

    public void updateLastAccessedTimestamp() {
        this.setLastAccessedTimestamp(Instant.now());
    }
}