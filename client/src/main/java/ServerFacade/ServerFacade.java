package ServerFacade;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacade {
    private String serverURL = "http://localhost:8080";

    private <T> T makeRequest(String method, String endpoint, Object requestBody, String authToken, Class<T> responseClass) throws Exception {
        var url = new URL(serverURL + endpoint);
        var gson = new Gson();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");

        if (authToken != null) {
            con.setRequestProperty("Authorization", authToken);
        }

        if (requestBody != null) {
            con.setDoOutput(true);

            String json = gson.toJson(requestBody);

            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

            osw.write(json);
            osw.flush();
            osw.close();
        }

        con.connect();

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new Exception(con.getErrorStream().toString());
        }

        InputStream is = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);

        T result = gson.fromJson(isr, responseClass);
        return result;
    }
}
