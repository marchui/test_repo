//**********************************************************************************************************
//
// Name        : Marcus Hui
// Class       : CISC 665
// Subject     : Distributed Systems
// Semester    : Spring 2009
// Description : This is assignment 1 of CISC 665, Distributed System. This project asked the students to
// 				 build a word count server to count the number of words from a text file that is
//				 uploaded by a client. This project uses client-server network communication protocol
//				 to transfer files (TCP). This is the client program.
//
//				 This client program will serves as the main user interface to the user and will allow
//				 the user to select a predefined set of commands to send to the server. User can upload
//				 files on the server, update files, remove files, view files and to see the file statistics.
//
//				 The client side program will also be use to manage cache files. The cache size (the number
//				 of text files that can be save in the client computer) can be adjusted. The default value
//				 is 5 files. The connection hostname and port number can also be changed. When no specific
//				 hostname, port number, and cache size is given; the default value will be used. The
//				 default value for hostname is "localhost" and port number is 12345.
//
//**********************************************************************************************************


import java.io.*;
import java.net.*;
import java.util.*;

class WCClient
{
	private static BufferedReader inFromUser = null;			//The command line reader.
	private static Socket clientSocket = null;					//Socket connection object
	private static DataOutputStream outToServer = null;			//Messager sender to the server
	private static BufferedReader inFromServer = null;			//Messager reader from the server
	private static String hostName = "localhost";				//Default host name
	private static int port = 12345;							//Default port size
	private static int cacheSize = 5;							//Default cache size
	private static String username = new String();				//User's name
	private static String cacheDirName = "cachefile";			//Cache file folder name
	private static File cacheDir = new File(cacheDirName);		//Cache file folder object
	private static String seperator = "#*#*#*#*#";				//Special seperator for data files
	private static String [] cacheFiles;						//Cache file array
	private static int cacheArrayIndex = 0;						//Cache file array index


	//**********************
	//
	// private static void openClientConnection()
	//
	// This method handles the client's TCP connection to the word count server. Every
	// time the client needs to connect to the word count server, this method will be called.
	//
	//**********************
	private static void openClientConnection()
	{
		try
		{
			clientSocket = new Socket(hostName, port);
			inFromUser = new BufferedReader(new InputStreamReader(System.in));
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			cacheFiles = new String[cacheSize];
		}
		catch(UnknownHostException e)
		{
			System.out.println("Error: Problem locating host!!!");
			System.exit(0);
		}
		catch(IOException ee)
		{
			System.out.println("Error: Connection to server refused!!!");
			System.exit(0);
		}
	}



	//**********************
	//
	// private static void closeClientConnection()
	//
	// This method will close the connection to the word count server. Every time the client
	// is done with the server, this method will be called.
	//
	//**********************
	private static void closeClientConnection()
	{
		try
		{
			clientSocket.close();
			inFromUser.close();
			outToServer.close();
			inFromServer.close();
		}
		catch(IOException e)
		{
			System.out.println("Error: Connection to server refused!!!");
			System.exit(0);
		}
	}



	//**********************
	//
	// private static String mainMenu()
	//
	// This is the method that is used to display the main menu.
	// This method will handle all basic user requests.
	//
	//**********************
	private static String mainMenu()
		throws IOException
	{
		String value = new String();

		do
		{
			System.out.println("\n1. Store new file");
			System.out.println("2. Update existing file");
			System.out.println("3. Remove file");
			System.out.println("4. List files");
			System.out.println("5. Read file");
			System.out.println("6. Character count");
			System.out.println("7. Word count");
			System.out.println("8. Line count");
			System.out.println("9. Total system count");
			System.out.println("q. Quit\n");
			System.out.print("Please enter your choice (1-9, q to quit): ");
			value = inFromUser.readLine();

			if(value.length() == 0)
				System.out.println("\nPlease enter a valid choice!\n");

		} while(value.length() == 0);

		return value;
	}



