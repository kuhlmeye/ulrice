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
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.net.TrustAllTrustManager;

/**
 * Task for the authentication at a tivoli access manager
 * 
 * @author christof
 */
public class TamLogin extends AbstractTask {

    /** The logger used by this class. */
    private static final Logger LOG = Logger.getLogger(TamLogin.class.getName());

    public static final String URL_ATTRIBUTE = "url";

    @Override
    public boolean doTask(ProcessThread thread) {
        String urlString = getParameterAsString(URL_ATTRIBUTE);
        String loginType = "pwd";
        String userId = thread.getContext().getUserId();
        String password = thread.getContext().getPassword();

        // TODO Complete cookie handling (see rfc)

        if (urlString == null) {
            thread.handleError(this, "No Url specified.", "TAM Url was not specified. Please specify a TAM url in the application description file.");
            return false;
        }
        if (userId == null) {
            thread.handleError(this, "No UserId specified.", "No user id was given. Please enter a UserId in the user id field.");
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
            sc.init(null, new TrustManager[] { new TrustAllTrustManager() }, new java.security.SecureRandom());
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
            try {
                os.write(content);
                os.flush();
            }
            finally {
                os.close();
            }

            StringBuffer strRespBuff = new StringBuffer();
            Reader responseReader = new InputStreamReader(con.getInputStream());
            try {
                char[] responseBuffer = new char[1024];
                while (true) {
                    int responseLen = responseReader.read(responseBuffer);
                    strRespBuff.append(responseBuffer);
                    if (responseLen < responseBuffer.length) {
                        break;
                    }
                }
            }
            finally {
                responseReader.close();
            }

            String tamResponseContent = getTamResponseValue(strRespBuff.toString());
            if (tamResponseContent != null) {
                if ("Login successful".equalsIgnoreCase(tamResponseContent)) {
                    // Login was successful.
                    thread.fireTaskProgressed(this, 100, "Login successful", "Successfully logged in.");
                    List<String> cookieStrList = con.getHeaderFields().get("Set-Cookie");
                    if (cookieStrList != null) {
                        for (String cookieString : cookieStrList) {

                            StringTokenizer tok = new StringTokenizer(cookieString, ";");
                            while (tok.hasMoreTokens()) {
                                String cookiePart = tok.nextToken();
                                String[] cookieParts = cookiePart.split("=");
                                String cookieKey = cookieParts[0].trim();
                                String cookieValue = null;
                                if (cookieParts.length > 1) {
                                    cookieValue = cookieParts[1].trim();
                                }
                                if (!"expires".equalsIgnoreCase(cookieKey) && !"domain".equalsIgnoreCase(cookieKey) && !"path".equalsIgnoreCase(cookieKey)
                                    && !"secure".equalsIgnoreCase(cookieKey)) {
                                    thread.getContext().getCookieMap().put(cookieKey, cookieValue);
                                }
                            }
                        }
                    }

                    return true;
                }
                else if ("".equals(tamResponseContent)) {
                    if ((password == null) || "".equals(password)) {
                        thread.handleError(this, "No password specified.", "No password was specified. Please enter your password.");
                    }
                    else {
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
                        // The user's account has expired.
                        thread.handleError(this, "Your account has expired.", tamResponseContent);
                        return false;
                    }
                    else if ("HPDIA0199W".equalsIgnoreCase(errorCode)) {
                        // The user's account has expired.
                        thread.handleError(this, "Authentication mechanism not available.", tamResponseContent);
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

        }
        catch (MalformedURLException e) {
            thread.handleError(this, "TAM Login failed.", "URL is malformed. " + e.getMessage());
            LOG.log(Level.SEVERE, "TAM Login failed.", e);
            return false;
        }
        catch (UnsupportedEncodingException e) {
            thread.handleError(this, "TAM Login failed.", "Encoding is not supported.. " + e.getMessage());
            LOG.log(Level.SEVERE, "TAM Login failed.", e);
            return false;
        }
        catch (IOException e) {
            thread.handleError(this, "TAM Login failed.", "IO exception occurred. " + e.getMessage());
            LOG.log(Level.SEVERE, "TAM Login failed.", e);
            return false;
        }
        catch (NoSuchAlgorithmException e) {
            thread.handleError(this, "TAM Login failed.", "Could not found algorithm. " + e.getMessage());
            LOG.log(Level.SEVERE, "TAM Login failed.", e);
            return false;
        }
        catch (KeyManagementException e) {
            thread.handleError(this, "TAM Login failed.", "Key management error. " + e.getMessage());
            LOG.log(Level.SEVERE, "TAM Login failed.", e);
            return false;
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
