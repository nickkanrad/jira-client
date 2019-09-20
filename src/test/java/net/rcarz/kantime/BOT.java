package net.rcarz.kantime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class BOT {

  public void sendPost(String ChatRoomUrl, String message) throws IOException {

    final HttpClient client = new DefaultHttpClient();
    final HttpPost request = new HttpPost(ChatRoomUrl);
    // FROM HERE

    request.addHeader("Content-Type", "application/json; charset=UTF-8");

    final StringEntity params = new StringEntity("{\"text\":\" " + message + "\"}",
        ContentType.APPLICATION_FORM_URLENCODED);
    request.setEntity(params);

    // TO HERE
    final HttpResponse response = client.execute(request);

    final BufferedReader rd =
        new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

    StringBuffer result = new StringBuffer();
    String line;
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }

    System.out.println(result.toString());
  }

}
