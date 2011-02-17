package net.ulrice.webstarter.tasks;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import net.ulrice.webstarter.ProcessContext;
import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.net.TrustAllTrustManager;

public class TamLogin extends AbstractTask {

	public static final String URL_ATTRIBUTE = "url";

	@Override
	public boolean doTask(ProcessThread thread) {
		String urlString = getParameterAsString(URL_ATTRIBUTE);
		String loginType = thread.getContext().getValueAsString(ProcessContext.LOGIN_TYPE, "pwd");
		String userId = thread.getContext().getValueAsString(ProcessContext.USERID);
		String password = thread.getContext().getValueAsString(ProcessContext.PASSWORD);

		if (urlString == null || userId == null) {
			return false;
		}

		try {

			StringBuffer buffer = new StringBuffer();
			buffer.append("username=").append(URLEncoder.encode(userId, "UTF-8"));
			if (password != null) {
				buffer.append("&password=").append(URLEncoder.encode(password, "UTF-8"));
			}
			buffer.append("&login-form-type=").append(loginType);
			
		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, new TrustManager[]{new TrustAllTrustManager()}, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			
			
			URL url = new URL(urlString);

        	thread.fireTaskProgressed(this, 50, "Connecting to TAM", "Connect to TAM at " + url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        HttpURLConnection.setFollowRedirects(false);
	        con.setDoInput(true);
	        con.setDoOutput(true);
	        con.setRequestProperty("Content-type", "text/html");
	        con.setRequestMethod("POST");
	        con.setAllowUserInteraction(false);
	        con.setUseCaches(false);

	        byte[] content = buffer.toString().getBytes();
	        int len = content.length;
	        con.setRequestProperty("Content-length", Integer.toString(len));
	        // send content
	        OutputStream os = con.getOutputStream();
	        os.write(content);
	        os.flush();
	        os.close();

			Reader responseReader = new InputStreamReader(con.getInputStream());
			char[] responseBuffer = new char[1024];
			StringBuffer strRespBuff = new StringBuffer();
			while (true) {
				int responseLen = responseReader.read(responseBuffer);
				strRespBuff.append(responseBuffer);
				if (responseLen < responseBuffer.length) {
					break;
				}
			}

			String tamResponseContent = getTamResponseValue(strRespBuff.toString());
	        if (tamResponseContent != null) {
	            if ("Login successful".equalsIgnoreCase(tamResponseContent)) {
	                // Login was successful.	    	        
	            	thread.fireTaskProgressed(this, 100, "Login successful", "Successfully logged in.");
	                String cookieString = con.getHeaderField("Set-Cookie");
	                thread.getContext().setValue(ProcessContext.COOKIE, cookieString);
	                return true;
	            }
	            else if ("".equals(tamResponseContent)) {
	            	if(password == null || "".equals(password)) {
		            	thread.handleError(this, "No password specified.", "No password was specified. Please enter your password.");	            		
	            	} else {
		            	thread.handleError(this, "Unknown error", "Unknown error during TAM Login");
	            	}
	            	
	            	return false;
	            }
	            else {
	                // And error message occurred.
	                String errorCode = tamResponseContent.substring(0, 10);
	                if ("HPDIA0200W".equalsIgnoreCase(errorCode)) {
		            	thread.handleError(this, "Wrong UserId/Password", tamResponseContent);
		            	return false;
	                }
	                else if ("HPDIA0205W".equalsIgnoreCase(errorCode)) {
	                    // The user’s account has expired.
		            	thread.handleError(this, "Your account has expired.", tamResponseContent);
		            	return false;
	                }
	                else if ("HPDIA0204W".equalsIgnoreCase(errorCode)) {
	                    // The user's password has expired.
	                    // TODO Handle password changed exception.
		            	thread.handleError(this, "Your password has expired.", tamResponseContent);
		            	return false;
	                }
	                else {
		            	thread.handleError(this, "Unknown error during login", tamResponseContent);
		            	return false;
	                }
	            }
	        }
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private String getTamResponseValue(String response) {
        int startIdx = response.indexOf("name=\"idp_error\"");
        int endIdx = -1;

        if (startIdx >= 0) {
            endIdx = response.indexOf(">", startIdx);

            String tamResponse = response.substring(startIdx, endIdx);

            int cntStartIdx = tamResponse.indexOf("content=\"");
            if (cntStartIdx >= 0) {
                cntStartIdx += "content=\"".length();
                int cntEndIdx = tamResponse.indexOf("\"", cntStartIdx);
                if (cntEndIdx >= 0) {
                    return tamResponse.substring(cntStartIdx, cntEndIdx);
                }
            }
        }
        return null;
	}


}
