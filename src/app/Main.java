package app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * @author dzimiks
 * Date: 18-03-2019 at 20:42
 */
public class Main {

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject json = new JsonObject();
        json.addProperty("number", 21);
        json.addProperty("boolean", true);
        json.addProperty("string", "text");
        String result = gson.toJson(json);
        System.out.println(result);
    }
}
