/*
 * Designer: Marcus Hui
 * Date: 10 Aug, 2011
 * 
 * Problem Description:
 * You are in a room with a circle of 100 occupied chairs.  
 * The chairs are numbered sequentially from 1 to 100.
 * 
 * At some point in time, the person in chair #1 will be asked to leave.  
 * The person in chair #2 will be skipped, and the person in chair #3 
 * will be asked to leave.  This pattern of skipping one person and 
 * asking the next to leave will keep going around the circle until 
 * there is one person left: the survivor.
 * 
 * Write a program to determine which chair the survivor is sitting in.  
 * Please send us the answer and the working code you used to figure it 
 * out.
 */

import java.util.*;

class Chairs
{
	public static void main(String arg[])
	{
		ArrayList <Integer> chairList = new ArrayList<Integer>();
		int index = 0;

		//This for loop will populate the list of chairs
		for(int i = 0; i < 100; i++)
			chairList.add(i+1);

		//The main while loop will continue until there is only one person 
		//left in the circle of chairs
		while(chairList.size() > 1)
		{
			boolean lastelement = (index == chairList.size()-1) ? true : false;
			chairList.remove(index);
			
			if(lastelement)
				index = 1;
			else
				index = (index+1 >= chairList.size()) ? 0 : index+1;
		}

		//Output of the solution
		System.out.println("Final survivor is chair " + chairList.get(0));
	}
}
