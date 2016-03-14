package vista;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import controlador.Aplicacion;

public class VentanaLicencia extends JFrame {
	
	private static VentanaLicencia instancia;
	public static VentanaLicencia getInstance() {
	        if (instancia == null)
	        	instancia = new VentanaLicencia();
	        return instancia;
	    }
	private String texto = null;
	private JPanel panel;
	private JPanel panelInferior;
	
	 private VentanaLicencia(){
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		this.setResizable(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setIconImage(new ImageIcon(this.getClass().getResource("/images/icono.jpg")).getImage());
		this.setType(Type.UTILITY);
		Box cuadro = Box.createHorizontalBox();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/license/licencia.txt")));
			StringBuilder sb = new StringBuilder();
			String line;
			line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append('\n');
				line = br.readLine();
			}
			texto = sb.toString();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JTextArea area1 = new JTextArea(texto, 40, 80);
		area1.setEditable(false);
		area1.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, area1.getFont().getSize()));
		cuadro.add(new JScrollPane(area1));
		panel.add(cuadro);
		JButton acceptBoton = new JButton("Accept");
		acceptBoton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		
		JButton dismissBoton = new JButton("Dismiss");
		dismissBoton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		dismissBoton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		final JButton dismissBotonAux = dismissBoton;
		acceptBoton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
				    public void run() {
				    	dismissBotonAux.setVisible(false);
				    }
				  });
				VentanaLicencia.getInstance().dispatchEvent(new WindowEvent(VentanaLicencia.getInstance(), WindowEvent.WINDOW_CLOSING));
			}
		});
		panelInferior = new JPanel();
		panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.LINE_AXIS));
		panelInferior.add(acceptBoton);
		panelInferior.add(dismissBoton);
		panel.add(panelInferior);
		this.add(panel);
		this.setUndecorated(true);
		this.pack();
		this.setLocationRelativeTo(null); // En el centro de la pantalla.
	}

	public void setAplicacion(Aplicacion aplicacion) {
		addWindowListener(aplicacion);
		
	}
	
}
