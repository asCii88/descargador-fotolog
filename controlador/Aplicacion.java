package controlador;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import vista.VentanaLicencia;
import vista.VentanaPrincipal;
import motor.MotorPrincipal;

public class Aplicacion implements WindowListener, PropertyChangeListener{

	public static void main(String[] args) {
		new Aplicacion();
		
	}
	
	/**
	 * @param args
	 */
	private VentanaPrincipal ventanaPrincipal;
	private MotorPrincipal motor;
	
	private Aplicacion(){
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (InstantiationException e) {
	        e.printStackTrace();
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    } catch (UnsupportedLookAndFeelException e) {
	        e.printStackTrace();
	    }
		ventanaPrincipal = new VentanaPrincipal(this);
		VentanaLicencia.getInstance().setAplicacion(this);
		VentanaLicencia.getInstance().setVisible(true);

	}
	
	public void iniciarDescarga(String URL){
		motor = new MotorPrincipal(URL);
		motor.addPropertyChangeListener(this);
		motor.execute();
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		 if ("progress" == evt.getPropertyName()) {
	            int progress = (Integer) evt.getNewValue();
	            ventanaPrincipal.setProgressBar(progress);
	        } 
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		
		
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		VentanaLicencia.getInstance().setVisible(false);
			ventanaPrincipal.setVisible(true);		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
