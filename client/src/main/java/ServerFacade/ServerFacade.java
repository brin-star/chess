package ServerFacade;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static javax.management.remote.JMXConnectorFactory.connect;

public class ServerFacade {
    private String serverURL = "http://localhost:8080";

    private <T> T makeRequest(String method, String endpoint, Object requestBody, String authToken, Class<T> responseClass) throws Exception {
        var url = new URL(serverURL + endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);

        if (requestBody != null) {
            con.connect();
        }
        else {
            con.connect();

        }
        OutputStream os = con.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

        osw.write(msg.toString());
        osw.flush();
        osw.close();

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Request failed with code " + responseCode);
        }
    }
}
