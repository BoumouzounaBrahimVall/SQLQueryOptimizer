package org.QueryOptimizer;
import org.QueryOptimizer.dictionnary.DictionaryReader;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
public class Optimizer {
    private static final Estimator es=new Estimator();
    private static final String INDEX_PR="2nd index";
    private static final String INDEX_SC="1st index";
    private static final String HASH="2nd index";
    private static final String FULL_SCAN="balayage";
    public Set<Node> physiquesArbre(Node arbre) {
        Set<Node> treeSet = new HashSet<>();
        treeSet.add(arbre);
        Node Ar = Node.cloneTree(arbre);
        treeSet.add(Ar);
        //jointure
        change(Ar, "BIB").forEach(n1->{selectPhysicalVars(treeSet,n1);});
        change(Ar, "BII").forEach(n1->{selectPhysicalVars(treeSet,n1);});
        change(Ar, "JH").forEach(n1->{selectPhysicalVars(treeSet,n1);});
        change(Ar, "PJ").forEach(n1->{selectPhysicalVars(treeSet,n1);});
        change(Ar,  "JTF").forEach(n1->{selectPhysicalVars(treeSet,n1);});
        removeGarbage(treeSet);
        return treeSet;
    }

    private void selectPhysicalVars(Set<Node> treeSet, Node n1) {
        treeSet.addAll(change(n1, FULL_SCAN));
        treeSet.addAll(change(n1,  INDEX_SC));
        treeSet.addAll(change(n1, INDEX_PR));
        treeSet.addAll(change(n1,  HASH));
    }

    private void removeGarbage(Set<Node> treeSet) {
        treeSet.removeIf(this::downReader);
    }

    private boolean downReader(Node n) {
        if (n==null)return false;
        if(n.getData().matches("(σ\\w+\\s*\\.\\s*\\w+\\s*[=><]\\s*'[^']*')|(⋈\\w+\\.\\w+\\s*=\\s*\\w+\\.\\w+)")) {
            System.out.println(n.getData());
            return true;
        }
        return downReader(n.getLeft())||downReader(n.getRight());
    }


    private Set<Node> change(Node arbre, String nouveau) {
        Set<Node> treeSet = new HashSet<>();
        Node tmp = change1(Node.cloneTree(arbre), nouveau);
        treeSet.add(tmp);
        return treeSet;
    }

    private Node change1(Node a, String nouveau) {
        if (a == null) {
            return null;
        }
        if (a.getData().contains("⋈")) {
            String oldCnt = a.getData();
            String aff = oldCnt.replaceAll("\\([^)]*\\)", "") + " (" + nouveau + ")";
            System.out.println("aff: " + aff);
            a.setData("(" + nouveau + ")");
        }
        else  if(a.getData().contains("σ")){

            String match="";
            String pattern = "\\w+\\s*\\.\\s*\\w+\\s*";//[=><]\s*'[^']*' todo will be treated later
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(a.getData());
            String oldCnt=a.getData();

            if (m.find()) match = m.group(); // Extract the matched substring
            String table=match.split("\\.")[0].trim();
            String col=match.split("\\.")[1].trim();
                if(nouveau.contains(HASH)){
                    if(!es.getParser().isUnique(table,col)) nouveau=FULL_SCAN;

                }else if(nouveau.contains(INDEX_PR)){
                    if(!es.getParser().isPrimaryKey(table,col))  nouveau=FULL_SCAN;
                }else if(nouveau.contains(INDEX_SC)){
                    if(!es.getParser().isIndexed(table,col))  nouveau=FULL_SCAN;
                }

                a.setData( oldCnt.replaceAll("\\([^)]*\\)", "")+"("+nouveau+")" );
            }
        if (a.getRight() != null) change1(a.getRight(), nouveau);
        if (a.getLeft() != null) change1(a.getLeft(), nouveau);
        return a;

    }

