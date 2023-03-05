package org.QueryOptimizer.dictionnary;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DictionaryReader {
    private static String DIC_PATH="src/main/java/org/QueryOptimizer/dictionnary/dictionary.json";
    public DictionaryReader(){
        parser= new JSONParser();
    }
    private final JSONParser parser;
    public double getTransTime()  {
        double transTime;
        try {
            JSONObject jsonData = (JSONObject) this.parser.parse(new FileReader(DIC_PATH));
            transTime=Double.parseDouble(jsonData.get("transTime").toString());
        }catch(Exception e){
            System.out.println("error reading ");
            return 0;
        }
        return transTime;
    }
    public double getTpd()  { // tempsPosDebut
        double tpd;
        try {
            JSONObject jsonData = (JSONObject) this.parser.parse(new FileReader(DIC_PATH));
            tpd=Double.parseDouble(jsonData.get("tpd").toString());
        }catch(Exception e){
            System.out.println("error reading ");
            return 0;
        }
        return tpd;
    }
    public int getNbrPos()  {
        int nbrPos;
        try {
            JSONObject jsonData = (JSONObject) this.parser.parse(new FileReader(DIC_PATH));
            nbrPos=Integer.parseInt(jsonData.get("nbrPos").toString());
        }catch(Exception e){
            System.out.println("error reading ");
            return 0;
        }
        return nbrPos;
    }
    public int getLineSize(String tableName)  {
        int lineSize = 0;
        try {
            JSONObject jsonData = (JSONObject) this.parser.parse(new FileReader(DIC_PATH));
            JSONArray tables = (JSONArray) jsonData.get("tables");
            for (Object object : tables) {
                JSONObject table = (JSONObject) object;
                String tableNm = (String) table.get("tableName");
                if(tableNm.equals(tableName))
                {
                    lineSize=Integer.parseInt(table.get("lineSize").toString());
                }
            }
        }catch(Exception e){
            System.out.println("error reading ");
        }
        return lineSize;
    }

    public int getLineCount(String tableName)  {
        int lineCount = 0;
        try {
            JSONObject jsonData = (JSONObject) this.parser.parse(new FileReader(DIC_PATH));
            JSONArray tables = (JSONArray) jsonData.get("tables");
            for (Object object : tables) {
                JSONObject table = (JSONObject) object;
                String tableNm = (String) table.get("tableName");
                if(tableNm.equals(tableName))
                {
                    lineCount=Integer.parseInt(table.get("lineCount").toString());
                    return lineCount;
                }
            }
        }catch(Exception e){
            System.out.println("error reading LineCount ");
        }
        return lineCount;
    }

    public int getFB(String tableName)  {
        int FB = 0;
        try {
            JSONObject jsonData = (JSONObject) this.parser.parse(new FileReader(DIC_PATH));
            JSONArray tables = (JSONArray) jsonData.get("tables");
            for (Object object : tables) {
                JSONObject table = (JSONObject) object;
                String tableNm = (String) table.get("tableName");
                if(tableNm.equals(tableName))
                {
                    FB=Integer.parseInt(table.get("FB").toString());
                    return FB;
                }
            }
        }catch(Exception e){
            System.out.println("error reading LineCount ");
        }
        return FB;
    }

    public static void main(String[] args) throws IOException, ParseException {
        DictionaryReader d=new DictionaryReader();
        System.out.println(d.getFB("tab1"));
       /* JSONParser parser = new JSONParser();
        JSONObject jsonData = (JSONObject) parser.parse(new FileReader(DIC_PATH));
        JSONArray jsonArray = (JSONArray) jsonData.get("tables");
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            String value1 = (String) jsonObject.get("lineSize");
            System.out.println(value1);
           // int value2 = Integer.parseInt(jsonObject.get("propertyName2").toString());
            // Do something with the values
        }*/

    }
}
