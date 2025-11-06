package com.SWE.url_shortener.repository;
import com.SWE.url_shortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    // Look up a record by its shortCode
    Optional<Url> findByShortCode(String shortCode);
    Optional<Url> findByOriginalUrl(String originalUrl);
    boolean existsByShortCode(String shortCode);
}
