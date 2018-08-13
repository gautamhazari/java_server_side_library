package com.gsma.mobileconnect.r2.authentication;

import com.gsma.mobileconnect.r2.claims.ClaimsParameter;
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

/**
 * @since 2.0
 */
public class AuthenticationOptionsTest
{
    private AuthenticationOptions authenticationOptions;

    private String clientId = "clientId";
    private URI redirectUrl = null;
    private String nonce = "nonce";
    private String state = "state";
    private String prompt = "mobile";
    private String uiLocales = "uiLocales";
    private String claimsLocales = "claimsLocales";
    private String idTokenHint = "idTokenHint";
    private String loginHint = "loginHint";
    private String dbts = "dbts";
    private String clientName = "clientName";
    private String context = "context";
    private String bindingMessage = "bindingMessage";
    private String claimsJson = "claimsJson";
    private ClaimsParameter claims = null;

    @BeforeMethod
    public void setUp() throws Exception
    {
        authenticationOptions = new AuthenticationOptions.Builder()
            .withClientId(clientId)
            .withClientName(clientName)
            .withRedirectUrl(redirectUrl)
            .withNonce(nonce)
            .withState(state)
            .withPrompt(prompt)
            .withUiLocales(uiLocales)
            .withClaimsLocales(claimsLocales)
            .withIdTokenHint(idTokenHint)
            .withLoginHint(loginHint)
            .withDbts(dbts)
            .withContext(context)
            .withBindingMessage(bindingMessage)
            .withClaimsJson(claimsJson)
            .withClaims(claims)
            .build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        authenticationOptions = null;
    }

    @Test
    public void testGetClientId() throws Exception
    {
        assertEquals(authenticationOptions.getClientId(), clientId);
    }

    @Test
    public void testGetRedirectUrl() throws Exception
    {
        assertEquals(authenticationOptions.getRedirectUrl(), redirectUrl);
    }

    @Test
    public void testGetAcrValues() throws Exception
    {
        assertEquals(authenticationOptions.getAcrValues(),
            DefaultOptions.AUTHENTICATION_ACR_VALUES);
    }

    @Test
    public void testGetScope() throws Exception
    {
        assertEquals(authenticationOptions.getScope(), DefaultOptions.AUTHENTICATION_SCOPE);
    }

    @Test
    public void testGetNonce() throws Exception
    {
        assertEquals(authenticationOptions.getNonce(), nonce);
    }

    @Test
    public void testGetState() throws Exception
    {
        assertEquals(authenticationOptions.getState(), state);
    }

    @Test
    public void testGetMaxAge() throws Exception
    {
        assertEquals(authenticationOptions.getMaxAge(), DefaultOptions.AUTHENTICATION_MAX_AGE);
    }

    @Test
    public void testGetDisplay() throws Exception
    {
        assertEquals(authenticationOptions.getDisplay(), DefaultOptions.DISPLAY);
    }

    @Test
    public void testGetPrompt() throws Exception
    {
        assertEquals(authenticationOptions.getPrompt(), prompt);
    }

    @Test
    public void testGetUiLocales() throws Exception
    {
        assertEquals(authenticationOptions.getUiLocales(), uiLocales);
    }

    @Test
    public void testGetClaimsLocales() throws Exception
    {
        assertEquals(authenticationOptions.getClaimsLocales(), claimsLocales);
    }

    @Test
    public void testGetIdTokenHint() throws Exception
    {
        assertEquals(authenticationOptions.getIdTokenHint(), idTokenHint);
    }

    @Test
    public void testGetLoginHint() throws Exception
    {
        assertEquals(authenticationOptions.getLoginHint(), loginHint);
    }

    @Test
    public void testGetDbts() throws Exception
    {
        assertEquals(authenticationOptions.getDbts(), dbts);
    }

    @Test
    public void testGetClientName() throws Exception
    {
        assertEquals(authenticationOptions.getClientName(), clientName);
    }

    @Test
    public void testGetContext() throws Exception
    {
        assertEquals(authenticationOptions.getContext(), context);
    }

    @Test
    public void testGetBindingMessage() throws Exception
    {
        assertEquals(authenticationOptions.getBindingMessage(), bindingMessage);
    }

    @Test
    public void testGetClaimsJson() throws Exception
    {
        assertEquals(authenticationOptions.getClaimsJson(), claimsJson);
    }

    @Test
    public void testGetClaims() throws Exception
    {
        assertEquals(authenticationOptions.getClaims(), claims);
    }

    @Test
    public void builderObjectShouldBuildAuthenticationOptions() throws URISyntaxException
    {
        final String acrValues = "2";
        final String bindingMessage = "bindingMessage";
        final ClaimsParameter claimsParameter = new ClaimsParameter.Builder().build();
        final String claimsJson = "{\"k\":\"v\"}";
        final String claimsLocales = "claimsLocale";
        final String clientId = "clientId";
        final String clientName = "clientName";
        final String context = "context";
        final String dbts = "dbts";
        final String display = "display";
        final String idTokenHint = "idTokenHint";
        final String loginHint = "loginHint";
        final int maxAge = 0;
        final String nonce = "nonce";
        final String prompt = "prompt";
        final URI redirectUrl = new URI("uri");
        final String scope = "scope";
        final String state = "state";
        final String uiLocales = "uiLocales";

        final AuthenticationOptions authenticationOptions = new AuthenticationOptions.Builder()
            .withAcrValues(acrValues)
            .withBindingMessage(bindingMessage)
            .withClaims(claimsParameter)
            .withClaimsJson(claimsJson)
            .withClaimsLocales(claimsLocales)
            .withClientId(clientId)
            .withClientName(clientName)
            .withContext(context)
            .withDbts(dbts)
            .withDisplay(display)
            .withIdTokenHint(idTokenHint)
            .withLoginHint(loginHint)
            .withMaxAge(maxAge)
            .withNonce(nonce)
            .withPrompt(prompt)
            .withRedirectUrl(redirectUrl)
            .withScope(scope)
            .withState(state)
            .withUiLocales(uiLocales)
            .build();

        assertEquals(authenticationOptions.getAcrValues(), acrValues);
        assertEquals(authenticationOptions.getBindingMessage(), bindingMessage);
        assertEquals(authenticationOptions.getClaims(), claimsParameter);
        assertEquals(authenticationOptions.getClaimsJson(), claimsJson);
        assertEquals(authenticationOptions.getClaimsLocales(), claimsLocales);
        assertEquals(authenticationOptions.getClientId(), clientId);
        assertEquals(authenticationOptions.getClientName(), clientName);
        assertEquals(authenticationOptions.getContext(), context);
        assertEquals(authenticationOptions.getDbts(), dbts);
        assertEquals(authenticationOptions.getDisplay(), display);
        assertEquals(authenticationOptions.getIdTokenHint(), idTokenHint);
        assertEquals(authenticationOptions.getLoginHint(), loginHint);
        assertEquals(authenticationOptions.getMaxAge(), maxAge);
        assertEquals(authenticationOptions.getPrompt(), prompt);
        assertEquals(authenticationOptions.getRedirectUrl(), redirectUrl);
        assertEquals(authenticationOptions.getScope(), scope);
        assertEquals(authenticationOptions.getState(), state);
        assertEquals(authenticationOptions.getUiLocales(), uiLocales);
    }
}