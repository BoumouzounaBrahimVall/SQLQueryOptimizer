package org.QueryOptimizer.a;
import org.QueryOptimizer.Node;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NodeGUI extends JFrame {

    public NodeGUI(List<Node> l) {
        super("Nodes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(500, 500);

        JPanel contentPane = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(contentPane);

        JPanel panelNodes = new JPanel(new GridLayout(0, 1));
        for(Node n: l){
            NodePanel NodePanel = new NodePanel(n);
            panelNodes.add(NodePanel);
        }

        contentPane.add(panelNodes, BorderLayout.NORTH);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }



}

class NodePanel extends JPanel {

    private final Node racine;

    public NodePanel(Node rr) {
        this.racine = rr;
        setPreferredSize(new Dimension(1500, 500));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        int x = getWidth() / 2, y = 50;
        if (racine.getData().contains("π")) {
            g.drawString(racine.getData(), x - 12, y + 12);

            if (racine.getLeft() != null) {
                g.drawLine(x + 5, y + 20, x + 5, y + 40);
                dessinerNode(racine.getLeft(), x, y + 40, g);
            }
        } else dessinerNode(racine, x - 12, y + 12, g);


    }

    private void dessinerNode(Node Node, int x, int y, Graphics g) {
        //g.drawRect(x - 15, y - 15, 30, 30);
        g.drawString(Node.getData(), x - 12, y + 12);
        // g.drawOval(x-30 ,y-5 ,120,30);
        if (Node.getLeft() != null) {
            int x1 = x - 15;
            int y1 = y + 15;
            int x2 = (int) (x1 - Math.pow(2, getHeight(Node.getLeft())) * 10 + 0.5);
            int y2 = y + 45;
            g.drawLine(x1, y1, x2, y2);
            dessinerNode(Node.getLeft(), x2, y2, g);
        }
        if (Node.getRight() != null) {
            int x1 = x + 15;
            int y1 = y + 15;
            int x2 = (int) (x1 + Math.pow(2, getHeight(Node.getRight())) * 10 + 0.5);
            int y2 = y + 45;
            g.drawLine(x1, y1, x2, y2);
            dessinerNode(Node.getRight(), x2, y2, g);
        }
    }

    private int getHeight(Node Node) {
        if (Node == null) {
            return 0;
        }
        return Math.max(getHeight(Node.getLeft()), getHeight(Node.getRight())) + 1;
    }
/*
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawNode(g, getWidth() / 2, 50, Node);
    }

    private void drawNode(Graphics g, int x, int y, Node Node) {
        if (Node == null) {
            return;
        }

        g.drawString(Node.getData(), x, y);

        if (Node.getLeft() != null) {
            int xGauche = x - 50;
            int yGauche = y + 50;
            g.drawLine(x, y, xGauche, yGauche);
            drawNode(g, xGauche, yGauche, Node.getLeft());
        }

        if (Node.getRight() != null) {
            int xDroit = x + 50;
            int yDroit = y + 50;
            g.drawLine(x, y, xDroit, yDroit);
            drawNode(g, xDroit, yDroit, Node.getRight());
        }
    }


 */
}

