package fs.alu.ts.haprts

import java.awt.Font
import javax.swing.JFrame
import javax.swing.JTextField
import javax.swing.JButton
import java.awt.BorderLayout
import java.awt.event.ActionListener
import java.awt.Event
import javax.swing.JFileChooser
import java.awt.event.ActionEvent
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.JLabel
import javax.swing.ImageIcon
import java.awt.Image
import java.net.URL
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.JPanel
import javax.swing.JScrollPane

class DirSetter(m: PreviewCanvas) extends JFrame {
  val t = this
	val pane = getContentPane()
	val txtDirPath = new JTextField(m.path)
	val btnPath = new JButton("Get Directory")
	val btnOK = new JButton("Close")
  	
	val imgIcon = new ImageIcon
	val lblImage = new JLabel("Image Preview")
	val scrollPane = new JScrollPane(lblImage)
	
  	val pnImage = new PreviewCanvas()
  
	
	lblImage.setSize(400, 300)  
	pane.setLayout(new BorderLayout)
	//pane.add(pnImage, BorderLayout.CENTER)
	pane.add(txtDirPath, BorderLayout.CENTER)
	pane.add(btnPath, BorderLayout.EAST)
	pane.add(btnOK, BorderLayout.SOUTH)
	
	btnPath.addActionListener(new ActionListener{
		def actionPerformed(evt: ActionEvent){
		  val dirChooser = new JFileChooser
		  //dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
		  
		  if(txtDirPath.getText() != "")
		    dirChooser.setCurrentDirectory(new File(txtDirPath.getText()))
		  
		  val y = dirChooser.showOpenDialog(t)
		  if(y == JFileChooser.APPROVE_OPTION){
		    val dir = dirChooser.getCurrentDirectory()
		    txtDirPath.setText(dir.getAbsolutePath())
		    val imgFile = dir.listFiles()(0)
		    println(imgFile.getAbsolutePath())
		    //val url = new URL(imgFile)
		    
		    val image = ImageIO.read(imgFile)
		    pnImage.image = image
		    pnImage.repaint
		    
		    //partially working code
		    //imgIcon.setImage(image)
		    //lblImage.setIcon(imgIcon)
		    
		  }
		    
		}
	
	})
	
	btnOK.addActionListener(new ActionListener{
	  def actionPerformed(evt: ActionEvent){
	    t.dispatchEvent(new WindowEvent(t, WindowEvent.WINDOW_CLOSING));
	    m.path = txtDirPath.getText()
	    m.loadFirstImage()
	  }
	})
	setSize(800,80)
	show()
}