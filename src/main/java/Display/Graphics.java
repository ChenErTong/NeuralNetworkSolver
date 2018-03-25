package Display;

import com.wolfram.jlink.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public abstract class Graphics extends JFrame {

    private KernelLink kernelLink;
    private MathCanvas mathCanvas;
    private final static int WINDOW_HEIGHT = 600;
    private final static int PADDING = 10;
    private String formula_prefix;
    private String formula_postfix;

    int WINDOW_WIDTH;

    Graphics(KernelLink kl, int[][] range) {
        formula_prefix = constructPrefix();
        formula_postfix = constructPostfix(range);
        kernelLink = kl;
    }

    void init(Map<String, String> solutions){
        String[] objectives = new String[solutions.size()];
        int index = 0;
        for (String objective: solutions.keySet()) {
            objectives[index++] = objective;
        }

        setLayout(null);
        setTitle("Neural Network Parser");
        setBackground(Color.white);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
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
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        setLocation((int)(toolkit.getScreenSize().getWidth() - WINDOW_WIDTH)/2, (int)(toolkit.getScreenSize().getHeight() - WINDOW_HEIGHT)/2);

        mathCanvas = new MathCanvas(kernelLink);
        mathCanvas.setBackground(Color.white);
        mathCanvas.setBounds(PADDING, PADDING, WINDOW_HEIGHT - PADDING * 2, WINDOW_HEIGHT - PADDING * 6);
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
        jList.setBounds(WINDOW_HEIGHT, PADDING, WINDOW_WIDTH - WINDOW_HEIGHT - PADDING * 2, WINDOW_HEIGHT - PADDING * 6);
        add(jList);
        setMathCommand(solutions.get(objectives[0]));

        kernelLink.evaluateToInputForm("Needs[\"" + KernelLink.PACKAGE_CONTEXT + "\"]", 0);
        kernelLink.evaluateToInputForm("ConnectToFrontEnd[]", 0);

        setVisible(true);
        toFront();
    }

    protected abstract String constructPostfix(int[][] formula_postfix);

    protected abstract String constructPrefix();

    private void setMathCommand(String command){
        mathCanvas.setMathCommand(convertToRegionPlotFormula(command));
    }

    private String convertToRegionPlotFormula(String formula){
        return formula_prefix + formula + formula_postfix;
    }
}