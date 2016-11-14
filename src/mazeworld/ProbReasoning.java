package mazeworld;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ProbReasoning {
	//Store the maze
	private MazeWorld maze;
	//Store the distribution of each step
	private HashMap<Integer, HashMap<Coor, Double>> distributions;
	//Store the valid position in the maze
	private HashSet<Coor> validPos;
	//Store the color read by the sensor
	private String colorsRead;
	//Store how many positions are valid
	private int validPosNum;

	public class Coor {
		protected int x;
		protected int y;

		public Coor(int _x, int _y) {
			x = _x;
			y = _y;
		}

		@Override
		public boolean equals(Object other) {
			return this.x == ((Coor) (other)).x && this.y == ((Coor) (other)).y;
		}

		@Override
		public int hashCode() {
			return x * 37 + y;
		}

		@Override
		public String toString(){
			return "[" + Integer.toString(x) + "," + Integer.toString(y) + "]";
		}
	}

	public ProbReasoning(MazeWorld m, String cr) {
		maze = m;
		distributions = new HashMap<>();
		validPos = new HashSet<>();
		for (int i = 0; i < maze.height; i++) {
			for (int j = 0; j < maze.width; j++) {
				if (maze.isSafe(i, j))
					validPos.add(new Coor(i, j));
			}
		}

		colorsRead = cr;
		colorsRead.length();
		validPosNum = validPos.size();
	}

	public void getDistribution() {
		for(int i = 0; i < colorsRead.length(); i++)
			predict(i);
	}

	public void predict(int curStep) {
		distributions.put(curStep, new HashMap<>());
		if (curStep == 0) {
			for (Coor coor : validPos) {
				Coor curCoor = new Coor(coor.x, coor.y);
				distributions.get(curStep).put(curCoor, 1.0 / validPosNum);
			}
		} else {
			HashMap<Coor, Double> preDistr = distributions.get(curStep - 1);
			for (Coor coor : validPos) {
				Coor curCoor = new Coor(coor.x, coor.y);
				double curCoorDistr = getCurCoorDistr(curCoor, preDistr);
				distributions.get(curStep).put(curCoor, curCoorDistr);
			}
		}
		update(curStep);
		normalize(curStep);
	}

	private void update(int curStep) {
		char curReadColor = colorsRead.charAt(curStep);
		for (Coor coor : validPos) {
			char coorColor = maze.getColor(coor.x, coor.y);
			double updateDistr = distributions.get(curStep).get(coor);
			if (coorColor == curReadColor)
				updateDistr *= 0.88;
			else
				updateDistr *= 0.04;
			distributions.get(curStep).put(coor, updateDistr);
		}
	}

	private void normalize(int curStep) {
		HashMap<Coor, Double> curDistribution = distributions.get(curStep);
		double totalSum = 0;
		for (Coor coor : validPos) {
			totalSum += curDistribution.get(coor);
		}
		for (Coor coor : validPos) {
			double curCoorDistr = curDistribution.get(coor);
			curCoorDistr /= totalSum;
			curDistribution.put(coor, curCoorDistr);
		}
	}

	private double getCurCoorDistr(Coor curCoor, HashMap<Coor, Double> preDistr) {
		int[] dir = { 0, 1, 0, -1, 0 };
		double res = 0;
		double inValidAdjPos = 0;
		for (int i = 0; i < dir.length - 1; i++) {
			Coor adjCoor = new Coor(curCoor.x + dir[i], curCoor.y + dir[i + 1]);
			if (validPos.contains(adjCoor)) 
				res += preDistr.get(adjCoor) * 0.25;
			else inValidAdjPos++;
		}
		res += preDistr.get(curCoor) * inValidAdjPos * 0.25;
		return res;
	}

	
	private double[] getProbs(int curStep){
		if(distributions.size() < colorsRead.length()){
			double[] res = {-1};
			System.out.println("Estimation not done yet!");
			return res;
		}
		HashMap<Coor, Double> curDistr = distributions.get(curStep);
		double[] res = new double[maze.width * maze.height];
		Arrays.fill(res, 0);
		for(Coor coor : validPos){
			res[coor.x * maze.width + coor.y] = curDistr.get(coor);
		}
		return res;
	}
	
	public void printProbs(int curStep){
		double[] probs = getProbs(curStep);
		System.out.println("Step: " + Integer.toString(curStep));
		for(int i = 0; i < probs.length; i++){	
			System.out.print("[" + Integer.toString(i / maze.width));
			System.out.print("," + Integer.toString(i % maze.width) + "]");
			System.out.print(" " + Double.toString(probs[i]));
			System.out.println();
		}
	}
}
