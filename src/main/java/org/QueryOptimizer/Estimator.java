package org.QueryOptimizer;
import org.QueryOptimizer.dictionnary.DictionaryReader;
import java.io.IOException;
        import java.util.Collections;
        import java.util.Vector;


public class Estimator {

    private static DictionaryReader parser;


    public Estimator()
    {
        parser=new DictionaryReader("src/main/java/org/QueryOptimizer/dictionnary/dictionary.json");
    }

    public DictionaryReader getParser() {return parser;}


    static Double fullTableScane(String table) throws IOException {
        if(parser.getFbm(table)>=1)
            return roundFlout(parser.getNbrBloc(table)*parser.getTransTime());
        else
            return roundFlout(parser.getNbrBloc(table)*(parser.getTransTime()+parser.getTpd()));
    }

    public static Double indexScaneSec(String table, String index) throws IOException {
        Double blocInterne = parser.getHauteur(table,index);
        int sel=parser.getNbrLignesSelected(table,index);
        double feuilleIndex=sel/ parser.getOrderMoy(table,index);
        return roundFlout((blocInterne+sel+feuilleIndex)*(parser.getTransTime()+parser.getTpd()));
    }

    public Double indexScaneSec(String table,String index,Double v1, Double v2) throws IOException {
        Double blocInterne = parser.getHauteur(table,index);
        int sel=parser.getNbrLignesSelected(table,index,v1,v2);
        double feuilleIndex=sel/ parser.getOrderMoy(table,index);
        Double res = (blocInterne+sel+feuilleIndex)*(parser.getTransTime()+parser.getTpd());
        return roundFlout(res);
    }

    public static Double indexScanePri(String table, String index) throws IOException {
        return roundFlout(parser.getHauteur(table,index)*(parser.getTransTime()+parser.getTpd()));
    }

    public static Double hachageScane(String table) throws IOException {
        Double v1=parser.getTH(table)*parser.getFB(table);
        Double tes=parser.getTransTime()+parser.getTpd();
        return  roundFlout((parser.getLineCount(table)/v1)*tes);
    }


    public static double roundFlout(Double num)
    {
        return  Math.round(num * 1000.0) / 1000.0;
    }


    // joins  4

    public static Double BIB(String R, String S) throws IOException {
        Double tmps=parser.getTransTime()+parser.getTpd();
        return roundFlout(parser.getNbrBloc(R)*( tmps + parser.getNbrBloc(S)*tmps));
    }


    public static Double TRI(String table) throws IOException {
        Double b=parser.getNbrBloc(table);
        Double btm=b/parser.getM();
        return roundFlout(2*(btm*parser.getTpd()+b*parser.getTransTime()) );
    }

    public static Double JTF(String R, String S) throws IOException {
        Double tmps=parser.getTransTime()+parser.getTpd();
        return roundFlout(TRI(R)+TRI(S)+2*(parser.getNbrBloc(R)+parser.getNbrBloc(S))*tmps);
    }

    public static Double JF(String R, String S) throws IOException {
        //TempsES (BAL R) + TempsES (BALS) + 2 ×(BR + BS) ×TempsESBlo
        Double tmps=parser.getTransTime()+parser.getTpd();
        return roundFlout(fullTableScane(R)+fullTableScane(S)+2*(parser.getNbrBloc(R)+parser.getNbrBloc(S)*tmps));
    }


    public static Double PJ(String R, String S) throws IOException {
        return roundFlout(fullTableScane(R)+fullTableScane(S));
    }





