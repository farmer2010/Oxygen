package sct;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.event.*;
import javax.swing.*;

public class SettingsPanel extends JPanel {
	Timer timer = new Timer(10, new Listener());
	public SettingsPanel() {
		setLayout(null);
		setBackground(new Color(100, 100, 100));
		//
		addJSlider(0, 25, 250, 35, 0, 1500, (int)Constant.org_die_level, 200, true, e -> {Constant.org_die_level = ((JSlider) e.getSource()).getValue();});
		addJSlider(0, 80, 250, 35, 0, 100, Constant.age_minus_coeff, 20, true, e -> {Constant.age_minus_coeff = ((JSlider) e.getSource()).getValue();});
		addJSlider(0, 135, 250, 35, 0, 20, Constant.energy_for_life, 2, true, e -> {Constant.energy_for_life = ((JSlider) e.getSource()).getValue();});
		addJSlider(0, 190, 250, 35, 0, 20, Constant.energy_for_move, 2, true, e -> {Constant.energy_for_move = ((JSlider) e.getSource()).getValue();});
		addJSlider(0, 245, 250, 35, 0, 1000, Constant.energy_for_multiply, 100, true, e -> {Constant.energy_for_multiply = ((JSlider) e.getSource()).getValue();});
		addJSlider(0, 300, 250, 35, 0, 1000, Constant.energy_for_auto_multiply, 100, true, e -> {Constant.energy_for_auto_multiply = ((JSlider) e.getSource()).getValue();});
		addJSlider(0, 355, 250, 35, 0, 3000, Constant.max_age, 500, true, e -> {Constant.max_age = ((JSlider) e.getSource()).getValue();});
		addJSlider(0, 410, 250, 35, 0, 1000, (int)(Constant.org_recycle_coeff * 1000), 100, true, e -> {Constant.org_recycle_coeff = ((JSlider) e.getSource()).getValue() / 1000.0;});
		addJSlider(0, 465, 250, 35, 0, 1000, (int)Constant.pht_coeff, 100, true, e -> {Constant.pht_coeff = ((JSlider) e.getSource()).getValue();});
		for (int i = 0; i < 8; i++) {
			final int a = i;
			addJTextField(25 * i, 520, 20, 20, String.valueOf(Constant.pht_energy_list[i]), e -> {try{Constant.pht_energy_list[a] = Integer.parseInt(((JTextField) e.getSource()).getText());}catch(NumberFormatException ex) {System.out.println("err");}});
		}
		for (int i = 0; i < 9; i++) {
			final int a = i;
			addJTextField(35 * i, 560, 30, 20, String.valueOf(Constant.pht_neighbours_coeff[i]), e -> {try{Constant.pht_neighbours_coeff[a] = Integer.parseInt(((JTextField) e.getSource()).getText());}catch(NumberFormatException ex) {System.out.println("err");}});
		}
		addJSlider(0, 605, 250, 35, 0, 200, (int)(Constant.org_recycle_ox_coeff * 1000000), 50, true, e -> {Constant.org_recycle_ox_coeff = ((JSlider) e.getSource()).getValue() / 1000000.0;});
		addJSlider(0, 660, 250, 35, 0, 1000, (int)(Constant.life_ox_coeff * 100000), 100, true, e -> {Constant.life_ox_coeff = ((JSlider) e.getSource()).getValue() / 100000.0;});
		addJSlider(0, 715, 250, 35, 0, 1000, (int)(Constant.move_ox_coeff * 100000), 100, true, e -> {Constant.move_ox_coeff = ((JSlider) e.getSource()).getValue() / 100000.0;});
		addJSlider(0, 770, 250, 35, 0, 1000, (int)(Constant.attack_ox_coeff * 10000), 100, true, e -> {Constant.attack_ox_coeff = ((JSlider) e.getSource()).getValue() / 10000.0;});
		addJSlider(0, 825, 250, 35, 0, 1000, (int)(Constant.pht_ox_coeff * 100000), 100, true, e -> {Constant.pht_ox_coeff = ((JSlider) e.getSource()).getValue() / 100000.0;});
		addJSlider(0, 880, 250, 35, 0, 1000, (int)(Constant.die_ox_coeff * 10000), 100, true, e -> {Constant.die_ox_coeff = ((JSlider) e.getSource()).getValue() / 10000.0;});
		addJSlider(0, 935, 250, 35, 0, 1000, (int)((1 - Constant.evaporation_ox_coeff) * 1000), 100, true, e -> {Constant.evaporation_ox_coeff = 1 - ((JSlider) e.getSource()).getValue() / 1000.0;});
		addJSlider(0, 990, 250, 35, 0, 1000, (int)((Constant.ox_distribution_min) * 1000), 100, true, e -> {Constant.ox_distribution_min = ((JSlider) e.getSource()).getValue() / 1000.0;});
		//
		addJSlider(300, 25, 250, 35, 0, 1000, (int)(Constant.life_co2_coeff * 100000), 100, true, e -> {Constant.life_co2_coeff = ((JSlider) e.getSource()).getValue() / 100000.0;});
		addJSlider(300, 80, 250, 35, 0, 1000, (int)(Constant.pht_co2_coeff * 100000), 100, true, e -> {Constant.pht_co2_coeff = ((JSlider) e.getSource()).getValue() / 100000.0;});
		addJSlider(300, 135, 250, 35, 0, 1000, (int)(Constant.org_recycle_co2_coeff * 100000), 100, true, e -> {Constant.org_recycle_co2_coeff = ((JSlider) e.getSource()).getValue() / 100000.0;});
		addJSlider(300, 190, 250, 35, 0, 1000, (int)(Constant.die_co2_coeff * 100000), 100, true, e -> {Constant.die_co2_coeff = ((JSlider) e.getSource()).getValue() / 100000.0;});
	}
	public void addJSlider(int x, int y, int w, int h, int min, int max, int start, int spacing, boolean paint_labels, ChangeListener listener) {
		JSlider slider = new JSlider(min, max, start);
		slider.setBounds(x, y, w, h);
		slider.setPaintLabels(paint_labels);
        slider.setMajorTickSpacing(spacing);
        slider.addChangeListener(listener);
		add(slider);
	}
	public void addJTextField(int x, int y, int w, int h, String s, CaretListener listener) {
		JTextField text_field = new JTextField();
		text_field.setBounds(x, y, w, h);
		text_field.setText(s);
		text_field.addCaretListener(listener);
		add(text_field);
	}
	public void paintComponent(Graphics canvas) {
		super.paintComponent(canvas);
		canvas.setColor(new Color(0, 0, 0));
		canvas.setFont(new Font("arial", Font.BOLD, 18));
		canvas.drawString("Organics die level: " + Constant.org_die_level, 0, 20);
		canvas.drawString("Age reduction coeff: " + Constant.age_minus_coeff, 0, 75);
		canvas.drawString("Energy for life: " + Constant.energy_for_life, 0, 130);
		canvas.drawString("Energy for move: " + Constant.energy_for_move, 0, 185);
		canvas.drawString("Energy for multiply: " + Constant.energy_for_multiply, 0, 240);
		canvas.drawString("Energy for auto multiply: " + Constant.energy_for_auto_multiply, 0, 295);
		canvas.drawString("Maximum age: " + Constant.max_age, 0, 350);
		canvas.drawString("Organics recycle coeff: " + Constant.org_recycle_coeff, 0, 405);
		canvas.drawString("Photosynthesis coeff: " + Constant.pht_coeff, 0, 460);
		canvas.drawString("Photosynthesis levels:", 0, 515);
		canvas.drawString("Pht. neighbours coeff:", 0, 555);
		canvas.drawString("Oxygen for org recycle: " + (int)(Constant.org_recycle_ox_coeff * 1000000) + "E-6", 0, 600);
		canvas.drawString("Oxygen for life coeff: " + (int)(Constant.life_ox_coeff * 100000) + "E-5", 0, 655);
		canvas.drawString("Oxygen for move coeff: " + (int)(Constant.move_ox_coeff * 100000) + "E-5", 0, 710);
		canvas.drawString("Oxygen for attack coeff: " + (int)(Constant.attack_ox_coeff * 10000) + "E-4", 0, 765);
		canvas.drawString("Oxygen from pht. coeff: " + (int)(Constant.pht_ox_coeff * 100000) + "E-5", 0, 820);
		canvas.drawString("Oxygen for die coeff: " + (int)(Constant.die_ox_coeff * 10000) + "E-4", 0, 875);
		canvas.drawString("Oxygen evaporation: " + ((int)((1 - Constant.evaporation_ox_coeff) * 1000) / 1000.0), 0, 930);
		canvas.drawString("Minimum ox. for distr.: " + ((int)((Constant.ox_distribution_min) * 1000) / 1000.0), 0, 985);
		//
		canvas.drawString("Co2 from life coeff: " + (int)(Constant.life_co2_coeff * 100000) + "E-5", 300, 20);
		canvas.drawString("Co2 for pht. coeff: " + (int)(Constant.pht_co2_coeff * 100000) + "E-5", 300, 75);
		canvas.drawString("Co2 from org. recycle coeff: " + (int)(Constant.org_recycle_co2_coeff * 100000) + "E-5", 300, 130);
		canvas.drawString("Co2 from die coeff: " + (int)(Constant.die_co2_coeff * 100000) + "E-5", 300, 185);
	}
	private class Listener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			
		}
	}
}