	//**********************
	//
	// private static void storeFile()
	//
	// This is the method that is used to send file storage information to the word count server.
	//
	//**********************
	private static void storeFile()
		throws IOException
	{
		String newFileName = null;
		String serverResponse = null;
		File newFile = null;

		outToServer.writeBytes(username + ":storefile\n");

		System.out.print("Enter the name of the new file for storage: ");
		newFileName = inFromUser.readLine();
		newFile = new File(newFileName);

		if(newFile.exists() && newFile.isFile())
		{
			outToServer.writeBytes(newFile.getName() + '\n');
			serverResponse = inFromServer.readLine();

			if(serverResponse.equals("###FileExists###"))
			{
				System.out.println("'" + newFile.getName() + "' already exist in the server. Please select another file or choose update!\n\n");
			}
			else if(serverResponse.equals("###FileNotExist###"))
			{
				BufferedReader newBuff = new BufferedReader(new FileReader(newFile));
				while(newBuff.ready())
				{
					outToServer.writeBytes(newBuff.readLine() + "\n");
					serverResponse = inFromServer.readLine();
				}
				newBuff.close();

				outToServer.writeBytes("eof\n");
				serverResponse = inFromServer.readLine();
				if(serverResponse.equals("###success###"))
				{
					System.out.println("New file '" + newFile.getName() + "' uploaded successfully!\n\n");
				}
				else if(serverResponse.equals("###error###"))
				{
					System.out.println("New file '" + newFile.getName() + "' cannot be stored! New file is not built according to the grammar!\n\n");
				}
			}
		}
		else
		{
			outToServer.writeBytes("###error###\n");
			System.out.println("Local file '" + newFile.getName() + "' cannot be found!\n\n");
		}
	}



	//**********************
	//
	// private static void updateFile()
	//
	// This method is used to update an existing file. If there is no existing file
	// matches with the file the user is sending. The method will exit and print an
	// error message.
	//
	//**********************
	private static void updateFile()
		throws IOException
	{
		String newFileName = null;
		String serverResponse = null;
		File newFile = null;

		outToServer.writeBytes(username + ":updatefile\n");

		//Testing creating file through client side
		System.out.print("Enter the name of the file for update: ");
		newFileName = inFromUser.readLine();
		newFile = new File(newFileName);

		if(newFile.exists() && newFile.isFile())
		{
			outToServer.writeBytes(newFile.getName() + '\n');
			serverResponse = inFromServer.readLine();

			if(serverResponse.equals("###FileNotExist###"))
			{
				System.out.println("'" + newFile.getName() + "' does not exist in the server. Please select another file or choose store!\n\n");
			}
			else if(serverResponse.equals("###FileExists###"))
			{
				BufferedReader newBuff = new BufferedReader(new FileReader(newFile));
				while(newBuff.ready())
				{
					outToServer.writeBytes(newBuff.readLine() + "\n");
					serverResponse = inFromServer.readLine();
				}
				newBuff.close();

				outToServer.writeBytes("eof\n");
				serverResponse = inFromServer.readLine();
				if(serverResponse.equals("###success###"))
				{
					System.out.println("File '" + newFile.getName() + "' updated successfully!\n\n");
				}
				else if(serverResponse.equals("###error###"))
				{
					System.out.println("File '" + newFile.getName() + "' cannot be updated! New file is not built according to the grammar!\n\n");
				}
			}
		}
		else
		{
			outToServer.writeBytes("###error###\n");
			System.out.println("Local file '" + newFile.getName() + "' cannot be found!\n\n");
		}
	}



	//**********************
	//
	// private static void removeFile()
	//
	// This method is used to remove a file from the server's datafile directory.
	// If the file does not exist, the method will exit and print an error
	// message.
	//
	//**********************
	private static void removeFile()
		throws IOException
	{
		String removeFileName;
		String serverResponse;

		outToServer.writeBytes(username + ":removefile\n");
		System.out.print("Please enter the name of the file to be removed: ");
		removeFileName = inFromUser.readLine();

		outToServer.writeBytes(removeFileName + "\n");
		serverResponse = inFromServer.readLine();

		if(serverResponse.equals("###NoFileFound###"))
		{
			System.out.println("Error: The word count server does not contain the file '" + removeFileName + "'!\n\n");
		}
		else if(serverResponse.equals("###RemovalSuccess###"))
		{
			System.out.println("File removal successful. '" + removeFileName + "' is removed!\n\n");

			//Remove the file from cache.
			File tempFile = new File(cacheDirName + "/" + removeFileName);
			if(tempFile.exists())
			{
				tempFile.delete();

				for(int i = 0; i < cacheSize; i++)
				{
					if(cacheFiles[i] != null && cacheFiles[i].equals(removeFileName))
					{
						for(int j = i; j < cacheSize-1; j++)
							cacheFiles[j] = cacheFiles[j+1];

						cacheFiles[cacheSize-1] = null;

						for(int k = 0; k < cacheSize; k++)
							if(cacheFiles[k] == null)
							{
								cacheArrayIndex = k;
								break;
							}
						break;
					}
				}
			}
		}
	}



