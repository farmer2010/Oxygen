package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.ListIterator;

public class Bot{
	ArrayList<Bot> objects;
	Random rand = new Random();
	public Bot[][] map;
	//
	private int x;//позиция
	private int y;
	public int xpos;
	public int ypos;
	//
	public Color color;//основные параметры
	public Color clan_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
	public double energy;
	public int age = Constant.max_age;
	public int memory = 0;
	public int type;
	public int[] commands = new int[64];
	public int index = 0;
	public int rotate = rand.nextInt(8);
	private boolean[] chain = new boolean[8];
	//
	public int c_red = -1;//цвет в режиме отрисовки хищников
	public int c_green = -1;
	public int c_blue = -1;
	//
	public int state = 0;//тип бота
	public int pht_org_block = 0;//специализация: 1 - фотосинтез, 2 - переработка органики
	public int seed_time;//сколько лететь(если семечко)
	public int killed = 0;//убит ли бот
	//
	private double[][] oxygen_map;
	private double[][] co2_map;
	private double[][] org_map;
	public Bot(int new_xpos, int new_ypos, Color new_color, double new_energy, int new_type, double[][] new_oxygen_map, double[][] new_co2_map, double[][] new_org_map, Bot[][] new_map, ArrayList<Bot> new_objects) {
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * Constant.size;
		y = new_ypos * Constant.size;
		color = new_color;
		energy = new_energy;
		objects = new_objects;
		map = new_map;
		oxygen_map = new_oxygen_map;
		co2_map = new_co2_map;
		org_map = new_org_map;
		type = new_type;
		//
		for (int i = 0; i < 64; i++) {//заполняем мозг случайными числами
			commands[i] = rand.nextInt(64);
		}
	}
	public void Draw(Graphics canvas, int draw_type, int zoom, int[] pos) {
		if (zoom == 0) {//зум x1
			canvas.setColor(get_color(draw_type));
			canvas.fillRect(x, y, Constant.size, Constant.size);
		}else if (zoom == 1) {//зум x2.5
			int w = Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom];
			int h = Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom];
			if (xpos >= pos[0] - w / 2 && xpos < pos[0] + w / 2 && ypos >= pos[1] - h / 2 && ypos < pos[1] + h / 2) {
				canvas.setColor(get_color(draw_type));
				canvas.fillRect((xpos - pos[0] + w / 2) * 5, (ypos - pos[1] + h / 2) * 5, 5, 5);
			}
		}else if (zoom == 2) {//зум x5
			int w = Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom];
			int h = Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom];
			if (xpos >= pos[0] - w / 2 && xpos < pos[0] + w / 2 && ypos >= pos[1] - h / 2 && ypos < pos[1] + h / 2) {
				canvas.setColor(new Color(0, 0, 0));
				canvas.fillRect((xpos - pos[0] + w / 2) * 10, (ypos - pos[1] + h / 2) * 10, 10, 10);
				canvas.setColor(get_color(draw_type));
				canvas.fillRect((xpos - pos[0] + w / 2) * 10 + 1, (ypos - pos[1] + h / 2) * 10 + 1, 8, 8);
			}
		}
	}
	public int Update(ListIterator<Bot> iterator) {
		if (killed == 0) {
			if (state == 0) {//если бот
				age -= (int)(oxygen_map[xpos][ypos] * Constant.age_minus_coeff) + 1;
				energy -= Constant.energy_for_life;
				//
				int count = bot_count() + 1;//автоматическое деление и мозг
				if (count_oxygen() >= Constant.life_ox_coeff * count) {
					oxygen_map[xpos][ypos] -= Constant.life_ox_coeff * count;
					co2_map[xpos][ypos] += Constant.life_co2_coeff * count;
					update_commands(iterator);//если хватило кислорода, выполняем команды
					if (energy >= Constant.energy_for_auto_multiply) {//автоматическое деление
						multiply(rotate, 0, 0, 0, false, iterator);
					}
				}
				update_chain();//обновление цепочек
				//
				if (energy > 1000) {//ограничитель количества энергии
					energy = 1000;
				}
				//
				if (age <= 0 || org_map[xpos][ypos] >= Constant.org_die_level || energy <= 0) {//смерть от старости или от органики
					die_with_organics();
					return(0);
				}
			}else if (state == 1){//если семечко
				if (org_map[xpos][ypos] >= Constant.org_die_level) {//умираем от переизбытка органики
					die_with_organics();
					return(0);
				}
				//
				int res = move(rotate);//двигаемся
				if (res == 0) {//столкновение
					int[] pos = get_rotate_position(rotate);
					if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
						if (map[pos[0]][pos[1]] != null) {
							die_with_organics();//убить семечко
							map[pos[0]][pos[1]].die_with_organics();//убить бота
							return(0);
						}
					}
				}
				//
				seed_time--;//уменьшаем счетчик
				if (seed_time == 0) {//если время вышло, превращаемся в бота
					state = 0;
					rotate = rand.nextInt(8);
				}
			}
		}
		return(0);
	}
	public void update_commands(ListIterator<Bot> iterator) {//мозг
		for (int i = 0; i < 5; i++) {
			int command = commands[index];
			if (command == 0) {//повернуться
				rotate += commands[(index + 1) % 64] % 8;
				rotate %= 8;
				index += 2;
				index %= 64;
			}else if (command == 1) {//сменить направление
				rotate = commands[(index + 1) % 64] % 8;
				index += 2;
				index %= 64;
			}else if (command == 2 || command == 3 || command == 4) {//фотосинтез
				int sector = Constant.sector(ypos);
				if (sector <= 7 && pht_org_block != 2 && count_co2() >= Constant.pht_co2_coeff) {
					pht_org_block = 1;
					energy += photo_energy(sector);
					go_green();
					oxygen_map[xpos][ypos] += Constant.pht_ox_coeff * photo_energy(sector);
					if (oxygen_map[xpos][ypos] > 1) {
						oxygen_map[xpos][ypos] = 1;
					}
					co2_map[xpos][ypos] -= Constant.pht_co2_coeff;
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 5) {//походить относительно
				int count = bot_count() + 1;
				if (count_oxygen() >= Constant.move_ox_coeff * count && count_chains() == 0) {
					oxygen_map[xpos][ypos] -= Constant.move_ox_coeff * count;
					int sens = move(commands[(index + 1) % 64] % 8);
					if (sens == 1) {
						energy -= Constant.energy_for_move;
					}
				}
				index += 2;
				index %= 64;
				break;
			}else if(command == 6) {//походить абсолютно
				int count = bot_count() + 1;
				if (count_oxygen() >= Constant.move_ox_coeff * count && count_chains() == 0) {
					oxygen_map[xpos][ypos] -= Constant.move_ox_coeff * count;
					move(rotate);
					energy -= Constant.energy_for_move;
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 7) {//атаковать относительно
				int count = bot_count() + 1;
				if (count_oxygen() >= Constant.attack_ox_coeff * count) {
					oxygen_map[xpos][ypos] -= Constant.attack_ox_coeff * count;
					attack(commands[(index + 1) % 64] % 8);
				}
				index += 2;
				index %= 64;
				break;
			}else if (command == 8) {//атаковать абсолютно
				int count = bot_count() + 1;
				if (count_oxygen() >= Constant.attack_ox_coeff * count) {
					oxygen_map[xpos][ypos] -= Constant.attack_ox_coeff * count;
					attack(rotate);
				}
				index += 1;
				index %= 64;
				break;
			}else if (command == 9) {//посмотреть относительно
				int rot = commands[(index + 1) % 64] % 8;
				index = commands[(index + 2 + see(rot)) % 64];
			}else if (command == 10) {//посмотреть абсолютно
				index = commands[(index + 1 + see(rotate)) % 64];
			}else if (command == 11 | command == 12) {//отдать ресурсы относительно
				give(commands[(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 13 | command == 14) {//отдать ресурсы абсолютно
				give(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 15) {//сколько у меня энергии
				int ind = commands[(index + 1) % 64] * 15;
				if (energy >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 16) {//есть ли фотосинтез
				int sector = Constant.sector(ypos);
				if (sector <= 5) {
					index = commands[(index + 1) % 64];
				}else {
					index = commands[(index + 2) % 64];
				}
			}else if (command == 17) {//есть ли приход минералов
				int sector = Constant.sector(ypos);
				if (sector <= 7 & sector >= 5) {
					index = commands[(index + 1) % 64];
				}else {
					index = commands[(index + 2) % 64];
				}
			}else if (command == 18) {//поделиться относительно
				int new_type = commands[(index + 2) % 64] % 8;
				multiply(commands[(index + 1) % 64] % 8, 0, 0, new_type, false, iterator);
				index += 3;
				index %= 64;
				break;
			}else if (command == 19) {//поделиться абсолютно
				int new_type = commands[(index + 1) % 64] % 8;
				multiply(rotate, 0, 0, new_type, false, iterator);
				index += 2;
				index %= 64;
				break;
			}else if (command == 20) {//какая моя позиция x
				double ind = commands[(index + 1) % 64] / 64.0;
				if (xpos * 1.0 / Constant.world_scale[0] >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 21) {//какая моя позиция y
				double ind = commands[(index + 1) % 64] / 64.0;
				if (ypos * 1.0 / Constant.world_scale[1] >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 22) {//какой мой возраст
				int ind = commands[(index + 1) % 64] * 15;
				if (age >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 23) {//равномерное распределение ресурсов относительно
				unif_distrib(commands[(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 24) {//равномерное распределение ресурсов абсолютно
				unif_distrib(rotate);
				index += 1;
				index %= 64;
				break;
			}else if (command == 25) {//безусловный переход
				index = commands[(index + 1) % 64];
			}else if (command == 26) {//сколько кислорода
				int ind = commands[(index + 1) % 64] / 63;
				if (oxygen_map[xpos][ypos] >= ind) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 27 || command == 28) {//переработка органики под собой
				if (pht_org_block != 1) {
					pht_org_block = 2;
					recycle_organics_under((int)(commands[(index + 1) % 64] * Constant.org_recycle_coeff));
				}
				index += 2;
				index %= 64;
				break;
			}else if (command == 29 || command == 30) {//переработка органики перед собой относительно
				if (pht_org_block != 1) {
					pht_org_block = 2;
					recycle_organics(commands[(index + 1) % 64] % 8, (int)(commands[(index + 2) % 64] * Constant.org_recycle_coeff));
				}
				index += 3;
				index %= 64;
				break;
			}else if (command == 31 || command == 32) {//переработка органики перед собой абсолютно
				if (pht_org_block != 1) {
					pht_org_block = 2;
					recycle_organics(rotate, (int)(commands[(index + 2) % 64] * Constant.org_recycle_coeff));
				}
				index += 2;
				index %= 64;
				break;
			}else if (command == 33) {//сколько органики подо мной
				int ind = commands[(index + 1) % 64];
				if (org_map[xpos][ypos] >= ind * 15) {
					index = commands[(index + 2) % 64];
				}else {
					index = commands[(index + 3) % 64];
				}
			}else if (command == 34) {//сколько органики передо мной относительно
				int rot = commands[(index + 1) % 64] % 8;
				int ind = commands[(index + 2) % 64];
				index = commands[(index + 3 + see_org(rot, ind)) % 64];
			}else if (command == 35) {//сколько органики передо мной абсолютно
				int ind = commands[(index + 1) % 64];
				index = commands[(index + 2 + see_org(rotate, ind)) % 64];
			}else if (command == 36) {//какое мое направление
				int ind = commands[(index + 1) % 64] % 8;
				if (rotate >= ind) {
					index = commands[(index + 2) % 64];
				}else if (rotate == ind) {
					index = commands[(index + 3) % 64];
				}else {
					index = commands[(index + 4) % 64];
				}
			}else if (command == 37) {//установить направление в случайное
				rotate = rand.nextInt(8);
				index += 1;
				index %= 64;
			}else if (command == 38) {//сколько ботов вокруг
				int ind = commands[(index + 1) % 64] % 8;
				int count = bot_count();
				if (count >= ind) {
					index = commands[(index + 2) % 64];
				}else if (count == ind) {
					index = commands[(index + 3) % 64];
				}else {
					index = commands[(index + 4) % 64];
				}
			}else if (command == 39) {//стрелять семечком относительно
				int rot = commands[(index + 1) % 64] % 8;
				int time = commands[(index + 2) % 64] % 16 + 1;
				int new_type = commands[(index + 3) % 64] % 8;
				multiply(rot, 1, time, new_type, false, iterator);
				index += 4;
				index %= 64;
				break;
			}else if (command == 40) {//стрелять семечком абсолютно
				int time = commands[(index + 1) % 64] % 16 + 1;
				int new_type = commands[(index + 2) % 64] % 8;
				multiply(rotate, 1, time, new_type, false, iterator);
				index += 3;
				index %= 64;
				break;
			}else if (command == 41) {//какой у меня тип
				index = commands[(index + 1 + type) % 64];
			}else if (command == 42) {//добавление в цепочку относительно
				int new_type = commands[(index + 2) % 64] % 8;
				multiply(commands[(index + 1) % 64] % 8, 0, 0, new_type, true, iterator);
				index += 3;
				index %= 64;
				break;
			}else if (command == 43) {//добавление в цепочку абсолютно
				int new_type = commands[(index + 1) % 64] % 8;
				multiply(rotate, 0, 0, new_type, true, iterator);
				index += 2;
				index %= 64;
				break;
			}else if (command == 44) {//сколько у меня связей в цепочке
				int c = count_chains();
				int ind = commands[(index + 1) % 64] % 8;
				if (c > ind) {//
					index = commands[(index + 2) % 64];
				}else if (c < ind) {//
					index = commands[(index + 3) % 64];
				}else {//
					index = commands[(index + 4) % 64];
				}
			}else if (command == 45) {//присоеденить соседа к цепочке относительно
				add_neighbour_to_chain(commands[(index + 1) % 64] % 8);
				index += 2;
				index %= 64;
				break;
			}else if (command == 46) {//присоеденить соседа к цепочке абсолютно
				add_neighbour_to_chain(rotate);
				index += 1;
				index %= 64;
				break;
			}else {
				index += command;
				index %= 64;
			}
		}
	}
	//
	//КОМАНДЫ
	//
	public void add_neighbour_to_chain(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
				chain[rot] = true;
				map[pos[0]][pos[1]].chain[(rot + 4) % 8] = true;
			}
		}
	}
	//
	public int move(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (map[pos[0]][pos[1]] == null) {
				Bot self = map[xpos][ypos];
				map[xpos][ypos] = null;
				xpos = pos[0];
				ypos = pos[1];
				x = xpos * Constant.size;
				y = ypos * Constant.size;
				map[xpos][ypos] = self;
				return(1);
			}
		}
		return(0);
	}
	//
	public void attack(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (map[pos[0]][pos[1]] != null) {
				Bot victim = map[pos[0]][pos[1]];
				if (victim != null) {
					energy += victim.energy;
					victim.die_with_organics();
					go_red();
				}
			}
		}
	}
	//
	public int see(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (org_map[pos[0]][pos[1]] >= Constant.org_die_level) {
				return(3);//если переизбыток органики
			}else {
				if (map[pos[0]][pos[1]] == null) {
					return(1);//если ничего
				}else if (map[pos[0]][pos[1]].state == 0) {
					return(2);//если бот
				}
			}
		}else {
			return(0);//если граница
		}
		return(0);
	}
	//
	public void give(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
				Bot relative = map[pos[0]][pos[1]];
				if (relative.killed == 0) {
					relative.energy += energy / 4;
					energy -= energy / 4;
				}
			}
		}
	}
	//
	public void unif_distrib(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
				Bot relative = map[pos[0]][pos[1]];
				if (relative.killed == 0) {
					double enr = relative.energy + energy;
					relative.energy = enr / 2;
					energy = enr / 2;
				}
			}
		}
	}
	//
	public void recycle_organics_under(int org) {
		int[] pos = {xpos, ypos};
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			double ox = count_oxygen();
			if (org_map[pos[0]][pos[1]] > org) {
				if (ox >= org * Constant.org_recycle_ox_coeff) {
					oxygen_map[xpos][ypos] -= org * Constant.org_recycle_ox_coeff;
					co2_map[xpos][ypos] += org * Constant.org_recycle_co2_coeff;
					energy += org;
					org_map[pos[0]][pos[1]] -= org;
					go_yellow();
				}
			}else {
				if (ox >= org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff) {
					oxygen_map[xpos][ypos] -= org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff;
					co2_map[xpos][ypos] += org_map[pos[0]][pos[1]] * Constant.org_recycle_co2_coeff;
					energy += org_map[pos[0]][pos[1]];
					if (org_map[pos[0]][pos[1]] != 0) {
						go_yellow();
					}
					org_map[pos[0]][pos[1]] = 0;
				}
			}
		}
	}
	//
	public void recycle_organics(int rot, int org) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			double ox = count_oxygen();
			if (org_map[pos[0]][pos[1]] > org) {
				if (ox >= org * Constant.org_recycle_ox_coeff) {
					oxygen_map[xpos][ypos] -= org * Constant.org_recycle_ox_coeff;
					co2_map[xpos][ypos] += org * Constant.org_recycle_co2_coeff;
					energy += org;
					org_map[pos[0]][pos[1]] -= org;
					go_yellow();
				}
			}else {
				if (ox >= org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff) {
					oxygen_map[xpos][ypos] -= org_map[pos[0]][pos[1]] * Constant.org_recycle_ox_coeff;
					co2_map[xpos][ypos] += org_map[pos[0]][pos[1]] * Constant.org_recycle_co2_coeff;
					energy += org_map[pos[0]][pos[1]];
					if (org_map[pos[0]][pos[1]] > 0) {
						go_yellow();
					}
					org_map[pos[0]][pos[1]] = 0;
				}
			}
		}
	}
	//
	public int see_org(int rot, int ind) {
		int[] pos = get_rotate_position(rotate);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (org_map[pos[0]][pos[1]] >= ind * 15) {
				return(0);
			}else {
				return(1);
			}
		}else {
			return(2);
		}
	}
	//
	public void multiply(int rot, int seed, int time, int new_type, boolean ch, ListIterator<Bot> iterator) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
			if (map[pos[0]][pos[1]] == null) {
				energy -= Constant.energy_for_multiply;
				if (energy <= 0) {
					die_with_organics();
				}else {
					Color new_color;
					if (rand.nextInt(800) == 0) {//немного меняем(с шансом 1/800 - полностью)
						new_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
					}else {
						new_color = new Color(Constant.border(color.getRed() + rand.nextInt(-12, 13), 255, 0), Constant.border(color.getGreen() + rand.nextInt(-12, 13), 255, 0), Constant.border(color.getBlue() + rand.nextInt(-12, 13), 255, 0));
					}
					int[] new_brain = new int[64];
					for (int i = 0; i < 64; i++) {
						new_brain[i] = commands[i];
					}
					if (rand.nextInt(4) == 0) {//мутация
						new_brain[rand.nextInt(64)] = rand.nextInt(64);
					}
					Bot new_bot = new Bot(
						pos[0],
						pos[1],
						new_color,
						energy / 2,
						new_type,
						oxygen_map,
						co2_map,
						org_map,
						map,
						objects
					);
					energy /= 2;
					new_bot.commands = new_brain;
					new_bot.clan_color = clan_color;
					//
					if (seed == 1) {
						new_bot.state = 1;
						new_bot.seed_time = time;
						new_bot.rotate = rot;
					}else if (ch) {
						chain[rot] = true;
						new_bot.chain[(rot + 4) % 8] = true;
					}
					//
					iterator.add(new_bot);
					map[pos[0]][pos[1]] = new_bot;
				}
			}
		}
	}
	//
	//ТЕХНИЧЕСКИЕ ФУНКЦИИ
	//
	public double count_oxygen() {
		double ox = oxygen_map[xpos][ypos] * (oxygen_map[xpos][ypos] / (oxygen_map[xpos][ypos] + co2_map[xpos][ypos]));
		return(ox);
	}
	//
	public double count_co2() {
		double co2 = co2_map[xpos][ypos] * (co2_map[xpos][ypos] / (oxygen_map[xpos][ypos] + co2_map[xpos][ypos]));
		return(co2);
	}
	//
	public double photo_energy(int sector) {
		int count = bot_count();
		double n = ((9 - count) * Constant.pht_neighbours_coeff);
		if (n > 1) {
			n = 1;
		}
		double enr = Constant.pht_energy_list[sector] * (org_map[xpos][ypos] / Constant.pht_coeff) * n;
		return(enr);
	}
	//
	public int bot_count() {
		int count = 0;
		for (int i = 0; i < 8; i++) {
			int[] pos = get_rotate_position(i);
			if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
				if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
					count++;
				}
			}
		}
		return(count);
	}
	//
	public void die_with_organics() {//умереть с появлением органики
		killed = 1;
		map[xpos][ypos] = null;
		double enr = Constant.energy_for_multiply / 9.0;
		for (int i = 0; i < 8; i++) {
			int[] pos = get_rotate_position(i);
			if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
				org_map[pos[0]][pos[1]] += enr;
			}
		}
		org_map[xpos][ypos] += enr;
		co2_map[xpos][ypos] += Constant.die_co2_coeff * enr;
		oxygen_map[xpos][ypos] -= Constant.die_ox_coeff;
		if (oxygen_map[xpos][ypos] < 0) {
			oxygen_map[xpos][ypos] = 0;
		}
		delete_chain();
	}
	//
	public int[] get_rotate_position(int rot){
		int[] pos = new int[2];
		pos[0] = (xpos + Constant.movelist[rot][0]) % Constant.world_scale[0];
		pos[1] = ypos + Constant.movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = Constant.world_scale[0] - 1;
		}else if(pos[0] >= Constant.world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	//
	public Color get_color(int draw_type) {
		Color c = new Color(0, 0, 0);
		if (state == 0) {//рисуем бота
			if (draw_type == 0) {//режим отрисовки хищников
				if (c_red == -1 || c_green == -1 || c_blue == -1) {
					c = new Color(128, 128, 128);
				}else {
					c = new Color(c_red, c_green, c_blue);
				}
			}else if (draw_type == 1) {//цвета
				c = color;
			}else if (draw_type == 2) {//энергии
				double g = Constant.border(energy / 1000.0, 1, 0);
				c = Constant.gradient(new Color(255, 255, 0), new Color(255, 0, 0), g);
			}else if (draw_type == 3) {//кланов
				c = clan_color;
			}else if (draw_type == 4) {//возраста
				c = Constant.gradient(new Color(0, 0, 255), new Color(255, 255, 0), (age * 1.0) / Constant.max_age);
			}else if (draw_type == 5) {//типа
				if (type == 0) {
					c = new Color(0, 0, 255);
				}else if (type == 1) {
					c = new Color(0, 255, 0);
				}else if (type == 2) {
					c = new Color(255, 0, 0);
				}else if (type == 3) {
					c = new Color(255, 255, 0);
				}else if (type == 4) {
					c = new Color(255, 0, 255);
				}else if (type == 5) {
					c = new Color(0, 255, 255);
				}else if (type == 6) {
					c = new Color(128, 128, 128);
				}else if (type == 7) {
					c = new Color(0, 128, 255);
				}
			}else if (draw_type == 6) {
				int count_ch = count_chains();
				if (count_ch == 0) {
					c = new Color(255, 255, 128);
				}else if (count_ch == 1){
					c = new Color(85, 0, 85);
				}else if (count_ch == 2) {
					c = new Color(200, 0, 200);
				}else {
					c = new Color(255, 0, 255);
				}
			}
		}else if (state == 1){//рисуем семечко
			c = new Color(127, 60, 0);
		}
		return(c);
	}
	//
	public void go_red() {//краснеть
		if (c_red == -1 && c_green == -1 && c_blue == -1) {
			c_red = 255;
			c_green = 0;
			c_blue = 0;
		}else {
			c_red = Constant.border(c_red + 3, 255, 0);
			c_green = Constant.border(c_green - 3, 255, 0);
			c_blue = Constant.border(c_blue - 3, 255, 0);
		}
	}
	//
	public void go_green() {//зеленеть
		if (c_red == -1 && c_green == -1 && c_blue == -1) {
			c_red = 0;
			c_green = 255;
			c_blue = 0;
		}else {
			c_red = Constant.border(c_red - 3, 255, 0);
			c_green = Constant.border(c_green + 3, 255, 0);
			c_blue = Constant.border(c_blue - 3, 255, 0);
		}
	}
	//
	public void go_blue() {//синеть
		if (c_red == -1 && c_green == -1 && c_blue == -1) {
			c_red = 0;
			c_green = 0;
			c_blue = 255;
		}else {
			c_red = Constant.border(c_red - 3, 255, 0);
			c_green = Constant.border(c_green - 3, 255, 0);
			c_blue = Constant.border(c_blue + 3, 255, 0);
		}
	}
	//
	public void go_yellow() {//желтеть
		if (c_red == -1 && c_green == -1 && c_blue == -1) {
			c_red = 255;
			c_green = 255;
			c_blue = 0;
		}else {
			c_red = Constant.border(c_red + 3, 255, 0);
			c_green = Constant.border(c_green + 3, 255, 0);
			c_blue = Constant.border(c_blue - 3, 255, 0);
		}
	}
	//
	public void update_chain() {
		int c = 1;
		double energy_sum = energy;
		for (int i = 0; i < 8; i++) {
			if (chain[i]) {
				int[] pos = get_rotate_position(i);
				if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
					if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
						energy_sum += map[pos[0]][pos[1]].energy;
						c++;
					}else {
						chain[i] = false;
					}
				}
			}
		}
		if (c != 1) {
			double enr = energy_sum / c;
			energy = enr;
			for (int i = 0; i < 8; i++) {
				if (chain[i]) {
					int[] pos = get_rotate_position(i);
					if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
						map[pos[0]][pos[1]].energy = enr;
					}
				}
			}
		}
	}
	//
	public void delete_chain() {
		for (int g = 0; g < 8; g++) {
			if (chain[g]) {
				int[] pos = get_rotate_position(g);
				if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
					if (map[pos[0]][pos[1]] != null && map[pos[0]][pos[1]].state == 0) {
						map[pos[0]][pos[1]].chain[(g + 4) % 8] = false;
					}
				}
			}
		}
	}
	//
	public int count_chains() {
		int sum = 0;
		for (int i = 0; i < 8; i++) {
			if (chain[i]){
				sum++;
			}
		}
		return(sum);
	}
}
