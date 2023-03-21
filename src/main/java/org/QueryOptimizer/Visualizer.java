package org.QueryOptimizer;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class Visualizer extends JPanel {
    private final Node tree;

    public Visualizer(Node tr,Color col) {
        this.tree = tr;
        setPreferredSize(new Dimension(1000, 400));
        this.setBackground(col);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Serif", Font.ITALIC, 10));
        int x=getWidth()/2,y=50;
        if(tree.getData().contains("π")){
            g.drawString(tree.getData(), x- 12, y + 12);
            if(tree.getLeft()!=null) {
                g.drawLine(x+5, y+20, x+5, y+40);
                drawNode(tree.getLeft(), x, y+40, g);
            }
        } else drawNode(tree, x-10, y + 10,g);


    }

    private void drawNode(Node node, int x, int y, Graphics g) {
        String str= node.getData();
        if(str.contains("&&")){
            String[] strs=str.split("&&");
            g.drawString(strs[0], x-20, y + 12);
            g.drawString("&&", x-12, y + 24);
            int i=y+24;
            for (int j=1;j<strs.length-1;j++) {
                g.drawString(strs[j], x - 20, i += 12);
                g.drawString("&&", x - 12, i += 12);

            }
            g.drawString(strs[strs.length-1], x - 20, i += 12);
            y=i;
        }else if(str.contains("OR")&&str.length()>2){
            String[] strs=str.split("OR");
            g.drawString(strs[0], x-20, y + 12);
            g.drawString("OR", x-12, y + 24);
            int i=y+24;
            for (int j=1;j<strs.length-1;j++) {
                g.drawString(strs[j], x - 20, i += 12);
                g.drawString("OR", x - 12, i += 12);
            }
            g.drawString(strs[strs.length-1], x - 20, i += 12);
            y=i;
        }

        else if(node.getType().equals(Node.T)){
            g.drawString(node.getData(), x-9, y + 12);
        }
        else g.drawString(node.getData(), x-20, y + 12);//x - 30
        //

        if (node.getLeft() != null) {
            int x1 = x - 15;
            int y1 = y + 15;

            int x2 = (int) (x1 - Math.pow(2, getHeight(node.getLeft())) * 8 + 0.5);
            if(node.getType().equals(org.QueryOptimizer.Node.S)) x2=x1=x;
            int y2 = y + 40;
            g.drawLine(x1, y1, x2, y2);
            drawNode(node.getLeft(), x2, y2, g);
        }
        if (node.getRight() != null) {
            int x1 = x + 15;
            int y1 = y + 15;
            int x2 = (int) (x1 + Math.pow(2, getHeight(node.getRight())) * 8 + 0.5);
            int y2 = y + 40;
            g.drawLine(x1, y1, x2, y2);
            drawNode(node.getRight(), x2, y2, g);
        }
    }

    private int getHeight(Node Node) {
        if (Node == null) {
            return 0;
        }
        return Math.max(getHeight(Node.getLeft()), getHeight(Node.getRight())) + 1;
    }






    static JScrollPane drawListOfTrees(Optimizer op) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Set<Node> variants=op.getTr().getAllVariants();

        panel.add(drawnTree(op.getT().getFirstTree(),0,"main Tree ",new Color(14, 28, 162)));
        Node optimal=op.optimalTree(op.allPhysicalTrees(variants));
        panel.add(drawnTree(optimal, op.getEstimator().minCostsOneLogTree(op.physiquesArbre(optimal),optimal),"optimal Tree",new Color(13, 122, 18)));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        int i=0;
        for (Node tree : variants) {

            Visualizer subPanel = new Visualizer(tree,Color.DARK_GRAY);
            JLabel treeLabel = new JLabel("Tree " + (i + 1));i++;
            treeLabel.setFont(new Font("Arial", Font.BOLD, 16));
            treeLabel.setForeground(Color.WHITE);
            treeLabel.setOpaque(true);
            treeLabel.setBackground(Color.lightGray);
            treeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel ruleLabel = new JLabel("Rule applied: "+ op.getTr().reglenames.get(tree) );
            ruleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            ruleLabel.setForeground(new Color(102, 102, 102));

            JLabel costLabel = new JLabel("Physical costs: minCost: " );//+ minCost + "ms, maxCost: " + maxCost + "ms, count physical tree: " + ls.size());
            costLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            costLabel.setForeground(new Color(102, 102, 102));

            JTextArea costArea = new JTextArea();
            costArea.setEditable(false);
            costArea.setLineWrap(true);
            costArea.setWrapStyleWord(true);
            costArea.setFont(new Font("Arial", Font.PLAIN, 14));
            costArea.setForeground(new Color(102, 102, 102));
            JButton showCosts   = new JButton("Show costs");
            showCosts.setFont(new Font("Arial", Font.PLAIN, 14));
            showCosts.setBackground(new Color(115, 112, 234));
            showCosts.setForeground(Color.WHITE);
            JScrollPane scrollPane = new JScrollPane(showCosts);
            scrollPane.add(costArea);


            StringBuilder s = new StringBuilder("Costs: [");
            String costs = String.valueOf(s).substring(0, s.length() - 2) + "]";
            costArea.setText(costs);
            showCosts.addActionListener(e -> {

                    Set<Node>  treeCosts=op.physiquesArbre(tree);
                    createWindowWithPanels(treeCosts,tree,op.getEstimator());
            });
            JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 5));
            labelPanel.add(ruleLabel);
            labelPanel.add(costLabel);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(treeLabel, BorderLayout.NORTH);
            topPanel.add(labelPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(scrollPane, BorderLayout.CENTER);

            JPanel treePanel = new JPanel(new BorderLayout());
            treePanel.setBorder(BorderFactory.createLineBorder(new Color(102, 102, 102), 1));
            treePanel.add(topPanel, BorderLayout.NORTH);
            treePanel.add(subPanel, BorderLayout.CENTER);
            treePanel.add(bottomPanel, BorderLayout.SOUTH);

            panel.add(treePanel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(40);
        return scrollPane;
    }


    public static void createWindowWithPanels(Set<Node>  trees,Node tr,Estimator es) {
        // Création de la fenêtre principale
        JFrame frame = new JFrame("Physical trees   ");

        // Définition de la taille et de la position de la fenêtre
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Création du panneau principal qui contiendra tous les autres panneaux
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1));
        int i = 1;
        for (Node tree :trees){
        // Création des panneaux avec titre et champ de saisie
        JPanel panel1 = createPanelWithTitleAndInput(es.uniCosts(tree,tr),tree,i);
        i++;

        // Ajout des panneaux au panneau principal
        mainPanel.add(panel1);
    }

        // Ajout du panneau principal à une barre de défilement verticale
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Ajout de la barre de défilement à la fenêtre principale
        frame.add(scrollPane, BorderLayout.CENTER);

        // Affichage de la fenêtre principale
        frame.setVisible(true);
    }


    private static JPanel createPanelWithTitleAndInput(String cost,Node tree,int i) {
        // Création du panneau avec un titre et un champ de saisie
        Visualizer subPanel = new Visualizer(tree,Color.DARK_GRAY);

        JLabel treeLabel = new JLabel(" Physical Tree " + (i + 1)+cost);
        treeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        treeLabel.setForeground(Color.WHITE);
        treeLabel.setOpaque(true);
        treeLabel.setBackground(Color.lightGray);
        treeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(treeLabel, BorderLayout.NORTH);
        topPanel.add(treeLabel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBorder(BorderFactory.createLineBorder(new Color(102, 102, 102), 1));
        treePanel.add(topPanel, BorderLayout.NORTH);
        treePanel.add(subPanel, BorderLayout.CENTER);
        treePanel.add(bottomPanel, BorderLayout.SOUTH);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(treePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));




        treePanel.add(topPanel, BorderLayout.NORTH);
        treePanel.add(subPanel, BorderLayout.CENTER);
        treePanel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }
    private static JPanel drawnTree(Node node,double cost,String title,Color col){

        Visualizer mainsubPanel = new Visualizer(node,col);
        JPanel maintopPanel = new JPanel(new BorderLayout());

        // Create labels
        JLabel maintreeLabel = new JLabel(title);
        JTextArea maindescrArea = new JTextArea();
        maindescrArea.setEditable(false);
        maindescrArea.setLineWrap(true);
        maindescrArea.setWrapStyleWord(true);
        maindescrArea.setFont(new Font("Arial", Font.PLAIN, 14));
        maindescrArea.setForeground(col);
        String text=cost>0?"Cost: "+cost+"ms":"";
        maindescrArea.setText(text);

        maintreeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        maintreeLabel.setForeground(col);
        maintreeLabel.setOpaque(true);
        maintreeLabel.setBackground(Color.white);
        maintreeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));


        maintopPanel.add(maintreeLabel, BorderLayout.NORTH);
        maintopPanel.add(maindescrArea, BorderLayout.SOUTH);


        JPanel maintreePanel = new JPanel(new BorderLayout());
        maintreePanel.setBorder(BorderFactory.createLineBorder(col, 1));
        maintreePanel.add(maintopPanel, BorderLayout.NORTH);
        maintreePanel.add(mainsubPanel, BorderLayout.CENTER);
        return maintreePanel;
    }

}
