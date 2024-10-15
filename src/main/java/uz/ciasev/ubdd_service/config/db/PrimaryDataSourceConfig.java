package uz.ciasev.ubdd_service.config.db;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class PrimaryDataSourceConfig {
    @Bean(name = "postgresDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create().build();
    }
}