    /*
    private Node changeSel(Node a ,int initial,int[] counter,int maxCount,String nouveau){
        if (a == null) {
            return null;
        }
        if(a.getData().contains("σ")){
            if(counter[0]>=initial) {
                String match="";
                String pattern = "\\w+\\s*\\.\\s*\\w+\\s*";//[=><]\s*'[^']*' todo will be treated later
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(a.getData());
                String oldCnt=a.getData();
                String aff="";
                if (m.find()) match = m.group(); // Extract the matched substring
                String table=match.split("\\.")[0].trim();
                String col=match.split("\\.")[1].trim();
                if(nouveau.contains("hashage")){
                    if(!es.getParser().isUnique(table,col)) nouveau="balayage";

                }else if(nouveau.contains("1st index")){
                        if(!es.getParser().isPrimaryKey(table,col))  nouveau="balayage";
                }else if(nouveau.contains("2sd index")){
                    if(!es.getParser().isIndexed(table,col))  nouveau="balayage";
                }
                aff=oldCnt.replaceAll("\\([^)]*\\)", "")+"("+nouveau+")";
                a.setData(aff);
            }
            counter[0]++;
        }
        if(counter[0]==maxCount) return a;
        if(a.getLeft()!=null) changeSel(a.getLeft(),initial,counter, maxCount,nouveau);
        if (a.getRight() !=null) changeSel(a.getRight(),initial,counter, maxCount,nouveau);
        return a;
    }
*/
    public static void main(String[] args) {
        /*JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Optimizer op = new Optimizer();
        JTextField script = new JTextField(100);
        JButton execute = new JButton("Execute");
        execute.addActionListener(evt -> {
            Translator t = new Translator(script.getText());
            Transformer tr = new Transformer(t.getFirstTree());
            Estimator estimator = new Estimator();
            int h = Visualizer.drawListOfTrees(op.physiquesArbre(t.getFirstTree()), estimator, frame);//
            frame.setSize(new Dimension(frame.getWidth(), 3));//tr.getAllVariants()
            frame.pack();
            frame.setVisible(true);
        });
        JPanel p = new JPanel();
        p.add(script);
        p.add(execute);
        frame.add(p, BorderLayout.NORTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);*/
        // Translator t=  new Translator("
        // select * from client,achat, produit where client.idc=achat.idc and client.idc='12' and  client.nom='ahmed' and produit.titre='sth' or client.nom='salim' or produit.prix='100'  and achat.idp=produit.idp
        // select A.a, B.b from A,B,C where A.a=B.a AND A.a='2' AND A.z='3' and C.c='3' OR A.a<'7' AND A.a>'89' OR C.e='45' OR C.j='35' AND B.b=C.b");
        //Translator t=new Translator("Select t.t From T1,T2,T3 where T1.a=T2.a AND T2.b=T3.b");
        //  Transformer tr=new  Transformer(t.getFirstTree());
        //SELECT nom,age,prenom FROM Client,Voiture,Location WHERE Client.id_client=Location.id_client AND Voiture.id_voiture=Location.id_voiture AND Client.age='40' AND Voiture.km='1000' AND Voiture.marque='Mercedes'
        JFrame frame = new JFrame("Optimizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Optimizer optimizer = new Optimizer();

        JTextField script = new JTextField(50);
        script.setFont(new Font("Arial", Font.PLAIN, 14));
        script.setPreferredSize(new Dimension(400, 30));

        JButton execute = new JButton("Execute");
        execute.setFont(new Font("Arial", Font.PLAIN, 14));
        execute.setBackground(Color.BLUE);
        execute.setForeground(Color.WHITE);
        execute.setPreferredSize(new Dimension(100, 30));
        execute.addActionListener(evt -> {
            Translator t = new Translator(script.getText());
            Transformer tr = new Transformer(t.getFirstTree());
            Estimator estimator = new Estimator();

            int h = Visualizer.drawListOfTrees(optimizer.physiquesArbre(t.getFirstTree()), estimator, frame);
            frame.setSize(new Dimension(frame.getWidth(), h));

            frame.pack();
        });

        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(script, BorderLayout.CENTER);
        p.add(execute, BorderLayout.EAST);
        frame.add(p, BorderLayout.NORTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setSize(new Dimension(700, 500));
        frame. setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);
    }
}