	//**********************
	//
	// private static void listFile()
	//
	// This method is used to print a list of files in the server's data file directory for the user.
	//
	//**********************
	private static void listFile()
		throws IOException
	{
		String serverResponse;

		outToServer.writeBytes(username + ":listfile\n");
		serverResponse = inFromServer.readLine();

		if(serverResponse.equals("###nofiles###"))
		{
			System.out.println("There are currently no files in the server's data center!\n\n");
		}
		else
		{
			System.out.println("The following are the list of files in the server's data center:");
			System.out.println(serverResponse);
			outToServer.writeBytes("next\n");
			serverResponse = inFromServer.readLine();

			while(!serverResponse.equals("###eol###"))
			{
				System.out.println(serverResponse);
				outToServer.writeBytes("next\n");
				serverResponse = inFromServer.readLine();
			}

			System.out.println("\n\n");
		}
	}



	//**********************
	//
	// private static void readFile()
	//
	// This method will print the content of a file. This method will retrieve the file from
	// cache.
	//
	//**********************
	private static void readFile()
		throws IOException
	{
		String filename = null;
		String sentence = null;
		File readFile = null;

		System.out.print("Please enter the name of the file you would like to read: ");
		filename = inFromUser.readLine();

		if(filename == null || filename.length() == 0)
		{
			System.out.println("User must enter a valid filename.\n\n");
			return;
		}

		try
		{
			readFile = cache(filename);
		}
		catch(NullPointerException npe)
		{
			System.out.println("File '" + filename + "' does not exist!\n\n");
			return;
		}

		BufferedReader br = new BufferedReader(new FileReader(readFile));
		sentence = br.readLine();

		System.out.println("\n");
		while(br.ready() && !sentence.equals(seperator))
		{
			System.out.println(sentence);
			sentence = br.readLine();
		}
		br.close();
		System.out.println("\n\n");
	}



	//**********************
	//
	// private static File cache(String)
	//
	// This method will return the file in the client's cache. If no file is
	// found, this method will download the file from the file server. Also,
	// if the file in the cache folder is outdated, a new copy of the file
	// will be downloaded from the server.
	//
	// The cache can only hold a limit number of files in the folder depending
	// on the cache size. If that number is reached, the oldest downloaded file
	// will be removed to make room for a new file.
	//
	//**********************
	private static File cache(String filename)
		throws IOException
	{
		BufferedWriter newFileWriter = null;
		String newSentence = null;
		File tempFile = new File(cacheDirName + "/" + filename);
		tempFile.deleteOnExit();

		if(!tempFile.exists())
		{
			outToServer.writeBytes(username + ":readfile\n");
			outToServer.writeBytes("###getfile###\n");
			outToServer.writeBytes(filename + "\n");
			newSentence = inFromServer.readLine();

			if(newSentence.equals("###notexist###"))
				throw new NullPointerException();

			//Creating cache file
			tempFile.createNewFile();
			newFileWriter = new BufferedWriter(new FileWriter(tempFile));
			outToServer.writeBytes("ready\n");

			while(!newSentence.equals("eof"))
			{
				newFileWriter.write(newSentence);
				newFileWriter.newLine();
				newSentence = inFromServer.readLine();
				outToServer.writeBytes("ready\n");
			}

			newFileWriter.write("eof");
			newFileWriter.newLine();
			newFileWriter.close();
		}
		else
		{
			outToServer.writeBytes(username + ":readfile\n");
			outToServer.writeBytes("###checkmoddate###\n");
			outToServer.writeBytes(filename + "\n");
			String modDate = inFromServer.readLine();

			if(modDate.equals("###notexist###"))
				throw new NullPointerException();

			String modDate2 = new String();
			BufferedReader tempBR = new BufferedReader(new FileReader(tempFile));
			while(tempBR.ready())
			{
				modDate2 = tempBR.readLine();
				if(modDate2.startsWith("moddate"))
					break;
			}
			modDate2 = modDate2.substring(8);
			tempBR.close();

			if(!modDate.equals(modDate2))
			{
				outToServer.writeBytes("###getfile###\n");
				tempFile.delete();
				tempFile.createNewFile();
				newFileWriter = new BufferedWriter(new FileWriter(tempFile));
				newSentence = inFromServer.readLine();
				outToServer.writeBytes("ready\n");

				while(!newSentence.equals("eof"))
				{
					newFileWriter.write(newSentence);
					newFileWriter.newLine();
					newSentence = inFromServer.readLine();
					outToServer.writeBytes("ready\n");
				}

				newFileWriter.write("eof");
				newFileWriter.newLine();
				newFileWriter.close();
			}
			else
			{
				outToServer.writeBytes("###stillgood###\n");
			}
		}

		checkCacheArray(filename);
		return tempFile;
	}



