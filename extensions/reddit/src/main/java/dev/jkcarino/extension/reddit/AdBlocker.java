package dev.jkcarino.extension.reddit;

import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public final class AdBlocker {

    private static final String APOLLO_OPERATION_HEADER = "X-APOLLO-OPERATION-NAME";
    private static final String PDP_COMMENTS_ADS_OP = "PdpCommentsAds";

    private static final String DATA = "data";
    private static final String ELEMENTS = "elements";
    private static final String EDGES = "edges";
    private static final String NODE = "node";
    private static final String AD_PAYLOAD = "adPayload";
    private static final String GROUP_RECOMMENDATION_CONTEXT = "groupRecommendationContext";
    private static final String GROUP_RECOMMENDATION_TYPE_ID = "typeIdentifier";

    private static final String SEARCH_RECOMMENDATION = "recommendation";
    private static final String SEARCH_TRENDING_QUERIES = "trendingQueries";

    private static final Set<String> feeds = Set.of(
        "homeV3",
        "popularV3",
        "allV3",
        "customFeedV3",
        "subredditV3"
    );

    private static final HashSet<String> blockedHosts = new HashSet<>(
        Set.of(
            "alb.reddit.com",
            "e.reddit.com",
            "w3-reporting.reddit.com",
            "api2.branch.io"
        )
    );

    private static boolean hasBlockedHosts(Request request) {
        String host = request.url().host();
        return blockedHosts.contains(host);
    }

    private static boolean hasBlockedHeaders(Request request) {
        String apolloOperationName = request.header(APOLLO_OPERATION_HEADER);
        return PDP_COMMENTS_ADS_OP.equals(apolloOperationName);
    }

    private static boolean isAd(JSONObject edge) {
        JSONObject node = edge.optJSONObject(NODE);
        return node != null && node.has(AD_PAYLOAD) && !node.isNull(AD_PAYLOAD);
    }

    private static boolean isRecommendationAd(JSONObject edge) throws JSONException {
        if (edge.has(NODE)) {
            JSONObject node = edge.getJSONObject(NODE);
            JSONObject groupRecommendationContext = node.optJSONObject(GROUP_RECOMMENDATION_CONTEXT);

            boolean isRecommendationTypeUnknown = false;

            if (groupRecommendationContext != null) {
                String typeIdentifier = groupRecommendationContext.optString(GROUP_RECOMMENDATION_TYPE_ID);
                isRecommendationTypeUnknown = typeIdentifier.equalsIgnoreCase("unknown");
            }

            return groupRecommendationContext != null && !isRecommendationTypeUnknown;
        }
        return false;
    }

    private static boolean isFromSearch(JSONObject data) throws JSONException {
        JSONObject recommendation = data.optJSONObject(SEARCH_RECOMMENDATION);
        return recommendation != null && recommendation.has(SEARCH_TRENDING_QUERIES);
    }

    private static void removeFeedAds(JSONObject data) throws JSONException {
        for (String feed : feeds) {
            if (data.has(feed)) {
                JSONObject feedV3 = data.getJSONObject(feed);

                if (feedV3.has(ELEMENTS)) {
                    JSONObject elements = feedV3.getJSONObject(ELEMENTS);

                    if (elements.has(EDGES)) {
                        JSONArray edges = elements.getJSONArray(EDGES);
                        JSONArray filteredEdges = new JSONArray();

                        for (int i = 0; i < edges.length(); i++) {
                            JSONObject edge = edges.getJSONObject(i);

                            if (!isAd(edge) && !isRecommendationAd(edge)) {
                                filteredEdges.put(edge);
                            }
                        }

                        elements.put(EDGES, filteredEdges);
                    }
                }

                break;
            }
        }
    }

    private static void removeSearchAds(JSONObject data) throws JSONException {
        JSONObject recommendation = data.getJSONObject(SEARCH_RECOMMENDATION);
        JSONObject trendingQueries = recommendation.getJSONObject(SEARCH_TRENDING_QUERIES);

        if (trendingQueries.has(EDGES)) {
            JSONArray edges = trendingQueries.getJSONArray(EDGES);
            JSONArray filteredEdges = new JSONArray();

            for (int i = 0; i < edges.length(); i++) {
                JSONObject edge = edges.getJSONObject(i);

                if (edge.has(NODE)) {
                    JSONObject node = edge.getJSONObject(NODE);
                    boolean isPromoted = node.optBoolean("isPromoted", false);

                    if (!isPromoted) {
                        filteredEdges.put(edge);
                    }
                }
            }

            trendingQueries.put(EDGES, filteredEdges);
        }
    }

    public static boolean isRequestBlocked(Request request) {
        return hasBlockedHosts(request) || hasBlockedHeaders(request);
    }

    public static String removeAds(String jsonString) {
        try {
            JSONObject responseJson = new JSONObject(jsonString);
            JSONObject data = responseJson.optJSONObject(DATA);

            if (data != null) {
                if (isFromSearch(data)) {
                    removeSearchAds(data);
                } else {
                    removeFeedAds(data);
                }
            }
            return responseJson.toString();
        } catch (JSONException error) {
            return jsonString;
        }
    }
}
