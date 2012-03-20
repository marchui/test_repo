//**********************************************************************************************************
//
// Name        : Marcus Hui
// Class       : CISC 685
// Subject     : Human-Computer Interaction
// Semester    : Winter 2010
// Description : This is assignment 1 of CISC 685: Human-Computer Interaction, Winter 2010.
//				 For this assignment, we were asked to write a donation collection application
//				 for a local basketball team's fund raiser program. The local basketball team
//				 made arrangement with a number of local supermarkets. The supermarkets offer
//				 members coupons which can be used to purchase goods from the store. The coupons
//				 come in $5, $10, and $25. Per each coupon sold, the supermarket will donate 5%
//				 of the value of the coupon to the local basketball team.
//
//				 This program is the result of the requirement. It is written in Java Swing. It
//				 will collect a number of information from the user. The required information
//				 are first name, last name, address, phone number and the number of coupons
//				 the user wants to purchase. Once all the information is gather up the program
//				 will calculate the user’s total purchase amount along with the amount of
//				 donation for the basketball team. Users are required to fill out all the
//				 personal information. The numbers of coupons the user wish to purchase have
//				 to be in integers. If the user enters an invalid choice the program will
//				 reset all the coupon fields to 0 and ask the user to enter a valid choice.
//				 A blank coupon text field will be treated as a 0.
//
//**********************************************************************************************************


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.net.URL;
import java.text.DecimalFormat;

public class Donation extends JPanel implements KeyListener, ActionListener
{
	static JFrame frame;
	JLabel label, informationLabel;
	JButton submitButton;
	JTextField firstNameText,
			   lastNameText,
			   addressText,
			   phoneText,
			   coupon1Text,
			   coupon2Text,
			   coupon3Text;
	JPanel namePanel, couponPanel;
	String tempMessage;
	Calendar date;
	GridBagConstraints gbc;
	DecimalFormat moneyFormat = new DecimalFormat("$#,##0.00");
	String [] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
						"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	//
	// public Donation()
	//
	// This is the Constructor for Donation class.
	//
	public Donation()
	{
		super(new GridBagLayout());
		date = Calendar.getInstance();
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;

		addTitle();
		addNamePanel();
		addCouponPanel();
		addInfoPanel();
	}


	//
	// private ImageIcon createImageIcon(String path)
	//
	// This method will create an image for the title.
	//
	private ImageIcon createImageIcon(String path)
	{
		java.net.URL imgURL = Donation.class.getResource(path);

		if (imgURL != null)
		{
			return new ImageIcon(imgURL);
		}
		else
		{
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}


	//
	// private void addTitle()
	//
	// This method will add title info on the main panel.
	//
	private void addTitle()
	{
		ImageIcon icon = this.createImageIcon("images/BBall.jpg");

		//Adding title to the panel
		tempMessage = "<html><i><font size='5'>Welcome to Marcus Hui's Basketball Fund</font></i></html>";
		label = new JLabel(tempMessage, icon, JLabel.CENTER);
		label.setHorizontalAlignment(JLabel.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.ipadx = 100;
		gbc.ipady = 20;
		gbc.weightx = 1.0;
		add(label, gbc);
		gbc.ipadx = 0;
		gbc.ipady = 4;
		gbc.gridwidth = 1;
	}


	//
	// private void addNamePanel()
	//
	// This method will add the personal information panel to the main panel.
	//
	private void addNamePanel()
	{
		//Creating a new panel that contains the personal information form
		namePanel = new JPanel();
		namePanel.setLayout(new GridBagLayout());
		namePanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Personal Information"),
			BorderFactory.createEmptyBorder(0,5,5,5)));
		namePanel.setToolTipText("This is the user's personal information panel");

		//Adding instruction for the name panel
		label = new JLabel("<html><u>Please enter your personal information below</u></html>");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		namePanel.add(label, gbc);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;

		//Adding component related stuff to namePanel
		//Today's date
		label = new JLabel("Today's Date : ");
		gbc.gridx = 0;
		gbc.gridy = 1;
		namePanel.add(label, gbc);
		label = new JLabel(months[date.get(Calendar.MONTH)] + " " + date.get(Calendar.DATE) + ", " + date.get(Calendar.YEAR));
		label.setToolTipText("This is today's date");
		gbc.gridx = 1;
		gbc.gridy = 1;
		namePanel.add(label, gbc);

		//First name text field
		label = new JLabel("First Name : ");
		gbc.gridx = 0;
		gbc.gridy = 2;
		namePanel.add(label, gbc);
		firstNameText = new JTextField(10);
		firstNameText.setToolTipText("Enter your First Name here");
		firstNameText.addKeyListener(this);
		gbc.gridx = 1;
		gbc.gridy = 2;
		namePanel.add(firstNameText, gbc);

		//Last name text field
		label = new JLabel("Last Name : ");
		gbc.gridx = 0;
		gbc.gridy = 3;
		namePanel.add(label, gbc);
		lastNameText = new JTextField(10);
		lastNameText.setToolTipText("Enter your Last Name here");
		lastNameText.addKeyListener(this);
		gbc.gridx = 1;
		gbc.gridy = 3;
		namePanel.add(lastNameText, gbc);

		//Address text field
		label = new JLabel("Address : ");
		gbc.gridx = 0;
		gbc.gridy = 4;
		namePanel.add(label, gbc);
		addressText = new JTextField(30);
		addressText.setToolTipText("Enter your Home Address here");
		addressText.addKeyListener(this);
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		namePanel.add(addressText, gbc);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;

		//Phone number text field
		label = new JLabel("Phone Number : ");
		gbc.gridx = 0;
		gbc.gridy = 5;
		namePanel.add(label, gbc);
		phoneText = new JTextField(10);
		phoneText.setToolTipText("Enter your Home Phone Number here");
		phoneText.addKeyListener(this);
		gbc.gridx = 1;
		gbc.gridy = 5;
		namePanel.add(phoneText, gbc);

		//Adding namePanel to personal information form
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		add(namePanel, gbc);
	}


