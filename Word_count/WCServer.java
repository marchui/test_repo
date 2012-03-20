//**********************************************************************************************************
//
// Name        : Marcus Hui
// Class       : CISC 665
// Subject     : Distributed Systems
// Semester    : Spring 2009
// Description : This is assignment 1 of CISC 665, Distributed System. This project asked the students to
// 				 build a word count server to count the number of words from a text file that is
//				 uploaded by a client. This project uses client-server network communication protocol
//				 to transfer files (TCP). This is the server program.
//
//				 The server program is used to handle all incoming client connections and will serve
//				 the client program depending on the request given by the user. The server can handle
//				 multiple connections from the user. The connection handling mechanism is built using
//				 java's Thread class.
//
//				 The server also handles the data files that are upload by the users. All data files
//				 are save in the server's datafile folder and will be transfer to the user upon
//				 request. The connection port can be changed, it uses port 12345 by default.
//
//**********************************************************************************************************


import java.io.*;
import java.net.*;
import java.util.*;

class WCServer
{
	private static int port = 12345;								//The default connection port
	private static String dataFileDirName = "datafile";				//The data file folder name
	private static File dataFileDir = new File(dataFileDirName);	//The data file folder object
	private static String username = new String();					//The connecting user's name
	private static int totalWordCount = 0;							//The total word count of all the files
	private static int totalLineCount = 0;							//The total line count of all the files
	private static int totalCharCount = 0;							//The total character count of all the files
	private static String seperator = "#*#*#*#*#";					//The special seperator for data files

	//**********************
	//
	// private static void createFile(DataOutputStream, BufferedReader)
	//
	// This method is used for creating a new file in the word count server data center.
	// This method is only use for creating a new file.
	//
	//**********************
	private static void createFile(DataOutputStream out, BufferedReader in)
		throws IOException
	{
		try
		{
			System.out.println(username + " request to store a new file.");

			String fileName;
			String fileSentence;

			fileName = in.readLine();

			if(!fileName.equals("###error###"))
			{
				File newFile = new File(dataFileDirName + "/" + fileName);

				if(newFile.exists())
				{
					out.writeBytes("###FileExists###\n");
					System.out.println("File '" + fileName + "' already exist in the datafile directory.");
				}
				else
				{
					newFile.createNewFile();
					BufferedWriter newFileWriter = new BufferedWriter(new FileWriter(newFile));
					out.writeBytes("###FileNotExist###\n");
					fileSentence = in.readLine();

					while(!fileSentence.equals("eof"))
					{
						newFileWriter.write(fileSentence);
						newFileWriter.newLine();
						out.writeBytes("ready\n");
						fileSentence = in.readLine();
					}

					newFileWriter.close();

					//Inspecting file for bad characters and word count
					if(!inspectFile(newFile))
					{
						newFile.delete();
						System.out.println("New file '" + fileName + "' cannot be stored!");
						out.writeBytes("###error###\n");
					}
					else
					{
						System.out.println("New file '" + fileName + "' created by user " + username + ".");
						out.writeBytes("###success###\n");
					}
				}
			}
			else
			{
				System.out.println(username + " file creation aborted!");
			}
		}
		catch(NullPointerException e)
		{
			System.out.println("Error creating new file: " + e.toString());
		}
	}



