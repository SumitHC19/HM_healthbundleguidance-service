package com.alight.healthbundle.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.types.Decimal128;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@ConditionalOnProperty(name = "bundleguidance.mongo.enabled", havingValue = "true", matchIfMissing = true)
@EnableMongoRepositories(basePackages = { "com.alight" })
@EnableMongoAuditing
public class MongoConfig extends AbstractMongoClientConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    /**
     * Registers custom converters for MongoDB data type handling:
     * - LocalDateTime ↔ String: Stores dates as ISO 8601 strings for consistency
     * - BigDecimal ↔ Decimal128: Stores numbers as BSON numeric types (not strings)
     *
     * @return MongoCustomConversions with type converters
     */
    @Bean
    @Override
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new LocalDateTimeToStringConverter());
        converters.add(new StringToLocalDateTimeConverter());
        converters.add(new BigDecimalToDecimal128Converter());
        converters.add(new Decimal128ToBigDecimalConverter());
        logger.info("Registered MongoDB converters: LocalDateTime↔String, BigDecimal↔Decimal128");
        return new MongoCustomConversions(converters);
    }

    /**
     * Customizes MappingMongoConverter to exclude _class discriminator field.
     * Spring Data MongoDB adds _class field by default for polymorphic types,
     * but it's unnecessary for our models and adds storage overhead.
     *
     * @param databaseFactory MongoDB database factory
     * @param context         MongoDB mapping context
     * @param conversions     MongoDB custom conversions
     * @return Configured MappingMongoConverter without _class field
     */
    @Bean
    @Override
    public MappingMongoConverter mappingMongoConverter(
            org.springframework.data.mongodb.MongoDatabaseFactory databaseFactory,
            org.springframework.data.mongodb.core.convert.MongoCustomConversions conversions,
            org.springframework.data.mongodb.core.mapping.MongoMappingContext context) {

        org.springframework.data.mongodb.core.convert.DbRefResolver dbRefResolver = new org.springframework.data.mongodb.core.convert.DefaultDbRefResolver(
                databaseFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);
        converter.setCustomConversions(conversions);

        // Remove _class field by setting type mapper to null
        converter.setTypeMapper(new org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper(null));
        converter.afterPropertiesSet();

        logger.info("Configured MappingMongoConverter to exclude _class field");
        return converter;
    }

    /**
     * Configures Jackson ObjectMapper for proper JSON serialization.
     * - WRITE_BIGDECIMAL_AS_PLAIN: Serializes BigDecimal as numbers, not strings
     * - WRITE_DATES_AS_TIMESTAMPS: Disabled to use ISO-8601 string format
     * - NON_NULL: Excludes null fields from JSON output
     * - JavaTimeModule: Handles LocalDateTime serialization
     *
     * @return Configured ObjectMapper for application-wide JSON processing
     */
    @Bean
    @Primary
    public com.fasterxml.jackson.databind.ObjectMapper objectMapper() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.configure(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        logger.info("Configured ObjectMapper with BigDecimal and LocalDateTime support");
        return mapper;
    }

    /**
     * Converts LocalDateTime to ISO 8601 String for MongoDB storage.
     */
    static class LocalDateTimeToStringConverter implements Converter<LocalDateTime, String> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public String convert(LocalDateTime source) {
            return source.format(FORMATTER);
        }
    }

    /**
     * Converts ISO 8601 String from MongoDB back to LocalDateTime.
     */
    static class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(String source) {
            return LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    /**
     * Converts BigDecimal to Decimal128 for MongoDB storage.
     * Ensures numeric values are stored as BSON numeric types, not strings.
     */
    @WritingConverter
    static class BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {
        @Override
        public Decimal128 convert(@NonNull BigDecimal source) {
            return new Decimal128(source);
        }
    }

    /**
     * Converts Decimal128 from MongoDB back to BigDecimal.
     */
    @ReadingConverter
    static class Decimal128ToBigDecimalConverter implements Converter<Decimal128, BigDecimal> {
        @Override
        public BigDecimal convert(@NonNull Decimal128 source) {
            return source.bigDecimalValue();
        }
    }

    @Value("${MONGO_HOST:${bundleguidance.mongo.host:localhost}}")
    private String mongoHost;

    @Value("${MONGO_PORT:${bundleguidance.mongo.port:27017}}")
    private String mongoPort;

    @Value("${MONGO_USERNAME:${bundleguidance.mongo.username:}}")
    private String mongoUsername;

    @Value("${MONGO_PASSWORD:${bundleguidance.mongo.password:}}")
    private String mongoPassword;

    @Value("${MONGO_DATABASE:${bundleguidance.mongo.database:}}")
    private String mongoDatabase;

    @Value("${MONGO_PARAMS:${bundleguidance.mongo.params:}}")
    private String mongoParams;

    @Value("${spring.profiles.active:default}")
    private String profile;

    @Value("${mongodb.tls.ca-file:}")
    private String tlsCaFile;

    @Override
    @Bean
    public MongoClient mongoClient() {
        logger.info("Connecting to MongoDB at {}:{}", mongoHost, mongoPort);

        String connectionStringBase = buildConnectionStringBase();
        boolean tlsEnabled = mongoParams != null &&
                (mongoParams.contains("tls=true") || mongoParams.contains("ssl=true"));

        if (tlsEnabled) {
            logger.info("TLS enabled - using JVM default truststore configured via JAVA_TOOL_OPTIONS");

            String trustStore = System.getProperty("javax.net.ssl.trustStore");
            String trustStoreType = System.getProperty("javax.net.ssl.trustStoreType");
            if (trustStore != null) {
                logger.info("JVM truststore configured: path={}, type={}", trustStore, trustStoreType);
            } else {
                logger.warn(
                        "No JVM truststore configured. SSL may fail if default truststore doesn't contain required certificates.");
            }

            try {
                MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(connectionStringBase))
                        .applyToSslSettings(builder -> {
                            builder.enabled(true);
                            builder.invalidHostNameAllowed(true);
                        })
                        .applyToSocketSettings(builder -> {
                            builder.connectTimeout(10, TimeUnit.SECONDS);
                            builder.readTimeout(15, TimeUnit.SECONDS);
                        });

                // Add credentials separately if provided (avoids URL encoding issues)
                if (mongoUsername != null && !mongoUsername.isEmpty() &&
                        mongoPassword != null && !mongoPassword.isEmpty()) {
                    MongoCredential credential = MongoCredential.createCredential(
                            mongoUsername,
                            "admin", // auth source
                            mongoPassword.toCharArray());
                    settingsBuilder.credential(credential);
                    logger.debug("Authentication credentials configured via MongoCredential");
                }

                MongoClientSettings settings = settingsBuilder.build();
                logger.info("MongoDB client configured with TLS using JVM truststore for profile: {}", profile);
                return MongoClients.create(settings);

            } catch (IllegalArgumentException e) {
                logger.error("Invalid MongoDB configuration: {}", e.getMessage(), e);
                throw new RuntimeException("Invalid MongoDB configuration: " + e.getMessage(), e);
            } catch (Exception e) {
                logger.error("Failed to configure MongoDB SSL/TLS - Host: {}, Port: {}, TLS: enabled, Error: {}",
                        mongoHost, mongoPort, e.getMessage(), e);
                throw new RuntimeException("Failed to configure MongoDB SSL/TLS: " + e.getMessage(), e);
            }
        } else {
            logger.info("TLS not enabled - using default connection");
            try {
                MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(connectionStringBase));

                // Add credentials separately if provided (avoids URL encoding issues)
                if (mongoUsername != null && !mongoUsername.isEmpty() &&
                        mongoPassword != null && !mongoPassword.isEmpty()) {
                    MongoCredential credential = MongoCredential.createCredential(
                            mongoUsername,
                            "admin", // auth source
                            mongoPassword.toCharArray());
                    settingsBuilder.credential(credential);
                    logger.debug("Authentication credentials configured via MongoCredential");
                }

                MongoClientSettings settings = settingsBuilder.build();
                return MongoClients.create(settings);
            } catch (Exception e) {
                logger.error("Failed to create MongoDB client - Host: {}, Port: {}, Error: {}",
                        mongoHost, mongoPort, e.getMessage(), e);
                throw new RuntimeException("Failed to create MongoDB client: " + e.getMessage(), e);
            }
        }
    }

    @Override
    protected String getDatabaseName() {
        if (mongoDatabase != null && !mongoDatabase.isEmpty()) {
            logger.info("Using configured database: {}", mongoDatabase);
            return mongoDatabase;
        }

        logger.warn("No database name configured, using default");
        return "packageguidance";
    }

    private String buildConnectionStringBase() {
        logger.debug(
                "Building MongoDB connection string - Host: {}, Port: {}, Database: {}, HasAuth: {}, HasParams: {}",
                mongoHost, mongoPort, mongoDatabase,
                (mongoUsername != null && !mongoUsername.isEmpty()),
                (mongoParams != null && !mongoParams.isEmpty()));

        if (mongoHost == null || mongoHost.isEmpty()) {
            throw new IllegalStateException("MongoDB host is not configured");
        }
        if (mongoPort == null || mongoPort.isEmpty()) {
            throw new IllegalStateException("MongoDB port is not configured");
        }

        // Build connection string WITHOUT credentials (credentials passed via
        // MongoCredential)
        StringBuilder uri = new StringBuilder("mongodb://");
        uri.append(mongoHost).append(":").append(mongoPort);

        if (mongoDatabase != null && !mongoDatabase.isEmpty()) {
            uri.append("/").append(mongoDatabase);
        }

        if (mongoParams != null && !mongoParams.isEmpty()) {
            uri.append("?").append(mongoParams);
            logger.debug("Connection parameters added: {}", mongoParams);
        }

        logger.info("MongoDB connection string built successfully for {}:{}", mongoHost, mongoPort);
        return uri.toString();
    }

}
