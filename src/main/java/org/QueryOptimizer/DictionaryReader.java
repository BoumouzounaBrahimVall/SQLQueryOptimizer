package org.QueryOptimizer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class DictionaryReader {
    public static void main(String[] args) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonData = (JSONObject) parser.parse(new FileReader("C:\\Users\\S USER\\Documents\\JEE projects\\SQLQueryOptimizer\\src\\main\\java\\org\\QueryOptimizer\\dictionary.json"));
        JSONArray jsonArray = (JSONArray) jsonData.get("tables");
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            String value1 = (String) jsonObject.get("lineSize");
            System.out.println(value1);
           // int value2 = Integer.parseInt(jsonObject.get("propertyName2").toString());
            // Do something with the values
        }

    }
}
