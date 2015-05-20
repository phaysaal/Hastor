package fs.alu.ts.haprts


import java.awt.Canvas
import java.io.File
import java.awt.Color
import java.awt.Image
import javax.imageio.ImageIO
import java.awt.image.ImageObserver
import javax.swing.JPanel
import scala.swing.Component
import scala.swing.Graphics2D
import scala.swing.Reactions
import scala.swing.event.MouseClicked
import scala.swing.event.KeyPressed
import scala.swing.event.KeyTyped
import scala.swing.event.Key
import scala.swing.event.KeyPressed
import java.awt.image.ImageFilter
import scala.swing.event.KeyPressed
import scala.swing.event.MouseWheelMoved
import scala.collection.immutable.Map
import java.io.PrintWriter
import scala.io.Source
import scala.swing.Dialog
import java.awt.Stroke
import java.awt.BasicStroke




class PreviewCanvas extends Component {

	
	
	
	val Car = 1
			val Bus = 2
			val Riksaw = 3
			val Tempu = 4
			val CNG = 5


			val VehicleType: Map[Int, String] = Map(
			    1 -> "Car", 
			    2 -> "Bus", 
			    3 -> "Rik",
			    4 -> "Tem",
			    5 -> "CNG")

			val t = this
			var path = "/home/faisal/Pictures/Test" //"/media/LOGITEC HD/PilotDataforTraffic" //
			var image: Image = null
			var imageFiles:Array[File] = new Array[File](0) 
			var currentIndex = -1

			var currentVehicle = Car
			var currentVehicleId: Int = 0
			var maxVehicle = 0

			val dm = new DataManager(this)

			loadFirstImage

			listenTo(mouse.clicks, keys, mouse.wheel)
	this.reactions += {
	case MouseClicked(_, p, _, _,_) if refMode == false => createVehicle(p.x, p.y)
	case MouseClicked(_, p, _, _,_) if refMode == true => createRefPoint(p.x, p.y)
	case KeyPressed(_, Key.Left, _, _) => goPrev(); repaint
	case KeyPressed(_, Key.Control, _, _) => goPrev(); repaint
	case KeyPressed(_, Key.Right, _, _) => goNext(); repaint
	case KeyPressed(_, Key.Alt, _, _) => goNext(); repaint
	case KeyPressed(_, Key.O, _, _) => openDir()
	case KeyPressed(_, Key.Up, _, _) => {
		if(currentVehicleId < maxVehicle)
			currentVehicleId = currentVehicleId + 1
			repaint
	}
	case KeyPressed(_, Key.Down, _, _) => {
		if(currentVehicleId > 0) currentVehicleId = currentVehicleId - 1
				repaint
	}
	case KeyPressed(_, Key.C, _, _) => currentVehicle = Car; repaint
	case KeyPressed(_, Key.B, _, _) => currentVehicle = Bus; repaint
	case KeyPressed(_, Key.R, _, _) => currentVehicle = Riksaw; repaint
	case KeyPressed(_, Key.T, _, _) => currentVehicle = Tempu; repaint
	case KeyPressed(_, Key.N, _, _) => currentVehicle = CNG; repaint
	
	case KeyPressed(_, Key.BackSpace, _, _) => del(); repaint
	case KeyPressed(_, Key.S, _, _) => save(); repaint
	case KeyPressed(_, Key.V, _, _) => openVis
	case MouseWheelMoved(_, point, _, rotation) => mouseWheeled(point.x, point.y, rotation); repaint
	repaint

	}
	
	def openVis(){
			new Visualizer(dm)
	}

	def mouseWheeled(x: Int, y: Int, d: Int){

		if(currentVehicleId > 0 && d == -1)
			currentVehicleId = currentVehicleId - 1
			else if(currentVehicleId < maxVehicle && d == 1)
				currentVehicleId = currentVehicleId + 1  
	}

	def del(){
		dm.del(currentIndex, currentVehicleId)
	}

	def openDir(){
		new DirSetter(this)
	}

	def save(){
		val writer = new PrintWriter(new File(path+".cvs" ))

		println(path+".cvs", dm.isReferenced)
		if(dm.isReferenced)	
			writer.println("R" + dm.refLeftBehind.x+"-"+dm.refLeftBehind.y+"-"+
					dm.refLeftFront.x+"-"+dm.refLeftFront.y+"-"+
					dm.refRightBehind.x + "-"+dm.refRightBehind.y+"-"+
					dm.refRightFront.x+"-"+dm.refRightFront.y)
		dm.save(writer)
		writer.close()
		Dialog.showMessage(this, "Saved successfully", "Save", Dialog.Message.Info, null)
	}



	def load(){
	  dm.isReferenced = false
	  val f = new File(path+".cvs")
	  if(f.exists()){
		  val d = Source.fromFile(path+".cvs" )
		  val l = d.getLines
		  l.foreach(loadSelector)
	  }
	}
	
	def loadSelector(s: String) = s.head match {
	  case 'T' => dm.loadVehicle(s.tail)
	  case 'D' => dm.loadData(s.tail)
	  case 'R' => loadRef(s.tail)
	}

