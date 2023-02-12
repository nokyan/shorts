package net.nokyan.shorts.repository;

import java.util.regex.Pattern;

import org.springframework.data.jpa.repository.JpaRepository;

import net.nokyan.shorts.model.BlocklistEntry;

public interface BlocklistRepository extends JpaRepository<BlocklistEntry, Pattern> {
}
