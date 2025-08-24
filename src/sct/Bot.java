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
	public double[][] oxygen_map;
	public double[][] co2_map;
	public double[][] mnr_map;
	public double[][] org_map;
	public double[][] energy_map;
	//
	public int x;//позиция
	public int y;
	public int xpos;
	public int ypos;
	//
	public Color color;//основные параметры
	public Color clan_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
	public double energy;
	public int age = Constant.max_age;
	//public int memory = 0;
	public int type;
	public int[] commands = new int[256];
	public int index = 0;
	public int rotate = rand.nextInt(8);
	public boolean[] chain = new boolean[8];
	public int[] mitochondria = new int[1];//0 - фотосинтез, 1 - минералы
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
	public Bot(int new_xpos, int new_ypos, Color new_color, double new_energy, int new_type, double[][] new_oxygen_map, double[][] new_co2_map, double[][] new_mnr_map, double[][] new_org_map, double[][] new_energy_map, Bot[][] new_map, ArrayList<Bot> new_objects) {
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
		energy_map = new_energy_map;
		mnr_map = new_mnr_map;
		type = new_type;
		//type = 0;
		//
		for (int i = 0; i < 256; i++) {//заполняем мозг случайными числами
			commands[i] = rand.nextInt(256);
		}
		for (int i = 0; i < mitochondria.length; i++) {
			if (ypos <= Constant.world_scale[1] - (int)(Constant.world_scale[1] / 8.0 * 2)) {
				mitochondria[i] = 0;
			}else {
				mitochondria[i] = 1;
			}
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
				//
				canvas.setColor(new Color(0, 0, 0));
				int c = 0;
				for (int i = 0; i < 8; i++) {
					if (chain[i]) {
						c++;
						canvas.drawLine((xpos - pos[0] + w / 2) * 10 + 5, (ypos - pos[1] + h / 2) * 10 + 5, (xpos - pos[0] + w / 2) * 10 + 5 + Constant.movelist[i][0] * 4, (ypos - pos[1] + h / 2) * 10 + 5 + Constant.movelist[i][1] * 4);
					}
				}
				if (c != 0) {//если бот в цепоке, рисуем на нем квадрат
					canvas.fillRect((xpos - pos[0] + w / 2) * 10 + 3, (ypos - pos[1] + h / 2) * 10 + 3, 4, 4);
				}
			}
		}
	}
	public int Update(ListIterator<Bot> iterator) {
		if (age <= 0 || org_map[xpos][ypos] >= Constant.org_die_level || energy_map[xpos][ypos] >= Constant.energy_die_level || energy <= 0) {//смерть от старости или от органики
			BotUtils.die_with_organics(this);
			return(0);
		}
		if (killed == 0) {
			if (state == 0) {//если бот
				age -= (int)(oxygen_map[xpos][ypos] * Constant.age_minus_coeff) + 1;
				energy -= Constant.energy_for_life;
				//
				if (energy > 1000) {//ограничитель количества энергии
					energy = 1000;
				}
				//
				int count = BotUtils.bot_count(this) + 1;//количество соседей
				if (mitochondria[0] % 2 == 0) {
					if (BotUtils.count_oxygen(this) >= Constant.life_ox_coeff * count * (BotUtils.count_co2(this) * Constant.co2_ox_coeff + 1)) {
						oxygen_map[xpos][ypos] -= Constant.life_ox_coeff * count * (BotUtils.count_co2(this) * Constant.co2_ox_coeff + 1);
						co2_map[xpos][ypos] += Constant.life_co2_coeff * count;
						update_commands(iterator);//если хватило кислорода, выполняем команды
						if (energy >= Constant.energy_for_auto_multiply) {//автоматическое деление
							BotCommands.multiply(this, rotate, 0, 0, 0, false, iterator);
						}
					}
				}else if (mitochondria[0] % 2 == 1){
					if (mnr_map[xpos][ypos] > 0) {
						update_commands(iterator);
						if (energy >= Constant.energy_for_auto_multiply) {//автоматическое деление
							BotCommands.multiply(this, rotate, 0, 0, 0, false, iterator);
						}
					}
				}
				update_chain();//обновление цепочек
			}else if (state == 1){//если семечко
				int res = BotCommands.move(this, rotate);//двигаемся
				if (res == 0) {//столкновение
					int[] pos = Constant.get_rotate_position(rotate, new int[] {xpos, ypos});
					if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
						if (map[pos[0]][pos[1]] != null) {
							BotUtils.die_with_organics(this);//убить семечко
							BotUtils.die_with_organics(map[pos[0]][pos[1]]);//убить бота
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
	//
	public void update_chain() {
		int c = 1;
		double energy_sum = energy;
		for (int i = 0; i < 8; i++) {
			if (chain[i]) {
				int[] pos = Constant.get_rotate_position(i, new int[] {xpos, ypos});
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
					int[] pos = Constant.get_rotate_position(i, new int[] {xpos, ypos});
					if (pos[1] >= 0 & pos[1] < Constant.world_scale[1]) {
						map[pos[0]][pos[1]].energy = enr;
					}
				}
			}
		}
	}
	//
	public void update_commands(ListIterator<Bot> iterator) {//мозг
		for (int i = 0; i < 8; i++) {
			int command = commands[index] % 128;
			if (command == 0) {//повернуться
				rotate += commands[(index + 1) % 256] % 8;
				rotate %= 8;
				index += 2;
				index %= 256;
			}else if (command == 1) {//сменить направление
				rotate = commands[(index + 1) % 256] % 8;
				index += 2;
				index %= 256;
			}else if (command == 2 || command == 3 || command == 4) {//фотосинтез
				int sector = Constant.sector(ypos);
				if (sector <= 7 && (pht_org_block == 1 || pht_org_block == 0) && BotUtils.count_co2(this) >= Constant.pht_co2_coeff && mitochondria[0] % 2 == 0) {
					pht_org_block = 1;
					energy += BotUtils.photo_energy(this, sector);
					go_green();
					oxygen_map[xpos][ypos] += Constant.pht_ox_coeff * BotUtils.photo_energy(this, sector);
					if (oxygen_map[xpos][ypos] > 1) {
						oxygen_map[xpos][ypos] = 1;
					}
					co2_map[xpos][ypos] -= Constant.pht_co2_coeff;
					//System.out.println(BotUtils.photo_energy(this, sector));
				}
				index += 1;
				index %= 256;
				break;
			}else if (command == 5) {//походить относительно
				int count = BotUtils.bot_count(this) + 1;
				if (mitochondria[0] % 2 == 0) {
					if (BotUtils.count_oxygen(this) >= Constant.move_ox_coeff * count && BotUtils.count_chains(this) == 0) {
						oxygen_map[xpos][ypos] -= Constant.move_ox_coeff * count;
						BotCommands.move(this, commands[(index + 1) % 256] % 8);
						energy -= Constant.energy_for_move;
					}
				}else if (mitochondria[0] % 2 == 1) {
					BotCommands.move(this, commands[(index + 1) % 256] % 8);
					energy -= Constant.energy_for_move;
				}
				index += 2;
				index %= 256;
				break;
			}else if(command == 6) {//походить абсолютно
				int count = BotUtils.bot_count(this) + 1;
				if (mitochondria[0] % 2 == 0) {
					if (BotUtils.count_oxygen(this) >= Constant.move_ox_coeff * count && BotUtils.count_chains(this) == 0) {
						oxygen_map[xpos][ypos] -= Constant.move_ox_coeff * count;
						BotCommands.move(this, rotate);
						energy -= Constant.energy_for_move;
					}
				}else if (mitochondria[0] % 2 == 1) {
					BotCommands.move(this, rotate);
					energy -= Constant.energy_for_move;
				}
				index += 1;
				index %= 256;
				break;
			}else if (command == 7) {//атаковать относительно
				int count = BotUtils.bot_count(this) + 1;
				if (mitochondria[0] % 2 == 0) {
					if (BotUtils.count_oxygen(this) >= Constant.attack_ox_coeff * count) {
						oxygen_map[xpos][ypos] -= Constant.attack_ox_coeff * count;
						BotCommands.attack(this, commands[(index + 1) % 256] % 8);
					}
				}else if (mitochondria[0] % 2 == 1) {
					BotCommands.attack(this, commands[(index + 1) % 256] % 8);
				}
				index += 2;
				index %= 256;
				break;
			}else if (command == 8) {//атаковать абсолютно
				int count = BotUtils.bot_count(this) + 1;
				if (mitochondria[0] % 2 == 0) {
					if (BotUtils.count_oxygen(this) >= Constant.attack_ox_coeff * count) {
						oxygen_map[xpos][ypos] -= Constant.attack_ox_coeff * count;
						BotCommands.attack(this, rotate);
					}
				}else if (mitochondria[0] % 2 == 1) {
					BotCommands.attack(this, rotate);
				}
				index += 1;
				index %= 256;
				break;
			}else if (command == 9) {//посмотреть относительно
				int rot = commands[(index + 1) % 256] % 8;
				index = commands[(index + 2 + BotCommands.see(this, rot)) % 256];
			}else if (command == 10) {//посмотреть абсолютно
				index = commands[(index + 1 + BotCommands.see(this, rotate)) % 256];
			}else if (command == 11 | command == 12) {//отдать ресурсы относительно
				BotCommands.give(this, commands[(index + 1) % 256] % 8);
				index += 2;
				index %= 256;
				break;
			}else if (command == 13 | command == 14) {//отдать ресурсы абсолютно
				BotCommands.give(this, rotate);
				index += 1;
				index %= 256;
				break;
			}else if (command == 15) {//сколько у меня энергии
				int ind = commands[(index + 1) % 256] * 4;
				if (energy >= ind) {
					index = commands[(index + 2) % 256];
				}else {
					index = commands[(index + 3) % 256];
				}
			}else if (command == 16) {//есть ли фотосинтез
				int sector = Constant.sector(ypos);
				if (sector <= 5) {
					index = commands[(index + 1) % 256];
				}else {
					index = commands[(index + 2) % 256];
				}
			}else if (command == 17) {//есть ли приход минералов
				int sector = Constant.sector(ypos);
				if (sector <= 7 & sector >= 5) {
					index = commands[(index + 1) % 256];
				}else {
					index = commands[(index + 2) % 256];
				}
			}else if (command == 18) {//поделиться относительно
				int new_type = commands[(index + 2) % 256] % 8;
				BotCommands.multiply(this, commands[(index + 1) % 256] % 8, 0, 0, new_type, false, iterator);
				index += 3;
				index %= 256;
				break;
			}else if (command == 19) {//поделиться абсолютно
				int new_type = commands[(index + 1) % 256] % 8;
				BotCommands.multiply(this, rotate, 0, 0, new_type, false, iterator);
				index += 2;
				index %= 256;
				break;
			}else if (command == 20) {//какая моя позиция x
				double ind = commands[(index + 1) % 256] / 255.0;
				if (xpos * 1.0 / Constant.world_scale[0] >= ind) {
					index = commands[(index + 2) % 256];
				}else {
					index = commands[(index + 3) % 256];
				}
			}else if (command == 21) {//какая моя позиция y
				double ind = commands[(index + 1) % 256] / 255.0;
				if (ypos * 1.0 / Constant.world_scale[1] >= ind) {
					index = commands[(index + 2) % 256];
				}else {
					index = commands[(index + 3) % 256];
				}
			}else if (command == 22) {//какой мой возраст
				int ind = commands[(index + 1) % 256] * 4;
				if (age >= ind) {
					index = commands[(index + 2) % 256];
				}else {
					index = commands[(index + 3) % 256];
				}
			}else if (command == 23) {//равномерное распределение ресурсов относительно
				BotCommands.unif_distrib(this, commands[(index + 1) % 256] % 8);
				index += 2;
				index %= 256;
				break;
			}else if (command == 24) {//равномерное распределение ресурсов абсолютно
				BotCommands.unif_distrib(this, rotate);
				index += 1;
				index %= 256;
				break;
			}else if (command == 25) {//безусловный переход
				index = commands[(index + 1) % 256];
			}else if (command == 26) {//сколько кислорода
				double ind = commands[(index + 1) % 256] / 255.0;
				if (oxygen_map[xpos][ypos] >= ind) {
					index = commands[(index + 2) % 256];
				}else {
					index = commands[(index + 3) % 256];
				}
			}else if (command == 27 || command == 28 || command == 47 || command == 50) {//переработка органики под собой
				if (pht_org_block == 2 || pht_org_block == 0) {
					pht_org_block = 2;
					BotCommands.recycle_organics_under(this, (int)(commands[(index + 1) % 256] * Constant.org_recycle_coeff));
				}
				index += 2;
				index %= 256;
				break;
			}else if (command == 29 || command == 30 || command == 48 || command == 51) {//переработка органики перед собой относительно
				if (pht_org_block == 2 || pht_org_block == 0) {
					pht_org_block = 2;
					BotCommands.recycle_organics(this, commands[(index + 1) % 256] % 8, (int)(commands[(index + 2) % 256] * Constant.org_recycle_coeff));
				}
				index += 3;
				index %= 256;
				break;
			}else if (command == 31 || command == 32 || command == 49 || command == 52) {//переработка органики перед собой абсолютно
				if (pht_org_block == 2 || pht_org_block == 0) {
					pht_org_block = 2;
					BotCommands.recycle_organics(this, rotate, (int)(commands[(index + 2) % 256] * Constant.org_recycle_coeff));
				}
				index += 2;
				index %= 256;
				break;
			}else if (command == 33) {//сколько органики подо мной
				int ind = commands[(index + 1) % 256];
				if (org_map[xpos][ypos] >= ind * 4) {
					index = commands[(index + 2) % 256];
				}else {
					index = commands[(index + 3) % 256];
				}
			}else if (command == 34) {//сколько органики передо мной относительно
				int rot = commands[(index + 1) % 256] % 8;
				int ind = commands[(index + 2) % 256];
				index = commands[(index + 3 + BotCommands.see_org(this, rot, ind)) % 256];
			}else if (command == 35) {//сколько органики передо мной абсолютно
				int ind = commands[(index + 1) % 256];
				index = commands[(index + 2 + BotCommands.see_org(this, rotate, ind)) % 256];
			}else if (command == 36) {//какое мое направление
				int ind = commands[(index + 1) % 256] % 8;
				if (rotate >= ind) {
					index = commands[(index + 2) % 256];
				}else if (rotate == ind) {
					index = commands[(index + 3) % 256];
				}else {
					index = commands[(index + 4) % 256];
				}
			}else if (command == 37) {//установить направление в случайное
				rotate = rand.nextInt(8);
				index += 1;
				index %= 256;
			}else if (command == 38) {//сколько ботов вокруг
				int ind = commands[(index + 1) % 256] % 8;
				int count = BotUtils.bot_count(this);
				if (count >= ind) {
					index = commands[(index + 2) % 256];
				}else if (count == ind) {
					index = commands[(index + 3) % 256];
				}else {
					index = commands[(index + 4) % 256];
				}
			}else if (command == 39) {//стрелять семечком относительно
				int rot = commands[(index + 1) % 256] % 8;
				int time = commands[(index + 2) % 256] % 16 + 1;
				int new_type = commands[(index + 3) % 256] % 8;
				BotCommands.multiply(this, rot, 1, time, new_type, false, iterator);
				index += 4;
				index %= 256;
				break;
			}else if (command == 40) {//стрелять семечком абсолютно
				int time = commands[(index + 1) % 256] % 16 + 1;
				int new_type = commands[(index + 2) % 256] % 8;
				BotCommands.multiply(this, rotate, 1, time, new_type, false, iterator);
				index += 3;
				index %= 256;
				break;
			}else if (command == 41) {//какой у меня тип
				index = commands[(index + 1 + type) % 256];
			}else if (command == 42) {//добавление в цепочку относительно
				int new_type = commands[(index + 2) % 256] % 8;
				BotCommands.multiply(this, commands[(index + 1) % 256] % 8, 0, 0, new_type, true, iterator);
				index += 3;
				index %= 256;
				break;
			}else if (command == 43) {//добавление в цепочку абсолютно
				int new_type = commands[(index + 1) % 256] % 8;
				BotCommands.multiply(this, rotate, 0, 0, new_type, true, iterator);
				index += 2;
				index %= 256;
				break;
			}else if (command == 44) {//сколько у меня связей в цепочке
				int c = BotUtils.count_chains(this);
				int ind = commands[(index + 1) % 256] % 8;
				if (c > ind) {//
					index = commands[(index + 2) % 256];
				}else if (c < ind) {//
					index = commands[(index + 3) % 256];
				}else {//
					index = commands[(index + 4) % 256];
				}
			}else if (command == 45) {//присоеденить соседа к цепочке относительно
				BotCommands.add_neighbour_to_chain(this, commands[(index + 1) % 256] % 8);
				index += 2;
				index %= 256;
				break;
			}else if (command == 46) {//присоеденить соседа к цепочке абсолютно
				BotCommands.add_neighbour_to_chain(this, rotate);
				index += 1;
				index %= 256;
				break;
			}else if (command == 53 || command == 54) {//преобразовать минералы в энергию
				int ind = commands[(index + 1) % 256] % 4 + 1;
				if (mnr_map[xpos][ypos] > 0 && mnr_map[xpos][ypos] >= Math.min(mnr_map[xpos][ypos], ind) && mitochondria[0] % 2 == 1) {
					energy += Math.min(mnr_map[xpos][ypos], ind) * Constant.mnr_recycle_coeff;
					mnr_map[xpos][ypos] -= Math.min(mnr_map[xpos][ypos], ind);
					go_blue();
				}
				index += 2;
				index %= 256;
				break;
			}else if (command == 55 || command == 56 || command == 57) {//сбор энергии под собой
				if (pht_org_block == 3 || pht_org_block == 0) {
					pht_org_block = 3;
					BotCommands.collect_energy_under(this, (int)(commands[(index + 1) % 256] * Constant.enr_recycle_coeff));
				}
				index += 2;
				index %= 256;
				break;
			}else if (command == 58 || command == 59 || command == 60) {//сбор энергии относительно
				if (pht_org_block == 3 || pht_org_block == 0) {
					pht_org_block = 3;
					BotCommands.collect_energy(this, commands[(index + 2) % 256] % 8, (int)(commands[(index + 2) % 256] * Constant.enr_recycle_coeff));
				}
				index += 3;
				index %= 256;
				break;
			}else if (command == 61 || command == 62 || command == 63) {//сбор энергии абсолютно
				if (pht_org_block == 3 || pht_org_block == 0) {
					pht_org_block = 3;
					BotCommands.collect_energy(this, rotate, (int)(commands[(index + 1) % 256] * Constant.enr_recycle_coeff));
				}
				index += 2;
				index %= 256;
				break;
			}else if (command == 64) {//сколько энергии подо мной
				int ind = commands[(index + 1) % 256];
				if (energy_map[xpos][ypos] >= ind * 4) {
					index = commands[(index + 2) % 256];
				}else {
					index = commands[(index + 3) % 256];
				}
			}else if (command == 65) {//сколько энергии передо мной относительно
				int rot = commands[(index + 1) % 256] % 8;
				int ind = commands[(index + 2) % 256];
				index = commands[(index + 3 + BotCommands.see_enr(this, rot, ind)) % 256];
			}else if (command == 66) {//сколько энергии передо мной абсолютно
				int ind = commands[(index + 1) % 256];
				index = commands[(index + 2 + BotCommands.see_enr(this, rotate, ind)) % 256];
			}else {
				index = commands[(index + 1) % 256];
			}
		}
	}
	//
	//ТЕХНИЧЕСКИЕ ФУНКЦИИ
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
				int count_ch = BotUtils.count_chains(this);
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
	public void go_blue2() {//синеть
		if (c_red == -1 && c_green == -1 && c_blue == -1) {
			c_red = 0;
			c_green = 128;
			c_blue = 255;
		}else {
			c_red = Constant.border(c_red - 3, 255, 0);
			c_green = Constant.border(c_green + 3, 128, 0);
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
}
