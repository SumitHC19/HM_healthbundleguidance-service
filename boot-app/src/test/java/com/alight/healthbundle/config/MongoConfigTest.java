package com.alight.healthbundle.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MongoConfig.
 * These tests do NOT connect to a real MongoDB instance; they verify
 * configuration and conversion behavior.
 */
@ExtendWith(MockitoExtension.class)
class MongoConfigTest {

    private MongoConfig config;

    @BeforeEach
    void setUp() {
        config = new MongoConfig();
    }

    // --------------------------
    // Helper utilities
    // --------------------------

    private static void setField(Object target, String name, Object value) {
        try {
            Field f = MongoConfig.class.getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed setting field: " + name, e);
        }
    }

    private static String invokeBuildConnectionStringBase(MongoConfig cfg) {
        try {
            Method m = MongoConfig.class.getDeclaredMethod("buildConnectionStringBase");
            m.setAccessible(true);
            return (String) m.invoke(cfg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // --------------------------
    // MappingMongoConverter excludes _class
    // --------------------------

    static class TestEntity {
        private String id;
        private String name;

        TestEntity() {
        }

        TestEntity(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Entities used to verify custom conversions within MappingMongoConverter
    static class EntityWithDate {
        private LocalDateTime when;

        public EntityWithDate() {
        }

        public EntityWithDate(LocalDateTime when) {
            this.when = when;
        }

        public LocalDateTime getWhen() {
            return when;
        }

        public void setWhen(LocalDateTime when) {
            this.when = when;
        }
    }

    static class EntityWithAmount {
        private BigDecimal amount;

        public EntityWithAmount() {
        }

        public EntityWithAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    // --------------------------
    // ObjectMapper configuration
    // --------------------------

    static class SampleJson {
        public BigDecimal amount;
        public LocalDateTime when;
        public String optional; // null to ensure NON_NULL exclusion
    }

    @Test
    void objectMapper_serialization_plainBigDecimal_isoDate_and_nonNull() throws Exception {
        var mapper = config.objectMapper();

        SampleJson s = new SampleJson();
        s.amount = new BigDecimal("1E+3"); // should serialize as 1000 (plain)
        s.when = LocalDateTime.of(2024, 1, 2, 3, 4, 5);
        s.optional = null;

        String json = mapper.writeValueAsString(s);

        assertTrue(json.contains("\"amount\":1000"), "BigDecimal should be written as plain number");
        assertTrue(json.contains("\"when\":\"2024-01-02T03:04:05\""), "LocalDateTime should be ISO string");
        assertFalse(json.contains("optional"), "Null fields should be excluded (NON_NULL)");
    }

    // --------------------------
    // mongoClient() TLS enabled with credentials
    // --------------------------

    @Test
    void mongoClient_tlsEnabled_withCredentials_invokesMongoClientsCreate_withSslAndCredential() {
        setField(config, "mongoHost", "localhost");
        setField(config, "mongoPort", "27017");
        setField(config, "mongoDatabase", "db");
        setField(config, "mongoUsername", "user");
        setField(config, "mongoPassword", "secret");
        setField(config, "mongoParams", "tls=true&retryWrites=true");
        setField(config, "profile", "test");

        MongoClient mockClient = mock(MongoClient.class);

        try (MockedStatic<com.mongodb.client.MongoClients> mocked = mockStatic(com.mongodb.client.MongoClients.class)) {
            mocked.when(() -> com.mongodb.client.MongoClients.create(any(MongoClientSettings.class)))
                    .then(invocation -> {
                        MongoClientSettings settings = invocation.getArgument(0);
                        assertNotNull(settings);
                        // TLS path enables SSL
                        assertTrue(settings.getSslSettings().isEnabled(), "SSL must be enabled when tls=true");
                        // Credentials should be set
                        assertNotNull(settings.getCredential(),
                                "Credential must be present when username/password set");
                        // Socket settings configured (timeouts applied in TLS branch)
                        assertNotNull(settings.getSocketSettings(), "Socket settings should be configured");
                        return mockClient;
                    });

            MongoClient client = config.mongoClient();
            assertSame(mockClient, client);

            mocked.verify(() -> com.mongodb.client.MongoClients.create(any(MongoClientSettings.class)), times(1));
        }
    }

    // --------------------------
    // mongoClient() TLS enabled via ssl=true, wrapping errors
    // --------------------------

    @Test
    void mongoClient_tlsEnabled_viaSslParam_wrapsExceptions() {
        setField(config, "mongoHost", "localhost");
        setField(config, "mongoPort", "27017");
        setField(config, "mongoDatabase", "db");
        setField(config, "mongoUsername", "user");
        setField(config, "mongoPassword", "secret");
        setField(config, "mongoParams", "ssl=true"); // exercise ssl=true path (not tls=true)
        setField(config, "profile", "test");

        try (MockedStatic<com.mongodb.client.MongoClients> mocked = mockStatic(com.mongodb.client.MongoClients.class)) {
            mocked.when(() -> com.mongodb.client.MongoClients.create(any(MongoClientSettings.class)))
                    .thenThrow(new RuntimeException("SSL explode"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> config.mongoClient());
            assertTrue(ex.getMessage().contains("Failed to configure MongoDB SSL/TLS"));

            mocked.verify(() -> com.mongodb.client.MongoClients.create(any(MongoClientSettings.class)), times(1));
        }
    }

    // --------------------------
    // mongoClient() TLS disabled, no credentials
    // --------------------------

    @Test
    void mongoClient_tlsDisabled_noCredentials_invokesMongoClientsCreate_withoutSslAndCredential() {
        setField(config, "mongoHost", "127.0.0.1");
        setField(config, "mongoPort", "27018");
        setField(config, "mongoDatabase", "db2");
        setField(config, "mongoUsername", "");
        setField(config, "mongoPassword", "");
        setField(config, "mongoParams", ""); // no tls or ssl param

        MongoClient mockClient = mock(MongoClient.class);

        try (MockedStatic<com.mongodb.client.MongoClients> mocked = mockStatic(com.mongodb.client.MongoClients.class)) {
            mocked.when(() -> com.mongodb.client.MongoClients.create(any(MongoClientSettings.class)))
                    .then(invocation -> {
                        MongoClientSettings settings = invocation.getArgument(0);
                        assertNotNull(settings);
                        assertFalse(settings.getSslSettings().isEnabled(),
                                "SSL should be disabled when no tls/ssl param");
                        assertNull(settings.getCredential(), "No credential expected when username/password blank");
                        return mockClient;
                    });

            MongoClient client = config.mongoClient();
            assertSame(mockClient, client);

            mocked.verify(() -> com.mongodb.client.MongoClients.create(any(MongoClientSettings.class)), times(1));
        }
    }

    // --------------------------
    // mongoClient() TLS disabled, WITH credentials
    // --------------------------

    @Test
    void mongoClient_tlsDisabled_withCredentials_setsCredential() {
        setField(config, "mongoHost", "localhost");
        setField(config, "mongoPort", "27017");
        setField(config, "mongoDatabase", "db3");
        setField(config, "mongoUsername", "u1");
        setField(config, "mongoPassword", "p1");
        setField(config, "mongoParams", ""); // TLS disabled

        MongoClient mockClient = mock(MongoClient.class);

        try (MockedStatic<com.mongodb.client.MongoClients> mocked = mockStatic(com.mongodb.client.MongoClients.class)) {
            mocked.when(() -> com.mongodb.client.MongoClients.create(any(MongoClientSettings.class)))
                    .then(invocation -> {
                        MongoClientSettings settings = invocation.getArgument(0);
                        assertNotNull(settings);
                        assertFalse(settings.getSslSettings().isEnabled());
                        assertNotNull(settings.getCredential(),
                                "Credential should be present in non-TLS path when provided");
                        return mockClient;
                    });

            MongoClient client = config.mongoClient();
            assertSame(mockClient, client);
        }
    }

    // --------------------------
    // mongoClient() TLS disabled, username set but password empty => no credential
    // --------------------------

    @Test
    void mongoClient_tlsDisabled_usernameButNoPassword_doesNotSetCredential() {
        setField(config, "mongoHost", "localhost");
        setField(config, "mongoPort", "27017");
        setField(config, "mongoDatabase", "db4");
        setField(config, "mongoUsername", "userOnly");
        setField(config, "mongoPassword", ""); // blank
        setField(config, "mongoParams", "");

        MongoClient mockClient = mock(MongoClient.class);

        try (MockedStatic<com.mongodb.client.MongoClients> mocked = mockStatic(com.mongodb.client.MongoClients.class)) {
            mocked.when(() -> com.mongodb.client.MongoClients.create(any(MongoClientSettings.class)))
                    .then(invocation -> {
                        MongoClientSettings settings = invocation.getArgument(0);
                        assertNull(settings.getCredential(), "Credential must be null when password is blank");
                        return mockClient;
                    });

            MongoClient client = config.mongoClient();
            assertSame(mockClient, client);
        }
    }

    // --------------------------
    // mongoClient() TLS disabled and create() throws -> wrap
    // --------------------------

    @Test
    void mongoClient_tlsDisabled_wrapsCreateExceptions() {
        setField(config, "mongoHost", "localhost");
        setField(config, "mongoPort", "27017");
        setField(config, "mongoDatabase", "db");
        setField(config, "mongoUsername", "");
        setField(config, "mongoPassword", "");
        setField(config, "mongoParams", "");

        try (MockedStatic<com.mongodb.client.MongoClients> mocked = mockStatic(com.mongodb.client.MongoClients.class)) {
            mocked.when(() -> com.mongodb.client.MongoClients.create(any(MongoClientSettings.class)))
                    .thenThrow(new RuntimeException("driver init error"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> config.mongoClient());
            assertTrue(ex.getMessage().contains("Failed to create MongoDB client"));
        }
    }

    // --------------------------
    // getDatabaseName()
    // --------------------------

    @Test
    void getDatabaseName_returnsConfigured_whenPresent() {
        setField(config, "mongoDatabase", "healthdb");
        assertEquals("healthdb", config.getDatabaseName());
    }

    @Test
    void getDatabaseName_returnsDefault_whenMissing() {
        setField(config, "mongoDatabase", "");
        assertEquals("packageguidance", config.getDatabaseName());
    }

    // --------------------------
    // buildConnectionStringBase()
    // --------------------------

    @Test
    void buildConnectionStringBase_buildsUri_withDatabase_andParams() {
        setField(config, "mongoHost", "db.host");
        setField(config, "mongoPort", "27017");
        setField(config, "mongoDatabase", "alight");
        setField(config, "mongoParams", "tls=true&appName=bundle-guidance");

        String uri = invokeBuildConnectionStringBase(config);
        assertEquals("mongodb://db.host:27017/alight?tls=true&appName=bundle-guidance", uri);
    }

    @Test
    void buildConnectionStringBase_buildsUri_withoutDatabase_withParams() {
        setField(config, "mongoHost", "db.host");
        setField(config, "mongoPort", "27017");
        setField(config, "mongoDatabase", "");
        setField(config, "mongoParams", "foo=bar");

        String uri = invokeBuildConnectionStringBase(config);
        assertEquals("mongodb://db.host:27017?foo=bar", uri);
    }

    @Test
    void buildConnectionStringBase_throws_whenPortMissing() {
        setField(config, "mongoHost", "localhost");
        setField(config, "mongoPort", "");
        setField(config, "mongoDatabase", "db");
        setField(config, "mongoParams", "");

        assertThrows(RuntimeException.class,
                () -> invokeBuildConnectionStringBase(config));
    }

    // --------------------------
    // Custom Converters - direct round-trip tests
    // --------------------------

    @Test
    void converters_localDateTime_roundTrip_direct() {
        MongoConfig.LocalDateTimeToStringConverter toString = new MongoConfig.LocalDateTimeToStringConverter();
        MongoConfig.StringToLocalDateTimeConverter toLdt = new MongoConfig.StringToLocalDateTimeConverter();

        LocalDateTime now = LocalDateTime.of(2024, 6, 1, 12, 34, 56);
        String s = toString.convert(now);
        assertEquals("2024-06-01T12:34:56", s);

        LocalDateTime parsed = toLdt.convert(s);
        assertEquals(now, parsed);
    }

    @Test
    void mongoClient_tlsDisabled_invalidPort_wrapsCreateClientError() {
        // Arrange: invalid port makes the ConnectionString constructor throw
        // IllegalArgumentException
        setField(config, "mongoHost", "localhost");
        setField(config, "mongoPort", "abc"); // invalid port to break ConnectionString
        setField(config, "mongoDatabase", "db");
        setField(config, "mongoParams", ""); // TLS disabled path

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> config.mongoClient());
        assertTrue(ex.getMessage().contains("Failed to create MongoDB client"),
                "Should wrap with 'Failed to create MongoDB client'");
    }

    @Test
    void converters_bigDecimal_roundTrip_direct() {
        MongoConfig.BigDecimalToDecimal128Converter toD128 = new MongoConfig.BigDecimalToDecimal128Converter();
        MongoConfig.Decimal128ToBigDecimalConverter toBd = new MongoConfig.Decimal128ToBigDecimalConverter();

        BigDecimal[] values = {
                new BigDecimal("0"),
                new BigDecimal("123.456"),
                new BigDecimal("-789.0001"),
                new BigDecimal("1E+20")
        };

        for (BigDecimal v : values) {
            Decimal128 d = toD128.convert(v);
            assertNotNull(d);
            BigDecimal back = toBd.convert(d);
            assertEquals(v, back, "Round-trip BigDecimal -> Decimal128 -> BigDecimal must match");
        }

    }

    @Test
    void mongoClient_tlsEnabled_invalidPort_triggersIllegalArgumentCatch() {
        // Arrange: invalid port to make new ConnectionString(...) throw
        // IllegalArgumentException
        setField(config, "mongoHost", "localhost");
        setField(config, "mongoPort", "-1"); // negative port is invalid for URI; should throw IllegalArgumentException
        setField(config, "mongoDatabase", "db");
        setField(config, "mongoParams", "tls=true"); // ensure TLS branch
        setField(config, "mongoUsername", ""); // no credentials (doesn't matter here)
        setField(config, "mongoPassword", "");

        // IMPORTANT: do NOT mock MongoClients.create here—exception happens before it's
        // called

        RuntimeException ex = assertThrows(RuntimeException.class, () -> config.mongoClient());
        assertTrue(
                ex.getMessage().startsWith("Invalid MongoDB configuration:"),
                "Should wrap IllegalArgumentException with 'Invalid MongoDB configuration: ...'");
    }
}
