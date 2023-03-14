package org.QueryOptimizer;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
public class Optimizer {


    public Set<Node> physiquesArbre(Node arbre){
        Set<Node> treeSet = new HashSet<>();
        treeSet.add(arbre);
        Node Ar=Node.cloneTree(arbre);
        treeSet.add(Ar);
        change0(Ar);
        //jointure
        for(Node n1:change(Ar,"⋈","BII")){
            treeSet.addAll(change(n1, "⋈", "JH"));
            treeSet.addAll(change(n1, "⋈", "PJ"));
            treeSet.addAll(change(n1, "⋈", "JTF"));
            treeSet.addAll(change(n1, "σ", "indexageNonUnique"));
            treeSet.addAll(change(n1, "σ", "indexageSecondaire"));
            treeSet.addAll(change(n1, "σ", "indexagePrimaire"));
            treeSet.addAll(change(n1, "σ", "hachage"));
        }
        return treeSet;
    }
    private void change0(Node arbre){
        if(arbre==null) return;
        if(arbre.getData().contains(" ⋈"))
            arbre.setData(arbre.getData()+" (BIB)");
        if(arbre.getData().contains(" σ"))
            if(Existtables(arbre.getLeft().getData()))
                arbre.setData(arbre.getData()+" (Balayage)");

        change0(arbre.getLeft());
        change0(arbre.getRight());
    }
    private Set<Node> change(Node arbre, String test, String nouveau){
        Set<Node> treeSet = new HashSet<>();
        //commutativiteSelection.add(arbre);
        for(int i=0;i<2;i++){
            for (int j=0;j<=2;j++){
                Node tmp=change1(Node.cloneTree(arbre),i,new int[]{0},j,test,nouveau);
                Node tmp2=change2(Node.cloneTree(arbre),i,new int[]{0},j,test,nouveau);
                treeSet.add(tmp);
               treeSet.add(tmp2);
            }
        }
        return treeSet;
    }
    private Node change1(Node a ,int initial,int[] counter,int maxCount,String test,String nouveau){
        if (a == null) {
            return null;
        }
        if(a.getData().contains(test)){
            if(counter[0]>=initial) {
                String oldCnt=a.getData();
                if(Existtables(a.getLeft().getData()) || !a.getData().contains("σ"))
                {
                    String aff=oldCnt.replaceAll("\\([^)]*\\)", "")+" ("+nouveau+")";
                    System.out.println("aff: "+aff);
                    a.setData(aff);
                }


            }
            counter[0]++;
        }
        if(counter[0]==maxCount) return a;
        if (a.getRight()!=null) change1(a.getRight(),initial,counter, maxCount,test,nouveau);
        if(a.getLeft()!=null) change1(a.getLeft(),initial,counter, maxCount,test,nouveau);
        return a;

    }
    private Node change2(Node a ,int initial,int[] counter,int maxCount,String test,String nouveau){
        if (a == null) {
            return null;
        }

        if(a.getData().contains(test)){

            if(counter[0]>=initial) {
                String oldCnt=a.getData();
                if(Existtables(a.getLeft().getData()) || !a.getData().contains("σ"))
                {

                    String aff=oldCnt.replaceAll("\\([^)]*\\)", "")+"("+nouveau+")";
                    //a.setMethode(" ("+nouveau+")");
                    a.setData(aff);
                }

            }
            counter[0]++;
        }
        if(counter[0]==maxCount) return a;

        if(a.getLeft()!=null) change2(a.getLeft(),initial,counter, maxCount,test,nouveau);
        if (a.getRight()!=null) change2(a.getRight(),initial,counter, maxCount,test,nouveau);
        return a;
    }
    boolean Existtables(String a) {
     ///   for(String e:tables)
          //  if(a.equals(e)){
                return true;
         //   }
        //return false;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Optimizer op=new Optimizer();
        JTextField script =new JTextField(100);
        JButton execute=new JButton("Execute");
        execute.addActionListener(evt -> {
            Translator t=new Translator(script.getText());
            Transformer tr=new  Transformer(t.getFirstTree());
            Estimator estimator = new Estimator();
            int h=Visualizer.drawListOfTrees(tr.getAllVariants(),estimator,frame);//op.physiquesArbre(t.getFirstTree())
            frame.setSize(new Dimension(frame.getWidth(),3));//
            frame.pack();
            frame.setVisible(true);
        });
        JPanel p=new JPanel();
        p.add(script);
        p.add(execute);
        frame.add(p,BorderLayout.NORTH);
        // Translator t=  new Translator("
        // select * from client,achat, produit where client.idc=achat.idc and client.idc='12' and  client.nom='ahmed' and produit.titre='sth' or client.nom='salim' or produit.prix='100'  and achat.idp=produit.idp
        // select A.a, B.b from A,B,C where A.a=B.a AND A.a='2' AND A.z='3' and C.c='3' OR A.a<'7' AND A.a>'89' OR C.e='45' OR C.j='35' AND B.b=C.b");
        //Translator t=new Translator("Select t.t From T1,T2,T3 where T1.a=T2.a AND T2.b=T3.b");
        //  Transformer tr=new  Transformer(t.getFirstTree());
        //SELECT nom,age,prenom FROM Client,Voiture,Location WHERE Client.id_client=Location.id_client AND Voiture.id_voiture=Location.id_voiture AND Client.age='40' AND Voiture.km='1000' AND Voiture.marque='Mercedes'
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
