import com.wolfram.jlink.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphicsApp extends JFrame {
    private KernelLink kernelLink;
    private MathCanvas mathCanvas;

    private final int X = 200;
    private final int Y = 100;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    public GraphicsApp(KernelLink kl) {
        kernelLink = kl;

        setLayout(null);
        setTitle("Neural Network Parser");
        mathCanvas = new MathCanvas(kernelLink);
        add(mathCanvas);
        mathCanvas.setBackground(Color.white);

        setSize(WIDTH, HEIGHT);
        setLocation(X, Y);
        mathCanvas.setBounds(10, 10, WIDTH - 20, HEIGHT - 20);

        addWindowListener(new WnAdptr());
        setBackground(Color.white);
        setResizable(false);

        // Although this code would automatically be called in
        // evaluateToImage or evaluateToTypeset, it can cause the
        // front end window to come in front of this Java window.
        // Thus, it is best to get it out of the way at the start
        // and call toFront to put this window back in front.
        // KernelLink.PACKAGE_CONTEXT is just "JLink`", but it is
        // preferable to use this symbolic constant instead of
        // hard-coding the package context.
        kernelLink.evaluateToInputForm("Needs[\"" + KernelLink.PACKAGE_CONTEXT + "\"]", 0);
        kernelLink.evaluateToInputForm("ConnectToFrontEnd[]", 0);

        setVisible(true);
        toFront();
    }

    public void setMathCommand(String command){
        mathCanvas.setMathCommand(command);
    }

    class WnAdptr extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            if (kernelLink != null) {
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
                kernelLink.evaluateToInputForm("CloseFrontEnd[]", 0);
                kernelLink.close();
            }
            dispose();
            System.exit(0);
        }
    }
}
