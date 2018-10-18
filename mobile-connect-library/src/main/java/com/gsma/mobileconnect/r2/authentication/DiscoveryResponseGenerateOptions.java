package com.gsma.mobileconnect.r2.authentication;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gsma.mobileconnect.r2.utils.IBuilder;

import java.util.List;

public class DiscoveryResponseGenerateOptions {
    private String clientSecret;
    private String subscriptionId;
    private String clientApplicationName;
    private String clientKey;
    private List<String> linksList;
    private List<String> rel;

    private DiscoveryResponseGenerateOptions(final BuilderResponse builder) {
        this.clientSecret = builder.clientSecret;
        this.clientKey = builder.clientKey;
        this.subscriptionId = builder.subscriptionId;
        this.clientApplicationName = builder.clientApplicationName;
        this.linksList = builder.linksList;
        this.rel = builder.rel;
    }

    ObjectNode responseToJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode subscriberId = mapper.createObjectNode();
        ObjectNode response = mapper.createObjectNode();
        ObjectNode apis = mapper.createObjectNode();
        ObjectNode operatorId = mapper.createObjectNode();
        ArrayNode linkArray = mapper.createArrayNode();

        for (int i = 0; i < linksList.size(); i++) {
            ObjectNode link = mapper.createObjectNode();
            link.put("href", linksList.get(i));
            link.put("rel", rel.get(i));
            linkArray.add(link);
        }

        subscriberId.put("subscriber_id", subscriptionId);
        subscriberId.putPOJO("response", response);
        response.put("client_secret", clientSecret);
        response.put("client_name", clientApplicationName);
        response.put("client_id", clientKey);
        response.putPOJO("apis", apis);
        apis.putPOJO("operatorid", operatorId);
        operatorId.putPOJO("link", linkArray);

        return subscriberId;
    }

    public String getId() {
        return subscriptionId;
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
        private String subscriptionId;
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
                this.subscriptionId = options.getId();
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

        public BuilderResponse withSubscriberId(String val)
        {
            this.subscriptionId = val;
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
