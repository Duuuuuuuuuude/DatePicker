package gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDate;

import java.time.YearMonth;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public abstract class AbstractCalendar extends JPanel {
	private final static String[] months = { "Januar", "Februar", "Marts", "April", "Maj", "Juni", "Juli", "August",
			"September", "Oktober", "November", "December" };
	private DefaultTableModel tableModel;
	private JComboBox<Integer> cmbYear;
	private YearMonth currSetYearMonth;
	private JScrollPane spnlCalendar;
	private JTable tblCalendar;
	private JComboBox<String> cmbMonth;

	/**
	 * Create the panel.
	 */
	public AbstractCalendar(Class<?> columnClass, TableCellRenderer tableCellRenderer,
			DefaultTableModel defaultTableModel) {
		setLayout(new BorderLayout(0, 0));

		this.tblCalendar = new JTable() {
			@Override
			public void changeSelection(int row, int col, boolean toggle, boolean expand) {
				Object value = getModel().getValueAt(row, col);
				if (value != null) {
					LocalDate date = null;
					if (value.getClass() == LocalDate.class) {
						if ((((LocalDate) value).plusDays(1).isAfter(LocalDate.now()))) {
							super.changeSelection(row, col, toggle, expand);
						}
					} else {
						super.changeSelection(row, col, toggle, expand);
					}
				}
			}
		};
		tblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblCalendar.setCellSelectionEnabled(true);
		tblCalendar.getTableHeader().setReorderingAllowed(false);
		tblCalendar.setRowHeight(43);
		tblCalendar.setFillsViewportHeight(true);

		spnlCalendar = new JScrollPane();
		add(spnlCalendar, BorderLayout.CENTER);
		spnlCalendar.getVerticalScrollBar().setUnitIncrement(5);
		spnlCalendar.setViewportView(tblCalendar);

		JPanel pnlMonthSelector = new JPanel();
		add(pnlMonthSelector, BorderLayout.NORTH);
		GridBagLayout gbl_pnlMonthSelector = new GridBagLayout();
		gbl_pnlMonthSelector.columnWidths = new int[] { 0, 0, 70, 0, 0, 0 };
		gbl_pnlMonthSelector.rowHeights = new int[] { 5, 35, 0, 0 };
		gbl_pnlMonthSelector.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_pnlMonthSelector.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		pnlMonthSelector.setLayout(gbl_pnlMonthSelector);

		JButton btnPrevMonth = new JButton("<<");
		btnPrevMonth.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				prevMonthClicked();
			}
		});
		btnPrevMonth.setFocusPainted(false);
		GridBagConstraints gbc_btnPrevMonth = new GridBagConstraints();
		gbc_btnPrevMonth.insets = new Insets(0, 0, 5, 5);
		gbc_btnPrevMonth.gridx = 1;
		gbc_btnPrevMonth.gridy = 1;
		pnlMonthSelector.add(btnPrevMonth, gbc_btnPrevMonth);

		cmbMonth = new JComboBox();
		cmbMonth.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == 2) {
					cmbMonthChanged();
				}
			}
		});
		GridBagConstraints gbc_cmbMonth = new GridBagConstraints();
		gbc_cmbMonth.insets = new Insets(0, 0, 5, 5);
		gbc_cmbMonth.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbMonth.gridx = 2;
		gbc_cmbMonth.gridy = 1;
		pnlMonthSelector.add(cmbMonth, gbc_cmbMonth);

		JButton btnNextMonth = new JButton(">>");
		btnNextMonth.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nextMonthClicked();
			}
		});
		btnNextMonth.setFocusPainted(false);
		GridBagConstraints gbc_btnNextYear = new GridBagConstraints();
		gbc_btnNextYear.insets = new Insets(0, 0, 5, 5);
		gbc_btnNextYear.gridx = 3;
		gbc_btnNextYear.gridy = 1;
		pnlMonthSelector.add(btnNextMonth, gbc_btnNextYear);

		JPanel pnlYearSelector = new JPanel();
		add(pnlYearSelector, BorderLayout.SOUTH);
		GridBagLayout gbl_pnlYearSelector = new GridBagLayout();
		gbl_pnlYearSelector.columnWidths = new int[] { 5, 0, 190, 82, 5, 0 };
		gbl_pnlYearSelector.rowHeights = new int[] { 35, 0 };
		gbl_pnlYearSelector.columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_pnlYearSelector.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		pnlYearSelector.setLayout(gbl_pnlYearSelector);

		cmbYear = new JComboBox<>();
		cmbYear.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == 2) {
					cmbYearChanged();
				}
			}
		});
		GridBagConstraints gbc_cmbYear = new GridBagConstraints();
		gbc_cmbYear.insets = new Insets(0, 0, 0, 5);
		gbc_cmbYear.anchor = GridBagConstraints.EAST;
		gbc_cmbYear.gridx = 3;
		gbc_cmbYear.gridy = 0;
		pnlYearSelector.add(cmbYear, gbc_cmbYear);

		init(columnClass, tableCellRenderer, defaultTableModel);
	}

	private void init(Class<?> columnClass, TableCellRenderer tableCellRenderer, DefaultTableModel defaultTableModel) {
		tblCalendar.setDefaultRenderer(columnClass, tableCellRenderer);
		this.tableModel = defaultTableModel;
		this.tblCalendar.setModel(tableModel);

		// Populate cmbMonth
		for (int i = 0; i < months.length; i++) {
			cmbMonth.addItem(months[i]);
		}

		// Populate cmbYear
		for (int i = 2021; i <= 2080; i++) {
			cmbYear.addItem(i);
		}
		currSetYearMonth = YearMonth.now();
		cmbYear.setSelectedItem(currSetYearMonth.getYear());
		cmbMonth.setSelectedIndex(currSetYearMonth.getMonthValue() - 1);

		refreshCalendar();

	}

	private void cmbYearChanged() {
		currSetYearMonth = currSetYearMonth.withYear((int) cmbYear.getSelectedItem());
		refreshCalendar();
	}

	private void cmbMonthChanged() {
		currSetYearMonth = currSetYearMonth.withMonth((int) cmbMonth.getSelectedIndex() + 1);
		refreshCalendar();
	}

	private void nextMonthClicked() {
		currSetYearMonth = currSetYearMonth.plusMonths(1);
		cmbMonth.setSelectedIndex(currSetYearMonth.getMonthValue() - 1);
		cmbYear.getModel().setSelectedItem(currSetYearMonth.getYear());
		refreshCalendar();
	}

	private void prevMonthClicked() {
		currSetYearMonth = currSetYearMonth.minusMonths(1);
		cmbMonth.setSelectedIndex(currSetYearMonth.getMonthValue() - 1);
		cmbYear.getModel().setSelectedItem(currSetYearMonth.getYear());
		refreshCalendar();
	}

	protected LocalDate getChoosenDate() {
		int row = tblCalendar.getSelectedRow();
		int col = tblCalendar.getSelectedColumn();
		LocalDate res = null;
		if (row != -1 || col != -1) {
			res = (LocalDate) tblCalendar.getValueAt(row, col);
		}
		return res;
	}

	protected void setCurrSetYearMonth(YearMonth currSetYearMonth) {
		this.currSetYearMonth = currSetYearMonth;
		cmbMonth.setSelectedIndex(currSetYearMonth.getMonthValue() - 1);
		cmbYear.getModel().setSelectedItem(currSetYearMonth.getYear());
	}

	protected Object getTableModel() {
		return this.tableModel;
	}

	protected YearMonth getCurrSetYearMonth() {
		return this.currSetYearMonth;
	}

	protected JTable getTblCalendar() {
		return this.tblCalendar;
	}

	protected abstract void refreshCalendar();
}