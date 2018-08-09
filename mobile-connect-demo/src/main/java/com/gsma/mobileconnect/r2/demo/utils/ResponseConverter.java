package com.gsma.mobileconnect.r2.demo.utils;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.web.MobileConnectWebResponse;

/**
 * Created by a.furs on 16.05.2018.
 */
public class ResponseConverter {
    private final String STATUS_SUCCESS = "success";
    private final String STATUS_FAILURE = "failure";
//    Map<String, String> dictionary = new HashMap<MobileConnectResponseType, String>();
//    private static Map<MobileConnectResponseType, String> _actionDict = new HashMap<MobileConnectResponseType, String>();
//    _actionDict.put
////    {
////        { MobileConnectResponseType.Authentication, "authentication" },
////        { MobileConnectResponseType.Complete, "complete" },
////        { MobileConnectResponseType.Error, "error" },
////        { MobileConnectResponseType.OperatorSelection, "operator_selection" },
////        { MobileConnectResponseType.StartAuthentication, "start_authentication" },
////        { MobileConnectResponseType.StartDiscovery, "discovery" },
////        { MobileConnectResponseType.UserInfo, "user_info" },
////        { MobileConnectResponseType.Identity, "identity" },
////        { MobileConnectResponseType.TokenRevoked, "token_revoked" },
////

    public static MobileConnectWebResponse Convert(MobileConnectStatus status) {
        MobileConnectWebResponse response = new MobileConnectWebResponse(status);
        return response;
    }
}
