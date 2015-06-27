import java.awt.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.*;

@SuppressWarnings("serial")
public class TCSS458Paint extends JFrame
{
//	/** File chooser for opening and saving. */
//	private final JFileChooser my_file_chooser = 
//    	new JFileChooser(System.getProperty("user.dir"));
	StringBuffer stringBuffer;

    int width;
    int height;
    int imageSize;    
    int[] pixels;    
    
    void drawPixel(int x, int y, int r, int g, int b) {
        pixels[(height-y-1)*width*3+x*3] = r;
        pixels[(height-y-1)*width*3+x*3+1] = g;
        pixels[(height-y-1)*width*3+x*3+2] = b;                
    }
    
    
    void drawLine(int x1, int y1, int x2, int y2, int r, int g, int b) {
		double dx = (double) x2-x1;
		double dy = (double) y2-y1;
		int xx;
		int yy;
		// If it's just supposed to be a single point.
		if ((x1 == x2) && (y1 == y2)) {
			drawPixel(x1, y1, r, g, b);
		}
		// If the slope is <= 1.
		if (Math.abs(dy) <= Math.abs(dx)) {
			if (x2 < x1) { // Switch to draw line forward.
				int p = x2;
				x2 = x1;
				x1 = p;
				p = y2;
				y2 = y1;
				y1 = p;
			}
			dx = (double) x2-x1;
			dy = (double) y2-y1;
			double m = (double)dy/dx; // Calculate slope after so it is right.
			double yedit = (double)y1;
			
			for (int j = x1; j < x2; j++) {
				yy = (int) Math.round(yedit);
				drawPixel(j, yy, r, g, b);
				yedit += m;
			}
		// If the slope is > 1.
		} else {
			if (y2 < y1) { // Switch to draw line forward.
				int p = x2;
				x2 = x1;
				x1 = p;
				p = y2;
				y2 = y1;
				y1 = p;
			}
			dx = (double) x2-x1;
			dy = (double) y2-y1;
			double m = (double)dx/dy; // Calculate slope after so it is right.
			double xedit = (double)x1;       			
			for (int k = y1; k < y2; k++) {
				xx = (int) Math.round(xedit);
				drawPixel(xx, k, r, g, b);
				xedit += m;
			}
		}	    			
    }
    
