package mazeworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProbReasoningDriver {
	public static void main(String[] args) {
		
		List<List<Integer>> path = new ArrayList<List<Integer>>();
		path.add(Arrays.asList(3, 0));
		path.add(Arrays.asList(3, 1));
		path.add(Arrays.asList(3, 2));
		path.add(Arrays.asList(3, 3));
		path.add(Arrays.asList(2, 3));
		path.add(Arrays.asList(1, 3));
		path.add(Arrays.asList(0, 3));
		test("sampleMaze.txt", path, true);
		
		/*path.add(Arrays.asList(1, 0));
		path.add(Arrays.asList(1, 1));
		path.add(Arrays.asList(0, 1));
		test("simpleMaze.txt", path, true);*/
	}
	
	public static void test(String filename, List<List<Integer>> realPath, boolean showEachProb){
		MazeWorld maze = new MazeWorld(filename);
		String colorRead = maze.generateColor(realPath, true);
		String realColor = maze.generateColor(realPath, false);
		ProbReasoning pr = new ProbReasoning(maze, colorRead);
		pr.getDistribution();
		maze.printGrid();
		System.out.println("Real Path Color: " + realColor);
		maze.printPath(realPath);
		System.out.println("Color Read: " + colorRead);
		System.out.println();
		
		if(showEachProb){
			for(int i = 0; i < realPath.size(); i++)
				pr.printProbs(i);
		}
	}
}