	//**********************
	//
	// private static void updateFile(DataOutputStream, BufferedReader)
	//
	// This method will update an existing file from the user. The user
	// will send an updated version of the file from the client program.
	//
	//**********************
	private static void updateFile(DataOutputStream out, BufferedReader in)
		throws IOException
	{
		try
		{
			System.out.println(username + " request to update a file in the datafile directory.");

			String fileName;
			String fileSentence;

			fileName = in.readLine();

			if(!fileName.equals("###error###"))
			{
				File newFile = new File(dataFileDirName + "/" + fileName);

				if(!newFile.exists() || !newFile.isFile())
				{
					out.writeBytes("###FileNotExist###\n");
					System.out.println("File '" + fileName + "' does not exist in the datafile directory.");
				}
				else
				{
					File newFile2 = new File(dataFileDirName + "/tempupdatefile.txt");
					newFile2.createNewFile();
					BufferedWriter newFileWriter = new BufferedWriter(new FileWriter(newFile2));
					out.writeBytes("###FileExists###\n");
					fileSentence = in.readLine();

					while(!fileSentence.equals("eof"))
					{
						newFileWriter.write(fileSentence);
						newFileWriter.newLine();
						out.writeBytes("ready\n");
						fileSentence = in.readLine();
					}

					newFileWriter.close();

					//Inspecting file for bad characters and word count
					if(!inspectFile(newFile2))
					{
						newFile2.delete();
						System.out.println("File '" + fileName + "' cannot be updated!");
						out.writeBytes("###error###\n");
					}
					else
					{
						newFile.delete();
						newFile2.renameTo(newFile);
						System.out.println("File '" + fileName + "' updated by user " + username + ".");
						out.writeBytes("###success###\n");
					}
				}
			}
			else
			{
				System.out.println(username + " file update aborted!");
			}
		}
		catch(NullPointerException e)
		{
			System.out.println("Error updating file: " + e.toString());
		}
	}



	//**********************
	//
	// private static void removeFile(DataOutputStream, BufferedReader)
	//
	// This method is used to remove an existing file from the server's data file
	// directory.
	//
	//**********************
	private static void removeFile(DataOutputStream out, BufferedReader in)
		throws IOException
	{
		System.out.println(username + " request to remove a file in the datafile directory.");

		String fileName = in.readLine();
		File removeFile = new File(dataFileDirName + "/" + fileName);

		if(removeFile.exists() && removeFile.isFile())
		{
			removeFile.delete();
			System.out.println(username + " removed '" + fileName + "' from the datafile directory.");
			out.writeBytes("###RemovalSuccess###\n");
		}
		else
		{
			System.out.println("File '" + fileName + "' cannot be found.");
			out.writeBytes("###NoFileFound###\n");
		}
	}



	//**********************
	//
	// private static void listFile(DataOutputStream, BufferedReader)
	//
	// This method will send a list of files in the datafile directory to the user.
	//
	//**********************
	private static void listFile(DataOutputStream out, BufferedReader in)
		throws IOException
	{
		System.out.println(username + " request to list files in the datafile directory.");

		String [] fileList = dataFileDir.list();

		if (fileList.length == 0)
		{
			out.writeBytes("###nofiles###\n");
		}
		else
		{
			for(int i = 0; i < fileList.length; i++)
			{
				out.writeBytes(fileList[i] + "\n");
				in.readLine();
			}

			out.writeBytes("###eol###\n");
		}
	}



	//**********************
	//
	// private static void readFile(DataOutputStream, BufferedReader)
	//
	// This method will send the content of a file to the user.
	//
	//**********************
	private static void readFile(DataOutputStream out, BufferedReader in)
		throws IOException
	{
		System.out.println(username + " request to access a file in the datafile directory.");

		String action = in.readLine();
		String filename = in.readLine();
		File tempFile = new File(dataFileDirName + "/" + filename);

		if(!tempFile.exists())
		{
			System.out.println("File '" + filename + "' does not exist!");
			out.writeBytes("###notexist###\n");
		}
		else
		{
			BufferedReader fileBR = new BufferedReader(new FileReader(tempFile));
			String clientResponse = new String();

			if(action.equals("###getfile###"))
			{
				while(fileBR.ready())
				{
					out.writeBytes(fileBR.readLine() + "\n");
					clientResponse = in.readLine();
				}

				System.out.println("A new file has been sent to client " + username + ".");
			}
			else if(action.equals("###checkmoddate###"))
			{
				String modDate = new String();
				BufferedReader tempBR = new BufferedReader(new FileReader(tempFile));

				do
				{
					modDate = tempBR.readLine();
				} while(tempBR.ready() && !modDate.startsWith("moddate"));

				modDate = modDate.substring(8);
				tempBR.close();

				out.writeBytes(modDate + "\n");
				clientResponse = in.readLine();

				if(clientResponse.equals("###getfile###"))
				{
					while(fileBR.ready())
					{
						out.writeBytes(fileBR.readLine() + "\n");
						clientResponse = in.readLine();
					}

					System.out.println("A new file has been sent to client " + username + ".");
				}
			}

			fileBR.close();
		}
	}



