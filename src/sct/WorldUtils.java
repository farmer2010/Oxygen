package sct;

import java.util.Random;

public class WorldUtils {
	public static Random rand = new Random();
	public static void minerals(double[][] mnr_map) {//минералы
		//приход минералов
		int ymin = Constant.world_scale[1] - Constant.world_scale[1] / 8 * 2;
		for (int i = 0; i < 100; i++) {
			int x = rand.nextInt(Constant.world_scale[0]);
			int y = rand.nextInt(ymin, Constant.world_scale[1]);
			mnr_map[x][y] += rand.nextInt(10, 30);
			if (mnr_map[x][y] > 1000) {
				mnr_map[x][y] = 1000;
			}
		}
		//логика
		double[][] new_map = new double[Constant.world_scale[0]][Constant.world_scale[1]];
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				if (mnr_map[x][y] > 0) {
					
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
					int count = 1;
					for (int i = 0; i < 8; i++) {
						int[] f = {x, y};
						int[] pos = Constant.get_rotate_position(i, f);
						if (pos[1] >= 0 && pos[1] < Constant.world_scale[1]) {
							count++;
						}
					}
					double ox = gas_map[x][y] / count;
					new_map[x][y] += ox;
					if (new_map[x][y] > 1) {
						new_map[x][y] = 1;
					}
					for (int i = 0; i < 8; i++) {
						int[] f = {x, y};
						int[] pos = Constant.get_rotate_position(i, f);
						if (pos[1] >= 0 && pos[1] < Constant.world_scale[1]) {
							new_map[pos[0]][pos[1]] += ox;
							if (new_map[pos[0]][pos[1]] > 1) {
								new_map[pos[0]][pos[1]] = 1;
							}
						}
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
