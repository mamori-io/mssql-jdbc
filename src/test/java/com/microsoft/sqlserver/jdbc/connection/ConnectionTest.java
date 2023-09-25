/*
 * Microsoft JDBC Driver for SQL Server Copyright(c) Microsoft Corporation All rights reserved. This program is made
 * available under the terms of the MIT License. See the LICENSE file in the project root for more information.
 */
package com.microsoft.sqlserver.jdbc.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.microsoft.sqlserver.jdbc.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.microsoft.sqlserver.testframework.AbstractTest;


/*
 * This test is for testing various connection options
 */
@RunWith(JUnitPlatform.class)
public class ConnectionTest extends AbstractTest {

    @BeforeAll
    public static void setupTests() throws Exception {
        setConnection();
    }

    @Test
    public void testConnections() throws SQLException {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setURL(connectionString);
        ds.setKeyStoreAuthentication("KeyVaultClientSecret");
        ds.setKeyStorePrincipalId("placeholder");
        ds.setKeyStoreSecret("placeholder");

        // Multiple, successive connections should not fail
        try (Connection con = ds.getConnection()) {}

        try (Connection con = ds.getConnection()) {}
    }

    @Test
    public void testFailFastDatasourceConnection() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setURL(connectionString);
        ds.setDatabaseName("hghghghghghghghgh");
        ds.setFailFast(true);

        try (Connection con = ds.getConnection()) {
            con.close();
            Assertions.fail("The connection should have failed");
        } catch (SQLException ignored) {
        }
    }

    @Test
    public void testFailFastUrlConnection() {
        Properties props = new Properties();
        props.setProperty("database", "hghghghghghghghghg");
        props.setProperty("failFast", "true");

        try (Connection con = DriverManager.getConnection(connectionString, props)) {
            con.close();
            Assertions.fail("The connection should have failed");
        } catch (SQLException ignored) {
        }
    }

    @Test
    public void testConnectWithIPAddressPreference() throws SQLException {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setURL(connectionString);
        ds.setIPAddressPreference("IPv4First");
        try (Connection con = ds.getConnection()) {}
        ds.setIPAddressPreference("IPv6First");
        try (Connection con = ds.getConnection()) {}
        ds.setIPAddressPreference("UsePlatformDefault");
        try (Connection con = ds.getConnection()) {}
    }

    @Test
    public void testInvalidConnectWithIPAddressPreference() throws SQLException {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setURL(connectionString);
        ds.setIPAddressPreference("Bogus");
        try (Connection con = ds.getConnection()) {
            fail(TestResource.getResource("R_expectedFailPassed"));
        } catch (Exception e) {
            assertTrue(e.getMessage().matches(TestUtils.formatErrorMsg("R_InvalidIPAddressPreference")));
        }
    }

}
