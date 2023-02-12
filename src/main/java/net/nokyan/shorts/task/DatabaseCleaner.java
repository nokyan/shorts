package net.nokyan.shorts.task;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.nokyan.shorts.repository.UrlRepository;

@Slf4j
@Component
@EnableScheduling
public class DatabaseCleaner {

    @Autowired
    private UrlRepository urlRepository;

    @Value("${unused-short-ttl}")
    private int unusedShortTTL;

    @Scheduled(initialDelay = 0, fixedRateString = "${database-cleaning-period}", timeUnit = TimeUnit.SECONDS)
    public void pruneUnusedShorts() {
        log.info("Starting unused shorts pruning…");
        Instant cutoffTimestamp = Instant.now().minusSeconds(unusedShortTTL);
        urlRepository.deleteOlderThan(cutoffTimestamp);
    }
}