	val dash1 = Array(10.0f);
	val dashed = new BasicStroke(1.0f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f, dash1, 0.0f);
	override def paintComponent(g: Graphics2D){

		if(image != null){
			var width = size.width.toDouble
			var height = size.height.toDouble
			val screenAspectRatio = size.getWidth()/size.getHeight()
			val imageWidth = image.getWidth(null).toDouble  
			val imageHeight = image.getHeight(null).toDouble
			val aspectRatio = imageWidth / imageHeight
			
			if(screenAspectRatio > aspectRatio){
				width = aspectRatio * height
			}else{
				height = width / aspectRatio 
			}
			val left = (size.width - width)/2.0
			val right = (size.height - height)/2.0

			g.drawImage(image, left.toInt, right.toInt, width.toInt, height.toInt, null)
		}
		g.setColor(Color.WHITE)
		g.fillRect(10, 10, 60, 50)

		g.setColor(Color.RED)
		if(refMode == false){
			g.drawString( VehicleType(currentVehicle) +"-"+currentVehicleId, 10, 25)
			g.drawString( "Frame:"+currentIndex, 10, 45)
		}else{
		  g.drawString( "Ref Mode", 10, 25)
		  if(refState == 0)
		    g.drawString("Left Behind", 10, 45)
		  else if(refState == 1)
		    g.drawString("Left Front", 10, 45)
		  else if(refState == 2)
		    g.drawString("Right Behind", 10, 45)
		  else if(refState == 3)
		    g.drawString("Right Front", 10, 45)
		  
		}
		val  t = dm.get(currentIndex)
		if( t != null ){
			g.setColor(new Color(255,255,255,150))
			t.foreach(f => g.fillRect(f._2.xpos - 5, f._2.ypos-15, 50, 20))

			g.setColor(Color.BLUE)
			t.foreach(f => g.drawString(VehicleType(dm.vehicles(f._2.id))+"-"+f._2.id, f._2.xpos, f._2.ypos))
		}
		
		if(dm.isReferenced == true){
		  g.setStroke(dashed)
		  g.drawLine(dm.refLeftBehind.x, dm.refLeftBehind.y, dm.refLeftFront.x,dm.refLeftFront.y)
		  g.drawLine(dm.refRightBehind.x, dm.refRightBehind.y, dm.refRightFront.x,dm.refRightFront.y)
		}
	}





	def createVehicle(x: Int, y: Int){
		//val metadata = ImageMetadataReader.readMetadata(imageFile);
		if(currentVehicleId == maxVehicle){
			maxVehicle = maxVehicle + 1
		}

		if(currentIndex > -1){
			println(currentIndex, currentVehicle, currentVehicleId, x, y)  
			dm.add(currentIndex, currentVehicle, currentVehicleId, x, y)
			repaint  
		}
		//imageFiles(currentIndex)
	}



	def goNext(){ 
	  if(imageFiles != null){
		if(currentIndex + 1 < imageFiles.length)
			currentIndex = currentIndex + 1

			image = ImageIO.read(imageFiles(currentIndex))
	  }
	}
	def goPrev(){
	  if(imageFiles != null)
		if(currentIndex > 0){
			currentIndex = currentIndex - 1  
					image = ImageIO.read(imageFiles(currentIndex))
		}
	}

	def loadFirstImage(){
		imageFiles = (new File(path)).listFiles()
		currentIndex = 0
		if(imageFiles !=null && !imageFiles.isEmpty)
			image = ImageIO.read(imageFiles(currentIndex))
		
		load
		
		repaint
	}
	
	def setVehicle(x: Int){
	  currentVehicle = x
	  repaint
	}
	
	var refMode = false;
	var refState = 0;
	def startRef(){
		refMode = true;
		refState = 0;
		repaint
	}
	
	def createRefPoint(x: Int, y: Int){
	  if (refState == 0){
	    dm.refLeftBehind.x = x;
	    dm.refLeftBehind.y = y;
	    refState = 1
	  }else if (refState == 1){
	    dm.refLeftFront.x = x;
	    dm.refLeftFront.y = y;
	    refState = 2
	  }else if (refState == 2){
	    dm.refRightBehind.x = x;
	    dm.refRightBehind.y = y;
	    refState = 3
	  }else{
	    dm.refRightFront.x = x;
	    dm.refRightFront.y = y;
	    refState = 0
	    refMode = false
	    dm.isReferenced = true
	  }
	  println(refState)  
	  repaint
	}
	
	def loadRef(s: String){
	  val n = s.split("-");
	  dm.refLeftBehind.x = n(0).toInt
	  dm.refLeftBehind.y = n(1).toInt
	 
	  dm.refLeftFront.x = n(2).toInt
	  dm.refLeftFront.y = n(3).toInt
	  
	  dm.refRightBehind.x = n(4).toInt
	  dm.refRightBehind.y = n(5).toInt
	  
	  dm.refRightFront.x = n(6).toInt
	  dm.refRightFront.y = n(7).toInt
	   
	  dm.isReferenced = true
	}
}