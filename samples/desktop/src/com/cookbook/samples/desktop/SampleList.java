package com.cookbook.samples.desktop;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglPreferences;
import com.badlogic.gdx.files.FileHandle;
import com.cookbook.samples.GdxSamples;

class SampleList extends JPanel {
	
	public interface SampleLauncher {
		boolean launchSample(String sampleName);
	}
	
	private SampleLauncher testLauncher;
	
	public SampleList (SampleLauncher launcher) {
		testLauncher = launcher;
		
		setLayout(new BorderLayout());

		final JButton button = new JButton("Run Sample");

		final JList list = new JList(GdxSamples.getNames().toArray());
		JScrollPane pane = new JScrollPane(list);

		DefaultListSelectionModel m = new DefaultListSelectionModel();
		m.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m.setLeadAnchorNotificationEnabled(false);
		list.setSelectionModel(m);

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked (MouseEvent event) {
				if (event.getClickCount() == 2) button.doClick();
			}
		});

		list.addKeyListener(new KeyAdapter() {
			public void keyPressed (KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) button.doClick();
			}
		});

		final Preferences prefs = new LwjglPreferences(new FileHandle(new LwjglFiles().getExternalStoragePath()
			+ ".prefs/libgdxCookbookSamples"));
		list.setSelectedValue(prefs.getString("last", null), true);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				String testName = (String)list.getSelectedValue();
				prefs.putString("last", testName);
				prefs.flush();
				testLauncher.launchSample((testName));
			}
		});

		add(pane, BorderLayout.CENTER);
		add(button, BorderLayout.SOUTH);
	}
}