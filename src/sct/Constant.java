package sct;

public class Constant {
	public static int W = 1920;
	public static int H = 1080;
	public static int size = 2;
	public static int[] world_scale = {500, 500};
	public static int starting_bot_count = 45000;
	public static double starting_ox = 0.02;
	public static double starting_co2 = 0.02;
	public static int starting_org = 200;
	public static double ox_render_maximum_coeff = 0.25;
	public static double co2_render_maximum_coeff = 0.25;
	public static String[] draw_type_names = {"predators", "energy", "color", "clans", "age", "types"};
	public static String[] mouse_func_names = {"select", "set", "remove"};
	public static int[] zoom_sizes = {2, 5, 10};
	//
	public static double org_recycle_ox_coeff = 0.00003;
	public static double life_ox_coeff = 0.002;
	public static double move_ox_coeff = 0.003;
	public static double attack_ox_coeff = 0.01;
	public static double pht_ox_coeff = 0.003;
	public static double die_ox_coeff = 0.01;
	public static double evaporation_ox_coeff = 0.98;
	public static double ox_distribution_min = 0.001;
	//
	
	//
	public static int org_die_level = 800;
	public static int age_minus_coeff = 10;
	public static int energy_for_life = 1;
	public static int energy_for_move = 1;
	public static int energy_for_multiply = 20;
	public static int energy_for_auto_multiply = 800;
	public static int max_age = 1500;
	//
	public static int pht_coeff = 400;
	public static int[] pht_energy_list = {10, 9, 8, 7, 6, 5, 4, 3};
	public static double pht_neighbours_coeff = 0.1111;
	public static double org_recycle_coeff = 1;
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
}
