package org.QueryOptimizer;
import org.QueryOptimizer.dictionnary.DictionaryReader;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Estimator {

    private static DictionaryReader parser;


    public Estimator()
    {
        parser=new DictionaryReader("src/main/java/org/QueryOptimizer/dictionnary/dictionary2.json");
    }

    public DictionaryReader getParser() {return parser;}


    public Double fullTableScane(String table)  {
        if(parser.getFbm(table)>=1)
            return roundFlout(parser.getNbrBloc(table)*parser.getTransTime());
        else
            return roundFlout(parser.getNbrBloc(table)*(parser.getTransTime()+parser.getTpd()));
    }

    public  Double indexScaneSec(String table, String index){
        Double blocInterne = parser.getHauteur(table,index);
        int sel=parser.getNbrLignesSelected(table,index);
        double feuilleIndex=sel/ parser.getOrderMoy(table,index);
        return roundFlout((blocInterne+sel+feuilleIndex)*(parser.getTransTime()+parser.getTpd()));
    }

    public Double indexScaneSec(String table,String index,Double v1, Double v2) {
        Double blocInterne = parser.getHauteur(table,index);
        int sel=parser.getNbrLignesSelected(table,index,v1,v2);
        double feuilleIndex=sel/ parser.getOrderMoy(table,index);
        Double res = (blocInterne+sel+feuilleIndex)*(parser.getTransTime()+parser.getTpd());
        return roundFlout(res);
    }

    public  Double indexScanePri(String table, String index) {
        return roundFlout(parser.getHauteur(table,index)*(parser.getTransTime()+parser.getTpd()));
    }

    public  double hachageScane(String table)  {
        double v1=parser.getTH(table)*parser.getFB(table);
        double tes=parser.getTransTime()+parser.getTpd();
        return  roundFlout((parser.getLineCount(table)/v1)*tes);
    }


    public  double roundFlout(Double num)
    {
        return  Math.round(num * 1000.0) / 1000.0;
    }


    // joins  4

    public  Double BIB(String R, String S)  {
        double tmps=parser.getTransTime()+parser.getTpd();
        return roundFlout(parser.getNbrBloc(R)*( tmps + parser.getNbrBloc(S)*parser.getTransTime()+ parser.getTpd()));
    }


    public  Double TRI(String table){
        double b=parser.getNbrBloc(table);
        double btm=b/parser.getM();
        return roundFlout(2*(btm*parser.getTpd()+b*parser.getTransTime()) );
    }

    public  Double JTF(String R, String S)  {
        double tmps=parser.getTransTime()+parser.getTpd();
        return roundFlout(TRI(R)+TRI(S)+2*(parser.getNbrBloc(R)+parser.getNbrBloc(S))*tmps);
    }

    public  Double JH(String R, String S) {
        //TempsES (BAL R) + TempsES (BALS) + 2 ×(BR + BS) ×TempsESBlo
        double tmps=parser.getTransTime()+parser.getTpd();
        return roundFlout(fullTableScane(R)+fullTableScane(S)+2*(parser.getNbrBloc(R)+parser.getNbrBloc(S)*tmps));
    }


    public  Double PJ(String R, String S) {
        return roundFlout(fullTableScane(R)+fullTableScane(S));
    }

    private void inputConsumers(Node root, List<String>joinInputs, List<String>selInputs){
        if(root==null) return ;
        if(root.getData().contains("σ")&& root.getLeft().getLeft()==null){ // left is the table and table left is null
            String pattern = "\\w+\\s*\\.\\s*\\w+\\s*";//[=><]\s*'[^']*' todo will be treated later
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(root.getData());
           // System.out.println("sel added");
            if (m.find()) {
                String match = m.group(); // Extract the matched substring
                selInputs.add(match);

            }

        }
         if(root.getData().contains("⋈")&& (root.getLeft().getLeft()==null || root.getRight().getLeft()==null)){ // left or right is the table and table left is null
            String pattern = "\\w+\\.\\w+\\s*=\\s*\\w+\\.\\w+";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(root.getData());
    //        System.out.println("join added");
            if (m.find()) {
                String match = m.group(); // Extract the matched substring
                joinInputs.add(match);


            }
        }
        if(root.getLeft()!=null) inputConsumers(root.getLeft(),joinInputs,selInputs);
        if(root.getRight()!=null) inputConsumers(root.getRight(),joinInputs,selInputs);
    }

    public ArrayList<Double> selectionCostVariants(String sel){
        ArrayList<Double> costs=new ArrayList<>();
        String table=sel.split("\\.")[0].trim();
        String col=sel.split("\\.")[1].trim();

        costs.add(fullTableScane(table));
        if(parser.isUnique(table,col)){
            costs.add(hachageScane(table));
        }
        if(parser.isPrimaryKey(table,col)){
            costs.add(indexScanePri(table,col));

        }else if(parser.isIndexed(table,col)){
            costs.add(indexScaneSec(table,col));
        }
        return costs;
    }

    public HashSet<Double> calculateCosts(Node root){
        List<String> joins=new ArrayList<>();
        List<String> sels=new ArrayList<>();
        inputConsumers(root,joins,sels);
        List<List<Double>> cals=new ArrayList<>();
        if(!joins.isEmpty())
            for(String join:joins){
            ArrayList<Double> costList = joinCostVariants(join);
            cals.add(costList);
            }
        if(!sels.isEmpty())
            for (String sel:sels){
            ArrayList<Double> costList = selectionCostVariants(sel);
            cals.add(costList);
            }
        return  generateSumArray(cals);
    }
    public  HashSet<Double> generateSumArray(List<List<Double>> arrays) {
        int n = arrays.size();
        HashSet<Double> sums = new HashSet<>();
        if(arrays.size()>0) {
            sums.addAll(arrays.get(0));
            if(arrays.size()>1) sums.addAll(arrays.get(1));

            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    List<Double> arr1 = arrays.get(i);
                    List<Double> arr2 = arrays.get(j);

                    for (double num1 : arr1) {
                        for (double num2 : arr2) {
                            double sum = num1 + num2;
                            //  if(sum>0&&sum<10000)
                            sums.add(roundFlout(sum));
                        }
                    }
                }
            }
        }
        return sums;
    }
