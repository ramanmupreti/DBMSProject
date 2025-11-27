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
        try (InputStream in = new FileInputStream("database.properties")) {
            props.load(in);
        }
        return props;
    }

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

        // Fill button: uses db name from txtDatabase
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
                    String baseUrl  = props.getProperty("db.baseUrl");
                    String user     = props.getProperty("db.user");
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
                    
                      // Pop-up dialog
    javax.swing.JOptionPane.showMessageDialog(
        EmployeeSearchFrame.this,
        "Cannot use database \"" + dbName + "\".\n" +
        "Make sure it exists and has DEPARTMENT and PROJECT tables.\n\n" +
        "Details: " + ex.getMessage(),
        "Database Error",
        javax.swing.JOptionPane.ERROR_MESSAGE
    );

      ex.printStackTrace();              


                } finally {
                    try {
                        if (rs != null)   rs.close();
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    } catch (Exception ignore) {}
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

        // Search button
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
                    String baseUrl  = props.getProperty("db.baseUrl");
                    String user     = props.getProperty("db.user");
                    String password = props.getProperty("db.password");

                    String url = baseUrl + dbName + "?useSSL=false&allowPublicKeyRetrieval=true";

                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = java.sql.DriverManager.getConnection(url, user, password);
                    stmt = conn.createStatement();

                    String sql;

                    if (!notDept && !notProj) {
                        // in dept AND on project
                        sql =
                            "SELECT DISTINCT E.FNAME, E.LNAME " +
                            "FROM EMPLOYEE E " +
                            "JOIN DEPARTMENT D ON E.DNO = D.DNUMBER " +
                            "JOIN WORKS_ON W ON E.SSN = W.ESSN " +
                            "JOIN PROJECT P ON W.PNO = P.PNUMBER " +
                            "WHERE D.DNAME = '" + deptName + "' " +
                            "AND P.PNAME = '" + projName + "'";
                    } else if (!notDept && notProj) {
                        // in dept AND NOT on project
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
                        // NOT in dept AND on project
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
                        // NOT in dept AND NOT on project
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

        // Clear button
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
