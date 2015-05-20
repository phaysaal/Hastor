package fs.alu.ts.haprts
import java.awt.Canvas
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO

trait ImageManager{
	var path = ""
    var image: Image = null
    var imageFiles:Array[File] = new Array[File](0) 
	var currentIndex = 0
	
	def drawImage()
	def openDir()
	def goNext(){ 
	    if(currentIndex + 1 < imageFiles.length)
	    		currentIndex = currentIndex + 1

	    image = ImageIO.read(imageFiles(currentIndex))
	    drawImage()
	}
	def goPrev(){
	  if(currentIndex > 0){
		  currentIndex = currentIndex - 1  
		  image = ImageIO.read(imageFiles(currentIndex))
		  drawImage()
	  }
	}
	
	def loadFirstImage(){
	  imageFiles = (new File(path)).listFiles()
	  image = ImageIO.read(imageFiles(currentIndex))
	  drawImage()
	}
}