	//**********************
	//
	// private static void checkCacheArray(String)
	//
	// This method is used to clean up and make adjustment to the cache array and it's index.
	//
	//**********************
	private static void checkCacheArray(String filename)
	{
		boolean found = false;

		//Cleaning up leftover filenames stored in the cache array
		for(int i = 0; i < cacheSize; i++)
		{
			if(cacheFiles[i] != null)
			{
				File tempFile = new File(cacheDirName + "/" + cacheFiles[i]);
				if(!tempFile.exists())
				{
					for(int j = i; j < cacheSize-1; j++)
						cacheFiles[j] = cacheFiles[j+1];

					cacheFiles[cacheSize-1] = null;
					i--;
				}
			}
		}

		//Readjusting cache array index
		for(int k = 0; k < cacheSize; k++)
			if(cacheFiles[k] == null)
			{
				cacheArrayIndex = k;
				break;
			}

		//Adding new filename to the cache array
		for(int i = 0; i < cacheSize; i++)
		{
			if(filename.equals(cacheFiles[i]))
			{
				found = true;
				break;
			}
		}

		if(!found)
		{
			if(cacheFiles[cacheArrayIndex] != null)
			{
				File oldFile = new File(cacheDirName + "/" + cacheFiles[cacheArrayIndex]);
				oldFile.delete();
			}
			cacheFiles[cacheArrayIndex] = filename;
			cacheArrayIndex = (cacheArrayIndex == cacheSize - 1)? 0 : cacheArrayIndex + 1;
		}
	}



	//**********************
	//
	// private static void charCount()
	//
	// This method will return the character count of a file. This method will
	// retrieve the file from cache.
	//
	//**********************
	private static void charCount()
		throws IOException
	{
		String filename = null;
		String sentence = null;
		File readFile = null;

		System.out.print("Please enter the name of the file you would like to receive character count: ");
		filename = inFromUser.readLine();

		if(filename == null || filename.length() == 0)
		{
			System.out.println("User must enter a valid filename.\n\n");
			return;
		}

		try
		{
			readFile = cache(filename);
		}
		catch(NullPointerException npe)
		{
			System.out.println("File '" + filename + "' does not exist!\n\n");
			return;
		}

		BufferedReader br = new BufferedReader(new FileReader(readFile));
		do
		{
			sentence = br.readLine();
		} while(br.ready() && !sentence.startsWith("charcount"));
		br.close();

		System.out.println("\nFile '" + filename + "' contains " + sentence.substring(10) + " number of characters.\n\n");
	}



	//**********************
	//
	// private static void wordCount()
	//
	// This method will return the word count of a file. This method will
	// retrieve the file from cache.
	//
	//**********************
	private static void wordCount()
		throws IOException
	{
		String filename = null;
		String sentence = null;
		File readFile = null;

		System.out.print("Please enter the name of the file you would like to receive word count: ");
		filename = inFromUser.readLine();

		if(filename == null || filename.length() == 0)
		{
			System.out.println("User must enter a valid filename.\n\n");
			return;
		}

		try
		{
			readFile = cache(filename);
		}
		catch(NullPointerException npe)
		{
			System.out.println("File '" + filename + "' does not exist!\n\n");
			return;
		}

		BufferedReader br = new BufferedReader(new FileReader(readFile));
		do
		{
			sentence = br.readLine();
		} while(br.ready() && !sentence.startsWith("wordcount"));
		br.close();

		System.out.println("\nFile '" + filename + "' contains " + sentence.substring(10) + " number of words.\n\n");
	}



	//**********************
	//
	// private static void lineCount()
	//
	// This method will return the line count of a file. This method will
	// retrieve the file from cache.
	//
	//**********************
	private static void lineCount()
		throws IOException
	{
		String filename = null;
		String sentence = null;
		File readFile = null;

		System.out.print("Please enter the name of the file you would like to receive line count: ");
		filename = inFromUser.readLine();

		if(filename == null || filename.length() == 0)
		{
			System.out.println("User must enter a valid filename.\n\n");
			return;
		}

		try
		{
			readFile = cache(filename);
		}
		catch(NullPointerException npe)
		{
			System.out.println("File '" + filename + "' does not exist!\n\n");
			return;
		}

		BufferedReader br = new BufferedReader(new FileReader(readFile));
		do
		{
			sentence = br.readLine();
		} while(br.ready() && !sentence.startsWith("linecount"));
		br.close();

		System.out.println("\nFile '" + filename + "' contains " + sentence.substring(10) + " number of lines.\n\n");
	}