	//**********************
	//
	// private static void totalSysCount(DataOutputStream, BufferedReader)
	//
	// This method will send the total character, word, and line count of
	// all the files stored in the server to the user.
	//
	//**********************
	private static void totalSysCount(DataOutputStream out, BufferedReader in)
		throws IOException
	{
		System.out.println(username + " request the total character, word, and line count value from files in the datafile directory.");
		countAllFiles();

		out.writeBytes(String.valueOf(dataFileDir.listFiles().length) + "\n");
		out.writeBytes(String.valueOf(totalCharCount) + "\n");
		out.writeBytes(String.valueOf(totalWordCount) + "\n");
		out.writeBytes(String.valueOf(totalLineCount) + "\n");
	}



	//**********************
	//
	// private static void countAllFiles()
	//
	// This is the method to count all the character, word, and lines in every
	// files stored in the server.
	//
	//**********************
	private static void countAllFiles()
		throws IOException
	{
		File [] fileAry = dataFileDir.listFiles();

		for(int i = 0; i < fileAry.length; i++)
		{
			BufferedReader buff = new BufferedReader(new FileReader(fileAry[i]));
			String tempStr = new String();

			do
			{
				tempStr = buff.readLine();
			}while(!tempStr.startsWith("charcount"));

			totalCharCount = totalCharCount + Integer.parseInt(tempStr.substring(10));
			totalWordCount = totalWordCount + Integer.parseInt(buff.readLine().substring(10));
			totalLineCount = totalLineCount + Integer.parseInt(buff.readLine().substring(10));
			buff.close();
		}
	}



