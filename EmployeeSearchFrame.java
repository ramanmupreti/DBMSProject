/**
 * Author: Lon Smith, Ph.D.
 * Description: This is the framework for the database program. Additional requirements and functionality
 *    are to be built by you and your group.
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
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class EmployeeSearchFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtDatabase;
	private JList<String> lstDepartment;
	private DefaultListModel<String> department = new DefaultListModel<String>();
	private JList<String> lstProject;
	private DefaultListModel<String> project = new DefaultListModel<String>();
	private JTextArea textAreaEmployee;

	    private Properties loadDbProperties() throws Exception {
        Properties props = new Properties();
        // Assumes database.properties is in the same folder where you run java
        try (InputStream in = new FileInputStream("database.properties")) {
            props.load(in);
        }
        return props;
    }

	    // Temporary test: print all department names to the console
    private void testPrintDepartments() {
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;

        try {
            // Load properties
            Properties props = loadDbProperties();
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            conn = java.sql.DriverManager.getConnection(url, user, password);

            // Create a statement and execute query
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT DNAME FROM DEPARTMENT");

            // Print results to the console
            System.out.println("Departments in COMPANY:");
            while (rs.next()) {
                String name = rs.getString("DNAME");
                System.out.println(" - " + name);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // Always close in finally
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception ignore) {
            }
        }
    }


	/**
	 * Launch the application.
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
	 * Create the frame.
	 */
	public EmployeeSearchFrame() {
		setTitle("Employee Search");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 347);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Database:");
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblNewLabel.setBounds(21, 23, 59, 14);
		contentPane.add(lblNewLabel);
		
		txtDatabase = new JTextField();
		txtDatabase.setBounds(90, 20, 193, 20);
		contentPane.add(txtDatabase);
		txtDatabase.setColumns(10);
		
		JButton btnDBFill = new JButton("Fill");
		/**
		 * The btnDBFill should fill the department and project JList with the 
		 * departments and projects from your entered database name.
		 */
		btnDBFill.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {
				 java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;

        // Clear existing items in the list models
        department.clear();
        project.clear();

        try {
            // Load properties
            Properties props = loadDbProperties();
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Load driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect
            conn = java.sql.DriverManager.getConnection(url, user, password);

            stmt = conn.createStatement();

            // 1) Fill departments
            rs = stmt.executeQuery("SELECT DNAME FROM DEPARTMENT");
            while (rs.next()) {
                String dname = rs.getString("DNAME");
                department.addElement(dname);
            }
            rs.close();

            // 2) Fill projects
            rs = stmt.executeQuery("SELECT PNAME FROM PROJECT");
            while (rs.next()) {
                String pname = rs.getString("PNAME");
                project.addElement(pname);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception ignore) {}
        }
  

				
			}
		});
		
		btnDBFill.setFont(new Font("Times New Roman", Font.BOLD, 12));
		btnDBFill.setBounds(307, 19, 68, 23);
		contentPane.add(btnDBFill);
		
		JLabel lblDepartment = new JLabel("Department");
		lblDepartment.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblDepartment.setBounds(52, 63, 89, 14);
		contentPane.add(lblDepartment);
		
		JLabel lblProject = new JLabel("Project");
		lblProject.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblProject.setBounds(255, 63, 47, 14);
		contentPane.add(lblProject);
		
		lstProject = new JList<String>(new DefaultListModel<String>());
		lstProject.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lstProject.setModel(project);
		lstProject.setBounds(225, 84, 150, 42);
		contentPane.add(lstProject);
		
		JCheckBox chckbxNotDept = new JCheckBox("Not");
		chckbxNotDept.setBounds(71, 133, 59, 23);
		contentPane.add(chckbxNotDept);
		
		JCheckBox chckbxNotProject = new JCheckBox("Not");
		chckbxNotProject.setBounds(270, 133, 59, 23);
		contentPane.add(chckbxNotProject);
		
		lstDepartment = new JList<String>(new DefaultListModel<String>());
		lstDepartment.setBounds(36, 84, 172, 40);
		contentPane.add(lstDepartment);
		lstDepartment.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lstDepartment.setModel(department);
		
		JLabel lblEmployee = new JLabel("Employee");
		lblEmployee.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblEmployee.setBounds(52, 179, 89, 14);
		contentPane.add(lblEmployee);
		
		JButton btnSearch = new JButton("Search");
btnSearch.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {

        // 1) Read selections from GUI
        String deptName = (String) lstDepartment.getSelectedValue();
        String projName = (String) lstProject.getSelectedValue();

        boolean notDept = chckbxNotDept.isSelected();
        boolean notProj = chckbxNotProject.isSelected();

        if (deptName == null || projName == null) {
            textAreaEmployee.setText("Please select one department and one project.");
            return;
        }

        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;

        try {
            java.util.Properties props = loadDbProperties();
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = java.sql.DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();

            String sql;

            if (!notDept && !notProj) {
                sql =
                    "SELECT DISTINCT E.FNAME, E.LNAME " +
                    "FROM EMPLOYEE E " +
                    "JOIN DEPARTMENT D ON E.DNO = D.DNUMBER " +
                    "JOIN WORKS_ON W ON E.SSN = W.ESSN " +
                    "JOIN PROJECT P ON W.PNO = P.PNUMBER " +
                    "WHERE D.DNAME = '" + deptName + "' " +
                    "AND P.PNAME = '" + projName + "'";
            } else if (!notDept && notProj) {
                sql =
                    "SELECT DISTINCT E.FNAME, E.LNAME " +
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
                    "SELECT DISTINCT E.FNAME, E.LNAME " +
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
                    "SELECT DISTINCT E.FNAME, E.LNAME " +
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
                String lname = rs.getString("LNAME");
                sb.append(fname).append(" ").append(lname).append("\n");
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
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception ignore) {}
        }
    }
});


		btnSearch.setBounds(80, 276, 89, 23);
		contentPane.add(btnSearch);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				textAreaEmployee.setText("");
			}
		});
		btnClear.setBounds(236, 276, 89, 23);
		contentPane.add(btnClear);
		
		textAreaEmployee = new JTextArea();
		textAreaEmployee.setBounds(36, 197, 339, 68);
		contentPane.add(textAreaEmployee);
		
		// Temporary: test database connection and query
		//testPrintDepartments();
	}
	//Testing final
	        
        

}
