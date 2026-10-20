import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * BILLING SYSTEM  (Information Technology project)
 * ------------------------------------------------
 * A simple shop billing program made with Java Swing.
 *  - enter customer details
 *  - pick items from the list and add them to the bill
 *  - discount and GST are calculated automatically
 *  - bill is shown on the screen and can be saved as a text file
 *
 * Run:   javac BillingSystem.java
 *        java BillingSystem
 */
public class BillingSystem extends JPanel implements ActionListener {

    // items sold in the shop (name and price are kept in two arrays,
    // the item at position 0 in itemNames has the price at position 0 in itemPrices)
    String[] itemNames = {
        "Basmati Rice 1kg", "Wheat Atta 5kg", "Sugar 1kg", "Toor Dal 1kg",
        "Sunflower Oil 1L", "Tea Powder 250g", "Amul Butter 100g", "Full Cream Milk 1L",
        "Maggi Noodles", "Biscuits Pack", "Bath Soap", "Detergent 1kg",
        "Toothpaste 150g", "Notebook (200 pg)", "Ball Pen (Blue)"
    };
    double[] itemPrices = { 120, 245, 48, 155, 148, 135, 56, 68, 14, 30, 38, 110, 92, 60, 10 };

    final double GST_RATE = 5.0;   // GST in percent

    // swing components
    JTextField txtName, txtMobile, txtBillNo, txtDate;
    JComboBox<String> cmbItem;
    JTextField txtPrice, txtQty;
    JTextField txtSubTotal, txtDiscount, txtGst, txtGrandTotal;
    JButton btnAdd, btnRemove, btnGenerate, btnSave, btnClear, btnClose;
    JTable table;
    DefaultTableModel model;
    JTextArea txtBill;

    // values used for the bill
    double subTotal, discountPercent, discountAmt, gstAmt, grandTotal;
    boolean billGenerated = false;

