import java.awt.Container;

import javax.swing.JFrame;

public class RollCakeRSS extends JFrame {
	public static String FRAME_TITLE = "RollCakeRSS";
	FeedManager fm;

	public static void main(String... args) {
		System.out.println("main start");

		RollCakeRSS rcake = new RollCakeRSS();

		rcake.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rcake.setBounds(10, 10, 300, 200);
		rcake.setTitle(FRAME_TITLE);
		rcake.setVisible(true);

		System.out.println("main end");
	}

	RollCakeRSS() {
		//		Container cp = this.getContentPane();

		this.fm = new FeedManager();
		for (String url : Debug.DEBUG_URLS) {
			this.fm.addFeed(url, null);
		}
		this.updateTable();
		//		this.fm._consoleOutput();
	}

	public void updateTable() {
		Container cp = this.getContentPane();
		cp.add(this.fm.getPanel());
	}
}

