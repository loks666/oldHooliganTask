package pbgLecture5lab_wrapperForJBox2D;


import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BasicView extends JComponent {
	private BasicPhysicsEngineUsingBox2D game;
	private BufferedImage backgroundImg;

	public BasicView(BasicPhysicsEngineUsingBox2D game) {
		this.game = game;
		try {
			// 加载背景图片，使用相对路径
			backgroundImg = ImageIO.read(new File("resources/imgs/background/background.png"));
		} catch (IOException e) {
			System.err.println("无法加载背景图片：" + e.getMessage());
		}
		
		// 设置固定大小的画布
		Dimension size = new Dimension(800, 600);  // 设置画布大小为 800x600
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		setOpaque(true);
		setBackground(Color.BLACK); // 如果背景图加载失败，使用黑色背景
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// 1) 先绘制背景
		if (backgroundImg != null) {
			// 拉伸或平铺至整个窗口
			g2.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
		} else {
			// 如果背景图没加载到，则用纯色底色
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, getWidth(), getHeight());
		}

		// 2) 再绘制物理世界里的各种对象
		for (BasicParticle p : game.particles) {
			p.draw(g2);
		}
		for (BasicPolygon poly : game.polygons) {
			poly.draw(g2);
		}
		// 如果有其他对象（比如小猪、弹弓等），也要在这里依次绘制
		// ...
	}

	public void addKeyListener(KeyListener listener) {
		super.addKeyListener(listener);
	}

	public void addMouseMotionListener(MouseMotionListener listener) {
		super.addMouseMotionListener(listener);
	}

	public void updateGame(BasicPhysicsEngineUsingBox2D newGame) {
		this.game = newGame;  // 更新游戏对象
		this.repaint();  // 更新视图
	}
}
