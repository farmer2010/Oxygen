package sct;

import java.awt.Color;
import java.awt.Graphics;

public class Draw {
	public static void draw_ox(Graphics canvas, double[][] oxygen_map, int[][] org_map, int zoom, int[] zoom_disp_pos) {
		int w = Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom];
		int h = Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom];
		for (int i = 0; i < Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom]; i++) {
			for (int j = 0; j < Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom]; j++) {
				if (zoom == 0) {//зум 1x
					int x = i;
					int y = j;
					double ox = oxygen_map[x][y];
					if (ox > Constant.ox_render_maximum_coeff) {
						ox = Constant.ox_render_maximum_coeff;
					}
					canvas.setColor(new Color(255 - (int)(ox * 255 * (1 / Constant.ox_render_maximum_coeff)), 255 - (int)(ox * 255 * (1 / Constant.ox_render_maximum_coeff)), 255));
					canvas.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
					if (org_map[x][y] >= Constant.org_die_level) {
						canvas.setColor(new Color(90, 0, 0));
						canvas.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
					}
				}else {//зум 2.5x - 5x
					int x = i + zoom_disp_pos[0] - w / 2;
					int y = j + zoom_disp_pos[1] - h / 2;
					double ox = oxygen_map[x][y];
					if (ox > Constant.ox_render_maximum_coeff) {
						ox = Constant.ox_render_maximum_coeff;
					}
					canvas.setColor(new Color(255 - (int)(ox * 255 * (1 / Constant.ox_render_maximum_coeff)), 255 - (int)(ox * 255 * (1 / Constant.ox_render_maximum_coeff)), 255));
					canvas.fillRect(i * Constant.zoom_sizes[zoom], j * Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom]);
					if (org_map[x][y] >= Constant.org_die_level) {
						canvas.setColor(new Color(90, 0, 0));
						canvas.fillRect(i * Constant.zoom_sizes[zoom], j * Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom]);
					}
				}
			}
		}
	}
	//
	public static void draw_org(Graphics canvas, int[][] org_map, int zoom, int[] zoom_disp_pos) {
		int w = Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom];
		int h = Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom];
		for (int i = 0; i < Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom]; i++) {
			for (int j = 0; j < Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom]; j++) {
				if (zoom == 0) {//зум 1x
					int x = i;
					int y = j;
					int gr = Constant.border(255 - (int)(org_map[x][y] / Constant.org_die_level * 255), 255, 0);
					if (gr < 255) {
						canvas.setColor(new Color(gr, gr, gr));
						canvas.fillRect(x * Constant.zoom_sizes[zoom], y * Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom]);
					}
				}else {//зум 2.5x - 5x
					int x = i + zoom_disp_pos[0] - w / 2;
					int y = j + zoom_disp_pos[1] - h / 2;
					int gr = Constant.border(255 - (int)(org_map[x][y] / Constant.org_die_level * 255), 255, 0);
					if (gr < 255) {
						canvas.setColor(new Color(gr, gr, gr));
						canvas.fillRect(i * Constant.zoom_sizes[zoom], j * Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom]);
					}
				}
			}
		}
	}
	//
	public static void draw_co2(Graphics canvas, double[][] co2_map, int[][] org_map, int zoom, int[] zoom_disp_pos) {
		int w = Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom];
		int h = Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom];
		for (int i = 0; i < Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom]; i++) {
			for (int j = 0; j < Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom]; j++) {
				if (zoom == 0) {//зум 1x
					int x = i;
					int y = j;
					double co2 = co2_map[x][y];
					if (co2 > Constant.co2_render_maximum_coeff) {
						co2 = Constant.co2_render_maximum_coeff;
					}
					canvas.setColor(new Color(255 - (int)(co2 * 128 * (1 / Constant.co2_render_maximum_coeff)), 255, 255 - (int)(co2 * 128 * (1 / Constant.co2_render_maximum_coeff))));
					canvas.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
					if (org_map[x][y] >= Constant.org_die_level) {
						canvas.setColor(new Color(90, 0, 0));
						canvas.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
					}
				}else {//зум 2.5x - 5x
					int x = i + zoom_disp_pos[0] - w / 2;
					int y = j + zoom_disp_pos[1] - h / 2;
					double co2 = co2_map[x][y];
					if (co2 > Constant.co2_render_maximum_coeff) {
						co2 = Constant.co2_render_maximum_coeff;
					}
					canvas.setColor(new Color(128 - (int)(co2 * 128 * (1 / Constant.co2_render_maximum_coeff)), 255, 128 - (int)(co2 * 128 * (1 / Constant.co2_render_maximum_coeff))));
					canvas.fillRect(i * Constant.zoom_sizes[zoom], j * Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom]);
					if (org_map[x][y] >= Constant.org_die_level) {
						canvas.setColor(new Color(90, 0, 0));
						canvas.fillRect(i * Constant.zoom_sizes[zoom], j * Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom]);
					}
				}
			}
		}
	}
	//
	public static void draw_mnr(Graphics canvas, double[][] mnr_map) {
		for (int x = 0; x < Constant.world_scale[0]; x++) {
			for (int y = 0; y < Constant.world_scale[1]; y++) {
				canvas.setColor(new Color(255 - (int)(mnr_map[x][y] / 1000 * 255), 255 - (int)(mnr_map[x][y] / 1000 * 255), 255));
				canvas.fillRect(x * Constant.size, y * Constant.size, Constant.size, Constant.size);
			}
		}
	}
	//
	public static void draw_height(Graphics canvas, int[][] height_map, int zoom, int[] zoom_disp_pos) {
		int w = Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom];
		int h = Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom];
		for (int i = 0; i < Constant.world_scale[0] * Constant.size / Constant.zoom_sizes[zoom]; i++) {
			for (int j = 0; j < Constant.world_scale[1] * Constant.size / Constant.zoom_sizes[zoom]; j++) {
				if (zoom == 0) {//зум 1x
					int x = i;
					int y = j;
					int gr = Constant.border(255 - (int)(height_map[x][y] / 1000.0 * 255), 255, 0);
					if (gr < 255) {
						canvas.setColor(new Color(gr, gr, gr));
						canvas.fillRect(x * Constant.zoom_sizes[zoom], y * Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom]);
					}
				}else {//зум 2.5x - 5x
					int x = i + zoom_disp_pos[0] - w / 2;
					int y = j + zoom_disp_pos[1] - h / 2;
					int gr = Constant.border(255 - (int)(height_map[x][y] / 1000.0 * 255), 255, 0);
					if (gr < 255) {
						canvas.setColor(new Color(gr, gr, gr));
						canvas.fillRect(i * Constant.zoom_sizes[zoom], j * Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom], Constant.zoom_sizes[zoom]);
					}
				}
			}
		}
	}
}
