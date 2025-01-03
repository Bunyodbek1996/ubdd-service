package uz.ciasev.ubdd_service.config.db;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DataSourceRouting extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setDataSource(String dataSource) {
        CONTEXT.set(dataSource);
    }

    public static String getDataSource() {
        return CONTEXT.get();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return CONTEXT.get();
    }
}