package Display;

import com.wolfram.jlink.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public abstract class Graphics extends JFrame {
    private KernelLink kernelLink;
    private MathCanvas mathCanvas;

    private final int X = 200;
    private final int Y = 100;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private String formula_postfix;

    Graphics(Map<String, String> solutions, KernelLink kl, int[][] range) {
        formula_postfix = constructPostfix(range);
        kernelLink = kl;
        String[] objectives = new String[solutions.size()];
        int index = 0;
        for (String objective: solutions.keySet()) {
            objectives[index++] = objective;
        }

        setLayout(null);
        setTitle("Neural Network Parser");
        setBackground(Color.white);
        setLocation(X, Y);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (kernelLink != null) {
                    kernelLink.evaluateToInputForm("CloseFrontEnd[]", 0);
                    kernelLink.close();
                }
                dispose();
                System.exit(0);
            }
        });

        mathCanvas = new MathCanvas(kernelLink);
        mathCanvas.setBackground(Color.white);
        mathCanvas.setBounds(10, 10, WIDTH - 20, HEIGHT - 20);
        add(mathCanvas);

        JList<String> jList = new JList<>();
        jList.setPreferredSize(new Dimension(200, 100));
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setListData(objectives);
        jList.addListSelectionListener(e -> {
            if(e.getValueIsAdjusting()){
                int index1 = jList.getSelectedIndex();
                ListModel<String> listModel = jList.getModel();
                setMathCommand(solutions.get(listModel.getElementAt(index1)));
            }
        });
        jList.setSelectedIndex(0);
        add(jList);
        setMathCommand(solutions.get(objectives[0]));

        kernelLink.evaluateToInputForm("Needs[\"" + KernelLink.PACKAGE_CONTEXT + "\"]", 0);
        kernelLink.evaluateToInputForm("ConnectToFrontEnd[]", 0);

        setVisible(true);
        toFront();
    }

    protected abstract String constructPostfix(int[][] formula_postfix);

    private void setMathCommand(String command){
        mathCanvas.setMathCommand(convertToRegionPlotFormula(command));
    }

    private String convertToRegionPlotFormula(String formula){
        return "RegionPlot[" + formula + formula_postfix;
    }
}