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
package com.gsma.mobileconnect.r2.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gsma.mobileconnect.r2.MobileConnect;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
import com.gsma.mobileconnect.r2.cache.ConcurrentCache;
import com.gsma.mobileconnect.r2.demo.objects.OperatorParameters;
import com.gsma.mobileconnect.r2.demo.utils.Constants;
import com.gsma.mobileconnect.r2.demo.utils.ReadAndParseFiles;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

/**
 * @since 2.0
 */
@SpringBootConfiguration
public class DemoAppConfiguration
{

    private static final String PATH_TO_CONFIG_FOLDER = File.separator + "mobile-connect-demo" + File.separator + "src" + File.separator
            + "main" + File.separator + "resources" + File.separator + "public" + File.separator + "data"
            + File.separator;

    private static String userDir = System.getProperty("user.dir").replace("target", "");
    private static final String DEMO_APP_CONFIG = "defaultData.json";
    private static final String WITHOUT_DISCOVERY_APP_CONFIG = "defaultDataWD.json";
    private static final String INDIAN_DEMO_APP_CONFIG = "defaultDataWD.json";

    private String configFilePath =  userDir + PATH_TO_CONFIG_FOLDER + DEMO_APP_CONFIG;
    private String configFilePathWD =  userDir + PATH_TO_CONFIG_FOLDER + WITHOUT_DISCOVERY_APP_CONFIG;

    @Bean
    public MobileConnectConfig mobileConnectConfig() throws IOException, ParseException {
        JSONObject config = (JSONObject)new JSONParser().parse(new FileReader(configFilePath));
        return new MobileConnectConfig.Builder()
                .withClientId(config.get("clientID").toString())
                .withClientSecret(config.get("clientSecret").toString())
                .withDiscoveryUrl(URI.create(config.get("discoveryURL").toString()))
                .withRedirectUrl(URI.create(config.get("redirectURL").toString()))
                .withXRedirect(config.get("xRedirect").toString())
                .withIncludeRequestIP(config.get("includeRequestIP").toString().equals("True"))
                .build();
    }

    @Bean
    public OperatorUrls operatorUrlsWithProviderMetadata() throws IOException, ParseException {
        JSONObject config = (JSONObject)new JSONParser().parse(new FileReader(configFilePathWD));
        return new OperatorUrls.Builder().withProviderMetadataUri(config.get("metadataURL").toString()).build();
    }

    @Bean
    public MobileConnectWebInterface mobileConnectWebInterface(
        @Autowired final MobileConnectConfig config)
    {
        OperatorParameters operatorParams = ReadAndParseFiles.ReadFile(Constants.ConfigFilePath);
        return MobileConnect.buildWebInterface(config, new DefaultEncodeDecoder(),
                new ConcurrentCache.Builder().withJsonService(new JacksonJsonService()).withMaxCacheSize(operatorParams.getMaxDiscoveryCacheSize()).build(),
                new ConcurrentCache.Builder().withJsonService(new JacksonJsonService()).withMaxCacheSize(operatorParams.getMaxDiscoveryCacheSize()).build());
    }

    @Bean
    public ObjectMapper objectMapper()
    {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setPropertyNamingStrategy(
            PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(
        @Autowired final ObjectMapper objectMapper)
    {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
