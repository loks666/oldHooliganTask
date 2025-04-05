package pbgLecture5lab_wrapperForJBox2D;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JFrame;

public class JEasyFrame extends JFrame {
	/* Author: Norbert Voelker
	 */
	public Component comp;

	public JEasyFrame(Component comp, String title) {
		super(title);
		this.comp = comp;
		getContentPane().add(BorderLayout.CENTER, comp);
		setResizable(false);  // 禁止调整窗口大小
		pack();
		setLocationRelativeTo(null);  // 使窗口在屏幕中央显示
		setExtendedState(JFrame.NORMAL);  // 确保窗口以正常大小显示
		this.setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		repaint();
	}
}
