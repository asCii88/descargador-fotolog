package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import controlador.Aplicacion;

public class VentanaPrincipal extends JFrame{
	private JPanel panel;
	private JPanel panelInferior;
	private JTextField labelURL;
	private JProgressBar progressBar;
	private JButton descargarBoton;

	
	public VentanaPrincipal(Aplicacion aplicacion){
		this.setIconImage(new ImageIcon(this.getClass().getResource("/images/icono.jpg")).getImage()); // Se establece el ícono de la ventana.
		this.setTitle("v1.1"); // Se establece el título de la ventana.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		this.setResizable(false); // Se determina que el usuario no pueda modificar el tamaño de la ventana.
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		this.setBackground(Color.WHITE);
		
		/* Creación de las dos principales secciones de la ventana */

		JLabel logo = new JLabel(new ImageIcon(this.getClass().getResource("/images/logo.gif")));
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(logo);
		
		JPanel panelCentralInterno = new JPanel();
		panelCentralInterno.setLayout(new BoxLayout(panelCentralInterno, BoxLayout.LINE_AXIS));
		panelCentralInterno.add(new JLabel("http://www.fotolog.com/"));
		
		labelURL = new JTextField();
		panelCentralInterno.add(labelURL);
		
		final Aplicacion aplicacionAux = aplicacion; 
		final JTextField labelURLAux = labelURL;
		
		descargarBoton = new JButton("Descargar");
		descargarBoton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		
		panelCentralInterno.add(descargarBoton);
		
		panel.add(panelCentralInterno);
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		final JProgressBar progressBarAux = progressBar;
		descargarBoton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (((JButton) e.getSource()).isEnabled()) {
					aplicacionAux.iniciarDescarga(labelURL.getText());
					SwingUtilities.invokeLater(new Runnable() {
					    public void run() {
					    	labelURLAux.setEditable(false);
					    	progressBar.setIndeterminate(true);
							progressBarAux.setString("Espere...");
					    }
					  });
					
					((JButton) e.getSource()).setEnabled(false);
				}
			}
		});
		
		panel.add(progressBar);
		
		panelInferior = new JPanel();
		panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.LINE_AXIS));
		JLabel licenciaBoton = new JLabel("Licencia");
		licenciaBoton.setForeground(Color.BLUE);
		licenciaBoton.setFont(new java.awt.Font(licenciaBoton.getName(), java.awt.Font.BOLD, licenciaBoton.getFont().getSize()));
		licenciaBoton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		licenciaBoton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				VentanaLicencia ventanaLicencia = VentanaLicencia.getInstance();
				ventanaLicencia.setVisible(true);				
			}
		});

		
		panelInferior.add(licenciaBoton);
		
		panel.add(panelInferior);
		this.add(panel);
		this.pack(); // La ventana ocupa el tamaño de sus componentes.
		this.setLocationRelativeTo(null); // En el centro de la pantalla.
	}


	/**
	 * @param progressBar the progressBar to set
	 */
	public void setProgressBar(int progress) {
		progressBar.setIndeterminate(false);
		progressBar.setString(null);
		progressBar.setValue(progress);
	}

}
