package com.SWE.url_shortener.repository;
import com.SWE.url_shortener.model.url;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<url, Long> {
    // Look up a record by its shortCode
    Optional<url> findByShortCode(String shortCode);
    Optional<url> findByOriginalUrl(String originalUrl);
    boolean existsByShortCode(String shortCode);
}
