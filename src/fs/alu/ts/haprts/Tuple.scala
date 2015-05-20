package fs.alu.ts.haprts

import java.awt.Point
import java.io.PrintWriter

class Tuple( i: Int, x: Int, y: Int) {
	
	val xpos = x
	val ypos = y
	val id = i
	
	def save(writer: PrintWriter){
	  writer.print("-")
	  writer.print(id, xpos, ypos)
	  
	}
}