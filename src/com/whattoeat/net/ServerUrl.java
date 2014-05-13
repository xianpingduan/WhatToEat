package com.whattoeat.net;

public final class ServerUrl
{

    private static final int DEBUG_SERVER = 1001;
    private static final int PUBLIC_SERVER = 1002;

    private static final String PUBLIC_URL = "http://food.ixiuta.com";
    private static final String DEBUG_URL = "http://food.ixiuta.com";
    /**
     * 帐号支付中心URL
     */

    private static int CONNECT_TO = PUBLIC_SERVER;

    public static String getServerUrlApp()
    {
	if( CONNECT_TO == PUBLIC_SERVER )
	{
	    return PUBLIC_URL;
	}
	else
	{
	    return DEBUG_URL;
	}
    }
}
