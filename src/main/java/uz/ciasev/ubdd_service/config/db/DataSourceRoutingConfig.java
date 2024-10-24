package uz.ciasev.ubdd_service.config.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceRoutingConfig {

    @Bean
    @Primary
    public AbstractRoutingDataSource dataSourceRouting(
            @Qualifier("postgresDataSource") DataSource postgresDataSource,
            @Qualifier("postgresDataSourceForReadOnly") DataSource postgresDataSourceForReadOnly) {
        DataSourceRouting routingDataSource = new DataSourceRouting();
        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put("postgres", postgresDataSource);
        dataSources.put("postgresDataSourceForReadOnly", postgresDataSourceForReadOnly);
        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(postgresDataSource);
        return routingDataSource;
    }
}
