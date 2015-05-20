package fs.alu.ts.haprts

import java.awt.Toolkit
import java.awt.event.KeyListener
import java.awt.event.KeyEvent
import java.awt.Image
import scala.swing.MainFrame
import javax.swing.JPanel
import scala.swing.BorderPanel
import scala.swing.Component
import scala.swing.Button
import scala.swing.Reactions
import scala.swing.event.ButtonClicked
import scala.swing.Label


//import scala.swing.event.KeyEvent

class MainGUI extends MainFrame{
	
	//var allImages = new Array[Image];
	 
	//val mainPanel = new JPanel
	//mainPanel.setLayout(new BorderLayout)
	
	val mainPanel = new BorderPanel
	
	val pnImage = new PreviewCanvas
	val toolbar = new ToolBar
	
	
	val btnDir = new Button("Open")
	val btnSav = new Button("Save")

	val btnCar = new Button("Car")
	val btnBus = new Button("Bus")
	val btnRik = new Button("Rik")
	val btnTem = new Button("Tmp")
	val btnCNG = new Button("CNG")
	val btnRef = new Button("PersCorr")
	val btnVis = new Button("VisAnal")
	
	toolbar.add(btnDir)
	toolbar.add(btnSav)
	toolbar.add(new Label(" "))
	toolbar.add(btnCar)
	toolbar.add(btnBus)
	toolbar.add(btnRik)
	toolbar.add(btnTem)
	toolbar.add(btnCNG)
	toolbar.add(new Label(" "))
	toolbar.add(btnRef)
	toolbar.add(btnVis)
	
	
	listenTo(btnDir, btnSav, btnRef, btnCar, btnBus, btnRik, btnTem, btnCNG)
	
	reactions += {
	  case ButtonClicked(c) if c == btnDir => pnImage.openDir; rf
	  case ButtonClicked(c) if c == btnSav => pnImage.save; rf		
	  case ButtonClicked(c) if c == btnRef => pnImage.startRef; rf
	  case ButtonClicked(c) if c == btnVis => pnImage.openVis; rf
	  case ButtonClicked(c) if c == btnCar => pnImage.setVehicle(pnImage.Car); rf
	  case ButtonClicked(c) if c == btnBus => pnImage.setVehicle(pnImage.Bus); rf
	  case ButtonClicked(c) if c == btnRik => pnImage.setVehicle(pnImage.Riksaw); rf
	  case ButtonClicked(c) if c == btnTem => pnImage.setVehicle(pnImage.Tempu); rf
	  case ButtonClicked(c) if c == btnCNG => pnImage.setVehicle(pnImage.CNG); rf
	  
	}
	
	def rf(){
	  pnImage.requestFocus
	}
	
	//btn.action_= a
	
	mainPanel.contents.:+(pnImage)
	mainPanel.contents.:+(toolbar)
	
	mainPanel.layout(toolbar) = BorderPanel.Position.North
	mainPanel.layout(pnImage) = BorderPanel.Position.Center
	
	contents = mainPanel
	
	
	title = "Human Assisted Spatial and Temporal Object Recognizer"
	
	val screenSize = Toolkit.getDefaultToolkit.getScreenSize
	size = screenSize
	visible = true
	pnImage.requestFocus
	

}