package uz.ciasev.ubdd_service.config.db;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ReadOnlyDataSourceConfig {

    @Bean(name = "postgresDataSourceForReadOnly")
    @ConfigurationProperties(prefix = "reading-database.datasource")
    public DataSource postgresDataSourceForReadOnly() {
        return DataSourceBuilder.create().build();
    }
}
