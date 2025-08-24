package sct;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Constant {
	static Toolkit toolkit = Toolkit.getDefaultToolkit();
    static Dimension screenSize = toolkit.getScreenSize();
    //
	public static int W = (int) screenSize.getWidth();
	public static int H = (int) screenSize.getHeight();
	public static int size = 2;
	public static int[] world_scale = {(W - 300) / size, H / 2};
	public static int starting_bot_count = 45000;
	public static double starting_ox = 0.02;
	public static double starting_co2 = 0.02;
	public static double starting_minerals = 0;
	public static double starting_org = 200;
	public static double starting_energy = 200;
	public static double ox_render_maximum_coeff = 0.125;
	public static double co2_render_maximum_coeff = 0.125;
	public static String[] draw_type_names = {"predators", "energy", "color", "clans", "age", "types", "chains"};
	public static String[] mouse_func_names = {"select", "set", "remove"};
	public static int[] zoom_sizes = {size, 5, 10};
	public static int genome_size = 256;
	//
	public static double org_recycle_ox_coeff = 0.000015;//0.00002
	public static double life_ox_coeff = 0.0019;
	public static double move_ox_coeff = 0.003;
	public static double attack_ox_coeff = 0.01;
	public static double pht_ox_coeff = 0.003;
	public static double die_ox_coeff = 0.01;
	public static double evaporation_ox_coeff = 0.99;
	public static double ox_distribution_min = 0.001;
	//
	public static double life_co2_coeff = 0.0004;
	public static double pht_co2_coeff = 0.00033;
	public static double org_recycle_co2_coeff = 0.00016;
	public static double die_co2_coeff = 0.0008;
	public static double co2_ox_coeff = 0;
	//
	public static double minerals_max = 100;
	public static double mnr_recycle_coeff = 2;
	//
	public static double energy_die_level = 100000000;//2500
	public static double enr_recycle_coeff = 0.125;
	//
	public static double org_die_level = 800.0;
	public static int age_minus_coeff = 20;
	public static int energy_for_life = 1;
	public static int energy_for_move = 1;
	public static int energy_for_multiply = 80;
	public static int energy_for_auto_multiply = 800;
	public static int max_age = 1500;
	public static double org_recycle_coeff = 0.125;
	//
	public static double pht_coeff = 400;
	public static int[] pht_energy_list = {10, 9, 8, 7, 6, 5, 4, 3};
	public static double[] pht_neighbours_coeff = {1, 0.8, 0.5, 0.4, 0.3, 0.2, 0.1, 0.05, 0};
	//
	public static int[][] movelist = {
		{0, -1},
		{1, -1},
		{1, 0},
		{1, 1},
		{0, 1},
		{-1, 1},
		{-1, 0},
		{-1, -1}
	};
	//
	public static int border(int number, int border1, int border2) {
		if (number > border1) {
			number = border1;
		}else if (number < border2) {
			number = border2;
		}
		return(number);
	}
	public static double border(double number, double border1, double border2) {
		if (number > border1) {
			number = border1;
		}else if (number < border2) {
			number = border2;
		}
		return(number);
	}
	public static int sector(int y) {
		int sec = y / (Constant.world_scale[1] / 8);
		if (sec > 7) {
			sec = 7;
		}
		return(sec);
	}
	public static int[] get_rotate_position(int rot, int[] sp){
		int[] pos = new int[2];
		pos[0] = (sp[0] + Constant.movelist[rot][0]) % Constant.world_scale[0];
		pos[1] = sp[1] + Constant.movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = Constant.world_scale[0] - 1;
		}else if(pos[0] >= Constant.world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	public static Color gradient(Color color1, Color color2, double grad) {
		int r = (int)(color1.getRed() * (1 - grad) + color2.getRed() * grad);
		int g = (int)(color1.getGreen() * (1 - grad) + color2.getGreen() * grad);
		int b = (int)(color1.getBlue() * (1 - grad) + color2.getBlue() * grad);
		return(new Color(r, g, b));
	}
}
