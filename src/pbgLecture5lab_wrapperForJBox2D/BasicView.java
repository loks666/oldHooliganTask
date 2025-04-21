package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

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
		Dimension size = new Dimension(800, 600); // 设置画布大小为 800x600
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size); // 直接设置大小

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

		// 2) 绘制障碍物
		for (AnchoredBarrier barrier : game.barriers) {
			barrier.draw(g2);
		}

		// 3) 绘制弹弓
		Slingshot slingshot = game.getSlingshot();
		if (slingshot != null) {
			slingshot.draw(g2);
		}

		// 4) 再绘制物理世界里的各种对象
		for (BasicPolygon poly : game.polygons) {
			poly.draw(g2);
		}

		for (BasicParticle p : game.particles) {
			p.draw(g2);
		}

		// 5) 绘制连接器
		for (ElasticConnector connector : game.connectors) {
			connector.draw(g2);
		}
	}

	public void addKeyListener(KeyListener listener) {
		super.addKeyListener(listener);
	}

	public void addMouseMotionListener(MouseMotionListener listener) {
		super.addMouseMotionListener(listener);
	}

	public void addMouseListener(java.awt.event.MouseListener listener) {
		super.addMouseListener(listener);
	}

	public void updateGame(BasicPhysicsEngineUsingBox2D newGame) {
		this.game = newGame; // 更新游戏对象
		this.repaint(); // 更新视图
	}
}