	//
	// private void addCouponPanel()
	//
	// This method will add the coupon panel to the main panel.
	//
	private void addCouponPanel()
	{
		//Creating a new panel that contains the coupon purchase information form
		couponPanel = new JPanel();
		couponPanel.setLayout(new GridBagLayout());
		couponPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Coupon Selection"),
			BorderFactory.createEmptyBorder(0,5,5,5)));
		couponPanel.setToolTipText("This is the coupon selection panel");

		//Adding instruction for the coupon panel
		label = new JLabel("<html>Please select one or more of the following coupons " +
						   "by entering the <br>number of tickets you would like to purchase each:</html>");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 0.0;
		couponPanel.add(label, gbc);
		gbc.gridwidth = 1;

		//$5 coupon text field
		label = new JLabel("$5.00 Coupon        x");
		label.setHorizontalAlignment(JLabel.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0.3;
		couponPanel.add(label, gbc);
		coupon1Text = new JTextField(10);
		coupon1Text.setToolTipText("Enter the number of $5.00 coupon you would like to purchase here");
		coupon1Text.setText("0");
		coupon1Text.addKeyListener(this);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0.5;
		couponPanel.add(coupon1Text, gbc);
		label = new JLabel("coupons");
		label.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 0.1;
		couponPanel.add(label, gbc);

		//$10 coupon text field
		label = new JLabel("$10.00 Coupon      x");
		label.setHorizontalAlignment(JLabel.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0.3;
		couponPanel.add(label, gbc);
		coupon2Text = new JTextField(10);
		coupon2Text.setToolTipText("Enter the number of $10.00 coupon you would like to purchase here");
		coupon2Text.setText("0");
		coupon2Text.addKeyListener(this);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 0.5;
		couponPanel.add(coupon2Text, gbc);
		label = new JLabel("coupons");
		label.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.weightx = 0.1;
		couponPanel.add(label, gbc);

		//$25 coupon text field
		label = new JLabel("$25.00 Coupon      x");
		label.setHorizontalAlignment(JLabel.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 0.3;
		couponPanel.add(label, gbc);
		coupon3Text = new JTextField(10);
		coupon3Text.setToolTipText("Enter the number of $25.00 coupon you would like to purchase here");
		coupon3Text.setText("0");
		coupon3Text.addKeyListener(this);
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.weightx = 0.5;
		couponPanel.add(coupon3Text, gbc);
		label = new JLabel("coupons");
		label.setHorizontalAlignment(JLabel.LEFT);
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.weightx = 0.1;
		couponPanel.add(label, gbc);

		//Adding the submit button for the form
		submitButton = new JButton("Calculate Total Amount");
		submitButton.setToolTipText("Press here when you are ready to submit your purchase");
		submitButton.addActionListener(this);
		submitButton.addKeyListener(this);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		couponPanel.add(submitButton, gbc);

		//Adding the couponPanel to the main panel
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		add(couponPanel, gbc);
	}


	//
	// private void addInfoPanel()
	//
	// This method will add the information panel to the main panel.
	//
	private void addInfoPanel()
	{
		//Adding the information block.
		tempMessage = "<html>Please help our local community by purchasing one or more of the above coupons.<br>" +
		              " For every coupon purchase we will donate 5% of the proceeds to the local basketball team.<br>" +
		              " Your help is greatly appreciated.</html>";

		informationLabel = new JLabel(tempMessage);
		informationLabel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Important Messages"),
			BorderFactory.createEmptyBorder(5,5,5,5)));
		informationLabel.setToolTipText("This is information message panel");
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		add(informationLabel, gbc);
	}


	//
	// public void keyPressed(KeyEvent e)
	//
	// This method handles the key pressed event from the text fields.
	//
	public void keyPressed(KeyEvent e)
	{
		//This is the code that make the Enter button do what the Tab button does.
		//When Shift+Enter is pressed, it will act like Shift+Tab.
		if(e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiersEx() == 0)
		{
			if(firstNameText.isFocusOwner())
				lastNameText.requestFocusInWindow();
			else if(lastNameText.isFocusOwner())
				addressText.requestFocusInWindow();
			else if(addressText.isFocusOwner())
				phoneText.requestFocusInWindow();
			else if(phoneText.isFocusOwner())
				coupon1Text.requestFocusInWindow();
			else if(coupon1Text.isFocusOwner())
				coupon2Text.requestFocusInWindow();
			else if(coupon2Text.isFocusOwner())
				coupon3Text.requestFocusInWindow();
			else if(coupon3Text.isFocusOwner())
				submitButton.requestFocusInWindow();
			else if(submitButton.isFocusOwner())
				this.finalCount();
		}
		else if(e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiersEx() == 64)
		{
			if(firstNameText.isFocusOwner())
				coupon3Text.requestFocusInWindow();
			else if(lastNameText.isFocusOwner())
				firstNameText.requestFocusInWindow();
			else if(addressText.isFocusOwner())
				lastNameText.requestFocusInWindow();
			else if(phoneText.isFocusOwner())
				addressText.requestFocusInWindow();
			else if(coupon1Text.isFocusOwner())
				phoneText.requestFocusInWindow();
			else if(coupon2Text.isFocusOwner())
				coupon1Text.requestFocusInWindow();
			else if(coupon3Text.isFocusOwner())
				coupon2Text.requestFocusInWindow();
			else if(submitButton.isFocusOwner())
				this.finalCount();
		}
	}


	//
	// public void keyReleased(KeyEvent e)
	//
	// This is the required method from the interface KeyListener.
	// However, I have no use for this method.
	//
	public void keyReleased(KeyEvent e)
	{
		//Do Nothing...
	}


	//
	// public void keyTyped(KeyEvent e)
	//
	// This is the required method from the interface KeyListener.
	// However, I have no use for this method.
	//
	public void keyTyped(KeyEvent e)
	{
		//Do Nothing...
	}


	//
	// public void actionPerformed(ActionEvent e)
	//
	// This method will handle the button click event.
	//
	public void actionPerformed(ActionEvent e)
	{
		this.finalCount();
	}


	//
	// private void finalCount()
	//
	// This method is use to handle all the calculation after the user
	// submits the form. This method will check for empty required fields,
	// and the correct phone number format. It will post error messages
	// on the information panel.
	//
	// This method will also check for invalid entries in the coupon
	// number fields. Only intergers are allow in the coupon fields.
	//
	private void finalCount()
	{
		if(firstNameText.getText() == null || firstNameText.getText().length() == 0)
		{
			//User didn't enter his first name
			tempMessage = "<html><font color=red size='5'>*</font> Please enter your First Name.</html>";
		}
		else if(lastNameText.getText() == null || lastNameText.getText().length() == 0)
		{
			//User didn't enter his last name
			tempMessage = "<html><font color=red size='5'>*</font> Please enter your Last Name.</html>";
		}
		else if(addressText.getText() == null || addressText.getText().length() == 0)
		{
			//User didn't enter his address
			tempMessage = "<html><font color=red size='5'>*</font> Please enter your full Home Address.<br>" +
						  "Example: 123 Grant Ave, New York, New York, 12345</html>";
		}
		else if(phoneText.getText() == null || phoneText.getText().length() == 0)
		{
			//User didn't enter a phone number
			tempMessage = "<html><font color=red size='5'>*</font> Please enter your Home Phone Number starting" +
						  " with your area code.<br>" + "Example: (555)123-4567 or 555-123-4567</html>";
		}
		else
		{
			try
			{
				//Checking for the phone number's format
				if(phoneText.getText().length() != 12 && phoneText.getText().length() != 13)
					throw new NumberFormatException();
				else if(phoneText.getText().length() == 12)
				{
					String tempPhone = phoneText.getText();

					if(tempPhone.charAt(3) != '-' || tempPhone.charAt(7) != '-')
						throw new NumberFormatException();
					else
					{
						Integer.parseInt(tempPhone.substring(0, 3));
						Integer.parseInt(tempPhone.substring(4, 7));
						Integer.parseInt(tempPhone.substring(8));
					}

				}
				else if(phoneText.getText().length() == 13)
				{
					String tempPhone = phoneText.getText();

					if(tempPhone.charAt(0) != '(' || tempPhone.charAt(4) != ')' || tempPhone.charAt(8) != '-')
						throw new NumberFormatException();
					else
					{
						Integer.parseInt(tempPhone.substring(1, 4));
						Integer.parseInt(tempPhone.substring(5, 8));
						Integer.parseInt(tempPhone.substring(9));
					}
				}

				//Calculating the final purchase price for the user and
				//the donation amount for the fund
				try
				{
					int numCoup1 = (coupon1Text.getText().length() == 0)? 0 : Integer.parseInt(coupon1Text.getText());
					int numCoup2 = (coupon2Text.getText().length() == 0)? 0 : Integer.parseInt(coupon2Text.getText());
					int numCoup3 = (coupon3Text.getText().length() == 0)? 0 : Integer.parseInt(coupon3Text.getText());

					if(numCoup1 == 0 && numCoup2 == 0 && numCoup3 == 0)
					{
						tempMessage = "<html><font color=red size='5'>*</font> Please consider purchasing one of" +
									  " the available coupons above.</html>";
						coupon1Text.setText("0");
						coupon2Text.setText("0");
						coupon3Text.setText("0");
					}
					else
					{
						double paymentAmount = (5 * numCoup1) + (10 * numCoup2) + (25 * numCoup3);
						double donationAmount = (.25 * numCoup1) + (.5 * numCoup2) + (1.25 * numCoup3);
						int confirmOption = JOptionPane.showConfirmDialog(frame,
							"<html>Your total is <font color=red> " + moneyFormat.format(paymentAmount) +
							"</font><br> Would you like to submit this transaction?</html>",
							"Confirm purchase",
							JOptionPane.YES_NO_OPTION);

						//Confirming user purchase
						if (confirmOption == JOptionPane.YES_OPTION)
						{
							tempMessage = "<html><b><font size='3'>" + firstNameText.getText() +
										  ", your total purchase amount due is:</font></b><br>"+
										  "<font color='red'> " + moneyFormat.format(paymentAmount) + " </font><br><br>" +
										  "From the coupons you purchased, <font color='blue'>" +
										  moneyFormat.format(donationAmount) + " </font> will be donated to " +
										  "\"Marcus Hui's Basketball Fund\".<br><br>Thank you very much for your purchase.<br>" +
										  "Your support is changing children lives one dollar at a time!</html>";
						}
						else
						{
							tempMessage = "<html><font color=red size='5'>*</font> Please consider purchasing one of" +
										  " the available coupons above.</html>";
						}
					}
				}
				catch(NumberFormatException ee)
				{
					//User did not enter an integer for the coupon choice
					tempMessage = "<html><font color=red size='5'>*</font> Please enter an valid number for" +
								  " the coupon selections.</html>";
					coupon1Text.setText("0");
					coupon2Text.setText("0");
					coupon3Text.setText("0");
				}
			}
			catch(NumberFormatException eee)
			{
				//User did not enter a correct phone number
				tempMessage = "<html><font color=red size='5'>*</font> Please enter your Home Phone Number starting" +
							  " with your area code.<br>" + "Example: (555)123-4567 or 555-123-4567</html>";
			}
		}

		informationLabel.setText(tempMessage);
	}


	//
	// private static void createAndShowGUI()
	//
	// This method will set up the user interface for display and
	// user interaction.
	//
	private static void createAndShowGUI()
	{
		//Create and set up the window
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("CISC685 Assignment 1: Interface Design");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create a new content pane
        JComponent newContentPane = new Donation();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.getContentPane().add(newContentPane, BorderLayout.PAGE_START);
		frame.setMinimumSize(new Dimension(500, 620));
		frame.pack();
		frame.setVisible(true);
	}


	//
	// public static void main(String arg[])
	//
	// This is the main function that starts this program.
	//
	public static void main(String arg[])
	{
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createAndShowGUI();
			}
		});
	}
}