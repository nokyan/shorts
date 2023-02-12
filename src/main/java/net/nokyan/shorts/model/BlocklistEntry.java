package net.nokyan.shorts.model;

import java.time.Instant;
import java.util.Date;
import java.util.regex.Pattern;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
public class BlocklistEntry {
    @Id
    @Getter
    @Setter
    private Pattern regex;

    @Getter
    @Setter
    @NonNull
    private Date creationTimestamp;

    @PrePersist
    public void prePersistHook() {
        this.creationTimestamp = Date.from(Instant.now());
    }

    public BlocklistEntry() {
    }

    public BlocklistEntry(Pattern regex) {
        this.regex = regex;
    }
}