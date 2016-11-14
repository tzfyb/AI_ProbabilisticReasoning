package mazeworld;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MazeWorld {
	public static int[] NORTH = {0, 1};
	public static int[] SOUTH = {0, -1};
	public static int[] EAST = {1, 0};
	public static int[] WEST = {-1, 0};
	
	public int width, height;
	
	private char[][] maze_grid;
	
	//MazeWorld generator for a single robot, read from a file in current folder
	public MazeWorld(String filename){
		Charset charset = Charset.forName("UTF-8");
		File file =  new File(filename);
		String pathStr = file.getAbsolutePath();
		Path path = Paths.get(pathStr);
		
		try{
			List<String> lines = Files.readAllLines(path, charset);
			height = lines.size();
			width = lines.get(0).length();
			maze_grid = new char[height][width];
			for(int i = 0; i < height; i++)
				for(int j = 0; j < width; j++)
					maze_grid[i][j] = lines.get(i).charAt(j);
		} catch (IOException e){
			System.out.println(e);
		}
	}
	
	//Check if (x, y) is in the map and is a floor
	public boolean isSafe(int x, int y){
		if(x >= 0 && x < height && y >= 0 && y < width)
			return maze_grid[x][y] != '*';
		return false;
	}
	
	public char getColor(int x, int y){
		return maze_grid[x][y];
	}
	
	public String generateColor(List<List<Integer>> path, boolean rnd){
		StringBuilder sb = new StringBuilder();
		for(List<Integer> coor : path){
			int curX = coor.get(0);
			int curY = coor.get(1);
			char realColor = maze_grid[curX][curY];
			if(!rnd){
				sb.append(realColor);
				continue;
			}
			char[] color = {'R', 'G', 'Y', 'B'};
			List<Character> wrongColor = new ArrayList<Character>();
			for(int i = 0; i < color.length; i++){
				if(color[i] == realColor)
					continue;
				wrongColor.add(color[i]);
			}
			
			double prob = Math.random();
			if(prob < 0.88)
				sb.append(realColor);
			else if(prob < 0.92)
				sb.append(wrongColor.get(0));
			else if(prob < 0.96)
				sb.append(wrongColor.get(1));
			else
				sb.append(wrongColor.get(2));
		}
		return sb.toString();
	}
	
	public ArrayList<ArrayList<Character>> getGrid(){
		ArrayList<ArrayList<Character>> grid_copy = new ArrayList<ArrayList<Character>>();
		for(int i = 0; i < height; i++){
			ArrayList<Character> tmp = new ArrayList<Character>();
			for(int j = 0; j < width; j++)
				tmp.add(maze_grid[i][j]);
			grid_copy.add(tmp);
		}
		return grid_copy;
	}
	
	public String getGridString(){
		String res = "";
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++)
				res += maze_grid[y][x];
			res += "\n";
		}
		return res;
	}
	
	public void printGrid(){
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++)
				System.out.print(maze_grid[i][j]);
			System.out.println();
		}
	}
	
	public void printPath(List<List<Integer>> path){
		ArrayList<ArrayList<Character>> myGrid = getGrid();
		for(List<Integer> coor : path){
			myGrid.get(coor.get(0)).set(coor.get(1), '+');
		}
		for(ArrayList<Character> row : myGrid){
			for(int i = 0; i < row.size(); i++){
				if(row.get(i) != '+')
					row.set(i, '.');
			}
		}
		for(ArrayList<Character> row : myGrid){
			for(Character pos : row){
				System.out.print(pos);
			}
			System.out.println();
		}
	}
	
	//Test function for this class
	public static void main(String args[]){
		MazeWorld test = new MazeWorld("test");
		System.out.println(test.getGridString());
		if(test.isSafe(3, 2))
			System.out.println("Safe");
		else
			System.out.println("Not Safe");
	}
}
