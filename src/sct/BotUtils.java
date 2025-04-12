package sct;

public class BotUtils {
	public static int bot_count(Bot bot) {
		int count = 0;
		for (int i = 0; i < 8; i++) {
			int[] pos = Constant.get_rotate_position(i, new int[] {bot.xpos, bot.ypos});
			if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
				if (bot.map[pos[0]][pos[1]] != null && bot.map[pos[0]][pos[1]].state == 0) {
					count++;
				}
			}
		}
		return(count);
	}
	//
	public static double count_oxygen(Bot bot) {
		double ox = bot.oxygen_map[bot.xpos][bot.ypos] * (bot.oxygen_map[bot.xpos][bot.ypos] / (bot.oxygen_map[bot.xpos][bot.ypos] + bot.co2_map[bot.xpos][bot.ypos]));
		return(ox);
	}
	//
	public static double count_co2(Bot bot) {
		double co2 = bot.co2_map[bot.xpos][bot.ypos] * (bot.co2_map[bot.xpos][bot.ypos] / (bot.oxygen_map[bot.xpos][bot.ypos] + bot.co2_map[bot.xpos][bot.ypos]));
		return(co2);
	}
	//
	public static double photo_energy(Bot bot, int sector) {
		int count = BotUtils.bot_count(bot);
		double enr = Constant.pht_energy_list[sector] * (bot.org_map[bot.xpos][bot.ypos] / Constant.pht_coeff) * Constant.pht_neighbours_coeff[count];
		return(enr);
	}
	//
	public static void die_with_organics(Bot bot) {//умереть с появлением органики
		bot.killed = 1;
		bot.map[bot.xpos][bot.ypos] = null;
		double enr = Constant.energy_for_multiply / 9.0;
		for (int i = 0; i < 8; i++) {
			int[] pos = Constant.get_rotate_position(i, new int[] {bot.xpos, bot.ypos});
			if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
				bot.org_map[pos[0]][pos[1]] += enr;
			}
		}
		bot.org_map[bot.xpos][bot.ypos] += enr;
		bot.co2_map[bot.xpos][bot.ypos] += Constant.die_co2_coeff * enr;
		bot.oxygen_map[bot.xpos][bot.ypos] -= Constant.die_ox_coeff;
		if (bot.oxygen_map[bot.xpos][bot.ypos] < 0) {
			bot.oxygen_map[bot.xpos][bot.ypos] = 0;
		}
		BotUtils.delete_chain(bot);
	}
	//
	public static void delete_chain(Bot bot) {
		for (int g = 0; g < 8; g++) {
			if (bot.chain[g]) {
				int[] pos = Constant.get_rotate_position(g, new int[] {bot.xpos, bot.ypos});
				if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
					if (bot.map[pos[0]][pos[1]] != null && bot.map[pos[0]][pos[1]].state == 0) {
						bot.map[pos[0]][pos[1]].chain[(g + 4) % 8] = false;
					}
				}
			}
		}
	}
	//
	public static int count_chains(Bot bot) {
		int sum = 0;
		for (int i = 0; i < 8; i++) {
			if (bot.chain[i]){
				sum++;
			}
		}
		return(sum);
	}
}
