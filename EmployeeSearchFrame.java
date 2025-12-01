/**
•  Authors:
•    - SianRose Vincent
•    - Murari Raman Upreti
•    - Aadarsha Aryal
 *
•  Original Framework Author:
•    - Lon Smith, Ph.D.
 *
•  Description:
•  This GUI application connects to a MySQL database and allows users to search
•  for employees based on department and project selections. The program supports:
 *
•   - Loading department and project names from the database
•   - Applying logical IN / NOT IN filters
•   - Dynamically building appropriate SQL queries
•   - Displaying employee results in a scrollable text area
•   - Handling database and table errors through pop-up dialogs
 *
•  The database settings are taken from the external file: 
    database.properties
	
	 db.baseUrl=jdbc:mysql://localhost:3306/
     db.user= username
     db.password = passwd
   
 */

import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class EmployeeSearchFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    private JTextField txtDatabase;

    private JList<String> lstDepartment;
    private DefaultListModel<String> department = new DefaultListModel<String>();

    private JList<String> lstProject;
    private DefaultListModel<String> project = new DefaultListModel<String>();

    private JTextArea textAreaEmployee;

    /**
	  Loads database connection properties from the external file
	  "database.properties".
     *
	  @return Properties object containing database URL, username, and password
	  @throws Exception if the file cannot be opened or read
     */
    private Properties loadDbProperties() throws Exception {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream("database.properties")) {
            props.load(in);
        }
        return props;
    }

    /**
	  Main entry point for the Employee Search application. Launches the GUI on
	  the Event Dispatch Thread.
     *
	  @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EmployeeSearchFrame frame = new EmployeeSearchFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
	  Constructor for EmployeeSearchFrame. Builds the full GUI: - Database
	  input field - Department and Project lists - IN/NOT checkboxes - Fill,
	  Search, and Clear buttons - Employee result text output
     *
	  Also attaches all action listeners for buttons.
     */
    public EmployeeSearchFrame() {
        setTitle("Employee Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 380);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Database label + text field
        JLabel lblDatabase = new JLabel("Database:");
        lblDatabase.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblDatabase.setBounds(21, 23, 70, 14);
        contentPane.add(lblDatabase);

        txtDatabase = new JTextField();
        txtDatabase.setBounds(95, 20, 200, 20);
        contentPane.add(txtDatabase);
        txtDatabase.setColumns(10);

        // Result / message area
        textAreaEmployee = new JTextArea();
        JScrollPane scrollEmp = new JScrollPane(textAreaEmployee);
        scrollEmp.setBounds(36, 230, 386, 80);
        contentPane.add(scrollEmp);

        /**
		  Fill button handler. Connects to the database entered in txtDatabase,
		  loads all Department names and Project names, and populates the two
		  JList components.
         *
		  Displays an error dialog if: - Database doesn't exist - Required
		  tables (DEPARTMENT, PROJECT) are missing
         */
        JButton btnDBFill = new JButton("Fill");
        btnDBFill.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btnDBFill.setBounds(320, 19, 80, 23);
        contentPane.add(btnDBFill);

        btnDBFill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String dbName = txtDatabase.getText().trim();
                if (dbName.isEmpty()) {
                    textAreaEmployee.setText("Please enter a database name first.");
                    return;
                }

                java.sql.Connection conn = null;
                java.sql.Statement stmt = null;
                java.sql.ResultSet rs = null;

                department.clear();
                project.clear();

                try {
                    Properties props = loadDbProperties();
                    String baseUrl = props.getProperty("db.baseUrl");
                    String user = props.getProperty("db.user");
                    String password = props.getProperty("db.password");

                    String url = baseUrl + dbName + "?useSSL=false&allowPublicKeyRetrieval=true";

                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = java.sql.DriverManager.getConnection(url, user, password);
                    stmt = conn.createStatement();

                    // Departments
                    rs = stmt.executeQuery("SELECT DNAME FROM DEPARTMENT");
                    while (rs.next()) {
                        department.addElement(rs.getString("DNAME"));
                    }
                    rs.close();

                    // Projects
                    rs = stmt.executeQuery("SELECT PNAME FROM PROJECT");
                    while (rs.next()) {
                        project.addElement(rs.getString("PNAME"));
                    }

                    textAreaEmployee.setText("Loaded departments and projects from database: " + dbName);

                } catch (Exception ex) {

                    javax.swing.JOptionPane.showMessageDialog(
                            EmployeeSearchFrame.this,
                            "Cannot use database \"" + dbName + "\".\n"
                            + "Make sure it exists and has DEPARTMENT and PROJECT tables.\n\n"
                            + "Details: " + ex.getMessage(),
                            "Database Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE
                    );

                    ex.printStackTrace();

                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (stmt != null) {
                            stmt.close();
                        }
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
        });

        // Department / Project labels
        JLabel lblDepartment = new JLabel("Department");
        lblDepartment.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblDepartment.setBounds(52, 63, 89, 14);
        contentPane.add(lblDepartment);

        JLabel lblProject = new JLabel("Project");
        lblProject.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblProject.setBounds(275, 63, 60, 14);
        contentPane.add(lblProject);

        // Scrollable Department list
        lstDepartment = new JList<String>(department);
        lstDepartment.setFont(new Font("Tahoma", Font.PLAIN, 12));
        JScrollPane scrollDept = new JScrollPane(lstDepartment);
        scrollDept.setBounds(36, 83, 172, 60);
        contentPane.add(scrollDept);

        // Scrollable Project list
        lstProject = new JList<String>(project);
        lstProject.setFont(new Font("Tahoma", Font.PLAIN, 12));
        JScrollPane scrollProj = new JScrollPane(lstProject);
        scrollProj.setBounds(250, 83, 172, 60);
        contentPane.add(scrollProj);

        // Not checkboxes
        JCheckBox chckbxNotDept = new JCheckBox("Not");
        chckbxNotDept.setBounds(90, 153, 59, 23);
        contentPane.add(chckbxNotDept);

        JCheckBox chckbxNotProject = new JCheckBox("Not");
        chckbxNotProject.setBounds(300, 153, 59, 23);
        contentPane.add(chckbxNotProject);

        // Employee label
        JLabel lblEmployee = new JLabel("Employee");
        lblEmployee.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblEmployee.setBounds(52, 210, 89, 14);
        contentPane.add(lblEmployee);

        /**
		  Search button handler.
         *
		  Builds the correct SQL query based on: - Selected Department -
		  Selected Project - IN / NOT IN checkboxes
         *
		  Executes the query and prints: - Employee first name - Employee last
		  name
         *
		  Prints "No matching employees" if none found.
         */
        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(90, 320, 89, 23);
        contentPane.add(btnSearch);

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String deptName = (String) lstDepartment.getSelectedValue();
                String projName = (String) lstProject.getSelectedValue();

                boolean notDept = chckbxNotDept.isSelected();
                boolean notProj = chckbxNotProject.isSelected();

                if (deptName == null || projName == null) {
                    textAreaEmployee.setText("Please select one department and one project.");
                    return;
                }

                String dbName = txtDatabase.getText().trim();
                if (dbName.isEmpty()) {
                    textAreaEmployee.setText("Please enter a database name and click Fill first.");
                    return;
                }

                java.sql.Connection conn = null;
                java.sql.Statement stmt = null;
                java.sql.ResultSet rs = null;

                try {
                    Properties props = loadDbProperties();
                    String baseUrl = props.getProperty("db.baseUrl");
                    String user = props.getProperty("db.user");
                    String password = props.getProperty("db.password");

                    String url = baseUrl + dbName + "?useSSL=false&allowPublicKeyRetrieval=true";

                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = java.sql.DriverManager.getConnection(url, user, password);
                    stmt = conn.createStatement();

                    String sql = "";
                    /**
					•  SQL Query Building Section Constructs one of four search
					•  conditions:
                     *
					•  1. IN Department AND IN Project 2. IN Department AND NOT
					•  IN Project 3. NOT IN Department AND IN Project 4. NOT IN
					•  Department AND NOT IN Project
                     */
                   if (!notDept && !notProj) {
                        sql =
                            "SELECT DISTINCT E.FNAME, E.MINIT, E.LNAME " +
                            "FROM EMPLOYEE E " +
                            "JOIN DEPARTMENT D ON E.DNO = D.DNUMBER " +
                            "JOIN WORKS_ON W ON E.SSN = W.ESSN " +
                            "JOIN PROJECT P ON W.PNO = P.PNUMBER " +
                            "WHERE D.DNAME = '" + deptName + "' " +
                            "AND P.PNAME = '" + projName + "'";
                    } else if (!notDept && notProj) {
                        sql =
                            "SELECT DISTINCT E.FNAME, E.MINIT, E.LNAME " +
                            "FROM EMPLOYEE E " +
                            "JOIN DEPARTMENT D ON E.DNO = D.DNUMBER " +
                            "WHERE D.DNAME = '" + deptName + "' " +
                            "AND E.SSN NOT IN ( " +
                            "    SELECT W.ESSN " +
                            "    FROM WORKS_ON W " +
                            "    JOIN PROJECT P ON W.PNO = P.PNUMBER " +
                            "    WHERE P.PNAME = '" + projName + "' " +
                            ")";
                    } else if (notDept && !notProj) {
                        sql =
                            "SELECT DISTINCT E.FNAME, E.MINIT, E.LNAME " +
                            "FROM EMPLOYEE E " +
                            "JOIN WORKS_ON W ON E.SSN = W.ESSN " +
                            "JOIN PROJECT P ON W.PNO = P.PNUMBER " +
                            "WHERE P.PNAME = '" + projName + "' " +
                            "AND E.SSN NOT IN ( " +
                            "    SELECT E2.SSN " +
                            "    FROM EMPLOYEE E2 " +
                            "    JOIN DEPARTMENT D2 ON E2.DNO = D2.DNUMBER " +
                            "    WHERE D2.DNAME = '" + deptName + "' " +
                            ")";
                    } else {
                        sql =
                            "SELECT DISTINCT E.FNAME, E.MINIT, E.LNAME " +
                            "FROM EMPLOYEE E " +
                            "WHERE E.SSN NOT IN ( " +
                            "    SELECT E2.SSN " +
                            "    FROM EMPLOYEE E2 " +
                            "    JOIN DEPARTMENT D2 ON E2.DNO = D2.DNUMBER " +
                            "    WHERE D2.DNAME = '" + deptName + "' " +
                            ") " +
                            "AND E.SSN NOT IN ( " +
                            "    SELECT W.ESSN " +
                            "    FROM WORKS_ON W " +
                            "    JOIN PROJECT P ON W.PNO = P.PNUMBER " +
                            "    WHERE P.PNAME = '" + projName + "' " +
                            ")";
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("Dept = ")
                      .append(notDept ? "NOT " : "")
                      .append(deptName)
                      .append(", Project = ")
                      .append(notProj ? "NOT " : "")
                      .append(projName)
                      .append("\n\n");

                    rs = stmt.executeQuery(sql);
                    boolean any = false;

                    while (rs.next()) {
                        any = true;

                        String fname = rs.getString("FNAME");
                        String minit = rs.getString("MINIT");
                        String lname = rs.getString("LNAME");

                        if (minit == null || minit.trim().isEmpty()) {
                            sb.append(fname).append(" ").append(lname).append("\n");
                        } else {
                            sb.append(fname).append(" ").append(minit).append(". ").append(lname).append("\n");
                        }
                    }

                    if (!any) {
                        sb.append("No matching employees.");
                    }

                    textAreaEmployee.setText(sb.toString());


                } catch (Exception ex) {
                    textAreaEmployee.setText("Error: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (stmt != null) {
                            stmt.close();
                        }
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
        });

        /**
		  Clear button handler. Simply removes all text from the employee
		  output area.
         */
        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(260, 320, 89, 23);
        contentPane.add(btnClear);

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textAreaEmployee.setText("");
            }
        });
    }
}
