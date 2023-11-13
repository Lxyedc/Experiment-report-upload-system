package servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class GeneImg extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GeneImg() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

         
        int width = 60;
        int height = 40;
        String data = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789abcdefghijklmnpqrstuvwxyz";    
        Random random = new Random();
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Graphics g = image.getGraphics();
       
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.WHITE);
        g.fillRect(1, 1, width-2, height-2);
      
       g.setFont(new Font("ËÎÌו", Font.BOLD&Font.ITALIC, 20));      
       
        String builder ="";       
        for(int i = 0 ; i < 4 ;i ++){
            
            g.setColor(new Color(random.nextInt(255),random.nextInt(255), random.nextInt(255)));           
            
            int index = random.nextInt(data.length());
            String str = data.substring(index, index + 1);                  
            builder=builder+str;            
           
            g.drawString(str, (width / 6) * (i + 1) , 20);                     
        }
       
        for(int j=0,n=random.nextInt(100);j<n;j++){
            g.setColor(Color.RED);
            g.fillRect(random.nextInt(width),random.nextInt(height),1,1);
        }     
        
        String tempStr = builder.toString();
        request.getSession().setAttribute("sessioncode",tempStr);
        
        ImageIO.write(image, "jpg", response.getOutputStream());
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