    void drawTriangle(int x1,int y1,int x2,int y2,int r,int g,int b, ArrayList<Point> tok) {
		double dx = (double) x2-x1;
		double dy = (double) y2-y1;

		int yy;
		int xx;
		
		// If the slope is <= 1.
		if (Math.abs(dy) <= Math.abs(dx)) {
			if (x2 < x1) { 
				int p = x2;
				x2 = x1;
				x1 = p;
				p = y2;
				y2 = y1;
				y1 = p;
			}
			dx = (double) x2-x1;
			dy = (double) y2-y1;
			double m = (double)dy/dx;
			double yedit = (double)y1;
			for (int j = x1; j <= x2; j++) {
				yy = (int) Math.round(yedit);
				drawPixel(j, yy, r, g, b);
				
				Point p = new Point(j, yy);
				tok.add(p);

				yedit += m;
			}
		// If the slope is > 1.
		} else {
			if (y2 < y1) { 
				int p = x2;
				x2 = x1;
				x1 = p;
				p = y2;
				y2 = y1;
				y1 = p;
			}
			dx = (double) x2-x1;
			dy = (double) y2-y1;
			double m = (double)dx/dy;
			double xedit = (double)x1;   			
			for (int k = y1; k <= y2; k++) {
				xx = (int) Math.round(xedit);
				drawPixel(xx, k, r, g, b);
				   			
				Point p = new Point(xx, k);
				tok.add(p);
				
				xedit += m;
			}
		}
    }
    
    
    void createImage() {
    	// Get the input file.
    	getInput();
    	// Parse the String into individual elements.
    	String in = stringBuffer.toString();
    	String delims = "[ \n]+";
    	String[] tokens = in.split(delims);
    	
    	// Get the values for the dimensions.
    	width = Integer.parseInt(tokens[1]);
    	height = Integer.parseInt(tokens[2]);
        imageSize = width * height;
        pixels = new int[imageSize * 3];
    	// Iterate through the individual tokens.
        int x1 = 0, y1 = 0, r = 0, g = 0, b = 0;
        int x2 = 0, y2 = 0, x3 = 0, y3 = 0;
        
        // Change Background to white.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                drawPixel( x, y, 255, 255, 255);
            }
        }
        
    	for (int i = 3; i <tokens.length; i++) {
    		System.out.println(tokens[i]);
    		if (tokens[i].equals("RGB")) {
    			r = (int) (Double.parseDouble(tokens[i+1])*255);
    			g = (int) (Double.parseDouble(tokens[i+2])*255);
    			b = (int) (Double.parseDouble(tokens[i+3])*255);
//    			System.out.println(r + "\n" + g + "\n" + b);

    			i += 3;
    		} else if (tokens[i].equals("LINE")) {
    			// Point 1.
    			x1 = (int) Math.round(((Float.parseFloat(tokens[i+1])+1)/2)*(width-1));
    			y1 = (int) Math.round(((Float.parseFloat(tokens[i+2])+1)/2)*(height-1));
//    			System.out.println(x1 + "\n" + y1);
    			drawPixel(x1, y1, r, g, b);
    			
    			// Point 2.
    			x2 = (int) Math.round(((Float.parseFloat(tokens[i+3])+1)/2)*(width-1));
    			y2 = (int) Math.round(((Float.parseFloat(tokens[i+4])+1)/2)*(height-1));
    			drawPixel(x2, y2, r, g, b);
//    			System.out.println(x2 + "\n" + y2);
//    			System.out.println(r + "\n" + g + "\n" + b);
    			
    			drawLine(x1, y1, x2, y2, r, g, b);
    			
    			i += 4;
    		} else if (tokens[i].equals("TRI")) {
    			ArrayList<Point> tok = new ArrayList<Point>();
    			// Point 1.
    			x1 = (int) Math.round(((Float.parseFloat(tokens[i+1])+1)/2)*(width-1));
    			y1 = (int) Math.round(((Float.parseFloat(tokens[i+2])+1)/2)*(height-1));
//    			System.out.println(x1 + "\n" + y1);
    			drawPixel(x1, y1, r, g, b);
    			
    			// Point 2.
    			x2 = (int) Math.round(((Float.parseFloat(tokens[i+3])+1)/2)*(width-1));
    			y2 = (int) Math.round(((Float.parseFloat(tokens[i+4])+1)/2)*(height-1));
    			drawPixel(x2, y2, r, g, b);
//    			System.out.println(x2 + "\n" + y2);
    			
    			// Point 3.
    			x3 = (int) Math.round(((Float.parseFloat(tokens[i+5])+1)/2)*(width-1));
    			y3 = (int) Math.round(((Float.parseFloat(tokens[i+6])+1)/2)*(height-1));
    			drawPixel(x3, y3, r, g, b);
//    			System.out.println(x3 + "\n" + y3);
    			
// ******************************	Line 1	*******************************
    			drawTriangle(x1, y1, x2, y2, r, g, b, tok);

// ******************************	Line 2  *******************************
    			drawTriangle(x2, y2, x3, y3, r, g, b, tok);
    				
// ******************************	Line 3  *******************************
    			drawTriangle(x3, y3, x1, y1, r, g, b, tok);

    			
    			// Now fill in the Triangle.
    			// Sort the coordinates:
    			sortByXCoordinates(tok);
    					
//    			System.out.println(tok);
    			int xmin;
    			int xmax;
    			int yfinal;
    			
    	    	for (int i1 = 1; i1 < tok.size(); i1++) {
    	    		xmin = tok.get(i1 -1).x;
    	    		xmax = xmin;
    	    		yfinal = tok.get(i1 -1).y;
    	    		
    				while (i1 < tok.size() && yfinal == tok.get(i1).y) {
    					xmax = tok.get(i1).x;
    					i1 += 1;
    				}
//    	    		System.out.println(xmin + " " + xmax + " " + yfinal);	
    				drawLine(xmin, yfinal, xmax, yfinal, r, g, b);
    	    	}
    			i += 6;
    		}
    	}
    }
    
    /**
     * Compares two points by y first,
     * if they are the same then compare by x
     *
     * @author Casey Morrison
     * @version Oct 21, 2014
     */
    public class PointCompare implements Comparator<Point> {
	    public int compare(final Point a, final Point b) {
	        if (a.y < b.y) {
	            return -1;
	        }
	        else if (a.y > b.y) {
	            return 1;
	        }
	        else {
		        if (a.x < b.x) {
		            return -1;
		        }
		        else if (a.x > b.x) {
		            return 1;
		        } else {
		        	return 0;
		        }	
	        }
	    }
    }
    
    public void sortByXCoordinates(ArrayList<Point> a) {
        Collections.sort(a, new PointCompare());
    }
    
    
    private void getInput() {
    	boolean temp = true;
    	while (temp) {
    	String in = (String)JOptionPane.showInputDialog
    			("Please Indicate the Input File Name");
	    	if (in != null) {
	    		stringBuffer = new StringBuffer();
		    	File file = new File(in);
		    	Charset charset = Charset.forName("US-ASCII");
		    	try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
		    		String line = null;
		    		while ((line = reader.readLine()) != null) {
		    			stringBuffer.append(line);
		    			stringBuffer.append("\n");
		    		}
		    		reader.close();
		    		temp = false;
		    	} catch (IOException e) {
					System.err.format("IOException: %s%n", e);
					temp = true;
				}
	    	} else {
	    		int n = JOptionPane.showConfirmDialog(this, "You pressed 'Cancel' would you like to" +
	    				" retry to input a file name?", "Are You Sure?", 
	    				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	    		if (n == JOptionPane.YES_OPTION) {
	    			temp = true;
	    		} else {
		    		JOptionPane.showMessageDialog(this, "You chose 'No', and must restart\n" +
		    				"This will throw and exception since \nwe can't edit other methods.",
		    				"Major Error", JOptionPane.ERROR_MESSAGE);
		    		temp = false;
	    		}
	    	}
    	}
    	
//    	Boolean temp = true;
//    	while (temp) {
//    	JOptionPane.showMessageDialog(this, "Please Choose the .txt File to Open...");
//    	// now check to see if it is a .txt file
//    	int result = my_file_chooser.showOpenDialog(null);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            try {
//            	File one = my_file_chooser.getSelectedFile();
//            } catch (final IOException e) {
//              JOptionPane.showMessageDialog(null, "File did not contain a valid .txt file: " 
//                  + my_file_chooser.getSelectedFile(), "Invalid", JOptionPane.ERROR_MESSAGE);
//              temp = true;
//              continue;
//            }
//        } else {
//        	temp = true;
//        	continue;
//        }
//        temp = false;
//    	}
    }

    public TCSS458Paint() {
        createImage();
        getContentPane().add( createImageLabel(pixels) );
    }

    private JLabel createImageLabel(int[] pixels) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = image.getRaster();
        raster.setPixels(0, 0, width, height, pixels);
        JLabel label = new JLabel( new ImageIcon(image) );
        return label;
    }

    public static void main(String args[]) {
        JFrame frame = new TCSS458Paint();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }
}