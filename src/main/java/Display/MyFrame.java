package Display;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathCanvas;

import javax.swing.*;

public class MyFrame extends JFrame {
    /**
     * MathCanvas示例
     * @param kernelLink 链接接口
     * @param command 需要显示的Wolfram脚本指令
     */
    public MyFrame(KernelLink kernelLink, String command){
        this.setLayout(null);
        this.setSize(500, 500);

        MathCanvas mathCanvas = new MathCanvas(kernelLink);
        mathCanvas.setBounds(0, 0, 500, 500);
        this.add(mathCanvas);
        mathCanvas.setMathCommand(command);

        this.setVisible(true);
    }
}