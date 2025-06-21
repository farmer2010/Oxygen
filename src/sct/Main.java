package sct;
import java.io.File;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Main{
	public static void main(String[] args) {
		new File("record/predators-oxygen").mkdirs();
		new File("record/energy").mkdirs();
		new File("record/color").mkdirs();
		new File("record/predators-org").mkdirs();
		new File("record/predators-co2").mkdirs();
		//
		new File("saved worlds").mkdirs();
		//
		JFrame frame = new JFrame("Oxygen");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new World());
		Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
		frame.setSize(screenSize);
        //frame.setSize(720, 405);
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setVisible(true);
	}
}