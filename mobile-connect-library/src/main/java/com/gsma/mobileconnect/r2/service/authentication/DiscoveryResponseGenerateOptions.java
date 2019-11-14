package com.gsma.mobileconnect.r2.service.authentication;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gsma.mobileconnect.r2.model.constants.Parameters;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoveryResponseGenerateOptions {
    private String clientSecret;
    private String clientApplicationName;
    private String clientKey;
    private List<String> linksList;
    private List<String> rel;

    private DiscoveryResponseGenerateOptions(final BuilderResponse builder) {
        this.clientSecret = builder.clientSecret;
        this.clientKey = builder.clientKey;
        this.clientApplicationName = builder.clientApplicationName;
        this.linksList = builder.linksList;
        this.rel = builder.rel;
    }

    String responseToJson() {

        Gson gson = new Gson();

        List<Map> links = new ArrayList<>();
        for (int i = 0; i < linksList.size(); i++) {
            Map<String, Object> linkMap = new HashMap<>();
            linkMap.put("href", linksList.get(i));
            linkMap.put("rel", rel.get(i));
            links.add(linkMap);
        }

        JsonObject clientSecret = new JsonObject();
        clientSecret.addProperty(Parameters.CLIENT_SECRET, this.clientSecret);
        JsonObject clientName = new JsonObject();
        clientName.addProperty(Parameters.CLIENT_NAME, clientApplicationName);
        JsonObject clientId = new JsonObject();
        clientId.addProperty(Parameters.CLIENT_ID, clientKey);
        JsonObject link = new JsonObject();
        link.add(Parameters.LINK, gson.toJsonTree(links, new TypeToken<List<String>>(){}.getType()));
        JsonObject operatorId = new JsonObject();
        operatorId.add(Parameters.OPERATORID, link);
        JsonObject apis = new JsonObject();
        apis.add(Parameters.APIS, operatorId);
        JsonObject response = new JsonObject();

        Map<String, Object> responseMap = new HashMap();
        responseMap.put(Parameters.APIS, operatorId);
        responseMap.put(Parameters.CLIENT_ID, clientKey);
        responseMap.put(Parameters.CLIENT_NAME, clientApplicationName);
        responseMap.put(Parameters.CLIENT_SECRET, this.clientSecret);
        response.add(Parameters.RESPONSE, gson.toJsonTree(responseMap, new TypeToken<HashMap<String, Object>>(){}.getType()));

        return gson.toJson(response);
    }

    public String getSecret() {
        return clientSecret;
    }

    public String getName() {
        return clientApplicationName;
    }

    public String getClientKey() {
        return clientKey;
    }

    public List<String> getLinksList() {
        return linksList;
    }

    public List<String> getRel() {
        return rel;
    }

    public static final class BuilderResponse implements IBuilder<DiscoveryResponseGenerateOptions>
    {
        private String clientSecret;
        private String clientApplicationName;
        private String clientKey;
        private List<String> linksList;
        private List<String> rel;

        public BuilderResponse()
        {
            // default constructor
        }

        public BuilderResponse(final DiscoveryResponseGenerateOptions options)
        {
            if (options != null)
            {
                this.clientSecret = options.getSecret();
                this.clientKey = options.getClientKey();
                this.clientApplicationName = options.getName();
                this.linksList = options.getLinksList();
                this.rel = options.getRel();
            }
        }

        public BuilderResponse withSecretKey(String val)
        {
            this.clientSecret = val;
            return this;
        }

        public BuilderResponse withClientKey(String val)
        {
            this.clientKey = val;
            return this;
        }

        public BuilderResponse withName(String val) {
            this.clientApplicationName = val;
            return this;
        }

        public BuilderResponse withLinks(List<String> val)
        {
            this.linksList = val;
            return this;
        }

        public BuilderResponse withRel(List<String> val)
        {
            this.rel = val;
            return this;
        }

        @Override
        public DiscoveryResponseGenerateOptions build()
        {
            return new DiscoveryResponseGenerateOptions(this);
        }
    }
}
