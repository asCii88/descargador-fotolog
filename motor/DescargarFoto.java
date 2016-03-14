package motor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;


public class DescargarFoto implements Callable<DescargarFoto>{
	private String titulo = "";
	private StringBuilder descripcion = new StringBuilder();
	private LinkedList<String> comentarios = new LinkedList<String>();
	private String nombreImagen, pathImagen;
	private Date fecha = null;
	private String URL;
	
	public DescargarFoto(String URL){
		this.URL = URL;
		
	}

	@Override
	public DescargarFoto call() throws Exception {
		try {
			String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
			Document doc = Jsoup.connect(URL).userAgent(ua).get();
			Elements divs = doc.getElementsByTag("div");
			String imgUrl = null;
			for (Element div : divs){
				if (div.attr("id").equals("flog_img_holder")){
					Element img = div.select("img").first();
					imgUrl = img.attr("src");
				}
				if (div.attr("id").equals("description_photo")){
					if (div.select("h1").first() != null){
						titulo = div.select("h1").first().text();
					} 
					Queue<Node> nodos = new LinkedBlockingQueue<Node>(div.select("p").first().childNodes());
					Node nodo = nodos.poll();
					while (!(nodo.attr("class").equals("clear"))){
						descripcion.append(nodo);
						nodo = nodos.poll();
					}
					descripcion = new StringBuilder(Jsoup.parse(descripcion.toString()).text());
					while ((nodo.attr("class").equals("clear"))){
						nodo = nodos.poll();
					}
					fecha = new SimpleDateFormat("dd MMMMMMMMMM yyyy").parse(nodo.toString(), new ParsePosition(4));
					if (fecha == null){
						fecha = new SimpleDateFormat("dd MMMMMMMMMM yyyy", Locale.ENGLISH).parse(nodo.toString(), new ParsePosition(4));
					}
				}
				if (div.attr("class").equals("flog_img_comments")){
					if (div.select("p").first() != null)
						comentarios.addFirst(div.select("p").first().text());		
				}
			}
			String añoImagen = new SimpleDateFormat("yyyy").format(fecha);
			String mesImagen = new SimpleDateFormat("MMMMMMMMMM").format(fecha);
			String dirImagen = "imágenes" + File.separator + añoImagen + File.separator + mesImagen + File.separator;
			if (!new File(dirImagen).exists()) {
				new File(dirImagen).mkdirs();  
			}
			nombreImagen = new SimpleDateFormat("dd'-'MM'-'yyyy").format(fecha);
			pathImagen = dirImagen + nombreImagen + ".jpg";
			URL url = new URL(imgUrl);
		    URLConnection hc = url.openConnection();
		    hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30");
			ImageIO.write(ImageIO.read(hc.getInputStream()), "jpg", new FileImageOutputStream(new File(pathImagen)));
		} catch (IOException e) {
			e.printStackTrace();
			if(e.getMessage().equals("Unsupported Image Type")){
				e.printStackTrace();
			} else if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL:")){
				e.printStackTrace();
			
			}else {
				this.call();
			}
			
		}
		return this;
	}

	/**
	 * @return the comentarios
	 */
	public LinkedList<String> getComentarios() {
		return comentarios;
	}

	/**
	 * @return the descripcion
	 */
	public StringBuilder getDescripcion() {
		return descripcion;
	}

	/**
	 * @return the nombreImagen
	 */
	public String getNombreImagen() {
		return nombreImagen;
	}


	/**
	 * @return the nombreImagen
	 */
	public String getPathImagen() {
		return pathImagen;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}
	
}