	//**********************
	//
	// private static boolean inspectFile(File)
	//
	// This method will inspect an incoming file for bad characters. The inspection
	// will follow the grammar given on the assignment. This method will also count
	// the number of character, word, and lines in a file and store the information
	// in the file.
	//
	//**********************
	private static boolean inspectFile(File newFile)
		throws IOException
	{
		boolean success = true;
		int charcount = 0;
		int wordcount = 0;
		int linecount = 0;
		String modDate = new String();
		String tempStr = new String();
		BufferedReader newBuff = new BufferedReader(new FileReader(newFile));
		File tempFile = new File(dataFileDirName + "/tempinspecfile.txt");
		tempFile.createNewFile();
		BufferedWriter newFileWriter = new BufferedWriter(new FileWriter(tempFile));

		while(newBuff.ready())
		{
			tempStr = newBuff.readLine();
			linecount++;
			newFileWriter.write(tempStr);
			newFileWriter.newLine();

			for(int i = 0; i < tempStr.length(); i++)
			{
				if( (tempStr.charAt(i) >= 'a' && tempStr.charAt(i) <= 'z') ||
				    (tempStr.charAt(i) >= 'A' && tempStr.charAt(i) <= 'Z') ||
				    (tempStr.charAt(i) >= '0' && tempStr.charAt(i) <= '9'))
				{
					//The character in question is in good standing according to the grammar.
					charcount++;

					if(i == tempStr.length()-1 ||
					   tempStr.charAt(i+1) == ' ' ||
					   tempStr.charAt(i+1) == '.' ||
					   tempStr.charAt(i+1) == ':' ||
					   tempStr.charAt(i+1) == ';' ||
				       tempStr.charAt(i+1) == '-')
					{
						//The character is followed by a valid seperator,
						//therefore it is a word.
						wordcount++;
					}
				}
				else if(tempStr.charAt(i) == ':' ||
					   tempStr.charAt(i) == ';' ||
				       tempStr.charAt(i) == '-')
				{
					//Checking to see if the separator ':', ';', or '-' are between two characters
					//also checking to see if the separator is at the beginning or the end of the line
					//according to the grammar.
					if((i == 0) || (i == tempStr.length()-1) ||
					   !((tempStr.charAt(i-1) >= 'a' && tempStr.charAt(i-1) <= 'z') ||
					   (tempStr.charAt(i-1) >= 'A' && tempStr.charAt(i-1) <= 'Z') ||
					   (tempStr.charAt(i-1) >= '0' && tempStr.charAt(i-1) <= '9')) ||
					   !((tempStr.charAt(i+1) >= 'a' && tempStr.charAt(i+1) <= 'z') ||
					   (tempStr.charAt(i+1) >= 'A' && tempStr.charAt(i+1) <= 'Z') ||
					   (tempStr.charAt(i+1) >= '0' && tempStr.charAt(i+1) <= '9')) )
					{
						System.out.println("Separator '" + tempStr.charAt(i) + "' is not set between two unit of characters!");
						System.out.println("The line in question is: \n\t\"" + tempStr + "\"");
						success = false;
						break;
					}
				}
				else if(tempStr.charAt(i) == ' ')
				{
					//Checking to see if the separator ' ' are between two characters or blanks
					//also checking to see if the separator is at the beginning or the end of the line
					//according to the grammar.
					if((i == 0) || (i == tempStr.length()-1) ||
					   !((tempStr.charAt(i-1) >= 'a' && tempStr.charAt(i-1) <= 'z') ||
					   (tempStr.charAt(i-1) >= 'A' && tempStr.charAt(i-1) <= 'Z') ||
					   (tempStr.charAt(i-1) >= '0' && tempStr.charAt(i-1) <= '9') ||
					   tempStr.charAt(i-1) == ' ' || tempStr.charAt(i-1) == '.') ||
					   !((tempStr.charAt(i+1) >= 'a' && tempStr.charAt(i+1) <= 'z') ||
					   (tempStr.charAt(i+1) >= 'A' && tempStr.charAt(i+1) <= 'Z') ||
					   (tempStr.charAt(i+1) >= '0' && tempStr.charAt(i+1) <= '9') ||
					   tempStr.charAt(i+1) == ' ') )
					{
						System.out.println("Separator '" + tempStr.charAt(i) + "' is not set between two unit of characters or blanks!");
						System.out.println("The line in question is: \n\t\"" + tempStr + "\"");
						success = false;
						break;
					}
				}
				else if(tempStr.charAt(i) == '.')
				{
					//Checking to see if the separator '.' are between two characters or end with a blank
					//also checking to see if the separator is at the beginning or the end of the line
					//according to the grammar.
					if((i == 0) || (i == tempStr.length()-1) ||
					   !((tempStr.charAt(i-1) >= 'a' && tempStr.charAt(i-1) <= 'z') ||
					   (tempStr.charAt(i-1) >= 'A' && tempStr.charAt(i-1) <= 'Z') ||
					   (tempStr.charAt(i-1) >= '0' && tempStr.charAt(i-1) <= '9')) ||
					   !((tempStr.charAt(i+1) >= 'a' && tempStr.charAt(i+1) <= 'z') ||
					   (tempStr.charAt(i+1) >= 'A' && tempStr.charAt(i+1) <= 'Z') ||
					   (tempStr.charAt(i+1) >= '0' && tempStr.charAt(i+1) <= '9') ||
					   tempStr.charAt(i+1) == ' ') )


					{
						System.out.println("Separator '" + tempStr.charAt(i) + "' is not set between two unit of characters or follow by a blank!");
						System.out.println("The line in question is: \n\t\"" + tempStr + "\"");
						success = false;
						break;
					}
				}
				else
				{
					//The character in question is not one of the allowed character.
					System.out.println("The character '" + tempStr.charAt(i) + "' is not allowed!");
					System.out.println("The line in question is: \n\t\"" + tempStr + "\"");
					success = false;
					break;
				}
			}

			if(!success)
				break;
		}

		newBuff.close();

		if(success)
		{
			modDate = String.valueOf(newFile.lastModified());
			newFileWriter.write(seperator);
			newFileWriter.newLine();
			newFileWriter.write("charcount=" + charcount);
			newFileWriter.newLine();
			newFileWriter.write("wordcount=" + wordcount);
			newFileWriter.newLine();
			newFileWriter.write("linecount=" + linecount);
			newFileWriter.newLine();
			newFileWriter.write("moddate=" + modDate);
			newFileWriter.newLine();
			newFileWriter.write("eof");
			newFileWriter.newLine();
			newFileWriter.close();

			newFile.delete();
			tempFile.renameTo(newFile);
		}
		else
		{
			newFileWriter.close();
			tempFile.delete();
		}

		return success;
	}



