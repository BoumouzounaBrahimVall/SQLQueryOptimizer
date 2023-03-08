package org.QueryOptimizer.dictionnary;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DictionaryReader {
    private final String DIC_PATH;
    private final JSONParser parser;
    public DictionaryReader(String path){
        parser= new JSONParser();
        DIC_PATH=path;
    }
    /// general methods
    private double getDBProperty(String property){
        double prop=0;
        try {
            JSONObject jsonData = (JSONObject) this.parser.parse(new FileReader(DIC_PATH));
            prop=Double.parseDouble(jsonData.get(property).toString());
        }catch(Exception e){
            System.out.println("error reading database property: "+property);
        }
        return prop;
    }
    private double getTableProperty(String tableName,String property) {
        double value=0;
        try {
            JSONObject jsonData = (JSONObject) this.parser.parse(new FileReader(DIC_PATH));
            JSONArray tables = (JSONArray) jsonData.get("tables");
            for (Object object : tables) {
                JSONObject table = (JSONObject) object;
                String tableNm = (String) table.get("tableName");
                if(tableNm.equals(tableName))
                {
                    value=Double.parseDouble(table.get(property).toString());
                    break;
                }
            }
        }catch(Exception e){
            System.out.println("error reading property: "+property +" for table:"+tableName);
        }
        return value;
    }
    private double getColumnProperty(String tableName,String columnName,String property){
        double value=0;
        try {
            JSONObject jsonData = (JSONObject) this.parser.parse(new FileReader(DIC_PATH));

            JSONArray tables = (JSONArray) jsonData.get("tables");
            for (Object tab : tables) {
                JSONObject table = (JSONObject) tab;
                String tableNm = (String) table.get("tableName");
                if(tableNm.equals(tableName))
                {
                    JSONArray columns = (JSONArray) table.get("columns");
                    for (Object col : columns) {
                        JSONObject column = (JSONObject) col;
                        String columnNm = (String) column.get("columnName");
                        if(columnNm.equals(columnName)) {
                              value=Double.parseDouble(column.get(property).toString());
                              return value;
                        }

                    }
                }
            }
        }catch(Exception e){
            System.out.println("error reading property: "+property+" for column:"+columnName);
        }
        return value;
    }
    /// database properties
    public  Double getTailleBloc()   {return getDBProperty("blocSize");}
    public double getTransTime()  {return getDBProperty("transTime");}
    public double getTpd()  {return getDBProperty("tpd");} // tempsPosDebut
    public double getM(){return getDBProperty("M");}
    public double getTailleDescripteurBloc(){ return getDBProperty("tailleDescripteurBloc");}
    public double getNbrPos()  {return getDBProperty("nbrPos");}

    // table properties
    public double getLineSize(String tableName)  {return getTableProperty(tableName,"lineSize");}

    public double getLineCount(String tableName)  {return getTableProperty(tableName,"lineCount");}

    public double getFB(String tableName)  {return getTableProperty(tableName,"FB");}

    //column properties
    public boolean isPrimaryKey(String tableName,String columnName){return getColumnProperty(tableName, columnName, "Pk") == 1;}
    public boolean isIndexed(String tableName,String columnName){return getColumnProperty(tableName, columnName, "index") == 1;}
    public boolean isUnique(String tableName,String columnName){return getColumnProperty(tableName, columnName, "unique") == 1;}
    public double getCardinality(String tableName,String columnName){return getColumnProperty(tableName, columnName, "cardinality") ;}
    public double getMinVal(String tableName,String columnName){return getColumnProperty(tableName, columnName, "minVal") ;}
    public double getMaxVal(String tableName,String columnName){return getColumnProperty(tableName, columnName, "maxVal") ;}
    public double getOrderMoy(String tableName,String columnName){return getColumnProperty(tableName, columnName, "orderMoy") ;}

   // calculated table properties
    public Double getFbm(String tableName)   {return (getTailleBloc()-getTailleDescripteurBloc())/getLineSize(tableName);}
    public Double getNbrBloc(String tableName)  {
    //    System.out.println("lines"+tableName+": "+getLineCount(tableName)+" FB "+getFB(tableName));
        return (getLineCount(tableName)/getFB(tableName));}

    // calculated column properties
    public  int getNbrLignesSelected(String tableName,String col)  {return (int) (getLineCount(tableName)/getCardinality(tableName,col));}
    public int getNbrLignesSelected(String tableName,String col,Double v1, Double v2)  {return (int) (getFS(tableName,col,v1,v2)*getLineCount(tableName));}
    public  Double getFS(String tableName,String col) {return (double) (1/getCardinality(tableName,col))*100;}
    public Double getFS(String tableName,String col, Double v1, Double v2) {return ((v2-v1)/ (getMaxVal(tableName,col)-getMinVal(tableName,col)));}
    public Double getHauteur(String table, String index) {return (Math.log(getLineCount(table)) / Math.log(getOrderMoy(table,index)));}
    public Double getTH(String table)  {Double fb=0.8*getFbm(table) ;return getLineCount(table)/fb;}

/*
    public static void main(String[] args){
        DictionaryReader d=new DictionaryReader("src/main/java/org/QueryOptimizer/dictionnary/dictionary.json");
        System.out.println(d.getOrderMoy("tab1","name1"));

    }*/
}
