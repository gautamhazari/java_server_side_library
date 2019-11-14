/*
 * SOFTWARE USE PERMISSION
 *
 * By downloading and accessing this software and associated documentation files ("Software") you are granted the
 * unrestricted right to deal in the Software, including, without limitation the right to use, copy, modify, publish,
 * sublicense and grant such rights to third parties, subject to the following conditions:
 *
 * The following copyright notice and this permission notice shall be included in all copies, modifications or
 * substantial portions of this Software: Copyright © 2016 GSM Association.
 *
 * THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. YOU AGREE TO
 * INDEMNIFY AND HOLD HARMLESS THE AUTHORS AND COPYRIGHT HOLDERS FROM AND AGAINST ANY SUCH LIABILITY.
 */
package com.gsma.mobileconnect.r2.utils;

import com.gsma.mobileconnect.r2.web.rest.RestResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

import java.net.URI;

/**
 * Utility methods used by tests.
 *
 * @since 2.0
 */
public final class TestUtils
{
    public static final RestResponse OPERATOR_SELECTION_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_ACCEPTED)
        .withContent(
            "{\"links\":[{\"rel\":\"operatorSelection\",\"href\":\"http://discovery.sandbox2.mobileconnect.io/v2/discovery/users/operator-selection?session_id=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyZWRpcmVjdFVybCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMS8iLCJhcHBsaWNhdGlvbiI6eyJleHRlcm5hbF9pZCI6IjExMzgiLCJuYW1lIjoiY3NoYXJwLXNkayIsImtleXMiOnsic2FuZGJveCI6eyJrZXkiOiI2Njc0MmE4NS0yMjgyLTQ3NDctODgxZC1lZDViN2JkNzRkMmQiLCJzZWNyZXQiOiJmMTUxOTlmNC1iNjU4LTRlNTgtOGJiMy1lNDA5OTg4NzMzOTIifX0sInJlZGlyZWN0X3VyaSI6Imh0dHBzOi8vbG9jYWxob3N0OjgwMDEvIiwiZGV2ZWxvcGVyIjp7InBvcnRhbF91c2VyX2lkIjoiMTEzOCIsIm5hbWUiOiJOaWNob2xhcyBEb25vaG9lIiwiZW1haWwiOiJuaWNob2xhcy5kb25vaG9lQGJqc3MuY29tIiwicHJvZmlsZSI6Imh0dHBzOi8vZGV2ZWxvcGVyLm1vYmlsZWNvbm5lY3QuaW8vYXBpL3YxL3VzZXI_ZW1haWw9bmljaG9sYXMuZG9ub2hvZSU0MGJqc3MuY29tIiwidXBkYXRlZCI6IjIwMTYtMDQtMjBUMDk6MzQ6MThaIiwibXNpc2RucyI6WyI5NDE0ZTI1MmMzYjE1ZWUzMGIyN2NmYmQxNjkzN2UwNWJlMGQ1NWYwZGZjZGQ0MjM2OTg3NTU1MjQ3ZjU0YzUyIiwiZjYwZjFkZDU1YzUxMjE3ZTAwMTc4YWE3ZGIxM2Q5Njc4OGUxZmM0MzRkMGU2ZGZiZmI2NjVlYjU5NzU3MGIwZiJdLCJtc2lzZG5TaG9ydCI6WyI3NTc1IiwiMzMzMyJdLCJzbXNBdXRoIjp0cnVlLCJtY2MiOiI5MDEiLCJtbmMiOiIwMSIsImNvbnNlbnQiOmZhbHNlfX0sInVzZXIiOnsibmFtZSI6IjY2NzQyYTg1LTIyODItNDc0Ny04ODFkLWVkNWI3YmQ3NGQyZCIsInBhc3MiOiJmMTUxOTlmNC1iNjU4LTRlNTgtOGJiMy1lNDA5OTg4NzMzOTIifSwiaWF0IjoxNDYxMTY5MzA5fQ.2Lp0Xt9JXVZxNbnNq_RH-5KJPQ06qw6ttR4ZK3fwcQU\"}]}")
        .build();

    public static final RestResponse DISCOVERY_REQUEST_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_OK)
        .withContent(
            "{\"ttl\":1461169322705,\"subscriber_id\":\"e06a09de399ae6c6798c2126e531775ddf3cfe00367af1842534be709fef25e199157c49cc44adf661d286a29afa09c017747fb4383db22b2eaf33db5f878b3ea261c8f342b234e998757e83de23f4a637ce2390453d5d578c76cd65aae99332ee7fbdbd4a140c99babc4e700eae6aa44d3e17ac050771c1fd784fef0214bf770cd0854ea6f4cff87b3ea1e4b25dccd1d340f00eb66c0f041f90596f5236c1017b2541606fff5165320fc4b3381ebfe1fdb848ab04fbedc550bc575ca385b44695a0a9917a368552ee9f8e2178553318a17c32284197631f74f293f30fe6c04f7a77115ec0d2e8ab2a522db88c60263ec1b690ca22540b916e8a9d2c3d820ec1\",\"response\":{\"serving_operator\":\"Example Operator A\",\"country\":\"US\",\"currency\":\"USD\",\"apis\":{\"operatorid\":{\"link\":[{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/authorize\",\"rel\":\"authorization\"},{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/accesstoken\",\"rel\":\"token\"},{\"href\":\"http://operator_a.sandbox2.mobileconnect.io/oidc/userinfo\",\"rel\":\"userinfo\"},{\"href\":\"openid profile email\",\"rel\":\"scope\"}]}},\"client_id\":\"66742a85-2282-4747-881d-ed5b7bd74d2d\",\"client_secret\":\"f15199f4-b658-4e58-8bb3-e40998873392\",\"subscriber_id\":\"e06a09de399ae6c6798c2126e531775ddf3cfe00367af1842534be709fef25e199157c49cc44adf661d286a29afa09c017747fb4383db22b2eaf33db5f878b3ea261c8f342b234e998757e83de23f4a637ce2390453d5d578c76cd65aae99332ee7fbdbd4a140c99babc4e700eae6aa44d3e17ac050771c1fd784fef0214bf770cd0854ea6f4cff87b3ea1e4b25dccd1d340f00eb66c0f041f90596f5236c1017b2541606fff5165320fc4b3381ebfe1fdb848ab04fbedc550bc575ca385b44695a0a9917a368552ee9f8e2178553318a17c32284197631f74f293f30fe6c04f7a77115ec0d2e8ab2a522db88c60263ec1b690ca22540b916e8a9d2c3d820ec1\"}}")
        .build();

    public static final RestResponse AUTHENTICATION_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_OK)
        .withContent(
            "{\"ttl\":1466082848000,\"response\":{\"client_id\":\"x-ZWRhNjU3OWI3MGIwYTRh\",\"client_secret\":\"x-NjQzZTBhZWM0YmQ4ZDQ5\",\"serving_operator\":\"demo_unitedkingdom\",\"country\":\"UnitedKingdom\",\"currency\":\"GBP\",\"apis\":{\"operatorid\":{\"link\":[{\"rel\":\"authorization\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/index.php/auth\"},{\"rel\":\"token\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/index.php/token\"},{\"rel\":\"userinfo\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/index.php/userinfo\"},{\"rel\":\"jwks\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/cert.jwk\"},{\"rel\":\"tokenrevoke\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/revoketoken\"},{\"rel\":\"applicationShortName\",\"href\":\"test1\"},{\"rel\":\"openid-configuration\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/discovery.php/openid-configuration\"}]}}},\"subscriber_id\":\"6c483ef529a86e5aa808f9cfdcb78ac3ec9f24aba27ea1a003476b0693751d89c3feacd3d2ff00c0e1e1cb683ff7de9ea87bdd775d4e79b7da5a4fbec509d918c1f804fdaf1fcaa9d1aae572bd19a12de7de2d695d004a3b2828be9b79e5f13a5c70a35adebedef138ab11440f8573fff53e59c8348caaf458716dbb53b4162d27737f290a8a759a4eab409af27685b3667659ce1f5b2194ab68953c0381126fc941eb0043c17647021d1e47a07cfde2e5e18c9e29ca01af1a8d2b3558d9853ffeed1cd9c8545e0d4c609db4ca318c02d10cddaf83bab927f81c4ca8bbb04da4dba273a4f76d3962e5a31a59f806067393823ae6702850726281352849209fe4\"}")
        .build();

    public static final RestResponse AUTHENTICATION_NO_URI_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_OK)
        .withContent(
            "{\"ttl\":1466082848000,\"response\":{\"client_id\":\"x-ZWRhNjU3OWI3MGIwYTRh\",\"client_secret\":\"x-NjQzZTBhZWM0YmQ4ZDQ5\",\"serving_operator\":\"demo_unitedkingdom\",\"country\":\"UnitedKingdom\",\"currency\":\"GBP\",\"apis\":{\"operatorid\":{\"link\":[{\"rel\":\"authorization\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/index.php/auth\"},{\"rel\":\"token\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/index.php/token\"},{\"rel\":\"jwks\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/cert.jwk\"},{\"rel\":\"applicationShortName\",\"href\":\"test1\"},{\"rel\":\"openid-configuration\",\"href\":\"https://reference.mobileconnect.io/mobileconnect/discovery.php/openid-configuration\"}]}}},\"subscriber_id\":\"6c483ef529a86e5aa808f9cfdcb78ac3ec9f24aba27ea1a003476b0693751d89c3feacd3d2ff00c0e1e1cb683ff7de9ea87bdd775d4e79b7da5a4fbec509d918c1f804fdaf1fcaa9d1aae572bd19a12de7de2d695d004a3b2828be9b79e5f13a5c70a35adebedef138ab11440f8573fff53e59c8348caaf458716dbb53b4162d27737f290a8a759a4eab409af27685b3667659ce1f5b2194ab68953c0381126fc941eb0043c17647021d1e47a07cfde2e5e18c9e29ca01af1a8d2b3558d9853ffeed1cd9c8545e0d4c609db4ca318c02d10cddaf83bab927f81c4ca8bbb04da4dba273a4f76d3962e5a31a59f806067393823ae6702850726281352849209fe4\"}")
        .build();

    public static final RestResponse NOT_FOUND_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_OK)
        .withContent("{\"error\":\"Not_Found_Entity\",\"description\":\"Operator Not Found\"}")
        .build();

    public static final RestResponse INVALID_CODE_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_BAD_REQUEST)
        .withContent(
            "{\"error\":\"invalid_grant\",\"error_description\":\"Authorization code doesn't exist or is invalid for the client\"}")
        .build();

    public static final RestResponse PROVIDER_METADATA_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_OK)
        .withContent(
            "{\"version\":\"3.0\",\"issuer\":\"https://reference.mobileconnect.io/mobileconnect\",\"authorization_endpoint\":\"https://reference.mobileconnect.io/mobileconnect/index.php/auth\",\"token_endpoint\":\"https://reference.mobileconnect.io/mobileconnect/index.php/token\",\"userinfo_endpoint\":\"https://reference.mobileconnect.io/mobileconnect/index.php/userinfo\",\"check_session_iframe\":\"https://reference.mobileconnect.io/mobileconnect/opframe.php\",\"end_session_endpoint\":\"https://reference.mobileconnect.io/mobileconnect/index.php/endsession\",\"jwks_uri\":\"https://reference.mobileconnect.io/mobileconnect/op.jwk\",\"scopes_supported\":[\"openid\",\"mc_authn\",\"mc_authz\",\"profile\",\"email\",\"address\"],\"response_types_supported\":[\"code\",\"code token\",\"code id_token\",\"token\",\"token id_token\",\"code token id_token\",\"id_token\"],\"grant_types_supported\":[\"authorization_code\"],\"acr_values_supported\":[\"2\",\"3\"],\"subject_types_supported\":[\"public\",\"pairwise\"],\"userinfo_signing_alg_values_supported\":[\"HS256\",\"HS384\",\"HS512\",\"RS256\",\"RS384\",\"RS512\"],\"userinfo_encryption_alg_values_supported\":[\"RSA1_5\",\"RSA-OAEP\"],\"userinfo_encryption_enc_values_supported\":[\"A128CBC-HS256\",\"A256CBC-HS512\",\"A128GCM\",\"A256GCM\"],\"id_token_signing_alg_values_supported\":[\"HS256\",\"HS384\",\"HS512\",\"RS256\",\"RS384\",\"RS512\"],\"id_token_encryption_alg_values_supported\":[\"RSA1_5\",\"RSA-OAEP\"],\"id_token_encryption_enc_values_supported\":[\"A128CBC-HS256\",\"A256CBC-HS512\",\"A128GCM\",\"A256GCM\"],\"request_object_signing_alg_values_supported\":[\"HS256\",\"HS384\",\"HS512\",\"RS256\",\"RS384\",\"RS512\"],\"request_object_encryption_alg_values_supported\":[\"RSA1_5\",\"RSA-OAEP\"],\"request_object_encryption_enc_values_supported\":[\"A128CBC-HS256\",\"A256CBC-HS512\",\"A128GCM\",\"A256GCM\"],\"token_endpoint_auth_methods_supported\":[\"client_secret_post\",\"client_secret_basic\",\"client_secret_jwt\",\"private_key_jwt\"],\"token_endpoint_auth_signing_alg_values_supported\":[\"HS256\",\"HS384\",\"HS512\",\"RS256\",\"RS384\",\"RS512\"],\"display_values_supported\":[\"page\"],\"claim_types_supported\":[\"normal\"],\"claims_supported\":[\"name\",\"given_name\",\"family_name\",\"middle_name\",\"nickname\",\"preferred_username\",\"profile\",\"picture\",\"website\",\"email\",\"email_verified\",\"gender\",\"birthdate\",\"zoneinfo\",\"locale\",\"phone_number\",\"phone_number_verified\",\"address\",\"updated_at\"],\"service_documentation\":\"https://reference.mobileconnect.io/mobileconnect/index.php/servicedocs\",\"claims_locales_supported\":[\"en-US\"],\"ui_locales_supported\":[\"en-US\"],\"require_request_uri_registration\":false,\"op_policy_uri\":\"https://reference.mobileconnect.io/mobileconnect/index.php/op_policy\",\"op_tos_uri\":\"https://reference.mobileconnect.io/mobileconnect/index.php/op_tos\",\"claims_parameter_supported\":true,\"request_parameter_supported\":true,\"request_uri_parameter_supported\":true,\"mobile_connect_version_supported\":[{\"openid\":\"mc_v1.1\"},{\"openid mc_authn\":\"mc_v1.2\"},{\"openid mc_authz\":\"mc_v1.2\"}],\"login_hint_methods_supported\":[\"MSISDN\",\"ENCRYPTED_MSISDN\",\"PCR\"]} ")
        .build();

    public static final RestResponse USERINFO_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_OK)
        .withContent(
            "{\"sub\":\"411421B0-38D6-6568-A53A-DF99691B7EB6\",\"email\":\"test2@example.com\",\"email_verified\":true}")
        .build();

    @SuppressWarnings("unused")
    public static final RestResponse UNAUTHORIZED_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_UNAUTHORIZED)
        .withHeaders(new KeyValuePair.ListBuilder()
            .add(HttpHeaders.WWW_AUTHENTICATE,
                "Bearer error=\"invalid_request\", error_description=\"No Access Token\"")
            .build())
        .build();

    public static final RestResponse TOKEN_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_ACCEPTED)
        .withContent(
            "{\"access_token\":\"966ad150-16c5-11e6-944f-43079d13e2f3\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"id_token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJub25jZSI6Ijc3YzE2M2VmZDkzYzQ4ZDFhNWY2NzdmNGNmNTUzOGE4Iiwic3ViIjoiY2M3OGEwMmNjM2ViNjBjOWVjNTJiYjljZDNhMTg5MTAiLCJhbXIiOlsiU0lNX1BJTiJdLCJhdXRoX3RpbWUiOjE0NjI4OTQ4NTcsImFjciI6IjIiLCJhenAiOiI2Njc0MmE4NS0yMjgyLTQ3NDctODgxZC1lZDViN2JkNzRkMmQiLCJpYXQiOjE0NjI4OTQ4NTYsImV4cCI6MTQ2Mjg5ODQ1NiwiYXVkIjpbIjY2NzQyYTg1LTIyODItNDc0Ny04ODFkLWVkNWI3YmQ3NGQyZCJdLCJpc3MiOiJodHRwOi8vb3BlcmF0b3JfYS5zYW5kYm94Mi5tb2JpbGVjb25uZWN0LmlvL29pZGMvYWNjZXNzdG9rZW4ifQ.lwXhpEp2WUTi0brKBosM8Uygnrdq6FnLqkZ0Bm53gXA\"}")
        .build();

    public static final RestResponse VALIDATED_TOKEN_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_ACCEPTED)
        .withContent(
            "{\"access_token\":\"966ad150-16c5-11e6-944f-43079d13e2f3\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"id_token\":\"eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek\"}")
        .build();

    public static final RestResponse INVALID_TOKEN_RESPONSE_ACCESS_TOKEN_MISSING
        = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_ACCEPTED)
        .withContent(
            "{\"token_type\":\"Bearer\",\"expires_in\":3600,\"id_token\":\"eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek\"}")
        .build();

    public static final RestResponse JWKS_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_ACCEPTED)
        .withContent(
            "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"n\":\"ALyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5\",\"e\":\"AQAB\",\"kid\":\"PHPOP-00\"}]}")
        .build();

    public static final RestResponse INVALID_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_OK)
        .withContent("{unparseable}")
        .withUri(URI.create("http://error"))
        .build();

    public static final RestResponse REVOKE_TOKEN_SUCCESS_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_OK)
        .withMethod(HttpUtils.HttpMethod.POST.name())
        .withContent("{\"result\": \"Token revoked\"}")
        .build();

    public static final RestResponse REVOKE_TOKEN_ERROR_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_BAD_REQUEST)
        .withMethod(HttpUtils.HttpMethod.POST.name())
        .withContent("{\"error\": \"unsupported_token_type\"}")
        .build();

    public static final RestResponse REVOKE_TOKEN_NON_ERROR_RESPONSE = new RestResponse.Builder()
        .withStatusCode(HttpStatus.SC_MULTIPLE_CHOICES)
        .withMethod(HttpUtils.HttpMethod.POST.name())
        .withContent("{\"error\": \"unsupported_token_type\"}")
        .build();

    private TestUtils()
    {
    }

    /**
     * Strip the enclosing curly braces, then split array
     *
     * @param json to split.
     * @return array of Strings.
     */
    public static String[] splitArray(final String json)
    {
        return json.substring(1, json.length() - 1).split(",");
    }
}
