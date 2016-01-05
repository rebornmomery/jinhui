package com.android.shortvideo.common.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 协助工具
 */
public class JDHttpClientTool {

	
    /**
     * 转换Stream成String
     */
	public static String convertStreamToString(InputStream is)
    {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
		} 
        catch (IOException e)
        {
			e.printStackTrace();

		}
		return sb.toString();
	}
	
	
	
}
