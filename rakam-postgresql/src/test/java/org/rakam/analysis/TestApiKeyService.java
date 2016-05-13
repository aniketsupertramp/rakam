package org.rakam.analysis;

import com.google.common.collect.ImmutableSet;
import org.rakam.TestingEnvironment;
import org.rakam.analysis.ApiKeyService.AccessKeyType;
import org.rakam.postgresql.analysis.JDBCApiKeyService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TestApiKeyService extends TestingEnvironment {
    private static final String PROJECT_NAME = TestApiKeyService.class.getName().replace(".", "_").toLowerCase();

    private JDBCApiKeyService apiKeyService;

    @BeforeMethod
    public void setUpMethod() throws Exception {
        JDBCPoolDataSource apiKeyServiceDataSource = JDBCPoolDataSource.getOrCreateDataSource(getPostgresqlConfig());

        apiKeyService = new JDBCApiKeyService(apiKeyServiceDataSource);
        apiKeyService.setup();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        apiKeyService.clearCache();
        apiKeyService.revokeAllKeys(PROJECT_NAME);
    }

    @Test
    public void testGetApiKeys() throws Exception {
        ApiKeyService.ProjectApiKeys testing = apiKeyService.createApiKeys(PROJECT_NAME);
        assertEquals(ImmutableSet.copyOf(apiKeyService.getApiKeys(new int[]{testing.id})), ImmutableSet.of(testing));
    }



    @Test
    public void testCreateApiKeys() throws Exception {
        ApiKeyService.ProjectApiKeys testing = apiKeyService.createApiKeys(PROJECT_NAME);

        assertTrue(apiKeyService.checkPermission(PROJECT_NAME, AccessKeyType.READ_KEY, testing.readKey));
        assertTrue(apiKeyService.checkPermission(PROJECT_NAME, AccessKeyType.WRITE_KEY, testing.writeKey));
        assertTrue(apiKeyService.checkPermission(PROJECT_NAME, AccessKeyType.MASTER_KEY, testing.masterKey));

        assertFalse(apiKeyService.checkPermission(PROJECT_NAME, AccessKeyType.READ_KEY, "invalidKey"));
        assertFalse(apiKeyService.checkPermission(PROJECT_NAME, AccessKeyType.WRITE_KEY, "invalidKey"));
        assertFalse(apiKeyService.checkPermission(PROJECT_NAME, AccessKeyType.MASTER_KEY, "invalidKey"));
    }

    @Test
    public void testRevokeApiKeys() throws Exception {
        ApiKeyService.ProjectApiKeys testing = apiKeyService.createApiKeys(PROJECT_NAME);

        apiKeyService.revokeApiKeys(PROJECT_NAME, testing.id);

        assertFalse(apiKeyService.checkPermission(PROJECT_NAME, AccessKeyType.READ_KEY, testing.readKey));
        assertFalse(apiKeyService.checkPermission(PROJECT_NAME, AccessKeyType.WRITE_KEY, testing.writeKey));
        assertFalse(apiKeyService.checkPermission(PROJECT_NAME, AccessKeyType.MASTER_KEY, testing.masterKey));
    }
}
