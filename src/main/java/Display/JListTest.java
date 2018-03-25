package Display;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class JListTest {

    public static void main(String[] args) {
        JFrame jf = new JFrame("测试窗口");
        jf.setSize(300, 300);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();

        // 创建一个 JList 实例
        JList<String> list = new JList<String>();

        // 设置一下首选大小
        list.setPreferredSize(new Dimension(200, 100));

        // 允许可间断的多选
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 设置选项数据（内部将自动封装成 ListModel ）
        list.setListData(new String[]{"香蕉", "雪梨", "苹果", "荔枝"});

        // 添加选项选中状态被改变的监听器
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()){
                    // 获取所有被选中的选项索引
                    int index = list.getSelectedIndex();
                    // 获取选项数据的 ListModel
                    ListModel<String> listModel = list.getModel();
                    // 输出选中的选项
                    System.out.println("选中: " + index + " = " + listModel.getElementAt(index));
                }
            }
        });

        // 设置默认选中项
        list.setSelectedIndex(0);

        // 添加到内容面板容器
        panel.add(list);

        jf.setContentPane(panel);
        jf.setVisible(true);
    }
}