// select A.a, B.b from A,B,C where A.a=B.a AND A.a='2' AND A.z='3' and C.c='3' OR A.a<'7' AND A.k>'89' OR C.e='45' OR C.j='35' AND B.b=C.b
    public static  void main(String [] args)
    {

        Estimator E = new Estimator();
         Translator t=  new Translator("select * from client,achat where client.idc=achat.idc and client.nom='ahmed'");
        //Translator t=new Translator("Select t.t From T1,T2,T3 where T1.a=T2.a AND T2.b=T3.b");
        //  Transformer tr=new  Transformer(t.getFirstTree());
        Node.show(t.getFirstTree(),0);
        List<String> joins=new ArrayList<>();
        List<String> sels=new ArrayList<>();
        E.inputConsumers(t.getFirstTree(),joins,sels);
        System.out.println("\njoin terminals");
        joins.forEach(System.out::println);
        System.out.println("sels terminals");
        sels.forEach(System.out::println);
        System.err.println("estimations: ");
        (E.calculateCosts(t.getFirstTree())).forEach(System.out::println);

/*
       // try{
            //System.out.println("le temps de selection par balayage de la table voiture est : "+E.fullTableScane("Voiture")+" ms");
            System.out.println("le temps de selection par balayage de la table client est : "+E.fullTableScane("Client")+" ms");
            //System.out.println("le temps de selection par balayage de la table Location est : "+E.fullTableScane("Location")/1000+" s");
            System.out.println("le temps de selection par indexage secondaire de la table Client est : "+E.indexScaneSec("Client","idC")+" ms");
            //System.out.println("le temps de selection par indexage secondaire de la table Client est age 500 9000: "+E.indexScaneSec("Voiture","km",10.0,50.0)+" ms");

            System.out.println("le temps de selection par indexage primaire de la table Client est : "+E.indexScanePri("Client","idC")+" ms");


            System.out.println("le temps de selection par hachage  de la table Client est : "+E.hachageScane("Voiture")+" ms");

            //    System.out.println(E.roundFlout(124.9939844));

            System.out.println("le temps de jointure enre client et loction BIB est: "+E.BIB("Client","Location")+" ms");
            System.out.println("le temps de jointure enre location et client BIB est: "+E.BIB("Location","Client")+" ms");


            System.out.println("le temps de jointure enre client et loction JTF est: "+E.JTF("Client","Location")+" ms");
            System.out.println("le temps de jointure enre location et client JTF est: "+E.JTF("Location","Client")+" ms");

            System.out.println("le temps de jointure enre location et client JH est: "+E.JH("Location","Client")+" ms");
            System.out.println("le temps de jointure enre location et client PJ est: "+E.PJ("Client","Location")+" ms");


        }catch(IOException e)
        {
            e.printStackTrace();


 }
 */

    }

    private double selctionCal(Node root,String sel){
        System.out.println("sel: "+sel);
        String table=sel.split("\\.")[0].trim();
        String col=sel.split("\\.")[1].trim();
        System.out.println("here "+root.getData());
        switch (root.getData()){
            case "("+Optimizer.INDEX_PR+")" -> {
                return indexScanePri(table,col);
            }
            case "("+Optimizer.INDEX_SC+")" -> {
                return indexScaneSec(table,col);
            }
            case "("+Optimizer.HASH+")" -> {
                return hachageScane(table);
            }
            case "("+Optimizer.FULL_SCAN+")" -> {
                return fullTableScane(table);
            }
        }
        return 0;
    }
    public  ArrayList<Double> joinCostVariants(String join){
        List<String> attrs=new ArrayList<>();
        ArrayList<Double> costs=new ArrayList<>();
        Pattern p=Pattern.compile("\\w+");
        Matcher matcher = p.matcher(join);
        while (matcher.find()) attrs.add(matcher.group());
        costs.add(BIB(attrs.get(0),attrs.get(2)));
        //  System.out.println("BIB ("+attrs.get(2)+" " +attrs.get(0)+") :"+BIB(attrs.get(0),attrs.get(2)));
        costs.add(JTF(attrs.get(0),attrs.get(2)));
        // System.out.println("JTF ("+attrs.get(2)+" " +attrs.get(0)+") :"+JTF(attrs.get(0),attrs.get(2)));
        costs.add(PJ(attrs.get(0),attrs.get(2)));
        // System.out.println("JP ("+attrs.get(2)+" " +attrs.get(0)+") :"+PJ(attrs.get(0),attrs.get(2)));
        //  System.out.println("JH ("+attrs.get(2)+" " +attrs.get(0)+") :"+JH(attrs.get(0),attrs.get(2)));
        costs.add(JH(attrs.get(0),attrs.get(2)));
        return costs;
    }
    private double joinCalc(Node root, String join) {
        List<String> attrs=new ArrayList<>();
        Pattern p=Pattern.compile("\\w+");
        Matcher matcher = p.matcher(join);
        while (matcher.find()) attrs.add(matcher.group());
        switch (root.getData()){
            case "(BIB)" -> {
               // System.out.println(BIB(attrs.get(0),attrs.get(2)));
                return BIB(attrs.get(0),attrs.get(2));
            }
//            case "(BII)" -> {
//                return indexScaneSec(table,col);
//            }
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
    private Double treeCalc(Node root,Node mother, Double d){
        if(root==null) return d ;

        if(root.getType().equals(Node.S)&& root.getLeft().getLeft()==null){ // left is the table and table left is null
            String pattern = "\\w+\\s*\\.\\s*\\w+\\s*";//[=><]\s*'[^']*' todo will be treated later
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(mother.getData());
            String match="";
            if (m.find())  match = m.group(); // Extract the matched substring
            System.out.println("selies "+selctionCal(root,match));
            d=selctionCal(root,match);
        }
        else if(root.getType().equals(Node.J)&& (root.getLeft().getLeft()==null || root.getRight().getLeft()==null)){ // left or right is the table and table left is null
            String pattern = "\\w+\\.\\w+\\s*=\\s*\\w+\\.\\w+";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(mother.getData());
            String match="";
            if (m.find()) {
                 match = m.group(); // Extract the matched substring
            }
            d=joinCalc(root,match);
        }
        return d+ treeCalc(root.getLeft(),mother.getLeft(),d)+treeCalc(root.getRight(),mother.getRight(),d);
    }

    public List<Double> costs(Set<Node>phy,Node mother){
        List<Double> costs=new ArrayList<>();
        Double d;
        for(Node n: phy){
            d=0.0;
            d=treeCalc(n,mother,d);
            costs.add(d);
        }
        costs.forEach(System.out::println);
        return costs;
    }

    public Double uniCosts(Node phy,Node mother){
        Double d;
        d=0.0;
        d=treeCalc(phy,mother,d);
        return roundFlout(d);//Math.abs(roundFlout(d));
    }

}

