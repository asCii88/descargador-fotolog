package motor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class MotorPrincipal extends SwingWorker<Void, Void>{

	private static Font FUENTE_NOMBRE = new Font(FontFamily.HELVETICA, 15, Font.UNDERLINE + Font.BOLD);
	private static Font FUENTE_TITULO = new Font(FontFamily.HELVETICA, 13, Font.BOLD);
	private static  Font FUENTE_DESCRIPCION = new Font(FontFamily.HELVETICA, 11);
	private static Font FUENTE_COMENTARIOS = new Font(FontFamily.HELVETICA, 9);
	private String nombreFotolog;


	public MotorPrincipal(String nombreFotolog){
		this.nombreFotolog = nombreFotolog;
	}

	private Queue<FutureTask<DescargarFoto>> descargarFotos(Stack<String> urlFotos) {
		LinkedList<FutureTask<DescargarFoto>> activos = new LinkedList<FutureTask<DescargarFoto>>();
		Queue<FutureTask<DescargarFoto>> descargados = new LinkedBlockingQueue<FutureTask<DescargarFoto>>();
		while (!urlFotos.isEmpty()){
			String URL = urlFotos.pop();
			activos.add(new FutureTask<DescargarFoto>(new DescargarFoto(URL)));
		}
		ExecutorService executor = Executors.newFixedThreadPool(15);
		for (FutureTask<DescargarFoto> th : activos) {
			executor.execute(th);
		}
		int total = activos.size();
		while (!activos.isEmpty()){
			ListIterator<FutureTask<DescargarFoto>> iterador;
			iterador = activos.listIterator();
			FutureTask<DescargarFoto> actual;
			actual = iterador.next();
			if (actual.isDone()){
				descargados.add(actual);
				setProgress(Math.min(descargados.size()*100/total, 100));
				iterador.remove();
			}	
		}
		executor.shutdown();
		return descargados;
	}

	@Override
	protected Void doInBackground() {
		Stack<String> urlFotos;
		try {
			urlFotos = obtenerURLs(nombreFotolog);
			Queue<FutureTask<DescargarFoto>> descargados = descargarFotos(urlFotos);
			generarPDF(descargados);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"No se pudo encontrar el Fotolog solicitado.", null, JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}

	private String fechaDeHoy(){
		Calendar currentDate = Calendar.getInstance(); //Get the current date
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); //format it as per your requirement
		String dateNow = formatter.format(currentDate.getTime());
		return dateNow;
	}

	private void generarPDF(Queue<FutureTask<DescargarFoto>> descargados) {
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream("Backup de "+ this.getNombreFotolog() + " ("+ this.fechaDeHoy() +").pdf"));
			document.addTitle("Backup de "+ this.getNombreFotolog() + " ("+ this.fechaDeHoy() +")");
			document.addAuthor(this.getNombreFotolog());
			document.addCreator("Descargador de Fotolog");
			document.open();
			Paragraph nombreFlog = new Paragraph("Fotolog de " + this.getNombreFotolog(), FUENTE_NOMBRE);
			nombreFlog.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
			document.add(nombreFlog);
			document.add(new Paragraph(" "));
			PdfPCell celda;
			DescargarFoto actual;
			while (!descargados.isEmpty()){
				PdfPTable table = new PdfPTable(1);
				FutureTask<DescargarFoto> aux = descargados.poll();
				if (aux != null){
					actual = aux.get();
					Phrase titulo = new Phrase(new Chunk(actual.getTitulo() + " (" + actual.getNombreImagen() + ")", FUENTE_TITULO));
					Image img;        
					img = Image.getInstance(actual.getPathImagen());
					Phrase descripcion = new Phrase(new Chunk(actual.getDescripcion().toString(), FUENTE_DESCRIPCION));
					celda = new PdfPCell(titulo);
					table.addCell(celda);
					table.addCell(img);
					celda = new PdfPCell(descripcion);
					table.addCell(celda);

					PdfPTable tablaComentarios = new PdfPTable(1);
					for (String comentario: actual.getComentarios()){
						celda = new PdfPCell(new Phrase(new Chunk(comentario, FUENTE_COMENTARIOS)));
						tablaComentarios.addCell(celda);
					}
					table.addCell(tablaComentarios);

					document.add(table);
					document.newPage();
				}

			}

		} catch (DocumentException | IOException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		finally{
			document.close();
		}
	}

	/**
	 * @return the nombreFotolog
	 */
	private String getNombreFotolog() {
		return nombreFotolog;
	}

	private Stack<String> obtenerURLs(String urlFotolog) throws IOException {
		org.jsoup.nodes.Document doc;
		Stack<String> urlFotos = new Stack<String>();
		if (urlFotolog.matches("[0-9a-zA-Z\u005F]+$")) {
			int cant = 0, indice = 0;
			do{	
				String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
				doc = Jsoup.connect("http://ar.fotolog.com/" + urlFotolog + "/mosaic/"  + indice * 30).userAgent(ua).get();
				indice++;
				Elements as = doc.getElementsByTag("a");
				cant = 0;
				for (Element a : as){
					if (a.attr("class").equals("wall_img_container")){
						if(!(a.attr("href").equals("http://ar.fotolog.com/coco2938/72927016/"))){
							if(!(a.attr("href").equals("http://ar.fotolog.com/coco2938/76128840/"))){
								if(!(a.attr("href").equals("http://ar.fotolog.com/coco2938/77161465/"))){
									if(!(a.attr("href").equals("http://ar.fotolog.com/coco2938/77255734/"))){
										if(!(a.attr("href").equals("http://ar.fotolog.com/coco2938/77452049/"))){
											if(!(a.attr("href").equals("http://ar.fotolog.com/coco2938/77733027/"))){
												if(!(a.attr("href").equals("http://ar.fotolog.com/coco2938/78594922/"))){
													if(!(a.attr("href").equals("http://ar.fotolog.com/coco2938/78810993/"))){
														if(!(a.attr("href").equals("http://ar.fotolog.com/coco2938/79225346/"))){
															if(!(a.attr("href").equals("http://ar.fotolog.com/coco2938/153000000000004867/"))){
															urlFotos.add(a.attr("href"));
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
						cant++;
					}
				}
			} while (cant == 30);

		} else {
			JOptionPane.showMessageDialog(null,"La dirección ingresada es inválida.", null, JOptionPane.ERROR_MESSAGE);
			throw new IOException();
		}
		return urlFotos;
	}
}