	//**********************
	//
	// public static void main(String)
	//
	// This is the main function for the word count server. This method will receive
	// and handle all incoming commands from the user.
	//
	//**********************
	public static void main(String argv[])
		throws Exception
	{
		ServerSocket welcomeSocket = null;
		int newPort = port;

		//Handling incoming values for hostname, port, and cache size.
		try
		{

			if(argv.length > 0 && argv[0] != null && argv[0].length() > 0)
			{
				StringTokenizer strTok = new StringTokenizer(argv[0], "=");
				String tempStr = strTok.nextToken();

				if(tempStr.equals("port"))
					newPort = Integer.parseInt(strTok.nextToken());
				else
					System.out.println("Error reading port value. Default value will be used!\n");
			}
		}
		catch(NoSuchElementException e)
		{
			System.out.println("Error reading port value. Default value will be used!\n");
		}
		catch(NumberFormatException ee)
		{
			System.out.println("Error parsing port value. Default value will be used!\n");
		}


		//Start the server socket with a given port number.
		try
		{
			welcomeSocket = new ServerSocket(newPort);			//Create a server socket
		}
		catch(IllegalArgumentException eee)
		{
			System.out.println("Error connecting to given port. Default value will be used!\n");
			welcomeSocket = new ServerSocket(port);				//Create a server socket using the default port
		}
		catch(BindException eeee)
		{
			System.out.println("Error: Another instance of the server is already running on this machine. ");
			System.out.println("Please select another port or machine!");
			System.exit(0);
		}


		System.out.println("Word Count server started!");

		//Create a datafile directory for file storage
		if(!dataFileDir.exists())
		{
			dataFileDir.mkdir();
			System.out.println("'datafile' directed created");
		}
		else
			System.out.println("'datafile' directory already exist!");


		while(true)
		{
			try
			{
				//Start a new connection for each client
				ConnectionHandler newConn = new ConnectionHandler(welcomeSocket.accept());
				newConn.start();
			}
			catch(IOException e)
			{
				System.out.println("Error : " + e.toString());
			}
		}
	}



	//**********************
	//
	// private static class ConnectionHandler extends Thread
	//
	// This is a private class that handles incoming client connections.
	// A thread will be created for each new incoming client connection.
	//
	//**********************
	private static class ConnectionHandler extends Thread
	{
		Socket clientSocket;
		BufferedReader inFromClient;
		DataOutputStream outToClient;

		//**********************
		//
		// public ConnectionHandler(Socket)
		//
		// This is the constructor for the ConnectionHandler class.
		// This method will handle every new incoming client connections.
		//
		//**********************
		public ConnectionHandler(Socket newSock)
			throws IOException
		{
			clientSocket = newSock;
			inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToClient = new DataOutputStream(clientSocket.getOutputStream());
		}



		//**********************
		//
		// public void run()
		//
		// This is the thread start method. All the server interection with the client
		// will be handle in this method.
		//
		//**********************
		public void run()
		{
			try
			{
				String clientChoice;
				StringTokenizer strTok;

				do
				{
					clientChoice = inFromClient.readLine();
					strTok = new StringTokenizer(clientChoice, ":");
					username = strTok.nextToken();
					clientChoice = strTok.nextToken();

					if(clientChoice.equals("storefile"))
						createFile(outToClient, inFromClient);			//Store a new file
					else if(clientChoice.equals("updatefile"))
						updateFile(outToClient, inFromClient);			//Update an existing file
					else if(clientChoice.equals("removefile"))
						removeFile(outToClient, inFromClient);			//Remove an existing file
					else if(clientChoice.equals("listfile"))
						listFile(outToClient, inFromClient);			//List all files in the server
					else if(clientChoice.equals("readfile"))
						readFile(outToClient, inFromClient);			//Read the content of a file
					else if(clientChoice.equals("totalsyscount"))
						totalSysCount(outToClient, inFromClient);		//Total character, word, and line count for all files

				} while(!clientChoice.equals("exit"));

				clientSocket.close();
				inFromClient.close();
				outToClient.close();

				System.out.println("Service side done! User " + username + " has logoff the server.");
			}
			catch(IOException e)
			{
				System.out.println("Error: Client disconnected from server.");
			}
		}
	}
}