package com.cookbook.samples.desktop;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.cookbook.samples.GdxSamples;
import com.cookbook.samples.desktop.SampleList.SampleLauncher;

public class SwingCanvasSample extends JFrame implements SampleLauncher {
	LwjglAWTCanvas canvas;
	SampleList list;
	
	public SwingCanvasSample() {
		list = new SampleList(this);
		list.setSize(320, 540);
		
		Container container = getContentPane();
		container.add(list, BorderLayout.WEST);
		
		setSize(1280, 540);
		setVisible(true);
		setResizable(false);
		setTitle("Libgdx Game Development Cookbook Samples");
		
		addWindowListener(new WindowAdapter() {
			public void windowClosed (WindowEvent event) {
				System.exit(0);
			}
		});
	}

	@Override
	public boolean launchSample(String sampleName) {
		Container container = getContentPane();
		
		if (canvas != null) {
			container.remove(canvas.getCanvas());
		}
		
		ApplicationListener sample = GdxSamples.newSample(sampleName);
		
		canvas = new LwjglAWTCanvas(sample);
		canvas.getCanvas().setSize(960, 540);
		container.add(canvas.getCanvas(), BorderLayout.EAST);
		
		pack();
		
		return sample != null;
	}
	
	public static void main (String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run () {
				new SwingCanvasSample();
			}
		});
	}
}
