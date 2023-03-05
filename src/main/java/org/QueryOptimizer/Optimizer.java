package org.QueryOptimizer;
import java.awt.*;
import javax.swing.*;

public class Optimizer {

    public static void main(String[] args) {
        JFrame frame = new JFrame();frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField script =new JTextField(100);
        JButton execute=new JButton("Execute");
        execute.addActionListener(evt -> {
            Translator t=new Translator(script.getText());
            Transformer tr=new  Transformer(t.getFirstTree());
            int h=Visualizer.drawListOfTrees(tr.getAllVariants(),frame);
            frame.setSize(new Dimension(frame.getWidth(),h));
            frame.pack();
            frame.setVisible(true);
        });
        JPanel p=new JPanel();
        p.add(script);
        p.add(execute);
        frame.add(p,BorderLayout.NORTH);
        // Translator t=  new Translator("select A.a, B.b from A,B,C where A.a=B.b AND A.a='2' AND A.z='3' and C.c='3' OR A.a<'7' AND A.a>'89' OR C.e='45' OR C.j='35' AND B.b=C.b");
        //Translator t=new Translator("Select t.t From T1,T2,T3 where T1.a=T2.a AND T2.b=T3.b");
        //  Transformer tr=new  Transformer(t.getFirstTree());
        //SELECT nom,age,prenom FROM Client,Voiture,Location WHERE Client.id_client=Location.id_client AND Voiture.id_voiture=Location.id_voiture AND Client.age='40' AND Voiture.km='1000' AND Voiture.marque='Mercedes'
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
