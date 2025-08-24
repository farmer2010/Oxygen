package sct;

import java.util.Random;

public class WorldUtils {
	public static Random rand = new Random();
	public static void minerals(double[][] mnr_map) {//минералы
		//приход минералов
		int ymin = Constant.world_scale[1] - (int)(Constant.world_scale[1] / 8.0 * 2);
		for (int i = 0; i < 100; i++) {
			int x = rand.nextInt(Constant.world_scale[0]);
			int y = rand.nextInt(ymin, Constant.world_scale[1]);
			mnr_map[x][y] += rand.nextInt(1, 3);
			if (mnr_map[x][y] > Constant.minerals_max) {
				mnr_map[x][y] = Constant.minerals_max;
			}
		}
		//логика
		double[][] new_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				int[] pos = Constant.get_rotate_position(4, new int[] {x, y});
				if (pos[1] >= 0 && pos[1] < Constant.world_scale[1] && mnr_map[pos[0]][pos[1]] < Constant.minerals_max) {
					if (mnr_map[pos[0]][pos[1]] + mnr_map[x][y] <= Constant.minerals_max) {
						new_map[pos[0]][pos[1]] += mnr_map[x][y];
					}else {
						new_map[pos[0]][pos[1]] += Constant.minerals_max - mnr_map[pos[0]][pos[1]];
						new_map[x][y] += (mnr_map[pos[0]][pos[1]] + mnr_map[x][y]) - Constant.minerals_max;
					}
				}else{
					int c = 1;
					int[] left_pos = Constant.get_rotate_position(6, new int[] {x, y});
					int[] right_pos = Constant.get_rotate_position(2, new int[] {x, y});
					if (mnr_map[left_pos[0]][left_pos[1]] < Constant.minerals_max) {
						c++;
					}
					if (mnr_map[right_pos[0]][right_pos[1]] < Constant.minerals_max) {
						c++;
					}
					double w = mnr_map[x][y] / c;
					if (mnr_map[left_pos[0]][left_pos[1]] < Constant.minerals_max) {
						new_map[left_pos[0]][left_pos[1]] += Math.min(w, Constant.minerals_max - mnr_map[left_pos[0]][left_pos[1]]);
						new_map[x][y] += w - Math.min(w, Constant.minerals_max - mnr_map[left_pos[0]][left_pos[1]]);
					}
					if (mnr_map[right_pos[0]][right_pos[1]] < Constant.minerals_max) {
						new_map[right_pos[0]][right_pos[1]] += Math.min(w, Constant.minerals_max - mnr_map[right_pos[0]][right_pos[1]]);
						new_map[x][y] += w - Math.min(w, Constant.minerals_max - mnr_map[right_pos[0]][right_pos[1]]);
					}
					if (c > 1) {
						new_map[x][y] += w;
					}else if (mnr_map[x][y] >= Constant.minerals_max){
						new_map[x][y] += mnr_map[x][y] * 0.99;
						double s = mnr_map[x][y] * 0.01 / 9;
						for (int j = 0; j < 8; j++) {
							int[] p = Constant.get_rotate_position(j, new int[] {x, y});
							if (p[1] >= 0 && p[1] < Constant.world_scale[1] && mnr_map[p[0]][p[1]] >= Constant.minerals_max) {
								new_map[p[0]][p[1]] += s;
							}else {
								new_map[x][y] += s;
							}
						}
						new_map[x][y] += s;
					}else {
						new_map[x][y] += mnr_map[x][y];
					}
				}
			}
		}
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				mnr_map[x][y] = new_map[x][y];
			}
		}
	}
	//
	public static void gas(double[][] gas_map, double[][] org_map) {//распространение кислорода
		double[][] new_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				if (gas_map[x][y] >= Constant.ox_distribution_min) {
					gas_map[x][y] *= Constant.evaporation_ox_coeff;//испарение
					double ox = gas_map[x][y] / 9;
					new_map[x][y] += ox;
					int count = 0;
					for (int i = 0; i < 8; i++) {
						int[] f = {x, y};
						int[] pos = Constant.get_rotate_position(i, f);
						if (pos[1] >= 0 && pos[1] < Constant.world_scale[1]) {
							new_map[pos[0]][pos[1]] += ox;
							if (new_map[pos[0]][pos[1]] > 1) {
								new_map[pos[0]][pos[1]] = 1;
							}
						}else {
							count++;
						}
					}
					for (int i = 0; i < count; i++) {
						new_map[x][y] += ox;
					}
					if (new_map[x][y] > 1) {
						new_map[x][y] = 1;
					}
				}else {
					new_map[x][y] += gas_map[x][y];
					if (new_map[x][y] > 1) {
						new_map[x][y] = 1;
					}
				}
			}
		}
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				gas_map[x][y] = new_map[x][y];
			}
		}
	}
}