    public static void calculerCoutNode(Node node,int i)  {
        /*if(!node.isLeaf())
        {
            //System.out.println(node.getTablesFromCondition().get(0)+" "+node.getColumns().get(0));

            switch(i) {
                case 1:
                    if (node.getType().equals("SELECTION")) {
                        node.setCout(fullTableScane(node.getTablesFromCondition().get(0)));
                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(BIB(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 2:
                    if (node.getType().equals("SELECTION")) {
                        node.setCout(fullTableScane(node.getTablesFromCondition().get(0)));
                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(JTF(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 3:
                    if (node.getType().equals("SELECTION")) {
                        node.setCout(fullTableScane(node.getTablesFromCondition().get(0)));
                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(JF(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 4:
                    if (node.getType().equals("SELECTION")) {
                        node.setCout(fullTableScane(node.getTablesFromCondition().get(0)));
                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(PJ(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 5:
                    if (node.getType().equals("SELECTION")) {
                        node.setCout(hachageScane(node.getTablesFromCondition().get(0)));
                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(BIB(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 6:
                    if (node.getType().equals("SELECTION")) {
                        node.setCout(hachageScane(node.getTablesFromCondition().get(0)));
                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(JTF(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 7:
                    if (node.getType().equals("SELECTION")) {
                        node.setCout(hachageScane(node.getTablesFromCondition().get(0)));
                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(JF(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 8:
                    if (node.getType().equals("SELECTION")) {
                        node.setCout(hachageScane(node.getTablesFromCondition().get(0)));
                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(PJ(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 9:
                    if (node.getType().equals("SELECTION")) {
                        if(parser.isIndexUnique(node.getTablesFromCondition().get(0),node.getColumns().get(0))==1)
                        {
                            node.setCout(indexScanePri(node.getTablesFromCondition().get(0),node.getColumns().get(0)));
                        }
                        else node.setCout(hachageScane(node.getTablesFromCondition().get(0)));

                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(BIB(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 10:
                    if (node.getType().equals("SELECTION")) {
                        if(parser.isIndexUnique(node.getTablesFromCondition().get(0),node.getColumns().get(0))==1)
                        {
                            node.setCout(indexScanePri(node.getTablesFromCondition().get(0),node.getColumns().get(0)));
                        }
                        else node.setCout(hachageScane(node.getTablesFromCondition().get(0)));

                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(JTF(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 11:
                    if (node.getType().equals("SELECTION")) {
                        if(parser.isIndexUnique(node.getTablesFromCondition().get(0),node.getColumns().get(0))==1)
                        {
                            node.setCout(indexScanePri(node.getTablesFromCondition().get(0),node.getColumns().get(0)));
                        }
                        else node.setCout(hachageScane(node.getTablesFromCondition().get(0)));

                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(JF(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 12:
                    if (node.getType().equals("SELECTION")) {
                        //  System.out.println(node.getTablesFromCondition().get(0)+" "+node.getColumns().get(0));
                        if(parser.isIndexUnique(node.getTablesFromCondition().get(0),node.getColumns().get(0))==1)
                        {
                            node.setCout(indexScanePri(node.getTablesFromCondition().get(0),node.getColumns().get(0)));
                        }
                        else node.setCout(hachageScane(node.getTablesFromCondition().get(0)));

                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(PJ(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 13:
                    if (node.getType().equals("SELECTION")) {
                        if(parser.isIndexUnique(node.getTablesFromCondition().get(0),node.getColumns().get(0))==0)
                        {
                            node.setCout(indexScaneSec(node.getTablesFromCondition().get(0),node.getColumns().get(0)));
                        }
                        else node.setCout(hachageScane(node.getTablesFromCondition().get(0)));

                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(BIB(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 14:
                    if (node.getType().equals("SELECTION")) {
                        if(parser.isIndexUnique(node.getTablesFromCondition().get(0),node.getColumns().get(0))==0)
                        {
                            node.setCout(indexScaneSec(node.getTablesFromCondition().get(0),node.getColumns().get(0)));
                        }
                        else node.setCout(hachageScane(node.getTablesFromCondition().get(0)));

                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(JTF(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 15:
                    if (node.getType().equals("SELECTION")) {
                        if(parser.isIndexUnique(node.getTablesFromCondition().get(0),node.getColumns().get(0))==0)
                        {
                            node.setCout(indexScaneSec(node.getTablesFromCondition().get(0),node.getColumns().get(0)));
                        }
                        else node.setCout(hachageScane(node.getTablesFromCondition().get(0)));

                    } else if (node.getType().equals("JOIN")) {
                        node.setCout(JF(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
                case 16:
                    if (node.getType().equals("SELECTION")) {
                        if(parser.isIndexUnique(node.getTablesFromCondition().get(0),node.getColumns().get(0))==0)
                        {
                            node.setCout(indexScaneSec(node.getTablesFromCondition().get(0),node.getColumns().get(0)));
                        }
                        else node.setCout(hachageScane(node.getTablesFromCondition().get(0)));

                    }else if (node.getType().equals("JOIN")) {
                        node.setCout(PJ(node.getTablesFromCondition().get(0), node.getTablesFromCondition().get(1)));
                    }
                    break;
            }

        }*/
    }