	//**********************
	//
	// private static void totalSystemCount()
	//
	// This method will return the total character, word, and line count
	// of all the files in the server.
	//
	//**********************
	private static void totalSystemCount()
		throws IOException
	{
		outToServer.writeBytes(username + ":totalsyscount\n");
		String numOfFiles = inFromServer.readLine();
		String totCharCount = inFromServer.readLine();
		String totWordCount = inFromServer.readLine();
		String totLineCount = inFromServer.readLine();

		System.out.println("There are:");
		System.out.println(totCharCount + " number of characters");
		System.out.println(totWordCount + " number of Words");
		System.out.println(totLineCount + " number of Lines");
		System.out.println("In a total of " + numOfFiles + " files\n\n");
	}



	//**********************
	//
	// private static void exitServer()
	//
	// This method will inform the server that the user is exiting the client
	// program so the server need not to expect more request from the user.
	//
	//**********************
	private static void exitServer()
		throws IOException
	{
		outToServer.writeBytes(username + ":exit\n");
	}



	//**********************
	//
	// public static void main(String)
	//
	// This is the main function for the word count client. This method will
	// call on the main menu and handle all user input for their respective
	// actions.
	//
	//**********************
	public static void main(String argv[])
		throws Exception
	{
		try
		{
			String choice;		//The place holder for the user's choice

			//Create a cache directory for file storage
			if(!cacheDir.exists())
				cacheDir.mkdir();

			//Handling incoming values for hostname, port, and cache size.
			try
			{
				String tempHostName = hostName;
				int tempPort = port;
				int tempCacheSize = cacheSize;

				if(argv.length > 0)
				{
					for(int i = 0; i < argv.length; i++)
					{
						StringTokenizer strTok = new StringTokenizer(argv[i], "=");
						String tempStr = strTok.nextToken();

						if(tempStr.equals("hostname"))
							tempHostName = strTok.nextToken();
						else if(tempStr.equals("port"))
							tempPort = Integer.parseInt(strTok.nextToken());
						else if(tempStr.equals("cache"))
							tempCacheSize = Integer.parseInt(strTok.nextToken());
					}

					hostName = tempHostName;
					port = tempPort;
					cacheSize = tempCacheSize;
				}
			}
			catch(NoSuchElementException e)
			{
				System.out.println("Error reading hostname, port, or cache size values. Default values will be used!\n");
			}
			catch(NumberFormatException ee)
			{
				System.out.println("Error parsing port or cache size values. Default values will be used!\n");
			}

			openClientConnection();

			System.out.print("Please enter your name: ");
			username = inFromUser.readLine();

			if(username.length() == 0)
				username = "user";

			do{
				choice = mainMenu();

				if(choice != null && choice.length() > 0)
				{
					switch(choice.charAt(0))
					{
						case '1':
							storeFile();				//New file storage
							break;
						case '2':
							updateFile();				//Update existing file
							break;
						case '3':
							removeFile();				//Remove file from server
							break;
						case '4':
							listFile();					//List files in the server
							break;
						case '5':
							readFile();					//Open and read the content of a file
							break;
						case '6':
							charCount();				//Character count of a file
							break;
						case '7':
							wordCount();				//Word count of a file
							break;
						case '8':
							lineCount();				//Line count of a file
							break;
						case '9':
							totalSystemCount();			//Total character, word, and line count in the server
							break;
						case 'q':
						case 'Q':
							exitServer();				//Exit
							break;
						default:
							System.out.println("\nPlease enter a valid choice!\n");
							break;
					}
				}

			}while((choice.charAt(0) != 'q') && (choice.charAt(0) != 'Q'));


			closeClientConnection();
			System.out.println("Thank you for using the word count system " + username + "!");
		}
		catch(IOException ioe)
		{
			System.out.println("Error: Connection with server losted. Exiting application!");
			System.exit(0);
		}
		catch(NullPointerException npe)
		{
			System.out.println("\nError: User aborted. Exiting application!");
			System.exit(0);
		}
	}
}