    public BillingSystem() {
        Container c = this;
        c.setLayout(null);
        c.setBackground(new Color(236, 241, 246));
        c.setPreferredSize(new Dimension(980, 675));

        // ---------- heading ----------
        JPanel pnlHead = new JPanel(null);
        pnlHead.setBackground(new Color(25, 60, 110));
        pnlHead.setBounds(0, 0, 980, 55);
        JLabel lblHead = new JLabel("MINI MART  -  BILLING SYSTEM", JLabel.CENTER);
        lblHead.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblHead.setForeground(Color.WHITE);
        lblHead.setBounds(0, 0, 980, 55);
        pnlHead.add(lblHead);
        c.add(pnlHead);

        // ---------- customer details ----------
        JPanel pnlCust = makePanel("Customer Details", 15, 65, 460, 150);
        addLabel(pnlCust, "Customer Name", 15, 25);
        txtName = addField(pnlCust, 130, 25, 300, true);
        addLabel(pnlCust, "Mobile No.", 15, 55);
        txtMobile = addField(pnlCust, 130, 55, 300, true);
        addLabel(pnlCust, "Bill No.", 15, 85);
        txtBillNo = addField(pnlCust, 130, 85, 300, false);
        addLabel(pnlCust, "Date & Time", 15, 115);
        txtDate = addField(pnlCust, 130, 115, 300, false);
        c.add(pnlCust);

        // ---------- add item ----------
        JPanel pnlItem = makePanel("Add Item to Bill", 15, 225, 460, 155);
        addLabel(pnlItem, "Select Item", 15, 25);
        cmbItem = new JComboBox<String>(itemNames);
        cmbItem.setBounds(130, 25, 300, 26);
        cmbItem.addActionListener(this);
        pnlItem.add(cmbItem);
        addLabel(pnlItem, "Price (Rs.)", 15, 55);
        txtPrice = addField(pnlItem, 130, 55, 120, false);
        addLabel(pnlItem, "Quantity", 15, 85);
        txtQty = addField(pnlItem, 130, 85, 120, true);
        txtQty.setText("1");
        txtQty.addActionListener(this);          // pressing Enter adds the item
        btnAdd = addButton(pnlItem, "Add Item", 130, 115, 140, 30);
        btnRemove = addButton(pnlItem, "Remove Item", 290, 115, 140, 30);
        c.add(pnlItem);

        // ---------- amounts ----------
        JPanel pnlAmt = makePanel("Bill Amount", 15, 390, 460, 160);
        addLabel(pnlAmt, "Sub Total (Rs.)", 15, 25);
        txtSubTotal = addField(pnlAmt, 130, 25, 150, false);
        addLabel(pnlAmt, "Discount (%)", 15, 55);
        txtDiscount = addField(pnlAmt, 130, 55, 150, true);
        txtDiscount.setText("0");
        txtDiscount.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                calculateTotals();               // update amounts while typing
            }
        });
        addLabel(pnlAmt, "GST @ 5% (Rs.)", 15, 85);
        txtGst = addField(pnlAmt, 130, 85, 150, false);
        addLabel(pnlAmt, "Grand Total (Rs.)", 15, 115);
        txtGrandTotal = addField(pnlAmt, 130, 115, 150, false);
        txtGrandTotal.setFont(new Font("SansSerif", Font.BOLD, 15));
        c.add(pnlAmt);

        // ---------- main buttons ----------
        btnGenerate = addButton(c, "Generate Bill", 15, 565, 225, 40);
        btnSave = addButton(c, "Save Bill", 250, 565, 225, 40);
        btnClear = addButton(c, "Clear", 15, 615, 225, 40);
        btnClose = addButton(c, "Close", 250, 615, 225, 40);

        // ---------- table of items ----------
        String[] cols = { "S.No", "Item", "Price", "Qty", "Amount" };
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;                    // user should not type inside the table
            }
        };
        table = new JTable(model);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);

        // show price and amount with 2 decimal places
        DefaultTableCellRenderer money = new DefaultTableCellRenderer() {
            protected void setValue(Object v) {
                setText(String.format("%.2f", (Double) v));
                setHorizontalAlignment(RIGHT);
            }
        };
        table.getColumnModel().getColumn(2).setCellRenderer(money);
        table.getColumnModel().getColumn(4).setCellRenderer(money);

        // S.No and Qty in the centre
        DefaultTableCellRenderer middle = new DefaultTableCellRenderer();
        middle.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(middle);
        table.getColumnModel().getColumn(3).setCellRenderer(middle);

        JScrollPane spTable = new JScrollPane(table);
        spTable.setBorder(new TitledBorder("Items in Bill"));
        spTable.setBounds(490, 65, 475, 190);
        spTable.setBackground(c.getBackground());
        c.add(spTable);

        // ---------- bill preview ----------
        txtBill = new JTextArea();
        txtBill.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtBill.setEditable(false);
        JScrollPane spBill = new JScrollPane(txtBill);
        spBill.setBorder(new TitledBorder("Bill Preview"));
        spBill.setBounds(490, 265, 475, 390);
        c.add(spBill);

        // start with a fresh bill
        showPrice();
        clearAll();
    }

    // ------------------------------------------------------------------
    // small helper methods to create components
    // ------------------------------------------------------------------
    JPanel makePanel(String title, int x, int y, int w, int h) {
        JPanel p = new JPanel(null);
        p.setBorder(new TitledBorder(title));
        p.setBackground(new Color(236, 241, 246));
        p.setBounds(x, y, w, h);
        return p;
    }

    void addLabel(Container parent, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(x, y, 115, 26);
        parent.add(l);
    }

    JTextField addField(Container parent, int x, int y, int w, boolean editable) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, 26);
        t.setEditable(editable);
        parent.add(t);
        return t;
    }

    JButton addButton(Container parent, String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        b.addActionListener(this);
        parent.add(b);
        return b;
    }

    // ------------------------------------------------------------------
    // button clicks
    // ------------------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == cmbItem) {
            showPrice();
        } else if (src == btnAdd || src == txtQty) {
            addItem();
        } else if (src == btnRemove) {
            removeItem();
        } else if (src == btnGenerate) {
            generateBill();
        } else if (src == btnSave) {
            saveBill();
        } else if (src == btnClear) {
            clearAll();
        } else if (src == btnClose) {
            closeProgram();
        }
    }

    // show the price of the item selected in the combo box
    void showPrice() {
        int index = cmbItem.getSelectedIndex();
        txtPrice.setText(String.format("%.2f", itemPrices[index]));
    }

    // Add Item button
    void addItem() {
        int qty;
        try {
            qty = Integer.parseInt(txtQty.getText().trim());
        } catch (NumberFormatException ex) {
            showMessage("Quantity must be a whole number.",
                    "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
            txtQty.requestFocus();
            return;
        }
        if (qty <= 0) {
            showMessage("Quantity must be at least 1.",
                    "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
            txtQty.requestFocus();
            return;
        }

        String name = (String) cmbItem.getSelectedItem();
        double price = itemPrices[cmbItem.getSelectedIndex()];

        // if the item is already in the bill then only increase its quantity
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).equals(name)) {
                int newQty = (Integer) model.getValueAt(i, 3) + qty;
                model.setValueAt(newQty, i, 3);
                model.setValueAt(price * newQty, i, 4);
                txtQty.setText("1");
                calculateTotals();
                return;
            }
        }

        model.addRow(new Object[] { model.getRowCount() + 1, name, price, qty, price * qty });
        txtQty.setText("1");
        calculateTotals();
    }

    // Remove Item button
    void removeItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Please select an item from the table first.",
                    "No Item Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        model.removeRow(row);

        // fix the serial numbers after deleting
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
        calculateTotals();
    }

    // works out sub total, discount, gst and grand total
    void calculateTotals() {
        subTotal = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            subTotal = subTotal + (Double) model.getValueAt(i, 4);
        }

        try {
            discountPercent = Double.parseDouble(txtDiscount.getText().trim());
        } catch (NumberFormatException ex) {
            discountPercent = 0;
        }
        if (discountPercent < 0 || discountPercent > 100) {
            discountPercent = 0;
        }

        discountAmt = subTotal * discountPercent / 100;
        double afterDiscount = subTotal - discountAmt;
        gstAmt = afterDiscount * GST_RATE / 100;
        grandTotal = afterDiscount + gstAmt;

        txtSubTotal.setText(String.format("%.2f", subTotal));
        txtGst.setText(String.format("%.2f", gstAmt));
        txtGrandTotal.setText(String.format("%.2f", grandTotal));
    }

    // Generate Bill button
    void generateBill() {
        String name = txtName.getText().trim();
        String mobile = txtMobile.getText().trim();

        if (name.isEmpty()) {
            showMessage("Please enter the customer name.",
                    "Missing Details", JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus();
            return;
        }
        if (!mobile.matches("[0-9]{10}")) {
            showMessage("Mobile number must have exactly 10 digits.",
                    "Invalid Mobile No.", JOptionPane.WARNING_MESSAGE);
            txtMobile.requestFocus();
            return;
        }
        if (model.getRowCount() == 0) {
            showMessage("No items added. Please add at least one item.",
                    "Empty Bill", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // check the discount that the user typed
        try {
            double d = Double.parseDouble(txtDiscount.getText().trim());
            if (d < 0 || d > 100) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showMessage("Discount must be a number between 0 and 100.",
                    "Invalid Discount", JOptionPane.WARNING_MESSAGE);
            txtDiscount.requestFocus();
            return;
        }

        calculateTotals();
        txtDate.setText(now());

        String line1 = "============================================\n";
        String line2 = "--------------------------------------------\n";

        StringBuilder sb = new StringBuilder();
        sb.append(line1);
        sb.append(center("MINI MART")).append("\n");
        sb.append(center("Shop No. 12, Main Market")).append("\n");
        sb.append(center("GSTIN: 07ABCDE1234F1Z5")).append("\n");
        sb.append(line1);
        sb.append("Bill No  : ").append(txtBillNo.getText()).append("\n");
        sb.append("Date     : ").append(txtDate.getText()).append("\n");
        sb.append("Customer : ").append(name).append("\n");
        sb.append("Mobile   : ").append(mobile).append("\n");
        sb.append(line2);
        sb.append(String.format("%-19s%9s%4s%12s\n", "Item", "Price", "Qty", "Amount"));
        sb.append(line2);

        for (int i = 0; i < model.getRowCount(); i++) {
            String item = (String) model.getValueAt(i, 1);
            double price = (Double) model.getValueAt(i, 2);
            int qty = (Integer) model.getValueAt(i, 3);
            double amount = (Double) model.getValueAt(i, 4);
            sb.append(String.format("%-19s%9.2f%4d%12.2f\n", item, price, qty, amount));
        }

        sb.append(line2);
        sb.append(String.format("%-32s%12.2f\n", "Sub Total", subTotal));
        sb.append(String.format("%-32s%12.2f\n",
                "Discount (" + discountPercent + "%)", -discountAmt));
        sb.append(String.format("%-32s%12.2f\n", "GST (" + GST_RATE + "%)", gstAmt));
        sb.append(line1);
        sb.append(String.format("%-32s%12.2f\n", "GRAND TOTAL (Rs.)", grandTotal));
        sb.append(line1);
        sb.append(center("Thank you! Visit again.")).append("\n");

        txtBill.setText(sb.toString());
        txtBill.setCaretPosition(0);
        billGenerated = true;
    }

    // Save Bill button - writes the bill into a text file inside the "bills" folder
    void saveBill() {
        if (!billGenerated) {
            showMessage("Please generate the bill first.",
                    "Bill Not Generated", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File folder = new File("bills");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, "Bill_" + txtBillNo.getText() + ".txt");

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(txtBill.getText().replace("\n", System.lineSeparator()));
            showMessage("Bill saved in  " + file.getPath(),
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
            clearAll();                          // ready for the next customer
        } catch (IOException ex) {
            showMessage("Could not save the bill:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clear button
    void clearAll() {
        txtName.setText("");
        txtMobile.setText("");
        txtBillNo.setText(String.valueOf(getNextBillNo()));
        txtDate.setText(now());
        cmbItem.setSelectedIndex(0);
        txtQty.setText("1");
        txtDiscount.setText("0");
        model.setRowCount(0);
        txtBill.setText("");
        billGenerated = false;
        calculateTotals();
        txtName.requestFocus();
    }

    // Close button
    void closeProgram() {
        if (confirm("Do you really want to exit?", "Exit")) {
            System.exit(0);
        }
    }

    // all the pop-up boxes of the program are shown from these two methods
    void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    boolean confirm(String message, String title) {
        int ans = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
        return ans == JOptionPane.YES_OPTION;
    }

    // ------------------------------------------------------------------
    // other small methods
    // ------------------------------------------------------------------

    // bill numbers start from 1001 and go up by one for every saved bill
    int getNextBillNo() {
        int count = 0;
        File[] files = new File("bills").listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().startsWith("Bill_")) {
                    count++;
                }
            }
        }
        return 1001 + count;
    }

    String now() {
        return new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(new Date());
    }

    // puts spaces before the text so that it comes in the middle of the 44 character bill
    String center(String text) {
        int spaces = (44 - text.length()) / 2;
        String result = "";
        for (int i = 0; i < spaces; i++) {
            result = result + " ";
        }
        return result + text;
    }

    public static void main(String[] args) {
        System.setProperty("swing.boldMetal", "false");
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Billing System");
            BillingSystem bill = new BillingSystem();

            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    bill.closeProgram();         // asks before closing
                }
            });
            frame.add(bill);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