    public static Double coutMinimalTree(Node root)  {
        Vector<Double> couts=new Vector<>();
        for (int i=1;i<=8;i++) {
            couts.add(calculterVarianteCouts(root, i));
        }
        return Collections.min(couts);
    }

    public static String afficherCouts(Node root)
    {
        String couts="";
        for (int i=1;i<=8;i++) {
            couts+=("\n cout "+i+":    " +calculterVarianteCouts(root, i) + " s");
        }
        couts+="\n\nLE COUT MINIMAL :"+coutMinimalTree(root);
        return couts ;
    }
    public static Double calculterVarianteCouts(Node root,int i)  {
        calculerCout(root,i);
        return roundFlout(calculerCoutTree(root,i)/1000);
    }

    public static Double calculerCoutTree(Node root,int i)
    {
        if(root==null)return 0.0;
        return root.getCout()+calculerCoutTree(root.getLeft(),i)+calculerCoutTree(root.getRight(),i);
    }

    public static void calculerCout(Node root,int i) {
        if(root!=null)
        {
            calculerCout(root.getLeft(),i);
            calculerCoutNode(root,i);
            calculerCout(root.getRight(),i);
        }
    }


    public static  void main(String [] args)
    {

        Estimator E = new Estimator();



        try{
            //System.out.println("le temps de selection par balayage de la table voiture est : "+E.fullTableScane("Voiture")+" ms");
            System.out.println("le temps de selection par balayage de la table client est : "+E.fullTableScane("Voiture")+" ms");
            //System.out.println("le temps de selection par balayage de la table Location est : "+E.fullTableScane("Location")/1000+" s");
            System.out.println("le temps de selection par indexage secondaire de la table Client est : "+E.indexScaneSec("Voiture","km")+" ms");
            //System.out.println("le temps de selection par indexage secondaire de la table Client est age 500 9000: "+E.indexScaneSec("Voiture","km",10.0,50.0)+" ms");

            System.out.println("le temps de selection par indexage primaire de la table Client est : "+E.indexScanePri("Voiture","id_voiture")+" ms");


            System.out.println("le temps de selection par hachage  de la table Client est : "+E.hachageScane("Voiture")+" ms");

            //    System.out.println(E.roundFlout(124.9939844));

            System.out.println("le temps de jointure enre client et loction BIB est: "+E.BIB("Client","Location")+" ms");
            System.out.println("le temps de jointure enre location et client BIB est: "+E.BIB("Location","Client")+" ms");


            System.out.println("le temps de jointure enre client et loction JTF est: "+E.JTF("Client","Location")+" ms");
            System.out.println("le temps de jointure enre location et client JTF est: "+E.JTF("Location","Client")+" ms");

            System.out.println("le temps de jointure enre location et client JF est: "+E.JF("Location","Client")+" ms");
            System.out.println("le temps de jointure enre location et client PJ est: "+E.PJ("Client","Location")+" ms");


        }catch(IOException e)
        {
            e.printStackTrace();
        }

    }


}

