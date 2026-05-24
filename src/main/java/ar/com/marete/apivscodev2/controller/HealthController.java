package ar.com.marete.apivscodev2.controller;

import java.sql.Connection;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/db-health")
    public ResponseEntity<Map<String, String>> dbHealth() {
        try (Connection conn = dataSource.getConnection()) {
            return ResponseEntity.ok(Map.of(
                "status", "UP",
                "database", conn.getMetaData().getDatabaseProductName(),
                "version", conn.getMetaData().getDatabaseProductVersion()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "status", "DOWN",
                "error", e.getMessage()
            ));
        }
    }
}
