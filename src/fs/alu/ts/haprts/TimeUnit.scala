package fs.alu.ts.haprts

import scala.collection.immutable.Vector
import scala.collection.mutable.ArrayBuffer
import java.io.PrintWriter
import scala.collection.SortedMap
class TimeUnit(id: Int) {
	var data:SortedMap[Int, Tuple] = SortedMap()
	
	def add(i: Int, x: Int, y: Int){
	  if(!data.contains(i)){
		  val t = new Tuple(i, x, y)
		  data += (i -> t)
		  //println(data)
	  }
	}
	
	def del(i: Int){
	  data -= (i)
	}
	
	def save(writer: PrintWriter){
	  data.map(_._2.save(writer))
//	  data.foreach(f => f._2.save(writer))
	}
}