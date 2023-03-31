package org.QueryOptimizer;
import org.QueryOptimizer.dictionary.DictionaryReader;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Estimator {

    private static DictionaryReader parser;


    public Estimator()
    {
        parser=new DictionaryReader("src/main/java/org/QueryOptimizer/dictionary/dictionary.json");
    }

    public DictionaryReader getParser() {return parser;}


    public Double fullTableScan(String table)  {
        if(parser.getFbm(table)>=1)
            return roundNum(parser.getNbrBloc(table)*parser.getTransTime());
        else
            return roundNum(parser.getNbrBloc(table)*(parser.getTransTime()+parser.getTpd()));
    }

    public  Double indexScanSec(String table, String index){
        Double blocIntern = parser.getHauteur(table,index);
        int sel=parser.getNbrLignesSelected(table,index);
        double leafIndex=sel/ parser.getOrderMoy(table,index);
        return roundNum((blocIntern+sel+leafIndex)*(parser.getTransTime()+parser.getTpd()));
    }

    public Double indexScanSec(String table, String index, Double v1, Double v2) {
        Double blocIntern = parser.getHauteur(table,index);
        int sel=parser.getNbrLignesSelected(table,index,v1,v2);
        double leafIndex=sel/ parser.getOrderMoy(table,index);
        Double res = (blocIntern+sel+leafIndex)*(parser.getTransTime()+parser.getTpd());
        return roundNum(res);
    }

    public  Double indexScanPri(String table, String index) {
        return roundNum(parser.getHauteur(table,index)*(parser.getTransTime()+parser.getTpd()));
    }

    public  double hashScan(String table)  {
        double v1=parser.getTH(table)*parser.getFB(table);
        double tes=parser.getTransTime()+parser.getTpd();
        return  roundNum((parser.getLineCount(table)/v1)*tes);
    }


    public  double roundNum(Double num)
    {
        return  Math.round(num * 1000.0) / 1000.0;
    }


    // joins  4

    public  Double BIB(String R, String S)  {
        double temps=parser.getTransTime()+parser.getTpd();
        return roundNum(parser.getNbrBloc(R)*( temps + parser.getNbrBloc(S)*parser.getTransTime()+ parser.getTpd()));
    }


    public  Double TRI(String table){
        double b=parser.getNbrBloc(table);
        double btm=b/parser.getM();
        return roundNum(2*(btm*parser.getTpd()+b*parser.getTransTime()) );
    }

    public  Double JTF(String R, String S)  {
        double temps=parser.getTransTime()+parser.getTpd();
        return roundNum(TRI(R)+TRI(S)+2*(parser.getNbrBloc(R)+parser.getNbrBloc(S))*temps);
    }

    public  Double JH(String R, String S) {
        //TempsES (BAL R) + TempsES (BAL S) + 2 ×(BR + BS) ×TempsESBlo
        double temps=parser.getTransTime()+parser.getTpd();
        return roundNum(fullTableScan(R)+ fullTableScan(S)+2*(parser.getNbrBloc(R)+parser.getNbrBloc(S)*temps));
    }


    public  Double PJ(String R, String S) {
        return roundNum(fullTableScan(R)+ fullTableScan(S));
    }
    public  Double BII(String R,String S){
        double br=parser.getNbrBloc(R);
        double bs=parser.getNbrBloc(S);
        double temps=parser.getTransTime()+parser.getTpd();
        return roundNum(br*(temps + bs*parser.getTransTime() +parser.getTpd()) );
    }



    private double selectionCal(Node root, String sel){
        System.out.println("sel: "+sel);
        String table=sel.split("\\.")[0].trim();
        String col=sel.split("\\.")[1].trim();
        System.out.println("here "+root.getData());
        switch (root.getData()){
            case "("+Optimizer.INDEX_PR+")" -> {
                return indexScanPri(table,col);
            }
            case "("+Optimizer.INDEX_SC+")" -> {
                return indexScanSec(table,col);
            }
            case "("+Optimizer.HASH+")" -> {
                return hashScan(table);
            }
            case "("+Optimizer.FULL_SCAN+")" -> {
                return fullTableScan(table);
            }
        }
        return 0;
    }

    private double joinCalc(Node root, String join) {
        List<String> attrs=new ArrayList<>();
        Pattern p=Pattern.compile("\\w+");
        Matcher matcher = p.matcher(join);
        while (matcher.find()) attrs.add(matcher.group());
        switch (root.getData()){
            case "(BIB)" -> {
                return BIB(attrs.get(0),attrs.get(2));
            }
            case "(BII)" -> {
                return BII(attrs.get(0),attrs.get(2));
            }
            case "(JTF)" -> {
                return JTF(attrs.get(0),attrs.get(2));
            }
            case "(PJ)"-> {
                return PJ(attrs.get(0),attrs.get(2));
            }
            case "(JH)"-> {
                return JH(attrs.get(0),attrs.get(2));
            }
        }
        return 0.0;
    }
    private Double treeCalcMater(Node root, Node mother){
       double d=0.0;
        if(root==null) return d ;

        if(root.getType().equals(Node.S)){ // left is the table and table left is null
            String pattern = "\\w+\\s*\\.\\s*\\w+\\s*";//[=><]\s*'[^']*' todo will be treated later
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(mother.getData());
            String match="";
            if (m.find())  match = m.group(); // Extract the matched substring
            d= selectionCal(root,match)+1.1;
        }
        else if(root.getType().equals(Node.J)){ // left or right is the table and table left is null
            String pattern = "\\w+\\.\\w+\\s*=\\s*\\w+\\.\\w+";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(mother.getData());
            String match="";
            if (m.find()) {
                 match = m.group(); // Extract the matched substring
            }
            d=joinCalc(root,match)+1.1;
        }
        return d+ treeCalcMater(root.getLeft(),mother.getLeft())+ treeCalcMater(root.getRight(),mother.getRight());
    }

    private void treeCalcPipeline(Node root, Node mother, Set<Double> d){
        if(root==null) return ;

        if(root.getType().equals(Node.J)){ // left or right is the table and table left is null
            String pattern = "\\w+\\.\\w+\\s*=\\s*\\w+\\.\\w+";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(mother.getData());
            String match="";
            if (m.find()) {
                match = m.group(); // Extract the matched substring
            }
            d.add(joinCalc(root,match));
        }
        treeCalcPipeline(root.getLeft(),mother.getLeft(),d);
        treeCalcPipeline(root.getRight(),mother.getRight(),d);
    }



    public String uniCosts(Node phy,Node mother){
        double pipe,mater;
        Set<Double>s=new HashSet<>();
        treeCalcPipeline(phy,mother,s);
        pipe=s.stream().max(Double::compare).orElse(Double.NaN);
        mater= treeCalcMater(phy,mother);
        pipe= roundNum(pipe/1000.0);
        String pip=(pipe==0)?"---":pipe+"ms";
        return " Pipeline: "+pip+" Materialisation: "+ roundNum(mater/1000.0)+"ms";
    }
    public double treeCalcPipeline(Node phy,Node mother){
        Set<Double>s=new HashSet<>();
        treeCalcPipeline(phy,mother,s);
        return s.stream().max(Double::compare).orElse(Double.NaN);
    }

    public Set<Double> costs(Set<Node>phy,Node mother,int type){
        Set<Double> costs=new HashSet<>();

        double d;
        for(Node n: phy){
            d=(type==1)?treeCalcPipeline(n,mother): treeCalcMater(n,mother);
            costs.add(d);
        }
        costs.forEach(System.out::println);
        return costs;
    }
    public double minCostsOneLogTree(Set<Node> phys,Node mother){
        Set<Double> costs;
        if(Node.joinCount(mother)>0){// pipeline
            costs=costs(phys,mother,1);
        }else costs=costs(phys,mother,2);

       return roundNum(costs.stream().min(Double::compare).orElse(Double.NaN)/1000.0);
    }
    // map with keys as logical trees and values as physical trees


}

