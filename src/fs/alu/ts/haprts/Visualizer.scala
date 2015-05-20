package fs.alu.ts.haprts

import scala.swing.Component
import scala.swing.Frame
import java.awt.Toolkit
import java.awt.Graphics2D
import java.awt.Color
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.collection.SortedMap
import scala.swing.event.KeyPressed
import scala.swing.event.Key

class Visualizer(dm: DataManager) extends Frame with Runnable {
  val dataManaer = dm
  val xblock = 30
  val yblock = 30
  title = "HASTOR Visual Analyzer"

  val vehicles: Map[Int, TimeUnit] = Map()

  var isLoaded = false
  val screenSize = Toolkit.getDefaultToolkit.getScreenSize
  size = screenSize
  val ssize = size
  val w = size.width / xblock
  val h = size.height / yblock

  var amplitude = 0
  val pnImage = new Component {

    var clear = false

    val field = Array.ofDim[Int](w, h)
    this.size.width = ssize.width
    this.size.height = ssize.height

    def clearWeight() {
      for (i <- 0 to w - 1) {
        for (j <- 0 to h - 1) {
          field(i)(j) = 0
          //println(amplitude, field(i)(j)*(250/amplitude))
        }
      }
    }
    def addWeight(x: Int, y: Int) {
      field(x / xblock)(y / yblock) = field(x / xblock)(y / yblock) + 1

      if (amplitude < field(x / xblock)(y / yblock))
        amplitude = field(x / xblock)(y / yblock)
    }
    
    def ref(x:(Int, Int)):Int={
      if(dm.isReferenced){
	      val x5 = (dm.refLeftFront.x.toDouble + dm.refRightFront.x.toDouble) / 2
	      val d1 = x5 - dm.refLeftFront.x.toDouble
	      
	      val d = ((dm.refLeftBehind.x.toDouble - dm.refLeftFront.x.toDouble)*(dm.refLeftFront.y.toDouble-x._2))/(dm.refLeftFront.y.toDouble-dm.refLeftBehind.y.toDouble)
	      val x6 = dm.refLeftFront.x.toDouble + d
	      val d2 = x5 -  x6
	      val r1 = d1 / d2
	      val d3 = x5 - x._1.toDouble
	      val d3_ = d3 * r1
	      println(x._1.toString + ", " + x._2+ ", "+ (x5 - d3_).toInt)
	      (x5 - d3_).toInt
      }else{
    	  x._1
      }
    }

    def fill() {
      //correct coordinate
      dm.dataV.foreach(f => f._2.data.foreach(g => addWeight(ref(g._2.xpos, g._2.ypos),g._2.ypos)))
      organizeVehicle
      isLoaded = true
    }

    def organizeVehicle() {

      dm.dataV.map(decomposeFrame)
      println("size", vehicles.size)
    }

    def decomposeFrame(frame: (Int, TimeUnit)) {
      // f._1 is the frame id
      // f._2 is the TimeUnit that is an array of vehicles

      val tuples = frame._2.data
      tuples.keys.foreach(f => decomposeVehicle(frame._1, f, tuples(f)))
    }

    def decomposeVehicle(frame: Int, vehicle: Int, tuple: Tuple) {

      //println(vehicle, vehicles.contains(vehicle))

      if (vehicles.contains(vehicle)) {
        val tuples = vehicles(vehicle)
        tuples.add(frame, ref(tuple.xpos, tuple.ypos), tuple.ypos)
      } else {
        val t2 = new Tuple(frame, ref(tuple.xpos, tuple.ypos), tuple.ypos)
        val t3 = new TimeUnit(vehicle)
        t3.data += (frame -> t2)
        vehicles += (vehicle -> new TimeUnit(vehicle))
        //println(vehicle)
      }
    }

    override def paintComponent(g: Graphics2D) {
      if (clear == true) {
        g.clearRect(0, 0, this.size.width, this.size.height)
        clear = false
      } else {
        //println(clear)
        //*
        if (isLoaded) {
          println(w, h)
          println(field.size)
          for (i <- 0 to w - 1) {
            for (j <- 0 to h - 1) {
              //println(amplitude, field(i)(j)*(250/amplitude))
              g.setColor(new Color(255, 255 - field(i)(j) * (250 / amplitude), 255))
              g.fillRect(i * xblock, j * yblock, xblock, yblock)
            }
          }

          vehicles.foreach(t => paintVehicle(g, t._1, t._2))

        }
      }
      //*/
    }

    listenTo(keys)
    this.reactions += {
      case KeyPressed(_, Key.P, _, _) => start
    }
  }

  def paintVehicle(g: Graphics2D, v: Int, t: TimeUnit) {
    //println(v,t.data.isEmpty)
    if (t.data.isEmpty == false) {
      val vehiclePos = t.data.head._2
      g.setColor(new Color(0, 255, 0, 100))
      g.fillOval(vehiclePos.xpos - 5, vehiclePos.ypos - 5, 10, 10)
      g.drawString(v.toString, vehiclePos.xpos, vehiclePos.ypos)
      //println(v, t.data.tail.empty)
      if (t.data.tail.isEmpty == false)
        drawTrail(g, vehiclePos, t.data.tail, v)
    }

  }

  def drawTrail(g: Graphics2D, head: Tuple, tail: SortedMap[Int, Tuple], v: Int) {
    //println("!", head.xpos)
    if (tail.isEmpty == false) {
      val nextVehiclePos = tail.head._2
      g.setColor(new Color(0, 155, 0, 100))
      g.fillOval(nextVehiclePos.xpos - 5, nextVehiclePos.ypos - 5, 10, 10)
      g.drawString(v.toString, nextVehiclePos.xpos, nextVehiclePos.ypos)
      g.drawLine(head.xpos, head.ypos, nextVehiclePos.xpos, nextVehiclePos.ypos)

      //println(head.xpos)

      drawTrail(g, tail.head._2, tail.tail, v)
    }
  }

  contents = pnImage
  size = screenSize

  visible = true
  pnImage.requestFocus

  pnImage.fill

  pnImage.repaint

  def animate(i: Int) {
    var j = 0
    while (j < i) {
      dm.dataV(j)._2.data.foreach(g => pnImage.addWeight(g._2.xpos, g._2.ypos))
      pnImage.decomposeFrame(dm.dataV(j))
      j = j + 1
    }
    isLoaded = true
  }

  def start() {
    val t = new Thread(this)
    t.start()
  }

  override def run() {
    var i = 0

    pnImage.clear = true
    pnImage.repaint
    while (i < dm.dataV.size) {
      vehicles.clear
      pnImage.clearWeight
      animate(i)
      println("i", i, vehicles.size)
      pnImage.repaint
      Thread.sleep(400)
      i = i + 1
    }

  }

  //  def animate(){
  //    
  //  }
  //  
  //  object TimerAnonymous {
  //	  def oncePerSecond(callback: () => Unit) {
  //		  while (true) { callback(); Thread sleep 1000 }
  //	  }
  //	  def main(args: Array[String]) {
  //		  oncePerSecond(() =>
  //		  println("time flies like an arrow..."))
  //	  }
  //  }
}