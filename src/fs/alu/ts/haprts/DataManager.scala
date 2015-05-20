package fs.alu.ts.haprts

import scala.collection.immutable.Map
import java.io.PrintWriter
import scala.collection.mutable.ArrayBuffer
import java.awt.Point

class DataManager(pr: PreviewCanvas) {
	/*
	 * data is the map from time-stamp to the time unit
	 */
	//var data:Map[Int, TimeUnit] = Map()
	var dataV:ArrayBuffer[(Int, TimeUnit)] = ArrayBuffer()
	/*
	 * vehicles is the map from vehicle id to vehicle type
	 */
	var vehicles:Map[Int, Int] = Map()
	
	val refLeftBehind = new Point(0,0)
	val refRightBehind = new Point(0,0)
	val refLeftFront = new Point(0,0)
	val refRightFront = new Point(0,0)
	var isReferenced = false
	
	def add(time: Int, v: Int, id:Int, x: Int, y: Int){
	  /*
	   * if the vehicle is not already registered, register it
	   */
	  
	  if(!vehicles.contains(id)){
	    vehicles += (id -> v)
	  }
	  
	  /*
	   * if data does not contain this time stamp then add it
	   * otherwise get the time stamp
	   * And
	   * add the time unit to the time stamp
	   */
	  /*
	  if(data.contains(time)){
		val t = data(time)
		t.add(id, x, y)
	  }else{
	    val t = new TimeUnit(time)
	    t.add(id, x, y)
		data += (time -> t)
	  }
	  */
	  
	  val timeStamps = dataV.filter(p => p._1 == time)
	  if(timeStamps.size == 0){
	    val t = new TimeUnit(time)
	    t.add(id, x, y)
		dataV += (time -> t)
	  } else {
	    timeStamps(0)._2.add(id, x, y)
	  }
	}
	
	def get(id: Int) = {
	  val t = dataV.filter(p => p._1 == id)
	  
	  if(t.size == 0)
	    null
	  else
		t(0)._2.data
	    
	  /*
		if(data.contains(id))
		  	data(id).data;
		else
		    null;
		    * 
		    */
	}
	
	def del(time: Int, id: Int){
	  /*
	  val t = data(time)
	  t.del(id)
	  */
	  
	  val t = dataV.filter(p => p._1 == time)
	  if(t.size > 0)
	    t.head._2.del(id)
	}
	
	def save(writer: PrintWriter){
	  /*
	   * Save vehicles with their type
	   */
		vehicles.map(f => writer.println("T"+f))

		/*
		 * Save temporal and spatial data
		 */
		for(x <- dataV){
			writer.print("D"+x._1)
			x._2.save(writer)
			writer.println
		}
	}
	
	def loadVehicle(d: String){
	  
	  val a = d.trim()
	  val b = a.substring(1, a.length()-1).split(',')
	  
	  vehicles += (b(0).toInt -> b(1).toInt)
	  
	}
	
	def loadData(d: String){
	  val timeUnits = d.split('-')
	  
	  val timeStamp = timeUnits.head.toInt
	  
	  val t = new TimeUnit(timeStamp)
	  dataV += (timeStamp -> t)
	  
	  timeUnits.tail.foreach(f => loadTuple(t, f))
	  
	}
	
	def loadTuple(tu: TimeUnit, s: String){
	    val d = s.trim().substring(1, s.trim().length()-1)
	    val e = d.split(",")
	    
	    val id = e(0).toInt
	    tu.add(id, e(1).toInt, e(2).toInt)
	    if(pr.maxVehicle < id + 1)
	      pr.maxVehicle = id + 1
	      
	}
	
}