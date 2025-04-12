package sct;

import java.awt.Color;
import java.util.ListIterator;
import java.util.Random;

public class BotCommands {
	public static void add_neighbour_to_chain(Bot bot, int rot) {
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (bot.map[pos[0]][pos[1]] != null && bot.map[pos[0]][pos[1]].state == 0) {
				bot.chain[rot] = true;
				bot.map[pos[0]][pos[1]].chain[(rot + 4) % 8] = true;
			}
		}
	}
	//
	public static int move(Bot bot, int rot) {
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (bot.map[pos[0]][pos[1]] == null) {
				Bot self = bot.map[bot.xpos][bot.ypos];
				bot.map[bot.xpos][bot.ypos] = null;
				bot.xpos = pos[0];
				bot.ypos = pos[1];
				bot.x = bot.xpos * Constant.size;
				bot.y = bot.ypos * Constant.size;
				bot.map[bot.xpos][bot.ypos] = self;
				return(1);
			}
		}
		return(0);
	}
	//
	public static void attack(Bot bot, int rot) {
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (bot.map[pos[0]][pos[1]] != null) {
				Bot victim = bot.map[pos[0]][pos[1]];
				if (victim != null) {
					bot.energy += victim.energy;
					BotUtils.die_with_organics(victim);
					bot.go_red();
				}
			}
		}
	}
	//
	public static int see(Bot bot, int rot) {
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (bot.org_map[pos[0]][pos[1]] >= Constant.org_die_level) {
				return(3);//если переизбыток органики
			}else {
				if (bot.map[pos[0]][pos[1]] == null) {
					return(1);//если ничего
				}else if (bot.map[pos[0]][pos[1]].state == 0) {
					return(2);//если бот
				}
			}
		}else {
			return(0);//если граница
		}
		return(0);
	}
	//
	public static void give(Bot bot, int rot) {
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (bot.map[pos[0]][pos[1]] != null && bot.map[pos[0]][pos[1]].state == 0) {
				Bot relative = bot.map[pos[0]][pos[1]];
				if (relative.killed == 0) {
					relative.energy += bot.energy / 4;
					bot.energy -= bot.energy / 4;
				}
			}
		}
	}
	//
	public static void unif_distrib(Bot bot, int rot) {
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (bot.map[pos[0]][pos[1]] != null && bot.map[pos[0]][pos[1]].state == 0) {
				Bot relative = bot.map[pos[0]][pos[1]];
				if (relative.killed == 0) {
					double enr = relative.energy + bot.energy;
					relative.energy = enr / 2;
					bot.energy = enr / 2;
				}
			}
		}
	}
	//
	public static void recycle_organics_under(Bot bot, int org) {
		int[] pos = {bot.xpos, bot.ypos};
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			double ox = BotUtils.count_oxygen(bot);
			if (bot.org_map[pos[0]][pos[1]] > org) {
				if (ox >= org * Constant.org_recycle_ox_coeff) {
					bot.oxygen_map[bot.xpos][bot.ypos] -= org * Constant.org_recycle_ox_coeff;
					bot.co2_map[bot.xpos][bot.ypos] += org * Constant.org_recycle_co2_coeff;
					bot.energy += org;
					bot.org_map[pos[0]][pos[1]] -= org;
					bot.go_yellow();
				}
			}else {
				if (ox >= bot.org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff) {
					bot.oxygen_map[bot.xpos][bot.ypos] -= bot.org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff;
					bot.co2_map[bot.xpos][bot.ypos] += bot.org_map[pos[0]][pos[1]] * Constant.org_recycle_co2_coeff;
					bot.energy += bot.org_map[pos[0]][pos[1]];
					if (bot.org_map[pos[0]][pos[1]] != 0) {
						bot.go_yellow();
					}
					bot.org_map[pos[0]][pos[1]] = 0;
				}
			}
		}
	}
	//
	public static void recycle_organics(Bot bot, int rot, int org) {
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			double ox = BotUtils.count_oxygen(bot);
			if (bot.org_map[pos[0]][pos[1]] > org) {
				if (ox >= org * Constant.org_recycle_ox_coeff) {
					bot.oxygen_map[bot.xpos][bot.ypos] -= org * Constant.org_recycle_ox_coeff;
					bot.co2_map[bot.xpos][bot.ypos] += org * Constant.org_recycle_co2_coeff;
					bot.energy += org;
					bot.org_map[pos[0]][pos[1]] -= org;
					bot.go_yellow();
				}
			}else {
				if (ox >= bot.org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff) {
					bot.oxygen_map[bot.xpos][bot.ypos] -= bot.org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff;
					bot.co2_map[bot.xpos][bot.ypos] += bot.org_map[pos[0]][pos[1]] * Constant.org_recycle_co2_coeff;
					bot.energy += bot.org_map[pos[0]][pos[1]];
					if (bot.org_map[pos[0]][pos[1]] > 0) {
						bot.go_yellow();
					}
					bot.org_map[pos[0]][pos[1]] = 0;
				}
			}
		}
	}
	//
	public static int see_org(Bot bot, int rot, int ind) {
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (bot.org_map[pos[0]][pos[1]] >= ind * 15) {
				return(0);
			}else {
				return(1);
			}
		}else {
			return(2);
		}
	}
	//
	public static void multiply(Bot bot, int rot, int seed, int time, int new_type, boolean ch, ListIterator<Bot> iterator) {
		int[] pos = Constant.get_rotate_position(rot, new int[] {bot.xpos, bot.ypos});
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (bot.map[pos[0]][pos[1]] == null) {
				bot.energy -= Constant.energy_for_multiply;
				if (bot.energy <= 0) {
					BotUtils.die_with_organics(bot);
				}else {
					Color new_color;
					if (bot.rand.nextInt(800) == 0) {//немного меняем(с шансом 1/800 - полностью)
						new_color = new Color(bot.rand.nextInt(256), bot.rand.nextInt(256), bot.rand.nextInt(256));
					}else {
						new_color = new Color(Constant.border(bot.color.getRed() + bot.rand.nextInt(-12, 13), 255, 0), Constant.border(bot.color.getGreen() + bot.rand.nextInt(-12, 13), 255, 0), Constant.border(bot.color.getBlue() + bot.rand.nextInt(-12, 13), 255, 0));
					}
					int[] new_brain = new int[64];
					for (int i = 0; i < 64; i++) {
						new_brain[i] = bot.commands[i];
					}
					if (bot.rand.nextInt(4) == 0) {//мутация
						new_brain[bot.rand.nextInt(64)] = bot.rand.nextInt(64);
					}
					Bot new_bot = new Bot(
						pos[0],
						pos[1],
						new_color,
						bot.energy / 2,
						new_type,
						bot.oxygen_map,
						bot.co2_map,
						bot.org_map,
						bot.map,
						bot.objects
					);
					bot.energy /= 2;
					new_bot.commands = new_brain;
					new_bot.clan_color = bot.clan_color;
					//
					if (seed == 1) {
						new_bot.state = 1;
						new_bot.seed_time = time;
						new_bot.rotate = rot;
					}else if (ch) {
						bot.chain[rot] = true;
						new_bot.chain[(rot + 4) % 8] = true;
					}
					//
					iterator.add(new_bot);
					bot.map[pos[0]][pos[1]] = new_bot;
				}
			}
		}
	}
}
