package com.udayan.tallyapp.appproperty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.core.env.StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class ReadPropertiesFromDB implements EnvironmentPostProcessor, ApplicationListener<ApplicationEvent> {

    private static final DeferredLog log = new DeferredLog();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        application.addInitializers(ctx -> log.replayTo(ReadPropertiesFromDB.class));

        log.debug("Loading application configuration attributes");

        PropertySource<?> system = environment.getPropertySources()
                .get(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);

        try {
            String[] profiles = environment.getActiveProfiles();

            String currentProfile = null;

            if (profiles.length == 0) {
                log.info("Setting profile : dev");
                currentProfile = "dev";
            }

            currentProfile = profiles[0];

            log.info("Current active profile : " + currentProfile);

            DataSource ds = createDataSource(environment);

            Map<String, Object> propertySource = loadProperties(ds, currentProfile);

            log.info("Total attributes : " + propertySource.size());

            environment.getPropertySources()
                    .addAfter(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, new MapPropertySource("applicationAttributes", propertySource));

        } catch (Throwable e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.replayTo(ReadPropertiesFromDB.class);
    }

    private Map<String, Object> loadProperties(DataSource dataSource, String profile) {
        String query = "SELECT app_key, app_value FROM app_property where profile='" + profile + "'";
        Map<String, Object> propertySource = new HashMap<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                propertySource.put(rs.getString("app_key"), rs.getString("app_value"));
            }
        } catch (SQLException e) {
            log.error("", e);
            throw new RuntimeException("Failed to load properties from the database.", e);
        }
        return propertySource;
    }

    private DataSource createDataSource(Environment environment) {
        return DataSourceBuilder
                .create()
                .username(environment.getProperty("spring.datasource.username"))
                .password(environment.getProperty("spring.datasource.password"))
                .url(environment.getProperty("spring.datasource.url"))
                .driverClassName(environment.getProperty("spring.database.driverClassName"))
                .build();
    }

}
