package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import javax.swing.*;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
//
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.awt.Graphics2D;

public class World extends JPanel{
	//массивы, таймер, класс генератора случайных чисел
	ArrayList<Bot> objects;
	Random rand = new Random();
	Timer timer;
	SimplexNoise noise = new SimplexNoise(0);//шум
	Bot[][] Map = new Bot[Constant.world_scale[0]][Constant.world_scale[1]];//мир
	double[][] oxygen_map;//кислород
	double[][] co2_map;//углекислота
	double[][] org_map;//органика
	double[][] mnr_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];//минералы
	int[][] height_map = new int[Constant.world_scale[0]][Constant.world_scale[1]];//карта высот
	//цвета
	Color gray = new Color(100, 100, 100);
	Color black = new Color(0, 0, 0);
	//переменные
	int steps = 0;//количество шагов симуляции
	int b_count = 0;//количество ботов
	int obj_count = 0;//количество объектов
	int org_count = 0;//количество семян
	double count_ox = -1;//количество кислорода(под курсором)
	double count_org = -1;//количество органики(под курсором)
	double count_mnr = -1;//количество минералов(под курсором)
	double count_co2 = -1;//количество углекислоты(под курсором)
	int mouse = 0;//функция мыши
	int draw_type = 0;//режим отрисовки
	int gas_draw_type = 0;//режим отрисовки фона
	int zoom = 0;//увеличение (0 - x1, 1 - x2.5, 2 - x5)
	int[] zoom_disp_pos = {100, 100};
	int delay = 10;//скорость
	//запись, пауза, отрисовка
	boolean pause = false;//пауза
	boolean render = true;//отрисовка
	boolean sh_brain = false;//отрисовка мозга выбранного существа
	boolean rec = false;//запись
	//сохранение/загрузка
	Bot selection = null;
	Bot for_set = null;
	//кнопки
	JButton stop_button = new JButton("Stop");
	JButton save_button = new JButton("Save");
	JButton show_brain_button = new JButton("Show brain");
	JButton render_button = new JButton("Render: on");
	JButton record_button = new JButton("Record: off");
	JSlider skip_slider = new JSlider(1, 10, 2);//сколько шагов пропускать
	//
	JTextField for_load;
	public World() {
		setLayout(null);
		timer = new Timer(delay, new BotListener());
		objects = new ArrayList<Bot>();
		setBackground(new Color(255, 255, 255));
		addMouseListener(new BotListener());
		addMouseMotionListener(new BotListener());
		//
		stop_button.addActionListener(e -> start_stop());
		stop_button.setBounds(Constant.W - 300, 125, 125, 35);
        add(stop_button);
        //
        skip_slider.setBounds(Constant.W - 170, 125, 125, 35);
        skip_slider.setPaintLabels(true);
        skip_slider.setMajorTickSpacing(1);
        add(skip_slider);
        //
        //смена режимов отрисовки
        //
        JButton predators_button = new JButton("Predators");
        predators_button.addActionListener(new change_draw_type(0));
		predators_button.setBounds(Constant.W - 300, 190, 95, 20);
        add(predators_button);
        //
        JButton energy_button = new JButton("Energy");
        energy_button.addActionListener(new change_draw_type(2));
		energy_button.setBounds(Constant.W - 200, 190, 95, 20);
        add(energy_button);
        //
        JButton clan_button = new JButton("Clans");
		clan_button.setBounds(Constant.W - 200, 215, 95, 20);
		clan_button.addActionListener(new change_draw_type(3));
        add(clan_button);
        //
        JButton age_button = new JButton("Age");
        age_button.addActionListener(new change_draw_type(4));
		age_button.setBounds(Constant.W - 100, 190, 95, 20);
        add(age_button);
        //
        JButton color_button = new JButton("Color");
        color_button.addActionListener(new change_draw_type(1));
		color_button.setBounds(Constant.W - 300, 215, 95, 20);
        add(color_button);
        //
        JButton type_button = new JButton("Type");
        type_button.addActionListener(new change_draw_type(5));
        type_button.setBounds(Constant.W - 100, 215, 95, 20);
        add(type_button);
        //
        JButton chain_button = new JButton("Chain");
        chain_button.addActionListener(new change_draw_type(6));
        chain_button.setBounds(Constant.W - 300, 240, 95, 20);
        add(chain_button);
        //
        //смена режимов отрисовки фона
        //
        JButton none_button = new JButton("None");
        none_button.addActionListener(new change_gas_draw_type(0));
        none_button.setBounds(Constant.W - 300, 280, 95, 20);
        add(none_button);
        //
        JButton ox_button = new JButton("Oxygen");
        ox_button.addActionListener(new change_gas_draw_type(1));
        ox_button.setBounds(Constant.W - 200, 280, 95, 20);
        add(ox_button);
        //
        JButton org_button = new JButton("Organics");
        org_button.addActionListener(new change_gas_draw_type(2));
        org_button.setBounds(Constant.W - 100, 280, 95, 20);
        add(org_button);
        //
        JButton mnr_button = new JButton("Minerals");
        mnr_button.addActionListener(new change_gas_draw_type(3));
        mnr_button.setBounds(Constant.W - 300, 305, 95, 20);
        add(mnr_button);
        //
        JButton co2_button = new JButton("Co2");
        co2_button.addActionListener(new change_gas_draw_type(4));
        co2_button.setBounds(Constant.W - 200, 305, 95, 20);
        add(co2_button);
        //
        //смена функции мыши
        //
        JButton select_button = new JButton("Select");
        select_button.addActionListener(new change_mouse(0));
		select_button.setBounds(Constant.W - 300, 505, 95, 20);
        add(select_button);
        //
        JButton set_button = new JButton("Set");
        set_button.addActionListener(new change_mouse(1));
        set_button.setBounds(Constant.W - 200, 505, 95, 20);
        add(set_button);
        //
        JButton remove_button = new JButton("Remove");
        remove_button.addActionListener(new change_mouse(2));
        remove_button.setBounds(Constant.W - 100, 505, 95, 20);
        add(remove_button);
        //
        //сохранение и просмотр мозга
        //
        //save_button.addActionListener(new remove());
        save_button.setBounds(Constant.W - 300, 425, 125, 20);
        save_button.setEnabled(false);
        add(save_button);
        //
        show_brain_button.addActionListener(e -> shbr());
        show_brain_button.setBounds(Constant.W - 170, 425, 125, 20);
        show_brain_button.setEnabled(false);
        add(show_brain_button);
        //
        //поля для ввода(для сохранения/загрузки)
        //
        JTextField for_save = new JTextField();
        for_save.setBounds(Constant.W - 300, 465, 250, 20);
        add(for_save);
        //
        for_load = new JTextField();
        for_load.setBounds(Constant.W - 300, 565, 250, 20);
        add(for_load);
        //
        JButton load_bot_button = new JButton("Load bot");
        //load_bot_button.addActionListener(new load_bot());
        load_bot_button.setBounds(Constant.W - 300, 590, 90, 20);
        add(load_bot_button);
        //
        JButton load_world_button = new JButton("Load world");
        load_world_button.addActionListener(e -> load_world());
        load_world_button.setBounds(Constant.W - 205, 590, 90, 20);
        add(load_world_button);
        //
        JButton save_world_button = new JButton("Save world");
        save_world_button.addActionListener(e -> save_world());
        save_world_button.setBounds(Constant.W - 110, 590, 90, 20);
        add(save_world_button);
        //
        //создать случайную популяцию и очистить мир
        //
        JButton kill_button = new JButton("Kill all");
        kill_button.addActionListener(e -> kill_all());
        kill_button.setBounds(Constant.W - 170, 635, 125, 20);
        add(kill_button);
        //
        JButton new_population_button = new JButton("New population");
        new_population_button.addActionListener(e -> newPopulation());
        new_population_button.setBounds(Constant.W - 300, 635, 125, 20);
        add(new_population_button);
        //
        //вкл/выкл записи и отрисовки
        //
        render_button.addActionListener(e -> rndr());
        render_button.setBounds(Constant.W - 300, 660, 125, 20);
        add(render_button);
        //
        record_button.addActionListener(e -> rcrd());
        record_button.setBounds(Constant.W - 170, 660, 125, 20);
        add(record_button);
        //
        //зум
        //
        JButton x1_button = new JButton("x1");
        x1_button.addActionListener(new change_zoom(0));
        x1_button.setBounds(Constant.W - 300, 705, 95, 20);
        add(x1_button);
        //
        JButton x2_5_button = new JButton("x2.5");
        x2_5_button.addActionListener(new change_zoom(1));
        x2_5_button.setBounds(Constant.W - 200, 705, 95, 20);
        add(x2_5_button);
        //
        JButton x5_button = new JButton("x5");
        x5_button.addActionListener(new change_zoom(2));
        x5_button.setBounds(Constant.W - 100, 705, 95, 20);
        add(x5_button);
        //
        kill_all();//заполняем мир стартовыми ресурсами
		//
		timer.start();
	}
	//
	//ОТРИСОВКА
	//
	public void paintComponent(Graphics canvas) {
		super.paintComponent(canvas);
		canvas.setColor(gray);
		canvas.fillRect(0, 0, Constant.W, Constant.H);
		canvas.setColor(new Color(255, 255, 255));
		canvas.fillRect(0, 0, Constant.world_scale[0] * Constant.size, Constant.world_scale[1] * Constant.size);
		//
		if (render) {//рисуем фон и ботов
			if (gas_draw_type != 0) {
				if (gas_draw_type == 1) {
					Draw.draw_ox(canvas, oxygen_map, org_map, zoom, zoom_disp_pos);
				}else if (gas_draw_type == 2){
					Draw.draw_org(canvas, org_map, zoom, zoom_disp_pos);
				}else if (gas_draw_type == 3) {
					Draw.draw_mnr(canvas, mnr_map);
				}else if (gas_draw_type == 4) {
					Draw.draw_co2(canvas, co2_map, org_map, zoom, zoom_disp_pos);
				}else if (gas_draw_type == 5) {
					Draw.draw_height(canvas, height_map, zoom, zoom_disp_pos);
				}
			}else {
				//draw_height(canvas, zoom);
			}
			for(Bot b: objects) {
				b.Draw(canvas, draw_type, zoom, zoom_disp_pos);
			}
		}
		//
		canvas.setColor(black);//рисуем текст
		canvas.setFont(new Font("arial", Font.BOLD, 18));
		canvas.drawString("Main: ", Constant.W - 300, 20);
		canvas.drawString("version 4 ", Constant.W - 300, 40);
		canvas.drawString("steps: " + String.valueOf(steps), Constant.W - 300, 60);
		canvas.drawString("objects: " + String.valueOf(obj_count) + ", bots: " + String.valueOf(b_count), Constant.W - 300, 80);
		canvas.drawString("render type: " + Constant.draw_type_names[draw_type] + " view", Constant.W - 300, 100);
		canvas.drawString("mouse function: " + Constant.mouse_func_names[mouse], Constant.W - 300, 120);
		canvas.drawString("Render types:", Constant.W - 300, 180);
		canvas.drawString("Background render types:", Constant.W - 300, 275);
		canvas.drawString("Selection:", Constant.W - 300, 340);
		canvas.drawString("enter name:", Constant.W - 300, 460);
		canvas.drawString("Mouse functions:", Constant.W - 300, 500);
		canvas.drawString("Load:", Constant.W - 300, 540);
		canvas.drawString("enter name:", Constant.W - 300, 560);
		canvas.drawString("Controls:", Constant.W - 300, 630);
		canvas.drawString("Zoom:", Constant.W - 300, 700);
		canvas.drawString("Zoom position: " + String.valueOf(zoom_disp_pos[0]) + ", " + String.valueOf(zoom_disp_pos[1]), Constant.W - 300, 740);
		canvas.drawString("Oxygen: " + String.valueOf(count_ox), Constant.W - 300, 760);
		canvas.drawString("Organics: " + String.valueOf(count_org), Constant.W - 300, 780);
		canvas.drawString("Co2: " + String.valueOf(count_co2), Constant.W - 300, 800);
		//
		if (selection != null) {//данные о выбранном боте
			canvas.drawString("energy: " + String.valueOf(selection.energy) + ", : " + String.valueOf(0), Constant.W - 300, 360);
			canvas.drawString("age: " + String.valueOf(selection.age), Constant.W - 300, 380);
			canvas.drawString("position: " + "[" + String.valueOf(selection.xpos) + ", " + String.valueOf(selection.ypos) + "]", Constant.W - 300, 400);
			canvas.drawString("color: " + "(" + String.valueOf(selection.color.getRed()) + ", " + String.valueOf(selection.color.getGreen()) + ", " + String.valueOf(selection.color.getBlue()) + ")", Constant.W - 300, 420);
			canvas.setColor(new Color(90, 90, 90, 90));
			canvas.fillRect(0, 0, Constant.world_scale[0] * Constant.size, Constant.world_scale[1] * Constant.size);
			canvas.setColor(new Color(255, 0, 0));
			int w = Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom];
			int h = Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom];
			if (zoom == 0) {
				canvas.fillRect(selection.xpos * Constant.size, selection.ypos * Constant.size, Constant.size, Constant.size);
			}else {
				canvas.fillRect((selection.xpos - zoom_disp_pos[0] + w / 2) * Constant.zoom_sizes[zoom], (selection.ypos - zoom_disp_pos[1] + h / 2) * Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom]);
			}
		}else {
			canvas.drawString("none", Constant.W - 300, 295);
		}
		//
		if (sh_brain) {//рисуем мозг
			canvas.setColor(new Color(90, 90, 90));
			canvas.fillRect(0, 0, 360, 360);
			canvas.setColor(new Color(128, 128, 128));
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {
					canvas.setColor(new Color(128, 128, 128));
					canvas.fillRect(x * 45, y * 45, 40, 40);
					canvas.setColor(new Color(0, 0, 0));
					canvas.drawString(String.valueOf(selection.commands[x + y * 8]), x * 45 + 20, y * 45 + 20);
				}
			}
		}
	}
	//
	//ЗАПИСЬ
	//
	public void record() {
		//отрисовка
		BufferedImage[] buff = new BufferedImage[4];
		for (int i = 0; i < 4; i++) {
			buff[i] = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = buff[i].createGraphics();
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, 1000, 1000);
			if (i != 1) {
				Draw.draw_ox(g2d, oxygen_map, org_map, zoom, zoom_disp_pos);
			}else {
				Draw.draw_org(g2d, org_map, zoom, zoom_disp_pos);
			}
			for(Bot b: objects) {
				int dt = 0;
				if (i > 1) {
					dt = i - 1;
				}
				b.Draw(g2d, dt, 0, zoom_disp_pos);
			}
			g2d.dispose();
		}
		//сохранение
		try {
			ImageIO.write(buff[0], "png", new File("record/predators-oxygen/screen" + String.valueOf(steps / 25)+ ".png"));
			ImageIO.write(buff[1], "png", new File("record/predators-org/screen" + String.valueOf(steps / 25)+ ".png"));
			ImageIO.write(buff[2], "png", new File("record/color/screen" + String.valueOf(steps / 25)+ ".png"));
			ImageIO.write(buff[3], "png", new File("record/energy/screen" + String.valueOf(steps / 25)+ ".png"));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	//
	//МЫШЬ И ОБНОВЛЕНИЕ МИРА
	//
	private class BotListener extends MouseAdapter implements ActionListener{
		public void mousePressed(MouseEvent e) {
			if (e.getX() < Constant.world_scale[0] * Constant.size && e.getY() < Constant.world_scale[1] * Constant.size) {
				int[] botpos = new int[2];
				int w = Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom];
				int h = Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom];
				if (zoom == 0) {
					botpos[0] = e.getX() / Constant.size;
					botpos[1] = e.getY() / Constant.size;
				}else {
					botpos[0] = e.getX() / Constant.zoom_sizes[zoom] + zoom_disp_pos[0] - w / 2;
					botpos[1] = e.getY() / Constant.zoom_sizes[zoom] + zoom_disp_pos[1] - h / 2;
				}
				count_ox = oxygen_map[botpos[0]][botpos[1]];
				count_org = org_map[botpos[0]][botpos[1]];
				count_mnr = mnr_map[botpos[0]][botpos[1]];
				count_co2 = co2_map[botpos[0]][botpos[1]];
				//
				update_mouse(botpos, 1);
			}else {
				count_ox = -1;
				count_org = -1;
				count_mnr = -1;
				count_co2 = -1;
			}
		}
		//
		public void mouseDragged(MouseEvent e) {
			if (e.getX() < Constant.world_scale[0] * Constant.size && e.getY() < Constant.world_scale[1] * Constant.size) {
				int[] botpos = new int[2];
				int w = Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom];
				int h = Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom];
				if (zoom == 0) {
					botpos[0] = e.getX() / Constant.size;
					botpos[1] = e.getY() / Constant.size;
				}else {
					botpos[0] = e.getX() / Constant.zoom_sizes[zoom] + zoom_disp_pos[0] - w / 2;
					botpos[1] = e.getY() / Constant.zoom_sizes[zoom] + zoom_disp_pos[1] - h / 2;
				}
				count_ox = oxygen_map[botpos[0]][botpos[1]];
				count_org = org_map[botpos[0]][botpos[1]];
				count_mnr = mnr_map[botpos[0]][botpos[1]];
				count_co2 = co2_map[botpos[0]][botpos[1]];
				//
				update_mouse(botpos, 0);
			}else {
				count_ox = -1;
				count_org = -1;
				count_mnr = -1;
				count_co2 = -1;
			}
		}
		//
		public void actionPerformed(ActionEvent e) {
			if (!pause) {
				update();
			}
			//
			ListIterator<Bot> iterator = objects.listIterator();
			while (iterator.hasNext()) {
				Bot next_bot = iterator.next();
				if (next_bot.killed == 1) {
					iterator.remove();
				}
			}
			//
			repaint();
		}
	}
	//шаг симуляции
	public void update() {
		for (int i = 0; i < skip_slider.getValue(); i++) {
			steps++;
			b_count = 0;
			obj_count = 0;
			org_count = 0;
			//
			ListIterator<Bot> bot_iterator = objects.listIterator();
			while (bot_iterator.hasNext()) {
				Bot next_bot = bot_iterator.next();
				next_bot.Update(bot_iterator);
				if (selection != null) {
					if (next_bot.xpos == selection.xpos && next_bot.ypos == selection.ypos) {
						if (next_bot != selection) {
							selection = null;
							save_button.setEnabled(false);
							show_brain_button.setEnabled(false);
							sh_brain = false;
						}
					}
				}
				obj_count++;
				if (next_bot.state != 0) {
					org_count++;
				}else {
					b_count++;
				}
			}
			//
			if (selection != null) {
				if (selection.killed == 1 || Map[selection.xpos][selection.ypos] == null || selection.state != 0){
					selection = null;
					save_button.setEnabled(false);
					show_brain_button.setEnabled(false);
					sh_brain = false;
				}
			}
			//
			WorldUtils.gas(oxygen_map, org_map);
			WorldUtils.gas(co2_map, org_map);
			//WorldUtils.minerals(mnr_map);
			//
			if (rec && steps % 25 == 0) {
				record();
			}
		}
	}
	//
	public void update_mouse(int[] botpos, int select_work) {
		if (mouse == 0 && select_work == 1) {//select
			if (Map[botpos[0]][botpos[1]] != null && Map[botpos[0]][botpos[1]].state == 0) {
				for(Bot b: objects) {
					if (b.xpos == botpos[0] && b.ypos == botpos[1]) {
						selection = b;
						save_button.setEnabled(true);
						show_brain_button.setEnabled(true);
					}
				}
			}else {
				selection = null;
				save_button.setEnabled(false);
				show_brain_button.setEnabled(false);
				sh_brain = false;
			}
			//
			if (zoom == 0) {
				zoom_disp_pos[0] = botpos[0];
				zoom_disp_pos[1] = botpos[1];
			}else {
				int w = Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom];
				int h = Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom];
				zoom_disp_pos[0] = Constant.border(botpos[0], Constant.world_scale[0] - w / 2, w / 2);
				zoom_disp_pos[1] = Constant.border(botpos[1], Constant.world_scale[1] - h / 2, h / 2);
			}
		}else if (mouse == 1) {//set
			if (for_set != null) {
				if (Map[botpos[0]][botpos[1]] == null) {
					objects.add(for_set);
					Map[botpos[0]][botpos[1]] = for_set;
				}
			}
		}else if (mouse == 2){//remove
			if (Map[botpos[0]][botpos[1]] != null) {
				for(Bot b: objects) {
					if (b.xpos == botpos[0] && b.ypos == botpos[1]) {
						b.energy = 0;
						b.killed = 1;
						Map[botpos[0]][botpos[1]] = null;
					}
				}
			}
		}
	}
	//
	//ФУНКЦИИ КНОПОК
	//
	public void newPopulation() {//создать случайную популяцию
		kill_all();
		for (int i = 0; i < Constant.starting_bot_count; i++) {
			while(true){
				int x = rand.nextInt(Constant.world_scale[0]);
				int y = rand.nextInt(Constant.world_scale[1]);
				if (Map[x][y] == null) {
					Bot new_bot = new Bot(
						x,
						y,
						new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)),
						1000,
						0,
						oxygen_map,
						co2_map,
						org_map,
						Map,
						objects
					);
					objects.add(new_bot);
					Map[x][y] = new_bot;
					break;
				}
			}
		}
		repaint();
	}
	//
	public void kill_all() {//очистить мир
		steps = 0;
		objects = new ArrayList<Bot>();
		SimplexNoise noise = new SimplexNoise(rand.nextInt(-1000000000, 1000000000));//шум
		Map = new Bot[Constant.world_scale[0]][Constant.world_scale[1]];
		oxygen_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		co2_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		org_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		mnr_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		//стартовые ресурсы
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				oxygen_map[x][y] = Constant.starting_ox;
				co2_map[x][y] = Constant.starting_co2;
				org_map[x][y] = Constant.starting_org;
				mnr_map[x][y] = 0;
				Map[x][y] = null;
				height_map[x][y] = (int)(noise.sumOctaves(8, x, y, 0.5F, 0.007F, 0, 1000));
			}
		}
	}
	//
	public void shbr() {//отрисовка мозга
		sh_brain = !sh_brain;
		if (pause == false) {
			pause = true;
		}else if (sh_brain == false) {
			pause = false;
		}
	}
	//
	public void rndr() {//отрисовка
		render = !render;
		if (render) {
			render_button.setText("Render: on");
		}else {
			render_button.setText("Render: off");
		}
	}
	//
	public void rcrd() {//запись
		rec = !rec;
		if (rec) {
			record_button.setText("Record: on");
		}else {
			record_button.setText("Record: off");
		}
	}
	//
	public void start_stop() {//пауза
		pause = !pause;
		if (pause) {
			stop_button.setText("Start");
		}else {
			stop_button.setText("Stop");
		}
	}
	//
	public void load_world() {
		try {
            FileReader fileReader = new FileReader("saved worlds/" + for_load.getText() + ".dat");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            bufferedReader.close();
            String[] l = line.split(";");
            //
            steps = Integer.parseInt(l[0]);
            objects = new ArrayList<Bot>();
    		Map = new Bot[Constant.world_scale[0]][Constant.world_scale[1]];
    		//
    		String[] ox = l[1].split(":");
    		for (int i = 0; i < ox.length; i++) {
    			String[] ox_col = ox[i].split("'");
    			for (int j = 0; j < ox_col.length; j++) {
    				oxygen_map[i][j] = Double.parseDouble(ox_col[j]);
    			}
    		}
    		//
    		String[] organics = l[2].split(":");
    		for (int i = 0; i < organics.length; i++) {
    			String[] organics_col = organics[i].split("'");
    			for (int j = 0; j < organics_col.length; j++) {
    				org_map[i][j] = Double.parseDouble(organics_col[j]);
    			}
    		}
    		//
    		String[] co2 = l[3].split(":");
    		for (int i = 0; i < co2.length; i++) {
    			String[] co2_col = co2[i].split("'");
    			for (int j = 0; j < co2_col.length; j++) {
    				co2_map[i][j] = Double.parseDouble(co2_col[j]);
    			}
    		}
    		//
    		String[] bots = l[4].split(":");
    		for (int i = 0; i < bots.length; i++) {
    			String[] bot_data = bots[i].split("'");
    			Bot new_bot = new Bot(
    				Integer.parseInt(bot_data[2]),
    				Integer.parseInt(bot_data[3]),
    				new Color(Integer.parseInt(bot_data[9]), Integer.parseInt(bot_data[10]), Integer.parseInt(bot_data[11])),
    				Double.parseDouble(bot_data[0]),
    				0,
    				oxygen_map,
    				co2_map,
    				org_map,
    				Map,
    				objects
    			);
    			new_bot.age = Integer.parseInt(bot_data[1]);
    			new_bot.rotate = Integer.parseInt(bot_data[4]);
    			new_bot.state = Integer.parseInt(bot_data[5]);
    			new_bot.c_red = Integer.parseInt(bot_data[6]);
    			new_bot.c_green = Integer.parseInt(bot_data[7]);
    			new_bot.c_blue = Integer.parseInt(bot_data[8]);
    			new_bot.index = Integer.parseInt(bot_data[15]);
    			new_bot.clan_color = new Color(Integer.parseInt(bot_data[12]), Integer.parseInt(bot_data[13]), Integer.parseInt(bot_data[14]));
    			new_bot.pht_org_block = Integer.parseInt(bot_data[16]);
    			new_bot.seed_time = Integer.parseInt(bot_data[17]);
    			for (int j = 0; j < 64; j++) {
    				new_bot.commands[j] = Integer.parseInt(bot_data[18 + j]);;
    			}
    			Map[Integer.parseInt(bot_data[2])][Integer.parseInt(bot_data[3])] = new_bot;
    			objects.add(new_bot);
    		}
        } catch (IOException ex) {
            System.out.println("Ошибка при чтении файла");
            ex.printStackTrace();
        }
	}
	//
	public void save_world() {//сохранить мир
		try {
			//сохранение кислорода
			FileWriter fileWriter = new FileWriter("saved worlds/" + for_load.getText() + ".dat");
	        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(String.valueOf(steps) + ";");
			for (int x = 0; x < Constant.world_scale[0]; x++) {
				for (int y = 0; y < Constant.world_scale[1]; y++) {
					bufferedWriter.write(String.valueOf(oxygen_map[x][y]) + "'");
				}
				bufferedWriter.write(":");
			}
			//сохранение органики
			bufferedWriter.write(";");
			for (int x = 0; x < Constant.world_scale[0]; x++) {
				for (int y = 0; y < Constant.world_scale[1]; y++) {
					bufferedWriter.write(String.valueOf((int)(org_map[x][y])) + "'");
				}
				bufferedWriter.write(":");
			}
			//сохранение углекислоты
			bufferedWriter.write(";");
			for (int x = 0; x < Constant.world_scale[0]; x++) {
				for (int y = 0; y < Constant.world_scale[1]; y++) {
					bufferedWriter.write(String.valueOf(co2_map[x][y]) + "'");
				}
				bufferedWriter.write(":");
			}
			//сохранение бота
			bufferedWriter.write(";");
			for(Bot b: objects) {//                                                   длина бота - 82
				bufferedWriter.write(String.valueOf(b.energy) + "'");//               0 - энергия
				bufferedWriter.write(String.valueOf(b.age) + "'");//                  1 - возраст
				bufferedWriter.write(String.valueOf(b.xpos) + "'");//                 2 - позиция x
				bufferedWriter.write(String.valueOf(b.ypos) + "'");//                 3 - позиция y
				bufferedWriter.write(String.valueOf(b.rotate) + "'");//               4 - направление
				bufferedWriter.write(String.valueOf(b.state) + "'");//                5 - состояние
				bufferedWriter.write(String.valueOf(b.c_red) + "'");//                6 - красный в режиме отрисовки хищников
				bufferedWriter.write(String.valueOf(b.c_green) + "'");//              7 - зеленый в режиме отрисовки хищников
				bufferedWriter.write(String.valueOf(b.c_blue) + "'");//               8 - синий в режиме отрисовки хищников
				bufferedWriter.write(String.valueOf(b.color.getRed()) + "'");//       9 - красный
				bufferedWriter.write(String.valueOf(b.color.getGreen()) + "'");//     10 - зеленый
				bufferedWriter.write(String.valueOf(b.color.getBlue()) + "'");//      11 - синий
				bufferedWriter.write(String.valueOf(b.clan_color.getRed()) + "'");//  12 - красный(кланы)
				bufferedWriter.write(String.valueOf(b.clan_color.getGreen()) + "'");//13 - зеленый(кланы)
				bufferedWriter.write(String.valueOf(b.clan_color.getBlue()) + "'");// 14 - синий(кланы)
				bufferedWriter.write(String.valueOf(b.index) + "'");//                15 - индекс генома
				bufferedWriter.write(String.valueOf(b.pht_org_block) + "'");//        16 - специализация
				bufferedWriter.write(String.valueOf(b.seed_time) + "'");//            17 - сколько еще лететь(если семечко)
				for (int i = 0; i < 64; i++) {//                                      18 - 81 - геном
					bufferedWriter.write(String.valueOf(b.commands[i]) + "'");
				}
				bufferedWriter.write(":");
			}
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println("Ошибка при записи в файл");
            ex.printStackTrace();
        }
	}
	//
	private class change_draw_type implements ActionListener{//смена режима отрисовки(берется из параметра)
		int number;
		private change_draw_type(int new_number){
			number = new_number;
		}
		public void actionPerformed(ActionEvent e) {
			draw_type = number;
		}
	}
	private class change_gas_draw_type implements ActionListener{//смена режима отрисовки фона(берется из параметра)
		int number;
		private change_gas_draw_type(int new_number){
			number = new_number;
		}
		public void actionPerformed(ActionEvent e) {
			gas_draw_type = number;
		}
	}
	private class change_mouse implements ActionListener{//смена функции мыши(берется из параметра)
		int number;
		private change_mouse(int new_number){
			number = new_number;
		}
		public void actionPerformed(ActionEvent e) {
			mouse = number;
		}
	}
	private class change_zoom implements ActionListener{//смена увеличения(берется из параметра)
		int number;
		private change_zoom(int new_number){
			number = new_number;
		}
		public void actionPerformed(ActionEvent e) {
			zoom = number;
			if (zoom != 0) {
				int w = Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom];
				int h = Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom];
				zoom_disp_pos[0] = Constant.border(zoom_disp_pos[0], Constant.world_scale[0] - w / 2, w / 2);
				zoom_disp_pos[1] = Constant.border(zoom_disp_pos[1], Constant.world_scale[1] - h / 2, h / 2);
			}
		}
	}
}
