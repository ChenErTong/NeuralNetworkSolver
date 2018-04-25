package Display;

import Tool.FileProcesser;
import Tool.Solution;
import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public abstract class Graphics extends JFrame {

    private KernelLink kernelLink;
    private MathCanvas mathCanvas;
    private final static int WINDOW_HEIGHT = 600;
    private final static int PADDING = 10;
    private String formula_prefix;
    private String formula_postfix;
    protected String[] formula_infix;
    protected String[] objectives;

    int WINDOW_WIDTH;

    Graphics(KernelLink kl, int[][] range) {
        formula_prefix = constructPrefix();
        formula_postfix = constructPostfix(range);
        kernelLink = kl;
    }

    void init(List<Solution> solutions){
        preprocess(solutions);

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
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setListData(objectives);
        jList.addListSelectionListener(e -> {
            if(e.getValueIsAdjusting()){
                setMathCommand(formula_infix[jList.getSelectedIndex()]);
            }
        });
        jList.setSelectedIndex(0);
        jList.setBounds(WINDOW_HEIGHT, PADDING, WINDOW_WIDTH - WINDOW_HEIGHT - PADDING * 2, WINDOW_HEIGHT - PADDING * 6);
        add(jList);
        setMathCommand(formula_infix[0]);

        kernelLink.evaluateToInputForm("Needs[\"" + KernelLink.PACKAGE_CONTEXT + "\"]", 0);
        kernelLink.evaluateToInputForm("ConnectToFrontEnd[]", 0);

        setVisible(true);
        toFront();
    }

    protected abstract String constructPostfix(int[][] formula_postfix);

    protected abstract String constructPrefix();

    protected abstract void preprocess(List<Solution> solutions);

    private void setMathCommand(String command){
        mathCanvas.setMathCommand(convertToRegionPlotFormula(command));
    }

    private String convertToRegionPlotFormula(String formula){
        String command = formula_prefix + formula + formula_postfix;
        FileProcesser.writeToFile( "\n" + command + "\n");
        return command;
    }
}