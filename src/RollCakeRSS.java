import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class RollCakeRSS extends JFrame {
	RCManager manager;
	RightPanel rightPane;
	HomePanel homePane;
	DefaultListModel<String> feedListModel;
	JScrollPane homeWrap;
	JComboBox<String> groupBox;
	SettingPanel settingPane;

	public static void main(String... args) {
		System.out.println("main start");

		RollCakeRSS cake = new RollCakeRSS();

		cake.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cake.setBounds(10, 10, RCConfig.window_size_width, RCConfig.window_size_height);
		cake.setTitle(RCConfig.title_frame);
		cake.setVisible(true);

		System.out.println("main end");

		// ------------------- play -------------------//
		// ------------------- play end -------------------//

	}

	RollCakeRSS() {
		// ------------------- construct model -------------------//
		this.manager = new RCManager();
		this.manager.load();

		// ------------------- gui putting -------------------//
		JPanel pane = (JPanel) this.getContentPane();
		pane.setLayout(new BorderLayout());
		homePane = new HomePanel(manager.getActiveGroup());
		homeWrap = new JScrollPane(homePane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		homeWrap.getVerticalScrollBar().setUnitIncrement(20);
		rightPane = new RightPanel();
		pane.add(homeWrap, BorderLayout.CENTER);
		pane.add(rightPane, BorderLayout.EAST);
		settingPane = new SettingPanel(manager);

	}

	//	private void setupWindowConfig() throws Exception {
	//		UIManager.setLookAndFeel(UIManager.getInstalledLookAndFeels()[RCConfig.window_id_lookandfeel].getClassName());
	//	}
	//
	//	@SuppressWarnings("unused")
	//	@Deprecated
	//	private void setupToolBar() {
	//		JToolBar tb = new JToolBar();
	//	}

	//	@SuppressWarnings("unused")
	//	private void setupMenuBar() {
	//		JMenuBar menuBar = new JMenuBar();
	//		this.setJMenuBar(menuBar);
	//
	//		JMenu file = new JMenu("File");
	//		menuBar.add(file);
	//		JMenuItem itemExit = new JMenuItem("Exit");
	//		file.add(itemExit);
	//	}

	public void reloadAll() {
		JPanel pane = (JPanel) this.getContentPane();
		pane.removeAll();
		homePane = null;
		homePane = new HomePanel(manager.getActiveGroup());
		homeWrap = new JScrollPane(homePane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		homeWrap.getVerticalScrollBar().setUnitIncrement(20);
		rightPane = new RightPanel();
		pane.add(homeWrap, BorderLayout.CENTER);
		pane.add(rightPane, BorderLayout.EAST);
		this.setVisible(false);
		this.setVisible(true);
	}

	/*
	 * ---------------------------------------------------------
	 * RightPanel
	 * ---------------------------------------------------------
	 */
	class RightPanel extends JPanel {
		public JPanel feedListPane;
		public JPanel crawlButtons;
		public JPanel configButtons;

		public RightPanel() {
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			// ------------------- layoutButtons -------------------//
			crawlButtons = new JPanel(new GridLayout(1, 3));
			JButton crawlButton = new JButton("Crawl");
			JButton crawlOffButton = new JButton("Off");
			crawlButtons.add(crawlButton);
			crawlButtons.add(crawlOffButton);
			this.add(crawlButtons);
			crawlOffButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					homePane.closeFind();
					homePane.setVisible(false);
					homePane.setVisible(true);
				}
			});
			crawlButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final JTextField tf = new JTextField("");
					ArrayList<String> h = manager.getCrawledHistory();
					String[] vs = new String[h.size()];
					for (int i = 0; i < vs.length; i++)
						vs[i] = h.get(i);
					final JList<String> list = new JList<String>(vs);
					list.addListSelectionListener(new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent e) {
							if (!e.getValueIsAdjusting())
								return;
							tf.setText(list.getSelectedValue());
						}
					});
					Object[] o = {
							"探索する正規表現", tf, new JScrollPane(list)
					};
					int ans = JOptionPane.showConfirmDialog(rightPane, o, "探索", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (ans == JOptionPane.OK_OPTION) {
						String regex = tf.getText();
						homePane.crawl(regex);
						System.out.println("crawled[" + regex);
						manager.addCrawledHistory(regex);
						homePane.setVisible(false);
						homePane.setVisible(true);
						manager.save();
					}
				}
			});

			// ------------------- groupBox -------------------//
			String[] nameList = new String[manager.groupNameList().size()];
			for (RCGroup group : manager.getGroupList())
				nameList[group.getId()] = group.getName();
			groupBox = new JComboBox<String>(nameList);
			groupBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					changeGroup(groupBox.getSelectedIndex());
				}
			});
			this.add(groupBox);

			// ------------------- feedList -------------------//
			feedListPane = new JPanel();
			this.add(feedListPane);

			configButtons = new JPanel(new GridLayout(1, 2));
			JButton addBtn = new JButton("Add");
			JButton confBtn = new JButton("Config");

			// ------------------- button actions -------------------//
			addBtn.addActionListener(new callSettingPaneAction(0));
			confBtn.addActionListener(new callSettingPaneAction(1));
			configButtons.add(addBtn);
			configButtons.add(confBtn);
			this.add(configButtons);

			this.changeGroup(0);
		}

		public void setupFeel() {
			this.setPreferredSize(RCConfig.rightpane_size_dimension);
			this.setBackground(RCConfig.rightpane_background_color);
			crawlButtons.setMaximumSize(RCConfig.layout_button_box);
			crawlButtons.setMinimumSize(RCConfig.layout_button_box);
			crawlButtons.setBorder(RCConfig.margin_border);
			crawlButtons.setBackground(RCConfig.rightpane_background_color);
			((GridLayout) crawlButtons.getLayout()).setHgap(5);
			((GridLayout) crawlButtons.getLayout()).setVgap(5);
			for (Component button : crawlButtons.getComponents()) {
				if (button instanceof JButton) {
					button.setBackground(RCConfig.button_back_color);
				}
			}

			groupBox.setMaximumSize(RCConfig.group_comb);
			groupBox.setMinimumSize(RCConfig.group_comb);
			groupBox.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
			groupBox.setBackground(RCConfig.rightpane_background_color);

			feedListPane.setBackground(RCConfig.rightpane_background_color);
			feedListPane.setBorder(RCConfig.margin_border);
			for (Component button : feedListPane.getComponents()) {
				if (button instanceof JToggleButton) {
					button.setBackground(RCConfig.button_back_color);
					button.setPreferredSize(RCConfig.feedlist_button_size);
				}
			}

			configButtons.setMaximumSize(RCConfig.layout_button_box);
			configButtons.setMinimumSize(RCConfig.layout_button_box);
			configButtons.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
			configButtons.setBackground(RCConfig.rightpane_background_color);
			((GridLayout) configButtons.getLayout()).setHgap(5);
			((GridLayout) configButtons.getLayout()).setVgap(5);
			for (Component button : configButtons.getComponents()) {
				if (button instanceof JButton) {
					button.setBackground(RCConfig.button_back_color);
				}
			}
		}

		public void changeGroup(int groupId) {
			if (groupId == -1)
				return;
			ArrayList<RCGroup> groupList = manager.getGroupList();
			RCGroup group = groupList.get(groupId);
			feedListPane.removeAll();
			for (RCFeed feed : group.getFeedList()) {
				//				System.out.println(":" + feed.getName());
				JToggleButton tb = new JToggleButton(feed.getName());
				tb.setSelected(true);
				tb.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Object o = e.getSource();
						if (!(o instanceof JToggleButton))
							return;
						JToggleButton tb = (JToggleButton) o;
						int i = 0;
						for (Component c : feedListPane.getComponents()) {
							if (c.equals(tb))
								break;
							i++;
						}
						System.out.println(i + ": -" + tb.isSelected());
						homePane.changeItem(i, tb.isSelected());
					}
				});
				feedListPane.add(tb);
			}
			//			JViewport v = homeWrap.getViewport();
			homePane.setGroup(group);

			setupFeel();
			setVisible(false);
			setVisible(true);
		}
	}

	class callSettingPaneAction extends AbstractAction {
		int initIndex;

		public callSettingPaneAction(int initIndex) {
			this.initIndex = initIndex;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			settingPane.setSelectedIndex(initIndex);
			int ans = JOptionPane.showConfirmDialog(rightPane, settingPane, "設定", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (ans == JOptionPane.OK_OPTION) {
				System.out.println("--reload");
				reloadAll();
				manager.save();
			}
		}
	}
}
