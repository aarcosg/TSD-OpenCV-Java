package us.idinfor.opencv;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

public class Main
{
   public static void main( String[] args )
   {
	   System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	   try{
		   new DetectSignalDemo().run();
	   }catch(URISyntaxException e){
		   e.printStackTrace();
	   }
   }
}

//
//Detects signals in an image, draws boxes around them, and writes the results
//to "signalDetection.png".
//
class DetectSignalDemo {
	public void run() throws URISyntaxException {
	 System.out.println("\nRunning DetectSignalDemo");
	
	 // Create a signal detector from the cascade file in the resources
	 // directory.
	final URI xmlUri = getClass().getResource("/cascade.xml").toURI();
    final CascadeClassifier signalDetector =new CascadeClassifier(new File(xmlUri).getAbsolutePath());
    final URI imageUri = getClass().getResource("/signals.jpg").toURI();
    final Mat image = Highgui.imread(new File(imageUri).getAbsolutePath(),Highgui.CV_LOAD_IMAGE_COLOR);
	
	 // Detect signals in the image.
	 MatOfRect signalDetections = new MatOfRect();
	 signalDetector.detectMultiScale(image, signalDetections,1.15,3,0,new Size(20,20),new Size());
	
	 System.out.println(String.format("Detected %s faces", signalDetections.toArray().length));
	
	 // Draw a bounding box around each face.
	 for (Rect rect : signalDetections.toArray()) {
	     Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	 }
	
	 // Save the visualized detection.
	 String filename = "output/signalDetection.png";
	 System.out.println(String.format("Writing %s", filename));
	 Highgui.imwrite(filename, image);
	}
}