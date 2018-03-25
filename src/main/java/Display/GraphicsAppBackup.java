package Display;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GraphicsAppBackup extends JFrame {
    private static String path = "E:\\Wolfram Research\\Mathematica\\11.2\\mathkernel.exe";

    static GraphicsAppBackup app;
    static KernelLink ml;

//    MathCanvas mathCanvas;
    JMenuBar jMenuBar;
    JMenu jMenu;

    public static void main(String[] argv) {
        try {
            ml = connect();
            ml.discardAnswer();
        } catch (MathLinkException e) {
            System.out.println("An error occurred connecting to the kernel.");
            if (ml != null)
                ml.close();
            return;
        }
        app = new GraphicsAppBackup();

        try {
            ml.evaluate("RegionPlot[x^2 + y^3 < 2, {x, -2, 2}, {y, -2, 2}]");
        } catch (MathLinkException e) {
            System.out.println("An error occurred connecting to the kernel.");
            if (ml != null)
                ml.close();
            return;
        }
    }

    public GraphicsAppBackup() {
        setLayout(null);
        setTitle("Display.Graphics App");
//        mathCanvas = new MathCanvas(ml);
//        add(mathCanvas);
//        mathCanvas.setMathCommand("RegionPlot[x^2 + y^3 < 2, {x, -2, 2}, {y, -2, 2}]");
//        mathCanvas.setBackground(Color.white);


        setSize(300, 400);
        setLocation(100,100);
//        mathCanvas.setBounds(10, 25, 280, 240);

        jMenuBar = new JMenuBar();
        jMenu = new JMenu("颜色");
        JMenuItem jmt1=new JMenuItem("红色"),
                jmt2=new JMenuItem("黄色"),
                jmt3=new JMenuItem("蓝色");
        jMenuBar.setBounds(0, 0, 100, 100);

        setJMenuBar(jMenuBar);
        jMenuBar.add(jMenu);
        jMenu.add(jmt1);
        jMenu.add(jmt2);
        jMenu.add(jmt3);

        addWindowListener(new WnAdptr());
        setBackground(Color.lightGray);
        setResizable(false);

        // Although this code would automatically be called in
        // evaluateToImage or evaluateToTypeset, it can cause the
        // front end window to come in front of this Java window.
        // Thus, it is best to get it out of the way at the start
        // and call toFront to put this window back in front.
        // KernelLink.PACKAGE_CONTEXT is just "JLink`", but it is
        // preferable to use this symbolic constant instead of
        // hard-coding the package context.
        ml.evaluateToInputForm("Needs[\"" + KernelLink.PACKAGE_CONTEXT + "\"]", 0);
        ml.evaluateToInputForm("ConnectToFrontEnd[]", 0);

        setVisible(true);
        toFront();
    }

    class WnAdptr extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            if (ml != null) {
                // Because we used the front end, it is important
                // to call CloseFrontEnd[] before closing the link.
                // Counterintuitively, this is not because we want
                // to force the front end to quit, but because we
                // _don't_ want to do this if the user has begun
                // working in the front end session we started.
                // CloseFrontEnd knows how to politely disengage
                // from the front end if necessary. The need for
                // this will go away in future releases of
                // Mathematica.
                ml.evaluateToInputForm("CloseFrontEnd[]", 0);
                ml.close();
            }
            dispose();
            System.exit(0);
        }
    }

    private static KernelLink connect() {
        KernelLink ml = null;
        try {
            ml = MathLinkFactory.createKernelLink("-linkmode launch -linkname '" + path + "'");
        } catch (MathLinkException e) {
            System.out.println("Link could not be created: " + e.getMessage());
        }
        return ml;
    }
}